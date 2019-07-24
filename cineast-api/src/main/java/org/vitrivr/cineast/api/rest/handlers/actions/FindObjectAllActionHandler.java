package org.vitrivr.cineast.api.rest.handlers.actions;

import org.vitrivr.cineast.api.rest.handlers.abstracts.ParsingActionHandler;
import org.vitrivr.cineast.core.data.entities.MediaObjectDescriptor;
import org.vitrivr.cineast.api.messages.general.AnyMessage;
import org.vitrivr.cineast.api.messages.result.MediaObjectQueryResult;
import org.vitrivr.cineast.core.db.dao.reader.MediaObjectReader;

import java.util.List;
import java.util.Map;

/**
 * @author rgasser
 * @version 1.0
 * @created 10.01.17
 */
public class FindObjectAllActionHandler extends ParsingActionHandler<AnyMessage> {

    /**
     * Processes a HTTP GET request.
     *
     * @param parameters Map containing named parameters in the URL.
     * @return {@link MediaObjectQueryResult}
     */
    @Override
    public List<MediaObjectDescriptor> doGet(Map<String, String> parameters) {
        final MediaObjectReader ol = new MediaObjectReader();
        final List<MediaObjectDescriptor> multimediaobjectIds = ol.getAllObjects();
        ol.close();
        return multimediaobjectIds;
    }

    @Override
    public Class<AnyMessage> inClass() {
        return AnyMessage.class;
    }
}
