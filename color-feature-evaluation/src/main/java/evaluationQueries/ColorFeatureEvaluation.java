package evaluationQueries;

public class ColorFeatureEvaluation {

  String pathToSketches;
  String pathDictionary;
  String nameDictionary;
  String webdriver;
  String ip;
  String picName;
  boolean screenshot;
  int port;

  public ColorFeatureEvaluation() {
  }

  public String getWebdriver() {
    return webdriver;
  }

  public void setWebdriver(String webdriver) {
    this.webdriver = webdriver;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getPathDictionary() {
    return pathDictionary;
  }

  public void setPathDictionary(String pathDictionary) {
    this.pathDictionary = pathDictionary;
  }

  public String getNameDictionary() {
    return nameDictionary;
  }

  public void setNameDictionary(String nameDictionary) {
    this.nameDictionary = nameDictionary;
  }

  public String getPathToSketches() {
    return pathToSketches;
  }

  public void setPathToSketches(String pathToSketches) {
    this.pathToSketches = pathToSketches;
  }

  public String getPicName() {
    return picName;
  }

  public void setPicName(String picName) {
    this.picName = picName;
  }

  public boolean isScreenshot() {
    return screenshot;
  }

  public void setScreenshot(boolean screenshot) {
    this.screenshot = screenshot;
  }
}
