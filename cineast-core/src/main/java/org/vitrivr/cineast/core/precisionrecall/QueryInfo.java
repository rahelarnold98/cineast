package org.vitrivr.cineast.core.precisionrecall;

import java.util.ArrayList;

public class QueryInfo {

  String queryId;
  String formattedDate;

  ArrayList<Segment> segments;

  QueryInfo() {
  }

  public String getQueryId() {
    return queryId;
  }

  public void setQueryId(String queryId) {
    this.queryId = queryId;
  }

  public String getFormattedDate() {
    return formattedDate;
  }

  public void setFormattedDate(String formattedDate) {
    this.formattedDate = formattedDate;
  }

  public ArrayList<Segment> getSegments() {
    return segments;
  }

  public void setSegments(ArrayList<Segment> segments) {
    this.segments = segments;
  }

}
