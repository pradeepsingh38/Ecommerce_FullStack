package com.ecommerce.automation.driver;

import com.ecommerce.automation.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {

	private DriverFactory() {
	}

	public static WebDriver createDriver() {
		String browser = TestConfig.browser().toLowerCase();
		if (!"chrome".equals(browser)) {
			throw new IllegalArgumentException("Unsupported browser: " + browser);
		}
		return createChromeDriver();
	}

	private static WebDriver createChromeDriver() {
		WebDriverManager.chromedriver().setup();

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		options.addArguments("--start-maximized");
		if (TestConfig.headless()) {
			options.addArguments("--headless=new");
			options.addArguments("--window-size=1920,1080");
		}

		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().implicitlyWait(TestConfig.implicitWait());
		return driver;
	}
}
