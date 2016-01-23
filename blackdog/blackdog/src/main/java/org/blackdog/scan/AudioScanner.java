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
package org.blackdog.scan;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogTypesPlugin;
import org.blackdog.action.impl.ScanDialog;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.kernel.SongItemFactoryRegister;
import org.blackdog.report.ReportManager;
import org.blackdog.type.Scannable;
import org.blackdog.type.ScannablePlayList;
import org.blackdog.type.SongItem;
import org.blackdog.type.customizer.SongItemCustomizer;
import org.blackdog.type.factory.SongItemCreationException;
import org.blackdog.type.factory.SongItemFactory;
import org.blackdog.report.TagsUpdateReport;
import org.blackdog.type.scan.IgnoredDirectories;
import org.blackdog.type.scan.IgnoredDirectory;
import org.blackdog.type.scan.ScanProperties;
import org.blackdog.type.scan.ScannedDirectories;
import org.blackdog.type.scan.ScannedDirectory;
import org.siberia.ResourceLoader;
import org.siberia.base.file.Directory;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;

/**
 *
 * @author alexis
 */
public class AudioScanner extends SwingWorker<Integer, String>
{  
    /** logger */
    private static Logger logger = Logger.getLogger(AudioScanner.class);
    
    /** packet size */
    private static final int MAX_PACKET_SIZE = 51;
    
    /** file filter used to determine the audio items to add in the library */
    private static FileFilter audioFileFilter = new FileFilter()
                                                {
                                                    public boolean accept(File pathname)
                                                    {   boolean result = false;
                                                        if ( pathname.isDirectory() )
                                                        {   result = true; }
                                                        else
                                                        {   int index = pathname.getName().lastIndexOf('.');
                                                            
                                                            if ( index != -1 )
                                                            {   String extension = pathname.getName().substring(index + 1);
                                                                
                                                                if ( MusikKernelResources.getInstance().getSupportedAudioExtensions().contains(extension) )
                                                                {   result = true; }
                                                            }
                                                        }

                                                        return result;
                                                    }
                                                };
                                                
    /** approximative count of file that are considered as audio file */
    private int                      audioFileCount     = 0;
    
    private int                      audioFileProcessed = 0;
    
    /** scan dialog */
    private ScanDialog               dialog             = null;
    
    /** factory register */
    private SongItemFactoryRegister  factoryRegister    = new SongItemFactoryRegister();
    
    /** scan properties */
    private ScanProperties           properties         = null;
    
    /** the list to update */
    private ScannablePlayList        list               = null;
    
    /** report */
    private TagsUpdateReport         report             = null;
    
    /** sub list which allow to accelerate scanning */
    private List                     workingList        = new ArrayList(MAX_PACKET_SIZE);
    
    /** Creates a new instance of AudioScanner */
    public AudioScanner(ScanDialog dialog, ScannablePlayList list)
    {   
        this(dialog, (list instanceof Scannable ? ((Scannable)list).getScanProperties() : null), list);
    }
    
    /** Creates a new instance of AudioScanner */
    private AudioScanner(ScanDialog dialog, ScanProperties properties, ScannablePlayList list)
    {   
        if ( properties == null )
        {
            throw new IllegalArgumentException("the properties of the scan could not be null");
        }
        
        if ( list == null )
        {
            throw new IllegalArgumentException("the list could not be null");
        }
        
        this.dialog = dialog;
        
        this.properties = properties;
        this.list       = list;
	
	this.report = new TagsUpdateReport();
    }
    
    /** return the report created by the scanner
     *	@return a TagsUpdateReport
     */
    public TagsUpdateReport getReport()
    {
	return this.report;
    }
    
    /** return the total file count to process
     *  @return an integer
     */
    public int getTotalAudioFileCount()
    {   if ( this.audioFileCount == 0 )
        {   this.searchAudioItems(true); }
        
        return this.audioFileCount;
    }
    
    protected Integer doInBackground() throws Exception
    {   
	try
	{
	    if ( this.dialog != null )
	    {
		this.dialog.setTotalFileCount(this.getTotalAudioFileCount());
	    }

	    if ( logger.isInfoEnabled() )
	    {
		logger.info("audio file count : " + this.audioFileCount);
	    }

	    this.searchAudioItems(false);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    while(MusikKernelResources.getInstance().isScanRunning())
	    {
		if ( MusikKernelResources.getInstance().setScanRunning(false) )
		{
		    break;
		}
	    }
	}
        
        return 0;
    }


