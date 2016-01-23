/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.state.beans;

import java.beans.ConstructorProperties;

/**
 * Bean for net.sourceforge.atunes.kernel.modules.proxy.Proxy
 */
public class ProxyBean {

	public static final String HTTP_PROXY = "HTTP_PROXY";
	public static final String SOCKS_PROXY = "SOCKS_PROXY";

	private String type;
	private String url;
	private int port;
	private String user;
	private String password;
	private byte[] encryptedPassword;

	public ProxyBean() {
	}

	@ConstructorProperties( { "type", "url", "port", "user", "encryptedPassword" })
	public ProxyBean(String type, String url, int port, String user, byte[] encryptedPassword) {
		this.type = type;
		this.url = url;
		this.port = port;
		this.encryptedPassword = encryptedPassword;
	}

	public byte[] getEncryptedPassword() {
		return encryptedPassword;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public void setEncryptedPassword(byte[] encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
