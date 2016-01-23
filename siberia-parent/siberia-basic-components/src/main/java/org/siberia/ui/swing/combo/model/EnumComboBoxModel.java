/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.combo.model;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 *
 * ComboBoxModel specific to Enum
 *
 * @author alexis
 */
public class EnumComboBoxModel extends AbstractListModel implements ComboBoxModel
{
    /** selection */
    private Object selection = null;

    /** enum class */
    private Class  enumClass = null;

    /** array of object */
    private Object[] objects = null;

    /** create a new EnumComboBoxModel
     *  @param enumClass the class of the enumeration related to this model
     */
    public EnumComboBoxModel(Class enumClass)
    {   if ( ! Enum.class.isAssignableFrom(enumClass) )
            throw new IllegalArgumentException("class must extends java.lang.Enum");
        this.enumClass = enumClass;
    }

    /**
     * Returns the value at the specified index.  
     * 
     * @param index the requested index
     * @return the value at <code>index</code>
     */
    public Object getElementAt(int index)
    {   this.checkObjectArray();
        return this.objects[index];
    }

    /**
     * 
     * Returns the length of the list.
     * 
     * @return the length of the list
     */
    public int getSize()
    {   this.checkObjectArray();
        return this.objects.length;
    }

    /** create the array of object using reflection on the Enum class */
    private synchronized void checkObjectArray()
    {   if ( this.objects == null )
        {   
            try
            {   Method m = this.enumClass.getMethod("values", new Class[]{});
                this.objects = (Object[])m.invoke(null, new Object[]{});
                
                /** sort by name */
                Arrays.sort(this.objects, new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {   
                        int result = 0;
                        if ( ! (o1 instanceof Enum) )
                        {   if ( o2 instanceof Enum )
                                result = -1;
                        }
                        else
                        {   if ( ! (o2 instanceof Enum) )
                                result = 1;
                            else
                            {   Enum en1 = (Enum)o1;
                                Enum en2 = (Enum)o2;
                                
                                result = en1.name().compareTo(en2.name());
                            }
                        }
                        
                        return result;
                    }
                });
            }
            catch (Exception ex)
            {   ex.printStackTrace(); }
        }
    }

    /** return the Enum class related with this model
     *  @return a class that extends java.lang.Enum
     */
    public Class getEnumClass()
    {   return this.enumClass; }

    /**
     * Set the selected item. The implementation of this  method should notify
     * all registered <code>ListDataListener</code>s that the contents
     * have changed.
     *
     * @param anItem the list object to select or <code>null</code>
     *        to clear the selection
     */
    public void setSelectedItem(Object anItem)
    {   this.selection = anItem; }

    /**
     * Returns the selected item
     * @return The selected item or <code>null</code> if there is no selection
     */
    public Object getSelectedItem()
    {   return this.selection; }

}
