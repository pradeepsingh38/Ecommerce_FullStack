import fs from "node:fs/promises";
import path from "node:path";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

const rows = [
  ["TC-01", "1. User Authentication & Profile Management", "User Registration", "Verify a new customer can register successfully", "Unique email is available", "1. Open register page.\n2. Enter name, email, and password.\n3. Click Sign Up.", "Account created successfully.\nUser can login with new account.", "Positive"],
  ["TC-02", "1. User Authentication & Profile Management", "User Login", "Verify registered user can login", "Valid user account exists", "1. Open login page.\n2. Enter valid email and password.\n3. Click Login.", "Login successful.\nUser redirected to dashboard.", "Positive"],
  ["TC-03", "1. User Authentication & Profile Management", "Invalid Password Login", "Verify invalid password is rejected", "Registered account exists", "1. Open login page.\n2. Enter valid email.\n3. Enter wrong password.\n4. Click Login.", "Login fails.\nError message is displayed.", "Positive"],
  ["TC-04", "1. User Authentication & Profile Management", "Empty Login Fields Validation", "Verify empty login fields are validated", "None", "1. Open login page.\n2. Leave email and password blank.\n3. Click Login.", "Validation message displayed.\nSubmission blocked.", "Positive"],
  ["TC-05", "1. User Authentication & Profile Management", "Logout User", "Verify user can logout successfully", "User is logged in", "1. Click Logout button.", "User logged out.\nUser redirected to login page.", "Positive"],
  ["TC-06", "1. User Authentication & Profile Management", "Protected Route Access", "Verify protected pages cannot open without login", "User is logged out", "1. Open dashboard/cart/profile URL directly.", "User redirected to login page.", "Positive"],
  ["TC-07", "2. User Profile Module", "View Profile Details", "Verify logged-in user can view profile details", "User logged in", "1. Open Profile page.", "Name, email, and saved details are displayed.", "Positive"],
  ["TC-08", "2. User Profile Module", "Update Profile Name", "Verify user can update profile name", "User logged in", "1. Open Profile page.\n2. Click Edit name.\n3. Enter new name.\n4. Save.", "Profile name updated successfully.", "Positive"],
  ["TC-09", "2. User Profile Module", "Send Password Reset Link", "Verify password reset link can be requested", "User logged in with valid email", "1. Open Profile page.\n2. Click Update password.\n3. Click Send reset link.", "Password reset link request submitted.\nMessage displayed.", "Positive"],
  ["TC-10", "2. User Profile Module", "Add New Address", "Verify user can add a delivery address", "User logged in", "1. Open Profile page.\n2. Click Manage addresses.\n3. Click Add address.\n4. Fill address form.\n5. Save.", "New address saved.\nAddress displayed in address list.", "Positive"],
  ["TC-11", "2. User Profile Module", "Edit Saved Address", "Verify editing address updates existing address", "User has saved address", "1. Open Profile page.\n2. Manage addresses.\n3. Click Edit.\n4. Modify address.\n5. Save.", "Existing address updated.\nNo duplicate address created.", "Positive"],
  ["TC-12", "2. User Profile Module", "Invalid Address Pincode", "Verify invalid pincode is rejected", "User logged in", "1. Open address form.\n2. Enter pincode less than 6 digits.\n3. Save.", "Validation error displayed for pincode.", "Positive"],
  ["TC-13", "3. Home/Dashboard Module", "Dashboard UI Layout", "Verify dashboard layout loads correctly", "User logged in", "1. Open dashboard page.\n2. Observe sidebar, cards, and header.", "UI loads without overlap.\nContent is visible.", "Positive"],
  ["TC-14", "3. Home/Dashboard Module", "Sidebar Navigation", "Verify sidebar buttons navigate correctly", "User logged in", "1. Click Products.\n2. Click Cart.\n3. Click My Orders.\n4. Click Profile.", "Each button redirects to correct page.", "Positive"],
  ["TC-15", "3. Home/Dashboard Module", "Responsive Dashboard Layout", "Verify dashboard works on different screen sizes", "User logged in", "1. Open browser responsive mode.\n2. Test mobile, tablet, and desktop widths.", "Dashboard adjusts correctly.\nNo broken layout.", "Positive"],
  ["TC-16", "4. Product Module", "View Product Listing", "Verify products are displayed to user", "Products exist in database", "1. Open Products page.", "Product cards display image, name, price, and action buttons.", "Positive"],
  ["TC-17", "4. Product Module", "Search Product", "Verify product search works", "Products exist in database", "1. Open Products page.\n2. Enter product keyword in search.\n3. Submit search.", "Relevant products are displayed.", "Positive"],
  ["TC-18", "4. Product Module", "Filter Product by Category", "Verify category filter works", "Products with categories exist", "1. Open Products page.\n2. Select category filter.", "Products from selected category are displayed.", "Positive"],
  ["TC-19", "4. Product Module", "View Product Details", "Verify product details page opens", "Product exists", "1. Open Products page.\n2. Click product card/details.", "Product details page shows image, description, price, stock, and quantity selector.", "Positive"],
  ["TC-20", "4. Product Module", "Add Product to Cart", "Verify user can add product to cart", "User logged in and product in stock", "1. Open product details.\n2. Select quantity.\n3. Click Add to Cart.", "Product added to cart successfully.", "Positive"],
  ["TC-21", "5. Cart Module", "View Cart Items", "Verify cart displays added items", "Cart has at least one item", "1. Open Cart page.", "Cart items displayed with quantity, price, subtotal, and total.", "Positive"],
  ["TC-22", "5. Cart Module", "Update Cart Quantity", "Verify cart quantity can be updated", "Cart has item with stock available", "1. Open Cart page.\n2. Change quantity.", "Cart updates quantity, subtotal, and total amount.", "Positive"],
  ["TC-23", "5. Cart Module", "Remove Cart Item", "Verify item can be removed from cart", "Cart has at least one item", "1. Open Cart page.\n2. Click Remove on item.", "Item removed from cart.\nTotals updated.", "Positive"],
  ["TC-24", "5. Cart Module", "Clear Cart", "Verify all cart items can be cleared", "Cart has multiple items", "1. Open Cart page.\n2. Click Clear Cart.", "Cart becomes empty.", "Positive"],
  ["TC-25", "6. Checkout Module", "Checkout with Saved Address", "Verify user can checkout using saved address", "User logged in, cart has items, and saved address exists", "1. Open Checkout page.\n2. Select saved address.\n3. Select payment method.\n4. Place order.", "Order placed successfully using selected saved address.", "Positive"],
  ["TC-26", "6. Checkout Module", "Checkout with New Address", "Verify user can enter new address during checkout", "User logged in and cart has items", "1. Open Checkout page.\n2. Click Add new delivery address.\n3. Enter valid address.\n4. Place order.", "Order placed successfully with new address.", "Positive"],
  ["TC-27", "6. Checkout Module", "Checkout Empty Address Validation", "Verify checkout blocks missing address", "Cart has items", "1. Open Checkout page.\n2. Select Add new address.\n3. Leave required fields blank.\n4. Try placing order.", "Validation prevents order submission.", "Positive"],
  ["TC-28", "7. Orders Module", "View My Orders", "Verify user can view order history", "User has placed at least one order", "1. Open My Orders page.", "User orders displayed with item details, total, and shipping address.", "Positive"],
  ["TC-29", "7. Orders Module", "Order Summary Values", "Verify order total and item count are correct", "User has placed order", "1. Open My Orders page.\n2. Check order amount and item count.", "Displayed total and item count match ordered products.", "Positive"],
  ["TC-30", "8. Admin Module", "Admin Product Management Access", "Verify admin can access product management", "Admin account exists", "1. Login as admin.\n2. Open Products page.", "Admin can view manage, edit, and add product options.", "Positive"],
  ["TC-31", "8. Admin Module", "Add Product as Admin", "Verify admin can add a new product", "Admin logged in", "1. Open Add Product page.\n2. Enter product details.\n3. Save product.", "Product added and displayed in product list.", "Positive"],
  ["TC-32", "8. Admin Module", "Edit Product as Admin", "Verify admin can edit product details", "Admin logged in and product exists", "1. Open product edit page.\n2. Modify details.\n3. Save.", "Product details updated successfully.", "Positive"],
  ["TC-33", "8. Admin Module", "User Cannot Access Admin Pages", "Verify normal user cannot access admin features", "Normal user logged in", "1. Open admin-only route directly.", "Access denied or redirected.", "Positive"],
  ["TC-34", "8. Admin Module", "Admin View Users", "Verify admin can view registered users", "Admin logged in", "1. Open Users page.", "User list is displayed correctly.", "Positive"],
  ["TC-35", "8. Admin Module", "Admin View Order History", "Verify admin can view all orders", "Admin logged in and orders exist", "1. Open Order History page.", "All customer orders are displayed.", "Positive"],
];

