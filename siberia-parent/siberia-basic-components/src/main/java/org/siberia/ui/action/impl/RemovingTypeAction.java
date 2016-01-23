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
package org.siberia.ui.action.impl;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.ui.action.AbstractSingleTypeAction;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;

/**
 *
 * Action that remove items
 *
 *  if one of the related items could not be removed, then the action is disabled for all items
 *
 * @author alexis
 */
public class RemovingTypeAction<E extends SibType> extends AbstractSingleTypeAction<E>
{
    /** logger */
    private Logger logger = Logger.getLogger(RemovingTypeAction.class);
    
    /** Creates a new instance of TypeEditingAction */
    public RemovingTypeAction()
    {   super(); }
    
    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   final List<E> items = this.getTypes();
	if ( items != null )
	{
	    /** this action could come from a TablePanel, in this special case,
	     *	we do not necessairly use getParent method to determine the parent of each items to delete.
	     *	since an item could appear in severall SibList, we use the action event e to determine if its component descends from a SibTypeListTable.
	     *	if so, the parent is the list managed by this list.
	     */
	    SibCollection _tableCollection = null;
	    
	    if ( e.getSource() instanceof Component )
	    {
		Component currentComponent = (Component)e.getSource();
		
		while(currentComponent != null && _tableCollection == null)
		{
		    if ( currentComponent instanceof JTable )
		    {
			TableModel model = ((JTable)currentComponent).getModel();
			
			if ( model instanceof SibTypeListTableModel )
			{
			    _tableCollection = ((SibTypeListTableModel)model).getList();
			}
			else
			{
			    /** no need to go up to found another table */
			    break;
			}
		    }
		    else if ( currentComponent instanceof JPopupMenu )
		    {
			currentComponent = ((JPopupMenu)currentComponent).getInvoker();
		    }
		    else
		    {
			currentComponent = currentComponent.getParent();
		    }
		}
	    }
	    
	    final SibCollection tableCollection = _tableCollection;
	    
	    final SwingWorkerDialog dialog = new SwingWorkerDialog(this.getWindow(e), true);
	    
	    dialog.setDifferWorkerExecutionEnabled(false);
	    
	    SwingWorker worker = new ExtendedSwingWorker()
	    {
		protected Object doInBackground() throws Exception
		{
		    try
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("starting remove swing worker");
			}

			long time = System.currentTimeMillis();

			boolean interrupt = false;

			Map<SibCollection, List<SibType>> parentMap = null;

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("tableCollection is " + tableCollection);
			}

			if ( tableCollection == null )
			{
			    parentMap = new HashMap<SibCollection, List<SibType>>();

			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("looping on items to remove to determine the parent they are related on");
			    }

			    for(int i = 0; i < items.size() && ! interrupt; i++)
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("trying to remove item at " + i);
				}

