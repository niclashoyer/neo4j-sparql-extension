
package de.unikiel.inf.comsys.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SPARQLExtensionProps {
	
	private static final SPARQLExtensionProps instance = new SPARQLExtensionProps();
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
	
	public Properties getProperties() {
		return props;
	}
	
	public static String getProperty(String key) {
		return instance.getProperties().getProperty(prefix + key);
	}
	
	public static String getProperty(String key, String defaultValue) {
		return instance.getProperties().getProperty(prefix + key, defaultValue);
	}
	
}
