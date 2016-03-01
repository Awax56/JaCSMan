package org.jls.jacsman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.plugin.Plugin;
import org.jls.jacsman.plugin.PluginInterface;
import org.jls.toolbox.util.file.FileFilter;

/**
 * Singleton managing the plugins of the application.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class PluginManager {

	private static final String slash = File.separator;

	public static final String USER_DIR = System.getProperty("user.dir");
	private static final String PLUGINS_PATH = USER_DIR + slash + "plugins";
	private static final String PLUGIN_PACKAGE = "org.jls.jacsman.plugin";

	private static PluginManager UNIQUE_INSTANCE = null;

	private final Logger logger;

	/**
	 * Instanciates the plugin manager.
	 */
	private PluginManager () {
		this.logger = LogManager.getLogger();
	}

	/**
	 * Returns the unique instance of this class.
	 * 
	 * @return Unique instance of this class.
	 */
	public static final synchronized PluginManager getInstance () {
		if (UNIQUE_INSTANCE == null) {
			UNIQUE_INSTANCE = new PluginManager();
		}
		return UNIQUE_INSTANCE;
	}

	/**
	 * Returns the list of the plugins detected in the
	 * {@link PluginManager#PLUGINS_PATH plugin path}.
	 * 
	 * @return List of the detected plugins.
	 */
	public static File[] listPlugins () {
		File dir = new File(PLUGINS_PATH);
		return dir.listFiles(new FileFilter(FileFilter.ONLY_FILES, "jar"));
	}

	/**
	 * Loads the specified plugin from the plugin path specified by
	 * {@link PluginManager#PLUGINS_PATH}.
	 * 
	 * @param jarName
	 *            The jar name.
	 * @param pluginInt
	 *            Interface between the plugin and the application.
	 * @return The instance of the loaded plugin.
	 * @throws FileNotFoundException
	 *             If the specified jar is not found.
	 * @throws MalformedURLException
	 *             If specified jar does not exist.
	 * @throws ClassNotFoundException
	 *             If the main plugin class is not found.
	 * @throws IllegalAccessException
	 *             If the constructor of the main plugin class is not
	 *             accessible.
	 * @throws InstantiationException
	 *             If the main plugin class is not instanciable for a reason.
	 * @throws SecurityException
	 *             If a problem is detected at the {@link SecurityManager}
	 *             level.
	 * @throws NoSuchMethodException
	 *             If the main plugin class constructor is not found.
	 * @throws InvocationTargetException
	 *             If the main plugin class constructor throw an exception.
	 * @throws IllegalArgumentException
	 *             If the number of parameters specified to the main plugin
	 *             class constructor is invalid.
	 */
	@SuppressWarnings("resource")
	public Plugin loadPlugin (final String jarName, final PluginInterface pluginInt) throws MalformedURLException,
			ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException,
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		if (jarName == null || jarName.isEmpty()) {
			throw new IllegalArgumentException("Jar name cannot be null or empty");
		}
		if (pluginInt == null) {
			throw new NullPointerException("A plugin interface must be set");
		}

		File myJar = new File(PLUGINS_PATH + slash + jarName);
		String className = jarName.substring(0, jarName.lastIndexOf("."));
		if (myJar.exists()) {
			this.logger.info("Loading plugin : {}", myJar.getPath());
			URL jarPath = myJar.toURI().toURL();
			URL[] urls = new URL[] {jarPath};
			UrlClassLoader loader = new UrlClassLoader(urls);

			// Retrieves the class
			Class<?> pluginClass = Class.forName(PLUGIN_PACKAGE + "." + className, true, loader);
			Class<? extends Plugin> clazz = pluginClass.asSubclass(Plugin.class);
			Constructor<?> plugin = clazz.getConstructor(PluginInterface.class);
			return (Plugin) plugin.newInstance(pluginInt);
		} else {
			throw new FileNotFoundException("Plugin jar not found : " + myJar.getPath());
		}
	}
}