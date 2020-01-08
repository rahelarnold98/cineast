package org.vitrivr.cineast.core.db.cottontaildb;

import static org.vitrivr.cineast.core.db.RelationalOperator.NEQ;

import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.AtomicLiteralBooleanPredicate;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.AtomicLiteralBooleanPredicate.Operator;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.BatchedQueryMessage;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.BoolVector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.CompoundBooleanPredicate;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.CompoundBooleanPredicate.Builder;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Data;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.DoubleVector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Entity;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.FloatVector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.From;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.IntVector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Knn;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Knn.Distance;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.LongVector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Projection;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Query;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.QueryMessage;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Schema;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Tuple;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Vector;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Where;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Where.PredicateCase;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.googlecode.javaewah.datastructure.BitSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.providers.primitive.BitSetTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.BooleanTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.DoubleTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.FloatArrayTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.FloatTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.IntArrayTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.IntTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.LongTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.NothingProvider;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.StringTypeProvider;
import org.vitrivr.cineast.core.db.RelationalOperator;

public class CottontailMessageBuilder {

  private static final Logger LOGGER = LogManager.getLogger();

  public static final Schema CINEAST_SCHEMA = schema("cineast");


  public static Schema schema(String schema) {
    return Schema.newBuilder().setName(schema).build();

  }

  public static Entity entity(Schema schema, String name) {
    return Entity.newBuilder().setSchema(schema).setName(name).build();

  }

  public static Entity entity(String name) {
    return entity(CINEAST_SCHEMA, name);
  }

  public static From from(Entity entity) {
    return From.newBuilder().setEntity(entity).build();
  }

  public static Projection projection(Projection.Operation operation, String... attributes) {
    List<String> attrs = attributes == null ? Collections.emptyList() : Arrays.asList(attributes);
    final Projection.Builder projection = Projection.newBuilder().setOp(operation);
    attrs.forEach(e -> projection.putAttributes(e, ""));
    return projection.build();
  }

  public static AtomicLiteralBooleanPredicate.Operator toOperator(RelationalOperator op) {
    switch (op) {
      case EQ:
      case NEQ: //this has to be not-ed!!
        return Operator.EQUAL;
      case GEQ:
        return Operator.GEQUAL;
      case LEQ:
        return Operator.LEQUAL;
      case GREATER:
        return Operator.GREATER;
      case LESS:
        return Operator.LESS;
      case BETWEEN:
        return Operator.BETWEEN;
      case LIKE:
      case ILIKE: //TODO map differently?
      case RLIKE:
      case NLIKE:
        return Operator.LIKE;
      case ISNULL:
        return Operator.ISNULL;
      case ISNOTNULL:
        return Operator.ISNOTNULL;
      case IN:
        return Operator.IN;
    }
    throw new UnsupportedOperationException(op.toString());
  }

  public static AtomicLiteralBooleanPredicate atomicPredicate(String attribute, RelationalOperator operator, Data... data) {
    AtomicLiteralBooleanPredicate.Builder builder = AtomicLiteralBooleanPredicate.newBuilder().setAttribute(attribute);
    if (data != null) {
      for (Data d : data) {
        builder.addData(d);
      }
    }
    builder.setOp(toOperator(operator));
    if (operator == NEQ) {
      builder.setNot(true);
    }
    return builder.build();
  }

  public static Where atomicWhere(String attribute, RelationalOperator operator, Data... data) {
    return Where.newBuilder().setAtomic(atomicPredicate(attribute, operator, data)).build();
  }

  public static ImmutablePair<PredicateCase, Object> recursiveCompoundOrWhere(String fieldname, RelationalOperator operator, Data... data) {
    if (data.length == 1) {
      return ImmutablePair.of(PredicateCase.ATOMIC, atomicPredicate(fieldname, operator, data));
    }

    Builder builder = CompoundBooleanPredicate.newBuilder();
    builder.setAleft(atomicPredicate(fieldname, operator, data[0]));
    builder.setOp(CompoundBooleanPredicate.Operator.OR);
    if (data.length == 2) {
      builder.setAright(atomicPredicate(fieldname, operator, data[1]));
      return ImmutablePair.of(PredicateCase.COMPOUND, builder.build());
    }
    ImmutablePair<PredicateCase, Object> pair = recursiveCompoundOrWhere(fieldname, operator, Arrays.copyOfRange(data, 1, data.length));
    if (pair.left == PredicateCase.ATOMIC) {
      builder.setAright((AtomicLiteralBooleanPredicate) pair.right);
    }
    if (pair.left == PredicateCase.COMPOUND) {
      builder.setCright((CompoundBooleanPredicate) pair.right);
    }
    return ImmutablePair.of(PredicateCase.COMPOUND, builder.build());
  }

