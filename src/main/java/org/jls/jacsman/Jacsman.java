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

package org.jls.jacsman;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.util.ResourceManager;
import org.jls.toolbox.widget.ErrorPopUp;

/**
 * Main class of the JaCSMan application.
 * 
 * @author Julien LE SAUCE
 * @date 22 févr. 2016
 */
public class Jacsman {

	/**
	 * Launches the application and shows the Graphical User Interface.
	 * 
	 * @param args
	 *            No arguments.
	 */
	public static void main (final String[] args) {
		/*
		 * Configures the logger
		 */
		String log4jKey = "log4j.configurationFile";
		// If no configuration is configured
		if (System.getProperty(log4jKey) == null) {
			// Load the default configuration
			URL url = Thread.currentThread().getContextClassLoader().getResource(ResourceManager.LOG4J_FILE);
			System.setProperty(log4jKey, url.getFile());
		}
		final Logger logger = LogManager.getLogger();
		logger.info("log4j configuration file set : {}", System.getProperty(log4jKey));

		/*
		 * System Info
		 */
		final ResourceManager p = ResourceManager.getInstance();
		String head = "";
		String vmArgs = getVMArguments("\t#  \t", "\n");
		String mainArgs = formatMainArguments("\t#  \t", "\n", args);
		head += "\n\n\t#####################################\n";
		head += "\t#  Launching " + p.getString("name") + "\n";
		head += "\t#  OS Name : " + System.getProperty("os.name") + "\n";
		head += "\t#  Architecture : " + System.getProperty("os.arch") + "\n";
		head += "\t#  Processor : " + System.getProperty("sun.arch.data.model") + " bits\n";
		head += "\t#  Version : " + System.getProperty("os.version") + "\n";
		head += "\t#  " + "\n";
		head += "\t#  VM Arguments :" + "\n";
		head += vmArgs;
		head += "\t#  Main Arguments :" + "\n";
		head += mainArgs;
		head += "\t#####################################\n";
		logger.info(head);

		/*
		 * Starts GUI
		 */
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				try {
					final ApplicationModel model = new ApplicationModel();
					final ApplicationController controller = new ApplicationController(model);
					controller.setSkin("Nimbus");
					controller.setIcon(p.getIcon("icon").getImage());
					controller.showGui();
				} catch (Exception e) {
					Throwable t = e;
					while (t.getCause() != null) {
						t = t.getCause();
					}
					logger.fatal("An error occurred during application launching", e);
					ErrorPopUp.showExceptionDialog(null, t, e.getMessage());
				}
			}
		});
	}

	/**
	 * Returns the list of the VM arguments.
	 * 
	 * @param prefix
	 *            Prefix to prints to the console before printing the argument.
	 * @param suffix
	 *            Suffix to prints to the console before printing the argument.
	 * @return String containing the arguments of the Virtual Machine.
	 */
	private static String getVMArguments (String prefix, String suffix) {
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> vmArgs = runtimeMxBean.getInputArguments();
		if (vmArgs.size() > 0) {
			String args = "";
			for (String s : vmArgs) {
				args += prefix + s + suffix;
			}
			return args;
		}
		return prefix + "<none>" + suffix;
	}

	/**
	 * Returns the list of the application arguments.
	 * 
	 * @param prefix
	 *            Prefix to prints to the console before printing the argument.
	 * @param suffix
	 *            Suffix to prints to the console before printing the argument.
	 * @param args
	 *            Arguments specified to the application.
	 * @return String containing the arguments of the application.
	 */
	private static String formatMainArguments (String prefix, String suffix, String[] args) {
		if (args.length > 0) {
			String str = "";
			for (String s : args) {
				str += prefix + s + suffix;
			}
			return str;
		}
		return prefix + "<none>" + suffix;
	}
}