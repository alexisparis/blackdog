/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type;

import java.beans.PropertyVetoException;

/**
 * Interface for all SibType instance that are configurable<br>
 * That means that their caracteristics are not static
 *
 * @author alexis
 */
public abstract class SibConfigurable extends AbstractSibType implements Configurable
{
    /** property names */
    public static final String PROPERTY_CONFIGURABLE = "configurable";
    
    /** is configurable ? */
    private boolean config = false;
    
    /** create a new instanceof SibConfigurable */
    public SibConfigurable()
    {   super(); }

    /** tell if the type can be configured
     *  @return true if the type can be configured
     */
    public boolean isConfigurable()
    {   return this.config; }
 
    /** tell if the type can be configured
     *  @param config true if the type can be configured
     */   
    public void setConfigurable(boolean config) throws PropertyVetoException
    {   
	if ( config != this.isConfigurable() )
	{
	    this.fireVetoableChange(PROPERTY_CONFIGURABLE, this.config, config);

	    this.config = config;

	    this.firePropertyChange(PROPERTY_CONFIGURABLE, ! this.config, this.config);
	}
    }
    
    public Object clone()
    {   SibConfigurable other = (SibConfigurable)super.clone();
        
        try
        {   other.setConfigurable(this.isConfigurable()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return other;
    }
}
