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

package org.jls.jacsman.gui.client;

import java.awt.Color;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.ApplicationView;
import org.jls.jacsman.gui.server.ServerView;
import org.jls.jacsman.plugin.Plugin;
import org.jls.jacsman.plugin.PluginEvent;
import org.jls.jacsman.plugin.PluginInterface;
import org.jls.jacsman.util.PluginManager;
import org.jls.jacsman.util.Protocol;
import org.jls.toolbox.gui.AbstractView;
import org.jls.toolbox.gui.ActionCallback;
import org.jls.toolbox.gui.ActionEvent;
import org.jls.toolbox.net.Client;
import org.jls.toolbox.net.Interface;
import org.jls.toolbox.net.InterfaceEvent;
import org.jls.toolbox.net.InterfaceListener;
import org.jls.toolbox.net.TCPClient;
import org.jls.toolbox.net.UDPClient;
import org.jls.toolbox.net.UDPMulticastClient;
import org.jls.toolbox.util.TimeUtils;
import org.jls.toolbox.widget.dialog.Dialog;

/**
 * Client's panel controller.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ClientController implements InterfaceListener, PluginInterface {

	private final ClientModel model;
	private final Logger logger;
	private ClientView view;

	private Dialog pluginDialog;

	/**
	 * Instanciates the controller.
	 * 
	 * @param model
	 *            Data model.
	 */
	public ClientController (final ClientModel model) {
		this.model = model;
		this.logger = LogManager.getLogger();
		this.view = new ClientView(model);
		this.pluginDialog = null;
	}

	@Override
	public void onReceive (InterfaceEvent event) {
		byte[] msg = event.getMessage();
		if (msg != null && msg.length > 0) {
			Interface com = event.getInterface();
			this.logger.info("Incoming message from {} ({}:{}), size={} bytes", com.getId(), com.getAddress(), com.getPort(),
					msg.length);
			appendConsole("Incoming message from " + com.getId() + " (" + com.getAddress() + ":" + com.getPort() + "), size="
					+ msg.length + " bytes", ServerView.greenColor);

			// Notifies plugin
			PluginEvent pluginEvent = new PluginEvent(this, this.model.getClient().getInterface(), event.getMessage());
			this.model.getPlugin().onReceive(pluginEvent);
		} else {
			this.logger.warn("Receiving empty message from {}", event.getInterface().getId());
			appendConsole("Warning : Receiving empty message from " + event.getInterface().getId(), ServerView.redColor);
		}
	}

	@Override
	public void onTimeout (InterfaceEvent event) {
		this.logger.warn("*** TIMEOUT occurred on {}", event.getInterface().getId());
	}

	@Override
	public void onException (InterfaceEvent event, Throwable t) {
		this.logger.error("An error occurred on interface {}", event.getInterface().getId(), t);
		appendConsole("ERROR : An error occurred on interface " + event.getInterface().getId() + " : " + t.getMessage(),
				ServerView.redColor);
	}

	@Override
	public int send (byte[] msg) throws IOException {
		Client client = this.model.getClient();
		// If the client is instanciated
		if (client != null) {
			this.logger.info("Send message");
			Interface com = client.getInterface();
			appendConsole("Sending message to " + com.getId() + " (" + com.getAddress() + ":" + com.getPort() + "), size="
					+ msg.length + " bytes", ServerView.dataColor);
			client.send(msg);
			return msg.length;
		} else {
			throw new IllegalStateException("No client connected");
		}
	}

	@ActionCallback("connectClient")
	public void action_connectClient (final ActionEvent e) {
		this.logger.debug("Action : {}, From : {}", e.getActionCommand(), e.getView().getClass().getSimpleName());
		try {
			Interface com = new Interface(this.model.getClientType().toString() + "Client", this.view.getClientAddress(),
					this.view.getClientPort());
			startClient(com);
		} catch (Exception e1) {
			this.logger.error("An error occurred starting client", e1);
			appendConsole("An error occurred starting client : " + e1.getMessage(), ClientView.redColor);
			this.view.pop("Client Error", "An error occurred starting client :\n\n" + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@ActionCallback("disconnectClient")
	public void action_disconnectClient (final ActionEvent e) {
		this.logger.debug("Action : {}, From : {}", e.getActionCommand(), e.getView().getClass().getSimpleName());
		try {
			stopClient();
		} catch (Exception e1) {
			this.logger.error("An error occurred stopping client", e1);
			appendConsole("An error occurred stopping client : " + e1.getMessage(), ClientView.redColor);
			this.view.pop("Client Error", "An error occurred stopping client :\n\n" + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@ActionCallback("selectPlugin")
	public void action_selectPlugin (final ActionEvent e) {
		this.logger.debug("Action : {}, From : {}", e.getActionCommand(), e.getView().getClass().getSimpleName());
		try {
			JComboBox<?> box = (JComboBox<?>) e.getSource();
			loadPlugin(box.getSelectedItem().toString());
		} catch (Exception e1) {
			this.logger.error("An error occurred showing plugin view", e1);
			this.view.pop("Server Error", "An error occurred showing plugin view :\n\n" + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@ActionCallback("showPluginView")
	public void action_showPluginView (final ActionEvent e) {
		this.logger.debug("Action : {}, From : {}", e.getActionCommand(), e.getView().getClass().getSimpleName());
		try {
			showPluginView();
		} catch (Exception e1) {
			this.logger.error("An error occurred showing plugin view", e1);
			this.view.pop("Client Error", "An error occurred showing plugin view :\n\n" + e1.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Creates the associated graphical interface.
	 * 
	 * @return The new graphical interface.
	 */
	public AbstractView createGui () {
		this.view = new ClientView(this.model);
		this.view.addListener(this);
		this.view.createGui(null);
		this.model.addObserver(this.view);
		loadPlugin(this.view.getSelectedPlugin());
		return this.view;
	}

	/**
	 * Prints the specified text to the console.
	 * 
	 * @param text
	 *            The text to print.
	 * @param color
	 *            The text's color.
	 */
	public void appendConsole (final String text, final Color color) {
		this.view.print(TimeUtils.getConsoleTimestamp(), Color.blue, Font.BOLD);
		this.view.print(" > ", Color.blue, Font.BOLD);
		this.view.print(text + "\n", color, Font.PLAIN);
	}

	/**
	 * Updates the plugin instance used by the client.
	 * 
	 * @param plugin
	 *            Client's plugin.
	 */
	public void updatePlugin (final Plugin plugin) {
		this.model.setPlugin(plugin);
	}

	/**
	 * Starts the network client.
	 * 
	 * @param com
	 *            Network interface descriptor.
	 * @throws IOException
	 *             If an error occurred starting the client.
	 */
	private void startClient (final Interface com) throws IOException {
		Client client = this.model.getClient();
		// If no client instanciated
		if (client == null) {
			Protocol clientType = this.model.getClientType();
			this.logger.info("Creating client {}", com.getId());
			appendConsole("Creating client " + com.getId(), ClientView.normalColor);
			switch (clientType) {
				case TCP:
					client = new TCPClient(com, null);
					break;
				case UDP:
					client = new UDPClient(com, 2000);
					break;
				case UDP_MULTICAST:
					client = new UDPMulticastClient(com, 2000);
					break;
				default:
					throw new IllegalArgumentException("Unknown client type : " + clientType.toString());
			}
			this.model.setClient(client);
			client.addListener(this);
		}
		this.logger.info("Starting client {} on port {}", com.getId(), com.getPort());
		appendConsole("Starting " + com.getId() + " on port " + com.getPort(), ClientView.normalColor);
		client.start();
		if (client.isRunning()) {
			this.view.setConnected(true);
			appendConsole(com.getId() + " now listening on port " + com.getPort(), ClientView.greenColor);
		} else {
			this.logger.error("An error occurred starting {}", com.getId());
			this.view.pop("Client Error", "An error occurred starting " + com.getId() + " .", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Stops the running network client.
	 * 
	 * @throws IOException
	 *             If an error occurred stopping the network client.
	 */
	private void stopClient () throws IOException {
		Client client = this.model.getClient();
		// If a client has been instanciated
		if (client != null) {
			String clientId = client.getInterface().getId();
			this.logger.info("Stopping client {}", clientId);
			appendConsole("Stopping client " + clientId, ClientView.normalColor);
			client.stop();
			if (client.isRunning()) {
				this.logger.error("An error occurred stopping client {}", clientId);
				this.view.pop("Client Error", "An error occurred stopping client " + clientId + ".",
						JOptionPane.ERROR_MESSAGE);
			} else {
				this.view.setConnected(false);
				this.logger.info("{} now disconnected", clientId);
				appendConsole("Client now disconnected", ClientView.greenColor);
			}
			this.model.setClient(null);
		}
	}

	/**
	 * Shows the plugin associated view.
	 */
	private void showPluginView () {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				Plugin plugin = getModel().getPlugin();
				if (plugin != null) {
					Dialog dialog = getPluginDialog();
					if (dialog == null) {
						dialog = new Dialog(ApplicationView.APP_FRAME, plugin.getName(), plugin.getView());
						dialog.setModal(false);
						setPluginDialog(dialog);
					}
					dialog.showDialog();

					// Locate dialog on the bottom left corner
					int newX = ApplicationView.APP_FRAME.getLocation().x + ApplicationView.APP_FRAME.getSize().width;
					int newY = ApplicationView.APP_FRAME.getLocation().y + ApplicationView.APP_FRAME.getSize().height / 2;
					dialog.setLocation(newX, newY);
				} else {
					throw new IllegalStateException("No plugin instanciated");
				}
			}
		});
	}

	/**
	 * Loads the specified plugin selected by the user.
	 * 
	 * @param pluginName
	 *            The plugin identifier.
	 */
	private void loadPlugin (final String pluginName) {
		if (pluginName == null || pluginName.isEmpty()) {
			throw new IllegalArgumentException("Plugin name cannot be null or empty");
		}
		this.logger.info("Loading plugin : {}", pluginName);
		String pkg = "org.jls.jacsman.plugin";
		try {
			Plugin plugin = PluginManager.getInstance().loadPlugin(pluginName, this);
			this.model.setPlugin(plugin);
		} catch (FileNotFoundException e) {
			this.logger.error("Plugin jar {} not found", pluginName, e);
			this.view.pop("Plugin Error", "Plugin JAR file " + pluginName + " not found :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (MalformedURLException e) {
			this.logger.error("Plugin {} not found", pluginName, e);
			this.view.pop("Plugin Error", "Plugin " + pluginName + " not found :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			this.logger.error("Main plugin class {} not found", pkg + "." + pluginName, e);
			this.view.pop("Plugin Error", "Main class not found in plugin " + pluginName + " :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (InstantiationException e) {
			this.logger.error("Main plugin class instanciation failed", e);
			this.view.pop("Plugin Error", "Main plugin class instanciation failed :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (IllegalAccessException e) {
			this.logger.error("Illegal access to the main plugin class", e);
			this.view.pop("Plugin Error", "Illegal access to the main plugin class :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (NoSuchMethodException e) {
			this.logger.error("Main plugin class constructor not found", e);
			this.view.pop("Plugin Error", "Main plugin class constructor not found :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (SecurityException e) {
			this.logger.error("Security error occurred", e);
			this.view.pop("Plugin Error", "Security error occurred :\n\n" + e.getMessage(), JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) {
			this.logger.error("Main plugin class constructor thrown an exception", e);
			this.view.pop("Plugin Error", "Main plugin class constructor thrown an exception :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		} catch (InvocationTargetException e) {
			this.logger.error("An error occurred loading the plugin", e);
			this.view.pop("Plugin Error", "An error occurred loading the plugin :\n\n" + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public Interface getInterface () {
		return this.model.getClient().getInterface();
	}

	@Override
	public boolean isRunning () {
		return this.model.getClient().isRunning();
	}

	/**
	 * Return the data model.
	 * 
	 * @return The data model.
	 */
	protected ClientModel getModel () {
		return this.model;
	}

	/**
	 * Returns the plugin dialog.
	 * 
	 * @return The plugin's dialog.
	 */
	protected Dialog getPluginDialog () {
		return this.pluginDialog;
	}

	/**
	 * Updates the plugin dialog.
	 * 
	 * @param pluginDialog
	 *            The plugin's dialog.
	 */
	protected void setPluginDialog (Dialog pluginDialog) {
		this.pluginDialog = pluginDialog;
	}
}