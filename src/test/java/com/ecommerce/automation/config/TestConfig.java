package com.ecommerce.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public final class TestConfig {

	private static final String CONFIG_FILE = "/automation.properties";
	private static final Properties PROPERTIES = loadProperties();

	private TestConfig() {
	}

	public static String baseUrl() {
		return get("base.url", "http://localhost:5173");
	}

	public static String browser() {
		return get("browser", "chrome");
	}

	public static boolean headless() {
		return Boolean.parseBoolean(get("headless", "false"));
	}

	public static Duration implicitWait() {
		return Duration.ofSeconds(Long.parseLong(get("implicit.wait.seconds", "5")));
	}

	public static Duration explicitWait() {
		return Duration.ofSeconds(Long.parseLong(get("explicit.wait.seconds", "10")));
	}

	private static String get(String key, String defaultValue) {
		return System.getProperty(key, PROPERTIES.getProperty(key, defaultValue));
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream inputStream = TestConfig.class.getResourceAsStream(CONFIG_FILE)) {
			if (inputStream != null) {
				properties.load(inputStream);
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to load automation test configuration", exception);
		}
		return properties;
	}
}
