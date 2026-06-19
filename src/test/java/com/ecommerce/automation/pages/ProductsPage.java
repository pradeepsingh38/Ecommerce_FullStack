package com.ecommerce.automation.pages;

import java.util.List;
import java.util.Locale;

import com.ecommerce.automation.components.ProductCard;
import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ProductsPage extends BasePage {

	private final By pageHeading = By.xpath("//h1[normalize-space()='Explore Products' or normalize-space()='Manage Products']");
	private final By searchInput = By.cssSelector("input[placeholder='Search anything...']");
	private final By categorySelect = By.xpath("//select[option[normalize-space()='All categories']]");
	private final By searchButton = By.xpath("//form//button[normalize-space()='Search']");
	private final By resetButton = By.xpath("//form//button[normalize-space()='Reset']");
	private final By productCards = By.cssSelector("article[role='button']");
	private final By loadingMessage = By.xpath("//p[normalize-space()='Loading...']");
	private final By noProductsMessage = By.xpath("//p[normalize-space()='No products found']");
	private final By productCount = By.xpath("//span[contains(normalize-space(), 'products showing')]");
	private final By successToastMessage = By.xpath("//div[contains(@class,'toastSuccess')]/span");
	private final By cartButton = By.xpath("//button[normalize-space()='Cart']");

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
		return visibleProducts().size();
	}

	public List<ProductCard> visibleProducts() {
		return wait.untilAllVisible(productCards).stream().map(ProductCard::new).toList();
	}

	public ProductCard firstVisibleProduct() {
		return visibleProducts().get(0);
	}

	public ProductCard firstInStockProduct() {
		return visibleProducts().stream()
				.filter(ProductCard::isInStock)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No in-stock products are available"));
	}

	public ProductCard firstProductWithStockAtLeast(int minimumStock) {
		return visibleProducts().stream()
				.filter(product -> product.stockQuantity() >= minimumStock)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No product has stock of at least " + minimumStock));
	}

	public String successToastMessage() {
		return waitForVisible(successToastMessage).getText();
	}

	public void openCart() {
		driver.get(TestConfig.BASE_URL + "/cart");
		urlContains("/cart");
	}

	public void searchFor(String keyword) {
		type(searchInput, keyword);
		click(searchButton);
		String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
		wait.until(() -> productNames().stream()
				.allMatch(name -> name.toLowerCase(Locale.ROOT).contains(normalizedKeyword)));
		waitForCatalogRequest();
	}

	public String firstAvailableCategory() {
		Select select = new Select(waitForVisible(categorySelect));
		return select.getOptions().stream()
				.map(WebElement::getText)
				.filter(option -> !option.equals("All categories"))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No product categories are available"));
	}

	public void filterByCategory(String category) {
		new Select(waitForVisible(categorySelect)).selectByVisibleText(category);
		wait.until(() -> !productNames().isEmpty()
				&& visibleProducts().stream().allMatch(product -> product.category().equals(category)));
	}

	public boolean isNoProductsMessageVisible() {
		return wait.until(() -> !driver.findElements(noProductsMessage).isEmpty());
	}

	public void resetCatalog() {
		click(resetButton);
		wait.until(() -> !productNames().isEmpty());
	}

	public List<String> productNames() {
		return driver.findElements(productCards).stream()
				.filter(WebElement::isDisplayed)
				.map(card -> card.findElement(By.tagName("h3")).getText())
				.toList();
	}

	private void waitForCatalogRequest() {
		waitForVisible(pageHeading);
		wait.untilInvisible(loadingMessage);
	}

}
