import fs from "node:fs/promises";
import path from "node:path";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

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

const rows = [
  ["TC-01", "1. User Authentication & Profile Management", "User Registration", "Verify a new customer can successfully register a unique account.", "Clean database state or using a unique email.", "1. Navigate to register page.\n2. Enter valid name, email, and password.\n3. Click Sign Up button.", "Account created successfully.\nRedirect to login/dashboard.\nSuccess message displayed.", "Positive"],
  ["TC-02", "1. User Authentication & Profile Management", "User Login & Session Persistence", "Verify users can log in and retain session state.", "Valid registered account exists.", "1. Open login page.\n2. Enter correct credentials.\n3. Click Login.\n4. Refresh page.", "Login successful.\nSession remains active.\nJWT token remains available.", "Positive"],
  ["TC-03", "1. User Authentication & Profile Management", "Invalid Password Login", "Verify system rejects invalid passwords.", "Registered account exists.", "1. Open login page.\n2. Enter valid email.\n3. Enter wrong password.\n4. Click Login.", "Login fails.\nError message displayed.", "Positive"],
  ["TC-04", "1. User Authentication & Profile Management", "Empty Fields Validation", "Verify validation triggers for empty fields.", "None.", "1. Open login page.\n2. Leave fields blank.\n3. Click Login.", "Validation messages displayed.\nSubmission blocked.", "Positive"],
  ["TC-05", "1. User Authentication & Profile Management", "Password Validation Rules", "Verify password validation on registration.", "None.", "1. Open signup page.\n2. Enter weak password.\n3. Submit form.", "Weak password warning or validation message displayed.", "Positive"],
  ["TC-06", "1. User Authentication & Profile Management", "Protected Route Access", "Verify unauthenticated users cannot access protected pages.", "User is logged out.", "1. Open dashboard/cart/profile URL directly.", "User redirected to login page.", "Positive"],
  ["TC-07", "1. User Authentication & Profile Management", "Session Timeout", "Verify session expires after token timeout or inactivity.", "User logged in.", "1. Leave system idle.\n2. Wait for session/token expiry.\n3. Try protected action.", "Session expires.\nUser redirected to login.", "Negative"],
  ["TC-08", "1. User Authentication & Profile Management", "Multiple Login Attempts", "Verify repeated wrong login attempts are handled safely.", "Registered account exists.", "1. Open login page.\n2. Attempt wrong password multiple times.", "Login remains rejected.\nError message displayed for each invalid attempt.", "Negative"],
  ["TC-09", "2. User Profile Module", "View User Profile", "Verify user can view profile information.", "User logged in.", "1. Open profile page.", "Name, email, and saved address information displayed.", "Positive"],
  ["TC-10", "2. User Profile Module", "Update User Details", "Verify user can update profile details.", "User logged in.", "1. Open profile page.\n2. Click Edit name.\n3. Update details.\n4. Save changes.", "Profile updated successfully.", "Positive"],
  ["TC-11", "2. User Profile Module", "Invalid Email Update", "Verify validation for invalid email/profile data.", "User logged in.", "1. Open profile page.\n2. Enter invalid email/details.\n3. Save changes.", "Validation error displayed.\nInvalid data not saved.", "Positive"],
  ["TC-12", "2. User Profile Module", "Password Reset Link", "Verify password reset link request flow.", "User logged in with valid email.", "1. Open profile page.\n2. Click Update password.\n3. Click Send reset link.", "Password reset link message displayed.", "Positive"],
  ["TC-13", "2. User Profile Module", "Add New Address", "Verify user can add a new saved address.", "User logged in.", "1. Open profile page.\n2. Click Manage addresses.\n3. Click Add address.\n4. Fill address form.\n5. Save.", "New address saved and displayed correctly.", "Positive"],
  ["TC-14", "2. User Profile Module", "Edit Existing Address", "Verify editing an address updates same address.", "User has saved address.", "1. Open address book.\n2. Click Edit on saved address.\n3. Change address data.\n4. Save.", "Address updated successfully.\nNo duplicate address created.", "Positive"],
  ["TC-15", "2. User Profile Module", "Invalid Address Pincode", "Verify invalid pincode is rejected.", "User logged in.", "1. Open address form.\n2. Enter invalid pincode.\n3. Save address.", "Pincode validation error displayed.", "Positive"],
  ["TC-16", "2. User Profile Module", "Multiple Addresses Management", "Verify multiple addresses can be managed.", "User logged in.", "1. Add more than one address.\n2. Open address book.\n3. Review saved addresses.", "Multiple addresses displayed correctly.", "Positive"],
  ["TC-17", "2. User Profile Module", "Cancel Address Form", "Verify cancel/close address form does not crash system.", "User logged in.", "1. Open address form.\n2. Click close/cancel.", "Form closes safely.\nNo data corruption or crash occurs.", "Positive"],
  ["TC-18", "3. Home Page Module", "UI Elements Layout Validation", "Verify homepage/dashboard UI alignment.", "User logged in.", "1. Open dashboard/home page.\n2. Observe sidebar, header, cards, and buttons.", "UI loads without overlap.\nText and buttons are visible.", "Positive"],
  ["TC-19", "3. Home Page Module", "Responsive Layout Testing", "Verify responsive behavior.", "Desktop browser open.", "1. Open responsive mode.\n2. Test multiple resolutions.", "Layout adjusts correctly across screen sizes.", "Positive"],
  ["TC-20", "3. Home Page Module", "Sidebar Navigation Linkages", "Verify sidebar links redirect correctly.", "User logged in.", "1. Click Products.\n2. Click Cart.\n3. Click My Orders.\n4. Click Profile.", "Redirects to correct page for each sidebar option.", "Positive"],
  ["TC-21", "3. Home Page Module", "Dashboard Action Button", "Verify main dashboard action button works.", "User logged in.", "1. Open dashboard.\n2. Click Shop Products button.", "User redirected to products page.", "Positive"],
  ["TC-22", "3. Home Page Module", "Dashboard Cards Navigation", "Verify dashboard cards open correct modules.", "User logged in.", "1. Click Products card.\n2. Click Cart card.\n3. Click Orders card.", "Correct module/page opens for each card.", "Positive"],
  ["TC-23", "3. Home Page Module", "Broken Link Integrity Audits", "Verify all homepage links/buttons work.", "User logged in.", "1. Click all homepage/sidebar buttons.\n2. Check page redirections.", "No broken links found.", "Positive"],
];

