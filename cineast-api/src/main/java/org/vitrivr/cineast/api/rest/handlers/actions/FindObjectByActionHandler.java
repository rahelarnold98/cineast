package org.vitrivr.cineast.api.rest.handlers.actions;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.api.rest.handlers.abstracts.ParsingActionHandler;
import org.vitrivr.cineast.core.data.entities.MediaObjectDescriptor;
import org.vitrivr.cineast.api.messages.lookup.IdList;
import org.vitrivr.cineast.api.messages.result.MediaObjectQueryResult;
import org.vitrivr.cineast.core.db.dao.reader.MediaObjectReader;
import org.vitrivr.cineast.standalone.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import spark.route.HttpMethod;

/**
 * @author rgasser
 * @version 1.0
 * @created 10.01.17
 */
public class FindObjectByActionHandler extends ParsingActionHandler<IdList, MediaObjectQueryResult> {

    public final static String ATTRIBUTE_NAME = ":attribute";
    public final static String VALUE_NAME = ":value";

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<HttpMethod> supportedMethods() {
        return Arrays.asList(HttpMethod.get, HttpMethod.post);
    }
    /**
     * Processes a HTTP GET request.
     *
     * @param parameters Map containing named parameters in the URL.
     * @return {@link MediaObjectQueryResult}
     */
    @Override
    public MediaObjectQueryResult doGet(Map<String, String> parameters) {
        String attribute = parameters.get(ATTRIBUTE_NAME);
        String value = parameters.get(VALUE_NAME);

        MediaObjectReader ol = new MediaObjectReader(Config.sharedConfig().getDatabase().getSelectorSupplier().get());
        MediaObjectDescriptor object = null;

        switch (attribute.toLowerCase()) {
            case "id": {
                object = ol.lookUpObjectById(value);
                break;
            }
            case "name": {
                object = ol.lookUpObjectByName(value);
                break;
            }
            case "path": {
                object = ol.lookUpObjectByPath(value);
                break;
            }
            default: {
                LOGGER.error("Unknown attribute '{}' in FindObjectByActionHandler", attribute);
            }
        }

        ol.close();
        return new MediaObjectQueryResult("",Lists.newArrayList(object));
    }

    /**
     * Processes a HTTP GET request.
     *
     * @param parameters Map containing named parameters in the URL.
     * @return {@link MediaObjectQueryResult}
     */
    @Override
    public MediaObjectQueryResult doPost(IdList context, Map<String, String> parameters) {
        if(context == null || context.getIds().length == 0){
            return new MediaObjectQueryResult("",new ArrayList<>(0));
        }
        final MediaObjectReader ol = new MediaObjectReader(Config.sharedConfig().getDatabase().getSelectorSupplier().get());
        final Map<String, MediaObjectDescriptor> objects = ol.lookUpObjects(Arrays.asList(context.getIds()));
        ol.close();
        return new MediaObjectQueryResult("",new ArrayList<>(objects.values()));
    }

    @Override
    public Class<IdList> inClass() {
        return IdList.class;
    }

    @Override
    public String getRoute() {
        return String.format("find/object/by/%s/%s", ATTRIBUTE_NAME, VALUE_NAME);
    }

    @Override
    public String getDescription() {
        return "Find object by attribute and value";
    }

    @Override
    public Class<MediaObjectQueryResult> outClass() {
        return MediaObjectQueryResult.class;
    }
}
