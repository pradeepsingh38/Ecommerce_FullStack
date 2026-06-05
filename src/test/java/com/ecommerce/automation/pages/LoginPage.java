package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

	private By emailInput = By.id("login-email");
	private By passwordInput = By.cssSelector("#login-password");
	private By signInButton = By.cssSelector("#login-form button[type='submit']");
	private By createAccountLink = By.xpath("//a[@href='/register']");
	private By forgotPasswordButton = By.xpath("//button[contains(text(),'Forgot password')]");
	private By resetPasswordPopup = By.cssSelector("form[class*='modalCard']");
	private By closePopupButton = By.xpath("//button[@aria-label='Close']");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(BaseTest.BASE_URL + "/login");
	}

	public boolean isLoaded() {
		return waitForVisible(emailInput).isDisplayed()
				&& waitForVisible(passwordInput).isDisplayed()
				&& waitForVisible(signInButton).isDisplayed();
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
