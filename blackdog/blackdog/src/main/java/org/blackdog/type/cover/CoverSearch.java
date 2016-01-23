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
package org.blackdog.type.cover;

import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.blackdog.type.*;
import org.blackdog.type.cover.event.CoverSearchEvent;
import org.blackdog.type.cover.event.CoverSearchItemEvent;
import org.blackdog.type.cover.event.CoverSearchListener;
import org.siberia.ResourceLoader;
import org.siberia.image.searcher.ImageSearcher;
import org.siberia.image.searcher.event.ImageFoundEvent;
import org.siberia.image.searcher.event.ImageSearcherEvent;
import org.siberia.image.searcher.event.ImageSearcherListener;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.utilities.io.IOUtilities;

/**
 *
 * Object that collect and launch the search of images for a playable
 *
 * @author alexis
 */
@Bean( 
        name = "Cover search",
	internationalizationRef = "org.blackdog.rc.i18n.type.CoverSearch", 
	expert = false, 
	hidden = true, 
	preferred = true, 
	propertiesClassLimit = Object.class, 
	methodsClassLimit = Object.class
     )
public class CoverSearch extends AbstractSibType
{
    /** kind of cover item event */
    enum CoverEventType
    {
	ADD,
	REMOVED;
    }
    
    /** property playable */
    public static final String PROPERTY_PLAYABLE	      = "ImagesResearch.playable";
    
    /** property Image searcher */
    public static final String PROPERTY_IMAGE_SEARCHER	      = "ImagesResearch.imageSearcher";
    
    /** property additional criterions */
    public static final String PROPERTY_ADDITIONAL_CRITERIONS = "ImagesResearch.additionalCriterions";
    
    /** logger */
    private Logger                   logger                      = Logger.getLogger(CoverSearch.class);
    
    /** image searcher */
    private ImageSearcher            imageSearcher               = null;
    
    /** playable */
    private WeakReference<Playable>  playableRef                 = new WeakReference<Playable>(null);
    
    /** additional criterions */
    private String[]                 addCriterions               = null;
    
    /** image searcher listener */
    private ImageSearcherListener    imageSearcherListener       = null;
    
    /** list of CoverSearchListener */
    private List<CoverSearchListener> coverListeners             = null;
    
    /** vector of URL */
    private Vector<URL>               urls                       = new Vector<URL>();
    
    /** number of images that have been found on the local system */
    private int                       localImageFoundCount       = 0;
    
    /** directory of the current song item */
    private File                      currentPlayableDir         = null;
    
    /** properties resulting from the file that declare default image for directories */
    private Properties                defaultImageProperties     = null;
    
    /** file representing the location where are stored default images */
    private File                      defaultImagePropertiesFile = null;
    
    /** true if the cover search has to use ImageSearcher */
    private boolean                   useImageSearcher           = true;
    
    /** url of the default cover */
    private URL                       defaultCoverUrl            = null;
    
    /** enabled */
    private boolean                   enabled                    = true;
    
