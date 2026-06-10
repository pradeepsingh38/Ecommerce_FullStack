package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

	private final By emailInput = By.id("login-email");
	private final By passwordInput = By.cssSelector("#login-password");
	private final By signInButton = By.id("login-submit");
	private final By createAccountLink = By.xpath("//a[@href='/register']");
	private final By forgotPasswordButton = By.xpath("//button[contains(text(),'Forgot password')]");
	private final By resetPasswordPopup = By.cssSelector("form[class*='modalCard']");
	private final By closePopupButton = By.xpath("//button[@aria-label='Close']");
	private final By errorBanner = By.cssSelector("p[class*='errorBanner']");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(TestConfig.BASE_URL + "/login");
	}

	public boolean isLoaded() {
		return waitForVisible(emailInput).isDisplayed()
				&& waitForVisible(passwordInput).isDisplayed()
				&& waitForVisible(signInButton).isDisplayed();
	}

	public boolean isLoginOpened() {
		return urlContains("/login");
	}

	public boolean emailFoundById() {
		return waitForVisible(emailInput).isDisplayed();
	}

	public boolean passwordFoundByCss() {
		return waitForVisible(passwordInput).isDisplayed();
	}

	public boolean createAccountFoundByXpath() {
		return waitForVisible(createAccountLink).isDisplayed();
	}

	public void enterLoginDetails(String email, String password) {
		type(emailInput, email);
		type(passwordInput, password);
	}

	public String getEmailValue() {
		return valueOf(emailInput);
	}

	public String getPasswordValue() {
		return valueOf(passwordInput);
	}

	public void clickSignIn() {
		click(signInButton);
	}

	public HomePage loginAs(String email, String password) {
		enterLoginDetails(email, password);
		clickSignIn();
		urlContains("/dashboard");
		return new HomePage(driver);
	}

	public String getLoginErrorMessage() {
		return waitForVisible(errorBanner).getText();
	}

	public SignupPage goToSignupPage() {
		click(createAccountLink);
		return new SignupPage(driver);
	}

	public void openForgotPasswordPopup() {
		click(forgotPasswordButton);
	}

	public boolean isResetPasswordPopupVisible() {
		return waitForVisible(resetPasswordPopup).isDisplayed();
	}

	public void closeResetPasswordPopup() {
		click(closePopupButton);
	}
}
