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

import org.siberia.base.file.Directory;

import org.siberia.type.SibDirectory;
import org.siberia.type.annotation.bean.Bean;

import java.beans.PropertyVetoException;

/**
 *
 * @author alexis
 */
@Bean( 
        name = "IgnoredDirectory", internationalizationRef = "org.blackdog.rc.i18n.type.IgnoredDirectory", expert = false, hidden = false, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public class IgnoredDirectory
    extends SibDirectory
{
    /** Creates a new instance of ScannedDirectory */
    public IgnoredDirectory(  )
    {
    }

    @Override
    public void setValue( Directory value )
                  throws PropertyVetoException
    {
        super.setValue( value );

        /* if the name is not initialized ... */
        String name = this.getName(  );

        if ( ( value != null ) && ( ( name == null ) || ( name.trim(  ).length(  ) == 0 ) ) )
        {
            try
            {
                this.setName( value.getName(  ) );
            } catch ( PropertyVetoException e )
            {
            }
        }
    }
}
