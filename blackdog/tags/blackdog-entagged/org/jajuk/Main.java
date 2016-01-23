/*
 * Jajuk Copyright (C) 2003 The Jajuk Team
 *
 * This program is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307,USA
 * $Revision: 3422 $
 */
package org.jajuk;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.vlsolutions.swing.docking.ui.DockingUISettings;
import com.vlsolutions.swing.toolbars.ToolBarContainer;

import ext.JSplash;
import ext.JVM;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jajuk.base.AlbumManager;
import org.jajuk.base.AuthorManager;
import org.jajuk.base.Collection;
import org.jajuk.base.Device;
import org.jajuk.base.DeviceManager;
import org.jajuk.base.DirectoryManager;
import org.jajuk.base.FileManager;
import org.jajuk.base.ItemManager;
import org.jajuk.base.PlaylistFileManager;
import org.jajuk.base.PlaylistManager;
import org.jajuk.base.StyleManager;
import org.jajuk.base.TrackManager;
import org.jajuk.base.Type;
import org.jajuk.base.TypeManager;
import org.jajuk.base.WebRadio;
import org.jajuk.base.YearManager;
import org.jajuk.services.bookmark.History;
import org.jajuk.services.core.RatingManager;
import org.jajuk.services.dj.AmbienceManager;
import org.jajuk.services.dj.DigitalDJManager;
import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.services.lastfm.LastFmManager;
import org.jajuk.services.players.FIFO;
import org.jajuk.services.players.Player;
import org.jajuk.services.webradio.WebRadioManager;
import org.jajuk.ui.actions.ActionBase;
import org.jajuk.ui.actions.ActionManager;
import org.jajuk.ui.actions.RestoreAllViewsAction;
import org.jajuk.ui.helpers.FontManager;
import org.jajuk.ui.helpers.FontManager.JajukFont;
import org.jajuk.ui.perspectives.PerspectiveManager;
import org.jajuk.ui.thumbnails.ThumbnailManager;
import org.jajuk.ui.thumbnails.ThumbnailsMaker;
import org.jajuk.ui.widgets.CommandJPanel;
import org.jajuk.ui.widgets.InformationJPanel;
import org.jajuk.ui.widgets.JajukJMenuBar;
import org.jajuk.ui.widgets.JajukSystray;
import org.jajuk.ui.widgets.JajukWindow;
import org.jajuk.ui.widgets.PerspectiveBarJPanel;
import org.jajuk.ui.wizard.FirstTimeWizard;
import org.jajuk.ui.wizard.TipOfTheDay;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.DownloadManager;
import org.jajuk.util.EventSubject;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.UpgradeManager;
import org.jajuk.util.Util;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.log.Log;

/**
 * Jajuk launching class
 */
public class Main implements ITechnicalStrings {

  /** Main window */
  private static JajukWindow jw;

  /** Top command panel */
  public static CommandJPanel command;

  /** Toolbar container that can be serialized */
  private static ToolBarContainer tbcontainer;

  /** Left side perspective selection panel */
  public static PerspectiveBarJPanel perspectiveBar;

  /** Lower information panel */
  public static InformationJPanel information;

  /** Main frame panel */
  public static JPanel jpFrame;

  /** splashscreen */
  public static JSplash sc;

  /** Exit code */
  private static int iExitCode = 0;

  /** Debug mode */
  public static boolean bIdeMode = false;

  /** Test mode */
  public static boolean bTestMode = false;

  /** Exiting flag */
  public static boolean bExiting = false;

  /** Jukebox power pack flag* */
  public static boolean bPowerPack = false;

  /**
   * Thumb maker flag, true if this class is executed from the Thumb maker
   * process *
   */
  public static boolean bThumbMaker = false;

  /** Systray */
  private static JajukSystray jsystray;

  /** UI lauched flag */
  private static boolean bUILauched = false;

  /** default perspective to shoose, if null, we take the configuration one */
  private static String sPerspective;

  /** Server socket used to check other sessions */
  private static ServerSocket ss;

  /** Is it a minor or major X.Y upgrade */
  private static boolean bUpgraded = false;

  /** Is it the first session ever ? */
  public static boolean bFirstSession = false;

  /** Does this session follows a crash revover ? */
  private static boolean bCrashRecover = false;

  /** Lock used to trigger a first time wizard device creation and refresh * */
  public static short[] canLaunchRefresh = new short[0];

  /** Lock used to trigger first time wizard window close* */
  public static short[] isFirstTimeWizardClosed = new short[0];

  /** Mplayer state */
  private static MPlayerStatus mplayerStatus;

  /** Workspace PATH* */
  public static String workspace;

  /** MPlayer status possible values * */
  public static enum MPlayerStatus {
    MPLAYER_STATUS_OK, MPLAYER_STATUS_NOT_FOUND, MPLAYER_STATUS_WRONG_VERSION, MPLAYER_STATUS_JNLP_DOWNLOAD_PBM
  }

  /** ConfigurationManager Locales */
  public static final String[] locales = { "en", "fr", "de", "it", "sv", "nl", "zh", "es", "ca",
      "ko", "el", "ru" };

  /** DeviceTypes Identification strings */
  public static final String[] deviceTypes = { "Device_type.directory", "Device_type.file_cd",
      "Device_type.network_drive", "Device_type.extdd", "Device_type.player" };

  public static final String[] configChecks = { FILE_CONFIGURATION, FILE_HISTORY };

  public static final String[] dirChecks = {
      // internal pictures cache directory
      FILE_CACHE + '/' + FILE_INTERNAL_CACHE,
      // thumbnails directories and sub-directories
      FILE_THUMBS, FILE_THUMBS + "/" + THUMBNAIL_SIZE_50x50,
      FILE_THUMBS + "/" + THUMBNAIL_SIZE_100x100, FILE_THUMBS + "/" + THUMBNAIL_SIZE_150x150,
      FILE_THUMBS + "/" + THUMBNAIL_SIZE_200x200, FILE_THUMBS + "/" + THUMBNAIL_SIZE_250x250,
      FILE_THUMBS + "/" + THUMBNAIL_SIZE_300x300,
      // DJs directories
      FILE_DJ_DIR };

