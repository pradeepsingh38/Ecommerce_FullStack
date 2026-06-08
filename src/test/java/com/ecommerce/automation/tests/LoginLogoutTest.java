package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.HomePage;
import com.ecommerce.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginLogoutTest extends BaseTest {

	@Test
	public void shouldLoginWithValidCredentialsAndLogout() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		HomePage dashboardPage = loginPage.loginAs("admin@gmail.com", "admin123");

		Assert.assertTrue(dashboardPage.isDashboardLoaded());

		dashboardPage.clickLogout();

		Assert.assertTrue(dashboardPage.isLoginOpened());
		Assert.assertTrue(new LoginPage(driver).isLoaded());
	}

	@Test
	public void shouldShowErrorMessageForInvalidCredentials() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.enterLoginDetails("admin@gmail.com", "wrongpassword");
		loginPage.clickSignIn();

		Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
		Assert.assertEquals(loginPage.getLoginErrorMessage(), "Bad credentials");
	}
}
