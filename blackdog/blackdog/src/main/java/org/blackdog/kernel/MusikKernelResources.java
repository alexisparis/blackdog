/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
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
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.kernel;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogPlayerPlugin;
import org.blackdog.BlackdogTypesPlugin;
import org.blackdog.entagger.AudioEntagger;
import org.blackdog.kernel.persistence.PersistenceFuture;
import org.blackdog.kernel.persistence.PersistenceMonitor;
import org.blackdog.kernel.resource.AudioColdResource;
import org.blackdog.player.Player;
import org.blackdog.report.ReportManager;
import org.blackdog.report.TagsUpdateReport;
import org.blackdog.type.AudioItem;
import org.blackdog.type.AudioResources;
import org.blackdog.type.cover.CoverSearch;
import org.blackdog.type.RadioItem;
import org.blackdog.type.PlayList;
import org.blackdog.type.TaggedSongItem;
import org.blackdog.type.customizer.SongItemCustomizer;
import org.blackdog.type.scan.IgnoredDirectories;
import org.blackdog.type.scan.IgnoredDirectory;
import org.blackdog.type.scan.ScanProperties;
import org.blackdog.type.scan.ScannedDirectories;
import org.blackdog.type.scan.ScannedDirectory;
import org.blackdog.ui.SongEditorLaunchContext;
import org.siberia.SiberiaImageSearcherPlugin;
import org.siberia.binding.DataBaseBindingManager;
import org.siberia.binding.impl.db.DBUtilities;
import org.siberia.binding.transaction.Status;
import org.siberia.binding.transaction.Transaction;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.image.searcher.ImageSize;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.impl.AbstractKernelResources;
import org.siberia.kernel.resource.ColdResource;
import org.siberia.kernel.resource.DefaultColdResource;
import org.siberia.kernel.resource.ResourcePersistence;
import org.siberia.kernel.resource.ResourceVisualizationMode;
import org.siberia.kernel.resource.event.impl.ResourceModificationEvent;
import org.siberia.type.SibCollection;
import org.blackdog.type.Playable;
import org.siberia.ResourceLoader;
import org.siberia.editor.Editor;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;
import org.siberia.kernel.editor.EditorLauncher;
import org.siberia.type.SibType;
import org.siberia.binding.constraint.DataBaseBindingConstraint;
import org.siberia.binding.constraint.ConstraintRelation;
import org.blackdog.type.ScannablePlayList;
import org.siberia.image.searcher.ImageSearcher;
import org.siberia.properties.PropertiesManager;
import org.siberia.type.task.AbstractSibTask;
import org.siberia.type.task.SibTask;
import org.siberia.ui.UserInterface;
import org.siberia.ui.action.impl.DefaultTypeEditingAction;
import org.siberia.ui.action.impl.TypeEditingAction;

/**
 *
 * Singleton which is responsible for the main kernel resources of the framework<br>
 * Typically : - the current project.<br>
 *             - the list of availables metamodels.<br>
 *
 * This object is responsible for all operations concerning the management of the project <br>
 * and the loaded metamodels.<br>
 * KernelResources must allow all transactions, all modifications on its own structure.<br>
 *
 * TO DO : integration of the scripts action and condition<br>

 * @author alexis
 */
public class MusikKernelResources extends AbstractKernelResources implements EditorLauncher
                            
{
    /** property 'current playlist' */
    public static final String PROPERTY_CURRENT_PLAYLIST = "current-playlist";
    
    /** logger */
    private static Logger logger = Logger.getLogger(MusikKernelResources.class);
    
    /** id of the project */
    public static final String ID_PLAYLISTS               = "blackdog.playlists";
    
    /** id of the the scanning items */
    public static final String ID_SCANNING                = "blackdog.scanning";
    
    /** id of the global items cover search */
    public static final String ID_COVER_SEARCH            = "blackdog.coverSearch";
    
    /** the singleton **/
    private static MusikKernelResources     instance            = new MusikKernelResources();
    
    /** audio resources */
    private        AudioResources           audioResources      = null;
    
    /** the current playlist **/
    private        PlayList                 currentPlayList     = null;
    
    /** scan properties of the main library */
    private        ScanProperties           mainLibraryScanProp = null;
    
    /** player to use */
    private        Player                   player              = null;
    
    /** true to indicate that the kernel resources is in postConfigure */
    private        boolean                  isInPostConfigure   = false;
    
    /* thread related to database persistence */
    private        PersistenceMonitor       persistenceMonitor  = null;
    
    /** set of audio extension supported by blackdog */
    private        Set<String>              supportedExtensions = null;
    
    /** atomic boolean to indicate that a scan is already running */
    private        AtomicBoolean            scanRunning         = new AtomicBoolean(false);
    
    /** entagger */
    private        AudioEntagger            entagger            = null;
    
    /** list of SongItem customizer to take into account when creating a new SongItem */
    private List<SongItemCustomizer>	    customizers         = null;
    
    /** cover search */
    private CoverSearch                     coverSearch         = null;
    
    /** current item actually played */
    private Playable                        currentItemPlayed   = null;
    
    /** return the singleton of this class
     *  @return the KernelResources
     **/
    public static MusikKernelResources getInstance()
    {   return MusikKernelResources.instance; }
    
