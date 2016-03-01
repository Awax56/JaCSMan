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
 * Client's panel that starts / stops the associated network client.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ClientView extends AbstractView implements ActionListener {

	private static final long serialVersionUID = 8390257649747876686L;

	public static final Color normalColor = Color.black;
	public static final Color greenColor = Color.green.darker();
	public static final Color redColor = Color.red;

	private final ClientModel model;
	private final Logger logger;

	private Console console;

	// Client control panel
	private JPanel topPanel;
	private JLabel lblIsConnectedLed;
	private JLabel lblActivityLed;
	private JComboBox<Protocol> cbClientType;
	private JComboBox<String> cbPlugin;
	private JTextField tfClientAddr;
	private IncrementalSpinner spClientPort;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnShowPluginView;

	/**
	 * Instanciates a new panel.
	 * 
	 * @param model
	 *            Panel's data model.
	 */
	public ClientView (final ClientModel model) {
		super();
		this.model = model;
		this.logger = LogManager.getLogger();
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
		this.cbClientType.setEnabled(!isConnected);
		this.cbPlugin.setEnabled(!isConnected);
		this.spClientPort.setEnabled(!isConnected);
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
		this.btnConnect.setActionCommand("connectClient");
		this.btnDisconnect.setActionCommand("disconnectClient");
		this.btnShowPluginView.setActionCommand("showPluginView");
		this.cbPlugin.setActionCommand("selectPlugin");
		this.btnConnect.addActionListener(cmdListener);
		this.btnDisconnect.addActionListener(cmdListener);
		this.btnShowPluginView.addActionListener(cmdListener);
		this.cbPlugin.addActionListener(cmdListener);

		this.cbClientType.addActionListener(this);
		this.cbPlugin.addActionListener(this);
	}

	/**
	 * Creates the top panel of the main panel.
	 * 
	 * @return Control panel.
	 */
	private JPanel createTopPanel () {
		ResourceManager p = ResourceManager.getInstance();
		String defAddr = p.getString("clientView.address.default");
		int defPort = p.getInt("clientView.port.default");

		PluginManager.getInstance();
		// Retrieves the plugins
		File[] plugins = PluginManager.listPlugins();
		String[] pluginsNames = new String[plugins.length];
		for (int i = 0; i < pluginsNames.length; i++) {
			pluginsNames[i] = plugins[i].getName();
		}

		// Creates the graphical components
		JLabel lblIsConnected = new JLabel(p.getString("clientView.label.isConnected"));
		JLabel lblActivity = new JLabel(p.getString("clientView.label.activity"));
		JLabel lblClientType = new JLabel(p.getString("clientView.label.clientType"));
		JLabel lblPlugin = new JLabel(p.getString("clientView.label.plugin"));
		JLabel lblClientAddr = new JLabel(p.getString("clientView.label.clientAddress"));
		JLabel lblClientPort = new JLabel(p.getString("clientView.label.clientPort"));
		this.lblIsConnectedLed = new JLabel(ApplicationModel.LED_GRAY);
		this.lblActivityLed = new JLabel(ApplicationModel.LED_GRAY);
		this.cbClientType = new JComboBox<>(Protocol.values());
		this.cbPlugin = new JComboBox<>(pluginsNames);
		this.tfClientAddr = new JTextField(defAddr);
		this.tfClientAddr.setHorizontalAlignment(JTextField.CENTER);
		this.spClientPort = new IncrementalSpinner(new IncrementalSpinnerModel(defPort, 0, 99999, 1));
		this.btnConnect = new JButton(p.getString("clientView.button.connect.label"));
		this.btnDisconnect = new JButton(p.getString("clientView.button.disconnect.label"));
		this.btnShowPluginView = new JButton(p.getString("clientView.button.showPluginView.label"));

		// Creates the panel
		JPanel panel = new JPanel(new MigLayout("", "", "[]15lp[]"));
		panel.add(lblClientType, "split 8, span, left");
		panel.add(this.cbClientType, "");
		panel.add(lblPlugin, "gap left 25lp");
		panel.add(this.cbPlugin, "");
		panel.add(lblClientAddr, "gap left 25lp");
		panel.add(this.tfClientAddr, "width 130lp");
		panel.add(lblClientPort, "gap left 25lp");
		panel.add(this.spClientPort, "width 75lp, wrap");
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

			// Client Type
			if (this.cbClientType.equals(box)) {
				this.model.setClientType((Protocol) box.getSelectedItem());
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
	 * Returns the client's address.
	 * 
	 * @return Client's address.
	 */
	public String getClientAddress () {
		return this.tfClientAddr.getText();
	}

	/**
	 * Returns the client's port.
	 * 
	 * @return Client's port.
	 */
	public int getClientPort () {
		return (Integer) this.spClientPort.getValue();
	}
}