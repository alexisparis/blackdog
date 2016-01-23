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
package org.siberia.ui.swing.table.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * Object that represents the configuration of a TablePanel
 *
 * @author alexis
 */
public class TableConfiguration implements Serializable
{   
    /** property displayed columns */
    private static final String PROPERTY_COLUMNS                   = "displayedColumns";
    
    /** property row number displayed */
    private static final String PROPERTY_ROW_NUMBER_DISPLAYED      = "rowNumberDisplayed";
    
    /** property page size */
    private static final String PROPERTY_PAGE_SIZE                 = "pageSize";
    
    /** property page size */
    private static final String PROPERTY_HORIZONTAL_SCROLL_ENABLED = "horizontalScrollEnabled";
    
    /** property horizontal grid */
    private static final String PROPERTY_HORIZONTAL_GRID           = "horizontalLines";
    
    /** property vertical grid */
    private static final String PROPERTY_VERTICAL_GRID             = "verticalLines";
    
    /** prefix for property related to preferred size of column linked to a property
     *	the name of such a property for 'name' is PROPERTY_PREF_COLUMN_SIZE_PREFIX + 'name'
     */
    private static final String PROPERTY_PREF_COLUMN_SIZE_PREFIX   = "column.preferredSize.";
    
    /** logger */
    private static Logger        logger                  = Logger.getLogger(TableConfiguration.class);
    
    /** selected columns */
    private String[]             displayedColumns        = null;
    
    /** row number displayed */
    private boolean              rowNumberDisplayed      = true;
    
    /** page size */
    private int                  pageSize                = -1;
    
    /** horizontal scroll enabled */
    private boolean              horizontalScrollEnabled = false;
    
    /* show horizontal lines */
    private boolean              horizontalLines         = false;
    
    /* show vertical lines */
    private boolean              verticalLines           = false;
    
    /** map that link property name and its preferred column size */
    private Map<String, Integer> columnsPreferredSize    = new HashMap<String, Integer>();
    
    /** Creates a new instance of TableConfiguration
     *	@param displayedColumns an array of String representing the columns that are displayed
     *	@param horizontalScrollEnabled true to enable horizontal scroll
     *	@param rowNumberDisplayed
     */
    public TableConfiguration(String[] displayedColumns, boolean horizontalScrollEnabled, boolean rowNumberDisplayed)
    {
	this(displayedColumns, horizontalScrollEnabled, rowNumberDisplayed, -1);
    }
    
    /** Creates a new instance of TableConfiguration
     *	@param displayedColumns an array of String representing the columns that are displayed
     *	@param horizontalScrollEnabled true to enable horizontal scroll
     *	@param rowNumberDisplayed
     *	@param pageSize
     */
    public TableConfiguration(String[] displayedColumns, boolean horizontalScrollEnabled, boolean rowNumberDisplayed, int pageSize)
    {
	this(displayedColumns, horizontalScrollEnabled, rowNumberDisplayed, pageSize, false, false);
    }
    
    /** Creates a new instance of TableConfiguration
     *	@param displayedColumns an array of String representing the columns that are displayed
     *	@param horizontalScrollEnabled true to enable horizontal scroll
     *	@param rowNumberDisplayed
     *	@param pageSize
     */
    public TableConfiguration(String[] displayedColumns, boolean horizontalScrollEnabled, boolean rowNumberDisplayed, int pageSize, 
			      boolean horizontalGrid, boolean verticalGrid)
    {
	this.displayedColumns        = displayedColumns;
	this.setHorizontalScrollEnabled(horizontalScrollEnabled);
	this.setRowNumberDisplayed(rowNumberDisplayed);
	this.setPageSize(pageSize);
	this.setHorizontalLinesShown(horizontalGrid);
	this.setVerticalLinesShown(verticalGrid);
    }
    
