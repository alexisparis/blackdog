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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.SubstanceDefaultListCellRenderer;
import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;

public class FileSelectionDialog extends CustomModalDialog {

	private class Directory {
		public File file;

		Directory(File file) {
			this.file = file;
		}

		@Override
		public String toString() {
			return fsView.getSystemDisplayName(file);
		}
	}

	private static final long serialVersionUID = -1612490779910952274L;

	private static transient FileSystemView fsView = FileSystemView.getFileSystemView();
	private JTree fileSystemTree;
	private JList fileSystemList;
	private JLabel selection;
	private JButton okButton;

	private JButton cancelButton;

	private boolean dirOnly;
	private boolean canceled = true;
	private File selectedDir;

	private File[] selectedFiles;

	public FileSelectionDialog(JFrame owner, boolean dirOnly) {
		super(owner, 650, 400, true);
		this.dirOnly = dirOnly;
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	public static void main(String[] args) {
		new FileSelectionDialog(null, true).startDialog();
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(null);

		fileSystemTree = new JTree();
		fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane scroll1 = new JScrollPane(fileSystemTree);

		fileSystemList = new JList();
		setListRenderer();
		JScrollPane scroll2 = new JScrollPane(fileSystemList);

		selection = new JLabel();

		okButton = new CustomButton(null, LanguageTool.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedDir = null;
				selectedFiles = null;
				if (dirOnly) {
					if (fileSystemList.getSelectedValue() != null)
						selectedDir = new File(((File) fileSystemList.getSelectedValue()).getAbsolutePath());
					else {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
						selectedDir = ((Directory) node.getUserObject()).file;
					}
				} else {
					if (fileSystemList.getSelectedValues().length > 0) {
						Object[] files = fileSystemList.getSelectedValues();
						selectedFiles = new File[files.length];
						System.arraycopy(files, 0, selectedFiles, 0, files.length);
					} else {
						selectedFiles = new File[1];
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
						selectedFiles[0] = ((Directory) node.getUserObject()).file;
					}
				}
				canceled = false;
				setVisible(false);
			}
		});
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		scroll1.setSize(220, 320);
		scroll1.setLocation(10, 10);
		panel.add(scroll1);

		scroll2.setSize(400, 320);
		scroll2.setLocation(240, 10);
		panel.add(scroll2);

		selection.setSize(600, 20);
		selection.setLocation(10, 335);
		panel.add(selection);

		okButton.setSize(100, 25);
		okButton.setLocation(430, 360);
		panel.add(okButton);

		cancelButton.setSize(100, 25);
		cancelButton.setLocation(540, 360);
		panel.add(cancelButton);

		return panel;
	}

	File[] getFiles(File f) {
		File[] files = fsView.getFiles(f, true);
		List<File> list = new ArrayList<File>();
		for (File element : files) {
			if (!dirOnly)
				list.add(element);
			else if (element.isDirectory())
				list.add(element);
		}
		return list.toArray(new File[list.size()]);
	}

	public File getSelectedDir() {
		return selectedDir;
	}

	public File[] getSelectedFiles() {
		return selectedFiles;
	}

	public boolean isCanceled() {
		return canceled;
	}

	private void setListRenderer() {
		fileSystemList.setCellRenderer(new SubstanceDefaultListCellRenderer() {
			private static final long serialVersionUID = -9000934785599172292L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				File f = (File) value;

				JLabel icon = (JLabel) super.getListCellRendererComponent(list, fsView.getSystemDisplayName(f), index, isSelected, cellHasFocus);
				icon.setHorizontalAlignment(GuiUtils.getComponentOrientationAsSwingConstant());

				icon.setIcon(fsView.getSystemIcon(f));
				return icon;
			}
		});
	}

	void setSelectionText(File f) {
		String displayName;
		if (!fsView.isFileSystem(f))
			displayName = fsView.getSystemDisplayName(f);
		else
			displayName = f.getAbsolutePath();
		selection.setText(displayName);
	}

	private void setTree() {
		File[] roots = fsView.getRoots();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();

		for (File f : roots) {
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(new Directory(f));
			root.add(treeNode);
			File[] files = fsView.getFiles(f, true);
			Arrays.sort(files);
			for (File f2 : files) {
				if (fsView.isTraversable(f2)) {
					DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new Directory(f2));
					treeNode.add(treeNode2);
					treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
				}
			}
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		fileSystemTree.setModel(model);
		fileSystemTree.setRootVisible(false);
		fileSystemTree.expandRow(0);
		fileSystemTree.setSelectionRow(0);
		fileSystemList.setListData(getFiles(roots[0]));
		setSelectionText(roots[0]);
		setTreeRenderer();
		fileSystemTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// Nothing to do
			}

			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
				nodeSelected.removeAllChildren();
				Directory dir = (Directory) nodeSelected.getUserObject();
				File[] files = fsView.getFiles(dir.file, true);
				Arrays.sort(files);
				for (File f : files) {
					if (fsView.isTraversable(f)) {
						DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new Directory(f));
						nodeSelected.add(treeNode2);
						treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
					}
				}
			}
		});
		fileSystemTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				Directory dir = (Directory) node.getUserObject();
				setSelectionText(dir.file);
				File[] files = getFiles(dir.file);
				List<File> dirsList = new ArrayList<File>();
				List<File> filesList = new ArrayList<File>();
				for (File element : files) {
					if (element.isDirectory())
						dirsList.add(element);
					else
						filesList.add(element);
				}
				Collections.sort(dirsList);
				Collections.sort(filesList);
				dirsList.addAll(filesList);
				fileSystemList.setListData(dirsList.toArray());
			}
		});
		fileSystemList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				File f = (File) fileSystemList.getSelectedValue();
				setSelectionText(f);
				if (e.getClickCount() == 2) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) fileSystemTree.getSelectionPath().getLastPathComponent();
					TreePath path = new TreePath(parentNode.getPath());
					fileSystemTree.expandPath(path);
					int i = 0;
					DefaultMutableTreeNode childToSelect = null;
					while (i < parentNode.getChildCount() || childToSelect == null) {
						DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
						if (((Directory) child.getUserObject()).file.equals(f))
							childToSelect = child;
						i++;
					}
					TreeNode[] newPath = new TreeNode[parentNode.getPath().length + 1];
					for (int j = 0; j < parentNode.getPath().length; j++)
						newPath[j] = parentNode.getPath()[j];
					newPath[parentNode.getPath().length] = childToSelect;

					fileSystemTree.setSelectionPath(new TreePath(newPath));
				}
			}
		});
	}

	private void setTreeRenderer() {
		fileSystemTree.setCellRenderer(new SubstanceDefaultTreeCellRenderer() {
			private static final long serialVersionUID = -5929642375743958911L;

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

				if (node.getUserObject() instanceof String)
					return super.getTreeCellRendererComponent(tree, "", selected, expanded, leaf, row, hasFocus);

				Directory content = (Directory) node.getUserObject();
				JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				icon.setIcon(fsView.getSystemIcon(content.file));

				return icon;
			}
		});
	}

	public void startDialog() {
		canceled = true;
		setTree();
		setVisible(true);
	}
}