  public static Where compoundOrWhere(String fieldname, RelationalOperator operator, Data... data) {
    if (data.length == 1) {
      return atomicWhere(fieldname, operator, data);
    }
    ImmutablePair<PredicateCase, Object> pair = recursiveCompoundOrWhere(fieldname, operator, data);
    if (pair.left == PredicateCase.ATOMIC) {
      return Where.newBuilder().setAtomic((AtomicLiteralBooleanPredicate) pair.right).build();
    }
    if (pair.left == PredicateCase.COMPOUND) {
      return Where.newBuilder().setCompound((CompoundBooleanPredicate) pair.right).build();
    }
    throw new IllegalStateException(Arrays.toString(data));
  }


  public static Query query(Entity entity, Projection projection, Where where, Knn knn, Integer rows) {
    Query.Builder queryBuilder = Query.newBuilder();
    queryBuilder.setFrom(from(entity)).setProjection(projection);
    if (where != null) {
      queryBuilder.setWhere(where);
    }
    if (knn != null) {
      queryBuilder.setKnn(knn);
    }
    if (rows != null) {
      queryBuilder.setLimit(rows);
    }
    return queryBuilder.build();
  }

  public static Data toData(Object o) {

    Data.Builder dataBuilder = Data.newBuilder();
    dataBuilder.clear();

    if (o == null) {
      return dataBuilder.setStringData("null").build();
    }

    if (o instanceof Boolean) {
      return dataBuilder.setBooleanData((boolean) o).build();
    }

    if (o instanceof Integer) {
      return dataBuilder.setIntData((int) o).build();
    }

    if (o instanceof Float) {
      return dataBuilder.setFloatData((float) o).build();
    }

    if (o instanceof Double) {
      return dataBuilder.setDoubleData((double) o).build();
    }

    if (o instanceof Long) {
      return dataBuilder.setLongData((long) o).build();
    }

    if (o instanceof String) {
      return dataBuilder.setStringData((String) o).build();
    }

    if (o instanceof float[]) {
      return dataBuilder.setVectorData(toVector((float[]) o)).build();
    }

    if (o instanceof BitSet) {
      final boolean[] vector = new boolean[((BitSet) o).size()];
      for (int i = 0; i < ((BitSet) o).size(); i++) {
        vector[i] = ((BitSet) o).get(i);
      }
      return dataBuilder.setVectorData(toVector(vector)).build();
    }

    if (o instanceof ReadableFloatVector) {
      return dataBuilder.setVectorData(toVector(((ReadableFloatVector) o))).build();
    }

    Vector.Builder vectorBuilder = Vector.newBuilder();

    if (o instanceof double[]) {
      vectorBuilder.clear();
      return dataBuilder
          .setVectorData(
              vectorBuilder.setDoubleVector(
                  DoubleVector.newBuilder().addAllVector(Doubles.asList((double[]) o))))
          .build();
    }

    if (o instanceof int[]) {

      vectorBuilder.clear();
      return dataBuilder
          .setVectorData(
              vectorBuilder.setIntVector(
                  IntVector.newBuilder().addAllVector(Ints.asList((int[]) o))))
          .build();
    }

    if (o instanceof long[]) {
      vectorBuilder.clear();
      return dataBuilder
          .setVectorData(
              vectorBuilder.setLongVector(
                  LongVector.newBuilder().addAllVector(Longs.asList((long[]) o))))
          .build();
    }

    LOGGER.debug("Unknown type {} in message builder, serializing to string representation {}", o.getClass().getName(), o.toString());

    return dataBuilder.setStringData(o.toString()).build();
  }

  public static Data[] toDatas(Iterable<?> objects) {
    ArrayList<Data> tmp = new ArrayList<>();

    for (Object o : objects) {
      tmp.add(toData(o));
    }

    Data[] _return = new Data[tmp.size()];
    tmp.toArray(_return);

    return _return;
  }

  public static PrimitiveTypeProvider fromData(Data d) {
    switch (d.getDataCase()) {
      case BOOLEANDATA:
        return new BooleanTypeProvider(d.getBooleanData());
      case INTDATA:
        return new IntTypeProvider(d.getIntData());
      case LONGDATA:
        return new LongTypeProvider(d.getLongData());
      case FLOATDATA:
        return new FloatTypeProvider(d.getFloatData());
      case DOUBLEDATA:
        return new DoubleTypeProvider(d.getDoubleData());
      case STRINGDATA:
        return new StringTypeProvider(d.getStringData());
      case VECTORDATA: {
        Vector v = d.getVectorData();
        switch (v.getVectorDataCase()) {
          case FLOATVECTOR:
            return FloatArrayTypeProvider.fromList(v.getFloatVector().getVectorList());
          case DOUBLEVECTOR:
            return FloatArrayTypeProvider.fromDoubleList(v.getDoubleVector().getVectorList());
          case INTVECTOR:
            return IntArrayTypeProvider.fromList(v.getIntVector().getVectorList());
          case LONGVECTOR:
            return IntArrayTypeProvider.fromLongList(v.getLongVector().getVectorList());
          case BOOLVECTOR:
            return BitSetTypeProvider.fromBooleanList(v.getBoolVector().getVectorList());
          case VECTORDATA_NOT_SET:
            return new NothingProvider();
        }
      }

      case DATA_NOT_SET:
        return new NothingProvider();
    }

    return new NothingProvider();
  }

