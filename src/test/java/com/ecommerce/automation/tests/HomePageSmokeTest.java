package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.config.TestConfig;
import com.ecommerce.automation.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageSmokeTest extends BaseTest {

	@Test(enabled = false, description = "Enable after starting the frontend app on the configured base URL.")
	public void shouldOpenHomePage() {
		HomePage homePage = new HomePage(driver);

		Assert.assertTrue(homePage.currentUrl().startsWith(TestConfig.baseUrl()));
	}
}
