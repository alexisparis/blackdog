/* 
 * Siberia editor : siberia plugin defining editor framework
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
package org.siberia.editor.launch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.siberia.type.SibType;

/**
 *
 * Abstract implementation of LaunchContext
 *
 * @author alexis
 */
public abstract class AbstractEditorLaunchContext implements EditorLaunchContext
{
    /** parameter map */
    private Map<String, Object> parameters = null;
    
    /** related item */
    private SibType             item       = null;
    
    /** Creates a new instance of AbstractEditorLaunchContext */
    public AbstractEditorLaunchContext()
    {   this(null); }
    
    /** Creates a new instance of AbstractEditorLaunchContext
     *  @param item a Sibtype
     */
    public AbstractEditorLaunchContext(SibType item)
    {   this.setItem(item); }

    /**
     * get a launch parameter
     * 
     * @param launchParameter the name of a parameter
     * @return the associated value
     */
    public synchronized Object getParameter(String launchParameter)
    {   Object value = null;
        
        if ( this.parameters != null )
        {   value = this.parameters.get(launchParameter); }
        
        return value;
    }

    /**
     * add a launch parameter
     * 
     * @param launchParameter the name of a parameter
     * @param value the associated value
     */
    public synchronized void addParameter(String launchParameter, Object value)
    {   if ( this.parameters == null )
        {   this.parameters = new HashMap<String, Object>(); }
        
        this.parameters.put(launchParameter, value);
    }

    /** return the object that the editor will render
     *  @return a SibType
     */
    public SibType getItem()
    {   return this.item; }

    /** initilize the object that the editor will render
     *  @param item a SibType
     */
    public void setItem(SibType item)
    {   this.item = item; }

    @Override
    public String toString()
    {
	StringBuffer buffer = new StringBuffer(50);
	
	buffer.append(this.getClass().getSimpleName() + " for item of kind " +
		      (this.getItem() == null ? null : this.getItem().getClass()) + 
		      " named " + (this.getItem() == null ? null : this.getItem().getName()) +
		      " with parameters : ");
	
	if ( this.parameters == null )
	{
	    buffer.append("null");
	}
	else
	{
	    buffer.append("[");
	    
	    Iterator<Map.Entry<String, Object>> entries = this.parameters.entrySet().iterator();
	    
	    while(entries.hasNext())
	    {
		Map.Entry<String, Object> entry = entries.next();
		buffer.append(entry.getKey() + "=" + entry.getValue());
		
		if ( entries.hasNext() )
		{
		    buffer.append(";");
		}
		
	    }
	    buffer.append("]");
	}
	
	return buffer.toString();
    }
}
