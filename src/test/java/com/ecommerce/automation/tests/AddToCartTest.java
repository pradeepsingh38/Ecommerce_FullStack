package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.components.ProductCard;
import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class AddToCartTest extends BaseTest {

	@Test(description = "T088: Automate add-to-cart functionality")
	public void shouldAddProductToCartFromCatalog() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstInStockProduct();
		String productName = product.name();

		product.addToCart();

		Assert.assertTrue(productsPage.successToastMessage().contains(productName),
				"Successful add-to-cart toast should mention the selected product");
	}

	@Test(description = "T089: Validate product addition")
	public void shouldShowAddedProductInCart() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstInStockProduct();
		String expectedName = product.name();
		String expectedPrice = product.price();

		product.addToCart();
		productsPage.openCart();

		CartPage cartPage = new CartPage(driver);
		Assert.assertTrue(cartPage.isLoaded(), "Cart page should load after navigating from catalog");
		cartPage.waitForProduct(expectedName);
		Assert.assertTrue(cartPage.hasProduct(expectedName), "Added product should be present in the cart");
		Assert.assertEquals(cartPage.productPrice(expectedName), expectedPrice,
				"Cart should show the selected product price");
		Assert.assertEquals(cartPage.productQuantity(expectedName), "1",
				"Cart should show the default added quantity");
		Assert.assertEquals(cartPage.totalItems(), "1", "Cart summary should count the added product");
	}

	@Test(description = "T090: Validate cart items")
	public void shouldValidateCartItemDetails() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstInStockProduct();
		String expectedName = product.name();
		String expectedCategory = product.category();
		String expectedPrice = product.price();

		product.addToCart();
		productsPage.openCart();

		CartPage cartPage = new CartPage(driver);
		cartPage.waitForProduct(expectedName);
		Assert.assertEquals(cartPage.visibleItemCount(), 1, "Cart should show one cart item row");
		Assert.assertTrue(cartPage.hasProduct(expectedName), "Cart item name should match the added product");
		Assert.assertEquals(cartPage.productCategory(expectedName), expectedCategory,
				"Cart item category should match the added product");
		Assert.assertEquals(cartPage.productPrice(expectedName), expectedPrice,
				"Cart item unit price should match the added product");
		Assert.assertEquals(cartPage.productQuantity(expectedName), "1",
				"Cart item should default to quantity one");
		Assert.assertEquals(cartPage.totalItems(), "1", "Cart summary should reflect the cart item quantity");
	}

	@Test(description = "T091: Verify quantity & price")
	public void shouldVerifyCartQuantityAndPriceTotals() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstProductWithStockAtLeast(2);
		String expectedName = product.name();
		BigDecimal unitPrice = money(product.price());
		BigDecimal expectedSubtotal = unitPrice.multiply(BigDecimal.valueOf(2));

		product.addToCart();
		Assert.assertTrue(productsPage.successToastMessage().contains(expectedName),
				"First add-to-cart toast should mention the selected product");
		product.addToCart();
		productsPage.openCart();

		CartPage cartPage = new CartPage(driver);
		cartPage.waitForProductQuantity(expectedName, "2");
		Assert.assertEquals(cartPage.productQuantity(expectedName), "2",
				"Adding the same product twice should increase its cart quantity");
		assertMoneyEquals(money(cartPage.productPrice(expectedName)), unitPrice,
				"Cart should keep the selected product unit price");
		assertMoneyEquals(money(cartPage.productSubtotal(expectedName)), expectedSubtotal,
				"Cart line subtotal should equal unit price multiplied by quantity");
		Assert.assertEquals(cartPage.totalItems(), "2", "Cart summary should count both units");
		assertMoneyEquals(money(cartPage.totalAmount()), expectedSubtotal,
				"Cart summary total should match the line subtotal");
	}

	private void clearCart() {
		CartPage cartPage = new CartPage(driver);
		cartPage.open();
		cartPage.clearIfNotEmpty();
	}

	private BigDecimal money(String value) {
		return new BigDecimal(value.replace("Rs.", "").trim());
	}

	private void assertMoneyEquals(BigDecimal actual, BigDecimal expected, String message) {
		Assert.assertEquals(actual.compareTo(expected), 0, message);
	}
}
