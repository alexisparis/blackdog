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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.kernel.controllers.ripcd.RipCdDialogController;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class RipCdDialog extends CustomModalDialog {

	private static class CdInfoTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -7577681531593039707L;

		private transient CDInfo cdInfo;
		private List<String> trackNames;
		private List<String> artistNames;
		private List<String> composerNames;

		private List<Boolean> tracksSelected;

		public CdInfoTableModel() {
			// Nothing to do
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}

		@Override
		public int getColumnCount() {
			return 5;
		}

		@Override
		public String getColumnName(int column) {
			if (column == 0)
				return "";
			else if (column == 1)
				return LanguageTool.getString("TITLE");
			else if (column == 2)
				return LanguageTool.getString("ARTIST");
			else if (column == 3)
				return LanguageTool.getString("COMPOSER");
			else
				return "";

		}

		@Override
		public int getRowCount() {
			return cdInfo != null ? cdInfo.getTracks() : 0;
		}

		public List<Boolean> getTracksSelected() {
			return tracksSelected;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0)
				return tracksSelected.get(rowIndex);
			else if (columnIndex == 1) {
				if (trackNames == null || rowIndex > trackNames.size() - 1) {
					trackNames.add(rowIndex, StringUtils.getString(LanguageTool.getString("TRACK"), " ", (rowIndex + 1)));
					return StringUtils.getString(LanguageTool.getString("TRACK"), " ", (rowIndex + 1));
				}
				if (rowIndex < trackNames.size())
					return trackNames.get(rowIndex);
				else {
					trackNames.add(rowIndex, StringUtils.getString(LanguageTool.getString("TRACK"), " ", (rowIndex + 1)));
					return StringUtils.getString(LanguageTool.getString("TRACK"), " ", (rowIndex + 1));
				}
			} else if (columnIndex == 2) {
				if (artistNames == null || rowIndex > artistNames.size() - 1) {
					// TODO if cdda2wav is modified for detecting song artist modify here
					if (cdInfo.getArtist() != null) {
						artistNames.add(rowIndex, cdInfo.getArtist());
						return cdInfo.getArtist();
					} else {
						artistNames.add(rowIndex, LanguageTool.getString("UNKNOWN_ARTIST"));
						return LanguageTool.getString("UNKNOWN_ARTIST");
					}
				}
				if (rowIndex < artistNames.size() && artistNames.get(rowIndex) != "") {
					return artistNames.get(rowIndex);
				} else {
					if (cdInfo.getArtist() != null) {
						artistNames.add(rowIndex, cdInfo.getArtist());
						return cdInfo.getArtist();
					} else {
						artistNames.add(rowIndex, LanguageTool.getString("UNKNOWN_ARTIST"));
						return LanguageTool.getString("UNKNOWN_ARTIST");
					}
				}
			} else if (columnIndex == 4) {
				return cdInfo.getDurations().get(rowIndex);
			} else {
				if (composerNames == null || rowIndex > composerNames.size() - 1) {
					composerNames.add(rowIndex, "");
					return "";
				} else {
					return composerNames.get(rowIndex);
				}
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex != 4;
		}

		public void setArtistNames(List<String> artistNames) {
			this.artistNames = artistNames;
		}

		public void setCdInfo(CDInfo cdInfo) {
			if (cdInfo != null) {
				this.cdInfo = cdInfo;
				if (tracksSelected == null)
					tracksSelected = new ArrayList<Boolean>();
				tracksSelected.clear();
				for (int i = 0; i < cdInfo.getTracks(); i++) {
					tracksSelected.add(true);
				}
			}
		}

		public void setComposerNames(List<String> composerNames) {
			this.composerNames = composerNames;
		}

		public void setTrackNames(List<String> trackNames) {
			this.trackNames = trackNames;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				tracksSelected.remove(rowIndex);
				tracksSelected.add(rowIndex, (Boolean) aValue);
			} else if (columnIndex == 1) {
				trackNames.set(rowIndex, (String) aValue);
			} else if (columnIndex == 2) {
				artistNames.set(rowIndex, (String) aValue);
			} else if (columnIndex == 3) {
				composerNames.set(rowIndex, (String) aValue);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	private static final long serialVersionUID = 1987727841297807350L;
	private JTable table;
	private JTextField artistTextField;
	private JTextField albumTextField;
	private JTextField yearTextField;

	private JComboBox genreComboBox;
	private JButton amazonButton;
	private JComboBox format;
	private JComboBox quality;
	private JComboBox filePattern;
	private JTextField folderName;
	private JButton folderSelectionButton;
	private JButton ok;

	private JButton cancel;

	private CdInfoTableModel tableModel;

	public RipCdDialog(JFrame owner) {
		super(owner, 750, 540, true);
		setTitle(LanguageTool.getString("RIP_CD"));
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	public static void main(String[] args) {
		RipCdDialog dialog = new RipCdDialog(null);
		dialog.setVisible(true);
	}

	public JTextField getAlbumTextField() {
		return albumTextField;
	}

	public JButton getAmazonButton() {
		return amazonButton;
	}

	public List<String> getArtistNames() {
		return tableModel.artistNames;
	}

	public JTextField getArtistTextField() {
		return artistTextField;
	}

	public JButton getCancel() {
		return cancel;
	}

	public List<String> getComposerNames() {
		return tableModel.composerNames;
	}

	/**
	 * Defines the content of the dialog box
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		tableModel = new CdInfoTableModel();
		table = new JTable(tableModel);
		table.setShowGrid(false);
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.getColumnModel().getColumn(4).setMaxWidth(50);
		JCheckBox checkBox = new JCheckBox();
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(checkBox));
		JTextField textfield1 = new JTextField();
		JTextField textfield2 = new JTextField();
		JTextField textfield3 = new JTextField();
		textfield1.setBorder(BorderFactory.createEmptyBorder());
		textfield2.setBorder(BorderFactory.createEmptyBorder());
		textfield3.setBorder(BorderFactory.createEmptyBorder());
		GuiUtils.applyComponentOrientation(textfield1);
		GuiUtils.applyComponentOrientation(textfield2);
		GuiUtils.applyComponentOrientation(textfield3);

		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textfield1));
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(textfield2));
		table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(textfield3));

		table.setDefaultRenderer(String.class, Renderers.CD_TRACK_RENDERER);

		JScrollPane scrollPane = new JScrollPane(table);
		JLabel artistLabel = new JLabel(LanguageTool.getString("ALBUM_ARTIST"));
		artistTextField = new JTextField();
		JLabel albumLabel = new JLabel(LanguageTool.getString("ALBUM"));
		albumTextField = new JTextField();
		JLabel yearLabel = new JLabel();
		yearLabel.setText(LanguageTool.getString("YEAR"));
		yearTextField = new JTextField();
		JLabel genreLabel = new JLabel(LanguageTool.getString("GENRE"));

		genreComboBox = new JComboBox();
		genreComboBox.setEditable(true);
		amazonButton = new CustomButton(null, LanguageTool.getString("GET_TITLES_FROM_AMAZON"));
		amazonButton.setEnabled(false);
		JLabel formatLabel = new JLabel(LanguageTool.getString("ENCODE_TO"));

		format = new JComboBox();
		JLabel qualityLabel = new JLabel(LanguageTool.getString("QUALITY"));

		quality = new JComboBox(new String[] {});
		quality.setMinimumSize(new Dimension(150, 20));
		JLabel filePatternLabel = new JLabel(LanguageTool.getString("FILEPATTERN"));

		filePattern = new JComboBox(RipCdDialogController.FILENAMEPATTERN);
		JLabel dir = new JLabel(LanguageTool.getString("FOLDER"));

		folderName = new JTextField();
		folderSelectionButton = new JButton(LanguageTool.getString("SELECT_FOLDER"));

		// Explain what the file name pattern means
		JLabel explainPatterns = new JLabel(StringUtils.getString("%A=", LanguageTool.getString("ARTIST"), "  -  %L=", LanguageTool.getString("ALBUM"), "  -  %N=", LanguageTool
				.getString("TRACK"), "  -  %T=", LanguageTool.getString("TITLE")));

		ok = new CustomButton(null, LanguageTool.getString("OK"));
		cancel = new CustomButton(null, LanguageTool.getString("CANCEL"));

		JPanel auxPanel = new JPanel();
		auxPanel.setOpaque(false);
		auxPanel.add(ok);
		auxPanel.add(cancel);

		// Here we define the cd ripper dialog display layout
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridwidth = 4;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(20, 20, 10, 20);
		panel.add(scrollPane, c);
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 20, 5, 20);
		c.anchor = GridBagConstraints.WEST;
		panel.add(artistLabel, c);
		c.gridx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(artistTextField, c);
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		panel.add(albumLabel, c);
		c.gridx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(albumTextField, c);
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		panel.add(genreLabel, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(genreComboBox, c);
		c.gridx = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		panel.add(yearLabel, c);
		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(yearTextField, c);
		c.gridx = 1;
		c.gridwidth = 3;
		c.gridy = 4;
		panel.add(amazonButton, c);
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		panel.add(formatLabel, c);
		c.gridx = 1;
		c.weightx = 0.3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(format, c);
		c.gridx = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(qualityLabel, c);
		c.gridx = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(quality, c);
		c.gridx = 0;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		panel.add(dir, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		panel.add(folderName, c);
		c.gridx = 0;
		c.gridy = 7;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		panel.add(filePatternLabel, c);
		c.gridx = 1;
		c.weightx = 0.3;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(filePattern, c);
		c.gridx = 2;
		c.gridwidth = 2;
		panel.add(folderSelectionButton, c);
		c.gridx = 1;
		c.gridy = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 3;
		panel.add(explainPatterns, c);
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 8;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(auxPanel, c);

		return panel;
	}

	/**
	 * Returns the filename pattern selected in the CD ripper dialog
	 * 
	 * @return Filename pattern
	 */
	public String getFileNamePattern() {
		return (String) filePattern.getSelectedItem();
	}

	public JComboBox getFilePattern() {
		return filePattern;
	}

	public JTextField getFolderName() {
		return folderName;
	}

	public JButton getFolderSelectionButton() {
		return folderSelectionButton;
	}

	public JComboBox getFormat() {
		return format;
	}

	public JComboBox getGenreComboBox() {
		return genreComboBox;
	}

	public JButton getOk() {
		return ok;
	}

	public String getQuality() {
		return (String) quality.getSelectedItem();
	}

	public JComboBox getQualityComboBox() {
		return quality;
	}

	public List<String> getTrackNames() {
		return tableModel.trackNames;
	}

	public List<Integer> getTracksSelected() {
		List<Boolean> tracks = tableModel.getTracksSelected();
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < tracks.size(); i++) {
			if (tracks.get(i))
				result.add(i + 1);
		}
		return result;
	}

	public JTextField getYearTextField() {
		return yearTextField;
	}

	public void setTableData(CDInfo cdInfo) {
		tableModel.setCdInfo(cdInfo);
		tableModel.fireTableDataChanged();
	}

	public void updateArtistNames(List<String> names) {
		tableModel.setArtistNames(names);
		tableModel.fireTableDataChanged();
	}

	public void updateComposerNames(List<String> names) {
		tableModel.setComposerNames(names);
		tableModel.fireTableDataChanged();
	}

	public void updateTrackNames(List<String> names) {
		tableModel.setTrackNames(names);
		tableModel.fireTableDataChanged();
	}

}
