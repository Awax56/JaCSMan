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

import java.awt.Image;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.gui.client.ClientController;
import org.jls.jacsman.gui.client.ClientModel;
import org.jls.jacsman.gui.server.ServerController;
import org.jls.jacsman.gui.server.ServerModel;

/**
 * Application's controller.
 * 
 * @author Julien LE SAUCE
 * @date 22 févr. 2016
 */
public class ApplicationController {

	private final ApplicationModel model;
	private final ApplicationView view;
	private final Logger logger;

	/**
	 * Instanciates a new controller.
	 * 
	 * @param model
	 *            Data model of the application.
	 * @throws Exception
	 *             If an error occurred creating the user interfaces.
	 */
	public ApplicationController (final ApplicationModel model) throws Exception {
		this.logger = LogManager.getLogger();
		this.model = model;
		this.view = new ApplicationView(model, this);
	}

	/**
	 * Shows the Graphical User Interface.
	 */
	public void showGui () {
		this.view.showGui();
	}

	/**
	 * Exits the application and closes whatever needs to be closed.
	 */
	public void exitApplication () {
		this.logger.info("Exiting application");
		Runtime.getRuntime().exit(0);
	}

	/**
	 * Updates the application's icon.
	 * 
	 * @param icon
	 *            Specifies the application's icon.
	 */
	public void setIcon (final Image icon) {
		this.view.setIconImage(icon);
	}

	/**
	 * Updates the Look and Feel used by the application.
	 * 
	 * @param lafName
	 *            Look and Feel's identifier.
	 */
	public void setSkin (final String lafName) {
		// If it's the system's LF
		if (lafName.equals("System")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				SwingUtilities.updateComponentTreeUI(this.view);
				this.logger.debug("Selected Look&Feel {}", lafName);
			} catch (Exception e) {
				this.logger.error("Failed to set java Look&Feel {}", lafName, e);
			}
			// If it's the default LF
		} else if (lafName.equals("Default")) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				SwingUtilities.updateComponentTreeUI(this.view);
				this.logger.debug("Selected Look&Feel {}", lafName);
			} catch (Exception e) {
				this.logger.error("Failed to set java Look&Feel {}", lafName, e);
			}
		}
		// If it's a pre-installed LF
		else {
			for (LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
				if (laf.getName().equals(lafName)) {
					try {
						UIManager.setLookAndFeel(laf.getClassName());
						SwingUtilities.updateComponentTreeUI(this.view);
						this.logger.debug("Selected Look&Feel {}", lafName);
					} catch (Exception e) {
						this.logger.error("Failed to set java Look&Feel {}", lafName, e);
					}
					return;
				}
			}
			throw new IllegalArgumentException("Look&Feel not found : " + lafName);
		}
	}

	/**
	 * Creates a new server.
	 * 
	 * @param id
	 *            Server's identifier.
	 */
	public void createServer (final String id) {
		this.logger.info("Creating new server with ID : {}", id);
		ServerController controller = new ServerController(new ServerModel());
		this.model.getServers().put(id, controller);
		this.model.getTabs().put(id, controller.createGui());
		this.view.addTab(id, this.model.getTabs().get(id));
	}

	/**
	 * Creates a new client.
	 * 
	 * @param id
	 *            Client's identifier.
	 */
	public void createClient (final String id) {
		this.logger.info("Creating new client with ID : {}", id);
		ClientController controller = new ClientController(new ClientModel());
		this.model.getClients().put(id, controller);
		this.model.getTabs().put(id, controller.createGui());
		this.view.addTab(id, this.model.getTabs().get(id));
	}
}