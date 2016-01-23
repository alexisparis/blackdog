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
package org.siberia.ui.swing.transfer;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;
import org.siberia.type.SibList;
import org.siberia.ui.action.impl.AddingTypeAction;
import org.siberia.ui.swing.dialog.ExtendedSwingWorker;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;
import org.siberia.ui.swing.tree.GenericTree;

/**
 *
 * @author alexis
 */
public class SibTypeTransferHandler extends TransferHandler
{
    /** logger */
    private Logger logger = Logger.getLogger(SibTypeTransferHandler.class);
    
    /**
     * Creates a new instance of SibTypeTransferHandler
     */
    public SibTypeTransferHandler()
    {	}
    
    public int getSourceActions(JComponent c)
    {
	int result = TransferHandler.NONE;
	
	if ( c instanceof ComponentTransfer )
	{
	    result = ((ComponentTransfer)c).getTransferAction();
	}
	
	return result;
    }
    
    /** permet de prendre en compte le transf�rable
     *  @param t transferable
     *  @return true si l'on consid�re que le transferable a �t� pris en compte
     */
    protected boolean processTransferable(Transferable t) throws IOException, UnsupportedFlavorException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling processTransferable");
	}
        boolean result = false;
        
//        List files = (List)t.getTransferData(DataFlavor.javaFileListFlavor) ;
//        for ( int i = 0, size = files.size() ; i < size ; i++ )
//        {
//            this.importData( (File)files.get(i), null );
//        }
        
        result = true;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("processTransferable returns " + result);
	}
        
        return result;
    }
    
    /** return the component from the given component<br<
     *	if the component is a JScrollPane, then the view of its viewport is returned
     *	@param component a Component
     *	@return a Component
     */
    private Component findDataComponent(Component component)
    {
	Component _c = component;

	if ( _c != null )
	{
	    if ( _c instanceof JScrollPane )
	    {
		_c = ((JScrollPane)_c).getViewport();
	    }
	    if ( _c instanceof JViewport )
	    {
		_c = ((JViewport)_c).getView();
	    }
	}
	
	return _c;
    }
    
    @Override
    public boolean importData(JComponent comp, Transferable t)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling importData on " + (comp == null ? null : comp.getName() + " " + comp.getClass()));
	}
	
        boolean result = super.importData(comp, t);
        
	if ( t != null && ! result )
	{
	    DataFlavor[] flavors = t.getTransferDataFlavors();
	    
	    if ( this.hasSibTypeListFlavor(flavors) )
	    {
		Component _c = this.findDataComponent(comp);
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("component is " + (_c == null ? null : _c.getName() + " " + _c.getClass()));
		}
		
		try
		{
		    Object toImport = t.getTransferData(flavors[1]);
		    
		    if ( toImport instanceof SibTypeList )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("item to import is a SibTypeList");
			}
			final SibTypeList list = (SibTypeList)toImport;
			
			SibList listWhereToAdd = null;
				
			if ( _c instanceof SibTypeListTable )
			{
			    TableModel model = ((SibTypeListTable)_c).getModel();

			    if ( model instanceof SibTypeListTableModel )
			    {
				listWhereToAdd = ((SibTypeListTableModel)model).getList();
			    }
			    else
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("model is not a " + SibTypeListTableModel.class.getSimpleName() + " --> yield");
				}
			    }
			}
			else if ( _c instanceof GenericTree )
			{
			    SibType selection = ((GenericTree)_c).getElementUnderMouse();

			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("element under mouse is " + selection);
			    }

			    if ( selection instanceof SibList )
			    {
				listWhereToAdd = (SibList)selection;
			    }
			    else
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("element under mouse is not a SibList --> yield");
				}
			    }
			}
			else
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("component is not a " + SibTypeListTable.class.getSimpleName() + " --> yield");
			    }
			}
			
			if ( listWhereToAdd != null )
			{
			    result = true;
			    
			    final SibList _listWhereToAdd = listWhereToAdd;
			    
			    final SwingWorkerDialog dialog = new SwingWorkerDialog(SwingUtilities.getWindowAncestor(comp), true);

			    dialog.setDifferWorkerExecutionEnabled(false);

			    SwingWorker worker = new ExtendedSwingWorker()
			    {
				protected Object doInBackground() throws Exception
				{
				    try
				    {
					for(int i = 0; i < list.size() && dialog.isVisible(); i++)
					{
					    SibType type = list.get(i);

					    if ( logger.isDebugEnabled() )
					    {
						logger.debug("considering item '" + type + "'");
					    }

					    if ( type != null )
					    {
						if ( _listWhereToAdd != type.getParent() )
						{
						    int refIndex = _listWhereToAdd.indexOfByReference(type);

						    if ( logger.isDebugEnabled() )
						    {
							logger.debug("reference index of '" + type + "' in '" + _listWhereToAdd + "' is " + refIndex);
						    }

						    if ( refIndex < 0 && ! _listWhereToAdd.contains(type) )
						    {
							if ( _listWhereToAdd.add(type) )
							{
							    if ( logger.isDebugEnabled() )
							    {
								logger.debug("'" + type + "' added to '" + _listWhereToAdd + "'");
							    }
							}
							else
							{
							    if ( logger.isDebugEnabled() )
							    {
								logger.debug("could not add '" + type + "' to '" + _listWhereToAdd + "'");
							    }
							}
						    }
						    else
						    {
							if ( logger.isDebugEnabled() )
							{
							    logger.debug("item '" + type + "' is already contained by '" + _listWhereToAdd + "'");
							}
						    }
						}
						else
						{
						    if ( logger.isDebugEnabled() )
						    {
							logger.debug("trying to add an item to its current parent --> nothing to do");
						    }
						}
					    }
					}
				    }
				    catch(Exception e)
				    {
					logger.error("got error while removing items", e);
				    }
				    finally
				    {
					this.setProgress(100);

				    }

				    return null;
				}
			    };
			    dialog.setWorker(worker);
			    dialog.getProgressBar().setIndeterminate(true);
			    ResourceBundle rb = ResourceBundle.getBundle(SibTypeTransferHandler.class.getName());
			    dialog.getLabel().setText(rb.getString("dialog.label"));
			    dialog.setTitle(rb.getString("dialog.title"));
			    dialog.display();
			}
		    }
		    else
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("item to import is not a SibTypeList --> yield");
			}
		    }
		}
		catch(UnsupportedFlavorException e)
		{
		    logger.error("got error while trying to extract flavor from transferable", e);
		}
		catch(IOException e)
		{
		    logger.error("got error while trying to extract flavor from transferable", e);
		}
	    }
	    else
	    {
		logger.info("no SibTypeList flavor --> yield");
		result = false;
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("importData on " + (comp == null ? null : comp.getName() + " " + comp.getClass()) + " returns " + result);
	}

        return result;
    }
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling exportDone on component " + (source == null ? null : source.getName() + " " + source.getClass()));
	}
	
        if ( action == MOVE || action == COPY_OR_MOVE || action == NONE )
        {
            DataFlavor[] flavors = data.getTransferDataFlavors();
            
	    if ( this.hasSibTypeListFlavor(data.getTransferDataFlavors()) )
	    {
		try
		{
		    Object toExport = data.getTransferData(flavors[1]);

		    System.out.println("to export : " + toExport);
		}
		catch(UnsupportedFlavorException e)
		{
		    e.printStackTrace();
		}
		catch(IOException e)
		{
		    e.printStackTrace();
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of exportDone on " + (source == null ? null : source.getName() + " " + source.getClass()));
	}
    }
    
    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling canImport on component " + (comp == null ? null : comp.getName() + " " + comp.getClass()));
	}
        boolean result = false;
        
	if ( ! result )
	{
	    if ( this.hasSibTypeListFlavor(transferFlavors) )
	    {
		/** find the SibType in which item will be added */
		SibType newParent = null;
		
		Component _c = this.findDataComponent(comp);

		if ( _c instanceof SibTypeListTable )
		{
		    TableModel model = ((SibTypeListTable)_c).getModel();

		    if ( model instanceof SibTypeListTableModel )
		    {
			newParent = ((SibTypeListTableModel)model).getList();
		    }
		    else
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("model is not a " + SibTypeListTableModel.class.getSimpleName() + " --> yield");
			}
		    }
		}
		else if ( _c instanceof GenericTree )
		{
		    newParent = ((GenericTree)_c).getElementUnderMouse();
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("could not determine SibType from component " + (comp == null ? null : comp.getName() + " " + comp.getClass()));
		    }
		}
		
		if ( newParent instanceof SibList )
		{
		    /*
		     * determine if add is allowed
		     *	and if the content of items to import is good
		     */
		    if ( ((SibList)newParent).isCreateAuthorized() )
		    {
			Class c = ((SibList)newParent).getAllowedClass();
			
			if ( c == null )
			{
			    result = true;
			}
			else
			{
			    result = true;
			}
		    }
		    else
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("create is not allowed in the list " + newParent);
			}
		    }
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("parent is not a SibList --> dragged item could not be added");
		    }
		}
	    }
	    else
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("no SibTypeList --> yield");
		}
	    }
	    
