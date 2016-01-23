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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.Renderers;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import com.fleax.ant.BuildNumberReader;

public class AboutDialog extends CustomModalDialog {

	private static class AboutDialogTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1786557125033788184L;

		private List<String[]> valuesToShow;

		AboutDialogTableModel() {
			refreshData();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			return column == 0 ? "Property" : "Value";
		}

		private List<String[]> getData() {
			MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			MemoryUsage nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
			List<String[]> data = new ArrayList<String[]>();
			data.add(new String[] { "Version", Constants.APP_VERSION_NUMBER });
			data.add(new String[] { "Build Number", Integer.toString(BuildNumberReader.getBuildNumber()) });
			data.add(new String[] { "Build Date", new SimpleDateFormat("dd/MM/yyyy").format(BuildNumberReader.getBuildDate()) });
			data.add(new String[] { "Java Runtime Enviroment", System.getProperty("java.version") });
			data.add(new String[] { "OS", System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ')' });
			data.add(new String[] { "Used Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getUsed()) });
			data.add(new String[] { "Max Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getMax()) });
			data.add(new String[] { "Initial Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getInit()) });
			data.add(new String[] { "Committed Heap Space", StringUtils.fromByteToMegaOrGiga(heapUsage.getCommitted()) });
			data.add(new String[] { "Used Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getUsed()) });
			data.add(new String[] { "Max Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getMax()) });
			data.add(new String[] { "Initial Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getInit()) });
			data.add(new String[] { "Committed Non Heap Space", StringUtils.fromByteToMegaOrGiga(nonHeapUsage.getCommitted()) });
			return data;
		}

		@Override
		public int getRowCount() {
			return valuesToShow.size();
		}

		@Override
		public String getValueAt(int rowIndex, int columnIndex) {
			return valuesToShow.get(rowIndex)[columnIndex];
		}

		public void refreshData() {
			valuesToShow = getData();
		}
	}

	private static final long serialVersionUID = 8666235475424750562L;

	private AboutDialogTableModel tableModel = new AboutDialogTableModel();

	private String licenseText = getLicenseText();

	public AboutDialog(JFrame owner) {
		super(owner, 500, 620, true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel title = new JLabel(Constants.APP_NAME + ' ' + Constants.APP_VERSION_NUMBER);
		title.setFont(Fonts.ABOUT_BIG_FONT);
		JLabel description = new JLabel(Constants.APP_DESCRIPTION);

		JTextArea license = new JTextArea(licenseText);
		license.setEditable(false);
		license.setOpaque(false);
		license.setLineWrap(true);
		license.setWrapStyleWord(true);
		license.setBorder(BorderFactory.createEmptyBorder());

		UrlLabel url = new UrlLabel(Constants.APP_WEB, Constants.APP_WEB);
		UrlLabel url2 = new UrlLabel(LanguageTool.getString("CONTRIBUTORS"), Constants.CONTRIBUTORS_WEB);
		url2.setFont(Fonts.APP_VERSION_TITLE_FONT);

		JTable propertiesTable = new JTable(tableModel);
		propertiesTable.setShowGrid(false);
		propertiesTable.setDefaultRenderer(Object.class, Renderers.ABOUT_RENDERER);
		JScrollPane scrollPane = new JScrollPane(propertiesTable);
		//scrollPane.setBorder(BorderFactory.createEmptyBorder());

		JButton close = new CustomButton(null, LanguageTool.getString("CLOSE"));
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.1;
		panel.add(title, c);
		c.gridy = 1;
		c.weighty = 0.1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(description, c);
		c.gridy = 2;
		c.insets = new Insets(20, 20, 0, 20);
		panel.add(url2, c);
		c.gridy = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(20, 20, 0, 20);
		panel.add(license, c);
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(url, c);
		c.gridy = 5;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 20, 10, 20);
		panel.add(scrollPane, c);
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.weighty = 0;
		c.insets = new Insets(0, 20, 10, 20);
		panel.add(close, c);

		return panel;
	}

	private String getLicenseText() {
		return StringUtils.getString("Copyright (C) 2006-2008  The aTunes Team\n\n", "This program is free software; you can redistribute it and/or ",
				"modify it under the terms of the GNU General Public License ", "as published by the Free Software Foundation; either version 2 ",
				"of the License, or (at your option) any later version.\n\n", "This program is distributed in the hope that it will be useful, ",
				"but WITHOUT ANY WARRANTY; without even the implied warranty of ", "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ",
				"GNU General Public License for more details.\n\n", "You should have received a copy of the GNU General Public License ",
				"along with this program; if not, write to the\n\nFree Software ", "Foundation, Inc.\n51 Franklin Street, Fifth Floor\nBoston, MA\n02110-1301, USA");
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			tableModel.refreshData();
		}
		super.setVisible(visible);
	}
}
