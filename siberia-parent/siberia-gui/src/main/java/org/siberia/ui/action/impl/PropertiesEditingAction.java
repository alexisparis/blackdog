/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.action.impl;

import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import org.siberia.properties.ManagedSiberiaProperties;
import org.siberia.type.SibType;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;

/**
 *
 * Edit a ColdProperties
 *
 * @author alexis
 */
public class PropertiesEditingAction extends TypeReferenceEditingAction<ManagedSiberiaProperties>
{   
    
    /** called to prepare action
     *	@param list a List of E
     */
    protected void prepare(List<ManagedSiberiaProperties> list)
    {
	super.prepare(list);
	
	if ( list != null )
	{
	    for(int i = 0; i < list.size(); i++)
	    {
		ManagedSiberiaProperties currentProperties = list.get(i);
		
		if ( currentProperties != null )
		{
		    currentProperties.refresh();
		}
	    }
	}
    }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof ManagedSiberiaProperties
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected ManagedSiberiaProperties createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
        
        ManagedSiberiaProperties properties = null;
        if ( list != null )
        {   Iterator<Parameter> it = list.iterator();
            while(it.hasNext())
            {   Parameter param = it.next();
                    
                if ( param.getName().equals("id") )
                {   String value = "";
                    if ( param.getValue() instanceof String )
                    {   value = (String)param.getValue(); }

                    properties = new ManagedSiberiaProperties(value);
		    
		    ResourceBundle rb = ResourceBundle.getBundle(PropertiesEditingAction.class.getName());
		    
		    try
		    {
			properties.setName(rb.getString("name"));
		    }
		    catch(PropertyVetoException e)
		    {
			e.printStackTrace();
		    }
		    
                    this.setTypes(properties);
                }
            }
        }
        
        System.out.println("calling createType and returning : " + properties);
        
        return properties;
    }
    
}
