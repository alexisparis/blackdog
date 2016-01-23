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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * Representation of a named html url
 *
 * @author alexis
 */
@Bean(  name="SibHtmlURL",
        internationalizationRef="org.siberia.rc.i18n.type.SibHtmlURL",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibHtmlURL extends SibURL
{   
    /** create a new instanceof SibHtmlURL */
    public SibHtmlURL()
    {   super();
        
        this.addVetoableChangeListener(PROPERTY_VALUE, new VetoableChangeListener()
        {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException
            {
                /** if the url does not ends with html or htm, then throw an exception */
                if ( evt.getSource() == this )
                {   Object newValue = evt.getNewValue();
                    
                    if ( newValue instanceof URL )
                    {   String file = ((URL)newValue).getFile();
                        
                        if ( file != null && (file.endsWith(".html") && file.endsWith(".htm")) )
                        {   throw new PropertyVetoException("Illegal url, should ends with '.html' or '.htm'", evt); }
                    }
                }
            }
        });
    }
}
