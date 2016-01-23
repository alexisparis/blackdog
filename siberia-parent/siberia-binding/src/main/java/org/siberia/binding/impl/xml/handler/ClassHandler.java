/* 
 * Siberia binding : siberia plugin defining persistence services
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
//package org.siberia.binding.impl.xml.handler;
//
//import org.siberia.type.SibCollection;
//import org.siberia.type.AbstractSibType;
//import org.exolab.castor.mapping.FieldHandler;
//import org.exolab.castor.mapping.ValidityException;
//
///**
// * The FieldHandler for the Class class
// *
// */
//public class ClassHandler implements FieldHandler
//{
//    /**
//     * Creates a new MyDateHandler instance
//     */
//    public ClassHandler() {
//        super();
//    }
//
//    /**
//     * Returns the value of the field from the object.
//     *
//     * @param object The object
//     * @return The value of the field
//     * @throws IllegalStateException The Java object has changed and
//     *  is no longer supported by this handler, or the handler is not
//     *  compatiable with the Java object
//     */
//    public Object getValue( Object object )
//        throws IllegalStateException
//    {
//        System.out.println("getValue(" + object.getClass() + ")");
//        if ( object instanceof SibCollection )
//        {   Class c = ((SibCollection)object).getAllowedClass();
//            if ( c != null )
//                return c.getName();
//        }
//        return null;
//    }
//
//
//    /**
//     * Sets the value of the field on the object.
//     *
//     * @param object The object
//     * @param value The new value
//     * @throws IllegalStateException The Java object has changed and
//     *  is no longer supported by this handler, or the handler is not
//     *  compatiable with the Java object
//     * @thorws IllegalArgumentException The value passed is not of
//     *  a supported type
//     */
//    public void setValue( Object object, Object value )
//        throws IllegalStateException, IllegalArgumentException
//    {
//        System.out.println("setValue(" + object.getClass() + ", " + value.getClass());
//        if ( object != null && value != null )
//        {   if ( object instanceof SibCollection && value instanceof String )
//            {   Class c = null;
//                try
//                {   c = Class.forName((String)value);
//                    ((SibCollection)object).setAllowedClass(c);
//                }
//                catch(ClassNotFoundException e)
//                {   ((SibCollection)object).setAllowedClass(AbstractSibType.class); }
//            }
//        }
//    }
//
//    /**
//     * Creates a new instance of the object described by this field.
//     *
//     * @param parent The object for which the field is created
//     * @return A new instance of the field's value
//     * @throws IllegalStateException This field is a simple type and
//     *  cannot be instantiated
//     */
//    public Object newInstance( Object parent )
//        throws IllegalStateException
//    {
//        //-- Since it's marked as a string...just return null,
//        //-- it's not needed.
//        return null;
//    }
//
//
//    /**
//     * Sets the value of the field to a default value.
//     *
//     * Reference fields are set to null, primitive fields are set to
//     * their default value, collection fields are emptied of all
//     * elements.
//     *
//     * @param object The object
//     * @throws IllegalStateException The Java object has changed and
//     *  is no longer supported by this handler, or the handler is not
//     *  compatiable with the Java object
//     */
//    public void resetValue( Object object )
//        throws IllegalStateException, IllegalArgumentException
//    {
////        ((Root)object).setDate(null);
//    }
//
//
//
//    /**
//     * @deprecated No longer supported
//     */
//    public void checkValidity( Object object )
//        throws ValidityException, IllegalStateException
//    {
//        // do nothing
//    }
//}
