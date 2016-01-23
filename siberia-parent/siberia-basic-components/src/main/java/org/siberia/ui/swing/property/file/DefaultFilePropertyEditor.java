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
package org.siberia.ui.swing.property.file;

import javax.swing.JFileChooser;
import org.siberia.base.file.DefaultFile;

/**
 *
 * @author alexis
 */
public class DefaultFilePropertyEditor extends TypedFilePropertyEditor<DefaultFile>
{
    
    /** Creates a new instance of DefaultFilePropertyEditor */
    public DefaultFilePropertyEditor()
    {   }
    
    /** create the new value according to the input of the component that render the property
     *  @param input the String in the component not null and not empty
     *  @return a TypedFile
     */
    public DefaultFile getValueImpl(String input)
    {   return new DefaultFile(input); }
    
    /**
     * Placeholder for subclasses to customize the JFileChooser shown to select a
     * file.
     *
     * @param chooser
     */
    @Override
    protected void customizeFileChooser(JFileChooser chooser)
    {   
        super.customizeFileChooser(chooser);
        
        if ( chooser != null )
        {   chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); }
    }
    
}