    /** Creates a new instance of KernelResources */
    private MusikKernelResources()
    {   super();
        
        ResourceBundle rb = ResourceBundle.getBundle(MusikKernelResources.class.getName());
	
	this.persistenceMonitor = new PersistenceMonitor();
	this.persistenceMonitor.start();
        
        /** self launcher to use an existing Player for AudioItem */
        this.registerEditorLauncher(this);
        
        this.mainLibraryScanProp = new ScanProperties();
        /** add default directory to test */
//        ScannedDirectory dir = new ScannedDirectory();
//        
//        try
//        {   dir.setValue(new Directory("/mnt/data/music/div"));
//            dir.setName("Divers");
//        }
//        catch (PropertyVetoException ex)
//        {   ex.printStackTrace(); }
//        
//        this.mainLibraryScanProp.getScannedDirectories().add(dir);
        
	ColdResource scanningResources = new DefaultColdResource(this.mainLibraryScanProp, rb.getString("scanRc.name"), ResourcePersistence.DATABASE);
	scanningResources.setVisualizationMode(ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START_AND_CHANGE_ITEMS_AND_ADDED_ITEMS);
        this.addResource(ID_SCANNING, scanningResources);
        
        this.audioResources = new AudioResources();
        this.audioResources.getPlayListLibrary().setScanProperties(this.mainLibraryScanProp);
        
        try
        {
            this.audioResources.setName(rb.getString("audioRc.name"));
        }
        catch(PropertyVetoException e)
        {
            e.printStackTrace();
        }
	
	this.coverSearch = new CoverSearch();
	
        try
        {
            this.coverSearch.setName(rb.getString("coverSearch.name"));
        }
        catch(PropertyVetoException e)
        {
            e.printStackTrace();
        }
	
	this.addGlobalItem(ID_COVER_SEARCH, this.coverSearch);
        
        /* initialize the resources */
	ColdResource audioResources = new AudioColdResource(this.audioResources, this.audioResources.getName(), ResourcePersistence.DATABASE);
	audioResources.setVisualizationMode(ResourceVisualizationMode.VISUALIZE_ALL_ITEMS_AT_START_AND_CHANGE_ITEMS_AND_ADDED_ITEMS);
        this.addResource(ID_PLAYLISTS, audioResources);
        
        this.setAsMainResource(ID_PLAYLISTS);
    }

    /** return the item actually played
     *	@return a Playable
     */
    public Playable getCurrentItemPlayed()
    {
	return currentItemPlayed;
    }

    /** initialize the item actually played
     *	@param currentItemPlayed a Playable
     */
    public void setCurrentItemPlayed(Playable currentItemPlayed)
    {
	this.currentItemPlayed = currentItemPlayed;
	
	/* do not warn cover search if no item are played to continue to propose
	 *  last images search
	 */
	if ( this.coverSearch != null && this.currentItemPlayed != null )
	{
	    this.coverSearch.setPlayable(currentItemPlayed);
	}
    }
    
    /** return a map containing all image searcher extensions declared
     *	@return a map that contains as key the name of an ImageSearcher and as value the class of the ImageSearcher
     */
    public Map<String, String> getImageSearchersDeclared()
    {
	Map<String, String> result = null;
	
	Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(SiberiaImageSearcherPlugin.EXTENSION_POINT_IMAGE_SEARCHER);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("musik kernel found " + (extensions == null ? 0 : extensions.size()) + " extension point for " + 
			    SiberiaImageSearcherPlugin.EXTENSION_POINT_IMAGE_SEARCHER);
	}
	
