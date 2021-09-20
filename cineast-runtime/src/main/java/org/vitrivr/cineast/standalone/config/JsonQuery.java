package org.vitrivr.cineast.standalone.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonQuery {

  public static boolean createFile;

  @JsonProperty
  public boolean getCreateFile() {
    return createFile;
  }

  public void setCreateFile(boolean createFile) {
    this.createFile = createFile;
  }

  @JsonCreator
  public JsonQuery(){}

}
