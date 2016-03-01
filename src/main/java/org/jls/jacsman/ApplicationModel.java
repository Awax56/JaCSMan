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

import java.util.HashMap;

import javax.swing.ImageIcon;

import org.jls.jacsman.gui.client.ClientController;
import org.jls.jacsman.gui.server.ServerController;
import org.jls.jacsman.util.ResourceManager;
import org.jls.toolbox.gui.AbstractModel;
import org.jls.toolbox.gui.AbstractView;

/**
 * Application's data model.
 * 
 * @author Julien LE SAUCE
 * @date 22 févr. 2016
 */
public class ApplicationModel extends AbstractModel {

	public static final ImageIcon LED_GRAY = ResourceManager.getInstance().getIcon("icon.led.gray");
	public static final ImageIcon LED_RED = ResourceManager.getInstance().getIcon("icon.led.red");
	public static final ImageIcon LED_GREEN = ResourceManager.getInstance().getIcon("icon.led.green");
	public static final ImageIcon LED_ORANGE = ResourceManager.getInstance().getIcon("icon.led.orange");

	private final ResourceManager mod;
	private final String appName;
	private final HashMap<String, AbstractView> tabs;
	private final HashMap<String, ServerController> servers;
	private final HashMap<String, ClientController> clients;

	/**
	 * Instanciates a new default data model.
	 */
	public ApplicationModel () {
		this.mod = ResourceManager.getInstance();
		this.appName = this.mod.getString("name");
		this.tabs = new HashMap<>();
		this.servers = new HashMap<>();
		this.clients = new HashMap<>();
	}

	/**
	 * Returns the application's name.
	 * 
	 * @return Application's name.
	 */
	public String getAppName () {
		return this.appName;
	}

	public HashMap<String, AbstractView> getTabs () {
		return this.tabs;
	}

	public HashMap<String, ServerController> getServers () {
		return this.servers;
	}

	public HashMap<String, ClientController> getClients () {
		return this.clients;
	}
}