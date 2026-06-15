package com.ecommerce.automation.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class ProductCard {

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

	public void open() {
		root.click();
	}
}
