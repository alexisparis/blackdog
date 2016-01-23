/* 
 * Siberia properties : siberia plugin defining system properties
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
package org.siberia.properties;


import org.siberia.type.AbstractSibType;
import org.siberia.xml.schema.properties.PropertyContainer;

/**
 *
 * Class wrapping a PropertyContainer
 *
 * @author alexis
 */
public abstract class XmlPropertyContainer extends AbstractSibType
{
    /** name of properties */
    public static final String PROP_LABEL       = "label";
    public static final String PROP_ICON        = "icon";
    public static final String PROP_DESCRIPTION = "description";
    
    
    /** Creates a new instance of ColdXmlPropertyContainer */
    public XmlPropertyContainer()
    {   }
    
    /** return an instance of PropertyContainer
     *  @return an instance of PropertyContainer
     */
    public abstract PropertyContainer getPropertyContainer();
    
    /** return the order
     *  @return an integer representing the order
     */
    public int getOrder()
    {   int order = 0;
        if ( this.getPropertyContainer() != null )
            order = this.getPropertyContainer().getOrder();
        return order;
    }
    
    /** return a String representation of the icon to use
     *  @return a String representation of the icon to use or null if no icon specified
     */
    public String getIcon()
    {   String result = null;
        if ( this.getPropertyContainer() != null )
            result = this.getPropertyContainer().getIcon();
        return result;
    }
    
    /** initialize icon
     *  @param icon a new icon
     */
    public void setIcon(String icon)
    {   if ( this.getPropertyContainer() != null )
        {   boolean fire = false;
            String oldIcon = this.getPropertyContainer().getIcon();
            if ( oldIcon == null )
            {   if ( icon != null )
                    fire = true;
            }
            else
            {   if ( ! oldIcon.equals(icon) )
                    fire = true;
            }
            
            if ( fire )
            {   this.getPropertyContainer().setIcon(icon);
                this.firePropertyChange(PROP_ICON, oldIcon, icon);
            }
        }
    }
    
    /** return a label to use
     *  @return a label to use or null if no label specified
     */
    public String getLabel()
    {   String result = null;
        if ( this.getPropertyContainer() != null )
            result = this.getPropertyContainer().getLabel();
        return result;
    }
    
    /** initialize label
     *  @param label a new label
     */
    public void setLabel(String label)
    {   if ( this.getPropertyContainer() != null )
        {   boolean fire = false;
            String oldLabel = this.getPropertyContainer().getLabel();
            if ( oldLabel == null )
            {   if ( label != null )
                    fire = true;
            }
            else
            {   if ( ! oldLabel.equals(label) )
                    fire = true;
            }
            
            if ( fire )
            {   this.getPropertyContainer().setLabel(label);
                this.firePropertyChange(PROP_LABEL, oldLabel, label);
            }
        }
    }
    
    /** return a description to use
     *  @return a description to use or null if no label specified
     */
    public String getDescription()
    {   String result = null;
        if ( this.getPropertyContainer() != null )
            result = this.getPropertyContainer().getDescription();
        return result;
    }
    
    /** initialize description
     *  @param description a new description
     */
    public void setDescription(String description)
    {   if ( this.getPropertyContainer() != null )
        {   boolean fire = false;
            String oldDesc = this.getPropertyContainer().getDescription();
            if ( oldDesc == null )
            {   if ( description != null )
                    fire = true;
            }
            else
            {   if ( ! oldDesc.equals(description) )
                    fire = true;
            }
            
            if ( fire )
            {   this.getPropertyContainer().setDescription(description);
                this.firePropertyChange(PROP_DESCRIPTION, oldDesc, description);
            }
        }
    }    

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   throw new UnsupportedOperationException("ColdXmlProperty does not support valueAsString"); }
    
    /** method which allow to restore the old value for all properties */
    public abstract void restoreOldValues();
    
    /** method which indicate that all values are confirmed */
    public abstract void confirmValues();
    
}

