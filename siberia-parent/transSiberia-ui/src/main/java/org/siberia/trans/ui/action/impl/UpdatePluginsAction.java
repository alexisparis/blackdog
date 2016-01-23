/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.action.impl;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.awl.WizardConstants;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.kernel.Kernel;
import org.siberia.trans.PluginGraph;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.download.DownloadTransaction;
import org.siberia.trans.download.TransactionFactory;
import org.siberia.trans.ui.editor.impl.plugin.PluginChooserWizard;
import org.siberia.SiberiaApplicationInitializer;

/**
 *
 * Open a wizard that allow to choose and download desired plugins
 *
 * @author alexis
 */
public class UpdatePluginsAction extends TransSiberianAction
{   
    /** logger */
    private Logger logger = Logger.getLogger(DownloadPluginAction.class);
    
    /** Creates a new instance of DownloadPluginAction */
    public UpdatePluginsAction()
    {	}

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	Frame frame = null;
	if ( e.getSource() instanceof Component )
	{
	    Window window = SwingUtilities.getWindowAncestor((Component)e.getSource());
	    
	    if ( window instanceof Frame )
	    {
		frame = (Frame)window;
	    }
	}
	
	try
	{
	    List<PluginBuild> automatedBuilds = this.getTransSiberia().getAllBuildFromUpdateAll();
	    
	    if ( automatedBuilds == null || automatedBuilds.size() == 0 )
	    {
		throw new IllegalStateException("no build declared");
	    }
	    
	    TransactionFactory factory = new TransactionFactory()
	    {
		public DownloadTransaction createTransaction(PluginGraph graph)
		{
		    return new DownloadTransaction(graph)
		    {
			/** pre-commit method. called when sure that the transaction is not in rollback only
			 */
			protected void preCommit()
			{
			    super.preCommit();
			    
			    /** remoev all plugins from plugin directory */
			    PluginResources.getInstance().removeAllPlugins();
			}
			
			/** post-commit method. called when sure that the transaction is not in rollback only
			 */
			@Override
			protected void postCommit()
			{
			    super.postCommit();
			    
			    /** set the last date of update-all */
			    getTransSiberia().setLastUpdateAllDate(Calendar.getInstance().getTime());
			}
		    };
		}
	    };
	    
	    PluginChooserWizard wizard = new PluginChooserWizard(this.getTransSiberia(), frame, automatedBuilds, factory);

	    //wizard.pack();
	    wizard.setSize(600, 450);

	    wizard.setVisibleOnCenterOfOwner();

	    int result = wizard.getReturnCode();

	    if ( result == WizardConstants.WIZARD_VALID_OPTION )
	    {
		DownloadTransaction transaction = wizard.getDownloadTransaction();

		if ( transaction == null )
		{
		    JOptionPane.showMessageDialog( frame, "error while committing download transaction", "Error", JOptionPane.ERROR_MESSAGE );
		}
		else
		{   
		    if ( transaction.isRollbackOnly() )
		    {
			transaction.rollback();
			JOptionPane.showMessageDialog( frame, "error while committing download transaction", "Error", JOptionPane.ERROR_MESSAGE );
		    }
		    else
		    {
			try
			{
			    transaction.commit();
			    logger.info("download transaction committed");

			    if ( transaction.isRebootNeeded() )
			    {
				ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());
				JOptionPane.showMessageDialog(frame, rb.getString("reboot.message"), rb.getString("reboot.title"), JOptionPane.INFORMATION_MESSAGE);

				Kernel.getInstance().close(true);
			    
				System.exit(SiberiaApplicationInitializer.ERROR_CODE_RESTART);
			    }
			}
			catch(Exception ex)
			{
			    logger.error("error while committing download transaction", ex);

			    ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());
			    JOptionPane.showMessageDialog(frame, rb.getString("reboot.invalid.message"), rb.getString("reboot.invalid.title"), JOptionPane.INFORMATION_MESSAGE);

			    Kernel.getInstance().close(true);
			    
			    System.exit(SiberiaApplicationInitializer.ERROR_CODE_RESTART);

			}
		    }
		}
	    }
	}
	catch(Exception ec)
	{
	    logger.error("unable to process update-all", ec);
	    
	    ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());
	    JOptionPane.showMessageDialog(frame, rb.getString("update-all.message"), rb.getString("update-all.title"), JOptionPane.ERROR_MESSAGE);
	}
    }
    
}
