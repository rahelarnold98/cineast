package org.vitrivr.cineast.api.rest.handlers.actions.session;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.api.SessionExtractionContainer;
import org.vitrivr.cineast.api.messages.session.ExtractionContainerMessage;
import org.vitrivr.cineast.api.messages.session.SessionState;
import org.vitrivr.cineast.api.rest.handlers.interfaces.ParsingPostRestHandler;

import java.util.Arrays;

/**
 * @author silvan on 19.01.18.
 */
public class ExtractItemHandler implements ParsingPostRestHandler<ExtractionContainerMessage, SessionState> {
  
  public static final String ROUTE = "session/extract/new";
  private static final Logger LOGGER = LogManager.getLogger();
  
  @OpenApi(
      summary = "Extrat new item",
      path = ROUTE, method = HttpMethod.POST,
      requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = ExtractionContainerMessage.class)),
      tags = {"Session"},
      responses = {
          @OpenApiResponse(status = "200", content = @OpenApiContent(from = SessionState.class))
      }
  )
  @Override
  public SessionState performPost(ExtractionContainerMessage context, Context ctx) {
    SessionState state = ValidateSessionHandler.validateSession(ctx.pathParamMap()); //TODO Use State
    
    LOGGER.debug("Received items {}", Arrays.toString(context.getItemsAsArray()));
    SessionExtractionContainer.addPaths(context.getItemsAsArray());
    return state;
  }
  
  @Override
  public Class<ExtractionContainerMessage> inClass() {
    return ExtractionContainerMessage.class;
  }
  
  @Override
  public Class<SessionState> outClass() {
    return SessionState.class;
  }
  
  @Override
  public String route() {
    return ROUTE;
  }
  
  @OpenApi(
      summary = "Extrat new item",
      path = ROUTE, method = HttpMethod.POST,
      requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = ExtractionContainerMessage.class)),
      tags = {"Session"},
      responses = {
          @OpenApiResponse(status = "200", content = @OpenApiContent(from = SessionState.class))
      }
  )
  @Override
  public OpenApiDocumentation docs() {
    return OpenApiBuilder.document()
        .operation(op -> {
          op.operationId("extractItem");
          op.summary("Extract new item");
          op.description("TODO");
              op.addTagsItem("Session");
        })
        .body(inClass())
        .json("200", outClass());
  }
}
