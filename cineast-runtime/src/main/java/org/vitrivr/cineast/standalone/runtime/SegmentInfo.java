package org.vitrivr.cineast.standalone.runtime;

import java.util.HashMap;

public class SegmentInfo {

  String segment;
  HashMap<String, Double> features = new HashMap<>();

  // empty constructor used for JSON deserialization
  SegmentInfo() {

  }

  SegmentInfo(String segment, HashMap<String, Double> features) {
    this.segment = segment;
    this.features = features;
  }

  public String getSegment() {
    return segment;
  }

  public void setSegment(String segment) {
    this.segment = segment;
  }

  public HashMap<String, Double> getFeatures() {
    return features;
  }

}
