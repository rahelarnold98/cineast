package org.vitrivr.cineast.api.messages.result;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.vitrivr.cineast.api.messages.interfaces.Message;
import org.vitrivr.cineast.api.messages.interfaces.MessageType;

public class QueryEnd implements Message {

  private final String queryId;

  public QueryEnd(String queryId) {
    this.queryId = queryId;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.QR_END;
  }

  public String getQueryId() {
    return queryId;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
  }
}
