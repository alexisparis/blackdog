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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.Component;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.SongStats;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.SubstanceDefaultTableCellRenderer;

public class PlayListRenderers {

	static Font boldFont = Fonts.PLAY_LIST_FONT.deriveFont(Font.BOLD);

	/**
	 * Add renderers to PlayList
	 * 
	 * @param playlist
	 */
	public static void addRenderers(final PlayListTable playlist) {
		// INTEGER renderer
		playlist.setDefaultRenderer(Integer.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 4027676693367876748L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
				if (playlist.getPlayingSong() == row)
					((JLabel) c).setIcon(playlist.getPlayState() == PlayState.PLAYING ? ImageLoader.PLAY_TINY
							: (playlist.getPlayState() == PlayState.STOPPED ? ImageLoader.STOP_TINY : ImageLoader.PAUSE_TINY));
				else
					((JLabel) c).setIcon(ImageLoader.EMPTY);

				// Get alignment from model
				((JLabel) c).setHorizontalAlignment(((PlayListTableModel) table.getModel()).getColumn(column).getAlignment());
				return c;

			}
		});

		// ImageIcon renderer
		playlist.setDefaultRenderer(ImageIcon.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 62797739155426672L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
				((JLabel) c).setIcon((ImageIcon) value);

				// Get alignment from model
				((JLabel) c).setHorizontalAlignment(((PlayListTableModel) table.getModel()).getColumn(column).getAlignment());
				return c;

			}
		});

		// AUDIOBJECT renderer
		playlist.setDefaultRenderer(AudioObject.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 7305230546936745766L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, ((AudioObject) value).getTitleOrFileName(), isSelected, hasFocus, row, column);
				((JLabel) c).setFont(playlist.getPlayingSong() != row ? Fonts.PLAY_LIST_FONT : boldFont);
				((JLabel) c).setToolTipText(getToolTipForAudioObject((AudioObject) value));

				// Get alignment from model
				((JLabel) c).setHorizontalAlignment(((PlayListTableModel) table.getModel()).getColumn(column).getAlignment());
				return c;
			}
		});

		// STRING renderer
		playlist.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
			private static final long serialVersionUID = 7305230546936745766L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				((JLabel) c).setFont(playlist.getPlayingSong() != row ? Fonts.PLAY_LIST_FONT : boldFont);

				// Get alignment from model
				((JLabel) c).setHorizontalAlignment(((PlayListTableModel) table.getModel()).getColumn(column).getAlignment());
				return c;
			}
		});
	}

	/**
	 * Builds a String to use as Tool Tip for an AudioObject
	 * 
	 * @param audioObject
	 * @return
	 */
	static String getToolTipForAudioObject(AudioObject audioObject) {
		if (audioObject instanceof AudioFile) {
			// Get information
			SongStats stats = RepositoryHandler.getInstance().getSongStatistics((AudioFile) audioObject);

			// Build string
			StringBuilder sb = new StringBuilder();
			sb.append(audioObject.getTitleOrFileName()).append(" - ");
			sb.append(audioObject.getArtist()).append(" - ");

			// If stats is null -> never player
			if (stats == null)
				sb.append(LanguageTool.getString("SONG_NEVER_PLAYED"));
			else {
				sb.append(LanguageTool.getString("LAST_DATE_PLAYED"));
				sb.append(": ");
				sb.append(DateUtils.toString(stats.getLastPlayed()));
				sb.append(" - ");
				sb.append(LanguageTool.getString("TIMES_PLAYED"));
				sb.append(": ");
				sb.append(stats.getTimesPlayed());
			}
			return sb.toString();
		} else {
			return audioObject.getTitleOrFileName();
		}

	}

}
