package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.components.ProductCard;
import com.ecommerce.automation.pages.ProductDetailsPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductListingTest extends BaseTest {

	@Test
	public void shouldDisplayProductListingPage() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);

		productsPage.open();

		Assert.assertTrue(productsPage.isLoaded(), "Product listing page should load");
		Assert.assertEquals(productsPage.heading(), "Explore Products");
		Assert.assertTrue(productsPage.productCountText().matches("\\d+ products showing"));
	}

	@Test(description = "T082: Validate product details (name, price)")
	public void shouldDisplayProductNameAndPrice() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);

		productsPage.open();

		Assert.assertTrue(productsPage.visibleProductCount() > 0, "At least one product should be visible");
		ProductCard product = productsPage.firstVisibleProduct();
		Assert.assertFalse(product.name().isBlank(), "Product name should be visible");
		Assert.assertTrue(product.price().matches("Rs\\. \\d+(\\.\\d+)?"),
				"Product price should be visible and numeric");
	}

	@Test(description = "T083: Verify product navigation")
	public void shouldNavigateToSelectedProductDetails() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);

		productsPage.open();
		ProductCard selectedProduct = productsPage.firstVisibleProduct();
		String expectedName = selectedProduct.name();
		String expectedPrice = selectedProduct.price();
		selectedProduct.open();

		ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
		Assert.assertTrue(detailsPage.isLoaded(), "Product details page should load after selecting a product");
		Assert.assertEquals(detailsPage.productName(), expectedName,
				"Details page should show the selected product name");
		Assert.assertEquals(detailsPage.productPrice(), expectedPrice,
				"Details page should show the selected product price");
	}

	@Test(description = "T084: Test search functionality")
	public void shouldSearchProductsByName() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();

		String productName = productsPage.firstVisibleProduct().name();
		String keyword = productName.split("\\s+")[0];
		productsPage.searchFor(keyword);

		Assert.assertFalse(productsPage.productNames().isEmpty(), "Search should return matching products");
		Assert.assertTrue(productsPage.productNames().stream()
				.allMatch(name -> name.toLowerCase().contains(keyword.toLowerCase())),
				"Every search result should contain the entered keyword");
	}

	@Test(description = "T085: Validate filtering")
	public void shouldFilterProductsByCategory() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();

		String category = productsPage.firstAvailableCategory();
		productsPage.filterByCategory(category);

		Assert.assertFalse(productsPage.visibleProducts().isEmpty(), "Category filter should return products");
		Assert.assertTrue(productsPage.visibleProducts().stream()
				.allMatch(product -> product.category().equals(category)),
				"Every filtered product should belong to the selected category");
	}

	@Test(description = "T086: Handle dynamic elements")
	public void shouldHandleDynamicNoResultsAndResetStates() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		int initialCount = productsPage.visibleProductCount();

		productsPage.searchFor("product-that-does-not-exist-987654");
		Assert.assertTrue(productsPage.isNoProductsMessageVisible(), "No-results state should be displayed");

		productsPage.resetCatalog();
		Assert.assertEquals(productsPage.visibleProductCount(), initialCount,
				"Reset should restore the dynamically loaded catalog");
	}
}
