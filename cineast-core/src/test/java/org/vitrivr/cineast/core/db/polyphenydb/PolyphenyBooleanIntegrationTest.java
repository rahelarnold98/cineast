package org.vitrivr.cineast.core.db.polyphenydb;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.vitrivr.cineast.core.db.DBBooleanIntegrationTest;
import org.vitrivr.cineast.core.db.IntegrationDBProvider;
import org.vitrivr.cineast.core.db.setup.AttributeDefinition;
import org.vitrivr.cineast.core.db.setup.AttributeDefinition.AttributeType;

import java.sql.PreparedStatement;
import java.util.HashMap;

public class PolyphenyBooleanIntegrationTest extends DBBooleanIntegrationTest<PreparedStatement> {

    private final PolyphenyIntegrationDBProvider _provider;

    public PolyphenyBooleanIntegrationTest() {
        try {
            _provider = new PolyphenyIntegrationDBProvider();
        } catch (Throwable e) {
            LOGGER.error("Error occurred while starting and connecting to Polypheny: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Create table for boolean retrieval
     */
    @Override
    protected void createTables() {
        final HashMap<String, String> hints = new HashMap<>();
        hints.put("pk", "true");
        ec.createEntity(testTableName, new AttributeDefinition(ID_COL_NAME, AttributeType.INT, hints), new AttributeDefinition(DATA_COL_NAME_1, AttributeType.TEXT), new AttributeDefinition(DATA_COL_NAME_2, AttributeType.INT), new AttributeDefinition(DATA_COL_NAME_3, AttributeType.INT));
    }

    @Override
    protected void finishSetup() {
    }

    @Override
    protected IntegrationDBProvider<PreparedStatement> provider() {
        return _provider;
    }

    @Test
    @Override
    @Disabled
    public void testFulltextQuery() {
        /* TODO: Not supported on Polypheny DB yet. */
    }
}
