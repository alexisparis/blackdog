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
package org.siberia.ui.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.siberia.ui.swing.table.conf.TableConfiguration;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.table.model.PropertyBasedTableModel;
import org.siberia.ui.swing.table.model.PropertyDeclaration;

/**
 *
 * @author alexis
 */
public class SibListTablePanel extends TablePanel<SibTypeListTable>
{
    /** logger */
    private Logger logger = Logger.getLogger(SibListTablePanel.class);
    
    /** Creates a new instance of SibListTablePanel */
    public SibListTablePanel()
    {   super(); }
    
    @Override
    protected SibTypeListTable createTable()
    {   return new SibTypeListTable(); }
    
    /** create a TableConfiguration that describe the state of the TablePanel
     *	@return a Tableconfiguration
     */
    @Override
    public TableConfiguration createConfiguration()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createConfiguration()");
	}
	
	TableConfiguration conf = super.createConfiguration();
	
	if ( conf != null )
	{
	    if ( this.getTable() != null )
	    {
		if ( this.getTable().getModel() instanceof PropertyBasedTableModel )
		{
		    PropertyBasedTableModel introModel = (PropertyBasedTableModel)this.getTable().getModel();
		    
		    List<String> displayedColumns = new ArrayList<String>(introModel.getPropertyDeclarationCount());
		    
//		    for(int i = 0; i < introModel.getPropertyDeclarationCount(); i++)
//		    {
//			PropertyDeclaration decl = introModel.getPropertyDeclaration(i);
//			
//			if ( decl != null && decl.isEnabled() )
//			{
//			    displayedColumns.add( decl.getPropertyName() );
//			}
//		    }
		    
		    Map<Integer, String> propertyPositions = new HashMap<Integer, String>(introModel.getPropertyDeclarationCount());
		    
		    Set<String> propertyWithoutPositions = new HashSet<String>();
		    
		    for(int i = 0; i < introModel.getPropertyDeclarationCount(); i++)
		    {
			PropertyDeclaration decl = introModel.getPropertyDeclaration(i);
			
			if ( decl != null && decl.isEnabled() )
			{
			    /** find the column position to feed propertyPositions
			     *	and order property
			     */
			    int index = introModel.getColumnIndexOfProperty(decl.getPropertyName());
			    
			    int convertedIndex = this.getTable().convertColumnIndexToView(index);
			    
			    if ( propertyPositions.containsKey(convertedIndex) )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("adding property '" + decl.getPropertyName() + "' as displayed property");
				}
				propertyWithoutPositions.add(decl.getPropertyName());
			    }
			    else
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("adding property '" + decl.getPropertyName() + "' as displayed property at view index " + convertedIndex);
				}
				propertyPositions.put(convertedIndex, decl.getPropertyName());
			    }
			}
			    
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("setting preferred size " + decl.getPreferredSize() + " for property '" +
					 decl.getPropertyName() + "'");
			}
			conf.setColumnPreferredSizeForProperty(decl.getPropertyName(), decl.getPreferredSize());
		    }
		    
		    List<Integer> positions = new ArrayList<Integer>(propertyPositions.keySet());
		    Collections.sort(positions);
		    
		    for(int i = 0; i < positions.size(); i++)
		    {
			displayedColumns.add(propertyPositions.get(positions.get(i)));
		    }
		    
		    displayedColumns.addAll(propertyWithoutPositions);
		    
//		    TableColumnModel columnModel = this.getTable().getColumnModel();
//		    for(int i = 0; i < columnModel.getColumnCount(); i++)
//		    {
//			TableColumn column = columnModel.getColumn(i);
//			
//			int modelIndex = column.getModelIndex();
//			
//			if ( modelIndex >= 0 && modelIndex < introModel.getPropertyDeclarationCount() )
//			{
//			    PropertyDeclaration declaration = introModel.getPropertyDeclaration(modelIndex);
//			    
//			    if ( logger.isDebugEnabled() )
//			    {
//				logger.debug("considerating " + declaration + " which is " +
//					     (declaration.isEnabled() ? "enabled" : "disabled"));
//			    }
//			    
//			    /** it is visible so it must be enabled */
//			    if ( declaration.isEnabled() )
//			    {
//				displayedColumns.add(declaration.getPropertyName());
//				
//				if ( logger.isDebugEnabled() )
//				{
//				    logger.debug("adding property '" + declaration.getPropertyName() + "' as displayed property");
//				}
//			    }
//			    
//			    if ( logger.isDebugEnabled() )
//			    {
//				logger.debug("setting preferred size " + declaration.getPreferredSize() + " for property '" +
//					     declaration.getPropertyName() + "'");
//			    }
//			    conf.setColumnPreferredSizeForProperty(declaration.getPropertyName(), declaration.getPreferredSize());
//			}
//		    }
		    
		    if ( displayedColumns.size() == 0 )
		    {
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("configuration table configuration without any displayed properties");
			}
			conf.setDisplayedColumns((String[])null);
		    }
		    else
		    {
			if ( logger.isDebugEnabled() )
			{
			    StringBuffer buffer = new StringBuffer();
			    
			    for(int i = 0; i < displayedColumns.size(); i++)
			    {
				buffer.append(displayedColumns.get(i));
				
				if ( i < displayedColumns.size() - 1 )
				{
				    buffer.append(", ");
				}
			    }
			    logger.debug("configuration table configuration with displayed properties = {" + buffer + "}");
			}
			
			/** consider the column model to reorder columns */
			conf.setDisplayedColumns( (String[])displayedColumns.toArray(new String[displayedColumns.size()]) );
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createConfiguration() returns " + conf);
	    logger.debug("exiting createConfiguration()");
	}
	
	return conf;
    }
}
