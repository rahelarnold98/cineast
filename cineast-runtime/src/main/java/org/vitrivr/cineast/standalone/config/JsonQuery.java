package org.vitrivr.cineast.standalone.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JsonQuery {

  public static boolean createFile;
  public static String path = "json-queries/";


  @JsonCreator
  public JsonQuery() {
  }

  @JsonProperty
  public boolean getCreateFile() {
    return createFile;
  }

  public void setCreateFile(boolean createFile) {
    JsonQuery.createFile = createFile;
  }

  @JsonProperty
  public String getPath() {
    return path;
  }

  public void setPath(String test) {
    JsonQuery.path = test;
  }

}
