package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.ProductDetailsPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.pages.ProductsPage.ProductSummary;
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
		ProductSummary product = productsPage.firstVisibleProduct();
		Assert.assertFalse(product.name().isBlank(), "Product name should be visible");
		Assert.assertTrue(product.price().matches("Rs\\. \\d+(\\.\\d+)?"),
				"Product price should be visible and numeric");
	}

	@Test(description = "T083: Verify product navigation")
	public void shouldNavigateToSelectedProductDetails() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);

		productsPage.open();
		ProductSummary selectedProduct = productsPage.firstVisibleProduct();
		productsPage.openFirstVisibleProduct();

		ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
		Assert.assertTrue(detailsPage.isLoaded(), "Product details page should load after selecting a product");
		Assert.assertEquals(detailsPage.productName(), selectedProduct.name(),
				"Details page should show the selected product name");
		Assert.assertEquals(detailsPage.productPrice(), selectedProduct.price(),
				"Details page should show the selected product price");
	}
}
