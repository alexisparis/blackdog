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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.utils.LanguageTool;

public class InternetPanel extends PreferencesPanel {

	private static final long serialVersionUID = -1872565673079044088L;

	private JRadioButton noProxyRadioButton;
	private JRadioButton httpProxyRadioButton;
	private JRadioButton socksProxyRadioButton;
	private JLabel proxyURLLabel;
	private JTextField proxyURL;
	private JLabel proxyPortLabel;
	private JTextField proxyPort;
	private JLabel proxyUserLabel;
	private JTextField proxyUser;
	private JLabel proxyPasswordLabel;
	private JPasswordField proxyPassword;

	public InternetPanel() {
		super();

		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 10, 10);

		noProxyRadioButton = new JRadioButton(LanguageTool.getString("NO_PROXY"));
		noProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(false);
			}
		});
		httpProxyRadioButton = new JRadioButton(LanguageTool.getString("HTTP_PROXY"));
		httpProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(true);
			}
		});
		socksProxyRadioButton = new JRadioButton(LanguageTool.getString("SOCKS_PROXY"));
		socksProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(true);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(noProxyRadioButton);
		group.add(httpProxyRadioButton);
		group.add(socksProxyRadioButton);

		proxyURLLabel = new JLabel(LanguageTool.getString("HOST"));
		proxyURLLabel.setEnabled(false);
		proxyURL = new JTextField();

		proxyPortLabel = new JLabel(LanguageTool.getString("PORT"));
		proxyPortLabel.setEnabled(false);
		proxyPort = new JTextField();
		proxyPort.setEnabled(false);

		proxyUserLabel = new JLabel(LanguageTool.getString("USER"));
		proxyUserLabel.setEnabled(false);
		proxyUser = new JTextField();
		proxyUser.setEnabled(false);

		proxyPasswordLabel = new JLabel(LanguageTool.getString("PASSWORD"));
		proxyPasswordLabel.setEnabled(false);
		proxyPassword = new JPasswordField();
		proxyPassword.setEnabled(false);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GuiUtils.getComponentOrientation().isLeftToRight() ? GridBagConstraints.WEST : GridBagConstraints.EAST;
		add(noProxyRadioButton, c);
		c.gridy = 1;
		add(httpProxyRadioButton, c);
		c.gridy = 2;
		add(socksProxyRadioButton, c);
		c.gridy = 3;
		c.weightx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(proxyURLLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(proxyURL, c);
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		add(proxyPortLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(proxyPort, c);
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		add(proxyUserLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(proxyUser, c);
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		add(proxyPasswordLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(proxyPassword, c);
	}

	void enableProxySettings(boolean v) {
		proxyURLLabel.setEnabled(v);
		proxyURL.setEnabled(v);
		proxyPortLabel.setEnabled(v);
		proxyPort.setEnabled(v);
		proxyUserLabel.setEnabled(v);
		proxyUser.setEnabled(v);
		proxyPasswordLabel.setEnabled(v);
		proxyPassword.setEnabled(v);
	}

	private ProxyBean getProxy() {
		if (noProxyRadioButton.isSelected())
			return null;

		int port = Integer.parseInt(proxyPort.getText());
		String type;
		if (httpProxyRadioButton.isSelected())
			type = ProxyBean.HTTP_PROXY;
		else
			type = ProxyBean.SOCKS_PROXY;
		ProxyBean proxy = new ProxyBean();
		proxy.setUrl(proxyURL.getText());
		proxy.setPort(port);
		proxy.setUser(proxyUser.getText());
		proxy.setPassword(new String(proxyPassword.getPassword()));
		proxy.setType(type);
		return proxy;
	}

	@Override
	public Map<String, Object> getResult() {
		if (validateOptions()) {
			ProxyBean proxy = getProxy();
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("PROXY", proxy);
			return result;
		}
		return null;
	}

	public void setConfiguration(ProxyBean proxy) {
		enableProxySettings(proxy != null);
		if (proxy == null)
			noProxyRadioButton.setSelected(true);
		else if (proxy.getType().equals(ProxyBean.HTTP_PROXY))
			httpProxyRadioButton.setSelected(true);
		else
			socksProxyRadioButton.setSelected(true);
		proxyURL.setText(proxy != null ? proxy.getUrl() : "");
		proxyPort.setText(proxy != null ? Integer.toString(proxy.getPort()) : "");
		proxyUser.setText(proxy != null ? proxy.getUser() : "");
		proxyPassword.setText(proxy != null ? proxy.getPassword() : "");
	}

	private boolean validateOptions() {
		try {
			if (!noProxyRadioButton.isSelected()) {
				Integer.parseInt(proxyPort.getText());
				return true;
			}
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, LanguageTool.getString("INCORRECT_PORT"));
			return false;
		}
	}

}
