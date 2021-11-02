package evaluationQueries.sketches;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AutomatedQueriesOfSketches {

  public static void main(String[] args) throws InterruptedException, IOException {

    ColorFeatureEvaluation colorFeatureEvaluation = new ColorFeatureEvaluation();
    ObjectMapper mapper = new ObjectMapper();
    try {
      colorFeatureEvaluation = mapper.readValue(new File("ColorFeatureEvaluation.json"),
          ColorFeatureEvaluation.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    ArrayList<Entry> dictionary = new ArrayList<>();

    // String path = "set path to directory only containing automatedQueries.sketches";
    String path = colorFeatureEvaluation.getPathToSketches();
    //String path = "/Users/rahelarnold/Desktop/Master Project/cineast/bla";
    // set path to chromedriver
    //System.setProperty("webdriver.chrome.driver", "/Users/rahelarnold/Downloads/chromedriver");
    System.setProperty("webdriver.chrome.driver", colorFeatureEvaluation.getWebdriver());
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);

    driver.get("http://" + colorFeatureEvaluation.getIp() + ":" + colorFeatureEvaluation.getPort());
    Thread.sleep(1000);
    WebElement button = driver.findElement(By.cssSelector(
        "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > app-query-container > div > div:nth-child(2) > div > button:nth-child(1) > mat-icon"));
    button.click();

    WebElement imageHolder = driver.findElement(By.cssSelector(
        "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > app-query-container > div > div:nth-child(4) > app-query-stage > app-query-component > div > div:nth-child(1) > app-qt-image > img"));

    File dir = new File(path);
    File[] directoryListing = dir.listFiles();
    int i = 0;
    int queryCounter = 0;
    if (directoryListing != null) {
      for (File file : directoryListing) {
        String img = encodeFileToBase64Binary(file.getAbsolutePath());
        System.out.println(file.getAbsolutePath());
        if (file.getName().startsWith("sketch")) {

          JavascriptExecutor js = (JavascriptExecutor) driver;
          String script =
              "document.getElementsByClassName('mat-tooltip-trigger previewimg')[0].setAttribute('src','"
                  + img + "')";

          js.executeScript
              (script);
          Thread.sleep(1000);
          imageHolder.click();
          Thread.sleep(1000);
          WebElement close = driver.findElement(By.cssSelector(
              "#mat-dialog-" + i
                  + " > app-sketchpad > mat-dialog-content > mat-toolbar > button:nth-child(9) > span.mat-button-wrapper > mat-icon"));
          Thread.sleep(1000);
          close.click();
          i++;

          Thread.sleep(2000);

          WebElement search = driver.findElement(By.cssSelector(
              "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > div:nth-child(1) > button"));
          Thread.sleep(1000);
          search.click();
          Thread.sleep(2000);

          WebElement loadingBar = driver.findElement(By.cssSelector(
              "body > app-vitrivr > div > mat-progress-bar"));
          Thread.sleep(2000);
          WebDriverWait wait = new WebDriverWait(driver, 2000);

          wait.until(ExpectedConditions.invisibilityOf(loadingBar));
          dictionary.add(new Entry(file.getName(), queryCounter));
          queryCounter++;
          Thread.sleep(2000);
        }
      }

      try {
        File dictDir = new File(colorFeatureEvaluation.getPathDictionary());
        if (!dictDir.exists()) {
          dictDir.mkdir();
        }

        // specify name
        mapper.writeValue(new File(
            colorFeatureEvaluation.getPathDictionary() + "/"
                + colorFeatureEvaluation.nameDictionary), dictionary);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  private static String encodeFileToBase64Binary(String filePath) throws IOException {
    byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
    String encodedString =
        "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);
    return encodedString;
  }

  static class Entry {

    String name;
    int number;

    Entry(String name, int number) {
      this.name = name;
      this.number = number;
    }

    public String getName() {
      return name;
    }

    public int getNumber() {
      return number;
    }
  }
}