//	    if ( ! result && this.hasFileFlavor(transferFlavors) )
//	    {
//		System.out.println("has file flavor");
//		result = true;
//	    }
//	    if ( ! result && this.hasStringFlavor(transferFlavors) )
//	    {
//		System.out.println("has string flavor");
//		result = false;
//	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("canImport on component " + (comp == null ? null : comp.getName() + " " + comp.getClass()) + " returns " + result);
	}
	
	return result;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c)
    {
        Transferable transferable = null;
        
        /* s'il s'agit d'une table avec un mod�le supportant la gestion de la ged
         *  alors, on tente de cr�er un Transferable de type : GedDocumentsTransferable
         */
	Component _c = this.findDataComponent(c);
	
	Object[] selection = null;
	
	if ( _c instanceof SibTypeListTable )
	{
	    SibTypeListTable table = (SibTypeListTable)_c;
	    
	    if ( table.getSelectedRowCount() > 0 )
	    {
		selection = table.getSelectedObjects();
	    }
	}
	else if ( _c instanceof GenericTree )
	{
	    selection = ((GenericTree)_c).getSelectedElements();
	}
	
	if ( selection != null )
	{
	    SibTypeList list = new SibTypeList();
	    
	    for(int i = 0; i < selection.length; i++)
	    {
		Object current = selection[i];

		if ( current instanceof SibType )
		{
		    if ( ((SibType)current).isRemovable() )
		    {
			list.add( (SibType)current );
		    }
		}
	    }
	    
	    transferable = new SibTypeTransferable(list);
	}
        
        if ( transferable == null )
        {
            transferable = super.createTransferable(c);
        }
        
        return transferable;
    }
    
    /** ########################################################################
     *  ################# array flavors has specific flavor ? ##################
     *  ######################################################################## */

    protected boolean hasFileFlavor(DataFlavor[] flavors)
    {   
        boolean result = false;
        
        for (int i = 0; i < flavors.length; i++)
        {   if ( DataFlavor.javaFileListFlavor.equals(flavors[i]) )
            {   result = true;
                break;
            }
        }
        return result;
    }

    protected boolean hasStringFlavor(DataFlavor[] flavors)
    {
        boolean result = false;
        
        for (int i = 0; i < flavors.length; i++)
        {   if (DataFlavor.stringFlavor.equals(flavors[i]))
            {   result = true;
                break;
            }
        }
        return result;
    }

    protected boolean hasSibTypeListFlavor(DataFlavor[] flavors)
    {
        boolean result = false;
        
        for (int i = 0; i < flavors.length; i++)
        {   if ( flavors[i].getMimeType().equals(DataFlavor.javaJVMLocalObjectMimeType + "; class=" + SibTypeList.class.getName()) )
            {	result = true;
                break;
            }
        }
        return result;
    }
    
}
