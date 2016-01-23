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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.kernel.utils.SystemProperties;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.jvnet.substance.SubstanceDefaultTreeCellRenderer;

public class MultiFolderSelectionDialog extends CustomModalDialog {

	public class CheckNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = 3563009061452848710L;

		protected boolean isSelected;
		private boolean enabled = true;

		Directory dir;

		public CheckNode() {
			this(null);
		}

		public CheckNode(Object userObject) {
			this(userObject, true);
		}

		public CheckNode(Object userObject, boolean allowsChildren) {
			super(userObject, allowsChildren);
			this.dir = (Directory) userObject;
			if (this.dir != null && foldersSelected.contains(this.dir.file))
				setSelected(true);
		}

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isSelected() {
			return isSelected;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;

			if (children != null) {
				Enumeration<?> enume = children.elements();
				while (enume.hasMoreElements()) {
					CheckNode node = (CheckNode) enume.nextElement();
					node.setSelected(isSelected);
					node.setEnabled(!isSelected);
				}
			}

			((DefaultTreeModel) fileSystemTree.getModel()).nodeChanged(this);
		}
	}

	public class CheckRenderer extends SubstanceDefaultTreeCellRenderer implements TreeCellRenderer {
		private static final long serialVersionUID = 5564069979708271654L;
		protected JCheckBox check;
		protected JLabel label;

		public CheckRenderer() {
			setLayout(new FlowLayout());
			check = new JCheckBox();
			check.setOpaque(false);
			add(check);
			add(label = new JLabel());
		}

		// TODO this method should be aware of component orientation
		@Override
		public void doLayout() {
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();
			int y_check = 0;
			int y_label = 0;
			if (d_check.height < d_label.height) {
				y_check = (d_label.height - d_check.height) / 2;
			} else {
				y_label = (d_check.height - d_label.height) / 2;
			}
			check.setLocation(0, y_check);
			check.setBounds(0, y_check, d_check.width, d_check.height);
			label.setLocation(d_check.width, y_label);

			label.setBounds(d_check.width, y_label, d_label.width + 350, d_label.height);
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d_check = check.getPreferredSize();
			Dimension d_label = label.getPreferredSize();
			return new Dimension(d_check.width + d_label.width, (d_check.height < d_label.height ? d_label.height : d_check.height));
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus1) {
			String stringValue = value.toString();
			setEnabled(tree.isEnabled());
			check.setSelected(((CheckNode) value).isSelected());
			check.setEnabled(((CheckNode) value).isEnabled());
			label.setFont(tree.getFont());
			label.setText(stringValue);
			if (((CheckNode) value).getUserObject() instanceof Directory) {
				Directory content = (Directory) ((CheckNode) value).getUserObject();
				label.setIcon(fsView.getSystemIcon(content.file));
				if (isInPathOfSelectedFolders(content.file) || ((CheckNode) value).isSelected())
					label.setFont(label.getFont().deriveFont(Font.BOLD));
			}

			return this;
		}
	}

	private static class Directory {
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

	private static final Logger logger = new Logger();

	private static FileSystemView fsView = FileSystemView.getFileSystemView();

	private JTree fileSystemTree;
	private JScrollPane scrollPane;

	private JButton okButton;
	private JButton cancelButton;

	private JLabel text;

	private List<File> foldersSelected;

	private boolean cancelled = true;

	public MultiFolderSelectionDialog(JFrame owner) {
		super(owner, 460, 530, true);
		setContent(getContent());
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public static void main(String[] args) {
		MultiFolderSelectionDialog dialog = new MultiFolderSelectionDialog(null);

		dialog.startDialog(null);

		if (!dialog.isCancelled()) {
			List<File> folders = dialog.getFoldersSelected();
			for (File f : folders)
				System.out.println(f);
		}
	}

	private JPanel getContent() {
		JPanel panel = new JPanel(null);

		text = new JLabel();

		fileSystemTree = new JTree();
		fileSystemTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollPane = new JScrollPane();
		//scroll1.setViewportView(fileSystemTree);

		okButton = new CustomButton(null, LanguageTool.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled = false;
				dispose();
			}
		});
		cancelButton = new CustomButton(null, LanguageTool.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		text.setSize(430, 20);
		text.setLocation(10, 10);
		panel.add(text);

		scrollPane.setSize(430, 410);
		scrollPane.setLocation(10, 40);
		panel.add(scrollPane);

		okButton.setSize(100, 25);
		okButton.setLocation(230, 460);
		panel.add(okButton);

		cancelButton.setSize(100, 25);
		cancelButton.setLocation(340, 460);
		panel.add(cancelButton);

		return panel;
	}

	File[] getFiles(File f) {
		File[] files = fsView.getFiles(f, true);
		List<File> list = new ArrayList<File>();
		for (File element : files) {
			if (element.isDirectory())
				list.add(element);
		}
		return list.toArray(new File[list.size()]);
	}

	public List<File> getFoldersSelected() {
		return foldersSelected;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	boolean isInPathOfSelectedFolders(File dir) {
		String dirPath = dir.getAbsolutePath().concat(SystemProperties.fileSeparator);
		for (File folder : foldersSelected) {
			if (folder.getAbsolutePath().startsWith(dirPath))
				return true;
		}
		return false;
	}

	public void setText(String text) {
		this.text.setText(text);
	}

	private void setTree() {

		new SwingWorker<CheckNode, Void>() {

			@Override
			protected CheckNode doInBackground() throws Exception {
				File[] roots = fsView.getRoots();

				CheckNode root = new CheckNode();

				for (File f : roots) {
					CheckNode treeNode = new CheckNode(new Directory(f));
					root.add(treeNode);
					File[] files = fsView.getFiles(f, true);
					Arrays.sort(files);
					for (File f2 : files) {
						File[] f2Childs = f2.listFiles();
						if (f2Childs != null) {
							// Get number of dirs under f2
							int dirs = 0;
							for (File f3 : f2Childs)
								if (f3.isDirectory())
									dirs++;
							CheckNode treeNode2 = new CheckNode(new Directory(f2));
							treeNode.add(treeNode2);
							if (dirs > 0) {
								treeNode2.add(new CheckNode(new Directory(new File("Dummy node"))));
							}
						}
					}
				}
				return root;
			}

			@Override
			protected void done() {
				try {
					DefaultTreeModel model = new DefaultTreeModel(get());
					fileSystemTree.setModel(model);
					fileSystemTree.setRootVisible(false);
					fileSystemTree.expandRow(0);
					fileSystemTree.setSelectionRow(0);
					setTreeRenderer();

					fileSystemTree.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							if (e.getButton() == MouseEvent.BUTTON3) {
								int x = e.getX();
								int y = e.getY();
								int row = fileSystemTree.getRowForLocation(x, y);
								TreePath path = fileSystemTree.getPathForRow(row);
								if (path != null) {
									//fileSystemTree.expandPath(path);
									CheckNode node = (CheckNode) path.getLastPathComponent();
									if (node.isEnabled()) {
										boolean isSelected = !(node.isSelected());
										node.setSelected(isSelected);

										if (isSelected) {
											// Find if another child folder has been added before
											List<File> childFolders = new ArrayList<File>();
											for (File f : foldersSelected) {
												if (f.getAbsolutePath().startsWith(node.dir.file.getAbsolutePath())) {
													childFolders.add(f);
												}
											}
											for (File f : childFolders)
												foldersSelected.remove(f);

											foldersSelected.add(node.dir.file);
										} else
											foldersSelected.remove(node.dir.file);

										// I need revalidate if node is root.  but why?
										if (row == 0) {
											fileSystemTree.revalidate();
											fileSystemTree.repaint();
										}
									}
								}
							}
						}
					});
					fileSystemTree.addTreeWillExpandListener(new TreeWillExpandListener() {
						@Override
						public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
						}

						@Override
						public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
							try {
								// Show wait cursor
								fileSystemTree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

								CheckNode nodeSelected = (CheckNode) event.getPath().getLastPathComponent();
								fileSystemTree.setSelectionPath(event.getPath());
								nodeSelected.removeAllChildren();
								Directory dir = (Directory) nodeSelected.getUserObject();
								File[] files = fsView.getFiles(dir.file, true);
								Arrays.sort(files);
								for (File f : files) {
									File[] fChilds = f.listFiles();
									if (fChilds != null) {
										// Get number of dirs under f2
										int dirs = 0;
										for (File f3 : fChilds)
											if (f3.isDirectory())
												dirs++;
										CheckNode treeNode2 = new CheckNode(new Directory(f));
										treeNode2.setSelected(nodeSelected.isSelected || foldersSelected.contains(f));
										treeNode2.setEnabled(!nodeSelected.isSelected);

										nodeSelected.add(treeNode2);
										if (dirs > 0) {
											treeNode2.add(new CheckNode(new Directory(new File("Dummy node"))));
										}

									}

								}
							} finally {
								// Show default cursor
								fileSystemTree.setCursor(Cursor.getDefaultCursor());
							}
						}
					});
					fileSystemTree.addTreeSelectionListener(new TreeSelectionListener() {
						@Override
						public void valueChanged(TreeSelectionEvent e) {
							CheckNode node = (CheckNode) e.getPath().getLastPathComponent();
							Directory dir = (Directory) node.getUserObject();
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
						}

					});
					scrollPane.setViewportView(fileSystemTree);
					scrollPane.setVisible(true);
					fileSystemTree.revalidate();
					fileSystemTree.repaint();
				} catch (Exception e) {
					logger.internalError(e);
				} finally {
					okButton.setEnabled(true);
					// Show default cursor
					MultiFolderSelectionDialog.this.setCursor(Cursor.getDefaultCursor());
				}

			}

		}.execute();

		// Show wait cursor
		MultiFolderSelectionDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		okButton.setEnabled(false);
	}

	private void setTreeRenderer() {
		fileSystemTree.setCellRenderer(new CheckRenderer());
	}

	public void startDialog(List<File> selectedFolders) {
		cancelled = true;
		if (selectedFolders == null)
			foldersSelected = new ArrayList<File>();
		else
			foldersSelected = selectedFolders;
		setTree();
		setVisible(true);
	}

}
