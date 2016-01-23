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

package net.sourceforge.atunes.gui.views;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.handlers.FavoritesHandler;
import net.sourceforge.atunes.kernel.handlers.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerAlbum;
import net.sourceforge.atunes.kernel.modules.audioscrobbler.AudioScrobblerArtist;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.Genre;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.SubstanceDefaultListCellRenderer;
import org.jvnet.substance.SubstanceDefaultTableCellRenderer;
import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;

public class Renderers {

	public static final TreeCellRenderer NAVIGATION_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = -7992021225213275134L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			final Object content = node.getUserObject();

			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (content instanceof Artist) {
				if (!Kernel.getInstance().state.isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsValue(content))
					label.setIcon(ImageLoader.ARTIST);
				else
					label.setIcon(ImageLoader.ARTIST_FAVORITE);
				label.setToolTipText(getToolTipForArtist((Artist) content));
			} else if (content instanceof Album) {
				if (!Kernel.getInstance().state.isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsValue(content))
					label.setIcon(ImageLoader.ALBUM);
				else
					label.setIcon(ImageLoader.ALBUM_FAVORITE);
				if (!Kernel.getInstance().state.isShowAlbumTooltip())
					label.setToolTipText(getToolTipForAlbum((Album) content));
			} else if (content instanceof Genre) {
				label.setIcon(ImageLoader.GENRE);
				label.setToolTipText(getToolTipForGenre((Genre) content));
			} else {
				label.setIcon(ImageLoader.FOLDER);
				label.setToolTipText(getToolTipForRepository());
			}

