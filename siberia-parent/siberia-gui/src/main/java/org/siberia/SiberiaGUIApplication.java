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
package org.siberia;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.siberia.eventsystem.EventConfigurationEngine;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.ui.UserInterface;
import org.apache.log4j.Logger;
import org.java.plugin.boot.SplashHandler;
import org.siberia.utilities.task.SimpleTaskStatus;
import org.siberia.utilities.task.TaskStatus;

/**
 *
 * Main class of the platform :<br>
 *  <ul><li>start the graphical user interface</li>92
 *      <li>start the kernel</li></ul>
 *
 * @author alexis
 */
public abstract class SiberiaGUIApplication extends SiberiaApplication implements PropertyChangeListener
{   
    /** User interface reference */
    private        UserInterface                 gui            = null;
    
    /** id of the application plugin */
    private        String                        id             = "siberia";
    
    /** logger */
    public         Logger                        logger         = Logger.getLogger(SiberiaGUIApplication.class);
    
    /** TaskStatus used for the boot of the platform */
    private        TaskStatus                    guiBootStatus  = this.createGuiBootStatus();
    
    /** reference to the boot resource bundle */
    private        SoftReference<ResourceBundle> guiBootRb      = new SoftReference<ResourceBundle>(null);
    
    /* SplashHandler */
    private        SplashHandler                 splashHandler  = null;
    
    /** Creates a new instance of SiberiaLauncher
     *  @param id the id of the plugin
     */
    public SiberiaGUIApplication(String id)
    {   super(id);
        
        /** add the gui boot status to the global boot status */
        this.getGlobalBootStatus().append(this.getGuiBootStatus(), this.getGuiBootStatusUnit());
	
	this.getGuiBootStatus().setLabel(this.getGuiResourcebundle().getString("guiStart"));
        
        /** listen to global boot status to modify the SplashHandler */
        this.getGlobalBootStatus().addPropertyChangeListener(this);
    }
    
    /** return the ResourceBundle associated with boot sequence
     *  @return a ResourceBundle
     */
    public ResourceBundle getGuiResourcebundle()
    {   ResourceBundle rb = this.guiBootRb.get();
        
        if ( rb == null )
        {   rb = ResourceBundle.getBundle("org.siberia.rc.i18n.boot");
            this.guiBootRb = new SoftReference<ResourceBundle>(rb);
        }
        
        return rb;
    }
    
    /** method that return the gui boot status
     *  @return a TaskStatus
     */
    public TaskStatus getGuiBootStatus()
    {   return this.guiBootStatus; }
    
    /** method that create the gui boot status
     *  @return a TaskStatus
     */
    protected TaskStatus createGuiBootStatus()
    {   return new SimpleTaskStatus("Gui boot"); }
    
    /** return the time allocated to gui booting
     *  @return a number
     */
    protected int getGuiBootStatusUnit()
    {   return 40; }
    
    /** return the SplashHandler used by JPF
     *  @return a SplashHandler
     */
    public SplashHandler getSplashHandler()
    {   
	if ( this.splashHandler == null )
	{
	    this.splashHandler = org.java.plugin.boot.Boot.getSplashHandler();
	}
	
	return this.splashHandler;
    }
    
    /** method called before the gui start (in EDT) */
    protected void beforeGuiStart()
    {
	
    }
    
    /** method called before the gui is started (in EDT) */
    protected void afterGuiStart()
    {
	
    }
    
    /** start platform */
    protected void start()
    {   
        /** start Kernel */
        super.start();
        
        /* start GUI */
        this.gui    = UserInterface.getInstance();
        
	Runnable run = null;
	
	run = new Runnable()
	{   public void run() {	beforeGuiStart(); } };
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }

	gui.start(getGuiBootStatus());

	run = new Runnable()
	{   public void run() {	afterGuiStart(); } };
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }

	run = new Runnable()
	{   public void run() {	EventConfigurationEngine.registerInEventSystem(UserInterface.EVENT_SYSTEM_ID, gui); } };
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
	
	/* initializing event system */
	if ( getKernel() != null )
	{   
	    run = new Runnable()
	    {   
		public void run()
		{
		    getKernel().getResources().registerEditorLauncher(gui);

		    try
		    {   getKernel().launchDefaultEditions(); }
		    catch (NoLauncherFoundException ex)
		    {   logger.error("unable to launch default editions", ex);

			ResourceBundle rb = ResourceBundle.getBundle(SiberiaGUIApplication.class.getName());

			String title = rb.getString("noEditorLauncherErrorTitle");
			String content = rb.getString("noEditorLauncherErrorMessage");

			JOptionPane.showMessageDialog(UserInterface.getInstance().getFrame(), title, content, JOptionPane.ERROR_MESSAGE);

			ex.printStackTrace();
		    }
		}
	    };
	    if ( SwingUtilities.isEventDispatchThread() )
	    {   run.run(); }
	    else
	    {   SwingUtilities.invokeLater(run); }
	}
	else
	{   throw new RuntimeException("Kernel not initialized"); }

	/** gui boot finished */

	run = new Runnable()
	{   public void run()
	    {
		getGuiBootStatus().setLabel(getGuiResourcebundle().getString("guiStarted"));
		getGuiBootStatus().setPercentageCompleted(100);
	    }
	};
	if ( SwingUtilities.isEventDispatchThread() )
	{   run.run(); }
	else
	{   SwingUtilities.invokeLater(run); }
	
	/** wait for EDT if not EDT
	 *  to force the main thread to not stop until graphical initailization are finished
	 *  else, the splash screen is made invisible very soon
	 */
        if ( ! SwingUtilities.isEventDispatchThread() )
        {  
	    final Thread t = Thread.currentThread();
	    
	    run = new Runnable()
	    {
		public void run()
		{	    
		    logger.info("all graphical initializations finished");
		    t.interrupt();
		}
	    };
	    
//            try
//            {   SwingUtilities.invokeAndWait(run); }
//            catch (InterruptedException ex)
//            {   ex.printStackTrace(); }
//            catch (InvocationTargetException ex)
//            {   ex.printStackTrace(); }
	    
            SwingUtilities.invokeLater(run);
	    
	    while(true)
	    {
		try
		{
		    Thread.sleep(300);
		    logger.info("thread " + Thread.currentThread() + " is waiting for graphical initializations");
		}
		catch(InterruptedException e)
		{
		    break;
		}
	    }
        }
    }
    
    /** ########################################################################
     *  ################# PropertyChangeListener implementation ################
     *  ######################################################################## */
    
    public void propertyChange(final PropertyChangeEvent evt)
    {
        if ( evt.getSource() == this.getGlobalBootStatus() )
        {   
	    Runnable runnable = null;
	    
	    if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_LABEL) && evt.getNewValue() instanceof String )
            {   
		runnable = new Runnable()
		{
		    public void run()
		    {
			if ( getSplashHandler() != null )
			{
			    getSplashHandler().setText( (String)evt.getNewValue() );
			}
		    }
		};
	    }
            else if ( evt.getPropertyName().equals(TaskStatus.PROPERTY_COMPLETED) && evt.getNewValue() instanceof Number )
            {   
		runnable = new Runnable()
		{
		    public void run()
		    {
			if ( getSplashHandler() != null )
			{
			    getSplashHandler().setProgress( ((Number)evt.getNewValue()).floatValue() / 100 );
			}
		    }
		};
	    }
	    
	    if ( runnable != null )
	    {
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
    }
    
}
