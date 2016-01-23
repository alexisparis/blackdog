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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public abstract class AbstractFilter2 extends Filter
{
    /** default initial size of the inner list */
    protected static final int DEFAULT_LIST_INITIAL_SIZE = 100;
    
    protected List<Integer>	      toPrevious = null;
    
    /** table model */
    private WeakReference<TableModel> modelRef   = null;
    
    /** property change support */
    private PropertyChangeSupport     support    = new PropertyChangeSupport(this);
    
    /**
     * Creates a new instance of AbstractFilter
     */
    public AbstractFilter2()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of AbstractFilter
     * 
     * @param model a TableModel
     */
    public AbstractFilter2(TableModel model)
    {
        this(model, DEFAULT_LIST_INITIAL_SIZE);
    }
    
    /**
     * Creates a new instance of AbstractFilter
     * 
     * @param model a TableModel
     * @param initialSize the initial size of the internal map that redirect index
     */
    public AbstractFilter2(TableModel model, int initialSize)
    {   super();
        
        this.setTableModel(model);
        
        this.toPrevious = new ArrayList<Integer>(initialSize);
    }
    
    /** return the model related to this filter
     *  @return a TableModel
     */
    public TableModel getTableModel()
    {
        return this.modelRef.get();
    }
    
    /** initialize the model related to this filter
     *  @param model a TableModel
     */
    public void setTableModel(TableModel model)
    {
        this.modelRef = new WeakReference<TableModel>(model);
    }
    
    /** return the map that represents redirection
     *  @return a List containing integer
     */
    protected List<Integer> getToPreviousList()
    {
        return this.toPrevious;
    }
    
    @Override
    public int getSize()
    {
        return toPrevious.size();
    }
    
    @Override
    protected int mapTowardModel(int row)
    {
	int result = -1;
	
	if ( toPrevious != null && row >= 0 && row < toPrevious.size() )
	{
	    result = toPrevious.get(row);
	}
	
	return result;
    }
    
    @Override
    protected void reset()
    {
        toPrevious.clear();
        int inputSize = getInputSize();
        fromPrevious = new int[inputSize];  // fromPrevious is inherited protected
        for (int i = 0; i < inputSize; i++)
	{
            fromPrevious[i] = -1;
        }
    }
    
    @Override
    protected void init()
    {
        toPrevious = new ArrayList<Integer>();
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
