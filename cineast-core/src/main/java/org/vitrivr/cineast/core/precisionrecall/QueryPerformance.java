package org.vitrivr.cineast.core.precisionrecall;

public class QueryPerformance {

  String query_id;
  float precision;
  float recall;

  QueryPerformance(String query_id, float precision, float recall) {
    this.query_id = query_id;
    this.precision = precision;
    this.recall = recall;
  }

  public String getQuery_id() {
    return query_id;
  }

  public void setQuery_id(String query_id) {
    this.query_id = query_id;
  }

  public float getPrecision() {
    return precision;
  }

  public void setPrecision(float precision) {
    this.precision = precision;
  }

  public float getRecall() {
    return recall;
  }

  public void setRecall(float recall) {
    this.recall = recall;
  }
}
