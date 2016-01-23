/* 
 * Siberia properties ui : siberia plugin defining components to edit properties
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
package org.siberia.ui.properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import org.siberia.properties.XmlProperty;

/**
 *
 * define a renderer for a property
 *
 * @author alexis
 */
public interface PropertyComponent
{
    /** initialize the associated property
     *  @param property the associated property
     */
    public void setProperty(XmlProperty property);
    
    /** return the associated property
     *  @return the associated property
     */
    public XmlProperty getProperty();
    
    /** return a component that render a properties
     *  @return a JComponent
     */
    public JComponent getComponent();
    
    /** set the label related to the component
     *  @param label a JLabel
     */
    public void setLabel(JLabel label);
    
    /** return the label related to the component
     *  @return a JLabel
     */
    public JLabel getLabel();
    
}
