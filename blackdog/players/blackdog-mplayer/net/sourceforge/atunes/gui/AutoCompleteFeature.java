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

package net.sourceforge.atunes.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

public class AutoCompleteFeature extends PlainDocument {

	private static final long serialVersionUID = 7897016854316129843L;

	JComboBox comboBox;
	ComboBoxModel model;
	JTextComponent editor;
	boolean hitBackspace = false;
	boolean hitBackspaceOnSelection;
	boolean listContainsSelectedItem;

	public AutoCompleteFeature(final JComboBox comboBox) {
		this.comboBox = comboBox;
		model = comboBox.getModel();
		editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
		editor.setDocument(this);
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightCompletedText(0);
			}
		});
		editor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (comboBox.isDisplayable())
					comboBox.setPopupVisible(true);
				hitBackspace = false;
				switch (e.getKeyCode()) {
				// determine if the pressed key is backspace (needed by the remove method)
				case KeyEvent.VK_BACK_SPACE:
					hitBackspace = true;
					hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
					break;
				}
			}
		});
		// Highlight whole text when gaining focus
		editor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				highlightCompletedText(0);
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});
		// Handle initially selected object
		Object selected = comboBox.getSelectedItem();
		if (selected != null)
			setText(selected.toString());
		highlightCompletedText(0);
	}

	void highlightCompletedText(int start) {
		editor.setCaretPosition(editor.getDocument().getLength());
		editor.moveCaretPosition(start);
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		// insert the string into the document
		super.insertString(offs, str, a);
		// lookup and select a matching item
		Object item = lookupItem(getText(0, getLength()));

		listContainsSelectedItem = true;
		if (item == null) {
			// no item matches => use the current input as selected item
			item = getText(0, getLength());
			listContainsSelectedItem = false;
		}
		setSelectedItem(item);
		setText(item.toString());
		// select the completed part
		if (listContainsSelectedItem)
			highlightCompletedText(offs + str.length());
		if (comboBox.isPopupVisible()) {
			comboBox.setPopupVisible(false);
			comboBox.setPopupVisible(true);
		}
	}

	private Object lookupItem(String pattern) {
		Object selectedItem = model.getSelectedItem();
		// only search for a different item if the currently selected does not match
		if (selectedItem != null && selectedItem.toString().toLowerCase().startsWith(pattern.toLowerCase()) && listContainsSelectedItem) {
			return selectedItem;
		}
		// iterate over all items
		for (int i = 0, n = model.getSize(); i < n; i++) {
			Object currentItem = model.getElementAt(i);
			// current item starts with the pattern?
			if (currentItem.toString().toLowerCase().startsWith(pattern.toLowerCase())) {
				return currentItem;
			}
		}
		// no item starts with the pattern => return null
		return null;
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
		if (hitBackspace) {
			if (listContainsSelectedItem) {
				// move the selection backwards
				// old item keeps being selected
				if (offs > 0) {
					if (hitBackspaceOnSelection)
						offs--;
				} else {
					// User hit backspace with the cursor positioned on the start => beep
					comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
				}
				highlightCompletedText(offs);
				return;
			}
			super.remove(offs, len);
			String currentText = getText(0, getLength());
			// lookup if a matching item exists
			Object item = lookupItem(currentText);
			if (item != null) {
				setSelectedItem(item);
				listContainsSelectedItem = true;
			} else {
				// no item matches => use the current input as selected item
				item = currentText;
				setSelectedItem(item);
				listContainsSelectedItem = false;
			}
			// display the completed string
			String itemString = item.toString();
			setText(itemString);
			if (listContainsSelectedItem)
				highlightCompletedText(offs);
		} else {
			super.remove(offs, len);
			setSelectedItem(getText(0, getLength()));
			listContainsSelectedItem = false;
		}
	}

	private void setSelectedItem(Object item) {
		model.setSelectedItem(item);
	}

	private void setText(String text) {
		try {
			// remove all text and insert the completed string
			int caretPosition = editor.getCaretPosition();
			super.remove(0, getLength());
			super.insertString(0, text, null);
			editor.setCaretPosition(caretPosition);
		} catch (BadLocationException e) {
			throw new RuntimeException(e.toString());
		}
	}
}
