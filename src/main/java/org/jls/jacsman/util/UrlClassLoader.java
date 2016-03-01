package org.jls.jacsman.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class loader extends {@link URLClassLoader} and is used to load classes
 * and resources from a search path of URLs.
 * 
 * @see UrlClassLoader
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class UrlClassLoader extends URLClassLoader {

	/**
	 * Instanciates a new class loader.
	 * 
	 * @param urls
	 *            The list of urls.
	 */
	public UrlClassLoader (final URL[] urls) {
		super(urls);
	}

	@Override
	public void addURL (URL url) {
		super.addURL(url);
	}
}