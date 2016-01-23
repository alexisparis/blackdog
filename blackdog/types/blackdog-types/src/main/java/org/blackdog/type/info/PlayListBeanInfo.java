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

import org.blackdog.type.PlayList;

import org.siberia.type.SibCollection;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.info.AnnotationBasedBeanInfo;

import java.lang.reflect.Field;

/**
 *
 *  BeanInfo for SibType.
 *  This kind of BeanInfo allow to deal with annotations
 *
 * @author alexis
 */
public class PlayListBeanInfo
    extends AnnotationBasedBeanInfo
{
    /** create a new PlayListBeanInfo */
    public PlayListBeanInfo(  )
    {
        super( PlayList.class );
    }

    /** return true if the given Field should generate a PropertyDescriptor
     *  to overwrite
     *
     *  @param field a Field
     *  @return true if it should generate a PropertyEditor
     */
    protected boolean shouldGenerate( Field field )
    {
        boolean result = super.shouldGenerate( field );

        if ( result )
        {
            BeanProperty property = this.getBeanPropertyAnnotation( field );

            if ( property != null )
            {
                String name = property.name(  );

                if ( ! name.equals( SibType.PROPERTY_NAME ) && ! name.equals( SibType.PROPERTY_NAME_COULD_CHANGE ) )
                {
                    result = false;
                }
            } else
            {
                result = false;
            }
        }

        return result;
    }
}
