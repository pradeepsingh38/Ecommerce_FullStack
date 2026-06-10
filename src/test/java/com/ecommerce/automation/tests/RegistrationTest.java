package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {

	@Test
	public void shouldCreateAccountWithValidDetails() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		String email = "abhi.kumar" + System.currentTimeMillis() + "@gmail.com";
		signupPage.enterRegistrationDetails("Abhi Kumar", email, "Abhi@123");
		signupPage.clickCreateAccount();

		Assert.assertTrue(signupPage.isDashboardOpened());
	}

	@Test
	public void shouldValidateRegistrationInputFields() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		signupPage.enterRegistrationDetails("Rahul Sharma", "invalid-mail", "Rahul@123");

		Assert.assertEquals(signupPage.getNameValue(), "Rahul Sharma");
		Assert.assertEquals(signupPage.getEmailValue(), "invalid-mail");
		Assert.assertEquals(signupPage.getPasswordValue(), "Rahul@123");
	}

	@Test
	public void shouldShowPopupMessageForInvalidEmailType() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		signupPage.enterRegistrationDetails("Rahul Sharma", "invalid-mail", "Rahul@123");
		signupPage.clickCreateAccount();

		Assert.assertTrue(signupPage.isRegisterOpened());
		Assert.assertEquals(signupPage.getEmailErrorMessage(), "Enter a valid email address");
		Assert.assertEquals(signupPage.getRegistrationErrorMessage(), "Bad credentials");
	}
}
