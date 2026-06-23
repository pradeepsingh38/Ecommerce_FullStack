package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.components.ProductCard;
import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.CheckoutPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutFlowTest extends BaseTest {

	@Test(description = "T096: Automate checkout initiation")
	public void shouldInitiateCheckoutFromCart() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstInStockProduct();
		String expectedName = product.name();
		String expectedPrice = product.price();

		product.addToCart();
		Assert.assertTrue(productsPage.successToastMessage().contains(expectedName),
				"Add-to-cart toast should mention the selected product before checkout");
		productsPage.openCart();

		CartPage cartPage = new CartPage(driver);
		cartPage.waitForProduct(expectedName);
		cartPage.startCheckout();

		CheckoutPage checkoutPage = new CheckoutPage(driver);
		Assert.assertTrue(checkoutPage.isLoaded(), "Checkout page should load after clicking Checkout in cart");
		Assert.assertTrue(checkoutPage.hasSummaryProduct(expectedName),
				"Checkout summary should include the cart product");
		Assert.assertEquals(checkoutPage.totalItems(), "1", "Checkout summary should show the cart item count");
		Assert.assertEquals(checkoutPage.totalAmount(), expectedPrice,
				"Checkout total should match the cart product price");
	}

	@Test(description = "T097: Validate form inputs")
	public void shouldValidateCheckoutFormInputs() {
		LoginUtils.loginAsDefaultUser(driver);
		clearCart();

		ProductsPage productsPage = new ProductsPage(driver);
		productsPage.open();
		ProductCard product = productsPage.firstInStockProduct();
		String expectedName = product.name();
		product.addToCart();
		productsPage.openCart();

		CartPage cartPage = new CartPage(driver);
		cartPage.waitForProduct(expectedName);
		cartPage.startCheckout();

		CheckoutPage checkoutPage = new CheckoutPage(driver);
		checkoutPage.chooseNewAddress();

		Assert.assertFalse(checkoutPage.isPlaceOrderEnabled(),
				"Place Order should stay disabled until required address fields are completed");
		Assert.assertEquals(checkoutPage.fieldAttribute("houseNo", "required"), "true",
				"House/flat number should be required for a new delivery address");
		Assert.assertEquals(checkoutPage.fieldAttribute("city", "required"), "true",
				"City should be required for a new delivery address");
		Assert.assertEquals(checkoutPage.fieldAttribute("pincode", "pattern"), "[0-9]{6}",
				"Pincode should require exactly six digits");
		Assert.assertEquals(checkoutPage.fieldAttribute("state", "required"), "true",
				"State should be required for a new delivery address");

		checkoutPage.enterAddress("221B", "Baker Street", "Bengaluru", "12345", "Karnataka");
		Assert.assertFalse(checkoutPage.isFieldValid("pincode"),
				"Five-digit pincode should fail browser validation");

		checkoutPage.enterAddress("221B", "Baker Street", "Bengaluru", "560001", "Karnataka");
		checkoutPage.enterContactNumber("9876543210");
		checkoutPage.choosePaymentMethod("UPI");

		Assert.assertEquals(checkoutPage.fieldValue("contactNumber"), "9876543210",
				"Contact number should retain the entered value");
		Assert.assertTrue(checkoutPage.isFieldValid("pincode"),
				"Six-digit pincode should pass browser validation");
		Assert.assertTrue(checkoutPage.isPlaceOrderEnabled(),
				"Place Order should be enabled once the checkout form is valid");
	}

	private void clearCart() {
		CartPage cartPage = new CartPage(driver);
		cartPage.open();
		cartPage.clearIfNotEmpty();
	}
}
