package com.ecommerce.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ProductCard {

	private static final Pattern STOCK_QUANTITY = Pattern.compile("(\\d+)\\s+Qty");

	private final WebElement root;

	public ProductCard(WebElement root) {
		this.root = root;
	}

	public String name() {
		return root.findElement(By.tagName("h3")).getText();
	}

	public boolean isImageVisible() {
		return root.findElement(By.cssSelector("img[alt]")).isDisplayed();
	}

	public String category() {
		return root.findElement(By.cssSelector("span[class*='categoryPill']")).getText();
	}

	public String price() {
		return root.findElement(By.xpath(".//div[contains(@class,'cardFooter')]/span[1]")).getText();
	}

	public String stock() {
		return root.findElement(By.xpath(".//div[contains(@class,'cardFooter')]/span[2]")).getText();
	}

	public int stockQuantity() {
		Matcher matcher = STOCK_QUANTITY.matcher(stock());
		return matcher.matches() ? Integer.parseInt(matcher.group(1)) : 0;
	}

	public boolean isInStock() {
		return stockQuantity() > 0;
	}

	public void addToCart() {
		root.findElement(By.xpath(".//button[normalize-space()='Add to Cart']")).click();
	}

	public void open() {
		root.click();
	}
}