			if (value.toString() != null)
				if (value.toString().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || value.toString().equals(LanguageTool.getString("UNKNOWN_ALBUM"))
						|| value.toString().equals(LanguageTool.getString("UNKNOWN_GENRE")))
					label.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
			return label;
		}
	};

	public static final TreeCellRenderer FILE_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = 610272558504958085L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			icon.setIcon(ImageLoader.FOLDER);

			if (value.toString() != null)
				if (value.toString().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || value.toString().equals(LanguageTool.getString("UNKNOWN_ALBUM")))
					icon.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);

			return icon;
		}
	};

	public static final TreeCellRenderer FAVORITE_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = 2880969518313022116L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object content = node.getUserObject();

			JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (content instanceof Artist)
				icon.setIcon(ImageLoader.ARTIST);
			else if (content instanceof Album)
				icon.setIcon(ImageLoader.ALBUM);
			else
				icon.setIcon(ImageLoader.FAVORITE);

			if (value.toString() != null)
				if (value.toString().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || value.toString().equals(LanguageTool.getString("UNKNOWN_ALBUM")))
					icon.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);

			return icon;
		}
	};

	public static final TreeCellRenderer DEVICE_BY_TAG_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = -2376222352360663884L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object content = node.getUserObject();

			JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (content instanceof Artist)
				icon.setIcon(ImageLoader.ARTIST);
			else if (content instanceof Album)
				icon.setIcon(ImageLoader.ALBUM);
			else
				icon.setIcon(ImageLoader.DEVICE);

			if (value.toString() != null)
				if (value.toString().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || value.toString().equals(LanguageTool.getString("UNKNOWN_ALBUM")))
					icon.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);

			return icon;
		}
	};

	public static final TreeCellRenderer DEVICE_BY_FOLDER_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = 2709935527008825819L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			Object content = node.getUserObject();

			JLabel icon = new JLabel(value.toString());
			if (content instanceof Folder)
				icon.setIcon(ImageLoader.FOLDER);
			else
				icon.setIcon(ImageLoader.DEVICE);

			if (value.toString() != null)
				if (value.toString().equals(LanguageTool.getString("UNKNOWN_ARTIST")) || value.toString().equals(LanguageTool.getString("UNKNOWN_ALBUM")))
					icon.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);

			return icon;
		}
	};

	public static final TreeCellRenderer RADIO_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = 8184884292645176037L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			icon.setIcon(ImageLoader.RADIO_LITTLE);
			return icon;
		}
	};

	public static final TreeCellRenderer PODCAST_FEED_TREE_RENDERER = new SubstanceDefaultTreeCellRenderer() {
		private static final long serialVersionUID = -4467842082863144354L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			icon.setIcon(ImageLoader.RSS_LITTLE);
			return icon;
		}
	};

	public static final DefaultTableCellRenderer CD_TRACK_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = -701224939490230080L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final DefaultTableCellRenderer AUDIO_SCROBBLER_ALBUMS_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = -2265707513309618899L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, ((AudioScrobblerAlbum) value).getTitle(), arg2, arg3, arg4, arg5);

			((JLabel) c).setIcon(((AudioScrobblerAlbum) value).getCover());
			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final DefaultTableCellRenderer AUDIO_SCROBLLER_ALBUMS_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 0L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, ((AudioScrobblerArtist) value).getName(), arg2, arg3, arg4, arg5);

			((JLabel) c).setIcon(((AudioScrobblerArtist) value).getImage());
			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final DefaultTableCellRenderer AUDIO_SCROBBLER_TRACK_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 6554520059295478131L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final DefaultTableCellRenderer AUDIO_SCROBBLER_TRACK_NUMBER_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 1328605998912553401L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	/**
	 * List renderer for rendering locales with flags.
	 */
	public static final ListCellRenderer PREFERENCES_DIALOG_FLAG_RENDERER = new SubstanceDefaultListCellRenderer() {
		private static final long serialVersionUID = 4124370361802581951L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (!(value instanceof Locale))
				throw new IllegalArgumentException("Argument value must be instance of Locale");

			Locale displayingLocale = (Locale) value;
			Locale currentLocale = Kernel.getInstance().state.getLocale().getLocale();

			String name = displayingLocale.getDisplayName(currentLocale);
			name = String.valueOf(name.charAt(0)).toUpperCase(currentLocale) + name.substring(1);
			Component c = super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);

			// The name of flag file should be flag_<locale>.png
			// if the name of bundle is MainBundle_<locale>.properties
			String flag = StringUtils.getString("flag_", displayingLocale.toString(), ".png");
			File flagFile = new File(StringUtils.getString(Constants.TRANSLATIONS_DIR, SystemProperties.fileSeparator, flag));
			try {
				((JLabel) c).setIcon(new ImageIcon(ImageIO.read(flagFile)));
			} catch (IOException e) {
				// Don't show any flag
			}
			return c;
		}
	};

	public static final ListCellRenderer LANGUAGE_SELECTOR_DIALOG_FLAG_RENDERER = new SubstanceDefaultListCellRenderer() {
		private static final long serialVersionUID = 4124370361802581951L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			// Name of flag file should be <language_name>.png
			String flag = StringUtils.getString(((String) value).toLowerCase(), ".png");
			File flagFile = new File(StringUtils.getString(Constants.TRANSLATIONS_DIR, SystemProperties.fileSeparator, flag));
			try {
				((JLabel) c).setIcon(new ImageIcon(ImageIO.read(flagFile)));
			} catch (IOException e) {
				// Don't show any flag
			}
			return c;
		}
	};

	public static final TableCellRenderer ABOUT_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 1111298953883261220L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final TableCellRenderer PLAYLIST_COLUMN_SELECTOR_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 1111298953883261220L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	public static final TableCellRenderer HOTKEY_RENDERER = new SubstanceDefaultTableCellRenderer() {
		private static final long serialVersionUID = 1111298953883261220L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
			Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

			GuiUtils.applyComponentOrientation((JLabel) c);

			return c;
		}
	};

	static String getToolTipForAlbum(Album a) {
		int songs = a.getAudioObjects().size();
		return StringUtils.getString(a.getName(), " - ", a.getArtist(), " (", songs, " ", (songs > 1 ? LanguageTool.getString("SONGS") : LanguageTool.getString("SONG")), ")");
	}

	static String getToolTipForArtist(Artist a) {
		int albums = a.getAlbums().size();
		return StringUtils.getString(a.getName(), " (", albums, " ", (albums > 1 ? LanguageTool.getString("ALBUMS") : LanguageTool.getString("ALBUM")), ")");
	}

	static String getToolTipForGenre(Genre g) {
		int artists = g.getArtists().size();
		int songs = g.getAudioObjects().size();
		return StringUtils.getString(g.getName(), " (", LanguageTool.getString("ARTISTS"), ": ", artists, ", ", LanguageTool.getString("SONGS"), ": ", songs, ")");
	}

	static String getToolTipForRepository() {
		int songs = RepositoryHandler.getInstance().getSongs().size();
		return StringUtils.getString(LanguageTool.getString("REPOSITORY"), " (", songs, " ", (songs > 1 ? LanguageTool.getString("SONGS") : LanguageTool.getString("SONG")), ")");
	}
}
