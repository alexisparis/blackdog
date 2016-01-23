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
import java.beans.PropertyVetoException;
import javax.swing.AbstractAction;
import org.blackdog.type.AudioItem;
import org.blackdog.type.DefaultSongItem;
import org.blackdog.type.Mp3SongItem;
import org.blackdog.type.PlayList;
import org.siberia.binding.DataBaseBindingManager;
import org.siberia.binding.transaction.Transaction;
import org.siberia.kernel.Kernel;

/**
 *
 * @author alexis
 */
public class TestCreatePlayListAction extends AbstractAction
{
    
    /** Creates a new instance of TestCreatePlayListAction */
    public TestCreatePlayListAction()
    {
    }

    public void actionPerformed(ActionEvent ex2)
    {
        DataBaseBindingManager manager = Kernel.getInstance().getDatabaseBindingManager();
	
	Transaction transaction = null;
        
	try
	{
	    transaction = manager.createTransaction();
	    
	    PlayList list = new PlayList();

	    try
	    {   list.setName("tata"); }
	    catch (PropertyVetoException ex)
	    {   ex.printStackTrace(); }

	    AudioItem item1 = new DefaultSongItem();
	    AudioItem item2 = new DefaultSongItem();

	    try
	    {   item1.setName("tamere.ogg");
		item2.setName("Si seulement.mp3");
    //	    list.setContentItemAsChild(true);
	    }
	    catch(PropertyVetoException e)
	    {   e.printStackTrace(); }

	    manager.store(transaction, item1);

	    list.add(item1);

	    manager.store(transaction, list);

	    manager.store(transaction, item2);

	    list.add(item2);

	    manager.store(transaction, list);
	    
	    transaction.commit();
	    
	    transaction = null;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    if ( transaction != null )
	    {
		try
		{
			transaction.rollback();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	    }
	}
    }
}
