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
package org.siberia.ui.swing.table.model;

/**
 *
 * Define a TableModel based on bean property
 *
 * @author alexis
 */
public interface PropertyBasedTableModel extends SiberiaTableModel
{
    
    /** return the number of PropertyDeclaration
     *	@return an integer
     */
    public int getPropertyDeclarationCount();
    
    /** return the PropertyDeclaration at the given index
     *	@param index
     *	@return a PropertyDeclaration
     */
    public PropertyDeclaration getPropertyDeclaration(int index);
    
    /** return the name of the property linked to the given PropertyDeclaration
     *	@param declaration a PropertyDeclaration
     *	@return the name to use for the property declaration
     */
    public String getPropertyDisplayNameFor(PropertyDeclaration declaration);
    
    /** method that returns the position in the model of the given object
     *	@param property the property managed or not by the model
     *	@return the column index of the property in the model of the given object or -1 if not found
     */
    public int getColumnIndexOfProperty(String property);
    
    /** indicate the name of the properties enabled<br>
     *	other properties will be disabled
     *	@param properties an array of String representing the name of the properties which has to be enabled
     */
    public void enableProperties(String... properties);
    
    /** add a PropertyBasedTableModelListener
     *	@param listener a PropertyBasedTableModelListener
     */
    public void addPropertyBasedTableModelListener(PropertyBasedTableModelListener listener);
    
    /** remove a PropertyBasedTableModelListener
     *	@param listener a PropertyBasedTableModelListener
     */
    public void removePropertyBasedTableModelListener(PropertyBasedTableModelListener listener);
    
}
