/*
 * blackdog types : define kind of items maanged by blackdog
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

import java.lang.ref.WeakReference;
import org.blackdog.type.playlist.RemoveHandler;
import org.blackdog.type.session.PlayableSession;

import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * List that contains AudioItem
 *
 * @author alexis
 */
@Bean(
name = "playlist", internationalizationRef = "org.blackdog.rc.i18n.type.PlayList", expert = false, hidden = false, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
	)
	public class PlayList
	extends SibList
	implements HierarchicalPropertyChangeListener
{
    /** property main playlist */
    public static final String PROPERTY_MAIN = "main";
    
    /** property playable session */
    public static final String PROPERTY_SESSION = "current-session";
    
    /** main playlist */
    private boolean main = false;
    
    /** add HierarchicalPropertychangeListener on all items */
    protected boolean addHierarchicalListenerOnContentItems = true;
    
    /** playable session */
    protected transient WeakReference<PlayableSession> session = new WeakReference<PlayableSession>(null);
    
    /* remove handler */
    private transient   RemoveHandler   removeHandler = null;
    
    /** Creates a new instance of PlayList */
    public PlayList(  )
    {
	super(  );
	
	/* no listener, no PropertyVetoException will be throwed */
	try
	{
	    this.setAllowedClass( SongItem.class );
	    this.setEditAuthorization( true );
	    this.setCreateAuthorization( true );
	    this.setRemoveAuthorization( true );
	    this.setContentItemAsChild( false );
	}
	catch ( PropertyVetoException ex )
	{
	    ex.printStackTrace(  );
	}
    }

    /** return the RemoveHandler actually linked to this PlayList
     *	@return a RemoveHandler
     */
    public RemoveHandler getRemoveHandler()
    {
	return removeHandler;
    }

    /** initialize the RemoveHandler actually linked to this PlayList
     *	@param remoevHandler a RemoveHandler
     */
    public void setRemoveHandler(RemoveHandler removeHandler)
    {
	this.removeHandler = removeHandler;
    }
    
    /** return the current PlayableSession
     *        @return a PlayableSession
     */
    public PlayableSession getCurrentSession(  )
    {
	return this.session.get();
    }
    
    /** initailize the current PlayableSession
     *        @param session a PlayableSession
     */
    public void setCurrentSession( PlayableSession session )
    {
	PlayableSession oldSession = this.getCurrentSession();
	
	if ( oldSession != session )
	{
	    this.session = new WeakReference<PlayableSession>(session);
	    
	    this.firePropertyChange( PROPERTY_SESSION, oldSession, this.getCurrentSession(  ) );
	}
    }
    
    /** tell if the type is considered as leaf
     *  @return true if the element must be considered as leaf
     */
    @Override
    public boolean isLeaf(  )
    {
	return true;
    }
    
    /** return true if this playlist is the main playlist
     *        @return a boolean
     */
    public boolean isMainPlayList(  )
    {
	return main;
    }
    
    /** indicate true if this playlist is the main playlist
     *        @param maain a boolean
     */
    public void setMainPlayList( boolean main )
    {
	if ( main != this.isMainPlayList(  ) )
	{
	    this.main = main;
	    
	    this.firePropertyChange( PROPERTY_MAIN, ! main, main );
	}
    }
    
    @Override
    protected void setCollection( Collection collection )
    {
	Collection oldCollec = this.getCollection(  );
	
	if ( this.addHierarchicalListenerOnContentItems && ( oldCollec != null ) )
	{
	    Iterator it = oldCollec.iterator(  );
	    
	    while ( it.hasNext(  ) )
	    {
		Object current = it.next(  );
		
		if ( current instanceof SibType )
		{
		    ( (SibType) current ).removeHierarchicalPropertyChangeListener( this );
		}
	    }
	}
	
	super.setCollection( collection );
	
	Collection newCollec = this.getCollection(  );
	
	if ( this.addHierarchicalListenerOnContentItems && ( newCollec != null ) )
	{
	    Iterator it = newCollec.iterator(  );
	    
	    while ( it.hasNext(  ) )
	    {
		Object current = it.next(  );
		
		if ( current instanceof SibType )
		{
		    ( (SibType) current ).addHierarchicalPropertyChangeListener( this );
		}
	    }
	}
    }
    
    /** add a new element
     *  @param o the element to add
     *        @param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     *        @param addItemAsChild true to add this item as a child
     *  @return true if the collection was modified
     */
    @Override
    protected boolean add( Object o, boolean fireContentChanged, boolean addItemAsChild )
    {
	boolean result = super.add( o, fireContentChanged, addItemAsChild );
	
	if ( result && o instanceof SibType )
	{
	    ( (SibType) o ).addHierarchicalPropertyChangeListener( this );
	}
	
	return result;
    }
    
    /** remove the element at the given index
     *  @param index an integer representing an index in the list
     *  @return the removed element
     */
    @Override
    public Object remove(int index)
    {
	RemoveHandler handler = this.getRemoveHandler();
	if ( handler != null )
	{
	    Object toRemove = this.get(index);
	    
	    handler.preRemoveByIndex(this, index, toRemove);
	}

	Object removed = super.remove(index);
	
	if ( handler != null )
	{
	    handler.postRemoveByIndex(this, index, removed);
	}
	
	return removed;
    }

    /** remove an element
     *  @param o the object to remove
     *  @return true if the collection was modified
     */
    @Override
    public boolean remove(Object o)
    {
	RemoveHandler handler = this.getRemoveHandler();
	if ( handler != null )
	{
	    handler.preRemoveByReference(this, o);
	}

	boolean result = super.remove(o);
	
	if ( handler != null )
	{
	    handler.postRemoveByReference(this, o);
	}
	
	return result;
    }

    /** remove a collection
     *  @param c a collection to remove
     *  @return true if the collection was modified
     */
    @Override
    public boolean removeAll(Collection c)
    {
	RemoveHandler handler = this.getRemoveHandler();
	if ( handler != null )
	{
	    handler.preRemoveByCollectionReference(this, c);
	}
	
	boolean result = super.removeAll(c);
	
	
	if ( handler != null )
	{
	    handler.postRemoveByCollectionReference(this, c);
	}
	
	return result;
    }
    
    /** add an item at a specified position
     *  @param position an integer
     *  @param object the object to add
     *        @param fireContentChanged true to fire a ContentChangedEvent if the item was successfully added
     *        @param addItemAsChild true to add this item as a child
     */
    @Override
    protected boolean add( int position, Object object, boolean fireContentChanged, boolean addItemAsChild )
    {
	boolean result = super.add( position, object, fireContentChanged, addItemAsChild );
	
	if ( result && object instanceof SibType )
	{
	    ( (SibType) object ).addHierarchicalPropertyChangeListener( this );
	}
	
	return result;
    }
    
    /** remove an element
     *  @param o the object to remove
     *        @param fireContentChanged true to fire a ContentChangedEvent if the item were successfully removed
     *        @param removeItemAsChild true to remove this item as a child
     *  @return true if the collection was modified
     */
    @Override
    protected boolean remove( Object o, boolean fireContentChanged, boolean removeItemAsChild )
    {
	boolean result = super.remove( o, fireContentChanged, removeItemAsChild );
	
	if ( result && o instanceof SibType )
	{
	    ( (SibType) o ).removeHierarchicalPropertyChangeListener( this );
	}
	
	return result;
    }
    
    /** ########################################################################
     *  ########### HierarchicalPropertyChangeListener implementation ##########
     *  ######################################################################## */
    public void propertyChange( HierarchicalPropertyChangeEvent hierarchicalPropertyChangeEvent )
    {
	/* make as if the hierarchicalPropertyChangeEvent comes from a Child */
	if ( hierarchicalPropertyChangeEvent != null )
	{
	    /* modify event */
	    hierarchicalPropertyChangeEvent.setCurrentSource( this );
	    
	    /* and force parent to warn its own HierarchicalPropertyChangeEvent */
	    this.fireHierarchicalPropertyChangeEvent( hierarchicalPropertyChangeEvent );
	}
    }
}
