package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SignupPage extends BasePage {

	public static final String PATH = "/register";
	public static final By FULL_NAME_INPUT_ID = By.id("signup-name");
	public static final By EMAIL_INPUT_CSS = By.cssSelector("#signup-email");
	public static final By PASSWORD_INPUT_CSS = By.cssSelector("input[name='password']");
	public static final By CREATE_ACCOUNT_BUTTON_ID = By.id("signup-submit");
	public static final By SIGN_IN_LINK_XPATH = By.xpath("//a[@href='/login' and normalize-space()='Sign in']");
	public static final String VALID_NAME = "Automation User";
	public static final String VALID_EMAIL = "automation.user@example.com";
	public static final String VALID_PASSWORD = "123456";

	@FindBy(id = "signup-name")
	private WebElement fullNameInput;

	@FindBy(css = "#signup-email")
	private WebElement emailInput;

	@FindBy(css = "input[name='password']")
	private WebElement passwordInput;

	@FindBy(id = "signup-submit")
	private WebElement createAccountButton;

	@FindBy(xpath = "//a[@href='/login' and normalize-space()='Sign in']")
	private WebElement signInLink;

	public SignupPage(WebDriver driver) {
		super(driver);
	}

	public static void open(WebDriver driver) {
		driver.get(TestConfig.baseUrl() + PATH);
	}

	public boolean isLoaded() {
		return waitForVisible(fullNameInput).isDisplayed()
				&& waitForVisible(emailInput).isDisplayed()
				&& waitForVisible(passwordInput).isDisplayed()
				&& waitForVisible(createAccountButton).isDisplayed();
	}

	public SignupPage enterRegistrationDetails(String name, String email, String password) {
		waitForVisible(fullNameInput).clear();
		fullNameInput.sendKeys(name);
		waitForVisible(emailInput).clear();
		emailInput.sendKeys(email);
		waitForVisible(passwordInput).clear();
		passwordInput.sendKeys(password);
		return this;
	}

	public SignupPage enterValidRegistrationDetails() {
		return enterRegistrationDetails(VALID_NAME, VALID_EMAIL, VALID_PASSWORD);
	}

	public String enteredName() {
		return fullNameInput.getAttribute("value");
	}

	public String enteredEmail() {
		return emailInput.getAttribute("value");
	}

	public String enteredPassword() {
		return passwordInput.getAttribute("value");
	}

	public LoginPage goToLoginPage() {
		waitForVisible(signInLink).click();
		return new LoginPage(driver);
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}
}
