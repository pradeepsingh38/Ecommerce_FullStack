package com.ecommerce.automation.pages;

import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public String pageTitle() {
		return driver.getTitle();
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}
}
