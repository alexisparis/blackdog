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

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

import com.sun.jna.examples.WindowUtils;

/**
 * GUI related utility methods
 */
public class GuiUtils {

	private static final Logger logger = new Logger();

	private static Color borderColor = Color.BLACK;
	private static ComponentOrientation componentOrientation;

	public static void addCloseActionWithEscapeKey(final JDialog dialog) {
		//  Handle escape key to close the dialog

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		dialog.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	public static void addCloseActionWithEscapeKey(final JFrame frame) {
		//  Handle escape key to close the dialog

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		};
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	public static void addDisposeActionWithEscapeKey(final JDialog dialog) {
		//  Handle escape key to close the dialog

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action disposeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		dialog.getRootPane().getActionMap().put("ESCAPE", disposeAction);
	}

	public static void addDisposeActionWithEscapeKey(final JFrame frame) {
		//  Handle escape key to close the dialog

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action disposeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		};
		frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		frame.getRootPane().getActionMap().put("ESCAPE", disposeAction);
	}

	/**
	 * Applies Locale specific component orientation to containers
	 * 
	 * @param containers
	 *            One or more containers
	 */
	public static void applyComponentOrientation(Container... containers) {
		if (componentOrientation == null) {
			setComponentOrientation();
		}
		for (Container container : containers) {
			container.applyComponentOrientation(componentOrientation);
		}
	}

	/**
	 * Collapses all nodes in a tree
	 * 
	 * @param A
	 *            tree
	 */
	public static void collapseTree(JTree tree) {
		for (int i = 1; i < tree.getRowCount(); i++) {
			tree.collapseRow(i);
		}
		tree.setSelectionRow(0);
	}

	/**
	 * Expands all nodes in a tree
	 * 
	 * @param tree
	 *            A tree
	 */
	public static void expandTree(JTree tree) {
		for (int i = 1; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		tree.setSelectionRow(0);
	}

	/**
	 * Returns background color for panels, as set by Look And Feel
	 */
	public static Color getBackgroundColor() {
		return (Color) UIManager.get("Panel.background");
	}

	/**
	 * Returns border color for panels, based on background color
	 */
	public static Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Returns the component orientation
	 * 
	 * @return The component orientation
	 */
	public static ComponentOrientation getComponentOrientation() {
		if (componentOrientation == null) {
			setComponentOrientation();
		}
		return componentOrientation;
	}

	/**
	 * Returns the component orientation as a SwingConstant
	 * 
	 * @return The component orientation as a SwingConstant
	 */
	public static int getComponentOrientationAsSwingConstant() {
		if (componentOrientation == null) {
			setComponentOrientation();
		}
		return componentOrientation.isLeftToRight() ? SwingConstants.LEFT : SwingConstants.RIGHT;
	}

	/**
	 * Returns a proportional width according to screenWidth and desiredSize for
	 * the current screen resolution
	 * 
	 * @param screenWidth
	 * @param desiredSize
	 * @return
	 */
	public static int getComponentSizeForResolution(int screenWidth, int desiredSize) {
		int currentScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int result = desiredSize * currentScreenWidth / screenWidth;
		return result;
	}

	public static boolean isTransparentWindowSupported() {
		try {
			return WindowUtils.isWindowAlphaSupported();
		} catch (UnsatisfiedLinkError e) {
			return false;
		} catch (Throwable e) {
			return false;
		}
	}

	/**
	 * @param borderColor
	 *            the borderColor to set
	 */
	public static void setBorderColor(Color borderColor) {
		GuiUtils.borderColor = borderColor;
	}

	private static void setComponentOrientation() {
		if (Kernel.getInstance() == null) {
			componentOrientation = ComponentOrientation.getOrientation(Locale.getDefault());
		} else {
			if ("ug".equalsIgnoreCase(Kernel.getInstance().state.getLocale().getLocale().getLanguage())) {
				componentOrientation = ComponentOrientation.RIGHT_TO_LEFT;
			} else {
				componentOrientation = ComponentOrientation.getOrientation(Kernel.getInstance().state.getLocale().getLocale());
			}

		}
	}

	/**
	 * Sets the default font for all Swing components.
	 * 
	 * @param f
	 */
	public static void setUIFont(FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}

	public static void setWindowMask(Window window, Shape mask) {
		if (mask == null) {
			mask = WindowUtils.MASK_NONE;
		}
		WindowUtils.setWindowMask(window, mask);
	}

	public static void setWindowTransparentIfSupported(Window window, boolean transparent) {
		try {
			if (transparent) {
				System.setProperty("sun.java2d.noddraw", "true");
				if (WindowUtils.isWindowAlphaSupported()) {
					WindowUtils.setWindowAlpha(window, .75f);
				}
			} else {
				if (WindowUtils.isWindowAlphaSupported()) {
					WindowUtils.setWindowAlpha(window, 1f);
				}
				System.setProperty("sun.java2d.noddraw", "false");
			}
		} catch (Throwable e) {
			logger.error(LogCategories.DESKTOP, e);
		}
	}

}
