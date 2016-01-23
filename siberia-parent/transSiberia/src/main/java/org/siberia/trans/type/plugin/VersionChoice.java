/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.trans.type.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.siberia.base.collection.SortedList;

/**
 *
 * Object that contains a list of available versions
 *  and a selected version among this list
 *
 * @author alexis
 */
public class VersionChoice implements Serializable
{   
    /** property preferred version */
    public static final String PROPERTY_PREFERRED_VERSION  = "preferred-version";
    
    /** indexed property linked to available versions */
    public static final String PROPERTY_AVAILABLE_VERSIONS = "available-versions";
    
    /** sorted list containing available version of the plugin */
    private List<Version>         availableVersions = null;
    
    /** selected version */
    private Version               selectedVersion   = null;
    
    /** property change support */
    private PropertyChangeSupport support           = new PropertyChangeSupport(this);
    
    /** Creates a new instance of VersionChoice */
    public VersionChoice()
    {	
	
    }
    
    /** set the selected version for this plugin
     *	@param version a Version
     *	
     *	@exception IllegalArgumentException if the version given is not an available version
     */
    public void setSelectedVersion(Version version)
    {
	boolean equals = false;
	
	if ( version == null )
	{
	    if ( this.getSelectedVersion() == null )
	    {	equals = true; }
	}
	else
	{   equals = version.equals(this.getSelectedVersion()); }
	
	if ( ! equals )
	{
	    /** it must appear in the list of available version if not null */
	    if ( version != null )
	    {
		if ( ! this.isAnAvailableVersion(version) )
		{
		    throw new IllegalArgumentException("the selected version has to be an available version");
		}
	    }
	    
	    Version oldVersion = this.getSelectedVersion();
	    
	    this.selectedVersion = version;
	    
	    this.firePropertyChange(PROPERTY_PREFERRED_VERSION, oldVersion, version);
	}
    }
    
    /** return the selected version for this plugin
     *	@return a Version
     */
    public Version getSelectedVersion()
    {	return this.selectedVersion; }
    
    /** return true if the given version appears in the list of available versions
     *	@return a boolean
     */
    private boolean isAnAvailableVersion(Version version)
    {
	boolean result = false;
	
	if ( version != null )
	{
	    if ( this.availableVersions != null )
	    {
		result = this.availableVersions.contains(version);
	    }
	}
	return result;
    }
    
    /** remove all available versions
     *	this method set the selected version to null and clear the list of available versions
     */
    public void removeAvailableVersions()
    {
	this.setSelectedVersion(null);
	
	if ( this.availableVersions != null )
	{
	    for(int i = 0; i < this.availableVersions.size(); i++)
	    {
		Object oldValue = this.availableVersions.get(i);
		Object newValue = null;

		this.fireIndexedPropertyChange(PROPERTY_AVAILABLE_VERSIONS, i, oldValue, newValue);
	    }

	    this.availableVersions.clear();
	}
    }
    
    /** remove a given version from available version list
     *	@param version the version to remove
     */
    public void removeAvailableVersion(Version version)
    {
	if ( version != null && this.availableVersions != null )
	{
	    int index = this.availableVersions.indexOf(version);
	    if ( index >= 0 )
	    {
		boolean removed = this.availableVersions.remove(version);
		
		if ( removed )
		{
		    /** fire indexed property change event for all version at index 'index' or more */
		    for(int i = index; i < this.availableVersions.size(); i++)
		    {
			Object oldValue = null;
			Object newValue = null;
			
			if ( i == index )
			{   oldValue = version; }
			else
			{   oldValue = this.availableVersions.get(i - 1); }
			newValue = this.availableVersions.get(i);
			
			this.fireIndexedPropertyChange(PROPERTY_AVAILABLE_VERSIONS, i, oldValue, newValue);
		    }
		}

		/** if the version removed was the selected version, then select the more recent version
		 *	among available versions
		 */
		if ( version.equals(this.getSelectedVersion()) )
		{
		    if ( this.availableVersions.size() == 0 )
		    {   this.setSelectedVersion(null); }
		    else
		    {   this.setSelectedVersion(this.availableVersions.get(this.availableVersions.size() - 1)); }
		}
	    }
	}
    }
    
    /** return true if the choice contains available version
     *	@return true if the choice contains available version
     */
    public boolean containsAvailableVersions()
    {	boolean result = false;
	
	if ( this.availableVersions != null )
	{
	    result = this.availableVersions.size() > 0;
	}
	
	return result;
    }
    
    /** add a given version from available version list
     *	this method does not modify the selected version even if the new version added is more recent than the current selected version
     *	@param version the version to remove
     *	@return true if the version was successfully added
     */
    public boolean addAvailableVersion(Version version)
    {
	return this.addAvailableVersion(version, false);
    }
    
