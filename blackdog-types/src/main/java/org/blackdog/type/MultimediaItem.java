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

import org.siberia.type.SibURL;
import org.siberia.type.annotation.bean.Bean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * Abstract implementation of an AudioItem
 *
 * @author alexis
 */
@Bean( 
        name = "multimedia item", internationalizationRef = "org.blackdog.rc.i18n.type.MultimediaItem", expert = false, hidden = false, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public abstract class MultimediaItem
    extends SibURL
    implements CategorizedItem,
               Playable
{
    /** Creates a new instance of MultimediaItem */
    public MultimediaItem(  )
    {
    }

    /* #########################################################################
     * ###################### Playable implementation ##########################
     * ######################################################################### */

    /** return an InputStream
     *  @return an InputStream
     *
     *  @exception IOException if the creation failed
     */
    public InputStream createInputStream(  )
                                  throws IOException
    {
        InputStream stream = null;

        URL url = this.getValue(  );

        if ( url != null )
        {
            stream = url.openStream(  );
        }

        return stream;
    }

    /** return the simple name of the item
     *  @return a String that does not contains '.' (example : 'mp3', 'ogg', etc..)
     */
    private String getSimpleName(  )
    {
        String result = null;

        URL url = this.getValue(  );

        if ( url != null )
        {
            String file = url.getFile(  );

            if ( file != null )
            {
                int lastSlashIndex = file.lastIndexOf( File.separator );

                if ( lastSlashIndex != -1 )
                {
                    result = file.substring( lastSlashIndex + 1 );
                }
            }
        }

        return result;
    }

    /** return the extension of the item
     *  @return a String that does not contains '.' (example : 'mp3', 'ogg', etc..)
     */
    public String getExtension(  )
    {
        String result = null;

        String simpleName = this.getSimpleName(  );

        if ( simpleName != null )
        {
            int lastPointIndex = simpleName.lastIndexOf( '.' );

            if ( lastPointIndex != -1 )
            {
                result = simpleName.substring( lastPointIndex + 1 );
            }
        }

        return result;
    }

    /** return the name of the playable item
     *  @return the name
     */
    public String getPlayableName(  )
    {
        String result = null;

        String simpleName = this.getSimpleName(  );

        if ( simpleName != null )
        {
            int lastPointIndex = simpleName.lastIndexOf( '.' );

            if ( lastPointIndex != -1 )
            {
                result = simpleName.substring( 0, lastPointIndex );
            }
        }

        return result;
    }
}
