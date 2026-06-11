package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.HomePage;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

	@Test
	public void shouldLoginWithValidCredentials() {
		LoginPage loginPage = LoginUtils.openLoginPage(driver);

		loginPage.enterLoginDetails(LoginUtils.VALID_EMAIL, LoginUtils.VALID_PASSWORD);

		Assert.assertEquals(loginPage.getEmailValue(), LoginUtils.VALID_EMAIL);
		Assert.assertEquals(loginPage.getPasswordValue(), LoginUtils.VALID_PASSWORD);

		loginPage.clickSignIn();
		HomePage dashboardPage = new HomePage(driver);

		Assert.assertTrue(dashboardPage.isDashboardLoaded());
	}

	@Test
	public void shouldShowErrorMessageForInvalidCredentials() {
		LoginPage loginPage = LoginUtils.openLoginPage(driver);

		loginPage.enterLoginDetails(LoginUtils.INVALID_EMAIL, LoginUtils.INVALID_PASSWORD);
		loginPage.clickSignIn();

		Assert.assertTrue(loginPage.isLoginOpened());
		Assert.assertEquals(loginPage.getLoginErrorMessage(), LoginUtils.BAD_CREDENTIALS_MESSAGE);
	}

	@Test
	public void shouldLogoutAfterLogin() {
		HomePage dashboardPage = LoginUtils.loginWithValidCredentials(driver);

		Assert.assertTrue(dashboardPage.isLogoutButtonVisible());

		LoginPage loginPage = dashboardPage.logout();

		Assert.assertTrue(loginPage.isLoginOpened());
		Assert.assertTrue(loginPage.isLoaded());
		Assert.assertEquals(loginPage.getSuccessMessage(), "Logged out successfully");
	}

	@Test
	public void shouldRedirectToLoginAfterLogoutWhenOpeningDashboard() {
		HomePage dashboardPage = LoginUtils.loginWithValidCredentials(driver);
		LoginPage loginPage = dashboardPage.logout();

		Assert.assertTrue(loginPage.isLoginOpened());

		dashboardPage.openDashboard();

		Assert.assertTrue(loginPage.isLoginOpened());
	}

	@Test
	public void shouldRedirectToLoginAfterSessionIsCleared() {
		LoginUtils.loginAsDefaultUser(driver);

		LoginUtils.clearSessionAndGoTo(driver, BASE_URL + "/dashboard");
		LoginPage loginPage = new LoginPage(driver);

		Assert.assertTrue(loginPage.isLoginOpened());
		Assert.assertTrue(loginPage.isLoaded());
	}
}
