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

package net.sourceforge.atunes.kernel.handlers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Frame;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.MultipleFrame;
import net.sourceforge.atunes.gui.StandardFrame;
import net.sourceforge.atunes.gui.ToolBar;
import net.sourceforge.atunes.gui.model.PlayListTableModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListColumnSelector;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.gui.views.dialogs.AboutDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddPodcastFeedDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddRadioDialog;
import net.sourceforge.atunes.gui.views.dialogs.CopyProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorFrame;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.gui.views.dialogs.EqualizerDialog;
import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.gui.views.dialogs.FileSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.ImageDialog;
import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.InputDialog;
import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipperProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SplashScreenDialog;
import net.sourceforge.atunes.gui.views.dialogs.StatsDialog;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.gui.views.dialogs.fullScreen.FullScreenDialog;
import net.sourceforge.atunes.gui.views.dialogs.properties.PropertiesDialog;
import net.sourceforge.atunes.gui.views.menu.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioScrobblerPanel;
import net.sourceforge.atunes.gui.views.panels.FilePropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.controllers.DragAndDropListener;
import net.sourceforge.atunes.kernel.controllers.coverNavigator.CoverNavigatorController;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class VisualHandler {

	private static VisualHandler instance = new VisualHandler();

	/**
	 * Logger
	 */
	Logger logger = new Logger();

	/**
	 * Reference to the Kernel
	 */
	private Kernel kernel;

	/**
	 * Frames
	 */
	Frame frame;

	/**
	 * Dialogs
	 */
	private OSDDialog osdDialog;
	private ProgressDialog progressDialog;
	private EditTagDialog editTagDialog;
	private ExportOptionsDialog exportDialog;
	private StatsDialog statsDialog;
	private CopyProgressDialog exportProgressDialog;
	private CopyProgressDialog copyProgressDialog;
	private SearchDialog searchDialog;
	private RipCdDialog ripCdDialog;
	private RipperProgressDialog ripperProgressDialog;
	private IndeterminateProgressDialog indeterminateProgressDialog;
	private EditTitlesDialog editTitlesDialog;
	private EditPreferencesDialog editPreferencesDialog;
	private EqualizerDialog equalizerDialog;
	private AboutDialog aboutDialog;
	private SplashScreenDialog splashScreenDialog;

	private FullScreenDialog fullScreenFrame;

	private WindowAdapter fullFrameStateListener;

	private VisualHandler() {
		this.kernel = Kernel.getInstance();
		if (!kernel.state.isMultipleWindow())
			frame = new StandardFrame();
		else
			frame = new MultipleFrame();
	}

	public static VisualHandler getInstance() {
		return instance;
	}

	public void finish() {
		if (!kernel.state.isShowSystemTray())
			kernel.finish();
	}

	public AudioScrobblerPanel getAudioScrobblerPanel() {
		return frame.getAudioScrobblerPanel();
	}

	public CopyProgressDialog getCopyProgressDialog() {
		if (copyProgressDialog == null) {
			copyProgressDialog = new CopyProgressDialog(frame.getFrame(), StringUtils.getString(LanguageTool.getString("COPYING"), "..."));
		}
		return copyProgressDialog;
	}

	public EditPreferencesDialog getEditPreferencesDialog() {
		if (editPreferencesDialog == null)
			editPreferencesDialog = new EditPreferencesDialog(frame.getFrame());
		return editPreferencesDialog;
	}

	public EditTagDialog getEditTagDialog() {
		if (editTagDialog == null) {
			editTagDialog = new EditTagDialog(frame.getFrame());
		}
		return editTagDialog;
	}

	public EditTitlesDialog getEditTitlesDialog() {
		if (editTitlesDialog == null)
			editTitlesDialog = new EditTitlesDialog(frame.getFrame());
		return editTitlesDialog;
	}

	public EqualizerDialog getEqualizerDialog() {
		if (equalizerDialog == null)
			equalizerDialog = new EqualizerDialog(frame.getFrame());
		return equalizerDialog;
	}

	public ExportOptionsDialog getExportDialog() {
		if (exportDialog == null) {
			exportDialog = new ExportOptionsDialog(frame.getFrame());
		}
		return exportDialog;
	}

	public CopyProgressDialog getExportProgressDialog() {
		if (exportProgressDialog == null) {
			exportProgressDialog = new CopyProgressDialog(frame.getFrame(), StringUtils.getString(LanguageTool.getString("EXPORTING"), "..."));
		}
		return exportProgressDialog;
	}

	public FileSelectionDialog getFileSelectionDialog(boolean dirOnly) {
		return new FileSelectionDialog(frame.getFrame(), dirOnly);
	}

	public Frame getFrame() {
		return frame;
	}

	/**
	 * @return the fullScreenFrame
	 */
	public FullScreenDialog getFullScreenFrame() {
		if (fullScreenFrame == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			fullScreenFrame = new FullScreenDialog();
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		return fullScreenFrame;
	}

	public IndeterminateProgressDialog getIndeterminateProgressDialog() {
		if (indeterminateProgressDialog == null)
			indeterminateProgressDialog = new IndeterminateProgressDialog(frame.getFrame());
		return indeterminateProgressDialog;
	}

	public ApplicationMenuBar getMenuBar() {
		return frame.getAppMenuBar();
	}

	public MultiFolderSelectionDialog getMultiFolderSelectionDialog() {
		return new MultiFolderSelectionDialog(frame.getFrame());
	}

	public NavigationPanel getNavigationPanel() {
		return frame.getNavigationPanel();
	}

	public OSDDialog getOSDDialog() {
		if (osdDialog == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			osdDialog = new OSDDialog();
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		return osdDialog;
	}

	public PlayerControlsPanel getPlayerControls() {
		return frame.getPlayerControls();
	}

	public PlayListColumnSelector getPlayListColumnSelector() {
		return new PlayListColumnSelector(frame.getFrame());
	}

	public PlayListPanel getPlayListPanel() {
		return frame.getPlayListPanel();
	}

	public PlayListTable getPlayListTable() {
		return frame.getPlayListTable();
	}

	public PlayListTableModel getPlayListTableModel() {
		return (PlayListTableModel) getPlayListTable().getModel();
	}

	public ProgressDialog getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(frame.getFrame());
			progressDialog.getCancelButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					RepositoryHandler.getInstance().notifyCancel();
				}
			});
		}
		return progressDialog;
	}

	public FilePropertiesPanel getPropertiesPanel() {
		return frame.getPropertiesPanel();
	}

	public RipCdDialog getRipCdDialog() {
		if (ripCdDialog == null) {
			ripCdDialog = new RipCdDialog(frame.getFrame());
		}
		return ripCdDialog;
	}

	public RipperProgressDialog getRipperProgressDialog() {
		if (ripperProgressDialog == null) {
			ripperProgressDialog = new RipperProgressDialog();
			ripperProgressDialog.getCancelButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					RipperHandler.getInstance().cancelProcess();
				}
			});
		}
		return ripperProgressDialog;
	}

	public SearchDialog getSearchDialog() {
		if (searchDialog == null)
			searchDialog = new SearchDialog(frame.getFrame());
		return searchDialog;
	}

	public ComponentListener getStandardWindowListener() {
		final int minHeight = 410;
		return new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				JFrame f = frame.getFrame();

				if (frame.getPlayListPanel().getSize().width < StandardFrame.PLAY_LIST_PANEL_WIDTH) // Avoid resize
					f.setSize(f.getWidth() + (StandardFrame.PLAY_LIST_PANEL_WIDTH - frame.getPlayListPanel().getSize().width + 10), f.getHeight());

				if (f.getHeight() < minHeight)
					f.setSize(f.getWidth(), minHeight);
			}
		};
	}

	public StatsDialog getStatsDialog() {
		if (statsDialog == null)
			statsDialog = new StatsDialog();
		return statsDialog;
	}

	public ToolBar getToolBar() {
		return frame.getToolBar();
	}

	public Point getWindowLocation() {
		return frame.getLocation();
	}

	public Dimension getWindowSize() {
		return frame.getSize();
	}

	public WindowAdapter getWindowStateListener() {
		if (fullFrameStateListener == null) {
			fullFrameStateListener = new WindowAdapter() {
				@Override
				public void windowGainedFocus(WindowEvent e) {
					logger.debug(LogCategories.DESKTOP, "Window Gained Lost");
				}

				@Override
				public void windowLostFocus(WindowEvent e) {
					logger.debug(LogCategories.DESKTOP, "Window Focus Lost");
				}

				@Override
				public void windowStateChanged(WindowEvent e) {
					if ((e.getNewState() & java.awt.Frame.ICONIFIED) == java.awt.Frame.ICONIFIED) {
						if (kernel.state.isShowSystemTray())
							frame.setVisible(false);
						logger.debug(LogCategories.DESKTOP, "Window Iconified");
					} else if ((e.getNewState() & java.awt.Frame.NORMAL) == java.awt.Frame.NORMAL) {
						logger.debug(LogCategories.DESKTOP, "Window Deiconified");
						ControllerProxy.getInstance().getPlayListController().scrollPlayList();
					}
				}
			};
		}
		return fullFrameStateListener;
	}

	public void hideIndeterminateProgressDialog() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					getProgressDialog().setVisible(false);
				}
			});
		} catch (Exception e) {
			logger.internalError(e);
		}
	}

	public void hideSplashScreen() {
		if (splashScreenDialog != null) {
			splashScreenDialog.setVisible(false);
			splashScreenDialog.dispose();
			splashScreenDialog = null;
		}
	}

	/**
	 * Hides all windows
	 */
	public void hideWindows() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			window.setVisible(false);
		}
	}

	public boolean isMaximized() {
		return frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH;
	}

	public boolean isMultipleWindow() {
		return frame instanceof MultipleFrame;
	}

	public void repaint() {
		frame.getFrame().invalidate();
		frame.getFrame().validate();
		frame.getFrame().repaint();
	}

	public void setCenterStatusBarText(String text) {
		frame.setCenterStatusBar(text);
	}

	public void setFrameDefaultCloseOperation(int op) {
		frame.setDefaultCloseOperation(op);
	}

	public void setFullFrameExtendedState(int state) {
		frame.setExtendedState(state);
	}

	public void setFullFrameLocation(Point location) {
		frame.setLocation(location);
	}

	public void setFullFrameLocationRelativeTo(Component c) {
		frame.setLocationRelativeTo(c);
	}

	public void setFullFrameVisible(boolean visible) {
		frame.setVisible(visible);
	}

	public void setLeftStatusBarText(String text) {
		frame.setLeftStatusBarText(text);
	}

	public void setPlaying(boolean playing) {
		ControllerProxy.getInstance().getPlayerControlsController().setPlaying(playing);
		VisualHandler.getInstance().getFullScreenFrame().setPlaying(playing);
		SystemTrayHandler.getInstance().setPlaying(playing);
	}

	public void setRightStatusBarText(String text) {
		frame.setRightStatusBar(text);
	}

	/**
	 * Sets title bar text, adding app name and version
	 * 
	 * @param text
	 */
	public void setTitleBar(String text) {
		StringBuilder strBuilder = new StringBuilder();
		if (!text.equals("")) {
			strBuilder.append(text);
			strBuilder.append(" - ");
		}
		strBuilder.append(Constants.APP_NAME);
		strBuilder.append(" ");
		strBuilder.append(Constants.APP_VERSION_NUMBER);

		String result = strBuilder.toString();

		frame.setTitle(result);
		SystemTrayHandler.getInstance().setTrayToolTip(result);
	}

	public void setVolume(int volume) {
		frame.getPlayerControls().getVolumeSlider().setValue(volume);
	}

	public void showAboutDialog() {
		if (aboutDialog == null)
			aboutDialog = new AboutDialog(frame.getFrame());
		aboutDialog.setVisible(true);
	}

	public PodcastFeed showAddPodcastFeedDialog() {
		AddPodcastFeedDialog dialog = new AddPodcastFeedDialog(frame.getFrame());
		dialog.setVisible(true);
		return dialog.getPodcastFeed();
	}

	public Radio showAddRadioDialog() {
		AddRadioDialog dialog = new AddRadioDialog(frame.getFrame());
		dialog.setVisible(true);
		return dialog.getRadio();
	}

	public void showAudioScrobblerPanel(boolean show, boolean changeSize) {
		kernel.state.setUseAudioScrobbler(show);
		frame.showAudioScrobblerPanel(show, changeSize);
		if (show)
			ControllerProxy.getInstance().getAudioScrobblerController().updatePanel(PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject());
		ControllerProxy.getInstance().getMenuController().setShowAudioScrobblerPanel(show);
		ControllerProxy.getInstance().getToolBarController().setShowAudioScrobblerPanel(show);
	}

	public int showConfirmationDialog(String message, String title) {
		return JOptionPane.showConfirmDialog(frame.getFrame(), message, title, JOptionPane.YES_NO_OPTION);
	}

	public void showCoverNavigator() {
		CoverNavigatorFrame coverNavigator = new CoverNavigatorFrame(RepositoryHandler.getInstance().getArtists());
		new CoverNavigatorController(coverNavigator);
		coverNavigator.setVisible(true);
	}

	public void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(frame.getFrame(), message, LanguageTool.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
	}

	public void showErrorDialog(String message, Component parent) {
		JOptionPane.showMessageDialog(parent, message, LanguageTool.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Switches playlist filter on and off. Does not take into account the
	 * paramter passed! It will just show (if hidden) or hide (if visible) the
	 * filter each time called.
	 * 
	 * @param show
	 *            Gets ignored. You can either pass true or false with same
	 *            result
	 */
	public void showFilter(boolean show) {
		if (frame.getPlayListPanel().getPlayListFilter().isVisible())
			frame.getPlayListPanel().getPlayListFilter().setVisible(false);
		else
			frame.getPlayListPanel().getPlayListFilter().setVisible(true);
		// If show, then set focus to text field
		if (frame.getPlayListPanel().getPlayListFilter().isVisible())
			frame.getPlayListPanel().getPlayListFilter().setFocusToTextField();
	}

	public void showIconOnStatusBar(ImageIcon img, String text) {
		if (img == null)
			frame.showStatusBarImageLabel(false);
		else {
			frame.setStatusBarImageLabelIcon(img, text);
			frame.showStatusBarImageLabel(true);
		}
	}

	public void showImageDialog(AudioObject file) {
		new ImageDialog(frame.getFrame(), file);
	}

	public void showIndeterminateProgressDialog(final String text) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					getProgressDialog().setVisible(true);
					getProgressDialog().getLabel().setText(text);
					getProgressDialog().getProgressBar().setIndeterminate(true);
				}
			});
		} catch (Exception e) {
			logger.internalError(e);
		}
	}

	public void showInfo() {
		PlayListTable table = getPlayListTable();
		int[] rows = table.getSelectedRows();
		if (rows.length == 1) {
			AudioObject file = ((PlayListTableModel) table.getModel()).getFileAt(rows[0]);
			showPropertiesDialog(file);
		}
	}

	public String showInputDialog(String title, String text) {
		return showInputDialog(title, text, null);
	}

	public String showInputDialog(String title, String text, Image icon) {
		InputDialog id = new InputDialog(frame.getFrame(), title, icon);
		id.show(text);
		return id.getResult();
	}

	public void showMessage(String message) {
		JOptionPane.showMessageDialog(frame.getFrame(), message);
	}

	public void showNavigationPanel(boolean show, boolean changeSize) {
		kernel.state.setShowNavigationPanel(show);
		frame.showNavigationPanel(show, changeSize);
		ControllerProxy.getInstance().getMenuController().setShowNavigationPanel(show);
		ControllerProxy.getInstance().getToolBarController().setShowNavigatorPanel(show);
	}

	public void showNavigationTable(boolean show) {
		kernel.state.setShowNavigationTable(show);
		frame.showNavigationTable(show);
	}

	public int showOpenDialog(JFileChooser fileChooser, FileFilter filter) {
		fileChooser.setFileFilter(filter);
		return fileChooser.showOpenDialog(frame.getFrame());
	}

	public void showOSDDialog() {
		ControllerProxy.getInstance().getOSDDialogController().showOSD(PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject());
	}

	public void showPlayListInformation(PlayList playList) {
		int radios = playList.getNumberOfRadios();
		int podcastFeedEntries = playList.getNumberOfPodcastFeedEntries();
		int songs = playList.size() - radios;

		if (true) {
			Object[] strs = new Object[16];
			strs[0] = LanguageTool.getString("PLAYLIST");
			strs[1] = ": ";
			strs[2] = songs;
			strs[3] = " ";
			strs[4] = LanguageTool.getString("SONGS_IN_REPOSITORY");
			strs[5] = " (";
			strs[6] = playList.getLength();
			strs[7] = ") ";
			strs[8] = "/ ";
			strs[9] = radios;
			strs[10] = " ";
			strs[11] = LanguageTool.getString("RADIOS");
			strs[12] = " / ";
			strs[13] = podcastFeedEntries;
			strs[14] = " ";
			if (LanguageTool.getString("PODCAST_ENTRIES_COUNTER").isEmpty())
				strs[15] = LanguageTool.getString("PODCAST_ENTRIES");
			else
				strs[15] = LanguageTool.getString("PODCAST_ENTRIES_COUNTER");
			setRightStatusBarText(StringUtils.getString(strs));
		}
		//		else {
		//			Object[] strs = new Object[16];
		//			strs[0] = LanguageTool.getString("PODCAST_ENTRIES");
		//			strs[1] = " ";
		//			strs[2] = podcastFeedEntries;
		//			strs[3] = " / ";
		//			strs[4] = LanguageTool.getString("RADIOS");
		//			strs[5] = " ";
		//			strs[6] = radios;
		//			strs[7] = "/ ";
		//			strs[8] = " (";
		//			strs[9] = playList.getLength();
		//			strs[10] = ") ";
		//			strs[11] = LanguageTool.getString("SONGS_IN_REPOSITORY");
		//			strs[12] = " ";
		//			strs[13] = songs;
		//			strs[14] = " :";
		//			strs[15] = LanguageTool.getString("PLAYLIST");
		//			setRightStatusBarText(StringUtils.getString(strs));
		//		}

	}

	public void showProgressBar(boolean show) {
		frame.showProgressBar(show);
	}

	public void showPropertiesDialog(AudioObject file) {
		PropertiesDialog dialog = PropertiesDialog.newInstance(file);
		dialog.setVisible(true);
	}

	public void showRepositorySelectionInfoDialog() {
		new RepositorySelectionInfoDialog(frame.getFrame()).setVisible(true);
	}

	//TODO RTL component orientation
	public void showRepositorySongNumber(long size, long sizeInBytes, long duration) {
		if (true) {
			setCenterStatusBarText(StringUtils.getString(LanguageTool.getString("REPOSITORY"), ": ", size, " ", LanguageTool.getString("SONGS_IN_REPOSITORY"), " (", StringUtils
					.fromByteToMegaOrGiga(sizeInBytes), ") (", StringUtils.fromSecondsToHoursAndDays(duration), ")"));
		}
		//		else {
		//			setCenterStatusBarText(StringUtils.getString("(", StringUtils.fromSecondsToHoursAndDays(duration), ") (", StringUtils.fromByteToMegaOrGiga(sizeInBytes), ") ",
		//					LanguageTool.getString("SONGS_IN_REPOSITORY"), " ", size, " :", LanguageTool.getString("REPOSITORY")));
		//		}
	}

	public int showSaveDialog(JFileChooser fileChooser, FileFilter filter) {
		fileChooser.setFileFilter(filter);
		return fileChooser.showSaveDialog(frame.getFrame());
	}

	public void showSongProperties(boolean show, boolean update) {
		kernel.state.setShowSongProperties(show);
		frame.showSongProperties(show);
		ControllerProxy.getInstance().getMenuController().setShowSongProperties(show);
		ControllerProxy.getInstance().getToolBarController().setShowSongProperties(show);
		if (show && update)
			if (PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject() != null) {
				ControllerProxy.getInstance().getFilePropertiesController().updateValues(PlayerHandler.getInstance().getCurrentPlayList().getCurrentAudioObject());
			} else
				ControllerProxy.getInstance().getFilePropertiesController().onlyShowPropertiesPanel();
	}

	public void showSplashScreen() {
		if (Kernel.getInstance().state.isShowTitle()) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			splashScreenDialog = new SplashScreenDialog();
			JDialog.setDefaultLookAndFeelDecorated(true);
			splashScreenDialog.setVisible(true);
		}
	}

	public void showStatusBar(boolean show) {
		Kernel.getInstance().state.setShowStatusBar(show);
		frame.showStatusBar(show);
		repaint();
	}

	public void startVisualization() {
		logger.debug(LogCategories.START, "Starting visualization");

		frame.create();

		// Create drag and drop listener
		new DragAndDropListener();

		showProgressBar(false);
		showIconOnStatusBar(null, null);

		SwingUtilities.updateComponentTreeUI(frame.getFrame());
		logger.debug(LogCategories.START, "Start visualization done");
	}

	public void toggleWindowVisibility() {
		frame.setVisible(!frame.isVisible());
		frame.getFrame().toFront();
		frame.getFrame().setState(java.awt.Frame.NORMAL);
	}

	public void updatePlayState(PlayState state) {
		getPlayListTable().setPlayState(state);
	}

	public void updateStatusBar(AudioObject song) {
		if (GuiUtils.getComponentOrientation().isLeftToRight()) {
			String text = StringUtils.getString("<html>", LanguageTool.getString("PLAYING"), ": ");
			if (song instanceof Radio || song instanceof PodcastFeedEntry || ((AudioFile) song).getTag() == null)
				text = StringUtils.getString(text, "<b>", song.getTitleOrFileName(), "</b></html>");
			else {
				if (((AudioFile) song).getTag().getTitle() == null || ((AudioFile) song).getTag().getTitle().equals(""))
					text = StringUtils.getString(text, "<b>", ((AudioFile) song).getName(), "</b> - ");
				else
					text = StringUtils.getString(text, "<b>", ((AudioFile) song).getTag().getTitle(), "</b> - ");
				if (((AudioFile) song).getTag().getArtist() == null || ((AudioFile) song).getTag().getArtist().equals(""))
					text = StringUtils.getString(text, "<b>", LanguageTool.getString("UNKNOWN_ARTIST"), "</b> ");
				else
					text = StringUtils.getString(text, "<b>", ((AudioFile) song).getTag().getArtist(), "</b> ");
				text = StringUtils.getString(text, "(", TimeUtils.seconds2String(song.getDuration()), ")</html>");
			}
			setLeftStatusBarText(text);
		} else {
			String text = "";
			if (song instanceof Radio || song instanceof PodcastFeedEntry || ((AudioFile) song).getTag() == null) {
				text = StringUtils.getString("<html><b>", song.getTitleOrFileName(), "</b></html>");
			} else {
				text = StringUtils.getString("<html>(", TimeUtils.seconds2String(song.getDuration()), ") ");
				if (((AudioFile) song).getTag().getArtist() == null || ((AudioFile) song).getTag().getArtist().equals(""))
					text = StringUtils.getString(text, "<b>", LanguageTool.getString("UNKNOWN_ARTIST"), "</b> ");
				else
					text = StringUtils.getString(text, "<b>", ((AudioFile) song).getTag().getArtist(), "</b> ");
				if (((AudioFile) song).getTag().getTitle() == null || ((AudioFile) song).getTag().getTitle().equals(""))
					text = StringUtils.getString(text, " - <b>", ((AudioFile) song).getName(), "</b>");
				else
					text = StringUtils.getString(text, " - <b>", ((AudioFile) song).getTag().getTitle(), "</b>");
			}
			text = StringUtils.getString(text, " :", LanguageTool.getString("PLAYING"), "</html>");
			setLeftStatusBarText(text);
		}
	}

	public void updateStatusBar(String text) {
		setLeftStatusBarText(text);
	}

	public void updateTitleBar(AudioObject song) {
		if (song != null) {
			if (song instanceof Radio) {
				updateTitleBar(StringUtils.getString(((Radio) song).getName(), " (", ((Radio) song).getUrl(), ")"));
				return;
			}

			if (song instanceof PodcastFeedEntry) {
				updateTitleBar(StringUtils.getString(((PodcastFeedEntry) song).getName(), " (", ((PodcastFeedEntry) song).getUrl(), ")"));
				return;
			}

			StringBuilder strBuilder = new StringBuilder();
			if (((AudioFile) song).getTag() == null)
				strBuilder.append(((AudioFile) song).getName());
			else {
				strBuilder.append(song.getTitleOrFileName());
				strBuilder.append(" - ");
				strBuilder.append(song.getArtist());
				strBuilder.append(" - ");
				strBuilder.append(song.getAlbum());
			}
			strBuilder.append(" (");
			strBuilder.append(TimeUtils.seconds2String(song.getDuration()));
			strBuilder.append(")");
			updateTitleBar(strBuilder.toString());
		} else
			updateTitleBar("");
	}

	public void updateTitleBar(String text) {
		VisualHandler.getInstance().setTitleBar(text);
	}
}
