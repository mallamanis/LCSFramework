/*
 *	Copyright (C) 2011 by Allamanis Miltiadis
 *
 *	Permission is hereby granted, free of charge, to any person obtaining a copy
 *	of this software and associated documentation files (the "Software"), to deal
 *	in the Software without restriction, including without limitation the rights
 *	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *	copies of the Software, and to permit persons to whom the Software is
 *	furnished to do so, subject to the following conditions:
 *
 *	The above copyright notice and this permission notice shall be included in
 *	all copies or substantial portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *	THE SOFTWARE.
 */
/**
 * 
 */
package gr.auth.ee.lcs.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Load parameters from a file utility.
 * 
 * @author Miltiadis Allamanis
 * 
 */
public final class SettingsLoader {

	/**
	 * The static logger instance.
	 */
	private static final Logger CLASS_LOGGER = Logger
			.getLogger(SettingsLoader.class.getName());

	/**
	 * Private instance of properties.
	 */
	private final Properties lcsProperties;

	/**
	 * The unique static instance of the SettingsLoader.
	 */
	private static SettingsLoader instance;

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
		if (instance == null)
			try {
				loadSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
		final double value = instance.getNumericProperty(propertyName,
				defaultValue);
		final String output = "Parameter " + propertyName + " set to " + value;
		CLASS_LOGGER.config(output);
		return value;
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
		if (instance == null)
			try {
				loadSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
		final String value = instance.getStringProperty(propertyName,
				defaultValue);
		CLASS_LOGGER.config("Parameter " + propertyName + " set to " + value);
		return value;
	}

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
			final double value = Double.parseDouble(lcsProperties.getProperty(
					propertyName, Double.toString(defaultValue)));
			return value;
		} catch (Exception ex) {
			return defaultValue;
		}
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
		CLASS_LOGGER.fine("Loaded properties file " + filename);
		input.close();
	}

}
