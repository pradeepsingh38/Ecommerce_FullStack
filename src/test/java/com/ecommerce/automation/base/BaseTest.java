package com.ecommerce.automation.base;

import com.ecommerce.automation.config.TestConfig;
import com.ecommerce.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

	protected WebDriver driver;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		driver = DriverFactory.createDriver();
		driver.get(TestConfig.baseUrl());
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
