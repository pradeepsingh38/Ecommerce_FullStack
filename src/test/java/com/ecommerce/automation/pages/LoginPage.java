package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

	public static final String PATH = "/login";
	public static final By EMAIL_INPUT_ID = By.id("login-email");
	public static final By PASSWORD_INPUT_CSS = By.cssSelector("#login-password");
	public static final By SIGN_IN_BUTTON_CSS = By.cssSelector("#login-form button[type='submit']");
	public static final By CREATE_ACCOUNT_LINK_XPATH = By.xpath("//a[@href='/register' and normalize-space()='Create account']");

	@FindBy(id = "login-email")
	private WebElement emailInput;

	@FindBy(css = "#login-password")
	private WebElement passwordInput;

	@FindBy(css = "#login-form button[type='submit']")
	private WebElement signInButton;

	@FindBy(xpath = "//a[@href='/register' and normalize-space()='Create account']")
	private WebElement createAccountLink;

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public static void open(WebDriver driver) {
		driver.get(TestConfig.baseUrl() + PATH);
	}

	public boolean isLoaded() {
		return waitForVisible(emailInput).isDisplayed()
				&& waitForVisible(passwordInput).isDisplayed()
				&& waitForVisible(signInButton).isDisplayed();
	}

	public SignupPage goToSignupPage() {
		waitForVisible(createAccountLink).click();
		return new SignupPage(driver);
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}
}
