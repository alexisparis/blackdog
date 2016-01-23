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

import org.blackdog.type.AudioItem;
import org.blackdog.type.Playable;
import org.blackdog.type.TimeBasedItem;

import org.siberia.type.SibURL;
import org.siberia.type.info.AnnotationBasedBeanInfo;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.type.info.ExtendedPropertyDescriptor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ResourceBundle;

/**
 *
 *  BeanInfo for AudioItem.
 *  This kind of BeanInfo allow to deal with annotations
 *
 * @author alexis
 */
public class AudioItemBeanInfo
    extends AnnotationBasedBeanInfo
{
    /** create a new PlayListBeanInfo */
    public AudioItemBeanInfo(  )
    {
        this( AudioItem.class );
    }

    /** create a new PlayListBeanInfo */
    protected AudioItemBeanInfo( Class c )
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

        if ( TimeBasedItem.PROPERTY_DURATION.equals( descriptor.getName(  ) ) ||
                 TimeBasedItem.PROPERTY_DURATION_VERIFIED.equals( descriptor.getName(  ) ) ||
                 TimeBasedItem.PROPERTY_CREATION_DATE.equals( descriptor.getName(  ) ) ||
                 Playable.PROPERTY_PLAYED_COUNT.equals( descriptor.getName(  ) ) ||
                 Playable.PROPERTY_DATE_LAST_PLAYED.equals( descriptor.getName(  ) ) )
        {
            /** not changeable while initialized */
            try
            {
                descriptor.setWriteMethod( null );
            } catch ( IntrospectionException e )
            {
                e.printStackTrace(  );
            }

            if ( descriptor instanceof ExtendedPropertyDescriptor )
            {
                ( (ExtendedPropertyDescriptor) descriptor ).setSupportGrouping( false );
            }
        } else if ( SibURL.PROPERTY_VALUE.equals( descriptor.getName(  ) ) )
        {
            if ( BeanInfoCategory.BASICS.equals( this.getBeanInfoCategory(  ) ) )
            {
                /** not changeable while initialized */
                try
                {
                    descriptor.setWriteMethod( null );
                } catch ( IntrospectionException e )
                {
                    e.printStackTrace(  );
                }
            }

            ResourceBundle rb = ResourceBundle.getBundle( AudioItemBeanInfo.class.getName(  ) );

            descriptor.setDisplayName( rb.getString( "url.displayName" ) );
            descriptor.setShortDescription( rb.getString( "url.description" ) );

            if ( descriptor instanceof ExtendedPropertyDescriptor )
            {
                ( (ExtendedPropertyDescriptor) descriptor ).setSupportGrouping( false );
            }
        }
    }
}
