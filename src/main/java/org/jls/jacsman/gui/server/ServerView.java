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

package org.jls.jacsman.gui.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.ApplicationModel;
import org.jls.jacsman.util.PluginManager;
import org.jls.jacsman.util.Protocol;
import org.jls.jacsman.util.ResourceManager;
import org.jls.toolbox.gui.AbstractView;
import org.jls.toolbox.gui.CommandListener;
import org.jls.toolbox.widget.Console;
import org.jls.toolbox.widget.spinner.IncrementalSpinner;
import org.jls.toolbox.widget.spinner.IncrementalSpinnerModel;

import net.miginfocom.swing.MigLayout;

/**
 * Server's panel that starts / stops the associated network server.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ServerView extends AbstractView implements ActionListener {

	private static final long serialVersionUID = 8390257649747876686L;

	public static final Color normalColor = Color.black;
	public static final Color greenColor = Color.green.darker();
	public static final Color redColor = Color.red;
	public static final Color dataColor = new Color(230, 115, 0);

	private final ServerModel model;
	private final Logger logger;
	private final ResourceManager props;

	private Console console;

	// Server control panel
	private JPanel topPanel;
	private JLabel lblIsConnectedLed;
	private JLabel lblActivityLed;
	private JComboBox<Protocol> cbServerType;
	private JComboBox<String> cbPlugin;
	private JLabel lblServerAddress;
	private JTextField tfServerAddress;
	private IncrementalSpinner spServerPort;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnShowPluginView;

	/**
	 * Instanciates a new panel.
	 * 
	 * @param model
	 *            Panel's data model.
	 */
	public ServerView (final ServerModel model) {
		super();
		this.model = model;
		this.logger = LogManager.getLogger();
		this.props = ResourceManager.getInstance();
	}

	@Override
	public void update (Observable o, Object arg) {
		//
	}

	/**
	 * Updates the connection state LED.
	 * 
	 * @param isConnected
	 *            <code>true</code> to show the connected LED,
	 *            <code>false</code> otherwise.
	 */
	public void setConnected (final boolean isConnected) {
		this.lblIsConnectedLed.setIcon(isConnected ? ApplicationModel.LED_GREEN : ApplicationModel.LED_RED);
		this.cbServerType.setEnabled(!isConnected);
		this.cbPlugin.setEnabled(!isConnected);
		this.tfServerAddress.setEnabled(!isConnected);
		this.spServerPort.setEnabled(!isConnected);
	}

	/**
	 * Prints the specified text to the console.
	 * 
	 * @param text
	 *            The text to print.
	 * @param textColor
	 *            Text's color.
	 * @param fontStyle
	 *            Text's font style ({@link Font#BOLD}, {@link Font#ITALIC} or
	 *            {@link Font#PLAIN}.
	 */
	public void print (String text, Color textColor, int fontStyle) {
		this.console.print(text, textColor, fontStyle);
	}

	/**
	 * Notifies an incoming message. This method blinks the activity LED during
	 * a brief moment to indicates that a message has been received.
	 */
	public synchronized void incomingMessage () {
		// Blinks the LED
		new Thread(new Runnable() {

			@Override
			public void run () {
				try {
					getLblActivityLed().setIcon(ApplicationModel.LED_GREEN);
					Thread.sleep(100);
					getLblActivityLed().setIcon(ApplicationModel.LED_GRAY);
				} catch (InterruptedException e) {
					logger.error(e);
				}
			}
		}).start();
	}

	@Override
	protected void createComponents () {
		this.topPanel = createTopPanel();
		this.console = new Console();

		updateGui(this.model);
	}

	@Override
	protected void setStyle () {
		setLayout(new MigLayout(""));
		add(this.topPanel, "growx, pushx, span, grow");
		add(this.console, "span, grow, pushy, w 500lp:600lp, h 200lp:300lp");
	}

	@Override
	protected void addListeners () {
		CommandListener cmdListener = new CommandListener(this);
		this.btnConnect.setActionCommand("connectServer");
		this.btnDisconnect.setActionCommand("disconnectServer");
		this.btnShowPluginView.setActionCommand("showPluginView");
		this.cbPlugin.setActionCommand("selectPlugin");
		this.btnConnect.addActionListener(cmdListener);
		this.btnDisconnect.addActionListener(cmdListener);
		this.btnShowPluginView.addActionListener(cmdListener);
		this.cbPlugin.addActionListener(cmdListener);

		this.cbServerType.addActionListener(this);
		this.cbPlugin.addActionListener(this);
	}

	/**
	 * Updates the GUI using the data model.
	 * 
	 * @param model
	 *            Data model of the GUI.
	 */
	private void updateGui (final ServerModel model) {
		// Address Field visible only in multicast
		if (model.getServerType().equals(Protocol.UDP_MULTICAST)) {
			this.lblServerAddress.setVisible(true);
			this.tfServerAddress.setVisible(true);
		} else {
			this.lblServerAddress.setVisible(false);
			this.tfServerAddress.setVisible(false);
		}
	}

	/**
	 * Creates the top panel of the main panel.
	 * 
	 * @return Control panel.
	 */
	private JPanel createTopPanel () {
		PluginManager.getInstance();
		// Retrieves the plugins
		File[] plugins = PluginManager.listPlugins();
		String[] pluginsNames = new String[plugins.length];
		for (int i = 0; i < pluginsNames.length; i++) {
			pluginsNames[i] = plugins[i].getName();
		}
		int defPort = this.props.getInt("serverView.port.default");

		// Creates the graphical components
		JLabel lblIsConnected = new JLabel(this.props.getString("serverView.label.isConnected"));
		JLabel lblActivity = new JLabel(this.props.getString("serverView.label.activity"));
		JLabel lblServerType = new JLabel(this.props.getString("serverView.label.serverType"));
		JLabel lblPlugin = new JLabel(this.props.getString("serverView.label.plugin"));
		this.lblServerAddress = new JLabel(this.props.getString("serverView.label.serverAddress"));
		JLabel lblServerPort = new JLabel(this.props.getString("serverView.label.serverPort"));
		this.lblIsConnectedLed = new JLabel(ApplicationModel.LED_GRAY);
		this.lblActivityLed = new JLabel(ApplicationModel.LED_GRAY);
		this.cbServerType = new JComboBox<>(Protocol.values());
		this.cbPlugin = new JComboBox<>(pluginsNames);
		this.tfServerAddress = new JTextField(this.props.getString("serverView.address.multicast.default"));
		this.tfServerAddress.setHorizontalAlignment(JTextField.CENTER);
		this.spServerPort = new IncrementalSpinner(new IncrementalSpinnerModel(defPort, 0, 99999, 1));
		this.btnConnect = new JButton(this.props.getString("serverView.button.connect.label"));
		this.btnDisconnect = new JButton(this.props.getString("serverView.button.disconnect.label"));
		this.btnShowPluginView = new JButton(this.props.getString("serverView.button.showPluginView.label"));

		// Creates the panel
		JPanel panel = new JPanel(new MigLayout("", "", "[]15lp[]"));
		panel.add(lblServerType, "split 8, span, left");
		panel.add(this.cbServerType, "");
		panel.add(lblPlugin, "gap left 25lp");
		panel.add(this.cbPlugin, "");
		panel.add(this.lblServerAddress, "gap left 25lp, hidemode 2");
		panel.add(this.tfServerAddress, "width 130lp, hidemode 2");
		panel.add(lblServerPort, "gap left 25lp");
		panel.add(this.spServerPort, "width 75lp, wrap");
		panel.add(lblIsConnected, "split 7, span, left");
		panel.add(this.lblIsConnectedLed, "");
		panel.add(lblActivity, "");
		panel.add(this.lblActivityLed, "");
		panel.add(this.btnConnect, "");
		panel.add(this.btnDisconnect, "");
		panel.add(this.btnShowPluginView, "");
		return panel;
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JComboBox
		 */
		if (e.getSource() instanceof JComboBox) {
			JComboBox<?> box = (JComboBox<?>) e.getSource();

			// Server Type
			if (this.cbServerType.equals(box)) {
				this.model.setServerType((Protocol) box.getSelectedItem());
				updateGui(this.model);
			}
		}
	}

	/**
	 * Returns the activity LED.
	 * 
	 * @return The LED.
	 */
	protected JLabel getLblActivityLed () {
		return this.lblActivityLed;
	}

	/**
	 * Returns the selected plugin.
	 * 
	 * @return Name of the selected plugin.
	 */
	public String getSelectedPlugin () {
		return this.cbPlugin.getSelectedItem().toString();
	}

	/**
	 * Returns the server's address.
	 * 
	 * @return Server's address.
	 */
	public String getServerAddress () {
		return this.tfServerAddress.getText();
	}

	/**
	 * Returns the server's port.
	 * 
	 * @return Server's port.
	 */
	public int getServerPort () {
		return (Integer) this.spServerPort.getValue();
	}
}