    protected void process(List<String> chunks)
    {
        if ( this.dialog != null && chunks != null )
        {
            this.dialog.getDirectoryLabel().setText(chunks.get(chunks.size() - 1));
        }
    }

    protected void done()
    {   
	this.transfertWorkingItems();
	
        this.setProgress(100);
        
        if ( this.dialog != null )
        {
            this.dialog.getDirectoryLabel().setText("");
            
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {   e.printStackTrace(); }
            
            this.dialog.setVisible(false);
            this.dialog.dispose();
        }
	
	new ReportManager().processReport(this.getReport());
    }
    
    /** method that search for AudioItem and add them in the library
     *  @param fictive if true this method does not feed the PlayList library but count the approximative count of audio file found
     */
    public void searchAudioItems(final boolean fictive)
    {
        this.audioFileProcessed = 0;
        
        Runnable run = new Runnable()
        {
            public void run()
            {
                long time = System.currentTimeMillis();
                ScannedDirectories scannedDirs = properties.getScannedDirectories();

                if ( scannedDirs != null && scannedDirs.size() > 0 )
                {   
		    for(int i = 0; i < scannedDirs.size(); i++)
		    {   Object o = scannedDirs.get(i);

			if ( o instanceof ScannedDirectory )
			{   ScannedDirectory scannedDir = (ScannedDirectory)o;

			    Directory directory = scannedDir.getValue();

			    if ( directory != null )
			    {   searchAudioItems(fictive, directory.getAbsolutePath()); }
			}
		    }
                }

                long length = System.currentTimeMillis() - time;

                logger.info("scan duration : " + length + " ms");
            }
        };
        
        run.run();
//        new Thread(run).start();
    }
    
