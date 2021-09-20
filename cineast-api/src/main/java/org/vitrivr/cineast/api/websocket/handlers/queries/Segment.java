package org.vitrivr.cineast.api.websocket.handlers.queries;

import java.util.HashMap;

public class Segment {

  String segment_id;
  HashMap<String, Double> global = new HashMap<>();
  HashMap<String, Double> local = new HashMap<>();

  Double value;

  public String getSegment_id() {
    return segment_id;
  }

  public void setSegment_id(String segment_id) {
    this.segment_id = segment_id;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public HashMap<String, Double> getLocal() {
    return local;
  }

  public void setLocal(HashMap<String, Double> local) {
    this.local = local;
  }

  public HashMap<String, Double> getGlobal() {
    return global;
  }

  public void setGlobal(HashMap<String, Double> global) {
    this.global = global;
  }


}
