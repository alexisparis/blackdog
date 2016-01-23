/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.editor;

import java.util.Collections;
import java.util.List;
import org.siberia.ui.UserInterface;
import org.siberia.ui.swing.dialog.ValidationDialog;
import org.siberia.editor.launch.EditorLaunchContext;

/**
 *
 * Dialog that allow user to choose editors
 *
 * @author alexis
 */
public class EditorDialog extends ValidationDialog
{
    /** Editor chooser */
    private EditorChooser chooser  = null;
    
    /* has canceled */
    private boolean       canceled = false;
    
    /** Creates a new instance of EditorDialog
     *  @param contexts an array of EditorLaunchContext
     */
    public EditorDialog(EditorLaunchContext... contexts)
    {   super(UserInterface.getInstance().getFrame(), "Choose editors");
        this.canceled = false;
        
        this.chooser = new EditorChooser(contexts);
    }
    
    /** display the dialog on the window **/
    public void display()
    {   if ( ! this.chooser.isFixed() )
        {
            /* build the content of the dialog */
            this.chooser.createContainer(this.getContentPanel());
            
            super.display();
        }
        
        /* fix the chooser */
        this.chooser.fix();
    }
    
    /** return a List of editor class to edit the types
     *  @return a List of Class
     */
    public List<Class> getEditorClasses()
    {   if ( this.canceled )
            return Collections.EMPTY_LIST;
        else
            return this.chooser.getEditorClasses();
    }
    
    /* method to process the cancel action */
    public void cancel()
    {   this.canceled = true; }
    
}
