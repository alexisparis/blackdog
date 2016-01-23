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
package org.siberia.ui.status;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaGuiPlugin;
import org.siberia.editor.Editor;
import org.siberia.editor.agreement.ForbiddenSupportAgreement;
import org.siberia.editor.agreement.LikelySupportAgreement;
import org.siberia.editor.agreement.SupportAgreement;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.editor.support.EditorSupport;
import org.siberia.editor.support.EditorSupportEntity;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.type.message.SibMessage;
import org.siberia.type.task.SibTask;
import org.siberia.type.task.SibTaskStatus;
import org.siberia.kernel.Kernel;
import org.siberia.trans.ui.action.impl.UpdatePluginsAction;
import org.siberia.trans.TransSiberia;

/**
 *
 * Default status bar which is able to display message,
 *  to warn user at start if there are no plugins or new versions of installed plugins
 *  and which is able to show the task currently running.
 *
 * @author alexis
 */
public class DefaultStatusBar extends javax.swing.JPanel implements EditorSupport
{
    /** logger */
    private Logger		   logger                = Logger.getLogger(DefaultStatusBar.class);
    
    /** editor support adapter */
    private EditorSupportEntity	   support               = null;
    
    /** task manager */
    private TaskManager		   taskManager	         = new TaskManager();
    
    /**  */
    private List<SibMessage>	   messages	         = new ArrayList<SibMessage>();
    
    /** property change listener for tasks */
    private PropertyChangeListener taskChangeListener    = null;
    
    /** mouse listener on the progress to show popup */
    private MouseListener          progressMouseListener = null;
    
    /** List of editors */
    private List<Editor>           editors               = new ArrayList<Editor>();
    