	if ( extensions != null )
	{
	    Iterator<SiberExtension> it = extensions.iterator();

	    while(it.hasNext())
	    {
		SiberExtension sibExtension = it.next();

		if ( sibExtension != null )
		{
		    String name             = sibExtension.getStringParameterValue(SiberiaImageSearcherPlugin.EXTENSION_POINT_IMAGE_SEARCHER_NAME);
		    String imgSearcherClass = sibExtension.getStringParameterValue(SiberiaImageSearcherPlugin.EXTENSION_POINT_IMAGE_SEARCHER_CLASS);
		    
		    if ( name != null && imgSearcherClass != null )
		    {
			if ( result == null )
			{
			    result = new HashMap<String, String>(extensions.size());
			}
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("putting image searcher '" + name + "' with class '" + imgSearcherClass + "'");
			}
			
			result.put(name, imgSearcherClass);
		    }
		    else
		    {
			logger.error("unable to register image searcher name='" + name + "' with class='" + imgSearcherClass + "'");
		    }
		}
	    }
	}
	
	if ( result == null )
	{
	    result = Collections.EMPTY_MAP;
	}
	
	return result;
    }
    
    /** reinitialize the image searcher of the cover search item */
    public void initializeCoverSearchImageSearcher()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("initializeCoverSearchImageSearcher");
	}
	if ( this.coverSearch == null )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("cover search is null --> could not initailize image searcher");
	    }
	}
	else
	{
	    String propertySearcherChoice = "cover.searcher.choice";

	    /** load the properties relating to cover search */
	    Object[] properties = PropertiesManager.getGeneralProperties(propertySearcherChoice,
									 "cover.searcher.image.size",
									 "cover.searcher.image.maxRetrieve",
									 "cover.searcher.additionalCriterions",
									 "cover.searcher.enabled");

	    Map<String, String> searchers = this.getImageSearchersDeclared();

	    /* parse properties */
	    String    imageSearcher        = null;
	    ImageSize imagesize            = null;
	    int       maxImageRetrieved    = 5;
	    String    additionalCriterions = null;
	    boolean   webSearchEnabled     = true;

	    boolean initializeImageSearcherProperty = false;

	    if ( properties != null && properties.length == 5 )
	    {
		if ( properties[0] instanceof String )
		{
		    imageSearcher = (String)properties[0];
		}
		if ( properties[1] instanceof String )
		{
		    imagesize = ImageSize.getImageSizeWithLabel( (String)properties[1] );
		}
		if ( properties[2] instanceof Number )
		{
		    maxImageRetrieved = ((Number)properties[2]).intValue();
		}
		if ( properties[3] instanceof String )
		{
		    additionalCriterions = (String)properties[3];
		}
		if ( properties[4] instanceof Boolean )
		{
		    webSearchEnabled = ((Boolean)properties[4]).booleanValue();
		}
	    }
	    
	    this.coverSearch.setUseImageSearcher(webSearchEnabled);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("image searcher name='" + imageSearcher + "'");
		logger.debug("image size=" + imagesize);
		logger.debug("max image retrieved=" + maxImageRetrieved);
		logger.debug("additional criterions=" + additionalCriterions);
		
	    }

	    if ( imagesize == null )
	    {
		imagesize = ImageSize.MEDIUM;
	    }

	    initializeImageSearcherProperty = (imageSearcher == null);

	    ImageSearcher searcherInstance = null;

	    /** search in the map for the given imageSearcher */
	    if ( searchers != null )
	    {
		Iterator<Map.Entry<String, String>> entries = searchers.entrySet().iterator();

		while(entries.hasNext() && searcherInstance == null)
		{
		    Map.Entry<String, String> currentEntry = entries.next();

		    if ( currentEntry != null )
		    {
			String imgSearcherClass = null;

			if ( imageSearcher == null )
			{
			    /* try to instantiate related class */
			    imgSearcherClass = currentEntry.getValue();
			}
			else
			{
			    if ( imageSearcher.equals(currentEntry.getKey()) )
			    {
				imgSearcherClass = currentEntry.getValue();
			    }
			}

			if ( imgSearcherClass != null )
			{
			    try
			    {
				/* try to instantiate */
				Class c = ResourceLoader.getInstance().getClass(imgSearcherClass);
				searcherInstance = (ImageSearcher)c.newInstance();

				if ( initializeImageSearcherProperty )
				{
				    imageSearcher = currentEntry.getKey();
				}
			    }
			    catch (ResourceException ex)
			    {
				logger.error("could not load class '" + imgSearcherClass + "'", ex);
			    }
			    catch (InstantiationException ex)
			    {
				logger.error("could not instantiate class '" + imgSearcherClass + "'", ex);
			    }
			    catch (IllegalAccessException ex)
			    {
				logger.error("could not instantiate class '" + imgSearcherClass + "'", ex);
			    }
			}
		    }
		}
	    }

	    if ( initializeImageSearcherProperty && imageSearcher != null )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("trying to initialize image searcher property to'" + imageSearcher + "'");
		}
		try
		{
		    PropertiesManager.setGeneralProperty(propertySearcherChoice, imageSearcher);
		}
		catch(Exception ex)
		{
		    logger.error("unable to save value '" + imageSearcher + "' as value of the property '" + propertySearcherChoice + "'", ex);
		}
	    }

	    if ( searcherInstance == null )
	    {
		logger.error("could not create the image searcher --> no covers will be provided");
	    }
	    else
	    {
		searcherInstance.setImageSize(imagesize);
		searcherInstance.setMaximumLinksRetrieved(maxImageRetrieved);
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("image searcher instance is " + searcherInstance);
	    }
	    
	    this.coverSearch.setImagesearcher(searcherInstance);
	    this.coverSearch.setAdditionalCriterions( additionalCriterions == null ? null : additionalCriterions.split(" ") );
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of initializeCoverSearchImageSearcher");
	}
    }
    
    /** force entagger to be compute */
    public void resetAudioEntagger()
    {
	this.entagger = null;
    }
    
    /** force the entagger properties to be initialized
     *	this is called at first start when the system detect that main properties does not exists
     */
    public void forceAudioEntaggerInitialization()
    {
	this.getAudioEntagger();
    }
    
    /** return the AudioEntagger to use
     *	@return an AudioEntagger
     */
    public synchronized AudioEntagger getAudioEntagger()
    {
	if ( this.entagger == null )
	{
	    /** find in the properties */
	    String entaggerName = (String)PropertiesManager.getGeneralProperty("entagger.choice");
	    
	    if ( entaggerName != null && entaggerName.trim().length() == 0 )
	    {
		entaggerName = null;
	    }
	    
	    /* search in all extension the good entagger to use
	     *	if entaggerName is null, then we use the first
	     *	and we initialize the property
	     */
	    String firstEntaggerClass = null;
	    String firstEntaggerName  = null;
	    String entaggerClass      = null;
	    
            Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BlackdogTypesPlugin.AUDIO_ENTAGGER_EXTENSION_POINT_ID);
	    if ( extensions != null )
	    {
		Iterator<SiberExtension> it = extensions.iterator();
		
		while(it.hasNext())
		{
		    SiberExtension sibExtension = it.next();
		    
		    if ( sibExtension != null )
		    {
			if ( entaggerName != null )
			{
			    String name = sibExtension.getStringParameterValue("name");
			 
			    if ( entaggerName.equals(name) )
			    {
				entaggerClass = sibExtension.getStringParameterValue("class");
				break;
			    }
			}
			else
			{
			    if ( firstEntaggerName == null )
			    {
				firstEntaggerName = sibExtension.getStringParameterValue("name");
				firstEntaggerClass = sibExtension.getStringParameterValue("class");
			    }
			}
		    }
		}
	    }
	    
	    if ( entaggerName == null && firstEntaggerName != null )
	    {
		try
		{
		    PropertiesManager.setGeneralProperty("entagger.choice", firstEntaggerName);
		}
		catch(Exception ex)
		{
		    logger.error("unable to save value '" + firstEntaggerName + "' as value of the property 'entagger.choice'", ex);
		}
	    }
	    
	    if ( entaggerClass == null )
	    {
		entaggerClass = firstEntaggerClass;
	    }
	    
	    if ( entaggerClass != null )
	    {
		try
		{
		    this.entagger = (AudioEntagger)ResourceLoader.getInstance().getClass(entaggerClass).newInstance();
		}
		catch(Exception e)
		{
		    logger.error("unable to create n AudioEntagger from class " + entaggerClass, e);
		}
	    }
	}
	
	return this.entagger;
    }
    
    /** return the scan properties of the main library
     *  @return a ScanProperties
     */
    public ScanProperties getLibraryScanProperties()
    {
        return this.mainLibraryScanProp;
    }
    
    /** return an object representing the audio resources managed by blackdog application
     *  @return an AudioResources
     */
    public AudioResources getAudioResources()
    {
        return this.audioResources;
    }
    
    /** return the current playlist
     *  @return a PlayList
     */
    public PlayList getCurrentPlayList()
    {
        return this.currentPlayList;
    }
    
    /** set the current playlist
     *  @param playlist a PlayList
     */
    protected void setCurrentPlayList(PlayList playlist)
    {
        if ( playlist != this.getCurrentPlayList() )
        {
            PlayList old = this.getCurrentPlayList();

            this.currentPlayList = playlist;

            this.firePropertyChange(PROPERTY_CURRENT_PLAYLIST, old, this.getCurrentPlayList());
        }
    }
    
    /** launch edition on elements that has to be edited at start
     *  @return an array containing SibType
     */
    @Override
    public SibType[] getItemToEditAtStart()
    {
	SibType[] types = super.getItemToEditAtStart();
	
	/** look if we should start edition of the main library at start up */
	boolean editMainLibraryAtStart = false;
	boolean editRadioListAtStart   = false;
	
        Object o = PropertiesManager.getGeneralProperty("playlist.main.editAtStartup");
        if ( o instanceof Boolean )
        {
            editMainLibraryAtStart = ((Boolean)o).booleanValue();
        }
	
        o = PropertiesManager.getGeneralProperty("radiolist.main.editAtStartup");
        if ( o instanceof Boolean )
        {
            editRadioListAtStart = ((Boolean)o).booleanValue();
        }
	
	if ( (editMainLibraryAtStart && this.getAudioResources().getPlayListLibrary() != null) ||
	     (editRadioListAtStart && this.getAudioResources().getRadioList() != null) )
	{
	    SibType[] copy = null;
	    
	    copy = new SibType[ (types == null ? 0 : types.length) +
				( (editMainLibraryAtStart && this.getAudioResources().getPlayListLibrary() != null) ? 1 : 0 ) +
				( (editRadioListAtStart && this.getAudioResources().getRadioList() != null) ? 1 : 0 )];
	    
	    int index = 0;
	    if ( types != null )
	    {
		System.arraycopy(types, 0, copy, 0, types.length);
		index = types.length;
	    }
	    
	    if ( (editRadioListAtStart && this.getAudioResources().getRadioList() != null) )
	    {
		copy[index++] = this.getAudioResources().getRadioList();
	    }
	    if ( (editMainLibraryAtStart && this.getAudioResources().getPlayListLibrary() != null) )
	    {
		copy[index++] = this.getAudioResources().getPlayListLibrary();
	    }
		
	    types = copy;
	}
	
	return types;
    }
    
    /** return true if a scan is already running
     *	@return true if a scan is already running
     */
    public boolean isScanRunning()
    {
	return this.scanRunning.get();
    }
    
    /** set if a scan is already running
     *	@param running true if a scan is already running
     *	@return true if the indicator has been successfully changed
     */
    public boolean setScanRunning(boolean running)
    {
	return this.scanRunning.compareAndSet( ! running, running );
    }
    
    @Override
    /** post-configure resources when all sub-systems are initialized */
    public void postConfigure()
    {	
	super.postConfigure();

	/* ######### */
	/* load data */
	/* ######### */

	Runnable loadRunnable = new Runnable()
	{
	    public void run()
	    {
		long curTime = System.currentTimeMillis();
		
		try
		{
		    isInPostConfigure = true;

		    /* load directories */
		    DataBaseBindingManager manager = null;
		    
		    try
		    {
			manager = Kernel.getInstance().getDatabaseBindingManager();
		    }
		    catch(Exception e)
		    {
			logger.error("getting exception while recovering Scanneddirectories");
		    }

		    if ( manager == null )
		    {
			/** provide some messages to user */
		    }
		    else
		    {
			/** load scanned directories */
			try
			{
			    List items = DBUtilities.loadAll(manager, ScannedDirectory.class);

			    ScannedDirectories scannedList = getLibraryScanProperties().getScannedDirectories();

			    if ( items != null )
			    {
				ListIterator lIt = items.listIterator();

				while(lIt.hasNext())
				{
				    Object current = lIt.next();

				    scannedList.add(current);
				}
			    }
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}

			/** load ignored directories */
			try
			{
			    List items = DBUtilities.loadAll(manager, IgnoredDirectory.class);

			    IgnoredDirectories ignoredList = getLibraryScanProperties().getIgnoredDirectories();

			    if ( items != null )
			    {
				ListIterator lIt = items.listIterator();

				while(lIt.hasNext())
				{
				    Object current = lIt.next();

				    ignoredList.add(current);
				}
			    }
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}

			/** load playlists */
			List items = null;

			DataBaseBindingConstraint ctr = null;

			Transaction transaction = null;

			try
			{
			    transaction = manager.createTransaction();

			    ctr = new DataBaseBindingConstraint(
					PlayList.PROPERTY_MAIN,
					ConstraintRelation.EQ,
					true);

			    items = manager.load(transaction, getAudioResources().getPlayListLibrary().getClass(),
							      ctr);

			    if ( items == null || items.size() == 0 )
			    {
				manager.store(transaction, getAudioResources().getPlayListLibrary());
			    }
			    else
			    {
				getAudioResources().setPlayListLibrary( ((ScannablePlayList)items.get(0)) );
				getAudioResources().getPlayListLibrary().setScanProperties(mainLibraryScanProp);

				getAudioResources().getPlayListLibrary().size();
			    }

			    /** load others playlists */
			    ctr = new DataBaseBindingConstraint(
					PlayList.PROPERTY_MAIN,
					ConstraintRelation.EQ,
					false);

			    items = manager.load(transaction, getAudioResources().getPlayListList().getAllowedClass(),
							  ctr);

			    getAudioResources().getPlayListList().addAll(items);

			    transaction.commit();
			    transaction = null;
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
			finally
			{
			    if ( transaction != null )
			    {
				try
				{
				    transaction.rollback();
				}
				catch(Exception e)
				{
				    e.printStackTrace();
				}
			    }
			}
			
			/** load radio list */
			try
			{
			    items = DBUtilities.loadAll(manager, RadioItem.class);

			    getAudioResources().getRadioList().addAll(items);
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
		    }
		}
		finally
		{   
		    isInPostConfigure = false;

		    logger.info("data loaded in " + ((System.currentTimeMillis() - curTime) / 1000) + " seconds");
		}
	    }
	};

	/** wait for the loading of all entities
	 *  before continue launching
	 *
	 *  this cause the EDT to wait!
	 */
	PersistenceFuture future = this.persistenceMonitor.addTask(loadRunnable);
	
	this.waitForFuture(future);
    }
    
    /** block until the given future is done
     *	@param future a PersistenceFuture
     */
    private void waitForFuture(PersistenceFuture future)
    {
	this.waitForFuture(future, 50);
    }
    
    /** block until the given future is done
     *	@param future a PersistenceFuture
     *	@param sleepTime the value in milli-seconds of time to sleep between each verification of future status
     */
    private void waitForFuture(PersistenceFuture future, long sleepTime)
    {
	if ( future != null )
	{
	    while( ! future.isDone() )
	    {
		try
		{
		    Thread.sleep(sleepTime);
		}
		catch(InterruptedException e)
		{
		    e.printStackTrace();
		}
	    }
	}
    }
    
    /* #########################################################################
     * ########### callbacks methods when dynamical resource are ###############
     * ############################ modified ###################################
     * ######################################################################### */
    
    /** called when an add is detected on the collection of a SibCollection that is linked to a dynamic persistent resource
     *	@param evt a ResourceEvent
     *	@param manager a DatabaseBindingManager
     *	@param sibcollection the SibCollection where items were added
     *	@param added an array of SibType that were added
     */
    @Override
    protected void dynResItemsAdded(final ResourceModificationEvent evt, final DataBaseBindingManager manager, final SibCollection collection, final SibType[] added)
    {
	if ( ! this.isInPostConfigure )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    if ( collection != null && added != null && added.length > 0 )
		    {
			Transaction transaction = null;

			try
			{
			    transaction = manager.createTransaction();

			    /* this is a modif from ScannedDirectory
			     *  or IgnoredDirectory ...
			     */
			    if ( collection instanceof ScannedDirectories || collection instanceof IgnoredDirectories )
			    {
				/* delete the new scanned directories */
				for(int i = 0; i < added.length; i++)
				{
				    Object o = added[i];

				    if ( o != null )
				    {
					manager.store(transaction, o);
				    }
				}
			    }
			    /** some SongItems added to the library */
			    else if ( collection == getAudioResources().getPlayListLibrary() )
			    {
				/** if the list is the main PlayList, then
				 *  some SongItems were added,
				 *  we must delete those SongItems from all lists
				 *  and update all playLists affected by this modification
				 */
				/* add the new playlists */
				for(int i = 0; i < added.length; i++)
				{
				    Object o = added[i];

				    if ( o != null )
				    {
					manager.store(transaction, o);
				    }
				}

				manager.store(transaction, collection);
			    }
			    /* some playlists were added */
			    else if ( collection == getAudioResources().getPlayListList() )
			    {
				for(int i = 0; i < added.length; i++)
				{
				    Object o = added[i];

				    if ( o != null )
				    {
					manager.store(transaction, o);
				    }
				}
			    }
			    /** add on a playlist that is not the library playlist */
			    else if ( collection.getParent() == getAudioResources().getPlayListList() )
			    {
				manager.store(transaction, collection);
			    }
			    else if ( collection == getAudioResources().getDeviceList() )
			    {
				for(int i = 0; i < added.length; i++)
				{
				    Object o = added[i];

				    if ( o != null )
				    {
					manager.store(transaction, o);
				    }
				}

				manager.store(transaction, collection);
			    }
			    else if ( collection == getAudioResources().getRadioList() )
			    {
				for(int i = 0; i < added.length; i++)
				{
				    Object o = added[i];

				    if ( o != null )
				    {
					manager.store(transaction, o);
				    }
				}
			    }

			    transaction.commit();

			    transaction = null;
			}
			catch(Exception e)
			{
			    logger.error("unable to save items", e);
			}
			finally
			{
			    if ( transaction != null && ! transaction.getStatus().equals(Status.COMMITTED) )
			    {
				try
				{
				    transaction.rollback();
				}
				catch(Exception e)
				{
				    logger.error("error while rollbacking transaction", e);
				}
			    }
			}
		    }
		}
	    };
	    
	    if ( Thread.currentThread() == this.persistenceMonitor )
	    {
		runnable.run();
	    }
	    else
	    {
		PersistenceFuture future = this.persistenceMonitor.addTask(runnable);
		
		this.waitForFuture(future);
	    }
	}
    }
    
    /** called when something has been removed off a SibCollection that is linked to a dynamic persistent resource
     *	@param evt a ResourceEvent
     *	@param manager a DatabaseBindingManager
     *	@param sibcollection the SibCollection where items were added
     *	@param removed an array of SibType that were removed
     */
    @Override
    protected void dynResItemsRemoved(final ResourceModificationEvent evt, final DataBaseBindingManager manager, final SibCollection collection, final SibType[] removed)
    {
	if ( ! this.isInPostConfigure )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    if ( evt != null && collection != null && removed != null && removed.length > 0 )
		    {
			Transaction transaction = null;

			try
			{
			    transaction = manager.createTransaction();

			    /* this is a modif from ScannedDirectory
			     *  or IgnoredDirectory ...
			     */
			    if ( collection instanceof ScannedDirectories || collection instanceof IgnoredDirectories )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("removing " + removed.length + " items from " + collection);
				}
				
				/* delete the new scanned directories */
				for(int i = 0; i < removed.length; i++)
				{
				    Object o = removed[i];

				    if ( o != null )
				    {
					manager.delete(transaction, o);
				    }
				}
			    }
			    else if ( collection == getAudioResources().getPlayListLibrary() )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("removing " + removed.length + " items from librairy playlist");
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("updating librairy playlist");
				}
				manager.store(transaction, collection);
				
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("before deleting items");
				}
				for(int i = 0; i < removed.length; i++)
				{
				    Object o = removed[i];

				    if ( o != null )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("deleting " + o);
					}
					manager.delete(transaction, o);
				    }
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("after deleting items");
				}
			    }
			    else if ( collection == getAudioResources().getPlayListList() )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("removing " + removed.length + " items from list of playlist");
				}
				/* some playlists were removed */
				for(int i = 0; i < removed.length; i++)
				{
				    Object o = removed[i];

				    if ( o != null )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("deleting " + o);
					}
					manager.delete(transaction, o);
				    }
				}
			    }
			    else if ( collection.getParent() == getAudioResources().getPlayListList() )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("removing " + removed.length + " items from a playlist " + collection);
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("updating librairy " + collection);
				}
				manager.store(transaction, collection);
			    }
			    else if ( collection == getAudioResources().getDeviceList() )
			    {
				manager.store(transaction, collection);

				for(int i = 0; i < removed.length; i++)
				{
				    Object o = removed[i];

				    if ( o != null )
				    {
					manager.delete(transaction, o);
				    }
				}
			    }
			    else if ( collection == getAudioResources().getRadioList() )
			    {
				/** if the list is the main PlayList, then
				 *  some SongItems were deleted,
				 *  we must delete those SongItems from all lists
				 *  and update all playLists affected by this modification
				 */
				// TODO : remove also from others playlists
				/* delete the new scanned directories */
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("before deleting items");
				}
				for(int i = 0; i < removed.length; i++)
				{
				    Object o = removed[i];

				    if ( o != null )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("deleting " + o);
					}
					manager.delete(transaction, o);
				    }
				}
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("after deleting items");
				}
			    }

			    transaction.commit();

			    transaction = null;
			}
			catch(Exception e)
			{
			    logger.error("unable to save items", e);
			}
			finally
			{
			    if ( transaction != null && ! transaction.getStatus().equals(Status.COMMITTED) )
			    {
				try
				{
				    transaction.rollback();
				}
				catch(Exception e)
				{
				    logger.error("error while rollbacking transaction", e);
				}
			    }
			}
		    }
		}
	    };
	    
	    
	    if ( Thread.currentThread() == this.persistenceMonitor )
	    {
		runnable.run();
	    }
	    else
	    {
		PersistenceFuture future = this.persistenceMonitor.addTask(runnable);
		
		this.waitForFuture(future);
	    }
	}
    }
    
    /** called when something has been removed off a SibCollection that is linked to a dynamic persistent resource
     *	@param evt a ResourceEvent
     *	@param manager a DatabaseBindingManager
     *	@param Object the object that was modified
     *	@param property the property modified
     *	@param oldValue the old value of the property
     *	@param newValue the new value of the property
     */
    @Override
    protected void dynResItemStateChanged(final ResourceModificationEvent evt, final DataBaseBindingManager manager, final Object source, final String property, final Object oldValue, final Object newValue)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("dynResItemStateChanged for property " + property);
	}
	
	/* if post configure or scan */
	if ( ! this.isInPostConfigure && ! this.isScanRunning() )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    boolean shouldUpdateItem = false;

		    if ( ! SibType.PROPERTY_ID.equals(property) )
		    {
			if ( source instanceof ScannedDirectory || source instanceof IgnoredDirectory )
			{
			    shouldUpdateItem = true;
			}
			else if ( source instanceof AudioItem )
			{
			    shouldUpdateItem = true;
			}
			else if ( source instanceof RadioItem )
			{
			    shouldUpdateItem = true;
			}
			else if ( source instanceof PlayList )
			{
			    shouldUpdateItem = true;
			}   
		    }

		    if ( shouldUpdateItem )
		    {
			try
			{
			    /* save the item */
			    manager.storeInsideTransaction(source);
			}
			catch(Exception ex)
			{
			    ex.printStackTrace();
			}
		    }
		}
	    };
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("is persistence monitor ? " + (Thread.currentThread() == this.persistenceMonitor));
	    }
	    
	    if ( Thread.currentThread() == this.persistenceMonitor )
	    {
		runnable.run();
	    }
	    else
	    {
		PersistenceFuture future = this.persistenceMonitor.addTask(runnable);
		
		this.waitForFuture(future);
	    }
	    
	    if ( source instanceof TaggedSongItem && ! ((TaggedSongItem)source).isInTagsRefreshProcess() )
	    {
		final AudioEntagger entagger = getAudioEntagger();
		
		if ( entagger == null )
		{
		    logger.error("no entagger initialized --> tags will not be updated in file");
		}
		else
		{
		    if ( entagger.isSupportingOneOfProperties(property) )
		    {
			if( logger.isDebugEnabled() )
			{
			    logger.debug("item supports tag --> updating file tags");
			}

			/** create a task that will update tags in file */
			SibTask task = new AbstractSibTask()
			{
			    public void _run()
			    {
				AudioEntagger entagger = getAudioEntagger();

				if ( logger.isDebugEnabled() )
				{
				    logger.debug("entagger is " + entagger);
				}

				if ( entagger == null )
				{
				}
				else
				{
				    TagsUpdateReport report = new TagsUpdateReport();

				    entagger.updateFileTags((TaggedSongItem)source, report);

				    /* do not process report actually */
//				    ReportManager reportManager = new ReportManager();
//				    reportManager.processReport(report);
				}
			    }
			};

			try
			{   
			    task.setName(((TaggedSongItem)source).getName() + " tags update");
			}
			catch (PropertyVetoException ex)
			{
			    logger.warn("unable to set the name of the task " + task);
			}

			final TypeEditingAction action = new DefaultTypeEditingAction();
			action.setTypes(task);

			Runnable entaggedRunnable = new Runnable()
			{
			    public void run()
			    {
				action.actionPerformed(new ActionEvent(UserInterface.getInstance(), -1, ""));
			    }
			};

			if ( SwingUtilities.isEventDispatchThread() )
			{
			    entaggedRunnable.run();
			}
			else
			{
			    SwingUtilities.invokeLater(entaggedRunnable);
			}
		    }
		}
	    }
	}
    }
    
    /* #########################################################################
     * ############## IMPLEMENTATION DE L'ASPECT METIER ########################
     * ######################################################################### */
    
    /** return a Set containing all extensions related to audio file supported by blackdog
     *  @return a Set of String (for example : 'mp3', 'ogg', etc..)
     */
    public Set<String> getSupportedAudioExtensions()
    {   
	this.registerAudioItemExtensionPointIfNecessary();
        
	Set<String> result = this.supportedExtensions;
	
	if ( result == null )
	{   result = Collections.emptySet(); }
	
	return result;
    }
    
    /** return a list of customizer to use when scanning items
     *	@return a sorted List of SongItemCustomizer
     */
    public synchronized List<SongItemCustomizer> getSongItemCustomizers()
    {
	if ( this.customizers == null )
	{
	    this.initializeSongItemCustomizerList();
	}
	
	return this.customizers;
    }
    
    /** initialize SongItem list by registering extension points */
    private void initializeSongItemCustomizerList()
    {	    
        /** inspect plugin to look for type extensions */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BlackdogTypesPlugin.SONG_ITEM_CUSTOMIZER_EXTENSION_POINT_ID);
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
	    
	    Map<Integer, SongItemCustomizer> customizerMap = new HashMap<Integer, SongItemCustomizer>();
	    
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
		String classname = currentExtension.getStringParameterValue("class");
		String priority  = currentExtension.getStringParameterValue("priority");
		
                try
                {   
		    Class typeClass = ResourceLoader.getInstance().getClass(classname);
		    
		    /** instantiate class */
		    Object o = typeClass.newInstance();
		    
		    if ( o instanceof SongItemCustomizer )
		    {
			Integer priorityInt = null;
			
			try
			{
			    priorityInt = Integer.parseInt(priority);
			}
			catch(NumberFormatException e)
			{
			    logger.warn("error while parsing priority for song item customizer of kind '" + classname + "' --> '" + priority + "' is not an integer", e);
			}
			
			if ( priorityInt == null )
			{
			    priorityInt = 0;
			}
			
			customizerMap.put(priorityInt, (SongItemCustomizer)o);
		    }
		    else
		    {
			logger.warn("trying to register " + o + " as a song item customizer --> failed");
		    }
                }
                catch(Exception e)
                {   
		    logger.error("error while registering class=" + classname + " as a song item customizer", e);
		}           
            }
	    
	    List<Integer> sortedKeys = new ArrayList<Integer>(customizerMap.keySet());
	    
	    Collections.sort(sortedKeys);//, Collections.reverseOrder());
	    
	    for(int i = 0; i < sortedKeys.size(); i++)
	    {
		SongItemCustomizer currentCustomizer = customizerMap.get(sortedKeys.get(i));
		
		if ( currentCustomizer != null )
		{
		    if ( this.customizers == null )
		    {
			this.customizers = new ArrayList<SongItemCustomizer>(customizerMap.size());
		    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("adding customizer : " + currentCustomizer);
		    }
		    
		    this.customizers.add(currentCustomizer);
		}
	    }
        }
	
	if ( this.customizers == null )
	{
	    this.customizers = Collections.emptyList();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("customizer registered count : " + (this.customizers == null ? 0 : this.customizers.size()));
	}
    }
    
    /** method that allow to register the audio extensions allowed */
    private synchronized void registerAudioItemExtensionPointIfNecessary()
    {
        if ( this.supportedExtensions == null )
        {   
            Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BlackdogTypesPlugin.AUDIO_EXTENSION_SUPPORTED_EXTENSION_POINT_ID);
	    if ( extensions != null )
	    {
		Iterator<SiberExtension> it = extensions.iterator();
		
		while(it.hasNext())
		{
		    SiberExtension sibExtension = it.next();
		    
		    if ( sibExtension != null )
		    {
			String extension = sibExtension.getStringParameterValue("extension");
			
			if ( extension != null )
			{
			    if ( this.supportedExtensions == null )
			    {
				this.supportedExtensions = new HashSet<String>(extensions.size());
			    }
			    
			    this.supportedExtensions.add(extension);
			}
		    }
		}
	    }
        }
    }
    
    /** return a map containing all player extensions declared
     *	@return a map that contains as key a key representation of a Player and as value the class of the Player
     */
    public Map<PlayerKey, String> getPlayersDeclared()
    {
	Map<PlayerKey, String> result = null;
	
	Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BlackdogPlayerPlugin.PLAYER_EXTENSION_NAME);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("musik kernel found " + (extensions == null ? 0 : extensions.size()) + " extension point for " + 
			    BlackdogPlayerPlugin.PLAYER_EXTENSION_NAME);
	}
	
	if ( extensions != null )
	{
	    Iterator<SiberExtension> it = extensions.iterator();

	    while(it.hasNext())
	    {
		SiberExtension sibExtension = it.next();

		if ( sibExtension != null )
		{   
		    String name        = sibExtension.getStringParameterValue(BlackdogPlayerPlugin.EXTENSION_POINT_PLAYER_NAME);
		    String playerClass = sibExtension.getStringParameterValue(BlackdogPlayerPlugin.EXTENSION_POINT_PLAYER_CLASS);
		    String priority    = sibExtension.getStringParameterValue(BlackdogPlayerPlugin.EXTENSION_POINT_PLAYER_PRIORITY);
		    
		    if ( name != null && playerClass != null )
		    {
			if ( result == null )
			{
			    result = new HashMap<PlayerKey, String>(extensions.size());
			}
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("putting player '" + name + "' with class '" + playerClass + "'");
			}
			
			PlayerKey key = new PlayerKey();
			key.name = name;
			
			try
			{
			    int value = Integer.parseInt(priority);
			    
			    key.priority = value;
			}
			catch(NumberFormatException e)
			{
			    logger.warn("unable to parse priority for player '" + name + "'", e);
			}
			
			result.put(key, playerClass);
		    }
		    else
		    {
			logger.error("unable to register player name='" + name + "' with class='" + playerClass + "'");
		    }
		}
	    }
	}
	
	if ( result == null )
	{
	    result = Collections.EMPTY_MAP;
	}
	
	return result;
    }
    
    /** initialize the player */
    public void initializePlayer()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("initializePlayer");
	}
	
	this.player = null;
	
	String playerChoicePropertyRepr = "player.choice";
	
	/** load the properties relating to cover search */
	Object property = PropertiesManager.getGeneralProperty(playerChoicePropertyRepr);

	Map<PlayerKey, String> players = this.getPlayersDeclared();
	
	boolean resetProperty = false;
	
	String playerClass = null;
	
	if ( property != null )
	{
	    Set<PlayerKey> keySet = players.keySet();

	    Iterator<PlayerKey> keyIt = keySet.iterator();
	    while( keyIt.hasNext() && playerClass == null )
	    {
		PlayerKey currentKey = keyIt.next();

		if ( currentKey != null && property.equals(currentKey.getName()) )
		{
		    playerClass = players.get(currentKey);
		}
	    }
	
	    /* try to instantiate the choosen class */
	    if ( playerClass != null )
	    {
		try
		{
		    Class c = ResourceLoader.getInstance().getClass(playerClass);
		    this.player = (Player)c.newInstance();
		    
		    if ( ! this.player.acceptToBeUsed() )
		    {
			logger.warn("the player set in the property does not accept to be used --> searching for a better player");
			this.player = null;
		    }
		}
		catch(Exception e)
		{
		    logger.warn("unable to instantiate player for class '" + playerClass + "'", e);
		}
	    }
	}
	
	if ( this.player == null )
	{
	    /** create a list of PlayerKey according to their priority */
	    List<PlayerKey> playerKeyList = new ArrayList<PlayerKey>(players.keySet());
	    
	    Collections.sort(playerKeyList, Collections.reverseOrder());
	    
	    for(int i = 0; i < playerKeyList.size() && this.player == null ; i++)
	    {
		PlayerKey currentKey = playerKeyList.get(i);
		
		String currentClass = players.get(currentKey);
		
		if ( currentKey != null && currentClass != null )
		{
		    try
		    {
			Class c = ResourceLoader.getInstance().getClass(currentClass);
			
			Player instantiatedPlayer = (Player)c.newInstance();
			
			/** ask player if it ask to be used */
			if ( instantiatedPlayer.acceptToBeUsed() )
			{
			    this.player = instantiatedPlayer;
				    
			    property = currentKey.getName();

			    resetProperty = true;
			}
			else
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("player " + instantiatedPlayer + " does not acept to be launched --> " +
					     "continue to search for a valid player");
			    }
			}
		    }
		    catch(Exception e)
		    {
			logger.warn("unable to instantiate player for class '" + currentClass + "'", e);
		    }
		}
	    }
	}
	
	if ( resetProperty && property != null )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("trying to initialize player property to'" + property + "'");
	    }
	    try
	    {
		PropertiesManager.setGeneralProperty(playerChoicePropertyRepr, property);
	    }
	    catch(Exception ex)
	    {
		logger.error("unable to save value '" + property + "' as value of the property '" + playerChoicePropertyRepr + "'", ex);
	    }
	}
	
	if ( this.player != null )
	{
	    this.player.configure();
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("using player " + this.player);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of initializePlayer");
	}
    }
    
    /** returns a Player for the given Item
     *  @param item a Playable
     *  @return a Player
     */
    public synchronized Player getPlayerFor(Playable item)
    {   
        Player player = this.player;
        
        return player;
    }
    
    /* #########################################################################
     * ###################### EditorLauncher implementation ####################
     * ######################################################################### */

    /**
     * return the priority of the launcher
     * 
     * @return the priority of the launcher. if this priority is important, the launcher will be asked first to launch editor.
     *      a launcher with priority 0 will be asked to launch the editor if no launcher with a greater priority accept to care about the editor.
     */
    public int getPriority()
    {   return Integer.MAX_VALUE; }

    /**
     * ask to launch an Editor according to the given context
     * 
     * @param context an EditorLaunchContext
     * @return true if it has succeeded
     */
    public boolean launch(EditorLaunchContext context)
    {   
	/* if we try to edit a AudioItem, then check if an AudioReader is already registered,
         *  if so, then just change the instance of the AudioReader
         */
        boolean acceptLaunch = false;
        
        PlayList newPlayList = null;
        
        if ( context != null && context.getItem() instanceof AudioItem )
        {   
	    Editor editor = Kernel.getInstance().getEditorRegistry().getRegisteredEditorForItem(context.getItem());
            
            if ( context instanceof SongEditorLaunchContext )
            {
                newPlayList = ((SongEditorLaunchContext)context).getPlayList();
            }
        }
        
        this.setCurrentPlayList(newPlayList);
        
        return acceptLaunch;
    }
    
    /** represents a plugin key declaration of a Player */
    public static class PlayerKey implements Comparable<PlayerKey>
    {
	/** name of the player */
	String name     = null;
	
	/** priority of the player */
	int    priority = -1;
	
	/** return the name of the player
	 *  @return a String
	 */
	public String getName()
	{
	    return this.name;
	}
	
	/** return the priority of the player
	 *  @return an integer
	 */
	public int getPriority()
	{
	    return this.priority;
	}

	public boolean equals(Object obj)
	{
	    boolean result = false;
	    
	    if ( obj instanceof PlayerKey )
	    {
		if ( this.name == null )
		{
		    if ( ((PlayerKey)obj).name == null )
		    {
			result = true;
		    }
		}
	    }
	    else
	    {
		result = this.name.equals( ((PlayerKey)obj).name );
	    }
	    
	    return result;
	}

	public int compareTo(PlayerKey o)
	{
	    int result = 0;
	    
	    if ( o == null )
	    {
		result = 1;
	    }
	    else
	    {
		result = this.priority - o.priority;
	    }
	    
	    return result;
	}
    }
}
