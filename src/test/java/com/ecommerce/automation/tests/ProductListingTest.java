package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
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

	@Test
	public void shouldDisplayVisibleProductsWithRequiredDetails() {
		LoginUtils.loginAsDefaultUser(driver);
		ProductsPage productsPage = new ProductsPage(driver);

		productsPage.open();

		Assert.assertTrue(productsPage.visibleProductCount() > 0, "At least one product should be visible");
		ProductSummary product = productsPage.firstVisibleProduct();
		Assert.assertFalse(product.name().isBlank(), "Product name should be visible");
		Assert.assertTrue(product.imageVisible(), "Product image should be visible");
		Assert.assertFalse(product.category().isBlank(), "Product category should be visible");
		Assert.assertTrue(product.price().matches("Rs\\. \\d+(\\.\\d+)?"), "Product price should be visible");
		Assert.assertTrue(product.stock().matches("(\\d+ Qty|Out)"), "Product stock should be visible");
	}
}
