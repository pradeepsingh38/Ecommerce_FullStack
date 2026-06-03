package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

	protected final WebDriver driver;
	protected final WebDriverWait wait;

	protected BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, TestConfig.explicitWait());
		PageFactory.initElements(driver, this);
	}

	protected WebElement waitForVisible(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected void click(WebElement element) {
		waitForVisible(element).click();
	}

	protected void type(WebElement element, String value) {
		WebElement visibleElement = waitForVisible(element);
		visibleElement.clear();
		visibleElement.sendKeys(value);
	}
}
