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

package net.sourceforge.atunes.gui.dockableView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;

public class DockableFrame extends DockableWindow {

	CloseListener closeListener;

	public DockableFrame(int width, int heigth, Dimension minSize, DockFramePositionListener listener, CloseListener closeListener) {
		super(listener, minSize);
		this.closeListener = closeListener;
		frame = new CustomFrame() {
			private static final long serialVersionUID = 2553989166580280877L;
		};
		((JFrame) frame).setUndecorated(true);
		frame.setSize(width, heigth);
		frame.add(container);
		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		// Avoid window could be resized by desktop system. We'll do this
		((JFrame) frame).setResizable(false);
		addResizableBorders();
		setHidden(false);
	}

	@Override
	protected void addNorthResizableBorder() {
		super.addNorthResizableBorder();
		JLabel icon = new JLabel();
		icon.setIcon(ImageLoader.APP_ICON_TINY);
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (menu != null)
					menu.show(e.getComponent(), e.getX(), e.getY());
			}
		});
		titlePanel.add(icon, BorderLayout.WEST);

		JLabel minimizeButton = new JLabel();
		minimizeButton.setIcon(ImageLoader.MINIMIZE_BUTTON);
		minimizeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				((JFrame) frame).setExtendedState(Frame.ICONIFIED);
				closeListener.minimize();
			}
		});
		JLabel closeButton = new JLabel();
		closeButton.setIcon(ImageLoader.CLOSE_BUTTON);
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				frame.dispose();
				closeListener.close();
			}
		});
		BorderLayout l = new BorderLayout();
		l.setHgap(2);
		JPanel buttons = new JPanel(l);
		buttons.setOpaque(false);
		buttons.add(minimizeButton, BorderLayout.WEST);
		buttons.add(closeButton, BorderLayout.EAST);
		titlePanel.add(buttons, BorderLayout.EAST);
	}

	public int getExtendedState() {
		return ((JFrame) frame).getExtendedState();
	}

	public JFrame getFrame() {
		return (JFrame) frame;
	}

	@Override
	public void setDefaultCloseOperation(int op) {
		((JFrame) frame).setDefaultCloseOperation(op);
	}

	public void setExtendedState(int state) {
		((JFrame) frame).setExtendedState(state);
	}

	@Override
	public void setIcon(Image i) {
		((JFrame) frame).setIconImage(i);
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		((JFrame) frame).setLocationRelativeTo(c);
	}

	public void setSize(Dimension d) {
		((JFrame) frame).setSize(d);
	}

	@Override
	public void setTitle(String s) {
		title.setText(s);
		((JFrame) frame).setTitle(s);
	}

}
