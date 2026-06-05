package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistrationTest extends BaseTest {

	@Test
	public void shouldValidateRegistrationFields() {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		Assert.assertTrue(signupPage.nameFoundById());
		Assert.assertTrue(signupPage.emailFoundByCss());
		Assert.assertTrue(signupPage.passwordFoundByXpath());

		signupPage.enterRegistrationDetails("Abhi Kumar", "abhi.kumar@gmail.com", "Abhi@123");

		Assert.assertEquals(signupPage.getNameValue(), "Abhi Kumar");
		Assert.assertEquals(signupPage.getEmailValue(), "abhi.kumar@gmail.com");
		Assert.assertEquals(signupPage.getPasswordValue(), "Abhi@123");
	}

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
	public void shouldShowPasswordErrorWhenPasswordIsLessThanSixCharacters() throws InterruptedException {
		SignupPage signupPage = new SignupPage(driver);
		signupPage.open();

		signupPage.enterRegistrationDetails("Rahul Sharma", "rahul.sharma@gmail.com", "Rah");
		signupPage.clickCreateAccount();
		Thread.sleep(2000);

		Assert.assertTrue(driver.getCurrentUrl().contains("/register"));
		Assert.assertEquals(signupPage.getPasswordErrorMessage(), "Password must be at least 6 characters");
	}
}
