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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

class PodcastFeedEntryPropertiesDialog extends PropertiesDialog {

	private static final long serialVersionUID = -2472573171771586037L;

	private JLabel pictureLabel;
	private JLabel titleLabel;
	private JLabel artistLabel;
	private JLabel urlLabel;
	private JLabel durationLabel;
	private JLabel dateLabel;
	private JLabel podcastFeedLabel;
	private JLabel descriptionLabel;
	private JScrollPane descriptionScrollPane;
	private JTextArea descriptionTextArea;

	private PodcastFeedEntry entry;

	private Locale locale;

	PodcastFeedEntryPropertiesDialog(PodcastFeedEntry entry) {
		super(getTitleText(entry));
		this.entry = entry;
		this.locale = Kernel.getInstance().state.getLocale().getLocale();
		addContent();

		setContent();

		GuiUtils.applyComponentOrientation(this);
	}

	/**
	 * Gives a title for dialog
	 * 
	 * @param entry
	 * @return title for dialog
	 */
	private static String getTitleText(PodcastFeedEntry entry) {
		return StringUtils.getString(LanguageTool.getString("INFO_OF_PODCAST_FEED"), " ", entry.getName());
	}

	private void addContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		pictureLabel = new JLabel();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.VERTICAL;
		panel.add(pictureLabel, c);

		titleLabel = new JLabel();
		titleLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(titleLabel, c);

		artistLabel = new JLabel();
		artistLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 1;
		panel.add(artistLabel, c);

		urlLabel = new JLabel();
		urlLabel.setFont(Fonts.PROPERTIES_DIALOG_BIG_FONT);
		c.gridx = 1;
		c.gridy = 2;
		panel.add(urlLabel, c);

		durationLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 3;
		panel.add(durationLabel, c);

		dateLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 4;
		panel.add(dateLabel, c);

		podcastFeedLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 5;
		panel.add(podcastFeedLabel, c);

		descriptionLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 6;
		panel.add(descriptionLabel, c);

		descriptionScrollPane = new JScrollPane();
		descriptionScrollPane.setMinimumSize(new Dimension(400, 100));
		c.gridx = 1;
		c.gridy = 7;
		panel.add(descriptionScrollPane, c);

		descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionScrollPane.setViewportView(descriptionTextArea);

		add(panel);

	}

	private void fillPicture() {
		ImageIcon picture = ImageLoader.RSS;
		pictureLabel.setPreferredSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
		pictureLabel.setIcon(picture);
		pictureLabel.setVisible(true);
	}

	private void setContent() {
		fillPicture();
		titleLabel.setText(getHtmlFormatted(LanguageTool.getString("NAME"), entry.getName()));
		artistLabel.setText(getHtmlFormatted(LanguageTool.getString("ARTIST"), entry.getAuthor()));
		urlLabel.setText(getHtmlFormatted(LanguageTool.getString("URL"), entry.getUrl()));
		if (entry.getDuration() > 0) {
			durationLabel.setText(getHtmlFormatted(LanguageTool.getString("DURATION"), TimeUtils.seconds2String(entry.getDuration())));
		} else {
			durationLabel.setText(" ");
		}
		if (entry.getDate() != null) {
			dateLabel.setText(getHtmlFormatted(LanguageTool.getString("DATE"), StringUtils.getString(DateFormat.getDateInstance(DateFormat.LONG, locale).format(entry.getDate()),
					", ", DateFormat.getTimeInstance().format(entry.getDate()))));
		} else {
			dateLabel.setText(" ");
		}
		podcastFeedLabel.setText(getHtmlFormatted(LanguageTool.getString("PODCAST_FEED"), entry.getPodcastFeed().getName()));
		descriptionLabel.setText(getHtmlFormatted(LanguageTool.getString("DESCRIPTION"), ""));
		descriptionTextArea.setText(entry.getDescription());
		descriptionTextArea.setCaretPosition(0);
	}

}
