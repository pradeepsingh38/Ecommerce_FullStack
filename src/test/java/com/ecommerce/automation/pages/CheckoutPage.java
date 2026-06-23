package com.ecommerce.automation.pages;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckoutPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Checkout']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading checkout...']");
	private final By emptyCartMessage = By.xpath("//p[normalize-space()='Your cart is empty']");
	private final By addNewAddressButton = By.xpath("//button[normalize-space()='+ Add a new delivery address']");
	private final By placeOrderButton = By.xpath("//button[normalize-space()='Place Order' or normalize-space()='Placing Order...']");
	private final By checkoutItems = By.cssSelector("div[class*='checkoutItem']");
	private final By totalItems = By.xpath("//aside[contains(@class,'checkoutSummary')]//span[normalize-space()='Items']/following-sibling::strong");
	private final By totalAmount = By.xpath("//aside[contains(@class,'checkoutSummary')]//span[normalize-space()='Total']/following-sibling::strong");
	private final By alertMessage = By.cssSelector("[role='alert']");

	public CheckoutPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(TestConfig.BASE_URL + "/checkout");
		waitForCheckoutRequest();
	}

	public boolean isLoaded() {
		return urlContains("/checkout")
				&& waitForVisible(pageHeading).isDisplayed();
	}

	public boolean isEmptyCartMessageVisible() {
		return driver.findElements(emptyCartMessage).stream().anyMatch(WebElement::isDisplayed);
	}

	public void chooseNewAddress() {
		click(addNewAddressButton);
		waitForVisible(field("houseNo"));
	}

	public void enterAddress(String houseNo, String street, String city, String pincode, String state) {
		type(field("houseNo"), houseNo);
		type(field("street"), street);
		type(field("city"), city);
		type(field("pincode"), pincode);
		type(field("state"), state);
	}

	public void enterContactNumber(String contactNumber) {
		type(field("contactNumber"), contactNumber);
	}

	public void choosePaymentMethod(String paymentMethod) {
		click(By.cssSelector("input[name='paymentMethod'][value='" + paymentMethod + "']"));
	}

	public void placeOrder() {
		click(placeOrderButton);
		urlContains("/my-orders");
	}

	public void submitOrder() {
		click(placeOrderButton);
	}

	public boolean isOnCheckoutPage() {
		return driver.getCurrentUrl().contains("/checkout");
	}

	public boolean isPlaceOrderEnabled() {
		return waitForVisible(placeOrderButton).isEnabled();
	}

	public boolean hasSummaryProduct(String productName) {
		return wait.until(() -> driver.findElements(checkoutItems).stream()
				.anyMatch(item -> item.isDisplayed() && item.getText().contains(productName)));
	}

	public String totalItems() {
		return waitForVisible(totalItems).getText();
	}

	public String totalAmount() {
		return waitForVisible(totalAmount).getText();
	}

	public String fieldValue(String fieldName) {
		return valueOf(field(fieldName));
	}

	public String fieldAttribute(String fieldName, String attributeName) {
		return waitForVisible(field(fieldName)).getAttribute(attributeName);
	}

	public String fieldError(String fieldName) {
		String errorId = fieldName.equals("contactNumber") ? "contact-number-error" : fieldName + "-error";
		return waitForVisible(By.id(errorId)).getText();
	}

	public String alertMessage() {
		return waitForVisible(alertMessage).getText();
	}

	public boolean isFieldValid(String fieldName) {
		return (Boolean) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].checkValidity();",
				waitForVisible(field(fieldName)));
	}

	private By field(String fieldName) {
		return By.cssSelector("[name='" + fieldName + "']");
	}

	private void waitForCheckoutRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}
}