  private static Vector toVector(float[] vector) {
    return Vector.newBuilder()
        .setFloatVector(FloatVector.newBuilder().addAllVector(Floats.asList(vector)))
        .build();
  }

  private static Vector toVector(boolean[] vector) {
    return Vector.newBuilder()
        .setBoolVector(BoolVector.newBuilder().addAllVector(Booleans.asList(vector)))
        .build();
  }

  private static Vector toVector(ReadableFloatVector vector) {
    List<Float> floats = new ArrayList<>();
    for (int i = 0; i < vector.getElementCount(); i++) {
      floats.add(vector.getElement(i));
    }
    return Vector.newBuilder().setFloatVector(FloatVector.newBuilder().addAllVector(floats)).build();
  }

  public static List<Data> toData(Iterable<Object> obs) {

    List<Data> _return = new ArrayList<>();

    for (Object o : obs) {
      _return.add(toData(o));
    }

    return _return;
  }

  public static QueryMessage queryMessage(Query query, String queryId) {
    return QueryMessage.newBuilder().setQuery(query).setQueryId(queryId).build();
  }

  public static BatchedQueryMessage batchedQueryMessage(Iterable<Query> queries) {
    return BatchedQueryMessage.newBuilder().addAllQueries(queries).build();
  }

  public static Map<String, PrimitiveTypeProvider> tupleToMap(Tuple tuple) {

    if (tuple == null) {
      return null;
    }

    Map<String, Data> datamap = tuple.getDataMap();
    Map<String, PrimitiveTypeProvider> map = new HashMap<>(datamap.size());

    for (String k : datamap.keySet()) {
      map.put(k, fromData(datamap.get(k)));
    }

    return map;
  }

  public static Knn knn(
      String attribute,
      float[] vector,
      float[] weights,
      int k,
      ReadableQueryConfig.Distance distance) {
    Knn.Builder knnBuilder = Knn.newBuilder();
    knnBuilder.clear().setK(k).addQuery(toVector(vector)).setAttribute(attribute);

    if (weights != null) {
      knnBuilder.addWeights(toVector(weights));
    }

    switch (distance) {
      case manhattan: {
        knnBuilder.setDistance(Distance.L1);
        break;
      }
      case euclidean: {
        knnBuilder.setDistance(Distance.L2);
        break;
      }
      case squaredeuclidean: {
        knnBuilder.setDistance(Distance.L2SQUARED);
        break;
      }
      case chisquared: {
        knnBuilder.setDistance(Distance.CHISQUARED);
        break;
      }
      default: {
        LOGGER.error("distance '{}' not supported by cottontail", distance);
        break;
      }
    }

    return knnBuilder.build();
  }

  public static Knn batchedKnn(
      String attribute,
      List<float[]> vectors,
      List<float[]> weights,
      int k,
      ReadableQueryConfig.Distance distance
  ) {
    Knn.Builder knnBuilder = Knn.newBuilder();
    knnBuilder.clear().setK(k).setAttribute(attribute);
    vectors.forEach(v -> knnBuilder.addQuery(toVector(v)));

    if (weights != null) {
      weights.forEach(w -> knnBuilder.addWeights(toVector(w)));
    }

    switch (distance) {
      case manhattan: {
        knnBuilder.setDistance(Distance.L1);
        break;
      }
      case euclidean: {
        knnBuilder.setDistance(Distance.L2);
        break;
      }
      case squaredeuclidean: {
        knnBuilder.setDistance(Distance.L2SQUARED);
        break;
      }
      case chisquared: {
        knnBuilder.setDistance(Distance.CHISQUARED);
        break;
      }
      default: {
        LOGGER.error("distance '{}' not supported by cottontail", distance);
        break;
      }
    }

    return knnBuilder.build();
  }

  public static AtomicLiteralBooleanPredicate inList(String attribute, Collection<String> elements) {

    AtomicLiteralBooleanPredicate.Builder builder = AtomicLiteralBooleanPredicate.newBuilder().setAttribute(attribute);
    builder.setOp(Operator.IN);
    builder.addAllData(elements.stream().map(CottontailMessageBuilder::toData).collect(Collectors.toList()));
    return builder.build();

  }

  /**
   * Builds a where clause from a collection of strings. Returns null for an empty collection.
   */
  public static Where whereInList(String attribute, Collection<String> elements) {
    if (elements == null || elements.isEmpty()) {
      return null;
    }

    Where.Builder builder = Where.newBuilder();
    builder.setAtomic(inList(attribute, elements));

    return builder.build();
  }
}
