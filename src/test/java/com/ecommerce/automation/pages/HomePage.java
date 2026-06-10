package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

	private final By logoutButton = By.xpath("//button[normalize-space()='Logout']");
	private final By dashboardTitle = By.xpath("//h1[normalize-space()='Dashboard' or normalize-space()='Admin Dashboard']");

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(BaseTest.BASE_URL);
	}

	public void openDashboard() {
		driver.get(BaseTest.BASE_URL + "/dashboard");
	}

	public String pageTitle() {
		return driver.getTitle();
	}

	public String currentUrl() {
		return driver.getCurrentUrl();
	}

	public boolean isDashboardLoaded() {
		return waitForVisible(dashboardTitle).isDisplayed();
	}

	public boolean isLogoutButtonVisible() {
		return waitForVisible(logoutButton).isDisplayed();
	}

	public LoginPage logout() {
		click(logoutButton);
		urlContains("/login");
		return new LoginPage(driver);
	}

	public boolean isLoginOpened() {
		return urlContains("/login");
	}
}
