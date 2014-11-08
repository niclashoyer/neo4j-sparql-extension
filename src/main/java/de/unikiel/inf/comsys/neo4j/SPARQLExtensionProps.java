package de.unikiel.inf.comsys.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Container class that loads settings from a property file.
 * 
 * If there is a "sparql-extension.properties" file in the classpath
 * these settings will be used instead of the default settings.
 */
public class SPARQLExtensionProps {

	private static final SPARQLExtensionProps instance
			= new SPARQLExtensionProps();
	private static final String prefix = "de.unikiel.inf.comsys.neo4j.";

	private final Properties props;

	private SPARQLExtensionProps() {
		Properties defs = new Properties();
		try {
			defs.load(
					SPARQLExtensionProps.class
					.getResourceAsStream("default.properties"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		props = new Properties(defs);
		InputStream in = SPARQLExtensionProps.class
				.getResourceAsStream("/sparql-extension.properties");
		if (in != null) {
			try {
				props.load(in);
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * Returns the SPARQL extension properties.
	 * @return properties
	 */
	public Properties getProperties() {
		return props;
	}

	/**
	 * Returns a specific key from the SPARQL extension properties.
	 * @param key the key to look up in the properties
	 * @return the value of the key
	 */
	public static String getProperty(String key) {
		return instance.getProperties().getProperty(prefix + key);
	}

	/**
	 * Returns a specific key from the SPARQL extension properties and
	 * uses a default value if the key is not set.
	 * @param key the key to look up in the properties
	 * @param defaultValue the default value to use when key is not present
	 * @return the value of the key
	 */
	public static String getProperty(String key, String defaultValue) {
		return instance.getProperties().getProperty(prefix + key, defaultValue);
	}

}
