package com.ecommerce.automation.pages;

import com.ecommerce.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {

	protected final WebDriver driver;
	protected final WaitUtils wait;

	protected BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WaitUtils(driver);
	}

	protected WebElement waitForVisible(By locator) {
		return wait.untilVisible(locator);
	}

	protected void click(By locator) {
		wait.untilClickable(locator).click();
	}

	protected void type(By locator, String value) {
		WebElement element = waitForVisible(locator);
		element.clear();
		element.sendKeys(value);
	}

	protected String valueOf(By locator) {
		return waitForVisible(locator).getAttribute("value");
	}

	protected boolean urlContains(String text) {
		return wait.untilUrlContains(text);
	}
}
