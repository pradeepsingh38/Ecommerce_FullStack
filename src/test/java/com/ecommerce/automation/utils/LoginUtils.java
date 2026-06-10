package com.ecommerce.automation.utils;

import com.ecommerce.automation.pages.HomePage;
import com.ecommerce.automation.pages.LoginPage;
import org.openqa.selenium.WebDriver;

public final class LoginUtils {

	public static final String VALID_EMAIL = "pradeepsingh3802@gmail.com";
	public static final String VALID_PASSWORD = "123456";
	public static final String INVALID_EMAIL = "user1@gmail.com";
	public static final String INVALID_PASSWORD = "wrongpassword";
	public static final String BAD_CREDENTIALS_MESSAGE = "Bad credentials";

	private LoginUtils() {
	}

	public static HomePage loginWithValidCredentials(WebDriver driver) {
		LoginPage loginPage = openLoginPage(driver);
		return loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
	}

	public static LoginPage openLoginPage(WebDriver driver) {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		return loginPage;
	}
}
