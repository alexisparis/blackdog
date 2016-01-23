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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

public class InputDialog extends CustomModalDialog {

	private static final long serialVersionUID = -5789081662254435503L;

	private JTextField textField;

	private String result = null;

	public InputDialog(JFrame owner, String title, Image image) {
		super(owner, 400, 130, true);
		setResizable(false);
		if (image != null) {
			setIconImage(image);
		}
		setTitle(title);
		JPanel panel = new JPanel(new GridBagLayout());
		textField = new JTextField();
		JButton okButton = new JButton(LanguageTool.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = textField.getText();
				dispose();
			}
		});
		JButton cancelButton = new JButton(LanguageTool.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				result = null;
				dispose();
			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 20, 10, 20);
		GridLayout gl = new GridLayout(1, 2, 40, 0);
		JPanel auxPanel = new JPanel(gl);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);
		panel.add(textField, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 50, 0, 50);
		panel.add(auxPanel, c);
		setContent(panel);
		enableDisposeActionWithEscapeKey();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		GuiUtils.applyComponentOrientation(this);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		InputDialog id = new InputDialog(null, "JJ", null);
		id.show("sasas");
		System.out.println(id.getResult());
	}

	public String getResult() {
		return result;
	}

	public void show(String text) {
		textField.setText(text);
		textField.setSelectionStart(0);
		textField.setSelectionEnd(text.length());
		setVisible(true);
	}

}
