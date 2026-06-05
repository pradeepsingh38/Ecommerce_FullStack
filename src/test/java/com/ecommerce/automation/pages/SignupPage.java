package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class SignupPage extends BasePage {

	private By nameInput = By.id("signup-name");
	private By emailInput = By.cssSelector("#signup-email");
	private By passwordInput = By.xpath("//input[@name='password']");
	private By createAccountButton = By.id("signup-submit");
	private By signInLink = By.xpath("//a[@href='/login']");
	private By passwordError = By.xpath("//input[@id='signup-password']/following-sibling::small");

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

	public LoginPage goToLoginPage() {
		click(signInLink);
		return new LoginPage(driver);
	}

	public String getNameValue() {
		return driver.findElement(nameInput).getAttribute("value");
	}

	public String getEmailValue() {
		return driver.findElement(emailInput).getAttribute("value");
	}

	public String getPasswordValue() {
		return driver.findElement(passwordInput).getAttribute("value");
	}

	public String getPasswordErrorMessage() {
		return waitForVisible(passwordError).getText();
	}

	public boolean isDashboardOpened() {
		return wait.until(ExpectedConditions.urlContains("/dashboard"));
	}
}