    /** return the most recent version
     *	@return the most recent version or null if no available version
     */
    public Version getMostRecentVersion()
    {
	Version version = null;
	
	if ( this.availableVersions != null )
	{
	    version = this.availableVersions.get(this.availableVersions.size() - 1);
	}
	
	return version;
    }
    
    /** return a list copied from the available version
     *	@return a List
     */
    public List<Version> getAvailableVersions()
    {
	List<Version>     list     = null;
	
	if ( this.availableVersions != null && this.availableVersions.size() > 0 )
	{
	    synchronized(this)
	    {
		list = new ArrayList<Version>(this.availableVersions);
	    }
	}
	
	if ( list != null )
	{
	    Collections.sort(list);
	}
	
	if ( list == null )
	{   list = Collections.emptyList(); }
	
	return list;
    }
    
    /** add a given version from available version list
     *	@param version the version to remove
     *	@param moreRecentVersionAsSelected true to indicate to upadte selected version if the new version to add
     *	    is more recent as the current selected version
     *	@return true if the version was successfully added
     */
    public boolean addAvailableVersion(Version version, boolean moreRecentVersionAsSelected)
    {
	boolean result = false;
	
	if ( version != null )
	{
	    if ( this.availableVersions == null )
	    {
		this.availableVersions = new SortedList<Version>();
	    }
	    
	    if ( ! this.availableVersions.contains(version) )
	    {
		/** search the index where to put the new available version */
		if ( this.availableVersions.add(version) )
		{
		    int index = this.availableVersions.indexOf(version);

		    for(int i = index; i < this.availableVersions.size(); i++)
		    {
			Object oldValue = null;
			Object newValue = null;

			if ( i < this.availableVersions.size() - 1 )
			{
			    oldValue = this.availableVersions.get(i + 1);
			}
			else
			{
			    oldValue = null;
			}
			newValue = this.availableVersions.get(i);

			this.fireIndexedPropertyChange(PROPERTY_AVAILABLE_VERSIONS, i, oldValue, newValue);
		    }
		    
		    result = true;
		}
	    }
	}
	
	if ( moreRecentVersionAsSelected && result )
	{
	    this.setSelectedVersion( this.availableVersions.get(this.availableVersions.size() - 1) );
	}
	
	return result;
    }
    
    /** create a new VersionChoice with the same available version list reference and the same selected version
     *	@return a VersionChoice
     */
    public VersionChoice copy()
    {
	VersionChoice result = new VersionChoice();
	
	result.availableVersions = this.availableVersions;
	result.selectedVersion   = this.selectedVersion;
	
	return result;
    }

    public boolean equals(Object obj)
    {
	boolean result = false;
	
	if ( obj instanceof VersionChoice )
	{
	    VersionChoice other = (VersionChoice)obj;
	    
	    /** compare selection */
	    Version version      = this.getSelectedVersion();
	    Version otherVersion = other.getSelectedVersion();
	    if ( version == null )
	    {
		if ( otherVersion == null )
		{   result = true; }
	    }
	    else
	    {	result = version.equals(otherVersion); }
	    
	    if ( result )
	    {
		/** compare available version */
		List<Version> versions = this.availableVersions;
		List<Version> otherVersions = other.availableVersions;
		
		if ( versions == null )
		{
		    if ( otherVersions == null )
		    {	result = true; }
		}
		else
		{
		    /** compare size */
		    if ( otherVersions != null )
		    {
			if ( versions.size() == otherVersions.size() )
			{
			    /* for each item of versions, search if it appears in otherVersions */
			    int i = 0;
			    for(; i < versions.size(); i++)
			    {
				Version currentVersion = versions.get(i);
				
				if ( ! otherVersions.contains(currentVersion) )
				{   break; }
			    }
			    
			    if ( i == versions.size() )
			    {
				result = true;
			    }
			}
		    }
		}
	    }
	}
	
	return result;
    }
    
    /* #########################################################################
     * #################### PropertyChangeSupport methods ######################
     * ######################################################################### */
    
    /** add a PropertychangeListener
     *	@param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
	this.support.addPropertyChangeListener(listener);
    }
    
    /** add a PropertychangeListener
     *	@param propertyName the name of a property
     *	@param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
	this.support.addPropertyChangeListener(propertyName, listener);
    }
    
    /** remove a PropertychangeListener
     *	@param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
	this.support.removePropertyChangeListener(listener);
    }
    
    /** remove a PropertychangeListener
     *	@param propertyName the name of a property
     *	@param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
	this.support.removePropertyChangeListener(propertyName, listener);
    }
    
    /** send a PropertyChangeEvent
     *	@param propertyName the name of a property
     *	@param oldValue the old value of the property
     *	@param newValue the new value of the property
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
	this.support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    /** send an indexed PropertyChangeEvent
     *	@param propertyName the name of a property
     *	@param index the index of the change
     *	@param oldValue the old value of the property
     *	@param newValue the new value of the property
     */
    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue)
    {
	this.support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }
}
