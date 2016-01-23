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
package org.siberia.ui.swing.dialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.apache.log4j.Logger;

/**
 * 
 * a simple dialog that manage and show the state of a SwingWorker
 *  do not use setVisible directly, use display method
 *  and do not care about setVisible(false) cause it will be done after the end of the work 
 *  that has to do the Swingworker
 *
 * @author alexis
 */
public class SwingWorkerDialog extends ProgressDialog implements PropertyChangeListener,
								 ActionListener
{
    /** logger */
    private Logger      logger                   = Logger.getLogger(SwingWorkerDialog.class);
    
    /** Swingworker */
    private SwingWorker worker			 = null;
    
    /** represents the duration in milliseconds between the first time the dialog is visible and the launch of the Swingworker */
    private int         executeDelayAfterVisible = 20;
    
    /** represents the duration in milliseconds between the end of the SwingWorker and the disposing of this dialog */
    private int         visibleDelayAfterEnd     = 20;
    
    /** true to differ worker execution after dialog is visible */
    private boolean     differWorkerExecution    = false;
    
    /** Creates a new instance of SwingWorkerDialog
     *	@param parent the parent Window
     *	@param modal true to set the dialog as modal
     */
    public SwingWorkerDialog(Window parent, boolean modal)
    {
	this(parent, modal, 20, 20);
    }
    
    /** Creates a new instance of SwingWorkerDialog
     *	@param parent the parent Window
     *	@param modal true to set the dialog as modal
     *	@param executeDelayAfterVisible represents the duration in milliseconds between the first time the dialog is visible and the launch of the Swingworker
     *	@param visibleDelayAfterEnd represents the duration in milliseconds between the end of the SwingWorker and the disposing of this dialog
     */
    public SwingWorkerDialog(Window parent, boolean modal, int executeDelayAfterVisible, int visibleDelayAfterEnd)
    {
	super(parent, modal);
	
	this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	this.setResizable(false);
	
	this.worker = worker;
	
	this.executeDelayAfterVisible = executeDelayAfterVisible;
	this.visibleDelayAfterEnd     = visibleDelayAfterEnd;
    }

    /** return true if the execution of the worker is made some time after the dialog becomes visible<br>
     *	@return a boolean
     */
    public boolean isDifferWorkerExecutionEnabled()
    {
	return differWorkerExecution;
    }

    /** tell if the execution of the worker is made some time after the dialog becomes visible<br>
     *	<b>incompatible with modal dialog</b>
     *	@return a boolean
     */
    public void setDifferWorkerExecutionEnabled(boolean differWorkerExecution)
    {
	this.differWorkerExecution = differWorkerExecution;
    }
    
    /** set the SwingWorker
     *	@param worker a Swingworker
     */
    public void setWorker(SwingWorker worker)
    {
	if ( this.worker != worker )
	{
	    if ( this.worker != null )
	    {
		this.worker.removePropertyChangeListener(this);
	    }
	    
	    this.worker = worker;
	    
	    if ( this.worker != null )
	    {
		this.worker.addPropertyChangeListener(this);
	    }
	}
    }

    @Override
    public void setVisible(boolean b)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setVisible(" + b + ")");
	}
	
	boolean wasVisible = this.isVisible();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("was visible ? " + wasVisible);
	    logger.debug("differ worker execution ? " + this.isDifferWorkerExecutionEnabled());
	}
	
	if ( ! this.isDifferWorkerExecutionEnabled() && ! wasVisible && b )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before worker execution launch");
	    }
	    /* launch worker */
	    this.executeWorker();
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after worker execution launch");
	    }
	}
	
	super.setVisible(b);
	
	if ( this.isDifferWorkerExecutionEnabled() && ! wasVisible && b )
	{
	    /* launch SwingWorker execution after executeDelayAfterVisible */
	    Timer timer = new Timer(Math.max(0, this.executeDelayAfterVisible), this);
	    timer.setRepeats(false);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before starting delay timer");
	    }
	    timer.start();
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after starting delay timer");
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setVisible(" + b + ")");
	}
    }

    @Override
    public void dispose()
    {
	super.dispose();
	
	if ( this.worker != null )
	{
	    this.worker.removePropertyChangeListener(this);
	}
    }

    /* #########################################################################
     * ################# PropertyChangeListener implementation #################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( this.worker == evt.getSource() )
	{
	    if ( "progress".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Number )
	    {
		final int newValue = ((Number)evt.getNewValue()).intValue();
		
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			getProgressBar().setValue( newValue );
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
		
		
		if ( newValue >= 100 )
		{
		    Timer timer = new Timer(Math.max(0, this.visibleDelayAfterEnd), this);
		    timer.setRepeats(false);

		    timer.start();
		}
	    }
	}
    }
    
    /** launch Swingworker execution */
    private void executeWorker()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling executeWorker with worker : " + this.worker);
	}
	if ( this.worker != null )
	{
	    this.worker.execute();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of executeWorker with worker : " + this.worker);
	}
    }

    /* #########################################################################
     * ##################### ActionListener implementation #####################
     * ######################################################################### */

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	if ( e.getSource() instanceof Timer )
	{
	    /** if worker is not started, then go ... */
	    if ( this.worker.getState().equals(SwingWorker.StateValue.PENDING) )
	    {
		this.executeWorker();
	    }
	    else if ( this.worker.getState().equals(SwingWorker.StateValue.DONE) )
	    {
		this.setVisible(false);
	    }
	    
	    ((Timer)e.getSource()).stop();
	    ((Timer)e.getSource()).removeActionListener(this);
	}
    }
}