    /**
     * Creates new form DefaultStatusBar
     */
    public DefaultStatusBar()
    {
	initComponents();
	
	this.taskProgress.setVisible(false);
	
	this.support = new EditorSupportEntity(this, null, null, null);
	this.support.setPriority(Integer.MAX_VALUE);
	
	this.updateButton.setIcon(null);
	this.updateButton.setText("");
	this.updateButton.setVisible(false);
	
	this.updateButton.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		/** create an update action and execute it */
		UpdatePluginsAction action = new UpdatePluginsAction();
		
		action.setTransSiberia(Kernel.getInstance().getTransSiberia());
		
		updateButton.setIcon(null);
		updateButton.setVisible(false);
		
		action.actionPerformed(e);
	    }
	});
	
	/** launch a Thread that will determine if there is a new version of the application */
	new Thread()
	{
	    public void run()
	    {
		try
		{
		    TransSiberia rail = Kernel.getInstance().getTransSiberia();
		    
		    if ( rail == null )
		    {
			logger.error("no transsiberia configured in kernel --> could not look for updates");
		    }
		    else
		    {
			boolean releaseAvailable = rail.isUpdateAvailableFromUpdateAll();
			
			if ( releaseAvailable )
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("updates available from update all link");
			    }
			    
			    /** change icon of the button */
			    Runnable runnable = new Runnable()
			    {
				public void run()
				{
				    Icon icon = null;
				    try
				    {
					icon = ResourceLoader.getInstance().getIconNamed(SiberiaGuiPlugin.PLUGIN_ID + ";1::img/update-plugins.png");
				    }
				    catch(ResourceException e)
				    {
					logger.error("could not find icon for udpate button in status bar", e);
				    }
				    updateButton.setIcon(icon);
				    updateButton.setVisible(true);
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
			else
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("no updates available from update all link");
			    }
			}
		    }    
		}
		catch(Exception e)
		{
		    logger.error("error while trying to see if there is a new version of the application", e);
		}
	    }
	}.start();
	
	this.progressMouseListener = new MouseAdapter()
	{
	    @Override
	    public void mousePressed(MouseEvent e)
	    {
		if ( editors.size() > 0 )
		{
		    /* display popup menu showing all tasks waiting or running */
		    JPopupMenu taskMenu = new JPopupMenu();

		    taskMenu.setMaximumSize(new Dimension(500, 500));
		    
		    int editorAdded = 0;

		    for(int i = 0; i < editors.size(); i++)
		    {
			Editor editor = editors.get(i);
			
			if ( editor != null && editor.getInstance() instanceof SibTask )
			{
			    if ( ! ((SibTask)editor.getInstance()).getStatus().equals(SibTaskStatus.STOPPED) && 
				 ! ((SibTask)editor.getInstance()).getStatus().equals(SibTaskStatus.FINISHED) )
			    {
				taskMenu.insert(editors.get(i).getComponent(), 0);
				editorAdded ++;
			    }
			}
		    }

		    if ( editorAdded > 0 )
		    {
			taskMenu.pack();

			taskMenu.show(taskProgress, 0, - taskMenu.getPreferredSize().height);
		    }
		}
	    }
	};
	this.taskProgress.addMouseListener(this.progressMouseListener);
	
	this.taskChangeListener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( evt.getPropertyName().equals(SibTask.PROPERTY_STATUS) )
		{
		    if ( SibTaskStatus.STOPPED.equals(evt.getNewValue()) ||
			 SibTaskStatus.FINISHED.equals(evt.getNewValue()) )
		    {
			/** remove task from list */
			for(int i = 0; i < editors.size(); i++)
			{
			    Editor editor = editors.get(i);

			    if ( editor != null )
			    {
				if ( evt.getSource() == editor.getInstance() )
				{
				    editors.remove(editor);
				    editor.getInstance().removePropertyChangeListener(taskChangeListener);
				    editor.setInstance(null);
				    editor.close();
				    editor.dispose();
				}
			    }
			}
		    }
		}
	    }
	};
    }
    
    /** ########################################################################
     *  ################### EditorSupport implementation #######################
     *  ######################################################################## */

    /**
     * return an Iterator over all Editor being registered by the support
     * 
     * @return an Iterator over Editor
     */
    public Iterator<Editor> editors()
    {	return this.support.editors(); }

    /**
     * unregister a SibType
     * 
     * @param editor a SibType to unregister
     */
    public void unregister(SibType type)
    {	this.support.unregister(type); }

    /**
     * show a registered editor linked with the given type
     * 
     * @param type a SibType
     */
    public void show(SibType type)
    {	this.support.show(type); }

    /**
     * return true if the support is currently showing an editor related to this type
     * 
     * @param type a SibType
     * @return true if the support is currently showing an editor related to the given SibType
     */
    public boolean isShowing(SibType type)
    {	return this.support.isShowing(type); }

    /**
     * indicates if the given type is being registered
     * 
     * @param editor a SibType
     * @return true if it is registered
     */
    public boolean isRegistering(SibType type)
    {	return this.support.isRegistering(type); }

    /**
     * return a registered editor linked with this type
     * 
     * @param type a SibType
     * @return a registered editor or null if no found
     */
    public Editor getEditor(SibType type)
    {	return this.support.getEditor(type); }

    /**
     * unregister a new Editor
     * 
     * @param editor an Editor to unregister
     */
    public void unregister(Editor editor)
    {	this.support.unregister(editor); }

    /**
     * show a registered editor
     * 
     * @param editor an Editor that is already registered
     */
    public void show(Editor editor)
    {	this.support.show(editor); }

    /**
     * return true if the given editor is currently being shown
     * 
     * @param editor an Editor that is already registered
     * @return true if the support is currently showing the given editor
     */
    public boolean isShowing(Editor editor)
    {	return this.support.isShowing(editor); }

    /**
     * indicates if the given editor is being registered
     * 
     * @param editor an Editor
     * @return true if it is registered
     */
    public boolean isRegistering(Editor editor)
    {	return this.support.isRegistering(editor); }

    /**
     * register a new Editor
     * 
     * @param editor an Editor to register
     * @param launchContext a EditorLaunchContext
     */
    public void register(final Editor editor, EditorLaunchContext launchContext)
    {	
	this.support.register(editor, launchContext);
	
	if ( launchContext.getItem() instanceof SibMessage )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("asking to register a SibMessage on " + this);
	    }
	    
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    if ( messageContainer != null )
		    {
			messageContainer.removeAll();
			
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("removing all components from message container");
			}
		    }

		    Component component = editor.getComponent();

		    if ( component != null )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("adding " + component + " to message container");
			}
			messageContainer.add(component);
			messageContainer.revalidate();
		    }
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
	else if ( launchContext.getItem() instanceof SibTask )
	{
	    ((SibTask)launchContext.getItem()).addPropertyChangeListener(this.taskChangeListener);
	    
	    this.editors.add(editor);
	    
	    this.taskManager.appendTask( (SibTask)launchContext.getItem() );
	}
    }

    /**
     * return an object telling if this support will accept the given editor
     * 
     * @param editor the editor to support
     * @param launchContext a EditorLaunchContext
     */
    public SupportAgreement agreeToSupport(Editor editor, EditorLaunchContext launchContext)
    {	
	SupportAgreement agreement = null;
	
	if ( launchContext != null )
	{
	    SibType type = launchContext.getItem();
	    
	    if ( type instanceof SibMessage || type instanceof SibTask )
	    {
		agreement = new LikelySupportAgreement(this, editor);
	    }
	}
	
	if ( agreement == null )
	{
	    agreement = new ForbiddenSupportAgreement(this, editor);
	}
	
	return agreement;
    }
    
    /** return the priority of the support<br>
     *	    the registered support with the highest priority will be asked first to support an editor<br>
     *	@return the priority of the support
     */
    public int getPriority()
    {	return this.support.getPriority(); }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jPanel1 = new javax.swing.JPanel();
        updateButton = new javax.swing.JButton();
        taskProgress = new javax.swing.JProgressBar();
        messageContainer = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        updateButton.setText("jButton2");
        updateButton.setBorderPainted(false);
        updateButton.setMaximumSize(new java.awt.Dimension(88, 22));
        updateButton.setMinimumSize(new java.awt.Dimension(88, 22));
        updateButton.setOpaque(false);
        updateButton.setPreferredSize(new java.awt.Dimension(88, 22));

        taskProgress.setMaximumSize(new java.awt.Dimension(32767, 12));
        taskProgress.setMinimumSize(new java.awt.Dimension(10, 12));
        taskProgress.setPreferredSize(new java.awt.Dimension(148, 12));

        messageContainer.setLayout(new javax.swing.BoxLayout(messageContainer, javax.swing.BoxLayout.LINE_AXIS));

        messageContainer.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(messageContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taskProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messageContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(updateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(taskProgress, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel messageContainer;
    private javax.swing.JProgressBar taskProgress;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
    
}
