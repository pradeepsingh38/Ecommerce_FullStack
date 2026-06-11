package com.ecommerce.automation.base;

import java.time.Duration;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

	public static final String BASE_URL = TestConfig.BASE_URL;

	protected WebDriver driver;

	@BeforeClass(alwaysRun = true)
	public void setUp() {
		ChromeOptions options = new ChromeOptions();
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		options.addArguments(
				"--remote-allow-origins=*",
				"--disable-background-networking",
				"--disable-extensions",
				"--disable-notifications",
				"--no-first-run");
		if (TestConfig.HEADLESS) {
			options.addArguments("--headless=new", "--window-size=1920,1080");
		}

		driver = new ChromeDriver(options);
		if (!TestConfig.HEADLESS) {
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.PAGE_LOAD_TIMEOUT_SECONDS));
		driver.get(BASE_URL);
	}

	@BeforeMethod(alwaysRun = true)
	public void resetBrowserState() {
		driver.manage().deleteAllCookies();
		((JavascriptExecutor) driver).executeScript(
				"window.localStorage.clear(); window.sessionStorage.clear();");
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
