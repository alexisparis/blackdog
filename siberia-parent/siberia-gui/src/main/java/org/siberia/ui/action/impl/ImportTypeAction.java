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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.siberia.kernel.Kernel;
import org.siberia.type.SibCollection;
import org.siberia.type.SibType;
import org.siberia.ui.UserInterface;
import org.siberia.ui.action.AbstractSingleTypeAction;

/**
 *
 * action called to export an existing item
 *
 * @author alexis
 */
public class ImportTypeAction extends AbstractSingleTypeAction
{
    
    /** Creates a new instance of ImportTypeAction */
    public ImportTypeAction()
    {   }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {   
        SibType type = this.getType();
        
        if ( type != null )
        {   
            try
            {   
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
//		chooser.setFileFilter(this.createFilter());
		int result = chooser.showOpenDialog(null);

		if ( result == JFileChooser.APPROVE_OPTION )
		{   File[] files = chooser.getSelectedFiles();
		    
		    org.siberia.binding.constraint.FileBindingConstraint[] constraints = new org.siberia.binding.constraint.FileBindingConstraint[files.length];
		    
		    for(int i = 0; i < files.length; i++)
		    {
			constraints[i] = new org.siberia.binding.constraint.FileBindingConstraint(files[i]);
		    }

		    Object[] types = Kernel.getInstance().getDefaultBindingManager().load(constraints);
		    
		    String    dialogErrorTitle   = null;
		    String    dialogErrorMessage = null;
		    Component invoker            = null;
		    
		    if ( types != null )
		    {
			for(int i = 0; i < types.length; i++)
			{
			    Object newType = types[i];
			    
			    if ( type != null )
			    {
				boolean added = false;
				if ( type instanceof SibCollection )
				{   
				    if ( newType.getClass().equals(type.getClass()) )
				    {
					/* newType is a List */
					if ( ((SibCollection)newType).getAllowedClass() != null &&
					     ((SibCollection)type).getAllowedClass() != null && 
					     ((SibCollection)newType).getAllowedClass().equals(((SibCollection)type).getAllowedClass()) )
					{
					    added = ((SibCollection)type).addAll( (SibCollection)newType );
					}
				    }
				    else
				    {
					added = ((SibCollection)type).add(newType);
				    }
				    
				    if ( ! added )
				    {
					if ( dialogErrorMessage == null || dialogErrorTitle == null )
					{
					    ResourceBundle rb = ResourceBundle.getBundle(ImportTypeAction.class.getName());
					    
					    dialogErrorTitle   = rb.getString("import.error.title");
					    dialogErrorMessage = rb.getString("import.error.message");
					    
					    if ( dialogErrorTitle == null )
					    {
						dialogErrorTitle = "";
					    }
					    if ( dialogErrorMessage == null )
					    {
						dialogErrorMessage = "";
					    }
					}
					
					if ( invoker == null )
					{
					    if ( e.getSource() instanceof Component )
					    {
						invoker = SwingUtilities.getWindowAncestor( (Component)e.getSource() );
					    }
					    
					    if ( invoker == null )
					    {
						invoker = UserInterface.getInstance().getFrame();
					    }
					}
					
					JOptionPane.showMessageDialog(invoker, dialogErrorMessage, dialogErrorTitle, JOptionPane.ERROR_MESSAGE);
				    }
				}
			    }
			}
		    }
		}
            }
            catch(Exception ex)
            {   ex.printStackTrace(); }
        }
        else
        {   throw new RuntimeException("type is null"); }
    }
    
}
