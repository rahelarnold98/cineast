package evaluationQueries.sketches;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class WithRobot {

  public static void main(String[] args) throws InterruptedException, AWTException, IOException {

    // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
    System.setProperty("webdriver.chrome.driver", "/Users/rahelarnold/Downloads/chromedriver");
    WebDriver driver = new ChromeDriver();

    driver.get("http:127.0.0.1:4567");
    Thread.sleep(1000);
    WebElement button = driver.findElement(By.cssSelector(
        "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > app-query-container > div > div:nth-child(2) > div > button:nth-child(1) > mat-icon"));
    button.click();

    WebElement imageHolder = driver.findElement(By.cssSelector(
        "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > app-query-container > div > div:nth-child(4) > app-query-stage > app-query-component > div > div:nth-child(1) > app-qt-image > img"));

    imageHolder.click();

    WebElement upload = driver.findElement(By.cssSelector(
        "#mat-dialog-0 > app-sketchpad > mat-dialog-content > mat-toolbar > button:nth-child(8) > span.mat-button-wrapper > mat-icon"));

    upload = driver.findElement(By.cssSelector(
        "#mat-dialog-" + 0
            + " > app-sketchpad > mat-dialog-content > mat-toolbar > button:nth-child(8) > span.mat-button-wrapper > mat-icon"));
    upload.click();

    // set path to image retrieval object
    File file = new File("path to sketch");

    Thread.sleep(2000);// Image name can be of your choice
    StringSelection stringSelection = new StringSelection(file.getAbsolutePath());

    //Copy to clipboard
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

    // source of robot instructions:
    // https://learn-automation.com/upload-file-in-selenium-in-mac/
    Robot robot = new Robot();

    // Cmd + Tab is needed since it launches a Java app and the browser looses focus
    robot.keyPress(KeyEvent.VK_META);
    robot.keyPress(KeyEvent.VK_TAB);
    robot.keyRelease(KeyEvent.VK_META);
    robot.delay(1000);
    robot.keyRelease(KeyEvent.VK_TAB);
    robot.delay(1000);

    //Open Goto window
    robot.keyPress(KeyEvent.VK_META);
    robot.keyPress(KeyEvent.VK_SHIFT);
    robot.keyPress(KeyEvent.VK_G);
    robot.keyRelease(KeyEvent.VK_META);
    robot.delay(1000);
    robot.keyRelease(KeyEvent.VK_SHIFT);
    robot.delay(1000);

    robot.keyRelease(KeyEvent.VK_G);
    robot.delay(1000);

    //Paste the clipboard value
    robot.keyPress(KeyEvent.VK_META);
    robot.keyPress(KeyEvent.VK_V);
    robot.keyRelease(KeyEvent.VK_META);
    robot.delay(1000);
    robot.keyRelease(KeyEvent.VK_V);
    robot.delay(1000);

    //Press Enter key to close the Goto window and Upload window
    robot.keyPress(KeyEvent.VK_ENTER);
    robot.keyRelease(KeyEvent.VK_ENTER);
    robot.delay(1000);
    robot.keyPress(KeyEvent.VK_ENTER);
    robot.keyRelease(KeyEvent.VK_ENTER);

    Thread.sleep(2000);// Image name can be of your choice

    WebElement close = driver.findElement(By.cssSelector(
        "#mat-dialog-0 > app-sketchpad > mat-dialog-content > mat-toolbar > button:nth-child(9) > span.mat-button-wrapper > mat-icon"));
    close.click();

    Thread.sleep(2000);// Image name can be of your choice

    WebElement search = driver.findElement(By.cssSelector(
        "body > app-vitrivr > mat-sidenav-container > mat-sidenav.mat-drawer.mat-sidenav.left.ng-tns-c168-0.ng-trigger.ng-trigger-transform.mat-drawer-side.mat-drawer-opened.ng-star-inserted > div > app-query-sidebar > div > div:nth-child(1) > button"));
    search.click();
    Thread.sleep(2000);// Image name can be of your choice
  }


}

