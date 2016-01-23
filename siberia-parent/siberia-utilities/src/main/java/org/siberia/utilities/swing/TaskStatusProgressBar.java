/* 
 * Siberia utilities : siberia plugin providing severall utilities classes
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
package org.siberia.utilities.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.siberia.utilities.task.TaskStatus;

/**
 *
 * @author alexis
 */
public class TaskStatusProgressBar extends JProgressBar implements PropertyChangeListener
{
    /** status */
    private TaskStatus status = null;
    
    /** label that render the current message of the status */
    private JLabel     label  = null;
    
    /** Creates a new instance of TaskStatusProgressBar
     *  @param status an instance of TaskStatus to represent
     */
    public TaskStatusProgressBar(TaskStatus status)
    {   this(status, null); }
    
    /** Creates a new instance of TaskStatusProgressBar
     *  @param status an instance of TaskStatus to represent
     *  @param label a JLabel that has to render status information
     */
    public TaskStatusProgressBar(TaskStatus status, JLabel label)
    {   super();
        
        if ( status == null )
            throw new IllegalArgumentException("status must be non null");
        
        this.setMinimum(0);
        this.setMaximum(100);
        
        this.status = status;
        this.label  = label;
        
        this.status.addPropertyChangeListener(this);
    }
    
    /** return the label controlled by the progress bar
     *  @return a JLabel
     */
    public JLabel getControlledLabel()
    {   return this.label; }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {   
	if ( evt.getSource() == this.status )
	{   if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
	    {   if ( this.getControlledLabel() != null )
		{   if ( evt.getNewValue() instanceof String )
		    {   
			final String newValue = ((String)evt.getNewValue());

			Runnable run = new Runnable()
			{
			    public void run()
			    {
				getControlledLabel().setText( newValue );
			    }
			};
			if ( SwingUtilities.isEventDispatchThread() )
			{   run.run(); }
			else
			{   SwingUtilities.invokeLater(run); }
		    }
		}
	    }
	    else if( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
	    {   
		if ( evt.getNewValue() instanceof Number )
		{   
		    final Number newValue = (Number)evt.getNewValue();

		    Runnable run = new Runnable()
		    {
			public void run()
			{
			    setValue( newValue.intValue() );
			}
		    };
		    if ( SwingUtilities.isEventDispatchThread() )
		    {   run.run(); }
		    else
		    {   SwingUtilities.invokeLater(run); }			    
		}
	    }
	}
    }
    
    /** apply a TaskStatus on a JProgressBar
     *	@param status a TaskStatus
     *	@param progressBar a JProgressBar
     */
    public static void applyStatus(TaskStatus status, JProgressBar progressBar)
    {
	applyStatus(status, progressBar, null);
    }
    
    /** apply a TaskStatus on a JProgressBar
     *	@param status a TaskStatus
     *	@param progressBar a JProgressBar
     *	@param label a JLabel (optional)
     */
    public static void applyStatus(final TaskStatus status, final JProgressBar progressBar, final JLabel label)
    {
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
	
	PropertyChangeListener listener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( evt.getSource() == status )
		{   if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) )
		    {   if ( label != null )
			{   if ( evt.getNewValue() instanceof String )
			    {   
				final String newValue = ((String)evt.getNewValue());
				
				Runnable run = new Runnable()
				{
				    public void run()
				    {
					label.setText( newValue );
				    }
				};
				if ( SwingUtilities.isEventDispatchThread() )
				{   run.run(); }
				else
				{   SwingUtilities.invokeLater(run); }
			    }
			}
		    }
		    else if( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) )
		    {   
			if ( evt.getNewValue() instanceof Number )
			{   
			    final Number newValue = (Number)evt.getNewValue();
			    
			    Runnable run = new Runnable()
			    {
				public void run()
				{
				    progressBar.setValue( newValue.intValue() );
				}
			    };
			    if ( SwingUtilities.isEventDispatchThread() )
			    {   run.run(); }
			    else
			    {   SwingUtilities.invokeLater(run); }			    
			}
		    }
		}
	    }
	};
	
	status.addPropertyChangeListener(listener);
    }
    
}
