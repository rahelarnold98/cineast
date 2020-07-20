package org.vitrivr.cineast.core.db.cottontaildb;


import static org.vitrivr.cineast.core.db.setup.AttributeDefinition.AttributeType.BITSET;
import static org.vitrivr.cineast.core.db.setup.AttributeDefinition.AttributeType.TEXT;
import static org.vitrivr.cineast.core.db.setup.AttributeDefinition.AttributeType.VECTOR;

import org.vitrivr.cottontail.grpc.CottontailGrpc;
import org.vitrivr.cottontail.grpc.CottontailGrpc.*;
import org.vitrivr.cottontail.grpc.CottontailGrpc.Index.IndexType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.vitrivr.cineast.core.config.DatabaseConfig;
import org.vitrivr.cineast.core.data.entities.MediaObjectDescriptor;
import org.vitrivr.cineast.core.data.entities.MediaObjectMetadataDescriptor;
import org.vitrivr.cineast.core.data.entities.MediaSegmentDescriptor;
import org.vitrivr.cineast.core.data.entities.MediaSegmentMetadataDescriptor;
import org.vitrivr.cineast.core.db.dao.reader.TagReader;
import org.vitrivr.cineast.core.db.setup.AttributeDefinition;
import org.vitrivr.cineast.core.db.setup.EntityCreator;

public class CottontailEntityCreator implements EntityCreator {

  private final CottontailWrapper cottontail;


  public CottontailEntityCreator(DatabaseConfig config) {
    this.cottontail = new CottontailWrapper(config, true);
    init();
  }

  public CottontailEntityCreator(CottontailWrapper cottontailWrapper) {
    this.cottontail = cottontailWrapper;
    init();
  }

  private void init() {
    cottontail.createSchema("cineast");
  }

  @Override
  public boolean createTagEntity() {
    ArrayList<ColumnDefinition> columns = new ArrayList<>(4);
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();

    columns.add(builder.clear().setName(TagReader.TAG_ID_COLUMNNAME).setType(Type.STRING).build());
    columns.add(builder.clear().setName(TagReader.TAG_NAME_COLUMNNAME).setType(Type.STRING).build());
    columns.add(builder.clear().setName(TagReader.TAG_DESCRIPTION_COLUMNNAME).setType(Type.STRING).build());

    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(CottontailMessageBuilder.entity(TagReader.TAG_ENTITY_NAME))
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);

    this.createIndex(TagReader.TAG_ENTITY_NAME, TagReader.TAG_ID_COLUMNNAME, IndexType.HASH_UQ);
    this.createIndex(TagReader.TAG_ENTITY_NAME, TagReader.TAG_NAME_COLUMNNAME, IndexType.HASH);
    this.createIndex(TagReader.TAG_ENTITY_NAME, TagReader.TAG_NAME_COLUMNNAME, IndexType.LUCENE);

