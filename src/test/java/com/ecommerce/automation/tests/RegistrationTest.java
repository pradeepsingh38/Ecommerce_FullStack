package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {

	@Test
	public void shouldCreateAccountWithValidDetails() throws InterruptedException {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		String email = "abhi.kumar" + System.currentTimeMillis() + "@gmail.com";
		signupPage.enterRegistrationDetails("Abhi Kumar", email, "Abhi@123");
		signupPage.clickCreateAccount();
		Thread.sleep(2000);

		Assert.assertTrue(signupPage.isDashboardOpened());
	}

	@Test
	public void shouldShowPopupMessageForInvalidEmailType() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		signupPage.enterRegistrationDetails("Rahul Sharma", "invalid-mail", "Rahul@123");
		signupPage.clickCreateAccount();

		Assert.assertTrue(driver.getCurrentUrl().contains("/register"));
		Assert.assertTrue(signupPage.getEmailValidationMessage().contains("@"));
	}
}
