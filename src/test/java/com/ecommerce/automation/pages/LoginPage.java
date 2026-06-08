package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

	private By emailInput = By.id("login-email");
	private By passwordInput = By.cssSelector("#login-password");
	private By signInButton = By.cssSelector("#login-form button[type='submit']");
	private By createAccountLink = By.xpath("//a[@href='/register']");
	private By forgotPasswordButton = By.xpath("//button[contains(text(),'Forgot password')]");
	private By resetPasswordPopup = By.cssSelector("form[class*='modalCard']");
	private By closePopupButton = By.xpath("//button[@aria-label='Close']");
	private By errorBanner = By.cssSelector("p[class*='errorBanner']");

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

	public void enterLoginDetails(String email, String password) {
		type(emailInput, email);
		type(passwordInput, password);
	}

	public void clickSignIn() {
		click(signInButton);
	}

	public HomePage loginAs(String email, String password) {
		enterLoginDetails(email, password);
		clickSignIn();
		wait.until(ExpectedConditions.urlContains("/dashboard"));
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
