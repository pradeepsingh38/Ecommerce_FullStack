package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageSmokeTest extends BaseTest {

	@Test
	public void shouldOpenHomePage() {
		HomePage homePage = new HomePage(driver);
		homePage.open();

		Assert.assertTrue(homePage.currentUrl().contains(BASE_URL));
	}
}