const headers = [
  "Test Case ID",
  "Module/Category",
  "Test Case Name",
  "Objective",
  "Prerequisites",
  "Test Steps",
  "Expected Result",
  "Actual Result",
];

const workbook = Workbook.create();
const sheet = workbook.worksheets.add("Test Cases");
sheet.showGridLines = false;

sheet.getRange("A1:H1").values = [headers];
sheet.getRange(`A2:H${rows.length + 1}`).values = rows;

const fullRange = sheet.getRange(`A1:H${rows.length + 1}`);
fullRange.format = {
  font: { name: "Calibri", size: 11, color: "#102033" },
  wrapText: true,
  verticalAlignment: "top",
};

sheet.getRange("A1:H1").format = {
  fill: "#0F766E",
  font: { bold: true, color: "#FFFFFF" },
  horizontalAlignment: "center",
  verticalAlignment: "middle",
  wrapText: true,
};

sheet.getRange(`A2:A${rows.length + 1}`).format = { horizontalAlignment: "center", verticalAlignment: "top" };
sheet.getRange(`H2:H${rows.length + 1}`).format = {
  fill: "#ECFDF5",
  font: { bold: true, color: "#047857" },
  horizontalAlignment: "center",
  verticalAlignment: "top",
};

const widths = [90, 210, 190, 250, 220, 330, 300, 110];
widths.forEach((widthPx, index) => {
  sheet.getRangeByIndexes(0, index, rows.length + 1, 1).format.columnWidthPx = widthPx;
});

sheet.getRange("A1:H1").format.rowHeightPx = 34;
sheet.getRange(`A2:H${rows.length + 1}`).format.rowHeightPx = 124;

sheet.freezePanes.freezeRows(1);
const table = sheet.tables.add(`A1:H${rows.length + 1}`, true, "TestCasesTable");
table.style = "TableStyleMedium4";

const outputPath = path.resolve("ecommerce_test_cases_formatted.xlsx");
const preview = await workbook.render({ sheetName: "Test Cases", range: "A1:H12", scale: 1, format: "png" });
await fs.writeFile(path.resolve("ecommerce_test_cases_preview.png"), new Uint8Array(await preview.arrayBuffer()));
const output = await SpreadsheetFile.exportXlsx(workbook);
await output.save(outputPath);

console.log(outputPath);
