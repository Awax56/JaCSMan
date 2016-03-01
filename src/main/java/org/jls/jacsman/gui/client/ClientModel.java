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

import org.jls.jacsman.plugin.Plugin;
import org.jls.jacsman.util.Protocol;
import org.jls.toolbox.gui.AbstractModel;
import org.jls.toolbox.net.Client;

/**
 * Client's panel data model.
 * 
 * @author Julien LE SAUCE
 * @date 1 mars 2016
 */
public class ClientModel extends AbstractModel {

	private Protocol clientType;
	private Client client;
	private Plugin plugin;

	/**
	 * Instanciates a new default data model.
	 */
	public ClientModel () {
		this.clientType = Protocol.TCP;
		this.client = null;
		this.plugin = null;
	}

	/**
	 * Returns the client's type, i.e. TCP, UDP, etc.
	 * 
	 * @return The client's type.
	 */
	public Protocol getClientType () {
		return clientType;
	}

	/**
	 * Updates the client's type.
	 * 
	 * @param type
	 *            Client's type.
	 */
	public void setClientType (final Protocol type) {
		this.clientType = type;
	}

	/**
	 * Returns the network client.
	 * 
	 * @return Network client.
	 */
	public Client getClient () {
		return this.client;
	}

	/**
	 * Sets the network client.
	 * 
	 * @param client
	 *            Network client.
	 */
	public void setClient (Client client) {
		this.client = client;
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
}