    /**
     * Creates a new instance of CoverSearch
     */
    public CoverSearch()
    {	
	/** initailize defaultImageProperties */
	String applicationDirPath = ResourceLoader.getInstance().getApplicationHomeDirectory();
	
	this.defaultImagePropertiesFile = new File(applicationDirPath, "defaultImages.properties");
	
	if ( ! defaultImagePropertiesFile.exists() )
	{
	    try
	    {
		defaultImagePropertiesFile.createNewFile();
	    }
	    catch (IOException ex)
	    {
		logger.error("enable to create file '" + defaultImagePropertiesFile + "'", ex);
	    }
	}
	if ( defaultImagePropertiesFile.exists() )
	{
	    InputStream stream = null;
	    try
	    {
		this.defaultImageProperties = new Properties();
		stream = new FileInputStream(defaultImagePropertiesFile);
		this.defaultImageProperties.load(stream);
	    }
	    catch (IOException ex)
	    {
		logger.error("enable to load properties from '" + defaultImagePropertiesFile + "'", ex);
	    }
	    finally
	    {
		if ( stream != null )
		{
		    try
		    {
			stream.close();
		    }
		    catch (IOException ex)
		    {
			logger.error("error while closing input stream on default image file", ex);
		    }
		}
	    }
	}
	
	this.imageSearcherListener = new ImageSearcherListener()
	{
	    public synchronized void imageFound(ImageFoundEvent event)
	    {
		if ( event != null && event.getSource() == getImageSearcher() )
		{
		    if ( event.getImageIndex() >= 0 && event.getURL() != null )
		    {
			int convertedIndex = event.getImageIndex() + localImageFoundCount;
			
			/** ensure vector capacity */
			if ( convertedIndex >= urls.size() )
			{
			    for(int i = urls.size(); i < convertedIndex; i++)
			    {
				urls.add(null);
			    }
			    urls.add(event.getURL());
			}
			else
			{
			    urls.set(convertedIndex, event.getURL());
			}
			
			/** indicate to listeners that a new url is available */
			fireCoverSearchEvent(CoverEventType.ADD, convertedIndex);
		    }
		}
	    }
	    
	    public void searchFinished(ImageSearcherEvent event)
	    {
		if ( event != null && event.getSource() == getImageSearcher() )
		{
		    CoverSearchEvent evt = null;
		    
		    if ( coverListeners != null )
		    {
			for(int i = 0; i < coverListeners.size(); i++)
			{
			    CoverSearchListener currentListener = coverListeners.get(i);
			    
			    if ( currentListener != null )
			    {
				if ( evt == null )
				{
				    evt = new CoverSearchEvent(CoverSearch.this);
				}
				
				currentListener.searchFinished(evt);
			    }
			}
		    }
		}
	    }
	    
	    public void searchHasBegan(ImageSearcherEvent event)
	    {
		if ( event != null && event.getSource() == getImageSearcher() )
		{
		    CoverSearchEvent evt = null;
		    
		    if ( coverListeners != null )
		    {
			for(int i = 0; i < coverListeners.size(); i++)
			{
			    CoverSearchListener currentListener = coverListeners.get(i);
			    
			    if ( currentListener != null )
			    {
				if ( evt == null )
				{
				    evt = new CoverSearchEvent(CoverSearch.this);
				}
				
				currentListener.searchStarted(evt);
			    }
			}
		    }
		}
	    }
	};
    }

    public boolean isUseImageSearcher()
    {
	return useImageSearcher;
    }

    public void setUseImageSearcher(boolean useImageSearcher)
    {
	this.useImageSearcher = useImageSearcher;
    }
    
    public boolean isEnabled()
    {
	return enabled;
    }

    public void setEnabled(boolean enabled)
    {
	boolean oldEnabled = this.isEnabled();
	
	this.enabled = enabled;
	
	if ( oldEnabled != enabled )
	{
	    this.launchResearch();
	}
    }
    
