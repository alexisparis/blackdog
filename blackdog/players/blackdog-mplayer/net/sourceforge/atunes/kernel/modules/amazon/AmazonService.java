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

package net.sourceforge.atunes.kernel.modules.amazon;

import java.awt.Image;
import java.io.IOException;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.utils.NetworkUtils;
import net.sourceforge.atunes.kernel.utils.XMLUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.w3c.dom.Document;

public class AmazonService {

	private static Logger logger = new Logger();

	private static final String SUBSCRIPTION_ID = "06SX9FYP905XDBHBCZR2";
	private static final String ARTIST_WILDCARD = "(%ARTIST%)";
	private static final String ALBUM_WILDCARD = "(%ALBUM%)";
	private String searchURL = StringUtils.getString("http://ecs.amazonaws.com/onca/xml?Service=AWSECommerceService&Operation=ItemSearch&SubscriptionId=", SUBSCRIPTION_ID,
			"&SearchIndex=Music&Artist=", ARTIST_WILDCARD, "&Title=", ALBUM_WILDCARD, "&ResponseGroup=Images,Tracks");

	private Proxy proxy;

	public AmazonService(Proxy proxy) {
		this.proxy = proxy;
	}

	public static AmazonAlbum getAlbum(String artist, String album) {
		ProxyBean p = Kernel.getInstance().state.getProxy();
		AmazonService service = null;
		if (p == null)
			service = new AmazonService(null);
		else {
			try {
				Proxy proxy;
				proxy = new Proxy(p.getType().equals(ProxyBean.HTTP_PROXY) ? Proxy.Type.HTTP : Proxy.Type.SOCKS, p.getUrl(), p.getPort(), p.getUser(), p.getPassword());
				service = new AmazonService(proxy);
			} catch (Exception e1) {
				logger.internalError(e1);
			}
		}
		return service != null ? service.getAmazonAlbum(artist, album) : null;
	}

	public static Image getAmazonImage(String url) {
		ProxyBean p = Kernel.getInstance().state.getProxy();
		AmazonService service = null;
		if (p == null)
			service = new AmazonService(null);
		else {
			try {
				Proxy proxy;
				proxy = new Proxy(p.getType().equals(ProxyBean.HTTP_PROXY) ? Proxy.Type.HTTP : Proxy.Type.SOCKS, p.getUrl(), p.getPort(), p.getUser(), p.getPassword());
				service = new AmazonService(proxy);
			} catch (Exception e1) {
				logger.internalError(e1);
			}
		}
		return service != null ? service.getImage(url) : null;
	}

	private AmazonAlbum getAmazonAlbum(String artist, String album) {
		try {
			// build url
			String urlString = searchURL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(ALBUM_WILDCARD, NetworkUtils.encodeString(album));
			// read xml
			Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(NetworkUtils.getConnection(urlString, proxy)));
			return AmazonAlbum.getAlbum(xml);
		} catch (Exception e) {
			logger.internalError(e);
		}
		return null;
	}

	private Image getImage(String url) {
		try {
			return NetworkUtils.getImage(NetworkUtils.getConnection(url, proxy));
		} catch (IOException e) {
			return null;
		}
	}
}
