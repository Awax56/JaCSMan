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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.jacsman.util.ResourceManager;

/**
 * Main frame of the application.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ApplicationView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 5624587162291736726L;

	public static ApplicationView APP_FRAME = null;

	private final ApplicationController controller;
	private final Logger logger;
	private final ResourceManager props;

	private final HashMap<String, JMenu> menus;
	private final HashMap<String, JMenuItem> menuItems;
	private JMenuBar menuBar;
	private JTabbedPane tabbedPane;

	/**
	 * Instanciates the main frame of the application.
	 * 
	 * @param model
	 *            Data model.
	 * @param controller
	 *            Frame's controller.
	 */
	public ApplicationView (final ApplicationModel model, final ApplicationController controller) {
		super(model.getAppName());
		ApplicationView.APP_FRAME = this;
		this.logger = LogManager.getLogger();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.controller = controller;
		this.props = ResourceManager.getInstance();
		this.menus = new HashMap<>();
		this.menuItems = new HashMap<>();
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Shows the Graphical User Interface.
	 */
	public void showGui () {
		pack();
		setSize(new Dimension(800, 650));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Adds a new tab to the main frame.
	 * 
	 * @param title
	 *            Tab's title.
	 * @param panel
	 *            The panel associated with the new tab.
	 */
	public void addTab (final String title, final JPanel panel) {
		this.tabbedPane.add(title, panel);
	}

	/**
	 * Creates the components of the GUI.
	 */
	private void createComponents () {
		createMenus();
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	/**
	 * Creates the frame menus.
	 */
	private void createMenus () {
		this.menuBar = new JMenuBar();
		// Creates the menus
		this.menus.put("file", new JMenu(this.props.getString("mainView.menu.file.label")));
		this.menus.put("server", new JMenu(this.props.getString("mainView.menu.server.label")));
		this.menus.put("client", new JMenu(this.props.getString("mainView.menu.client.label")));
		this.menus.put("help", new JMenu(this.props.getString("mainView.menu.help.label")));

		// Creates the items
		this.menuItems.put("file.exit", new JMenuItem(this.props.getString("mainView.menu.item.exit.label")));
		this.menuItems.put("server.createServer",
				new JMenuItem(this.props.getString("mainView.menu.item.createServer.label")));
		this.menuItems.put("client.createClient",
				new JMenuItem(this.props.getString("mainView.menu.item.createClient.label")));
		this.menuItems.put("help.about", new JMenuItem(this.props.getString("mainView.menu.item.about.label")));

		// Adds the items
		this.menus.get("file").add(this.menuItems.get("file.exit"));
		this.menus.get("server").add(this.menuItems.get("server.createServer"));
		this.menus.get("client").add(this.menuItems.get("client.createClient"));
		this.menus.get("help").add(this.menuItems.get("help.about"));

		// Adds the menus
		this.menuBar.add(this.menus.get("file"));
		this.menuBar.add(this.menus.get("server"));
		this.menuBar.add(this.menus.get("client"));
		this.menuBar.add(this.menus.get("help"));
		setJMenuBar(this.menuBar);
	}

	/**
	 * Adds the components to the main frame panel.
	 */
	private void createGui () {
		setContentPane(this.tabbedPane);
	}

	/**
	 * Adds the listeners on the graphical components.
	 */
	private void addListeners () {
		this.menuItems.get("file.exit").addActionListener(this);
		this.menuItems.get("server.createServer").addActionListener(this);
		this.menuItems.get("client.createClient").addActionListener(this);
		this.menuItems.get("help.about").addActionListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JMenuItem
		 */
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem item = (JMenuItem) e.getSource();

			// Exit application
			if (this.menuItems.get("file.exit").equals(item)) {
				this.logger.debug("Exit application");
				this.controller.exitApplication();
			}
			// Create Server
			else if (this.menuItems.get("server.createServer").equals(item)) {
				this.controller.createServer("Server");
			}
			// Create Client
			else if (this.menuItems.get("client.createClient").equals(item)) {
				this.controller.createClient("Client");
			}
			// About application
			else if (this.menuItems.get("help.about").equals(item)) {
				this.logger.debug("Show About panel");
			}
		}
	}
}