package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(BaseTest.BASE_URL);
	}

	public String pageTitle() {
		return driver.getTitle();
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}
}