function csvCell(value) {
  const text = String(value ?? "");
  return /[",\n]/.test(text) ? `"${text.replaceAll('"', '""')}"` : text;
}

const csv = [headers, ...rows].map((row) => row.map(csvCell).join(",")).join("\n");
await fs.writeFile(path.resolve("ecommerce_test_cases.csv"), csv, "utf8");

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

const widths = [90, 230, 200, 260, 230, 340, 310, 110];
widths.forEach((widthPx, index) => {
  sheet.getRangeByIndexes(0, index, rows.length + 1, 1).format.columnWidthPx = widthPx;
});

sheet.getRange("A1:H1").format.rowHeightPx = 36;
sheet.getRange(`A2:H${rows.length + 1}`).format.rowHeightPx = 124;

sheet.freezePanes.freezeRows(1);
const table = sheet.tables.add(`A1:H${rows.length + 1}`, true, "TestCasesTable");
table.style = "TableStyleMedium4";

const preview = await workbook.render({ sheetName: "Test Cases", range: "A1:H12", scale: 1, format: "png" });
await fs.writeFile(path.resolve("ecommerce_test_cases_preview.png"), new Uint8Array(await preview.arrayBuffer()));
const output = await SpreadsheetFile.exportXlsx(workbook);
await output.save(path.resolve("ecommerce_test_cases_formatted.xlsx"));

console.log(path.resolve("ecommerce_test_cases_formatted.xlsx"));
