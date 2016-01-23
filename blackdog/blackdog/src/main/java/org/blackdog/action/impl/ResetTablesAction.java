/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.action.impl;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import org.siberia.kernel.Kernel;
import org.siberia.binding.DataBaseBindingManager;
import org.siberia.SiberiaApplicationInitializer;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;
import org.siberia.kernel.Kernel;

/**
 *
 * Reset all tables and restart application
 *
 * @author alexis
 */
public class ResetTablesAction extends org.siberia.ui.action.GenericAction
{
    /** Creates a new instance of ResetTablesAction */
    public ResetTablesAction()
    {	}

    public void actionPerformed(final ActionEvent e)
    {
	final ResourceBundle rb = ResourceBundle.getBundle(ResetTablesAction.class.getName());

	int answer = JOptionPane.showConfirmDialog(this.getWindow(e), 
			         	           rb.getString("confirm.dialog.message"),
				                   rb.getString("confirm.dialog.title"),
				                   JOptionPane.YES_NO_OPTION,
						   JOptionPane.WARNING_MESSAGE);
	
	if ( answer == JOptionPane.YES_OPTION )
	{
	    final DataBaseBindingManager manager = Kernel.getInstance().getDatabaseBindingManager();
	    
	    if ( manager != null )
	    {
		SwingWorkerDialog dialog = new SwingWorkerDialog(getWindow(e), false);
		
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		SwingWorker worker = new ExtendedSwingWorker()
		{
		    protected Object doInBackground() throws Exception
		    {
			try
			{
			    manager.dropAllTables();
			    
			    this.setProgress(100);
			    
			    JOptionPane.showMessageDialog(getWindow(e), 
							  rb.getString("restart.dialog.message"),
							  rb.getString("restart.dialog.title"),
							  JOptionPane.INFORMATION_MESSAGE);
			}
			finally
			{
			    Kernel.getInstance().close(true);
			    
			    System.exit(SiberiaApplicationInitializer.ERROR_CODE_RESTART);
			}
			
			return null;
		    }
		};
		
		dialog.setWorker(worker);

		JProgressBar progressBar = dialog.getProgressBar();
		progressBar.setIndeterminate(true);

		dialog.getLabel().setText(rb.getString("waiting.dialog.label"));
		dialog.setTitle(rb.getString("waiting.dialog.title"));

		dialog.display();
	    }
	}
    }
    
}