				if ( dialog != null && ! dialog.isVisible() )
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("remove interrupted");
				    }
				    interrupt = true;
				    continue;
				}

				SibType current = items.get(i);

				if ( current != null && current.isRemovable() )
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("item " + current + " can be removed");
				    }
				    SibType parent = current.getParent();

				    if ( parent == null )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("parent of " + current + " is null --> cannot be removed");
					}
				    }
				    else
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("parent of " + current + " is " + parent);
					}
					if ( parent instanceof SibCollection )
					{
					    if ( ((SibCollection)parent).isRemoveAuthorized() )
					    {
						/* if the parent is already contained in the map, then update its list of items to remove
						 *  else, create a new List
						 */
						List<SibType> currentList = null;
						if ( ! parentMap.containsKey(parent) )
						{
						    currentList = new ArrayList<SibType>();
						    parentMap.put((SibCollection)parent, currentList);

						    if ( logger.isDebugEnabled() )
						    {
							logger.debug("parent " + parent + " added in the parent map");
						    }
						}
						else
						{
						    /** make sure, we will not delete an item from an object that is equals to parent
						     *	so --> loop on parentMap keys to verify parent by reference
						     *	this is not a very efficient method
						     *	but in most case, deleting items will occurs on the same parent
						     */
						    Iterator<SibCollection> it = parentMap.keySet().iterator();
						    while(it.hasNext())
						    {
							SibType currentItemInMap = it.next();

							if ( currentItemInMap == parent )
							{
							    currentList = parentMap.get(currentItemInMap);
							    break;
							}
						    }

						    /** the parent was not in the map, so create a new entry for him */
						    if ( currentList == null ) 
						    {
							currentList = new ArrayList<SibType>();
							parentMap.put((SibCollection)parent, currentList);
						    }
						}

						if ( currentList != null )
						{
						    currentList.add(current);
						}
					    }
					}
					else
					{
					    if ( logger.isDebugEnabled() )
					    {
						logger.debug("parent of " + current + " : " + parent + " is not a collection --> could not be removed");
					    }
					}
				    }
				}
				else
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("item " + current + " cannot be removed");
				    }
				}
			    }

			    System.out.println("second phase parentMap size : " + parentMap.size());

			    /** process deletions on map items */
			    if ( ! interrupt && parentMap != null )
			    {
				Iterator<Map.Entry<SibCollection, List<SibType>>> entries = parentMap.entrySet().iterator();

				while(entries.hasNext() && ! interrupt)
				{
				    if ( dialog != null && ! dialog.isVisible() )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("remove (2) interrupted");
					}
					interrupt = true;
				    }
				    else
				    {
					Map.Entry<SibCollection, List<SibType>> currentEntry = entries.next();

					if ( logger.isDebugEnabled() )
					{
					    logger.debug("processing remove from entry : " + currentEntry);
					}

					if ( currentEntry != null )
					{
					    SibCollection currentParent = currentEntry.getKey();
					    List<SibType> children = currentEntry.getValue();

					    if ( children != null && ! children.isEmpty() && currentParent != null )
					    {
						System.out.println("size before : " + currentParent.size());
						System.out.println("items to remove count : " + children.size());
						currentParent.removeAll(children);
						System.out.println("size before : " + currentParent.size());
						children.clear();
					    }
					}
				    }
				}

				parentMap.clear();
			    }
			}
			else
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("trying to remove all items by calling removeAll on collection " + tableCollection + " (identity hashcode=" +
						System.identityHashCode(tableCollection) + ")");
			    }
			    boolean result = tableCollection.removeAll(items);
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("remove all finished --> returns " + result);
			    }
			}

			System.out.println("third phase");

			/* to hide dialog */
			this.setProgress(100);

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("remove swing worker ended");
			}
			logger.info(items.size() + " items deleted in " + (System.currentTimeMillis() - time) + " ms");
		    }
		    catch(Exception e)
		    {
			this.setProgress(100);
			
			logger.error("got error while removing items", e);
		    }
		    finally
		    {
			if ( items != null )
			{
			    items.clear();
			}
		    }

		    return null;
		}
	    };
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before initializing dialog with remove swing worker");
	    }
	    dialog.setWorker(worker);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after initializing dialog with remove swing worker");
	    }
	    dialog.getProgressBar().setIndeterminate(true);

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before loading resource bundle of remove action");
	    }
	    ResourceBundle rb = ResourceBundle.getBundle(RemovingTypeAction.class.getName());

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after loading resource bundle of remove action");
	    }
	    
	    dialog.getLabel().setText(rb.getString("dialog.label"));
	    dialog.setTitle(rb.getString("dialog.title"));

	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("before displaying remove dialog");
	    }
	    dialog.display();
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("after displaying remove dialog");
	    }
	}
	else
	{   new Exception("et merde").printStackTrace(); }
    }
    
    /** set the type related to this action
     *  @return a SibType
     */
    @Override
    public void setTypes(E... types)
    {   super.setTypes(types);
	
	boolean enabled = true;
	
	if ( types == null || types.length == 0 )
	{   enabled = false; }
	else
	{
	    for(int i = 0; i < types.length; i++)
	    {
		SibType item = types[i];
		
		if ( item != null )
		{
		    SibType parent = item.getParent();
		    
		    if ( parent instanceof SibCollection )
		    {   if ( ((SibCollection)parent).isRemoveAuthorized() )
			{   enabled = true; }
		    }
		    
		    if ( enabled )
		    {   enabled = this.getType().isRemovable(); }
		    
		    if ( ! enabled )
		    {   break; }
		}
	    }
	}
	
	this.setEnabled(enabled);
    }
    
}