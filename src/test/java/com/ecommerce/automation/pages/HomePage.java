package com.ecommerce.automation.pages;

import com.ecommerce.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

	private By logoutButton = By.xpath("//button[normalize-space()='Logout']");
	private By dashboardTitle = By.xpath("//h1[normalize-space()='Dashboard' or normalize-space()='Admin Dashboard']");

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(BaseTest.BASE_URL);
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

	public void clickLogout() {
		click(logoutButton);
	}

	public boolean isLoginOpened() {
		return wait.until(ExpectedConditions.urlContains("/login"));
	}
}