  /**
   * Main entry
   * 
   * @param args
   */
  public static void main(final String[] args) {
    // non ui init
    try {
      // check JVM version
      if (!JVM.current().isOrLater(JVM.JDK1_5)) {
        System.out.println("Java Runtime Environment 1.5 minimum required." + " You use a JVM "
            + JVM.current());
        System.exit(2); // error code 2 : wrong JVM
      }
      // set command line options
      for (final String element : args) {
        // Tells jajuk it is inside the IDE (useful to find right
        // location for images and jar resources)
        if (element.equals("-" + CLI_IDE)) {
          bIdeMode = true;
        }
        // Tells jajuk to use a .jajuk_test repository
        // The information can be given from CLI using
        // -test=[test|notest] option
        // or using the "test" env variable
        final String test = System.getProperty("test");
        if (element.equals("-" + CLI_TEST) || ((test != null) && test.equals("test"))) {
          bTestMode = true;
        }
        if (element.equals("-" + CLI_POWER_PACK)) {
          bPowerPack = true;
        }
      }
      // perform initial checkups and create needed files
      initialCheckups();

      // log startup depends on : setExecLocation, initialCheckups
      Log.getInstance();
      Log.setVerbosity(Log.DEBUG);

      // Register locals, needed by ConfigurationManager to choose
      // default language
      for (final String locale : locales) {
        Messages.getInstance().registerLocal(locale);
      }

      // Configuration manager startup. Depends on: initialCheckups,
      // registerLocal
      ConfigurationManager.getInstance();

      // Load user configuration. Depends on: initialCheckups
      ConfigurationManager.load();

      // Upgrade detection. Depends on: Configuration manager load
      final String sRelease = ConfigurationManager.getProperty(CONF_RELEASE);

      // check if it is a new major 'x.y' release: 1.2 != 1.3 for instance
      if (!bFirstSession
      // if first session, not taken as an upgrade
          && ((sRelease == null) || // null for jajuk releases < 1.2
          !sRelease.substring(0, 3).equals(JAJUK_VERSION.substring(0, 3)))) {
        bUpgraded = true;
      }
      // Now set current release in the conf
      ConfigurationManager.setProperty(CONF_RELEASE, JAJUK_VERSION);

      // Set actual log verbosity. Depends on:
      // ConfigurationManager.load
      if (!bTestMode) {
        // test mode is always in debug mode
        Log
            .setVerbosity(Integer
                .parseInt(ConfigurationManager.getProperty(CONF_OPTIONS_LOG_LEVEL)));
      }
      // Set locale. setSystemLocal
      Messages.getInstance().setLocal(ConfigurationManager.getProperty(CONF_OPTIONS_LANGUAGE));

      // Launch splashscreen. Depends on: log.setVerbosity,
      // configurationManager.load (for local)
      SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          // Set default fonts
          FontManager.getInstance().setDefaultFont();

          // Set window look and feel and watermarks
          Util.setLookAndFeel(ConfigurationManager.getProperty(CONF_OPTIONS_LNF));

          sc = new JSplash(IMAGES_SPLASHSCREEN, true, true, false, JAJUK_COPYRIGHT, JAJUK_VERSION
              + " \"" + JAJUK_CODENAME + "\"" + " " + JAJUK_VERSION_DATE, FontManager.getInstance()
              .getFont(JajukFont.SPLASH), null);
          sc.setTitle(Messages.getString("JajukWindow.3"));
          sc.setProgress(0, Messages.getString("SplashScreen.0"));
          // Actually show the splashscreen only if required
          if (ConfigurationManager.getBoolean(CONF_UI_SHOW_AT_STARTUP)) {
            sc.splashOn();
          }
        }
      });

      // Apply any proxy (requires load conf)
      DownloadManager.setDefaultProxySettings();

      // Registers ItemManager managers
      ItemManager.registerItemManager(org.jajuk.base.Album.class, AlbumManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Author.class, AuthorManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Device.class, DeviceManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.File.class, FileManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Directory.class, DirectoryManager
          .getInstance());
      ItemManager.registerItemManager(org.jajuk.base.PlaylistFile.class, PlaylistFileManager
          .getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Playlist.class, PlaylistManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Style.class, StyleManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Track.class, TrackManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Type.class, TypeManager.getInstance());
      ItemManager.registerItemManager(org.jajuk.base.Year.class, YearManager.getInstance());

      // Upgrade configuration from previous releases
      UpgradeManager.upgradeStep1();

      // Display user system configuration
      Log.debug("Workspace used: " + workspace);
      Log.debug(Util.getAnonymizedSystemProperties().toString());

      // Display user Jajuk configuration
      Log.debug(Util.getAnonymizedJajukProperties().toString());

      // check for another session (needs setLocal)
      checkOtherSession();

      // Set a session file
      final File sessionUser = Util.getConfFileByPath(FILE_SESSIONS + '/' + Util.getHostName()
          + '_' + System.getProperty("user.name"));
      sessionUser.mkdir();

      // Register device types
      for (final String deviceTypeId : deviceTypes) {
        DeviceManager.getInstance().registerDeviceType(Messages.getString(deviceTypeId));
      }
      // registers supported audio supports and default properties
      registerTypes();

      // Display progress
      // sc can be null if not already loaded. Done for perfs
      sc.setProgress(10, Messages.getString("SplashScreen.1"));

      // Start operations that can be done asynchronously during
      // collection loading
      startupAsyncBeforeCollectionLoad();

      // Load collection
      loadCollection();

      // Display progress
      sc.setProgress(70, Messages.getString("SplashScreen.2"));

      // Load history
      History.load();

      // Load ambiences
      AmbienceManager.getInstance().load();

      // Start LastFM support
      LastFmManager.getInstance();

      // Load djs
      DigitalDJManager.getInstance().loadAllDJs();

      // Various asynchronous startup actions that needs collection load
      startupAsyncAfterCollectionLoad();

      // Auto mount devices, freeze for SMB drives
      // if network is not reacheable
      // Do not start this if first session, it is causes concurrency with
      // first refresh thread
      if (!bFirstSession) {
        autoMount();
      }

      // Create automatically a Music device if we are packaging a
      // JukeboxPowerPack distribution
      powerPack();

      // Launch startup track if any (but don't start it if firsdt session
      // because the first refresh is probably still running)
      if (!bFirstSession) {
        launchInitialTrack();
      }

      // Start up action manager. To be done before launching ui and
      // tray
      ActionManager.getInstance();

      // show window if set in the systray conf.
      if (ConfigurationManager.getBoolean(CONF_UI_SHOW_AT_STARTUP)) {
        // Display progress
        sc.setProgress(80, Messages.getString("SplashScreen.3"));
        launchUI();
      }

      // start the tray
      launchTray();
    } catch (final JajukException je) { // last chance to catch any error for
      // logging purpose
      Log.error(je);
      if (je.getCode() == 5) {
        Messages.getChoice(Messages.getErrorMessage(5), JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE);
        exit(1);
      }
    } catch (final Exception e) { // last chance to catch any error for logging
      // purpose
      e.printStackTrace();
      Log.error(106, e);
      exit(1);
    } catch (final Error error) { // last chance to catch any error for logging
      // purpose
      error.printStackTrace();
      Log.error(106, error);
      exit(1);
    } finally { // make sure to close splashscreen in all cases (ie if
      // UI is not started)
      if (!ConfigurationManager.getBoolean(CONF_UI_SHOW_AT_STARTUP) && (sc != null)) {
        sc.setProgress(100);
        sc.splashOff();
      }
    }
  }

  /**
   * Performs some basic startup tests
   * 
   * @throws Exception
   */
  public static void initialCheckups() throws Exception {
    // Check for bootstrap file presence
    final File bootstrap = new File(FILE_BOOTSTRAP);
    // Default workspace: ~/.jajuk
    final File fDefaultWorkspace = Util.getConfFileByPath("");
    if (bootstrap.canRead()) {
      try {
        final BufferedReader br = new BufferedReader(new FileReader(bootstrap));
        // Bootstrap file should contain a single line containing the
        // path to jajuk workspace
        final String sPath = br.readLine();
        br.close();
        // Check if the repository can be found
        if (new File(sPath + '/' + (Main.bTestMode ? ".jajuk_test_" + TEST_VERSION : ".jajuk"))
            .canRead()) {
          Main.workspace = sPath;
        }
      } catch (final Exception e) {
        System.out.println("Cannot read bootstrap file, using ~ directory");
        Main.workspace = System.getProperty("user.home");
      }
    }
    // No bootstrap or unreadable or the path included inside is not
    // readable, show a wizard to select it
    if ((!bootstrap.canRead() || (Main.workspace == null))
    // don't launch the first time wizard if a previous release .jajuk dir
        // exists (upgrade from < 1.4)
        && !fDefaultWorkspace.canRead()) {
      // First time session ever
      bFirstSession = true;
      // display the first time wizard
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          new FirstTimeWizard();
        }
      });
      // Lock until first time wizard is closed
      synchronized (isFirstTimeWizardClosed) {
        isFirstTimeWizardClosed.wait();
      }
    }
    // In all cases, make sure to set a workspace
    if (workspace == null) {
      workspace = System.getProperty("user.home");
    }
    // check for jajuk directory
    final File fWorkspace = new File(workspace);
    if (!fWorkspace.exists()) {
      fWorkspace.mkdirs(); // create the directory if it doesn't exist
    }
    // check for image cache presence and create the workspace/.jajuk
    // directory
    final File fCache = Util.getConfFileByPath(FILE_CACHE);
    if (!fCache.exists()) {
      fCache.mkdirs();
    } else {
      // Empty cache
      final File[] cacheFiles = fCache.listFiles();
      for (final File element : cacheFiles) {
        element.delete();
      }
    }

    // checking required internal configuration files
    for (final String check : configChecks) {
      final File file = Util.getConfFileByPath(check);

      if (file.exists() == false) {
        // if config file doesn't exit, create
        // it with default values
        org.jajuk.util.ConfigurationManager.commit();
      }
    }

    // checking required internal directories
    for (final String check : dirChecks) {
      final File file = Util.getConfFileByPath(check);

      if ((file.exists() == false) && (file.mkdir() == false)) {
        Log.warn("Could not create missing required directory [" + check + "]");
      }
    }

    // Extract star icons (used by some HTML panels)
    for (int i = 1; i <= 4; i++) {
      final File star = Util.getConfFileByPath("cache/internal/star" + i + "_16x16.png");
      if (!star.exists()) {
        ImageIcon ii = null;
        switch (i) {
        case 1:
          ii = IconLoader.ICON_STAR_1;
          break;
        case 2:
          ii = IconLoader.ICON_STAR_2;
          break;
        case 3:
          ii = IconLoader.ICON_STAR_3;
          break;
        case 4:
          ii = IconLoader.ICON_STAR_4;
          break;
        }
        Util.extractImage(ii.getImage(), star);
      }
    }
  }

  /**
   * Asynchronous tasks executed at startup at the same time (for perf)
   */
  private static void startupAsyncAfterCollectionLoad() {
    new Thread("Startup Async After Collection Load Thread") {
      @Override
      public void run() {
        try {
          // start exit hook
          final Thread tHook = new Thread("Exit hook thread") {
            @Override
            public void run() {
              Log.debug("Exit Hook begin");
              try {
                SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    // TODO Auto-generated method stub
                    // commit perspectives if no full restore
                    // engaged. Perspective should be commited before the window
                    // being closed to avoid a dead lock in VLDocking
                    if (!RestoreAllViewsAction.fullRestore) {
                      try {
                        PerspectiveManager.commit();
                      } catch (Exception e) {
                        Log.error(e);
                      }
                    }
                    // hide window ASAP
                    if (getWindow() != null) {
                      getWindow().setVisible(false);
                    }
                    // hide systray
                    if (jsystray != null) {
                      jsystray.closeSystray();
                    }
                  }
                });
                Player.stop(true); // stop sound ASAP
              } catch (Exception e) {
                e.printStackTrace();
                // no log to make sure to reach collection
                // commit
              }
              try {
                if (iExitCode == 0) {
                  // Store current FIFO for next session
                  FIFO.getInstance().commit();
                  // commit only if exit is safe (to avoid
                  // commiting
                  // empty collection) commit ambiences
                  AmbienceManager.getInstance().commit();
                  // Commit webradios
                  WebRadioManager.getInstance().commit();
                  // Store webradio state
                  ConfigurationManager.setProperty(CONF_WEBRADIO_WAS_PLAYING, Boolean.toString(FIFO
                      .getInstance().isPlayingRadio()));
                  // commit configuration
                  org.jajuk.util.ConfigurationManager.commit();
                  // commit history
                  History.commit();
                  // Commit collection if not refreshing
                  if (!DeviceManager.getInstance().isAnyDeviceRefreshing()) {
                    Collection.commit(Util.getConfFileByPath(FILE_COLLECTION_EXIT));
                    // create a proof file
                    Util.createEmptyFile(Util.getConfFileByPath(FILE_COLLECTION_EXIT_PROOF));
                  }
                  /* release keystrokes resources */
                  ActionBase.cleanup();

                  // Remove localhost_<user> session files
                  // (can occur when network is not available)
                  File sessionUser = Util.getConfFileByPath(FILE_SESSIONS + "/localhost" + '_'
                      + System.getProperty("user.name"));
                  sessionUser.delete();
                  // Remove session flag. Exception can be
                  // thrown here if loopback interface is not
                  // correctly set up, so should be the last
                  // thing to do
                  sessionUser = Util.getConfFileByPath(FILE_SESSIONS + '/'
                      + InetAddress.getLocalHost().getHostName() + '_'
                      + System.getProperty("user.name"));
                  sessionUser.delete();

                }
              } catch (Exception e) {
                // don't use Log class here, it can cause freeze
                // if
                // workspace no more available
                e.printStackTrace();
              } finally {
                // don't use Log class here, it can cause freeze
                // if workspace is no more available
                System.out.println("Exit Hook end");
              }
            }
          };
          tHook.setPriority(Thread.MAX_PRIORITY);
          Runtime.getRuntime().addShutdownHook(tHook);

          // Clean the collection up
          Collection.cleanup();

          // Refresh max album rating
          AlbumManager.getInstance().refreshMaxRating();

          // Launch auto-refresh thread
          DeviceManager.getInstance().startAutoRefreshThread();

          // Start rating manager thread
          RatingManager.getInstance().start();

          // Force rebuilding thumbs (after an album id hashcode
          // method change for eg)
          if (Collection.getInstance().hmWrongRightAlbumID.size() > 0) {
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_50x50);
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_100x100);
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_150x150);
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_200x200);
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_250x250);
            ThumbnailManager.cleanThumbs(THUMBNAIL_SIZE_300x300);
            // Launch thumbs creation in another process
            ThumbnailsMaker.launchAllSizes(true);
          }
        } catch (final Exception e) {
          Log.error(e);
        }
      }
    }.start();
  }

  /**
   * Asynchronous tasks executed at startup at the same time (for perf)
   */
  private static void startupAsyncBeforeCollectionLoad() {
    new Thread("Startup Async Before Collection Load Thread") {
      @Override
      public void run() {
        // Force loading all icons now
        IconLoader.ICON_ACCURACY_HIGH.toString();
      }
    }.start();
  }

  /**
   * Registers supported audio supports and default properties
   */
  public static void registerTypes() {
    try {
      // test mplayer presence in PATH
      mplayerStatus = MPlayerStatus.MPLAYER_STATUS_OK;
      if (Util.isUnderWindows()) {
        final File mplayerPath = Util.getMPlayerWindowsPath();
        // try to find mplayer executable in know locations first
        if (mplayerPath == null) {
          try {
            sc.setProgress(5, Messages.getString("Main.22"));
            Log.debug("Download Mplayer from: " + URL_MPLAYER); //$NON-NLS-1$
            File fMPlayer = Util.getConfFileByPath(FILE_MPLAYER_EXE);
            DownloadManager.download(new URL(URL_MPLAYER), fMPlayer);
            // make sure to delete corrupted mplayer in case of
            // download problem
            if (fMPlayer.length() != MPLAYER_EXE_SIZE) {
              fMPlayer.delete();
              throw new Exception("MPlayer corrupted"); //$NON-NLS-1$
            }
          } catch (Exception e) {
            mplayerStatus = MPlayerStatus.MPLAYER_STATUS_JNLP_DOWNLOAD_PBM;
          }
        }
      }
      // Under non-windows OS, we assume mplayer has been installed
      // using external standard distributions
      else {
        // If a forced mplayer path is defined, test it
        final String forced = ConfigurationManager.getProperty(CONF_MPLAYER_PATH_FORCED);
        if (!Util.isVoid(forced)) {
          // Test forced path
          mplayerStatus = Util.getMplayerStatus(forced);
        } else {
          mplayerStatus = MPlayerStatus.MPLAYER_STATUS_NOT_FOUND;
        }
        if (mplayerStatus != MPlayerStatus.MPLAYER_STATUS_OK) {
          // try to find a correct mplayer from the path
          // Under OSX, it will work only if jajuk is launched from
          // command line
          mplayerStatus = Util.getMplayerStatus("");
          if (mplayerStatus != MPlayerStatus.MPLAYER_STATUS_OK) {
            // OK, try to find MPlayer in standards OSX directories
            if (Util.isUnderOSXpower()) {
              mplayerStatus = Util.getMplayerStatus(FILE_DEFAULT_MPLAYER_POWER_OSX_PATH);
            } else {
              mplayerStatus = Util.getMplayerStatus(FILE_DEFAULT_MPLAYER_X86_OSX_PATH);
            }
          }
        }
      }
      // Choose player according to mplayer presence or not
      if (mplayerStatus != MPlayerStatus.MPLAYER_STATUS_OK) {
        Log.debug("Mplayer status=" + mplayerStatus);
        // No mplayer, show mplayer warnings
        if (mplayerStatus != MPlayerStatus.MPLAYER_STATUS_OK) {
          // Test if user didn't already select "don't show again"
          if (!ConfigurationManager.getBoolean(CONF_NOT_SHOW_AGAIN_PLAYER)) {
            if (mplayerStatus == MPlayerStatus.MPLAYER_STATUS_NOT_FOUND) {
              // No mplayer
              Messages.showHideableWarningMessage(Messages.getString("Warning.0"), //$NON-NLS-1$
                  CONF_NOT_SHOW_AGAIN_PLAYER);
            } else if (mplayerStatus == MPlayerStatus.MPLAYER_STATUS_WRONG_VERSION) {
              // wrong mplayer release
              Messages.showHideableWarningMessage(Messages.getString("Warning.1"), //$NON-NLS-1$
                  CONF_NOT_SHOW_AGAIN_PLAYER);
            }
          } else if (mplayerStatus == MPlayerStatus.MPLAYER_STATUS_JNLP_DOWNLOAD_PBM) {
            // wrong mplayer release
            Messages.showHideableWarningMessage(Messages.getString("Warning.3"), //$NON-NLS-1$
                CONF_NOT_SHOW_AGAIN_PLAYER);
          }
        }
        // mp3
        Type type = TypeManager.getInstance().registerType(Messages.getString("Type.mp3"), EXT_MP3,
            Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_MP3);
        // Do not use IconLoader to get icon, it takes too much time to
        // load all icons
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_mp3_16x16.png")
            .toExternalForm());
        // playlists
        type = TypeManager.getInstance().registerType(Messages.getString("Type.playlist"),
            EXT_PLAYLIST, Class.forName(PLAYER_IMPL_JAVALAYER), null);
        type.setProperty(XML_TYPE_IS_MUSIC, false);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, false);
        // Ogg vorbis
        type = TypeManager.getInstance().registerType(Messages.getString("Type.ogg"), EXT_OGG,
            Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, false);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_OGG);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_ogg_16x16.png")
            .toExternalForm());
        // Wave
        type = TypeManager.getInstance().registerType(Messages.getString("Type.wav"), EXT_WAV,
            Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_NO_TAGS));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_WAVE);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_wav_16x16.png")
            .toExternalForm());
        // au
        type = TypeManager.getInstance().registerType(Messages.getString("Type.au"), EXT_AU,
            Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_NO_TAGS));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, false);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_AU);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_wav_16x16.png")
            .toExternalForm());
      } else { // mplayer enabled
        // mp3
        Type type = TypeManager.getInstance().registerType(Messages.getString("Type.mp3"), EXT_MP3,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_MP3);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_mp3_16x16.png")
            .toExternalForm());
        // playlists
        type = TypeManager.getInstance().registerType(Messages.getString("Type.playlist"),
            EXT_PLAYLIST, Class.forName(PLAYER_IMPL_JAVALAYER), null);
        type.setProperty(XML_TYPE_IS_MUSIC, false);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, false);
        // Ogg vorbis
        type = TypeManager.getInstance().registerType(Messages.getString("Type.ogg"), EXT_OGG,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_OGG);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_ogg_16x16.png")
            .toExternalForm());
        // Wave
        type = TypeManager.getInstance().registerType(Messages.getString("Type.wav"), EXT_WAV,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_NO_TAGS));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_WAVE);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_wav_16x16.png")
            .toExternalForm());
        // au
        type = TypeManager.getInstance().registerType(Messages.getString("Type.au"), EXT_AU,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_NO_TAGS));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_AU);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_wav_16x16.png")
            .toExternalForm());
        // flac
        type = TypeManager.getInstance().registerType(Messages.getString("Type.flac"), EXT_FLAC,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_FLAC);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_flac_16x16.png")
            .toExternalForm());
        // WMA
        type = TypeManager.getInstance().registerType(Messages.getString("Type.wma"), EXT_WMA,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_WMA);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_wma_16x16.png")
            .toExternalForm());
        // AAC
        type = TypeManager.getInstance().registerType(Messages.getString("Type.aac"), EXT_AAC,
            Class.forName(PLAYER_IMPL_MPLAYER), null);
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_AAC);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_aac_16x16.png")
            .toExternalForm());
        // M4A (=AAC)
        type = TypeManager.getInstance().registerType(Messages.getString("Type.aac"), EXT_M4A,
            Class.forName(PLAYER_IMPL_MPLAYER), null);
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_AAC);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_aac_16x16.png")
            .toExternalForm());
        // Real audio
        type = TypeManager.getInstance().registerType(Messages.getString("Type.real"), EXT_REAL,
            Class.forName(PLAYER_IMPL_MPLAYER), null);
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_RAM);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_ram_16x16.png")
            .toExternalForm());
        // mp2
        type = TypeManager.getInstance().registerType(Messages.getString("Type.mp2"), EXT_MP2,
            Class.forName(PLAYER_IMPL_MPLAYER), Class.forName(TAG_IMPL_ENTAGGED));
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_MP2);
        type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_mp2_16x16.png")
            .toExternalForm());
        // web radios
        type = TypeManager.getInstance().registerType(Messages.getString("Type.radio"), EXT_RADIO,
            Class.forName(PLAYER_IMPL_WEBRADIOS), null);
        type.setProperty(XML_TYPE_IS_MUSIC, true);
        type.setProperty(XML_TYPE_SEEK_SUPPORTED, true);
        type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_RADIO);
      }
      // Types not only supported by mplayer but supported by basicplayer
      // APE
      Type type = TypeManager.getInstance().registerType(Messages.getString("Type.ape"), EXT_APE,
          Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_ENTAGGED));
      type.setProperty(XML_TYPE_IS_MUSIC, true);
      type.setProperty(XML_TYPE_SEEK_SUPPORTED, TRUE);
      type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_APE);
      type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_ape_16x16.png")
          .toExternalForm());
      // MAC
      type = TypeManager.getInstance().registerType(Messages.getString("Type.mac"), EXT_MAC,
          Class.forName(PLAYER_IMPL_JAVALAYER), Class.forName(TAG_IMPL_ENTAGGED));
      type.setProperty(XML_TYPE_IS_MUSIC, true);
      type.setProperty(XML_TYPE_SEEK_SUPPORTED, TRUE);
      type.setProperty(XML_TYPE_TECH_DESC, TYPE_PROPERTY_TECH_DESC_APE);
      type.setProperty(XML_TYPE_ICON, Util.getResource("icons/16x16/type_ape_16x16.png")
          .toExternalForm());
    } catch (final Exception e1) {
      Log.error(26, e1);
    }
  }

  /**
   * check if another session is already started
   * 
   */
  private static void checkOtherSession() {
    // check for a concurrent jajuk session on local box, try to create a
    // new server socket
    try {
      ss = new ServerSocket(Main.bTestMode ? PORT_TEST : PORT);
      // No error? jajuk was not started, leave
    } catch (final IOException e) { // error? looks like Jajuk is already
      // started
      if (sc != null) {
        sc.dispose();
      }
      Log.error(124);
      Messages.getChoice(Messages.getErrorMessage(124), JOptionPane.DEFAULT_OPTION,
          JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
    // start listening
    new Thread("Concurrent Session Avoidance Thread") {
      @Override
      public void run() {
        try {
          ss.accept();
        } catch (final IOException e) {
          Log.error(e);
        }
      }
    }.start();
    // Now check for remote concurrent users using the same configuration
    // files
    // Create concurrent session directory if needed
    final File sessions = Util.getConfFileByPath(FILE_SESSIONS);
    if (!sessions.exists()) {
      sessions.mkdir();
    }
    // Check for concurrent session
    final File[] files = sessions.listFiles();
    String sHostname;
    try {
      sHostname = InetAddress.getLocalHost().getHostName();
    } catch (final UnknownHostException e) {
      sHostname = "";
    }
    // display a warning if sessions directory contains some others users
    // We ignore presence of ourself session id that can be caused by a
    // crash
    if ((files.length > 0)
        && !((files.length == 1) && files[0].getName().equals(
            sHostname + '_' + System.getProperty("user.name")))) {
      Messages.showHideableWarningMessage(Messages.getString("Warning.2"),
          CONF_NOT_SHOW_AGAIN_CONCURRENT_SESSION);
    }
  }

  /**
   * Exit code, then system will execute the exit hook
   * 
   * @param iExitCode
   *          exit code
   *          <p>
   *          0 : normal exit
   *          <p>
   *          1: unexpected error
   */
  public static void exit(final int iExitCode) {
    // set exiting flag
    bExiting = true;
    // store exit code to be read by the system hook
    Main.iExitCode = iExitCode;
    // display a message
    Log.debug("Exit with code: " + iExitCode);
    System.exit(iExitCode);
  }

  /**
   * Load persisted collection file
   */
  private static void loadCollection() {
    if (bFirstSession) {
      Log.info("First session, collection will be created");
      return;
    }
    final File fCollection = Util.getConfFileByPath(FILE_COLLECTION);
    final File fCollectionExit = Util.getConfFileByPath(FILE_COLLECTION_EXIT);
    final File fCollectionExitProof = Util.getConfFileByPath(FILE_COLLECTION_EXIT_PROOF);
    // check if previous exit was OK
    boolean bParsingOK = true;
    try {
      if (fCollectionExit.exists() && fCollectionExitProof.exists()) {
        fCollectionExitProof.delete(); // delete this file created just
        // after collection exit commit
        Collection.load(Util.getConfFileByPath(FILE_COLLECTION_EXIT));
        // Remove the collection (required by renameTo next line under
        // Windows)
        fCollection.delete();
        // parsing of collection exit ok, use this collection file as
        // final collection
        if (!fCollectionExit.renameTo(fCollection)) {
          Log.warn("Cannot rename collection file");
        }
        // backup the collection
        Util.backupFile(Util.getConfFileByPath(FILE_COLLECTION), ConfigurationManager
            .getInt(CONF_BACKUP_SIZE));
      } else {
        bCrashRecover = true;
        throw new JajukException(5);
      }
    } catch (final Exception e) {
      Log.error(5, fCollectionExit.getAbsolutePath(), e);
      Log
          .debug("Jajuk was not closed properly during previous session, try to load previous collection file");
      if (fCollectionExit.exists()) {
        fCollectionExit.delete();
      }
      try {
        // try to load "official" collection file, should be OK but not
        // always up-to-date
        Collection.load(Util.getConfFileByPath(FILE_COLLECTION));
      } catch (final Exception e2) {
        // not better? strange...
        Log.error(5, fCollection.getAbsolutePath(), e2);
        bParsingOK = false;
      }
    }
    if (!bParsingOK) { // even final collection file parsing failed
      // (very unlikely), try to restore a backup file
      final File[] fBackups = Util.getConfFileByPath("").listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          if (name.indexOf("backup") != -1) {
            return true;
          }
          return false;
        }
      });
      final ArrayList<File> alBackupFiles = new ArrayList<File>(Arrays.asList(fBackups));
      Collections.sort(alBackupFiles); // sort alphabetically (newest
      // last)
      Collections.reverse(alBackupFiles); // newest first now
      final Iterator<File> it = alBackupFiles.iterator();
      // parse all backup files, newest first
      while (!bParsingOK && it.hasNext()) {
        final File file = it.next();
        try {
          Collection.load(file);
          bParsingOK = true;
          final int i = Messages.getChoice(Messages.getString("Error.133") + ":\n"
              + file.getAbsolutePath(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
          if (i == JOptionPane.CANCEL_OPTION) {
            System.exit(-1);
          }
          break;
        } catch (final Exception e2) {
          Log.error(5, file.getAbsolutePath(), e2);
        }
      }
      if (!bParsingOK) { // not better? ok, commit and load a void
        // collection
        Collection.cleanup();
        DeviceManager.getInstance().cleanAllDevices();
        System.gc();
        try {
          Collection.commit(Util.getConfFileByPath(FILE_COLLECTION));
        } catch (final Exception e2) {
          Log.error(e2);
        }
      }
    }
  }

  /**
   * Launch initial track at startup
   */
  private static void launchInitialTrack() {
    List<org.jajuk.base.File> alToPlay = new ArrayList<org.jajuk.base.File>();
    org.jajuk.base.File fileToPlay = null;
    if (!ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_NOTHING)) {
      if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_LAST)
          || ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_LAST_KEEP_POS)
          || ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_FILE)) {

        if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_FILE)) {
          fileToPlay = FileManager.getInstance().getFileByID(
              ConfigurationManager.getProperty(CONF_STARTUP_FILE));
        } else {
          // If we were playing a webradio when leaving, launch it
          if (ConfigurationManager.getBoolean(CONF_WEBRADIO_WAS_PLAYING)) {
            final WebRadio radio = WebRadioManager.getInstance().getWebRadioByName(
                ConfigurationManager.getProperty(CONF_DEFAULT_WEB_RADIO));
            if (radio != null) {
              new Thread("WebRadio launch thread") {
                @Override
                public void run() {
                  FIFO.getInstance().launchRadio(radio);
                }
              }.start();
            }
            return;
          }
          // last file from beginning or last file keep position
          else if (ConfigurationManager.getBoolean(CONF_STATE_WAS_PLAYING)
              && (History.getInstance().getHistory().size() > 0)) {
            // make sure user didn't exit jajuk in the stopped state
            // and that history is not void
            fileToPlay = FileManager.getInstance().getFileByID(History.getInstance().getLastFile());
          } else {
            // do not try to launch anything, stay in stop state
            return;
          }
        }
        if (fileToPlay != null) {
          if (fileToPlay.isReady()) {
            // we try to launch at startup only existing and mounted
            // files
            alToPlay.add(fileToPlay);
          } else {
            // file exists but is not mounted, just notify the error
            // without anoying dialog at each startup try to mount
            // device
            Log.debug("Startup file located on an unmounted device" + ", try to mount it");
            try {
              fileToPlay.getDevice().mount(true);
              Log.debug("Mount OK");
              alToPlay.add(fileToPlay);
            } catch (final Exception e) {
              Log.debug("Mount failed");
              final Properties pDetail = new Properties();
              pDetail.put(DETAIL_CONTENT, fileToPlay);
              pDetail.put(DETAIL_REASON, "010");
              ObservationManager.notify(new Event(EventSubject.EVENT_PLAY_ERROR, pDetail));
              FIFO.setFirstFile(false); // no more first file
            }
          }
        } else {
          // file no more exists
          Messages.getChoice(Messages.getErrorMessage(23), JOptionPane.DEFAULT_OPTION,
              JOptionPane.WARNING_MESSAGE);
          FIFO.setFirstFile(false);
          // no more first file
          return;
        }
        // For last tracks playing, add all ready files from last
        // session stored FIFO
        if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_LAST)
            || ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(
                STARTUP_MODE_LAST_KEEP_POS)) {
          final File fifo = Util.getConfFileByPath(FILE_FIFO);
          if (!fifo.exists()) {
            Log.debug("No fifo file");
          } else {
            try {
              final BufferedReader br = new BufferedReader(new FileReader(Util
                  .getConfFileByPath(FILE_FIFO)));
              String s = null;
              for (; (s = br.readLine()) != null;) {
                final org.jajuk.base.File file = FileManager.getInstance().getFileByID(s);
                if ((file != null) && file.isReady()) {
                  alToPlay.add(file);
                }
              }
              br.close();
            } catch (final IOException ioe) {
              Log.error(ioe);
            }
          }
        }
      } else if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_SHUFFLE)) {
        alToPlay = FileManager.getInstance().getGlobalShufflePlaylist();
      } else if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_BESTOF)) {
        alToPlay = FileManager.getInstance().getGlobalBestofPlaylist();
      } else if (ConfigurationManager.getProperty(CONF_STARTUP_MODE).equals(STARTUP_MODE_NOVELTIES)) {
        alToPlay = FileManager.getInstance().getGlobalNoveltiesPlaylist();
        if ((alToPlay != null) && (alToPlay.size() > 0)) {
          // shuffle the selection
          Collections.shuffle(alToPlay, new Random());
        } else {
          // Alert user that no novelties have been found
          InformationJPanel.getInstance().setMessage(Messages.getString("Error.127"),
              InformationJPanel.ERROR);
        }
      }
      // launch selected file
      if ((alToPlay != null) && (alToPlay.size() > 0)) {
        FIFO.getInstance().push(
            Util.createStackItems(alToPlay, ConfigurationManager.getBoolean(CONF_STATE_REPEAT),
                false), false);
      }
    }
  }

  /**
   * Auto-Mount required devices
   * 
   */
  public static void autoMount() {
    for (final Device device : DeviceManager.getInstance().getDevices()) {
      if (device.getBooleanValue(XML_DEVICE_AUTO_MOUNT)) {
        try {
          device.mount();
        } catch (final Exception e) {
          Log.error(112, device.getName(), e);
          // show a confirm dialog if the device can't be mounted,
          // we can't use regular Messages.showErrorMessage
          // because main window is not yet displayed
          final String sError = Messages.getErrorMessage(112) + " : " + device.getName();
          InformationJPanel.getInstance().setMessage(sError, InformationJPanel.ERROR);
          continue;
        }
      }
    }
  }

  /**
   * @return Returns the main window.
   */
  public static JajukWindow getWindow() {
    return jw;
  }

  /**
   * @return Returns whether jajuk is in exiting state
   */
  public static boolean isExiting() {
    return bExiting;
  }

  /**
   * Lauch UI
   */
  public static void launchUI() throws Exception {
    if (bUILauched) {
      return;
    }
    // ui init
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          // Init perf monitor
          if (bTestMode) {
            ext.EventDispatchThreadHangMonitor.initMonitoring();
          }

          // Light drag and drop for VLDocking
          UIManager.put("DragControler.paintBackgroundUnderDragRect", Boolean.FALSE);

          // Set windows decoration to look and feel
          JFrame.setDefaultLookAndFeelDecorated(true);
          JDialog.setDefaultLookAndFeelDecorated(true);

          // Prepare toolbars
          DockingUISettings.getInstance().installUI();
          tbcontainer = ToolBarContainer.createDefaultContainer(true, false, true, false);

          // starts ui
          jw = JajukWindow.getInstance();

          // Creates the panel
          jpFrame = (JPanel) jw.getContentPane();
          jpFrame.setOpaque(true);
          jpFrame.setLayout(new BorderLayout());

          // create the command bar
          command = CommandJPanel.getInstance();
          command.initUI();

          // Create the information bar panel
          information = InformationJPanel.getInstance();

          // Add information panel
          jpFrame.add(information, BorderLayout.SOUTH);

          // Create the perspective manager
          PerspectiveManager.load();

          // Set menu bar to the frame
          jw.setJMenuBar(JajukJMenuBar.getInstance());

          // Create the perspective tool bar panel
          perspectiveBar = PerspectiveBarJPanel.getInstance();
          jpFrame.add(perspectiveBar, BorderLayout.WEST);

          // Apply size and location BEFORE setVisible
          jw.applyStoredSize();

          // Display the frame
          jw.setVisible(true);

          // Apply watermark
          Util.setWatermark(ConfigurationManager.getProperty(CONF_OPTIONS_WATERMARK));

          // Apply size and location again
          // (required by Gnome for ie to fix the 0-sized maximized
          // frame)
          jw.applyStoredSize();

          // Initialize and add the desktop
          PerspectiveManager.init();

          // Add main container (contains toolbars + desktop)
          final FormLayout layout = new FormLayout("f:d:grow", // columns
              "f:d:grow, 0dlu, d"); // rows
          final PanelBuilder builder = new PanelBuilder(layout);
          final CellConstraints cc = new CellConstraints();
          // Add items
          builder.add(tbcontainer, cc.xy(1, 1));
          builder.add(command, cc.xy(1, 3));
          jpFrame.add(builder.getPanel(), BorderLayout.CENTER);

          // Upgrade step2
          UpgradeManager.upgradeStep2();

          // Display tip of the day if required (not at the first
          // session)
          if (ConfigurationManager.getBoolean(CONF_SHOW_TIP_ON_STARTUP) && !bFirstSession) {
            final TipOfTheDay tipsView = new TipOfTheDay();
            tipsView.setLocationRelativeTo(jw);
            tipsView.setVisible(true);
          }

        } catch (final Exception e) { // last chance to catch any error for
          // logging purpose
          e.printStackTrace();
          Log.error(106, e);
        } finally {
          if (sc != null) {
            // Display progress
            sc.setProgress(100);
            sc.splashOff();
          }
          bUILauched = true;
          // Notify any first time wizard to startup refresh
          synchronized (canLaunchRefresh) {
            canLaunchRefresh.notify();
          }
        }
      }
    });

  }

  /** Launch tray, only for linux and windows, not mac for the moment */
  private static void launchTray() throws Exception {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (Util.isUnderLinux() || Util.isUnderWindows()) {
          jsystray = JajukSystray.getInstance();
        }
      }
    });
  }

  /**
   * @return Returns the bUILauched.
   */
  public static boolean isUILaunched() {
    return bUILauched;
  }

  /**
   * @return Returns the sPerspective.
   */
  public static String getDefaultPerspective() {
    return sPerspective;
  }

  /**
   * @param perspective
   *          The sPerspective to set.
   */
  public static void setDefaultPerspective(final String perspective) {
    sPerspective = perspective;
  }

  /**
   * 
   * @return the systray
   */
  public static JajukSystray getSystray() {
    return jsystray;
  }

  /**
   * @return toolbar container
   */
  public static ToolBarContainer getToolbarContainer() {
    return tbcontainer;
  }

  /**
   * @return true if it is the first session after a minor or major upgrade
   *         session
   */
  public static boolean isUpgradeDetected() {
    return bUpgraded;
  }

  public static boolean isCrashRecover() {
    return bCrashRecover;
  }

  /**
   * Create automatically a free music directory (currently ../Music directory
   * relatively to jajuk.jar file) that contains free music packaged with
   * Jukebox Power Pack releases
   */
  private static void powerPack() {
    if (bPowerPack) {
      try {
        // Check if this device don't already exit
        for (Device device : DeviceManager.getInstance().getDevices()) {
          if (FREE_MUSIC_DEVICE_NAME.equals(device.getName())) {
            return;
          }
        }
        // Check for ../Music file presence
        String music = new File(Util.getJarLocation(Main.class).toURI()).getParentFile()
            .getParentFile().getAbsolutePath();
        music += '/' + FREE_MUSIC_DIR;
        File fMusic = new File(music);
        Log.debug("Powerpack detected, tested path: " + fMusic.getAbsolutePath());
        if (fMusic.exists()) {
          Device device = DeviceManager.getInstance().registerDevice(FREE_MUSIC_DEVICE_NAME,
              Device.TYPE_DIRECTORY, fMusic.getAbsolutePath());
          device.setProperty(XML_DEVICE_AUTO_MOUNT, true);
          device.setProperty(XML_DEVICE_AUTO_REFRESH, 0.5d);
          device.mount();
          device.refreshCommand(true, false);
        }
      } catch (Exception e) {
        Log.error(e);
      }
    }
  }

}
