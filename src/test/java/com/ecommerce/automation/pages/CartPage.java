package com.ecommerce.automation.pages;

import java.util.List;
import java.util.Optional;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Cart']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading...']");
	private final By emptyCartMessage = By.xpath("//p[normalize-space()='Your cart is empty']");
	private final By errorMessage = By.cssSelector("p[class*='error']");
	private final By cartItems = By.cssSelector("article[class*='cartItem']");
	private final By clearCartButton = By.xpath("//button[normalize-space()='Clear Cart']");
	private final By checkoutButton = By.xpath("//aside[contains(@class,'cartSummary')]//button[normalize-space()='Checkout']");
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

	public void waitUntilEmpty() {
		wait.until(this::isEmpty);
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

	public void waitForProductToBeRemoved(String productName) {
		wait.until(() -> cartItem(productName).isEmpty());
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

	public String productMinimumQuantity(String productName) {
		return cartItem(productName)
				.map(item -> item.findElement(By.cssSelector("input[type='number']")).getAttribute("min"))
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public String productMaximumQuantity(String productName) {
		return cartItem(productName)
				.map(item -> item.findElement(By.cssSelector("input[type='number']")).getAttribute("max"))
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
	}

	public void updateProductQuantity(String productName, int quantity) {
		WebElement quantityInput = cartItem(productName)
				.map(item -> item.findElement(By.cssSelector("input[type='number']")))
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
		quantityInput.clear();
		quantityInput.sendKeys(String.valueOf(quantity));
		waitForProductQuantity(productName, String.valueOf(quantity));
	}

	public void removeProduct(String productName) {
		WebElement removeButton = cartItem(productName)
				.map(item -> item.findElement(By.xpath(".//button[normalize-space()='Remove']")))
				.orElseThrow(() -> new IllegalStateException("Product is not present in cart: " + productName));
		removeButton.click();
	}

	public boolean isErrorVisible() {
		return driver.findElements(errorMessage).stream().anyMatch(WebElement::isDisplayed);
	}

	public void startCheckout() {
		click(checkoutButton);
		urlContains("/checkout");
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
		for (WebElement item : driver.findElements(cartItems)) {
			try {
				if (item.isDisplayed() && item.findElement(By.tagName("h3")).getText().equals(productName)) {
					return Optional.of(item);
				}
			} catch (StaleElementReferenceException ignored) {
				// React may replace cart rows after update/remove actions; retry on the next wait poll.
			}
		}
		return Optional.empty();
	}

	private List<WebElement> visibleCartItems() {
		return driver.findElements(cartItems).stream()
				.filter(this::isDisplayed)
				.toList();
	}

	private boolean isDisplayed(WebElement element) {
		try {
			return element.isDisplayed();
		} catch (StaleElementReferenceException ignored) {
			return false;
		}
	}

	private void waitForCartRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}
}