    /** return the preferred size for the column linked to the given property name
     *	@param propertyName the name of a property
     *	@return an integer or -1 if not set
     */
    public int getColumnPreferredSizeForProperty(String propertyName)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getColumnPreferredSizeForProperty(String)");
	    logger.debug("calling getColumnPreferredSizeForProperty(String) with " + propertyName);
	}
	
	Integer result = this.columnsPreferredSize.get(propertyName);
	
	if ( result == null )
	{
	    result = -1;
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getColumnPreferredSizeForProperty(String) returns " + result);
	    logger.debug("exiting getColumnPreferredSizeForProperty(String)");
	}
	
	return result;
    }
    
    /** intialize the preferred size for the column linked to the given property name
     *	@param propertyName the name of a property
     *	@param size the preferred size to apply
     */
    public void setColumnPreferredSizeForProperty(String propertyName, int size)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setColumnPreferredSizeForProperty(String, int)");
	    logger.debug("calling setColumnPreferredSizeForProperty(String, int) with " + propertyName + " and " + size);
	}
	this.columnsPreferredSize.put(propertyName, size);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setColumnPreferredSizeForProperty(String, int)");
	}
    }
    
    /** clear the columns preferred size
     */
    public void clearColumnPreferredSizeForProperty()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering clearColumnPreferredSizeForProperty()");
	}
	this.columnsPreferredSize.clear();
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting clearColumnPreferredSizeForProperty()");
	}
    }
    
    /** indicate if the vertical grid is enabled
    
    /** returns the displayed columns
     *	@return an array of String
     */
    public String[] getDisplayedColumns()
    {
	return this.displayedColumns;
    }
    
    /* set the displayed columns
     *	@param columns an array of String
     */
    public void setDisplayedColumns(String... columns)
    {
	if ( logger.isDebugEnabled() )
	{
	    StringBuffer buffer = new StringBuffer();
	    if ( columns != null )
	    {
		for(int i = 0; i < columns.length; i++)
		{
		    buffer.append(columns[i]);
		    
		    if ( i < columns.length - 1 )
		    {
			buffer.append(", ");
		    }
		}
	    }
	    logger.debug("entering setDisplayedColumns(String[])");
	    logger.debug("calling setDisplayedColumns(String[]) with {" + buffer + "}");
	}
	this.displayedColumns = columns;
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setDisplayedColumns(String[])");
	}
    }
    
    /** return true if horizontal scroll is enabled
     *	@return a boolean
     */
    public boolean isHorizontalScrollEnabled()
    {
	return this.horizontalScrollEnabled;
    }
    
    /** indicate if horizontal scroll is enabled
     *	@param enabled a boolean
     */
    public void setHorizontalScrollEnabled(boolean enabled)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setHorizontalScrollEnabled(boolean)");
	    logger.debug("calling setHorizontalScrollEnabled(boolean) with " + enabled);
	}
	if ( enabled != this.isHorizontalScrollEnabled() )
	{
	    this.horizontalScrollEnabled = enabled;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing 'horizontal scroll enabled' from " + (!enabled) + " to " + enabled);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setHorizontalScrollEnabled(boolean)");
	}
    }
    
    /** return true if the row number is displayed
     *	@return a boolean
     */
    public boolean isRowNumberDisplayed()
    {	
	return this.rowNumberDisplayed;
    }
    
    /** indicate if the row number is displayed
     *	@param displayed a boolean
     */
    public void setRowNumberDisplayed(boolean displayed)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setRowNumberDisplayed(boolean)");
	    logger.debug("calling setRowNumberDisplayed(boolean) with " + displayed);
	}
	if ( displayed != this.isRowNumberDisplayed() )
	{
	    this.rowNumberDisplayed = displayed;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing 'row number displayed' from " + (!displayed) + " to " + displayed);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setRowNumberDisplayed(boolean)");
	}
    }
    
    /** return the page size to use
     *	@return an integer representing the page size
     */
    public int getPageSize()
    {
	return this.pageSize;
    }
    
    /** initialize the page size to use
     *	@param size an integer representing the page size
     */
    public void setPageSize(int size)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setPageSize(int)");
	    logger.debug("calling setPageSize(int) with " + size);
	}
	if ( size != this.getPageSize() )
	{
	    int old = this.getPageSize();
	    this.pageSize = size;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing 'page size' from " + old + " to " + this.getPageSize());
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setPageSize(int)");
	}
    }

    /** return true if the horizontal lines are shown
     *	@return a boolean
     */
    public boolean areHorizontalLinesShown()
    {	return horizontalLines; }

    /** indicate if the horizontal lines are shown
     *	@param enabled a boolean
     */
    public void setHorizontalLinesShown(boolean enabled)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setHorizontalLinesShown(boolean)");
	    logger.debug("calling setHorizontalLinesShown(boolean) with " + enabled);
	}
	if ( enabled != this.areHorizontalLinesShown() )
	{
	    this.horizontalLines = enabled;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing 'horizontal lines shown' from " + (!enabled) + " to " + enabled);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setHorizontalLinesShown(boolean)");
	}
    }

    /** return true if the vertical lines are shwon
     *	@return a boolean
     */
    public boolean areVerticalLinesShown()
    {	return verticalLines; }

    /** indicate if the vertical grid is enabled
     *	@param enabled a boolean
     */
    public void setVerticalLinesShown(boolean enabled)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setVerticalLinesShown(boolean)");
	    logger.debug("calling setVerticalLinesShown(boolean) with " + enabled);
	}
	if ( enabled != this.areVerticalLinesShown() )
	{
	    this.verticalLines = enabled;
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing 'vertical lines shown' from " + (!enabled) + " to " + enabled);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setVerticalLinesShown(boolean)");
	}
    }
    
    /** ask the configuration to be saved at the given location
     *	@param location the location where the configuration has to be saved
     */
    void save(String location) throws IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering save(String)");
	    logger.debug("calling save(String) with " + location);
	}
	
	Properties properties = new Properties();
	
	StringBuffer buffer = new StringBuffer(100);
	if ( this.displayedColumns != null )
	{
	    for(int i = 0; i < this.displayedColumns.length; i++)
	    {
		String current = this.displayedColumns[i];
		if ( current != null )
		{
		    buffer.append(current);
		    
		    if ( i < this.displayedColumns.length - 1 )
		    {
			buffer.append(",");
		    }
		}
	    }
	}
	
	properties.setProperty(PROPERTY_COLUMNS, buffer.toString());
	properties.setProperty(PROPERTY_PAGE_SIZE, Integer.toString(this.getPageSize()));
	properties.setProperty(PROPERTY_ROW_NUMBER_DISPLAYED, Boolean.toString(this.isRowNumberDisplayed()));
	properties.setProperty(PROPERTY_HORIZONTAL_SCROLL_ENABLED, Boolean.toString(this.isHorizontalScrollEnabled()));
	
	properties.setProperty(PROPERTY_HORIZONTAL_GRID, Boolean.toString(this.areHorizontalLinesShown()));
	properties.setProperty(PROPERTY_VERTICAL_GRID, Boolean.toString(this.areVerticalLinesShown()));
	
	Iterator<Map.Entry<String, Integer>> keys = this.columnsPreferredSize.entrySet().iterator();
	while(keys.hasNext())
	{
	    Map.Entry<String, Integer> currentEntry = keys.next();
	    
	    if (currentEntry.getValue() != null )
	    {
		properties.setProperty(PROPERTY_PREF_COLUMN_SIZE_PREFIX + currentEntry.getKey(), currentEntry.getValue().toString());
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("displaying contents of properties :");
	    Enumeration en = properties.propertyNames();
	    while(en.hasMoreElements())
	    {
		Object currentKey = en.nextElement();
		logger.debug("\t" + currentKey + " --> " + properties.get(currentKey));
	    }
	}
	
	/** ensure that the file exists */
	File f = new File(location);
	
	if ( ! f.exists() )
	{
	    File parent = f.getParentFile();
	    
	    if ( ! parent.exists() )
	    {
		parent.mkdirs();
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("creating directory " + parent);
		}
	    }
	    
	    f.createNewFile();
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("file " + f + " created");
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("before storing properties to " + f);
	}
	properties.store(new FileOutputStream(f), null);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("after storing properties to " + f);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting save(String)");
	}
    }
    
    /** create a new TableConfiguration according to the given file
     *	@return a TableConfiguration or null if the file does not exist
     */
    static TableConfiguration loadConfiguration(String location) throws IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering loadConfiguration(String)");
	    logger.debug("calling loadConfiguration(String) with " + location);
	}
	
	TableConfiguration config = null;
	
	if ( location != null )
	{
	    Properties props = new Properties();
	    
	    File f = new File(location);
	    if ( f.exists() )
	    {
		props.load(new FileInputStream(f));
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("properties loaded : ");
		    
		    Enumeration en = props.propertyNames();
		    
		    while(en.hasMoreElements())
		    {
			Object current = en.nextElement();
			logger.debug("\t" + current + " --> " + props.get(current));
		    }
		}
		
		String property = null;
		
		String[] columns          = null;
		boolean  horScrollEnabled = false;
		boolean  rowDisplayed     = true;
		int      pagesize         = -1;
		boolean  horizontalGrid   = false;
		boolean  verticalGrid     = false;
		
		property = props.getProperty(PROPERTY_COLUMNS);
		if ( property != null )
		{
		    StringTokenizer tokenizer = new StringTokenizer(property, ",");
		    
		    int tokenCount = tokenizer.countTokens();
		    if ( tokenCount > 0 )
		    {
			columns = new String[tokenCount];
			int index = 0;
			while(tokenizer.hasMoreTokens())
			{
			    columns[index++] = tokenizer.nextToken();
			}
		    }
		}
		
		property = props.getProperty(PROPERTY_ROW_NUMBER_DISPLAYED);
		if ( property != null )
		{
		    rowDisplayed = Boolean.parseBoolean(property);
		}
		
		property = props.getProperty(PROPERTY_PAGE_SIZE);
		if ( property != null )
		{
		    pagesize = Integer.parseInt(property);
		}
		
		property = props.getProperty(PROPERTY_HORIZONTAL_SCROLL_ENABLED);
		if ( property != null )
		{
		    horScrollEnabled = Boolean.parseBoolean(property);
		}
		
		property = props.getProperty(PROPERTY_HORIZONTAL_GRID);
		if ( property != null )
		{
		    horizontalGrid = Boolean.parseBoolean(property);
		}
		
		property = props.getProperty(PROPERTY_VERTICAL_GRID);
		if ( property != null )
		{
		    verticalGrid = Boolean.parseBoolean(property);
		}
		
		config = new TableConfiguration(columns, horScrollEnabled, rowDisplayed, pagesize, horizontalGrid, verticalGrid);
		
		Enumeration en = props.propertyNames();
		if ( en != null )
		{
		    while(en.hasMoreElements())
		    {
			Object current = en.nextElement();
			
			if ( current instanceof String && ((String)current).startsWith(PROPERTY_PREF_COLUMN_SIZE_PREFIX) )
			{
			    String  suffix = ((String)current).substring(PROPERTY_PREF_COLUMN_SIZE_PREFIX.length());
			    Integer value  = null;
			    
			    try
			    {
				value = Integer.parseInt(props.getProperty( (String)current ));
				config.setColumnPreferredSizeForProperty(suffix, value);
			    }
			    catch(NumberFormatException e)
			    {	
				logger.error("unable to parse " + props.getProperty( (String)current ) + " as an integer");
			    }
			}
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("loadConfiguration(String) returns " + config);
	    logger.debug("exiting loadConfiguration(String)");
	}
	
	return config;
    }

    public String toString()
    {
	StringBuffer buffer = new StringBuffer();
	
	buffer.append("table configuration (horizontal lines displayed=" + this.areHorizontalLinesShown() + ", " +
					   "vertical lines displayed=" + this.areVerticalLinesShown() + ", " +
					   "page size=" + this.getPageSize() + ", " +
					   "row number displayed=" + this.isRowNumberDisplayed() + ", " +
					   "horizontal scroll enabled=" + this.isHorizontalScrollEnabled() + ", ");
	buffer.append("displayed columns={");
	
	if ( this.displayedColumns != null )
	{
	    for(int i = 0; i < this.displayedColumns.length; i++)
	    {
		buffer.append(this.displayedColumns[i]);
		
		if ( i < this.displayedColumns.length - 1 )
		{
		    buffer.append(";");
		}
	    }
	}
	buffer.append("}, ");
	
	buffer.append("columns preferred size={");
	
	if ( this.columnsPreferredSize != null )
	{
	    Iterator<Map.Entry<String, Integer>> it = this.columnsPreferredSize.entrySet().iterator();
	    while(it.hasNext())
	    {
		Map.Entry<String, Integer> entry = it.next();
		
		buffer.append(entry.getKey() + "-->" + entry.getValue());
		
		if ( it.hasNext() )
		{
		    buffer.append(";");
		}
	    }
	}
	buffer.append("})");
	
	return buffer.toString();
    }
    
}
