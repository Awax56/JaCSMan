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

import org.jls.jacsman.plugin.Plugin;
import org.jls.jacsman.util.Protocol;
import org.jls.toolbox.gui.AbstractModel;
import org.jls.toolbox.net.Server;

/**
 * Server's panel data model.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ServerModel extends AbstractModel {

	private Protocol serverType;
	private Server server;
	private Plugin plugin;
	private String serverAddress;

	/**
	 * Instanciates a new default data model.
	 */
	public ServerModel () {
		this.serverType = Protocol.TCP;
		this.server = null;
		this.plugin = null;
		this.serverAddress = null;
	}

	/**
	 * Returns the server's type, i.e. TCP, UDP, etc.
	 * 
	 * @return The server's type.
	 */
	public Protocol getServerType () {
		return serverType;
	}

	/**
	 * Updates the server's type.
	 * 
	 * @param type
	 *            server's type.
	 */
	public void setServerType (final Protocol type) {
		this.serverType = type;
	}

	/**
	 * Returns the network server.
	 * 
	 * @return Network server.
	 */
	public Server getServer () {
		return server;
	}

	/**
	 * Sets the network server.
	 * 
	 * @param server
	 *            Network server.
	 */
	public void setServer (Server server) {
		this.server = server;
	}

	/**
	 * Returns the selected plugin.
	 * 
	 * @return The selected plugin.
	 */
	public Plugin getPlugin () {
		return plugin;
	}

	/**
	 * Updates the plugin.
	 * 
	 * @param plugin
	 *            The plugin.
	 */
	public void setPlugin (final Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Returns the server address.
	 * 
	 * @return Server's address.
	 */
	public String getServerAddress () {
		return this.serverAddress;
	}

	/**
	 * Updates the server address.
	 * 
	 * @param addr
	 *            Server address.
	 */
	public void setServerAddress (String addr) {
		this.serverAddress = addr;
	}
}