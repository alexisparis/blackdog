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
package org.blackdog.type.scan;

import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;

import java.beans.PropertyVetoException;

/**
 *
 * @author alexis
 */
@Bean( 
        name = "Ignored directories", internationalizationRef = "org.blackdog.rc.i18n.type.IgnoredDirectories", expert = false, hidden = false, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public class IgnoredDirectories
    extends SibList
{
    /** Creates a new instance of IgnoredDirectories */
    public IgnoredDirectories(  )
    {
        this.setAllowedClass( IgnoredDirectory.class );

        try
        {
            this.setName( "Ignored" );
            this.setContentItemAsChild( true );
        } catch ( PropertyVetoException ex )
        {
            ex.printStackTrace(  );
        }
    }
}
