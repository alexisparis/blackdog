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
package org.siberia.ui.action.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.awl.WizardConstants;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.ui.action.AbstractSingleTypeAction;
import org.siberia.ui.action.impl.wizard.CreationWizard;

/**
 *
 * Action that allow to create a new item for a SibCollection
 *
 * @author alexis
 */
public class AddingTypeAction<E extends SibType> extends AbstractSingleTypeAction<E>
{
    /* logger */
    private static Logger logger = Logger.getLogger(AddingTypeAction.class);
    
    /** last item created */
    private WeakReference<SibType> newlyCreatedItem = null;
    
    /** Creates a new instance of TypeEditingAction */
    public AddingTypeAction()
    {   super(); }

    /** return the last item created by this action
     *	@return a SibType or null if the creation was aborted
     */
    public SibType getNewlyCreatedItem()
    {
	return (newlyCreatedItem == null ? null : newlyCreatedItem.get());
    }

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   
	this.newlyCreatedItem = null;
	
	Object item = this.getType();
        if ( item instanceof SibCollection )
        {   SibCollection collection = (SibCollection)item;
            if ( collection.isCreateAuthorized() )
            {   
		Component c = null;
		
		Frame frame = null;
		
		if ( e.getSource() instanceof Component )
		{
		    c = (Component)e.getSource();
		}
		
		if ( c != null )
		{   
		    if ( c instanceof JMenuItem )
		    {
			
			while(c.getParent() != null)
			{
			    c = c.getParent();
			}
			
			if ( c instanceof JPopupMenu )
			{
			    c = ((JPopupMenu)c).getInvoker();
			}
		    }
		    
		    Window window = (c instanceof Window ? (Window)c : SwingUtilities.getWindowAncestor(c));
		    
		    if ( window instanceof Frame )
		    {
			frame = (Frame)window;
		    }
		}
		
                CreationWizard wizard = new CreationWizard(frame, collection, this.getClassToPropose());

                wizard.setMinimumSize(new Dimension(300, 200));
                wizard.setPreferredSize(new Dimension(500, 300));
                wizard.pack();

                wizard.setVisibleOnCenterOfOwner();

                int result = wizard.getReturnCode();

                if ( result == WizardConstants.WIZARD_VALID_OPTION )
                {   SibType newType = wizard.getCreatedObject();
                    if ( newType != null )
                    {   collection.add(newType); }
		    
		    wizard.dispose();
		    
		    this.newlyCreatedItem = new WeakReference<SibType>(newType);
                }                   
            }
        }
//        Kernel.getInstance().getResources().edit(this.getType());
    }
    
    /** method that allow the wizard to avoid the choice of the class to instantiate by proposing the given method
     *  @return a Class
     */
    public Class getClassToPropose()
    {
        return null;
    }
    
    /** set the types related to this action
     *  @return an array of SibType
     */
    @Override
    public void setTypes(E[] types)
    {   super.setTypes(types);
        
        boolean enabled = false;
        
        if ( this.getType() instanceof SibCollection )
        {   if ( ((SibCollection)this.getType()).isCreateAuthorized() )
            {   enabled = true; }
            else
            {   logger.debug(this.getClass().getName() + " not enabled because this collection does not accept creation"); }
        }
        else
        {   logger.debug(this.getClass().getName() + " not enabled because related item is not a Sibcollection (" + this.getType() + ")"); }
        
        this.setEnabled(enabled);
    }
    
}
