package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserOrdersPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='My Orders']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading your orders...']");
	private final By orderCards = By.cssSelector("article[class*='buyerOrderCard']");

	public UserOrdersPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(TestConfig.BASE_URL + "/my-orders");
		waitForOrdersRequest();
	}

	public boolean isLoaded() {
		return urlContains("/my-orders")
				&& waitForVisible(pageHeading).isDisplayed();
	}

	public boolean hasOrder(String productName, String shippingAddress, String contactNumber) {
		waitForOrdersRequest();
		return wait.until(() -> driver.findElements(orderCards).stream()
				.anyMatch(card -> containsOrderDetails(card, productName, shippingAddress, contactNumber)));
	}

	private boolean containsOrderDetails(WebElement card, String productName, String shippingAddress, String contactNumber) {
		try {
			String text = card.getText();
			return card.isDisplayed()
					&& text.contains(productName)
					&& text.contains(shippingAddress)
					&& text.contains(contactNumber);
		} catch (StaleElementReferenceException ignored) {
			return false;
		}
	}

	private void waitForOrdersRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}
}
