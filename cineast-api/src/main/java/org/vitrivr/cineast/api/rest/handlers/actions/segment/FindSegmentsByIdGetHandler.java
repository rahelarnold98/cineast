package org.vitrivr.cineast.api.rest.handlers.actions.segment;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import org.vitrivr.cineast.api.APIEndpoint;
import org.vitrivr.cineast.api.messages.lookup.IdList;
import org.vitrivr.cineast.api.messages.result.MediaObjectMetadataQueryResult;
import org.vitrivr.cineast.api.messages.result.MediaSegmentQueryResult;
import org.vitrivr.cineast.api.rest.handlers.interfaces.GetRestHandler;
import org.vitrivr.cineast.core.data.entities.MediaSegmentDescriptor;
import org.vitrivr.cineast.core.db.dao.reader.MediaSegmentReader;
import org.vitrivr.cineast.standalone.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.vitrivr.cineast.api.rest.handlers.actions.metadata.FindObjectMetadataFullyQualifiedGetHandler.KEY_NAME;

public class FindSegmentsByIdGetHandler implements GetRestHandler<MediaSegmentQueryResult> {
  
  public static final String ID_NAME = "id";
  
  public static final String ROUTE = "find/segments/by/id/:"+ ID_NAME;
  
  @OpenApi(
      summary = "Find segments for specified ids",
      path = ROUTE, method = HttpMethod.GET,
      pathParams = {
          @OpenApiParam(name = ID_NAME, description = "The id of the segments")
      },
      tags = {"Segment"},
      responses = {
          @OpenApiResponse(status = "200", content = @OpenApiContent(from = MediaSegmentQueryResult.class))
      }
  )
  @Override
  public MediaSegmentQueryResult doGet(Context ctx) {
    final Map<String,String> parameters = ctx.pathParamMap();
    final String segmentId = parameters.get(ID_NAME);
    final MediaSegmentReader sl = new MediaSegmentReader(Config.sharedConfig().getDatabase().getSelectorSupplier().get());
    final List<MediaSegmentDescriptor> list = sl.lookUpSegment(segmentId).map(s -> {
      final List<MediaSegmentDescriptor> segments = new ArrayList<>(1);
      segments.add(s);
      return segments;
    }).orElse(new ArrayList<>(0));
    sl.close();
    return new MediaSegmentQueryResult("",list);
  }
  
  @Override
  public Class<MediaSegmentQueryResult> outClass() {
    return MediaSegmentQueryResult.class;
  }
  
  @Override
  public String route() {
    return ROUTE;
  }
}
