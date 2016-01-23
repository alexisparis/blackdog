/* 
 * Siberia editor : siberia plugin defining editor framework
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
package org.siberia.editor.launch;

import org.siberia.type.SibType;

/**
 *
 * Define the manner an editor is launch
 *
 * @author alexis
 */
public interface EditorLaunchContext extends EditorLaunchConstants
{
    /** add a launch parameter
     *  @param launchParameter the name of a parameter
     *  @param value the associated value
     */
    public void addParameter(String launchParameter, Object value);
    
    /** get a launch parameter
     *  @param launchParameter the name of a parameter
     *  @return the associated value
     */
    public Object getParameter(String launchParameter);

    /** return the object that the editor will render
     *  @return a SibType
     */
    public SibType getItem();

    /** initilize the object that the editor will render
     *  @param item a SibType
     */
    public void setItem(SibType item);
}
