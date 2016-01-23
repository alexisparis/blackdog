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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.trans.type.RepositoryPluginContainer;
import org.siberia.trans.type.SiberiaRepositoryFactory;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.trans.type.repository.SiberiaRepositoryLocation;
import org.siberia.type.SibType;
import org.siberia.ui.action.AbstractSingleTypeAction;

/**
 *
 * Action that allow to open the mail client to send a mail to the administrator of
 * a repository
 *
 * @author alexis
 */
public class MailRepositoryAdministratorAction<E extends SibType> extends AbstractSingleTypeAction<E>
{
    /* logger */
    private static Logger logger = Logger.getLogger(MailRepositoryAdministratorAction.class);
    
    /** Creates a new instance of MailRepositoryAdministratorAction */
    public MailRepositoryAdministratorAction()
    {   super(); }

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(final ActionEvent e)
    {   
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		final String mail = getMail();
		if ( Desktop.isDesktopSupported() )
		{
		    Runnable edtRunnable = new Runnable()
		    {
			public void run()
			{
			    try
			    {
				if ( Desktop.getDesktop().isSupported(Desktop.Action.MAIL) )
				{
				    Desktop.getDesktop().mail(new URI("mailto:" + mail));
				}
				else
				{
				    throw new IllegalStateException("mail unsupported by Desktop");
				}
			    }
			    catch (Exception ex)
			    {
				logger.error("unable to execute mail with uri='mailto:" + mail + "'", ex);

				Component c = null;
				if ( e.getSource() instanceof Component )
				{
				    c = (Component)e.getSource();
				}

				ResourceBundle rb = ResourceBundle.getBundle(MailRepositoryAdministratorAction.class.getName());

				JOptionPane.showMessageDialog(c, rb.getString("mailErrorMessage"), rb.getString("mailErrorTitle"), JOptionPane.ERROR_MESSAGE);
			    }
			}
		    };
		    
		    SwingUtilities.invokeLater(edtRunnable);
		}
	    }
	};
	
	new Thread(runnable).start();
    }
    
    /** return the mail linked to the item associated with this action
     *	@return a String
     */
    public String getMail()
    {
	SibType type = this.getType();
	
	SiberiaRepository repository = null;
        
        if ( type instanceof SiberiaRepository )
	{
	    repository = (SiberiaRepository)type;
	}
	else if ( type instanceof RepositoryPluginContainer )
        {   
	    repository = ((RepositoryPluginContainer)type).getRepository();
	}
	else if ( type instanceof SiberiaRepositoryLocation )
	{
	    URL url = ((SiberiaRepositoryLocation)type).getURL();
	    try
	    {
		repository = SiberiaRepositoryFactory.create(url);
	    }
	    catch (Exception ex)
	    {
		logger.warn("unable to create repository with '" + url + "' to get the administrator mail", ex);
	    }
	}
	
	return (repository == null ? null : repository.getAdministratorMail());
    }
    
    /** set the types related to this action
     *  @return an array of SibType
     */
    @Override
    public void setTypes(E[] types)
    {   super.setTypes(types);
	
	this.setEnabled(false);
        
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		String mail = getMail();

		boolean enabled = false;

		if ( mail != null && mail.trim().length() > 0 && Desktop.isDesktopSupported() )
		{
		    enabled = true;
		}
		
		final boolean finalEnabled = enabled;

		SwingUtilities.invokeLater(new Runnable()
		{
		    public void run()
		    {
			setEnabled( finalEnabled );
		    }
		});
		
	    }
	};
	new Thread(runnable).start();
    }
    
}
