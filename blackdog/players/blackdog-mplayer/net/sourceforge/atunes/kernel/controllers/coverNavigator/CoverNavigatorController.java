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

package net.sourceforge.atunes.kernel.controllers.coverNavigator;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorFrame;
import net.sourceforge.atunes.kernel.controllers.model.FrameController;
import net.sourceforge.atunes.kernel.executors.BackgroundExecutor;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;

public class CoverNavigatorController extends FrameController<CoverNavigatorFrame> {

	private class IntermediateResult {
		private Album album;
		private ImageIcon cover;
		private GridBagConstraints gridBagConstraints;

		public IntermediateResult(Album album, ImageIcon cover, GridBagConstraints gridBagConstraints) {
			this.album = album;
			this.cover = cover;
			this.gridBagConstraints = gridBagConstraints;
		}

		public Album getAlbum() {
			return album;
		}

		public ImageIcon getCover() {
			return cover;
		}

		public GridBagConstraints getGridBagConstraints() {
			return gridBagConstraints;
		}
	}

	public CoverNavigatorController(CoverNavigatorFrame frame) {
		super(frame);
		addBindings();
	}

	@Override
	protected void addBindings() {
		final CoverNavigatorFrame frame = frameControlled;
		frame.getList().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!((JList) e.getSource()).getValueIsAdjusting())
					updateCovers();
			}

		});
		final CoverNavigatorController coverNavigatorController = this;
		frame.getCoversButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Artist selectedArtist = (Artist) frame.getList().getSelectedValue();
				if (selectedArtist != null)
					BackgroundExecutor.getCoversFromAmazon(selectedArtist, frameControlled, coverNavigatorController);
			}
		});
	}

	@Override
	protected void addStateBindings() {
	}

	private JPanel getPanelForAlbum(Album album, ImageIcon cover, int coversSize) {
		JLabel label = new JLabel(album.getName(), cover, SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(140, 140));
		label.setLocation(0, 0);
		label.setVerticalTextPosition(SwingConstants.BOTTOM);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		label.setToolTipText(album.getName());
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(coversSize, coversSize));
		panel.add(label);
		GuiUtils.applyComponentOrientation(panel);
		return panel;
	}

	@Override
	protected void notifyReload() {
	}

	public void updateCovers() {
		final Artist artistSelected = (Artist) frameControlled.getList().getSelectedValue();
		if (artistSelected == null)
			return;

		frameControlled.getCoversPanel().removeAll();

		frameControlled.getList().setEnabled(false);
		frameControlled.getCoversButton().setEnabled(false);
		frameControlled.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		SwingWorker<Void, IntermediateResult> generateAndShowAlbumPanels = new SwingWorker<Void, IntermediateResult>() {
			@Override
			protected Void doInBackground() throws Exception {

				final List<Album> albums = new ArrayList<Album>(artistSelected.getAlbums().values());
				Collections.sort(albums);

				int colNumber = frameControlled.getCoversScrollPaneWidth() / frameControlled.getCoversSize();

				int coversOfLastRow = albums.size() % colNumber;
				if (coversOfLastRow == 0)
					coversOfLastRow = colNumber;
				int coversAdded = 0;
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 0;
				for (Album album : albums) {
					if (coversAdded == albums.size() - coversOfLastRow) {
						c.weighty = 1;
						c.fill = GridBagConstraints.BOTH;
					}
					if (albums.size() < colNumber && coversAdded == albums.size() - 1) {
						c.weightx = 1;
						c.fill = GridBagConstraints.NONE;
						c.anchor = GridBagConstraints.NORTHWEST;
					}
					ImageIcon cover = album.getPicture(120, 120);
					publish(new IntermediateResult(album, cover, (GridBagConstraints) c.clone()));
					coversAdded++;
					c.gridx++;
					if (c.gridx == colNumber) {
						c.gridx = 0;
						c.gridy++;
					}
				}
				return null;
			}

			@Override
			protected void done() {
				frameControlled.setCursor(Cursor.getDefaultCursor());
				frameControlled.getList().setEnabled(true);
				frameControlled.getCoversButton().setEnabled(true);
			}

			@Override
			protected void process(List<IntermediateResult> intermediateResults) {
				for (IntermediateResult intermediateResult : intermediateResults) {
					frameControlled.getCoversPanel().add(getPanelForAlbum(intermediateResult.getAlbum(), intermediateResult.getCover(), frameControlled.getCoversSize()),
							intermediateResult.getGridBagConstraints());
					frameControlled.getCoversPanel().revalidate();
					frameControlled.getCoversPanel().repaint();
				}
			}
		};
		generateAndShowAlbumPanels.execute();
	}

}
