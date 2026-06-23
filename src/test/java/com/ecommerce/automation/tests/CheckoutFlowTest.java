package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.components.ProductCard;
import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.CheckoutPage;
import com.ecommerce.automation.pages.ProductsPage;
import com.ecommerce.automation.pages.UserOrdersPage;
import com.ecommerce.automation.utils.LoginUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutFlowTest extends BaseTest {

	@Test(description = "T096: Automate checkout initiation and verify order appears in My Orders")
	public void shouldInitiateCheckoutAndShowPlacedOrderInOrders() {
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

		String houseNo = "T096-" + System.currentTimeMillis();
		String shippingAddress = houseNo + ", Automation Street, Bengaluru, 560001, Karnataka";
		String contactNumber = "98765432109";
		checkoutPage.chooseNewAddress();
		checkoutPage.enterAddress(houseNo, "Automation Street", "Bengaluru", "560001", "Karnataka");
		checkoutPage.enterContactNumber(contactNumber);
		checkoutPage.choosePaymentMethod("COD");
		checkoutPage.placeOrder();

		UserOrdersPage userOrdersPage = new UserOrdersPage(driver);
		Assert.assertTrue(userOrdersPage.isLoaded(), "My Orders page should load after placing an order");
		Assert.assertTrue(userOrdersPage.hasOrder(expectedName, shippingAddress, contactNumber),
				"Placed checkout order should appear in My Orders with product, address, and contact number");
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
		Assert.assertEquals(checkoutPage.fieldAttribute("contactNumber", "pattern"), "[0-9]{1,11}",
				"Contact number should accept at most eleven digits");

		checkoutPage.enterAddress("221B", "Baker Street", "Bengaluru", "12345", "Karnataka");
		Assert.assertFalse(checkoutPage.isFieldValid("pincode"),
				"Five-digit pincode should fail browser validation");
		Assert.assertEquals(checkoutPage.fieldError("pincode"), "Pincode must be exactly 6 digits",
				"Pincode field should show an inline error when it is invalid");

		checkoutPage.enterAddress("221B", "Baker Street", "Bengaluru", "1234567", "Karnataka");
		Assert.assertFalse(checkoutPage.isFieldValid("pincode"),
				"Seven-digit pincode should fail browser validation");
		Assert.assertEquals(checkoutPage.fieldError("pincode"), "Pincode must be exactly 6 digits",
				"Pincode field should show an inline error when it has more than six digits");
		checkoutPage.submitOrder();
		Assert.assertEquals(checkoutPage.alertMessage(),
				"Please correct the highlighted checkout fields before placing your order",
				"Checkout should show an alert message when invalid pincode is submitted");
		Assert.assertTrue(checkoutPage.isOnCheckoutPage(),
				"Invalid pincode should block order placement and keep the user on checkout");

		checkoutPage.enterAddress("221B", "Baker Street", "Bengaluru", "560001", "Karnataka");
		checkoutPage.enterContactNumber("987654321098");
		Assert.assertFalse(checkoutPage.isFieldValid("contactNumber"),
				"Twelve-digit contact number should fail browser validation");
		Assert.assertEquals(checkoutPage.fieldError("contactNumber"), "Contact number must not be more than 11 digits",
				"Contact number field should show an inline error when it has more than eleven digits");
		checkoutPage.submitOrder();
		Assert.assertEquals(checkoutPage.alertMessage(),
				"Please correct the highlighted checkout fields before placing your order",
				"Checkout should show an alert message when invalid contact number is submitted");
		Assert.assertTrue(checkoutPage.isOnCheckoutPage(),
				"Invalid contact number should block order placement and keep the user on checkout");

		checkoutPage.enterContactNumber("9876543210");
		checkoutPage.choosePaymentMethod("UPI");

		Assert.assertEquals(checkoutPage.fieldValue("contactNumber"), "9876543210",
				"Contact number should retain the entered value");
		Assert.assertTrue(checkoutPage.isFieldValid("pincode"),
				"Six-digit pincode should pass browser validation");
		Assert.assertTrue(checkoutPage.isFieldValid("contactNumber"),
				"Ten-digit contact number should pass browser validation");
		Assert.assertTrue(checkoutPage.isPlaceOrderEnabled(),
				"Place Order should be enabled once the checkout form is valid");
	}

	@Test(description = "T097: Reject checkout form with invalid pincode and contact number")
	public void shouldRejectCheckoutFormWithInvalidPincodeAndContactNumber() {
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
		checkoutPage.enterAddress("44", "Invalid Data Street", "Bengaluru", "1234567", "Karnataka");
		checkoutPage.enterContactNumber("987654321098");

		Assert.assertFalse(checkoutPage.isFieldValid("pincode"),
				"Pincode with more than six digits should be invalid");
		Assert.assertFalse(checkoutPage.isFieldValid("contactNumber"),
				"Contact number with more than eleven digits should be invalid");
		Assert.assertEquals(checkoutPage.fieldError("pincode"), "Pincode must be exactly 6 digits",
				"Invalid pincode should show an inline field error");
		Assert.assertEquals(checkoutPage.fieldError("contactNumber"), "Contact number must not be more than 11 digits",
				"Invalid contact number should show an inline field error");

		checkoutPage.submitOrder();

		Assert.assertEquals(checkoutPage.alertMessage(),
				"Please correct the highlighted checkout fields before placing your order",
				"Checkout should show an alert message when invalid data is submitted");
		Assert.assertTrue(checkoutPage.isOnCheckoutPage(),
				"Invalid checkout data should block order placement");
	}

	private void clearCart() {
		CartPage cartPage = new CartPage(driver);
		cartPage.open();
		cartPage.clearIfNotEmpty();
	}
}
