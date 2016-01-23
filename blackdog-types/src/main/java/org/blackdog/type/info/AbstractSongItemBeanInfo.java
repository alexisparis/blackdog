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
package org.blackdog.type.info;

import java.beans.IntrospectionException;
import org.blackdog.type.AbstractSongItem;
import org.blackdog.type.AudioItem;
import org.blackdog.type.SongItem;

import org.siberia.type.SibURL;
import org.siberia.type.info.AnnotationBasedBeanInfo;
import org.siberia.type.info.ExtendedPropertyDescriptor;

import java.beans.PropertyDescriptor;

/**
 *
 *  BeanInfo for AbstractSongItem.
 *  This kind of BeanInfo allow to deal with annotations
 *
 * @author alexis
 */
public class AbstractSongItemBeanInfo extends AudioItemBeanInfo
{
    /** create a new PlayListBeanInfo */
    public AbstractSongItemBeanInfo(  )
    {
        this( AbstractSongItem.class );
    }

    /** create a new AbstractSongItemBeanInfo */
    protected AbstractSongItemBeanInfo( Class c )
    {
        super( c );
    }

    /** method that is called after building the PropertyDescriptor according to annotation information
     *  Overwrite this method to modify the information of the PropertyDescriptor
     *  @param descriptor a PropertyDescriptor
     */
    @Override
    protected void postProcessProperty( PropertyDescriptor descriptor )
    {
        super.postProcessProperty( descriptor );

        if ( SongItem.PROPERTY_BITRATE.equals( descriptor.getName(  ) ) )
        {
            /** not changeable while initialized */
            try
            {
                descriptor.setWriteMethod( null );
            }
	    catch ( IntrospectionException e )
            {
                e.printStackTrace(  );
            }

            if ( descriptor instanceof ExtendedPropertyDescriptor )
            {
                ( (ExtendedPropertyDescriptor) descriptor ).setSupportGrouping( false );
            }
        }
    }
}
