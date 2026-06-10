package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SignupPage extends BasePage {

	private final By nameInput = By.id("signup-name");
	private final By emailInput = By.cssSelector("#signup-email");
	private final By passwordInput = By.xpath("//input[@name='password']");
	private final By createAccountButton = By.id("signup-submit");
	private final By signInLink = By.xpath("//a[@href='/login']");
	private final By emailError = By.xpath("//input[@id='signup-email']/following-sibling::small");
	private final By passwordError = By.xpath("//input[@id='signup-password']/following-sibling::small");
	private final By errorBanner = By.cssSelector("p[class*='errorBanner']");

	public SignupPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(BaseTest.BASE_URL + "/register");
	}

	public boolean isLoaded() {
		return waitForVisible(nameInput).isDisplayed()
				&& waitForVisible(emailInput).isDisplayed()
				&& waitForVisible(passwordInput).isDisplayed()
				&& waitForVisible(createAccountButton).isDisplayed();
	}

	public boolean isRegisterOpened() {
		return urlContains("/register");
	}

	public boolean nameFoundById() {
		return waitForVisible(nameInput).isDisplayed();
	}

	public boolean emailFoundByCss() {
		return waitForVisible(emailInput).isDisplayed();
	}

	public boolean passwordFoundByXpath() {
		return waitForVisible(passwordInput).isDisplayed();
	}

	public void enterRegistrationDetails(String name, String email, String password) {
		type(nameInput, name);
		type(emailInput, email);
		type(passwordInput, password);
	}

	public void clickCreateAccount() {
		click(createAccountButton);
	}

	public String getEmailValidationMessage() {
		return driver.findElement(emailInput).getAttribute("validationMessage");
	}

	public String getRegistrationErrorMessage() {
		return waitForVisible(errorBanner).getText();
	}

	public LoginPage goToLoginPage() {
		click(signInLink);
		return new LoginPage(driver);
	}

	public String getNameValue() {
		return valueOf(nameInput);
	}

	public String getEmailValue() {
		return valueOf(emailInput);
	}

	public String getPasswordValue() {
		return valueOf(passwordInput);
	}

	public String getEmailErrorMessage() {
		return waitForVisible(emailError).getText();
	}

	public String getPasswordErrorMessage() {
		return waitForVisible(passwordError).getText();
	}

	public boolean isDashboardOpened() {
		return urlContains("/dashboard");
	}
}
