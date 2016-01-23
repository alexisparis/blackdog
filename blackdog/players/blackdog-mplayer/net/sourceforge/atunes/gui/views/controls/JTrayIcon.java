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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/*
 * This class is based on Java6Tray class from TVBrowser project
 * (http://sourceforge.net/projects/tvbrowser/)
 */
/**
 * Tray icon with a Swing popup menu
 */
public class JTrayIcon extends TrayIcon {

	/**
	 * This special JPopupMenu prevents the user from removing the popup menu
	 * listener
	 */
	public class JTrayIconPopupMenu extends JPopupMenu {

		private static final long serialVersionUID = -220434547680783992L;

		@Override
		public void removePopupMenuListener(PopupMenuListener l) {
			if (l == popupMenuListener) {
				return;
			}
			super.removePopupMenuListener(l);
		}
	}

	private JDialog trayParent;
	private JPopupMenu popupMenu;

	private MouseListener trayIconMouseListener;
	private PopupMenuListener popupMenuListener;

	public JTrayIcon(Image image) {
		super(image);
		init();
	}

	/**
	 * Compute the proper position for a popup
	 */
	private Point computeDisplayPoint(int x, int y, Dimension dim) {
		if (x - dim.width > 0)
			x -= dim.width;
		if (y - dim.height > 0)
			y -= dim.height;
		return new Point(x, y);
	}

	private void init() {
		trayParent = new JDialog();
		trayParent.setSize(0, 0);
		trayParent.setUndecorated(true);
		trayParent.setAlwaysOnTop(true);
		trayParent.setVisible(false);
	}

	@Override
	public synchronized void removeMouseListener(MouseListener listener) {
		if (listener == trayIconMouseListener) {
			return;
		}
		super.removeMouseListener(listener);
	}

	public void setJTrayIconJPopupMenu(JTrayIconPopupMenu popup) {
		popupMenu = popup;
		popupMenuListener = new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				trayParent.setVisible(false);
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		};
		popupMenu.addPopupMenuListener(popupMenuListener);
		setTrayIconMouseListener();
	}

	@Override
	public void setPopupMenu(PopupMenu popup) {
		throw new UnsupportedOperationException("use setJTrayIconPopupMenu(JTrayIconPopupMenu popup) instead");
	}

	private void setTrayIconMouseListener() {
		if (trayIconMouseListener == null) {
			trayIconMouseListener = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						showPopup(e.getPoint());
						trayParent.setVisible(false);
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger()) {
						if (trayParent.isVisible()) {
							trayParent.setVisible(false);
						} else {
							showPopup(e.getPoint());
						}

					}
				}
			};
			addMouseListener(trayIconMouseListener);
		}
	}

	private void showPopup(final Point p) {
		trayParent.setVisible(true);
		trayParent.toFront();
		Point p2 = computeDisplayPoint(p.x, p.y, popupMenu.getPreferredSize());
		popupMenu.show(trayParent, p2.x - trayParent.getLocation().x, p2.y - trayParent.getLocation().y);
	}

}
