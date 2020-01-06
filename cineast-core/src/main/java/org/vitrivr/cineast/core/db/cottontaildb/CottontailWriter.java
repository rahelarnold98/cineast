package org.vitrivr.cineast.core.db.cottontaildb;

import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Data;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Entity;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.InsertMessage;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Projection;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Projection.Operation;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.QueryResponseMessage;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Tuple;
import ch.unibas.dmi.dbis.cottontail.grpc.CottontailGrpc.Where;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.db.AbstractPersistencyWriter;
import org.vitrivr.cineast.core.db.PersistentTuple;
import org.vitrivr.cineast.core.db.RelationalOperator;

public class CottontailWriter extends AbstractPersistencyWriter<Tuple> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final CottontailWrapper cottontail;

    public CottontailWriter(CottontailWrapper wrapper) {
        this.cottontail = wrapper;
    }

    private Entity entity;


    @Override
    public boolean open(String name) {
        this.entity = CottontailMessageBuilder.entity(name);
        return true;
    }

    @Override
    public boolean close() {
        this.cottontail.close();
        return true;
    }


    @Override
    public boolean exists(String key, String value) {

        Projection projection = CottontailMessageBuilder.projection(Operation.SELECT, key); //TODO replace with exists projection
        Where where = CottontailMessageBuilder.atomicWhere(key, RelationalOperator.EQ, CottontailMessageBuilder.toData(value));

        List<QueryResponseMessage> result = cottontail.query(CottontailMessageBuilder.queryMessage(CottontailMessageBuilder.query(entity, projection, where, null, 1), ""));

        if (result.isEmpty()) {
            return false;
        }

        return result.get(0).getResultsCount() > 0;

    }


    @Override
    public boolean persist(List<PersistentTuple> tuples) {
        InsertMessage im = InsertMessage.newBuilder().setEntity(this.entity).addAllTuple(tuples.stream().map(this::getPersistentRepresentation).collect(Collectors.toList())).build();
        if (im.getSerializedSize() > 3_000_000) {
            LOGGER.debug("inserting im with size {}, which is above 3MB. Although max message size is configurable and set to 4MB, anything above 10MB is pushing the limits of proto", im.getSerializedSize());
        }
        return this.cottontail.insertBlocking(im).getSuccess();
    }

    @Override
    public Tuple getPersistentRepresentation(PersistentTuple tuple) {

        Tuple.Builder tupleBuilder = Tuple.newBuilder();

        HashMap<String, Data> tmpMap = new HashMap<>();
        int nameIndex = 0;

        for (Object o : tuple.getElements()) {
            tmpMap.put(names[nameIndex++], CottontailMessageBuilder.toData(o));
        }

        return tupleBuilder.putAllData(tmpMap).build();

    }
}
