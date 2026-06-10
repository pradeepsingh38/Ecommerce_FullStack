package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthNavigationTest extends BaseTest {

	@Test
	public void shouldNavigateFromLoginToSignup() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		Assert.assertTrue(loginPage.isLoaded());
		Assert.assertTrue(loginPage.emailFoundById());
		Assert.assertTrue(loginPage.passwordFoundByCss());
		Assert.assertTrue(loginPage.createAccountFoundByXpath());

		SignupPage signupPage = loginPage.goToSignupPage();

		Assert.assertTrue(signupPage.isRegisterOpened());
		Assert.assertTrue(signupPage.isLoaded());
	}

	@Test
	public void shouldNavigateFromSignupToLogin() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		LoginPage loginPage = signupPage.goToLoginPage();

		Assert.assertTrue(loginPage.isLoginOpened());
		Assert.assertTrue(loginPage.isLoaded());
	}

	@Test
	public void shouldOpenAndCloseForgotPasswordPopup() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();

		loginPage.openForgotPasswordPopup();
		Assert.assertTrue(loginPage.isResetPasswordPopupVisible());

		loginPage.closeResetPasswordPopup();
		Assert.assertTrue(loginPage.isLoaded());
	}
}