    /** method that search for AudioItem and add them in the library for a gievn directory
     *  @param fictive if true this method does not feed the PlayList library but count the approximative count of audio file found
     *  @param directory the path of a directory
     */ 
    public void searchAudioItems(boolean fictive, String directory)
    {
        File f = new File(directory);
        
        if ( ! fictive )
        {   this.publish(directory);
            
            try
            {   Thread.sleep(10); }
            catch(InterruptedException e)
            {   e.printStackTrace(); }
        }
        
        if ( f.exists() )
        {   if ( f.isDirectory() )
            {   /** check if the directory can be scanned */
                if ( canProcessDirectory(directory) )
                {   
                    File[] children = f.listFiles(audioFileFilter);
                    
                    for(int i = 0; i < children.length; i++)
                    {   File current = children[i];
                        
                        if ( current.isDirectory() )
                        {   searchAudioItems(fictive, current.getAbsolutePath()); }
                        else
                        {
                            if ( fictive )
                            {   this.audioFileCount ++; }
                            else
                            {
                                SongItem item = null;
                                
                                String name = current.getName();
                                int index = name.lastIndexOf('.');
                                String extension = name.substring(index + 1);
                                
                                SongItemFactory factory = null;
                                
                                try
                                {   factory = factoryRegister.get(extension); }
                                catch(Exception e)
                                {   e.printStackTrace(); }
                                
                                if ( logger.isDebugEnabled() )
                                {
                                    logger.debug("using factory " + factory + " for file : " + current);
                                }
                                
                                /* determine if the url already appears in the current list */
                                try
                                {
                                    URL url = current.toURI().toURL();
				    
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("considering file with url : " + url);
				    }

                                    if ( this.listAlreadyContains(url) )
                                    {
                                        if ( logger.isDebugEnabled() )
                                        {
                                            logger.debug("list already contains an item linked to url : '" + url + "'");
                                        }
                                    }
                                    else
                                    {
                                        item = factory.createSongItem(url, report);
					
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("asking factory to create item according to file with url " + url);
					}
					
					List<SongItemCustomizer> customizers = MusikKernelResources.getInstance().getSongItemCustomizers();
					
					/** ask to all SongItem customizer to finish item initialization */
					if ( customizers != null )
					{
					    ListIterator<SongItemCustomizer> it = customizers.listIterator();
					    
					    while(it.hasNext())
					    {
						SongItemCustomizer currentCustomizer = it.next();
						
						try
						{
						    if ( currentCustomizer != null )
						    {
							if ( logger.isDebugEnabled() )
							{
							    logger.debug("asking customizer " + currentCustomizer + " to customize " + item);
							}

							currentCustomizer.customize(report, item);
							
							if ( logger.isDebugEnabled() )
							{
							    logger.debug("customization if " + item + " with " + currentCustomizer + " ended");
							}
						    }
						}
						catch(Exception e)
						{
						    logger.error("error while asking customizer " + currentCustomizer + " to customize " + item, e);
						}
					    }
					}
					
					if ( item != null )
					{
					    try
					    {
						item.setCreationDate(Calendar.getInstance().getTime());
					    }
					    catch(PropertyVetoException e)
					    {
						String message = "unable to initialize creation date.";
						logger.warn(message, e);
						report.addErrorLog(item, message, e);
					    }
					}

                                        if ( logger.isDebugEnabled() )
                                        {
                                            logger.debug("adding audio item at '" + current + "' in the library");
                                        }

					this.workingList.add(item);
                                    }
                                }
                                catch(MalformedURLException e)
                                {   
				    String message = "unable to create an URL for file " + current;
				    logger.error(message, e);
				    report.addErrorLog(item, message, e);
                                    continue;
                                }
                                catch(SongItemCreationException e)
                                {   
				    String message = "unable to create a SongItem for file " + current;
				    logger.error(message, e);
				    report.addErrorLog(item, message, e);
                                    continue;
                                }
                                catch(Exception e)
                                {
				    e.printStackTrace();
				    String message = "got exception while craeting SongItem for file " + current;
				    logger.error(message, e);
				    report.addErrorLog(item, message, e);
                                    continue;
				}
				finally
				{
				    this.audioFileProcessed ++;
				}
                                
                                if ( this.dialog != null )
                                {
                                    this.dialog.setFileProcessedCount(this.audioFileProcessed);
                                }
                                
                                int progress = Math.min(100,
                                        (int)Math.floor((((double)this.audioFileProcessed) / ((double)this.audioFileCount)) * 100) );
                                
                                this.setProgress( progress );
				
				if ( this.workingList.size() >= MAX_PACKET_SIZE - 1 ) // to avoid list size extension
				{
				    transfertWorkingItems();
				}
                            }
                        }
                    }
                }
            }
            else
            {   if ( logger.isDebugEnabled() )
                {
                    logger.debug("enable to search audio item in '" + directory + "' because it is not a directory");
                }
            }
        }
        else
        {   if ( logger.isDebugEnabled() )
            {
                logger.debug("enable to search audio item in '" + directory + "' because it does not exists");
            }
        }
    }
    
    /** transfer item from the working list to the main list */
    private void transfertWorkingItems()
    {
	if ( this.workingList != null && this.workingList.size() > 0 )
	{
	    this.list.addAll(this.workingList);

	    this.workingList.clear();
	}
    }
    
    /** indicates if the given directory appears to be in a declared scanned directory and not in an ignored directory
     *  @param directory the path of a directory
     *  @return true if the directory can be considered in scan process
     */
    private boolean canProcessDirectory(String directory)
    {   
        boolean canBeProcessed = false;
        
        if ( directory != null && directory.length() > 0 )
        {
            ScannedDirectories scannedDirs = this.properties.getScannedDirectories();

	    boolean isInScannedDirs = false;

	    for(int i = 0; i < scannedDirs.size(); i++)
	    {   Object o = scannedDirs.get(i);

		if ( o instanceof ScannedDirectory )
		{   ScannedDirectory dir = (ScannedDirectory)o;
		    String path = (dir.getValue() == null ? null : dir.getValue().getAbsolutePath());

		    if ( path != null )
		    {   if ( directory.startsWith(path) )
			{   isInScannedDirs = true;
			    break;
			}
		    }
		}
	    }
                
	    if ( isInScannedDirs )
	    {   canBeProcessed = true;
		IgnoredDirectories ignoredDirs = this.properties.getIgnoredDirectories();
   
		for(int i = 0; i < ignoredDirs.size(); i++)
		{   Object o = ignoredDirs.get(i);

		    if ( o instanceof IgnoredDirectory )
		    {   IgnoredDirectory dir = (IgnoredDirectory)o;
			String path = (dir.getValue() == null ? null : dir.getValue().getAbsolutePath());

			if ( path != null )
			{   if ( directory.startsWith(path) )
			    {   canBeProcessed = false;
				break;
			    }
			}
		    }
		}
            }
        }
        
        if ( logger.isDebugEnabled() )
        {
            logger.debug("can scan into directory '" + directory + "' ? " + canBeProcessed);
        }
        
        return canBeProcessed;
    }
    
    /** method that determine if an item is already assigned to the given url
     *  @param url an URL
     *  @return true if an item is already assigned to the given url
     */
    private boolean listAlreadyContains(URL url)
    {
        boolean result = false;
        
        ScannablePlayList list = this.list;
        
        if ( url != null && list != null )
        {
	    result = list.getItemLinkedTo(url) != null;
	}
        
        return result;
    }
    
}
