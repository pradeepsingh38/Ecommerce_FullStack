package com.ecommerce.automation.pages;

import java.util.List;
import java.util.Optional;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Cart']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading...']");
	private final By emptyCartMessage = By.xpath("//p[normalize-space()='Your cart is empty']");
	private final By cartItems = By.cssSelector("article[class*='cartItem']");
	private final By clearCartButton = By.xpath("//button[normalize-space()='Clear Cart']");
	private final By totalItems = By.xpath("//aside[contains(@class,'cartSummary')]//span[normalize-space()='Items']/following-sibling::strong");
	private final By totalAmount = By.xpath("//aside[contains(@class,'cartSummary')]//span[normalize-space()='Total']/following-sibling::strong");

	public CartPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(TestConfig.BASE_URL + "/cart");
		waitForCartRequest();
	}

	public boolean isLoaded() {
		return urlContains("/cart")
				&& waitForVisible(pageHeading).isDisplayed();
	}

	public void clearIfNotEmpty() {
		waitForCartRequest();
		if (isEmpty()) {
			return;
		}

		click(clearCartButton);
		wait.until(this::isEmpty);
	}

	public boolean isEmpty() {
		return driver.findElements(emptyCartMessage).stream().anyMatch(WebElement::isDisplayed);
	}

	public void waitForProduct(String productName) {
		wait.until(() -> cartItem(productName).isPresent());
	}

	public void waitForProductQuantity(String productName, String quantity) {
		wait.until(() -> cartItem(productName)
				.map(item -> item.findElement(By.cssSelector("input[type='number']")).getAttribute("value"))
				.filter(quantity::equals)
				.isPresent());
	}

	public boolean hasProduct(String productName) {
		return cartItem(productName).isPresent();
	}

	public String productPrice(String productName) {
		return cartItem(productName)
				.map(item -> item.findElement(By.xpath(".//strong[starts-with(normalize-space(), 'Rs.')]")).getText())
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public String productCategory(String productName) {
		return cartItem(productName)
				.map(item -> item.findElement(By.xpath(".//h3/following-sibling::p")).getText())
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public String productSubtotal(String productName) {
		return cartItem(productName)
				.map(item -> item.findElements(By.xpath(".//strong[starts-with(normalize-space(), 'Rs.')]")).get(1).getText())
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public String productQuantity(String productName) {
		return cartItem(productName)
				.map(item -> item.findElement(By.cssSelector("input[type='number']")).getAttribute("value"))
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public String totalItems() {
		return waitForVisible(totalItems).getText();
	}

	public String totalAmount() {
		return waitForVisible(totalAmount).getText();
	}

	public int visibleItemCount() {
		return visibleCartItems().size();
	}

	private Optional<WebElement> cartItem(String productName) {
		return visibleCartItems().stream()
				.filter(item -> item.findElement(By.tagName("h3")).getText().equals(productName))
				.findFirst();
	}

	private List<WebElement> visibleCartItems() {
		return driver.findElements(cartItems).stream()
				.filter(WebElement::isDisplayed)
				.toList();
	}

	private void waitForCartRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}
}
