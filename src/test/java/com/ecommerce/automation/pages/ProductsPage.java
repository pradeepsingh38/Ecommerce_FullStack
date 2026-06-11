package com.ecommerce.automation.pages;

import java.util.List;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductsPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Explore Products' or normalize-space()='Manage Products']");
	private final By searchInput = By.cssSelector("input[placeholder='Search anything...']");
	private final By categorySelect = By.xpath("//select[option[normalize-space()='All categories']]");
	private final By productCards = By.cssSelector("article[role='button']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading...']");
	private final By productCount = By.xpath("//span[contains(normalize-space(), 'products showing')]");

	public ProductsPage(WebDriver driver) {
		super(driver);
	}

	public void open() {
		driver.get(TestConfig.BASE_URL + "/products");
		waitForCatalogRequest();
	}

	public boolean isLoaded() {
		return urlContains("/products")
				&& waitForVisible(pageHeading).isDisplayed()
				&& waitForVisible(searchInput).isDisplayed()
				&& waitForVisible(categorySelect).isDisplayed();
	}

	public String heading() {
		return waitForVisible(pageHeading).getText();
	}

	public String productCountText() {
		return waitForVisible(productCount).getText();
	}

	public int visibleProductCount() {
		return wait.untilAllVisible(productCards).size();
	}

	public ProductSummary firstVisibleProduct() {
		List<WebElement> cards = wait.untilAllVisible(productCards);
		WebElement card = cards.get(0);

		return new ProductSummary(
				card.findElement(By.tagName("h3")).getText(),
				card.findElement(By.cssSelector("img[alt]")).isDisplayed(),
				card.findElement(By.cssSelector("span[class*='categoryPill']")).getText(),
				card.findElement(By.xpath(".//div[contains(@class,'cardFooter')]/span[1]")).getText(),
				card.findElement(By.xpath(".//div[contains(@class,'cardFooter')]/span[2]")).getText());
	}

	public void openFirstVisibleProduct() {
		List<WebElement> cards = wait.untilAllVisible(productCards);
		cards.get(0).click();
	}

	private void waitForCatalogRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}

	public record ProductSummary(String name, boolean imageVisible, String category, String price, String stock) {
	}
}
