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
package org.siberia.ui.swing.property;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import java.beans.PropertyVetoException;

/**
 * 
 * listener that would like to be warned when a PropertyVetoException is throwed
 *
 * @author alexis
 */
public interface PropertyVetoListener
{
    /** method that is called when the model catch a PropertyVetoException
     *  @param model the model that catched an exception of kind PropertyVetoException
     *  @param the current object managed by the model
     *  @param item the Item that is responsible
     *  @param exception a PropertyVetoException
     */
    public void vetoException(ExtendedPropertySheetTableModel model,
                              Object object,
                              Item item,
                              PropertyVetoException exception);
    
}
