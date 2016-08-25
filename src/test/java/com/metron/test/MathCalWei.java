package com.metron.test;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.*;
import static org.testng.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

public class MathCalWei {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  @BeforeClass(alwaysRun = true)
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://gymgo.com/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

  @Test
  public void testMathCalWei() throws Exception {
    driver.get("http://www.calculator.net/");
    assertEquals(driver.getTitle(), "Calculator.net: Free Online Calculators - Math, Health, Financial, Science");
    driver.findElement(By.xpath("(//a[contains(text(),'Weight Loss')])[2]")).click();
    assertEquals(driver.getTitle(), "Weight Loss Calculators");
    driver.findElement(By.xpath("//div[@id='content']/table/tbody/tr[7]/td/a/img")).click();
    assertEquals(driver.getTitle(), "Weight Calculator");
    driver.findElement(By.cssSelector("input[type=\"image\"]")).click();
    assertEquals(driver.getTitle(), "Weight Calculator");
    assertEquals(driver.findElement(By.cssSelector("td.bigtext > font > b")).getText(), "0.45359238");
    // ERROR: Caught exception [ERROR: Unsupported command [getTable | css=td > table.0.3 | ]]
  }

  @AfterClass(alwaysRun = true)
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }
  
}  