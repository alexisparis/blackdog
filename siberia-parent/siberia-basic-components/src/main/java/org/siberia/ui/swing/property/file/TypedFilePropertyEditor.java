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

import com.l2fprod.common.beans.editor.FilePropertyEditor;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import org.siberia.base.file.TypedFile;

/**
 *
 * Abstract property editor for TypedFile declared by plugin siberia-types
 *
 * @author alexis
 */
public abstract class TypedFilePropertyEditor<T extends TypedFile> extends FilePropertyEditor
{
    
    /** Creates a new instance of TypedFilePropertyEditor */
    public TypedFilePropertyEditor()
    {   super();
        
        Component c = this.getCustomEditor();
        
        if ( c instanceof Container )
        {   for(int i = 0; i < ((Container)c).getComponentCount(); i++)
            {   Component subComponent = ((Container)c).getComponent(i);
                
                if ( subComponent instanceof JButton )
                {   subComponent.setPreferredSize(new Dimension(16, 30)); }
            }
        }
    }
    
    @Override
    public Object getValue()
    {   Object o = null;
        if ( ! "".equals(textfield.getText().trim()) )
        {   o = this.getValueImpl(this.textfield.getText()); }
        
        return o;
    }
    
    /** create the new value according to the input of the component that render the property
     *  @param input the String in the component
     *  @return a TypedFile
     */
    public abstract T getValueImpl(String input);
    
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
        {   
            String input = this.textfield.getText();
            
            if ( input != null && input.length() > 0 )
            {   
                File f = new File(input);
                
                chooser.setSelectedFile(f);
                
            }
        }
        
        
    }
    
}
