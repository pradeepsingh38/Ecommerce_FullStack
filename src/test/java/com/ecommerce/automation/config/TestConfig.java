package com.ecommerce.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {

	private static final String CONFIG_FILE = "automation.properties";
	private static final Properties PROPERTIES = loadProperties();

	public static final String BASE_URL = value("automation.base-url");
	public static final String VALID_EMAIL = value("automation.valid-email");
	public static final String VALID_PASSWORD = value("automation.valid-password");
	public static final String INVALID_EMAIL = value("automation.invalid-email");
	public static final String INVALID_PASSWORD = value("automation.invalid-password");
	public static final String BAD_CREDENTIALS_MESSAGE = value("automation.bad-credentials-message");
	public static final long EXPLICIT_WAIT_SECONDS = longValue("automation.explicit-wait-seconds");
	public static final long PAGE_LOAD_TIMEOUT_SECONDS = longValue("automation.page-load-timeout-seconds");
	public static final boolean HEADLESS = booleanValue("automation.headless", false);

	private TestConfig() {
	}

	private static Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (input == null) {
				throw new IllegalStateException("Missing test configuration: " + CONFIG_FILE);
			}
			properties.load(input);
			return properties;
		} catch (IOException exception) {
			throw new IllegalStateException("Unable to load test configuration: " + CONFIG_FILE, exception);
		}
	}

	private static String value(String key) {
		String systemValue = System.getProperty(key);
		if (systemValue != null && !systemValue.isBlank()) {
			return systemValue;
		}

		String environmentValue = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_'));
		if (environmentValue != null && !environmentValue.isBlank()) {
			return environmentValue;
		}

		String configuredValue = PROPERTIES.getProperty(key);
		if (configuredValue == null || configuredValue.isBlank()) {
			throw new IllegalStateException("Missing test configuration value: " + key);
		}
		return configuredValue.trim();
	}

	private static long longValue(String key) {
		try {
			return Long.parseLong(value(key));
		} catch (NumberFormatException exception) {
			throw new IllegalStateException("Test configuration must be a number: " + key, exception);
		}
	}

	private static boolean booleanValue(String key, boolean defaultValue) {
		String systemValue = System.getProperty(key);
		if (systemValue != null && !systemValue.isBlank()) {
			return Boolean.parseBoolean(systemValue);
		}

		String environmentValue = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_'));
		if (environmentValue != null && !environmentValue.isBlank()) {
			return Boolean.parseBoolean(environmentValue);
		}

		String configuredValue = PROPERTIES.getProperty(key);
		return configuredValue == null || configuredValue.isBlank()
				? defaultValue
				: Boolean.parseBoolean(configuredValue.trim());
	}
}