    return true;
  }

  @Override
  public boolean createMultiMediaObjectsEntity() {

    ArrayList<ColumnDefinition> columns = new ArrayList<>(4);
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();

    columns.add(builder.clear().setName(MediaObjectDescriptor.FIELDNAMES[0]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaObjectDescriptor.FIELDNAMES[1]).setType(Type.INTEGER).build());
    columns.add(builder.clear().setName(MediaObjectDescriptor.FIELDNAMES[2]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaObjectDescriptor.FIELDNAMES[3]).setType(Type.STRING).build());

    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(CottontailMessageBuilder.entity(MediaObjectDescriptor.ENTITY))
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);

    this.createIndex(MediaObjectDescriptor.ENTITY, MediaObjectDescriptor.FIELDNAMES[0], IndexType.HASH_UQ);

    return true;
  }

  @Override
  public boolean createMetadataEntity() {

    ArrayList<ColumnDefinition> columns = new ArrayList<>(4);
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();

    columns.add(builder.clear().setName(MediaObjectMetadataDescriptor.FIELDNAMES[0]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaObjectMetadataDescriptor.FIELDNAMES[1]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaObjectMetadataDescriptor.FIELDNAMES[2]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaObjectMetadataDescriptor.FIELDNAMES[3]).setType(Type.STRING).build());

    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(CottontailMessageBuilder.entity(MediaObjectMetadataDescriptor.ENTITY))
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);

    this.createIndex(MediaObjectMetadataDescriptor.ENTITY, MediaObjectMetadataDescriptor.FIELDNAMES[0], IndexType.HASH);

    return true;
  }

  @Override
  public boolean createSegmentMetadataEntity() {
    ArrayList<ColumnDefinition> columns = new ArrayList<>(4);
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();

    columns.add(builder.clear().setName(MediaSegmentMetadataDescriptor.FIELDNAMES[0]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaSegmentMetadataDescriptor.FIELDNAMES[1]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaSegmentMetadataDescriptor.FIELDNAMES[2]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaSegmentMetadataDescriptor.FIELDNAMES[3]).setType(Type.STRING).build());

    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(CottontailMessageBuilder.entity(MediaSegmentMetadataDescriptor.ENTITY))
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);
    this.createIndex(MediaSegmentMetadataDescriptor.ENTITY, MediaSegmentMetadataDescriptor.FIELDNAMES[0], IndexType.HASH);
    return true;
  }

  public void createIndex(String entityName, String attribute, IndexType type) {
    Entity entity = CottontailMessageBuilder.entity(entityName);
    Index index = Index.newBuilder().setEntity(entity)
        .setName("index-" + type.name().toLowerCase() + "-" + entity.getSchema().getName() + "_" + entity.getName() + "_" + attribute)
        .setType(type).build();
    /* Cottontail ignores index params as of july 19 */
    CreateIndexMessage idxMessage = CreateIndexMessage.newBuilder().setIndex(index).addColumns(attribute).build();
    cottontail.createIndexBlocking(idxMessage);
  }

  @Override
  public boolean createSegmentEntity() {
    ArrayList<ColumnDefinition> columns = new ArrayList<>(4);
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();

    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[0]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[1]).setType(Type.STRING).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[2]).setType(Type.INTEGER).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[3]).setType(Type.INTEGER).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[4]).setType(Type.INTEGER).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[5]).setType(Type.DOUBLE).build());
    columns.add(builder.clear().setName(MediaSegmentDescriptor.FIELDNAMES[6]).setType(Type.DOUBLE).build());

    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(CottontailMessageBuilder.entity(MediaSegmentDescriptor.ENTITY))
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);

    this.createIndex(MediaSegmentDescriptor.ENTITY, MediaSegmentDescriptor.FIELDNAMES[0], IndexType.HASH_UQ);
    this.createIndex(MediaSegmentDescriptor.ENTITY, MediaSegmentDescriptor.FIELDNAMES[1], IndexType.HASH);

    return true;
  }

  @Override
  public boolean createFeatureEntity(String featurename, boolean unique, int length,
      String... featureNames) {
    final AttributeDefinition[] attributes = Arrays.stream(featureNames)
        .map(s -> new AttributeDefinition(s, VECTOR, length))
        .toArray(AttributeDefinition[]::new);
    return this.createFeatureEntity(featurename, unique, attributes);
  }

  @Override
  public boolean createFeatureEntity(String featurename, boolean unique,
      AttributeDefinition... attributes) {
    final AttributeDefinition[] extended = new AttributeDefinition[attributes.length + 1];
    final HashMap<String, String> hints = new HashMap<>(1);

    extended[0] = new AttributeDefinition("id", AttributeDefinition.AttributeType.STRING, hints);
    System.arraycopy(attributes, 0, extended, 1, attributes.length);
    return this.createEntity(featurename, extended);
  }

  @Override
  public boolean createIdEntity(String entityName, AttributeDefinition... attributes) {
    final AttributeDefinition[] extended = new AttributeDefinition[attributes.length + 1];
    extended[0] = new AttributeDefinition("id", AttributeDefinition.AttributeType.STRING);
    System.arraycopy(attributes, 0, extended, 1, attributes.length);
    return this.createEntity(entityName, extended);
  }

  @Override
  public boolean createEntity(String entityName, AttributeDefinition... attributes) {

    ArrayList<ColumnDefinition> columns = new ArrayList<>();
    ColumnDefinition.Builder builder = ColumnDefinition.newBuilder();
    for (AttributeDefinition attribute : attributes) {
      builder.setName(attribute.getName()).setType(mapAttributeType(attribute.getType()));
      if ((attribute.getType() == VECTOR || attribute.getType() == BITSET) && attribute.getLength() > 0) {
        builder.setLength(attribute.getLength());
      }
      columns.add(builder.build());
      builder.clear();
    }
    Entity entity = CottontailMessageBuilder.entity(CottontailMessageBuilder.CINEAST_SCHEMA, entityName);
    EntityDefinition message = EntityDefinition.newBuilder()
        .setEntity(entity)
        .addAllColumns(columns).build();

    cottontail.createEntityBlocking(message);

    for (AttributeDefinition attribute : attributes) {
      if (attribute.getType() == TEXT) {
        this.createIndex(entityName, attribute.getName(), IndexType.LUCENE);
      }
    }

    return true;
  }

  @Override
  public boolean createHashNonUniqueIndex(String entityName, String column) {
    this.createIndex(entityName, column, IndexType.HASH);
    return true;
  }

  @Override
  public boolean existsEntity(String entityName) {
    return false;
  } // fixme Replace with actual check

  @Override
  public boolean dropEntity(String entityName) {
    cottontail.dropEntityBlocking(CottontailWrapper.entityByName(entityName));
    return true;
  }


  @Override
  public void close() {
    this.cottontail.close();
  }

  public static final Type mapAttributeType(AttributeDefinition.AttributeType type) {
    switch (type) {

      case BOOLEAN:
        return Type.BOOLEAN;
      case DOUBLE:
        return Type.DOUBLE;
      case VECTOR:
        return Type.FLOAT_VEC;
      case BITSET:
        return Type.BOOL_VEC;
      case FLOAT:
        return Type.FLOAT;
      /*case GEOGRAPHY:
        return Type.GEOGRAPHY;
      case GEOMETRY:
        return Type.GEOMETRY;*/
      case INT:
        return Type.INTEGER;
      case LONG:
        return Type.LONG;
      case STRING:
      case TEXT:
        return Type.STRING;
      default:
        throw new RuntimeException("type " + type + " has no matching analogue in CottontailDB");
    }
  }

}
