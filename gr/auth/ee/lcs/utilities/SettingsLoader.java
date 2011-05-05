/**
 * 
 */
package gr.auth.ee.lcs.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Load parameters from a file utility.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class SettingsLoader {

	/**
	 * Private instance of properties.
	 */
	private final Properties lcsProperties;

	/**
	 * Constructor, loads defaultLcs.properties file.
	 * 
	 * @throws IOException
	 *             when default properties file is not found
	 */
	private SettingsLoader() throws IOException {
		lcsProperties = new Properties();
		loadProperties("defaultLcs.properties");
	}

	/**
	 * Constructor loads an arbitrary file.
	 * 
	 * @param file
	 *            the .properties filename to load
	 * @throws IOException
	 *             when default properties file is not found
	 */
	private SettingsLoader(final String file) throws IOException {
		lcsProperties = new Properties();
		loadProperties(file);
	}

	/**
	 * Load the properties from a file.
	 * 
	 * @param filename
	 *            the .properties filename
	 * @throws IOException
	 *             when default properties file is not found
	 */
	private void loadProperties(final String filename) throws IOException {
		final FileInputStream input = new FileInputStream(
				"defaultLcs.properties");
		lcsProperties.load(input);
	}

	/**
	 * Return a numeric property in the properties file.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param defaultValue
	 *            the default value to be used for this property when the
	 *            numeric property is either not set or invalid
	 * @return the numeric property at the loaded file or the default value if
	 *         one is not found
	 */
	private double getNumericProperty(final String propertyName,
			final double defaultValue) {
		try {
			return Double.parseDouble(lcsProperties.getProperty(propertyName,
					Double.toString(defaultValue)));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	/**
	 * Static getter for the singleton instance.
	 * 
	 * @param propertyName
	 *            the name of the property file to be used
	 * @param defaultValue
	 *            the default value of the property
	 * @return he numeric property at the loaded file or the default value if
	 *         one is not found
	 */
	public static double getNumericSetting(final String propertyName,
			final double defaultValue) {
		return instance.getNumericProperty(propertyName, defaultValue);
	}

	/**
	 * Return a string property.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param defaultValue
	 *            the default value of the property
	 * @return the string of the property at the loaded file or the default
	 *         value if one is not found
	 */
	private String getStringProperty(final String propertyName,
			final String defaultValue) {
		return lcsProperties.getProperty(propertyName, defaultValue);
	}

	/**
	 * Static getter of a string property from the loaded file.
	 * 
	 * @param propertyName
	 *            the name of the property
	 * @param defaultValue
	 *            the default value of the property
	 * @return the string of the property at the loaded file or the default
	 *         value if one is not found
	 */
	public static String getStringSetting(final String propertyName,
			final String defaultValue) {
		return instance.getStringProperty(propertyName, defaultValue);
	}

	/**
	 * The unique static instance of the SettingsLoader.
	 */
	private static SettingsLoader instance;

	/**
	 * Load to the static store the settings file.
	 * 
	 * @throws IOException
	 *             when default file is not found
	 */
	public static void loadSettings() throws IOException {
		instance = new SettingsLoader();
	}

	/**
	 * Load to the static store a settings file.
	 * 
	 * @param filename
	 *            the filename to load
	 * @throws IOException
	 *             when file is not found
	 */
	public static void loadSettings(final String filename) throws IOException {
		instance = new SettingsLoader(filename);
	}

}
