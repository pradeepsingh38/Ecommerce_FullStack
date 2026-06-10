package com.ecommerce.automation.utils;

import com.ecommerce.automation.config.TestConfig;
import com.ecommerce.automation.pages.HomePage;
import com.ecommerce.automation.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public final class LoginUtils {

	public static final String VALID_EMAIL = TestConfig.VALID_EMAIL;
	public static final String VALID_PASSWORD = TestConfig.VALID_PASSWORD;
	public static final String INVALID_EMAIL = TestConfig.INVALID_EMAIL;
	public static final String INVALID_PASSWORD = TestConfig.INVALID_PASSWORD;
	public static final String BAD_CREDENTIALS_MESSAGE = TestConfig.BAD_CREDENTIALS_MESSAGE;

	private LoginUtils() {
	}

	public static HomePage loginWithValidCredentials(WebDriver driver) {
		return loginAsDefaultUser(driver);
	}

	public static HomePage loginAsDefaultUser(WebDriver driver) {
		return loginAs(driver, VALID_EMAIL, VALID_PASSWORD);
	}

	public static HomePage loginAs(WebDriver driver, String email, String password) {
		return openLoginPage(driver).loginAs(email, password);
	}

	public static LoginPage openLoginPage(WebDriver driver) {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open();
		return loginPage;
	}

	public static void logoutIfLoggedIn(WebDriver driver) {
		HomePage homePage = new HomePage(driver);
		if (homePage.isLogoutButtonPresent()) {
			homePage.logout();
		}
	}

	public static void clearSession(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript(
				"window.localStorage.clear(); window.sessionStorage.clear();");
	}

	public static void clearSessionAndGoTo(WebDriver driver, String url) {
		clearSession(driver);
		driver.get(url);
	}
}
