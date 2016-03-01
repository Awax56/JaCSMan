/*#
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 LE SAUCE Julien
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 #*/

package org.jls.jacsman.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton managing the access to the resources of the application.
 * 
 * @author LE SAUCE Julien
 * @date Aug 31, 2015
 */
public class ResourceManager {

	/*
	 * Resources
	 */
	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String RESOURCES_DIR = "resources";
	public static final String IMG_DIR = "img";

	public static final String RESOURCES_PATH = getResource(".").getFile();

	public static final String LOG4J_FILE = "log4j2.xml";
	public static final String CONFIG_FILE = "configuration.xml";

	/**
	 * Unique instance of the class.
	 */
	private static ResourceManager INSTANCE = null;

	private final Logger logger;
	private CombinedConfiguration configuration;

	/**
	 * Instanciates the resources manager.
	 */
	private ResourceManager () {
		this.logger = LogManager.getLogger();
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		builder.setConfigurationBasePath(RESOURCES_PATH);
		builder.setBasePath(RESOURCES_PATH);
		try {
			builder.setFile(getResourceAsFile(CONFIG_FILE));
			builder.setEncoding("UTF8");
			this.configuration = builder.getConfiguration(true);
		} catch (Exception e) {
			this.logger.fatal("An error occured while building application properties", e);
			Runtime.getRuntime().exit(-1);
		}
	}

	/**
	 * Returns the unique instance of this class.
	 * 
	 * @return Unique instance of this class.
	 */
	public final static ResourceManager getInstance () {
		if (ResourceManager.INSTANCE == null) {
			ResourceManager.INSTANCE = new ResourceManager();
		}
		return ResourceManager.INSTANCE;
	}

	/**
	 * Finds the resource with the given name. A resource is some data (images,
	 * audio, text, etc). The name of a resource is a '/'-separated path name
	 * that identifies the resource.
	 * 
	 * @param name
	 *            The resource name.
	 * @return A {@link URL} object for reading the resource, or
	 *         <code>null</code> if the resource could not be found or the
	 *         invoker doesn't have adequate privileges to get the resource.
	 */
	public final static URL getResource (final String name) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(name);
		if (url == null) {
			url = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_DIR + File.separator + name);
		}
		return url;
	}

	/**
	 * Returns an {@link InputStream} for reading the specified resource.
	 * 
	 * @param name
	 *            The resource name.
	 * @return An input stream for reading the resource, or <code>null</code> if
	 *         the resource could not be found.
	 */
	public final static InputStream getResourceAsStream (final String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	}

	/**
	 * Returns a {@link File} for reading the specified resource. The file is
	 * created using the constructor {@link File#File(String)} where the
	 * argument is the path of the resource given by
	 * {@link ResourceManager#getResource(String)}.
	 * 
	 * @param name
	 *            The resource name.
	 * @return A file object to access the resource.
	 */
	public final static File getResourceAsFile (final String name) {
		return new File(getResource(name).getPath());
	}

	/**
	 * Sets the value of the specified property.
	 * 
	 * @param key
	 *            The key of the property to set.
	 * @param value
	 *            The new value of this property
	 * @return The old value of this property.
	 */
	public String setProperty (final String key, final String value) {
		if (value != null && !value.isEmpty()) {
			if (this.configuration.containsKey(key)) {
				String oldValue = this.getString(key);
				this.configuration.setProperty(key, value);
				return oldValue;
			}
			throw new IllegalArgumentException("Key does not exist : " + key);
		}
		throw new IllegalArgumentException("Value cannot be null or empty");
	}

	/**
	 * Get a string associated with the given configuration key. If the key does
	 * not exist, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param key
	 *            The configuration key.
	 * @return The associated string.
	 */
	public String getString (final String key) {
		if (this.configuration.containsKey(key)) {
			return this.configuration.getString(key);
		}
		throw new IllegalArgumentException("Key does not exist : " + key);
	}

	/**
	 * Get an integer associated with the given configuration key. If the key
	 * does not exist, an {@link IllegalArgumentException} is thrown. If the
	 * value cannot be decoded using {@link Integer#parseInt(String)}, a
	 * {@link NumberFormatException} is thrown.
	 * 
	 * @param key
	 *            The configuration key.
	 * @return The associated value.
	 */
	public int getInt (final String key) {
		String str = getString(key);
		if (!str.isEmpty()) {
			try {
				return Integer.parseInt(str);
			} catch (@SuppressWarnings("unused") Exception e) {
				throw new NumberFormatException("Cannot parse value to integer : " + str);
			}
		}
		throw new IllegalStateException("Empty value");
	}

	/**
	 * Get a color associated with the given configuration key. If the key does
	 * not exist, an {@link IllegalArgumentException} is thrown. If the value
	 * cannot be decoded using {@link Color#decode(String)}, an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param key
	 *            The configuration key.
	 * @return The associated color.
	 */
	public Color getColor (final String key) {
		String str = getString(key);
		if (!str.isEmpty()) {
			try {
				return Color.decode(str);
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"String cannot be interpreted as a decimal, octal, or hexadecimal integer : " + str, e);
			}
		}
		throw new IllegalStateException("Empty value");
	}

	/**
	 * Get an icon associated with the given configuration key. If the key does
	 * not exist, an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param key
	 *            The configuration key.
	 * @return The associated icon, or <code>null</code> if the resource could
	 *         not be found.
	 */
	public ImageIcon getIcon (final String key) {
		String filename = getString(key);
		String path = IMG_DIR + "/" + filename;

		// Creates an icon from the stream
		try (InputStream stream = getResourceAsStream(path)) {
			if (stream == null) {
				throw new IllegalArgumentException("Resource not found : " + path);
			}
			BufferedImage bi = ImageIO.read(stream);
			ImageIcon icon = new ImageIcon(bi);
			return icon;
		} catch (IOException e) {
			this.logger.error("An error occurred retrieving icon : {}", path, e);
			return null;
		}
	}
}