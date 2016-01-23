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
package org.siberia.ui.editor.task;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaGuiPlugin;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.type.task.SibTask;
import org.siberia.type.task.SibTaskStatus;

/**
 *
 * editor for task
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.task.SibTask.class},
                  description="Editor for task",
                  name="Task editor",
                  launchedInstancesMaximum=-1)
public class TaskEditor extends AbstractEditor implements ActionListener
{
    /** property change listener on the instance to update level icon and message text */
    private PropertyChangeListener instanceListener = null;
    
    /** container */
    private Container    container   = null;
    
    /** label */
    private JLabel       label       = null;
    
    /** progression */
    private JProgressBar progressBar = null;
    
    /** close button */
    private JButton      stopButon   = null;
    
    /**
     * Creates a new instance of TaskEditor
     */
    public TaskEditor()
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
	if ( this.container != null )
	{
	    final boolean changeLabel      = this.getInstance() != null;
	    final boolean componentVisible = true;//(this.getInstance() instanceof SibTask ? true : false);
	    final String text              = (this.getInstance() instanceof SibTask ? this.getInstance().getName() : null);
	    final int progression          = (this.getInstance() instanceof SibTask ? ((SibTask)this.getInstance()).getProgression() : 100);
	    final boolean deterministic    = (this.getInstance() instanceof SibTask ? ((SibTask)this.getInstance()).isDeterministic() : true);
	    final boolean stoppable        = (this.getInstance() instanceof SibTask ? ((SibTask)this.getInstance()).isStoppable() : false);

	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    container.setVisible(componentVisible);
		    
		    if ( changeLabel )
		    {
			label.setText(text);
		    }
		    progressBar.setIndeterminate( ! deterministic );
		    
		    if ( deterministic )
		    {
			progressBar.setValue(progression);
		    }
		    
		    stopButon.setEnabled(stoppable);
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
	if ( this.container == null )
	{
	    this.label = new JLabel();
	    
	    this.progressBar = new JProgressBar();
	    
	    this.stopButon = new JButton();
	    
	    this.stopButon.addActionListener(this);
	    
	    try
	    {	this.stopButon.setIcon(
			ResourceLoader.getInstance().getIconNamed(SiberiaGuiPlugin.PLUGIN_ID + ";1::img/stop_task.png"));
	    }
	    catch (ResourceException ex)
	    {	ex.printStackTrace(); }
	    
	    this.stopButon.setVerticalAlignment(SwingConstants.TOP);
	    
	    this.stopButon.setPreferredSize(new Dimension(this.stopButon.getIcon().getIconWidth() + 4,
							  this.stopButon.getIcon().getIconHeight() + 4));
	    this.stopButon.setBorderPainted(false);
	    this.stopButon.setRequestFocusEnabled(false);
	    this.stopButon.setFocusable(false);
	    this.stopButon.setOpaque(false);
	    
	    this.container = new JPanel();
	    
	    this.container.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = new GridBagConstraints();
	    
	    gbc.gridx = 1;
	    gbc.gridy = 1;
	    gbc.gridwidth = 3;
	    gbc.weightx = 1.0f;
	    gbc.weighty = 1.0f;
	    gbc.fill = gbc.BOTH;
	    
	    this.container.add(this.label, gbc);
	    
	    gbc.gridx = 1;
	    gbc.gridy = 3;
	    gbc.gridwidth = 1;
	    gbc.weightx = 1.0f;
	    gbc.weighty = 0.0f;
	    gbc.fill = gbc.BOTH;
	    
	    this.container.add(this.progressBar, gbc);
	    
	    gbc.gridx = 2;
	    gbc.gridy = 2;
	    this.container.add(new JToolBar.Separator(new Dimension(5, 5)), gbc);
	    
	    gbc.gridx = 3;
	    gbc.gridy = 3;
	    gbc.weightx = 0.0f;
	    gbc.weighty = 1.0f;
	    gbc.fill = gbc.NONE;
	    
	    this.container.add(this.stopButon, gbc);
	    
	    this.refreshComponents();
	}
	
	return this.container;
    }
    
    /* #########################################################################
     * ##################### ActionListener implementation #####################
     * ######################################################################### */

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	if ( e.getSource() == this.stopButon )
	{
	    if ( this.getInstance() instanceof SibTask )
	    {
		((SibTask)this.getInstance()).setStatus(SibTaskStatus.STOPPED);
		
		this.stopButon.setEnabled(false);
	    }
	}
    }
    
}
