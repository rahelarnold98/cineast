package org.vitrivr.cineast.api.messages.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.vitrivr.cineast.api.messages.abstracts.AbstractQueryResultMessage;
import org.vitrivr.cineast.api.messages.interfaces.MessageType;
import org.vitrivr.cineast.core.data.StringDoublePair;

import java.util.List;

/**
 * @author rgasser
 * @created 11.01.17
 */
public class SimilarityQueryResult extends AbstractQueryResultMessage<StringDoublePair> {
  
  private String category;
  private int containerId;

  @JsonCreator
  public SimilarityQueryResult(String queryId, String category, int containerId, List<StringDoublePair> content) {
    super(queryId, StringDoublePair.class, content);
    this.category = category;
    this.containerId = containerId;
  }
  
  public int getContainerId() {
    return this.containerId;
  }
  
  public String getCategory() {
    return category;
  }
  
  @Override
  public MessageType getMessageType() {
    return MessageType.QR_SIMILARITY;
  }
  
  @Override
  public String toString() {
    return "SimilarityQueryResult{" +
        "category='" + getCategory() + '\'' +
        ", containerId='" + containerId + '\'' +
        '}';
  }
}
