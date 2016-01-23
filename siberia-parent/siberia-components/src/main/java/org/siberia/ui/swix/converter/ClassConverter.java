/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.swix.converter;

import org.jdom.Attribute;
import org.swixml.Converter;
import org.swixml.Localizer;

/**
 *
 * Converter for classes
 *
 * @author alexis
 */
public class ClassConverter implements Converter
{
    
    /**
     * Convert the value of the given <code>Attribute</code> object into an output object of the
     * specified type.
     *
     * @param type <code>Class</code> Data type to which the Attribute's value should be converted
     * @param attr <code>Attribute</code> the attribute, providing the value to be converted.
     *
     */
    public Object convert(Class type, Attribute attr, Localizer lz) throws Exception
    {
        System.out.println("attr value : " + attr.getValue());
//        SimpleTimeZone tz = null;
//        if (attr != null && attr.getValue() != null) {
//            tz = new SimpleTimeZone( 0, attr.getValue() );
//        }
//        return tz;
        return null;//org.siberia.ui.swing.tree.model.MutableTreeModel.class;
    }

    /**
     * A <code>Converters</code> conversTo method informs about the Class type the converter
     * is returning when its <code>convert</code> method is called
     * @return <code>Class</code> - the Class the converter is returning when its convert method is called
     */
    public Class convertsTo()
    {   return Class.class; }
}
