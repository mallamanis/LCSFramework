/**
 * 
 */
package gr.auth.ee.lcs.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Load parameters from a file
 * 
 * @author Miltiadis Allamanis
 * 
 */
public class SettingsLoader {

	private final Properties lcsProperties;

	private SettingsLoader() throws IOException {
		lcsProperties = new Properties();
		loadProperties("defaultLcs.properties");
	}

	private SettingsLoader(String file) throws IOException {
		lcsProperties = new Properties();
		loadProperties(file);
	}

	private void loadProperties(String filename) throws IOException {
		final FileInputStream input = new FileInputStream(
				"defaultLcs.properties");
		lcsProperties.load(input);
	}

	private double getNumericProperty(String propertyName, double defaultValue) {
		try {
			return Double.parseDouble(lcsProperties.getProperty(propertyName,
					Double.toString(defaultValue)));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static double getNumericSetting(String propertyName,
			double defaultValue) {
		return instance.getNumericProperty(propertyName, defaultValue);
	}

	private String getStringProperty(String propertyName, String defaultValue) {
		return lcsProperties.getProperty(propertyName, defaultValue);
	}

	public static String getStringSetting(String propertyName,
			String defaultValue) {
		return instance.getStringProperty(propertyName, defaultValue);
	}

	private static SettingsLoader instance;

	public static void loadSettings() throws IOException {
		instance = new SettingsLoader();
	}

	public static void loadSettings(String filename) throws IOException {
		instance = new SettingsLoader(filename);
	}

}
