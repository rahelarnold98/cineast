package automatedQueries;

public class ColorFeatureEvaluation {

  String pathToThumbnails;
  String pathDictionary;
  String nameDictionary;
  String webdriver;
  String ip;
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

  public String getPathToThumbnails() {
    return pathToThumbnails;
  }

  public void setPathToThumbnails(String pathToThumbnails) {
    this.pathToThumbnails = pathToThumbnails;
  }
}
