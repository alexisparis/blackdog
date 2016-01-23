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
package org.siberia.ui.action.impl;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.type.SibType;
import org.siberia.ui.action.ParameterizedAction;
import org.siberia.ui.action.TypeReferenceAction;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.ui.UserInterface;

/**
 *
 * @author alexis
 */
public abstract class TypeReferenceEditingAction<E extends SibType> extends TypeReferenceAction<E> implements ParameterizedAction
{
    /** logger */
    private static Logger logger = Logger.getLogger(TypeReferenceEditingAction.class);
    
    /** Creates a new instance of TypeEditingAction */
    public TypeReferenceEditingAction()
    {   super(); }
    
    /** called to prepare action
     *	@param list a List of E
     */
    protected void prepare(List<E> list)
    {
	
    }

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   
        List<E> types = this.getTypes();
	
	this.prepare(types);
        
        if ( types != null && types.size() > 0 )
        {
            EditorLaunchContext[] contexts = new EditorLaunchContext[types.size()];

            for(int i = 0; i < types.size(); i++)
            {   SibType type = types.get(i);

                contexts[i] = new DefaultEditorLaunchContext();
                contexts[i].setItem(type);
            }

            try
            {   Kernel.getInstance().getResources().edit(contexts); }
            catch (NoLauncherFoundException ex)
            {   logger.error("unable to edit " + this.getTypes(), ex);

                ResourceBundle rb = ResourceBundle.getBundle(TypeReferenceEditingAction.class.getName());

                String title = rb.getString("noEditorLauncherErrorTitle");
                String content = rb.getString("noEditorLauncherErrorMessage");

                /** replace ${element.name} by the name of the item we try ot edit */
                content.replaceAll("${element.name}", this.getTypes().toString());

                JOptionPane.showMessageDialog(UserInterface.getInstance().getFrame(), title, content, JOptionPane.ERROR_MESSAGE);

                ex.printStackTrace();
            }
        }
        else
        {   new Exception("et merdede").printStackTrace(); }
    }
    
}
