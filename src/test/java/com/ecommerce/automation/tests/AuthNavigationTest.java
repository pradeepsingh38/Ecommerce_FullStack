package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthNavigationTest extends BaseTest {

	@Test(description = "Verify login page locators and navigation to signup.")
	public void shouldNavigateFromLoginToSignup() {
		LoginPage.open(driver);
		LoginPage loginPage = new LoginPage(driver);

		Assert.assertTrue(loginPage.isLoaded(), "Login page fields should be visible.");
		Assert.assertTrue(driver.findElement(LoginPage.EMAIL_INPUT_ID).isDisplayed(), "Login email should be found by id.");
		Assert.assertTrue(driver.findElement(LoginPage.PASSWORD_INPUT_CSS).isDisplayed(), "Login password should be found by css.");
		Assert.assertTrue(driver.findElement(LoginPage.CREATE_ACCOUNT_LINK_XPATH).isDisplayed(), "Signup link should be found by xpath.");

		SignupPage signupPage = loginPage.goToSignupPage();

		Assert.assertTrue(signupPage.currentUrl().contains(SignupPage.PATH), "Create account link should open signup page.");
		Assert.assertTrue(signupPage.isLoaded(), "Signup page fields should be visible.");
	}

	@Test(description = "Verify signup page locators, valid data entry, and navigation to login.")
	public void shouldNavigateFromSignupToLogin() {
		SignupPage.open(driver);
		SignupPage signupPage = new SignupPage(driver);

		Assert.assertTrue(signupPage.isLoaded(), "Signup page fields should be visible.");
		Assert.assertTrue(driver.findElement(SignupPage.FULL_NAME_INPUT_ID).isDisplayed(), "Signup name should be found by id.");
		Assert.assertTrue(driver.findElement(SignupPage.EMAIL_INPUT_CSS).isDisplayed(), "Signup email should be found by css.");
		Assert.assertTrue(driver.findElement(SignupPage.SIGN_IN_LINK_XPATH).isDisplayed(), "Login link should be found by xpath.");
		signupPage.enterValidRegistrationDetails();
		Assert.assertEquals(signupPage.enteredName(), SignupPage.VALID_NAME, "Signup name should accept valid test data.");
		Assert.assertEquals(signupPage.enteredEmail(), SignupPage.VALID_EMAIL, "Signup email should accept valid test data.");
		Assert.assertEquals(signupPage.enteredPassword(), SignupPage.VALID_PASSWORD, "Signup password should accept 6-character test data.");

		LoginPage loginPage = signupPage.goToLoginPage();

		Assert.assertTrue(loginPage.currentUrl().contains(LoginPage.PATH), "Sign in link should open login page.");
		Assert.assertTrue(loginPage.isLoaded(), "Login page fields should be visible.");
	}
}
