package com.ecommerce.automation.base;

import java.time.Duration;

import com.ecommerce.automation.config.TestConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

	public static final String BASE_URL = TestConfig.BASE_URL;

	protected WebDriver driver;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		if (TestConfig.HEADLESS) {
			options.addArguments("--headless=new", "--window-size=1920,1080");
		}

		driver = new ChromeDriver(options);
		if (!TestConfig.HEADLESS) {
			driver.manage().window().maximize();
		}
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TestConfig.PAGE_LOAD_TIMEOUT_SECONDS));
	}

	@AfterMethod(alwaysRun = true)
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
