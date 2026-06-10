package com.ecommerce.automation.utils;

import java.time.Duration;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class WaitUtils {

	private final WebDriverWait wait;

	public WaitUtils(WebDriver driver) {
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT_SECONDS));
	}

	public WebElement untilVisible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	public WebElement untilClickable(By locator) {
		return wait.until(ExpectedConditions.elementToBeClickable(locator));
	}

	public boolean untilUrlContains(String text) {
		return wait.until(ExpectedConditions.urlContains(text));
	}
}
