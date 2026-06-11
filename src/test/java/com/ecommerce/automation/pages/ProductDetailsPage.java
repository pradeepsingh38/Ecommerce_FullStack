package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailsPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Product Details' or normalize-space()='Admin Product Details']");
	private final By productName = By.cssSelector("section[class*='detailLayout'] h2");
	private final By productPrice = By.xpath("//span[normalize-space()='Price']/following-sibling::strong");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading...']");

	public ProductDetailsPage(WebDriver driver) {
		super(driver);
	}

	public boolean isLoaded() {
		wait.untilInvisible(loadingMessage);
		return urlContains("/products/")
				&& waitForVisible(pageHeading).isDisplayed()
				&& waitForVisible(productName).isDisplayed();
	}

	public String productName() {
		return waitForVisible(productName).getText();
	}

	public String productPrice() {
		return waitForVisible(productPrice).getText();
	}
}