    /** copy the image represented by the given url into the current directory
     *	@param url an URL
     *	@return the url of the copied image
     */
    public URL copyImage(URL url) throws IOException
    {
	URL result = null;
	
	if ( url != null && this.currentPlayableDir != null && url.getProtocol() != null )
	{
	    if ( "file".equals(url.getProtocol()) )
	    {
		result = url;
	    }
	    else
	    {
		InputStream stream = null;

		File destination = null;

		try
		{
		    stream = url.openStream();

		    int index = 0;
		    
		    String fileName = url.getFile();
		    String extension = null;
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("currentPlayableDir is " + this.currentPlayableDir);
			logger.debug("url is " + url);
			logger.debug("url file is " + url.getFile());
			logger.debug("url path is " + url.getPath());
		    }
		    
		    if ( fileName == null )
		    {
			fileName = "cover";
		    }
		    
		    int lastSlashIndex = fileName.lastIndexOf('/');
		    if ( lastSlashIndex != -1 )
		    {
			fileName = fileName.substring(lastSlashIndex + 1);
		    }
		    int lastPointIndex = fileName.lastIndexOf('.');
		    if ( lastPointIndex != -1 )
		    {
			extension = fileName.substring(lastPointIndex + 1);
			fileName  = fileName.substring(0, lastPointIndex);
		    }

		    do
		    {
			if ( index == 0 )
			{
			    destination = new File(this.currentPlayableDir, fileName + (extension == null ? "" : "." + extension));
			}
			else
			{
			    destination = new File(this.currentPlayableDir, fileName + "_" + index + (extension == null ? "" : "." + extension));
			}
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("trying to determine if file " + destination + " could be used to copy the image");
			}
			
			index++;
		    }
		    while( destination.exists() );

		    if ( destination.createNewFile() )
		    {
			IOUtilities.copy(stream, destination);

			result = destination.toURL();
		    }
		}
		catch(IOException e)
		{
		    logger.error("error while copying " + url + " to " + destination, e);
		}
		finally
		{
		    if ( stream != null )
		    {
			try
			{
			    stream.close();
			}
			catch (IOException ex)
			{
			    ex.printStackTrace();
			}
		    }
		}
	    }
	}
	
	return result;
    }
    
    /** indicate which image to consider as default for the current directory
     *	@param file the file representing the current image
     */
    public void setDefaultImageForCurrentDirectory(File file)
    {
	if ( file != null && this.defaultImageProperties != null )
	{
	    if ( this.currentPlayableDir != null )
	    {
		this.defaultImageProperties.setProperty(this.currentPlayableDir.getAbsolutePath(), file.getAbsolutePath());
		
		OutputStream stream = null;
		
		try
		{
		    stream = new FileOutputStream(this.defaultImagePropertiesFile);
		    this.defaultImageProperties.store(stream, "");
		    
		    this.defaultCoverUrl = file.toURL();
		}
		catch(IOException e)
		{
		    logger.error("unable to save default image properties", e);
		}
		finally
		{
		    if ( stream != null )
		    {
			try
			{
			    stream.close();
			}
			catch (IOException ex)
			{
			    logger.error("error while closing input stream on default image file", ex);
			}
		    }
		}
	    }
	}
    }
    
    /** return the image searcher
     *	@param an ImageSearcher
     */
    public ImageSearcher getImageSearcher()
    {
	return this.imageSearcher;
    }
    
    
    /** set the image searcher
     *	@param searcher an ImageSearcher
     */
    public void setImagesearcher(ImageSearcher searcher)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("setting image searcher " + searcher);
	}
	
	if ( searcher != this.getImageSearcher() )
	{
	    ImageSearcher oldSearcher = this.getImageSearcher();
	    
	    if ( oldSearcher != null )
	    {
		oldSearcher.removeImageDownloadedListener(this.imageSearcherListener);
	    }
	    
	    this.imageSearcher = searcher;
	    
	    if ( searcher != null )
	    {
		searcher.addImageDownloadedListener(this.imageSearcherListener);
	    }
	    
	    this.firePropertyChange(PROPERTY_IMAGE_SEARCHER, oldSearcher, searcher);
	}
    }
    
    /** return the playable
     *	@return a Playable
     */
    public Playable getPlayable()
    {
	Playable playable = this.playableRef.get();
	
	return playable;
    }
    
    /** returns true if the two given String arrays are the same
     *	@param array0
     *	@param array1
     *	@return a boolean
     */
    private boolean arrayEquals(String[] array0, String[] array1)
    {
	return Arrays.equals(array0, array1);
    }
    
    /** return true if the given url represents the default cover
     *	@param url an URL
     */
    public boolean isDefaultCover(URL url)
    {
	boolean result = false;
	
	if ( url != null && this.defaultCoverUrl != null )
	{
	    return this.defaultCoverUrl.equals(url);
	}
	
	return result;
    }
    
    /** launch research */
    private void launchResearch()
    {
	Playable playable = this.getPlayable();
	
	this.currentPlayableDir = null;
	this.defaultCoverUrl   = null;

	if ( this.getImageSearcher() != null )
	{
	    this.getImageSearcher().cancel();
	}

	if ( playable instanceof SongItem )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("playable is a SongItem");
	    }
	    SongItem songItem = (SongItem)playable;

	    /** indicate to image searcher to start */
	    ImageSearcher searcher = this.getImageSearcher();

	    String criterion = null;
	    List<String> criterionList = new ArrayList<String>();

	    boolean albumAdded  = false;
	    boolean artistAdded = false;
	    boolean authorAdded = false;

	    if ( songItem.getAlbum() != null )
	    {
		criterion = songItem.getAlbum().trim();

		if ( criterion.length() > 0 )
		{
		    criterionList.add( criterion );
		    albumAdded = true;
		}
	    }

	    if ( songItem.getArtist() != null )
	    {
		criterion = songItem.getArtist().trim();

		if ( criterion.length() > 0 )
		{
		    criterionList.add( criterion );
		    artistAdded = true;
		}
	    }

	    if ( ! artistAdded && songItem.getAuthor() != null )
	    {
		criterion = songItem.getAuthor().trim();

		if ( criterion.length() > 0 )
		{
		    criterionList.add( criterion );
		    authorAdded = true;
		}
	    }
	    if ( ! artistAdded && ! authorAdded && songItem.getLeadArtist() != null )
	    {
		criterion = songItem.getLeadArtist().trim();

		if ( criterion.length() > 0 )
		{
		    criterionList.add( criterion );
		    authorAdded = true;
		}
	    }

	    if ( criterionList.size() == 0 )
	    {
		criterionList.add(songItem.getTitle());
	    }

	    String[] _additionalCriterions = this.getAdditionalCriterions();

	    String[] criterions = new String[criterionList.size() + (_additionalCriterions == null ? 0 : _additionalCriterions.length)];

	    /** fill criterionList */
	    if ( _additionalCriterions != null && _additionalCriterions.length > 0 )
	    {
		System.arraycopy(_additionalCriterions, 0, criterions, 0, _additionalCriterions.length);
	    }

	    if ( criterionList.size() > 0 )
	    {
		System.arraycopy( (String[])criterionList.toArray(new String[criterionList.size()]), 0,
				  criterions, _additionalCriterions == null ? 0 : _additionalCriterions.length, criterionList.size());
	    }

	    /* now we have our criterions, let's compare with the actual criterions of the ImageSearcher
	     * if the criterions are the same, no need to launch another research
	     */
	    boolean sameCriterions = this.arrayEquals(searcher.getCriterions(), criterions);

	    if ( sameCriterions )
	    {
		logger.info("criterions are the same than the last one --> nothing to do");
	    }
	    else
	    {
		/** clear the list of url */
		this.fireNoCovers();

		searcher.setCriterions(criterions);

		if ( logger.isDebugEnabled() )
		{
		    StringBuffer buffer = new StringBuffer();

		    if ( criterions != null )
		    {
			for(int i = 0; i < criterions.length; i++)
			{
			    buffer.append(criterions[i]);

			    if ( i < criterions.length - 1 )
			    {
				buffer.append(", ");
			    }
			}
		    }

		    logger.debug("applying criterions {" + buffer.toString() + "} to image searcher");
		}

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("launching image searcher " + searcher);
		}

		/* before launching searcher, see in the directory of the playable if there are image files */
		URL url = songItem.getValue();

		if ( url != null && "file".equals(url.getProtocol()) && url.getFile() != null )
		{
		    final String[] suffixes = ImageIO.getReaderFileSuffixes();

		    if ( suffixes != null && suffixes.length > 0 )
		    {
			if ( logger.isDebugEnabled() )
			{
			    StringBuffer buffer = new StringBuffer();
			    for(int i = 0; i < suffixes.length; i++)
			    {
				if ( buffer.length() > 0 )
				{
				    buffer.append(",");
				}
				buffer.append(suffixes[i]);
			    }

			    logger.debug("images extensions : {" + buffer.toString() + "}");
			}

			File f = null;

			try
			{	
			    f = new File(url.toURI());
			}
			catch (URISyntaxException ex)
			{
			    ex.printStackTrace();
			}

			if ( f != null && f.exists() )
			{
			    f = f.getParentFile();

			    currentPlayableDir = f;

			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("setting currentPlayableDir to " + currentPlayableDir);
			    }

			    String defaultCoverPath = null;

			    if ( defaultImageProperties != null && currentPlayableDir != null )
			    {
				defaultCoverPath = defaultImageProperties.getProperty(currentPlayableDir.getAbsolutePath());
			    }

			    File[] imageFiles = f.listFiles(new FilenameFilter()
			    {
				public boolean accept(File dir, String name)
				{
				    boolean result = false;

				    if ( name != null )
				    {
					for(int k = 0; k < suffixes.length; k++)
					{
					    String currentSuffix = suffixes[k];

					    if ( currentSuffix != null )
					    {
						if ( name.endsWith(currentSuffix) )
						{
						    result = true;
						    break;
						}
					    }
					}
				    }

				    return result;
				}
			    });

			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("image count in local : " + (imageFiles == null ? 0 : imageFiles.length));
			    }

			    if ( imageFiles != null )
			    {
				int ignoredIndex = -1;

				if ( defaultCoverPath != null )
				{
				    for(int k = 0; k < imageFiles.length && ignoredIndex == -1; k++)
				    {
					File currentFile = imageFiles[k];

					if ( defaultCoverPath.equals(currentFile.getAbsolutePath()) )
					{
					    /** add it as first --> it is the default cover */
					    try
					    {
						URL u = currentFile.toURL();

						defaultCoverUrl = u;

						urls.add(u);

						localImageFoundCount ++;

						if ( logger.isDebugEnabled() )
						{
						    logger.debug("indicating that an image has been detected on local as '" + u + "' for index : " + (urls.size() - 1));
						}

						fireCoverSearchEvent(CoverEventType.ADD, urls.size() - 1);

						ignoredIndex = k;
					    }
					    catch (MalformedURLException ex)
					    {
						logger.warn("could not get url from file " + f, ex);
					    }
					}
				    }
				}

				for(int k = 0; k < imageFiles.length; k++)
				{
				    if ( k != ignoredIndex )
				    {
					File currentFile = imageFiles[k];

					if ( logger.isDebugEnabled() )
					{
					    logger.debug("considering local image " + currentFile);
					}

					if ( f != null )
					{
					    try
					    {
						URL u = currentFile.toURL();

						urls.add(u);

						localImageFoundCount ++;

						if ( logger.isDebugEnabled() )
						{
						    logger.debug("indicating that an image has been detected on local as '" + u + "' for index : " + (urls.size() - 1));
						}

						fireCoverSearchEvent(CoverEventType.ADD, urls.size() - 1);
					    }
					    catch (MalformedURLException ex)
					    {
						logger.warn("could not get url from file " + f, ex);
					    }
					}
				    }
				}
			    }

			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("currentPlayableDir is " + currentPlayableDir);
			    }
			}
		    }
		}	    

		if ( this.useImageSearcher )
		{
		    /* and launch search */
		    searcher.search();
		}
		else
		{
		    if ( this.imageSearcherListener != null )
		    {
			this.imageSearcherListener.searchFinished(new ImageSearcherEvent(this.getImageSearcher()));
		    }
		}
	    }
	}
	else
	{
	    if ( logger.isInfoEnabled() )
	    {
		logger.info("playable " + playable + " is not a SongItem --> could not dtermine criterions");
	    }

	    this.fireNoCovers();
	}
    }
    
    /** initialize the playable
     *	@param playable a Playable
     */
    public void setPlayable(Playable playable)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("setting playable " + playable);
	}
	Playable current = this.getPlayable();
	
	if ( playable != current )
	{
	    this.playableRef = new WeakReference<Playable>(playable);
	 
	    this.firePropertyChange(PROPERTY_PLAYABLE, current, playable);
	    
	    if ( this.isEnabled() )
	    {
		this.launchResearch();
	    }
	}
    }

    /** return the additional criterions
     *	@return an array of String
     */
    public String[] getAdditionalCriterions()
    {
	return addCriterions;
    }

    /** initialize the additional criterions
     *	@param addCriterions an array of String
     */
    public void setAdditionalCriterions(String[] addCriterions)
    {
	if ( addCriterions != this.getAdditionalCriterions() )
	{
	    String[] oldCriterions = this.getAdditionalCriterions();

	    this.addCriterions = addCriterions;

	    this.firePropertyChange(PROPERTY_ADDITIONAL_CRITERIONS, oldCriterions, this.getAdditionalCriterions());
	}
    }
    
    /** ########################################################################
     *  ############### methods related to url search results ##################
     *  ######################################################################## */
    
    /** return the url on the given index
     *	@param index an integer
     *	@return an URL
     */
     public URL getURLAt(int index)
     {
	 URL result = null;
	 
	 if ( index >= 0 && index < this.urls.size() )
	 {
	     result = this.urls.elementAt(index);
	 }
	 
	 return result;
     }
     
     /** return the number of URL
      *	@return an integer
      */
     public int getURLCount()
     {
	 return this.urls.size();
     }
    
    /** add a new CoverSearchListener
     *	@param listener a CoverSearchListener
     */
    public void addCoverSearchListener(CoverSearchListener listener)
    {
	if ( listener != null )
	{
	    if ( this.coverListeners == null )
	    {
		this.coverListeners = new ArrayList<CoverSearchListener>();
	    }
	    
	    this.coverListeners.add(listener);
	}
    }
    
    /** remove a CoverSearchListener
     *	@param listener a CoverSearchListener
     */
    public void removeCoverSearchListener(CoverSearchListener listener)
    {
	if ( listener != null && this.coverListeners != null )
	{
	    this.coverListeners.remove(listener);
	}
    }
    
    /** indicate if necessary to all CoverSearchListener that no results are available */
    private void fireNoCovers()
    {
	/** indicate to listeners that all urls have been removed */
	int size = this.urls.size();
	if ( size > 0 )
	{
	    this.urls.clear();

	    this.localImageFoundCount = 0;
	    
	    this.fireCoverSearchEvent(CoverEventType.REMOVED, -1);
	}
    }
    
    /* fire a CoverSearchItemEvent
     *	@param eventType the type of event
     *	@param index0 
     */
    protected void fireCoverSearchEvent(CoverEventType eventType, int index0)
    {
	if ( eventType != null && this.coverListeners != null )
	{
	    CoverSearchItemEvent evt = null;
	    
	    for(int i = 0; i < this.coverListeners.size(); i++)
	    {
		CoverSearchListener currentListener = this.coverListeners.get(i);
		
		if ( currentListener != null )
		{
		    if ( evt == null )
		    {
			evt = new CoverSearchItemEvent(this, index0);
		    }

		    if ( CoverEventType.ADD.equals(eventType) )
		    {
			currentListener.imageAdded(evt);
		    }
		    else if ( CoverEventType.REMOVED.equals(eventType) )
		    {
			currentListener.imagesRemoved(evt);
		    }
		}
	    }
	}
    }

}
