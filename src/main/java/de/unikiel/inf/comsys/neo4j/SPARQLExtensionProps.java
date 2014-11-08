package de.unikiel.inf.comsys.neo4j;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
