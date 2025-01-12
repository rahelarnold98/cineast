package org.vitrivr.cineast.api.messages.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.vitrivr.cineast.api.messages.abstracts.AbstractQueryResultMessage;
import org.vitrivr.cineast.api.messages.interfaces.MessageType;
import org.vitrivr.cineast.core.data.TemporalObject;

public class TemporalQueryResult extends AbstractQueryResultMessage<TemporalObject> {

  @JsonCreator
  public TemporalQueryResult(String queryId, List<TemporalObject> content) {
    super(queryId, TemporalObject.class, content);
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.QR_TEMPORAL;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
  }

}
