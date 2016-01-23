/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.filter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.decorator.Filter;

/**
 *
 * Class that extends Filter to simplify filter creation
 *
 * @author alexis
 */
public abstract class AbstractFilter extends Filter
{
    /** default initial size of the inner map */
    protected static final int DEFAULT_MAP_INITIAL_SIZE = 100;
    
    /** table model */
    private TableModel            model            = null;
		
    /** a map redirecting indexes */
    private Map<Integer, Integer> indexRedirection = null;
    
    /** property change support */
    private PropertyChangeSupport support          = new PropertyChangeSupport(this);
    
    /**
     * Creates a new instance of AbstractFilter
     */
    public AbstractFilter()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of AbstractFilter
     * 
     * @param model a TableModel
     */
    public AbstractFilter(TableModel model)
    {
        this(model, DEFAULT_MAP_INITIAL_SIZE);
    }
    
    /**
     * Creates a new instance of AbstractFilter
     * 
     * @param model a TableModel
     * @param initialSize the initial size of the internal map that redirect index
     */
    public AbstractFilter(TableModel model, int initialSize)
    {   super();
        
        this.setTableModel(model);
        
        this.indexRedirection = new HashMap<Integer, Integer>(initialSize);
    }
    
    /** return the model related to this filter
     *  @return a TableModel
     */
    public TableModel getTableModel()
    {
        return this.model;
    }
    
    /** initialize the model related to this filter
     *  @param model a TableModel
     */
    public void setTableModel(TableModel model)
    {
        this.model = model;
    }
    
    /** return the map that represents redirection
     *  @return a Map containing integer
     */
    protected Map<Integer, Integer> getRedirectionMap()
    {
        return this.indexRedirection;
    }
    
    /** method to override to return a custom size
     *  @return the size or null to let default behaviour
     */
    protected Integer getSizeImpl()
    {
        return null;
    }

    @Override
    public final int getSize()
    {   
        int size = 0;
        
        Integer i = this.getSizeImpl();
        if ( i != null )
        {
            size = i.intValue();
        }
        else
        {
            size = this.getRedirectionMap().size();
        }
        
	System.out.println("filter :: getSize returns " + size);
        return size;
    }

    @Override
    protected int mapTowardModel(int arg0)
    {	int result = arg0;
        
        Integer r = this.getRedirectionMap().get(arg0);
        if ( r == null )
        {   
	    result = -1;
	}
	else
	{
	    result = r.intValue();
	}
	
	System.out.println("filter :: maptowardModel(" + arg0 + ") returns " + result);

        return result; 
    }

    @Override
    protected void reset()
    {	this.getRedirectionMap().clear(); }

    protected void init()
    {
        /* do nothing */
    }
    
    /** ######################################################################## 
     *  ################### PropertyChangeListener methods #####################
     *  ######################################################################## */
    
    /** add a PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        this.support.addPropertyChangeListener(listener);
    }
    
    /** add a PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        this.support.addPropertyChangeListener(propertyName, listener);
    }
    
    /** remove a PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        this.support.removePropertyChangeListener(listener);
    }
    
    /** remove a PropertyChangeListener
     *  @param propertyName the name of a property
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        this.support.removePropertyChangeListener(propertyName, listener);
    }
    
    /** fire a PropertyChangeEvent
     *  @param propertyName the name of the modified property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        this.support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
}
