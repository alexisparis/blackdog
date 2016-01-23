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
package org.blackdog.type;

import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.ResourceBundle;
import org.blackdog.type.playlist.RemoveHandler;
import org.blackdog.type.playlist.RemoveHandlerAdapter;
import org.siberia.type.AbstractSibType;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * Define an objects that contains all audio resources that can be manage in
 * blackdog application
 *
 * @author alexis
 */
@Bean(  name="Audio resources",
        internationalizationRef="org.blackdog.rc.i18n.type.AudioResources",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class AudioResources extends AbstractSibType
{
    /** playlist list */
    private PlayListList      playLists			= null;
    
    /** main playlist that contains all audio items scanned of the current machine (except those in removal drive) */
    private ScannablePlayList library			= null;
    
    /** radio list */
    private RadioList         radios			= null;
    
    /* device list */
    private DeviceList        devices                   = null;
    
    /** main playlist remove handler */
    private RemoveHandler     mainPlaylistRemoveHandler = null;
    
    /** Creates a new instance of AudioResources */
    public AudioResources()
    {   
        super();
	
	try
	{   
	    this.setRemovable(false);
	}
	catch(PropertyVetoException e)
	{   e.printStackTrace(); }
	
        ResourceBundle rb = ResourceBundle.getBundle(AudioResources.class.getName());
        
        this.playLists = new PlayListList();
        try
        {   this.playLists.setName(rb.getString("playlistsName"));
            this.playLists.setNameCouldChange(false);
	    this.playLists.setRemovable(false);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        this.addChildElement(this.playLists);
	
	this.mainPlaylistRemoveHandler = new RemoveHandlerAdapter()
	{
	    /** called when a remove has been ordered by index on the given playlist
	     *	before calling super
	     *	@param playlist a PlayList
	     *	@param index
	     *	@param toRemove the object that has to be removed
	     */
	    @Override
	    public void preRemoveByIndex(PlayList playList, int index, Object toRemove)
	    {	
		super.preRemoveByIndex(playList, index, toRemove);
		
		if ( toRemove != null && playList == getPlayListLibrary() )
		{
		    PlayListList playListList = getPlayListList();

		    if ( playListList != null )
		    {
			for(int i = 0; i < playListList.size(); i++)
			{
			    Object current = playListList.get(i);

			    if ( current != playList && current instanceof SibList )
			    {
				((SibList)current).remove(toRemove);
			    }
			}
		    }
		}
	    }
    
	    /** called when a remove has been ordered by reference on the given playlist
	     *	before calling super
	     *	@param playlist a PlayList
	     *	@param toRemove the object that has to be removed
	     */
	    @Override
	    public void preRemoveByReference(PlayList playList, Object toRemove)
	    {	
		super.preRemoveByReference(playList, toRemove);
		
		if ( toRemove != null && playList == getPlayListLibrary() )
		{
		    PlayListList playListList = getPlayListList();

		    if ( playListList != null )
		    {
			for(int i = 0; i < playListList.size(); i++)
			{
			    Object current = playListList.get(i);

			    if ( current != playList && current instanceof SibList )
			    {
				((SibList)current).remove(toRemove);
			    }
			}
		    }
		}
	    }
	    
	    /** called when a remove has been ordered by giving a collection on the given playlist
	     *	before calling super
	     *	@param playlist a PlayList
	     *	@param collectionToRemove the collection to remove
	     */
	    @Override
	    public void preRemoveByCollectionReference(PlayList playList, Collection collectionToRemove)
	    {	
		super.preRemoveByCollectionReference(playList, collectionToRemove);
		
		if ( collectionToRemove != null && playList == getPlayListLibrary() )
		{
		    PlayListList playListList = getPlayListList();

		    if ( playListList != null )
		    {
			for(int i = 0; i < playListList.size(); i++)
			{
			    Object current = playListList.get(i);

			    if ( current != playList && current instanceof SibList )
			    {
				((SibList)current).removeAll(collectionToRemove);
			    }
			}
		    }
		}
	    }
	};
        
        /** create the library playlist
	 *  override remove methods to also delete items from other playlist if they
	 *  are removed from the main playlist
	 */
        this.library = new ScannablePlayList();
	
	this.library.setRemoveHandler(this.mainPlaylistRemoveHandler);
	
	this.library.setMainPlayList(true);
        try
        {   this.library.setName(rb.getString("libraryName"));
            this.library.setRemovable(false);
            this.library.setNameCouldChange(false);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        this.playLists.addLibrary(this.library);
        
        /** create radios list */
        this.radios = new RadioList();
        try
        {   
	    this.radios.setContentItemAsChild(true);
	    this.radios.setName(rb.getString("radiosName"));
            this.radios.setNameCouldChange(false);
            this.radios.setRemovable(false);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        this.addChildElement(this.radios);
        
//        this.devices = new DeviceList();
//        try
//        {   this.devices.setName(rb.getString("devicesName"));
//            this.devices.setNameCouldChange(false);
//        }
//        catch (PropertyVetoException ex)
//        {   ex.printStackTrace(); }
//        
//        this.addChildElement(this.devices);
    }
    
    /** return the list of PlayList
     *  @return a SibList containing Playlist
     */
    public PlayListList getPlayListList()
    {   return this.playLists; }
    
    /** return the main playlist that is considered as the library
     *  @return a ScannablePlayList
     */
    public ScannablePlayList getPlayListLibrary()
    {
        return this.library;
    }
    
    /** initialize the libray playlist
     *	@param list a ScannablePlayList
     */
    public void setPlayListLibrary(ScannablePlayList list)
    {
	ScannablePlayList old = this.getPlayListLibrary();
	
	if ( old != null )
	{
	    try
	    {
		old.setRemovable(true);
		boolean removed = this.getPlayListList().remove(old);
		this.getPlayListList().removeChildElement(old);
		old.setRemoveHandler(null);
	    }
	    catch(PropertyVetoException e)
	    {
		e.printStackTrace();
	    }
	}
	
	this.library = list;
	if ( this.getPlayListLibrary() != null )
	{
	    this.getPlayListLibrary().setMainPlayList(true);
	    this.getPlayListLibrary().setRemoveHandler(this.mainPlaylistRemoveHandler);
	}
	
	this.getPlayListList().addLibrary(list);
    }
    
    /** return the list of RadioItem
     *  @return a SibList containing RadioItem
     */
    public RadioList getRadioList()
    {   return this.radios; }
    
    /** return the list of AbstractDevice
     *  @return a SibList containing AbstractDevice
     */
    public DeviceList getDeviceList()
    {   return this.devices; }
    
}
