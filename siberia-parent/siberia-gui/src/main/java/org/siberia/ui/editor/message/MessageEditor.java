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
package org.siberia.ui.editor.message;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.type.SibType;
import org.siberia.type.message.SibMessage;
import org.siberia.ui.IconCache;

/**
 *
 * editor for message
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.message.SibMessage.class},
                  description="Editor for messages",
                  name="Message editor",
                  launchedInstancesMaximum=-1)
public class MessageEditor extends AbstractEditor
{
    /** property change listener on the instance to update level icon and message text */
    private PropertyChangeListener instanceListener = null;
    
    /** labhel that renders the message */
    private JLabel                 label            = null;
    
    /**
     * Creates a new instance of MessageEditor
     */
    public MessageEditor()
    {	}
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
	SibType old = this.getInstance();
	
	super.setInstance(instance);
	
	if ( old != this.getInstance() )
	{
	    if ( old != null && this.instanceListener != null )
	    {
		old.removePropertyChangeListener(this.instanceListener);
	    }
	    
	    if ( this.getInstance() != null )
	    {
		if ( this.instanceListener == null )
		{
		    this.instanceListener = new PropertyChangeListener()
		    {
			public void propertyChange(PropertyChangeEvent evt)
			{
			    refreshComponents();
			}
		    };
		}
		this.getInstance().addPropertyChangeListener(this.instanceListener);
	    }
	    
	    /** refresh components */
	    this.refreshComponents();
	}
    }
    
    /** refresh components state */
    private void refreshComponents()
    {
	if ( this.label != null )
	{
	    final Icon   icon = (this.getInstance() instanceof SibMessage ? IconCache.getInstance().get(this.getInstance()) : null);
	    final String text = (this.getInstance() instanceof SibMessage ? this.getInstance().getName() : null);

	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    label.setIcon(icon);
		    label.setText(text);
		    label.setToolTipText(text);
		}
	    };
	    
	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		runnable.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(runnable);
	    }
	}
    }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {
	if ( this.label == null )
	{
	    this.label = new JLabel();
	    this.refreshComponents();
	}
	
	return this.label;
    }
    
}
