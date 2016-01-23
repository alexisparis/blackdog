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
package org.siberia.ui.swing.table.model;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import org.apache.log4j.Logger;
import org.siberia.type.SibList;
import org.siberia.SiberiaIntrospector;
import org.siberia.TypeInformationProvider;
import org.siberia.type.SibType;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.ui.swing.error.ErrorEvent;

/**
 *
 * @author alexis
 */
public class IntrospectionSibTypeListTableModel<T extends SibType> extends SibTypeListTableModel<T> implements PropertyBasedTableModel
{
    /** maximum reinit size of data structure */
    private static final int DATA_REINIT_MAX_SIZE = 100;
    
    /** logger */
    private static Logger logger = Logger.getLogger(IntrospectionSibTypeListTableModel.class);
    
    /** map linking class and related bean info */
    private Map<Class, BeanInfo>               beanInfoMap          = new HashMap<Class, BeanInfo>();
    
    /** vector that contains the values */
    private Vector<WeakReference<Row>>         data                 = new Vector<WeakReference<Row>>();
    
    /** soft reference on the introspector */
    private SoftReference<SiberiaIntrospector> introspectorRef      = new SoftReference<SiberiaIntrospector>(null);
    
    /** object to synchronize when using properties */
    private Object                             propertiesLock       = new Object();
    
    /** map linking property name and their ColumnDescriptor */
    private Map<String, ColumnDescriptor>      columnDescs          = new HashMap<String, ColumnDescriptor>(20);
    
    /** map linking property name to custom column name */
    private Map<String, String>                customColumnNames    = null;
    
    /** a custom allowed class could be provided
     *	this override the allowed class taken from the list if not null
     *	and avoid a gigantic search on all SibType if the SibList get allowed class to SibType but
     *	only contains SibString!!
     *	so, this attribute only gives optimization
     */
    private Class                              specificAllowedClass  = null;
    
    /** list of PropertyDeclaration */
    private List<PropertyDeclaration>          propertyDeclarations  = new ArrayList<PropertyDeclaration>(20);
    
    /** array representing the name of the properties */
    private String[]                           propertiesName        = null;
    
    /** property change listener on PropertyDeclaration */
    private PropertyChangeListener             declarationListener   = null;
    
    /** in set Properties */
    private boolean                            inSetProperties       = false;
    
    /** event listener list */
    private EventListenerList                  eventListenerList     = new EventListenerList();
    
    /** Creates a new instance of SibTypeListTableModel */
    public IntrospectionSibTypeListTableModel()
    {   super();
	
	this.declarationListener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		if ( ! inSetProperties )
		{
		    updateArrayProperties();
		}
		
		if ( evt.getSource() instanceof PropertyDeclaration )
		{
		    
		    fireDeclarationChanged( (PropertyDeclaration)evt.getSource(), evt );
		}
	    }
	};
    }
    
    /** return the specific allowed class
     *	@return a Class
     */
    public Class getSpecificAllowedClass()
    {
	return this.specificAllowedClass;
    }
    
    /** return the specific allowed class
     *	this override the allowed class taken from the list if not null
     *	and avoid a gigantic search on all SibType if the SibList get allowed class to SibType but
     *	only contains SibString!!
     *	so, this attribute only gives optimization
     *	@return a Class
     */
    public void setSpecificAllowedClass(Class allowedClass)
    {
	if ( allowedClass != this.getSpecificAllowedClass() )
	{
	    this.specificAllowedClass = allowedClass;
	    
	    /** force to recompute all column descriptors */
	    this.columnDescs = null;
	}
    }
    
    /* ######################################################################### 
     * ##################### Property managment methods ########################
     * ######################################################################### */
    
    /** method that returns the position in the model of the given object
     *	@param property the property managed or not by the model
     *	@return the column index of the property in the model of the given object or -1 if not found
     */
    public int getColumnIndexOfProperty(String property)
    {
	int result = -1;
	
	if ( property != null && this.propertiesName != null )
	{
	    for(int i = 0; i < this.propertiesName.length; i++)
	    {
		String currentProperty = this.propertiesName[i];
		
		if ( property.equals(currentProperty) )
		{   result = i;
		    break;
		}
	    }
	}
	
	return result;
    }
    
	/* #########################################################################
	 * ##################### PropertyDeclaration methods #######################
	 * ######################################################################### */
    
    /** set the properties for this model
     *	@param properties an array of property
     */
    public void setProperties(String... properties)
    {
	this.clearPropertyDeclaration();
	
	if ( properties != null )
	{
	    this.inSetProperties = true;
	    
	    boolean updateArray = false;
	    
	    for(int i = 0; i < properties.length; i++)
	    {
		String currentProperty = properties[i];
		
		if ( currentProperty != null )
		{
		    this.addPropertyDeclarations(new PropertyDeclaration(currentProperty));
		    
		    if ( ! updateArray )
		    {
			updateArray = true;
		    }
		}
	    }
	    
	    this.inSetProperties = false;
	    
	    if ( updateArray )
	    {
		this.updateArrayProperties();
	    }
	}
    }
    
    /** add property declarations
     *	@param declarations an array of PropertyDeclaration
     */
    public void addPropertyDeclarations(PropertyDeclaration... declarations)
    {
	if ( declarations != null && declarations.length > 0 )
	{
	    synchronized(this.propertyDeclarations)
	    {
		boolean reinitColumnDescriptors = false;
		for(int i = 0; i < declarations.length; i++)
		{
		    PropertyDeclaration current = declarations[i];

		    if ( current != null )
		    {
			current.addPropertyChangeListener(this.declarationListener);
			
			if ( ! reinitColumnDescriptors )
			{
			    reinitColumnDescriptors = true;
			}
			this.propertyDeclarations.add(current);
		    }
		}
		
		if ( reinitColumnDescriptors && ! this.inSetProperties )
		{
		    this.updateArrayProperties();
		}
	    }
	}
    }
    
    /** insert a property declaration
     *	@param index index where to insert the given PropertyDeclaration
     *	@param declaration a PropertyDeclaration
     */
    public void insertPropertyDeclaration(int index, PropertyDeclaration declaration)
    {
	if ( declaration != null )
	{
	    synchronized(this.propertyDeclarations)
	    {
		int newIndex = index;
		if ( newIndex > this.propertyDeclarations.size() )
		{
		    newIndex = this.propertyDeclarations.size();
		}
		else if ( newIndex < 0 )
		{
		    newIndex = 0;
		}
		
		declaration.addPropertyChangeListener(this.declarationListener);
		
		this.propertyDeclarations.add(index, declaration);
		
		if ( ! this.inSetProperties )
		{
		    this.updateArrayProperties();
		}
	    }
	}
    }
    
    /** remove a property declaration by its property name
     *	@param propertyName the name of the property to remove
     *	@return true if the PropertyDeclaration was removed
     */
    public boolean removePropertyDeclaration(String propertyName)
    {
	boolean result = false;
	
	synchronized(this.propertyDeclarations)
	{
	    for(int i = 0; i < this.propertyDeclarations.size(); i++)
	    {
		PropertyDeclaration current = this.propertyDeclarations.get(i);
		
		if ( current.getPropertyName().equals(propertyName) )
		{
		    result = this.propertyDeclarations.remove(current);
		    
		    current.removePropertyChangeListener(this.declarationListener);
		    
		    break;
		}
	    }
	}
		
	if ( result )
	{
	    this.updateArrayProperties();
	}
	
	return result;
    }
    
    /** clear all property declarations */
    public void clearPropertyDeclaration()
    {
	synchronized(this.propertyDeclarations)
	{
	    if ( this.propertyDeclarations.size() > 0 )
	    {
		Iterator<PropertyDeclaration> it = this.propertyDeclarations.iterator();
		while(it.hasNext())
		{
		    PropertyDeclaration current = it.next();
		    
		    if ( current != null )
		    {
			current.removePropertyChangeListener(PropertyDeclaration.PROPERTY_ENABLED, this.declarationListener);
		    }
		}
		
		this.propertyDeclarations.clear();
		
		this.updateArrayProperties();
	    }
	}
    }
    
    /** return the number of PropertyDeclaration
     *	@return an integer
     */
    public int getPropertyDeclarationCount()
    {
	return this.propertyDeclarations.size();
    }
    
    /** return the PropertyDeclaration at the given index
     *	@param index
     *	@return a PropertyDeclaration
     */
    public PropertyDeclaration getPropertyDeclaration(int index)
    {
	PropertyDeclaration declaration = null;
	
	synchronized(this.propertyDeclarations)
	{
	    declaration = this.propertyDeclarations.get(index);
	}
	
	return declaration;
    }
    
    /** return the name of the property linked to the given PropertyDeclaration
     *	@param declaration a PropertyDeclaration
     *	@return the name to use for the property declaration
     */
    public String getPropertyDisplayNameFor(PropertyDeclaration declaration)
    {
	String result = null;
	
	if ( declaration != null )
	{
	    result = this.getColumnNameForProperty(declaration.getPropertyName());
	}
	
	return result;
    }
    
    /** indicate the name of the properties enabled<br>
     *	other properties will be disabled
     *	@param properties an array of String representing the name of the properties which has to be enabled
     */
    public void enableProperties(String... properties)
    {
	if ( this.propertyDeclarations != null )
	{
	    this.inSetProperties = true;
	    
	    for(int i = 0; i < this.propertyDeclarations.size(); i++)
	    {
		PropertyDeclaration decl = this.propertyDeclarations.get(i);
		
		boolean enabled = false;
		
		if ( properties != null )
		{
		    for(int j = 0; j < properties.length; j++)
		    {
			String currentProperty = properties[j];
			
			if ( currentProperty != null && currentProperty.equals(decl.getPropertyName()) )
			{
			    enabled = true;
			    break;
			}
		    }
		}
		
		decl.setEnabled(enabled);
		
	    }
	    
	    this.inSetProperties = false;
	    
	    this.updateArrayProperties();
	}
    }
    
    /** add a PropertyBasedTableModelListener
     *	@param listener a PropertyBasedTableModelListener
     */
    public void addPropertyBasedTableModelListener(PropertyBasedTableModelListener listener)
    {
	if ( listener != null )
	{
	    this.eventListenerList.add(PropertyBasedTableModelListener.class, listener);
	}
    }
    
    /** remove a PropertyBasedTableModelListener
     *	@param listener a PropertyBasedTableModelListener
     */
    public void removePropertyBasedTableModelListener(PropertyBasedTableModelListener listener)
    {
	if ( listener != null )
	{
	    this.eventListenerList.remove(PropertyBasedTableModelListener.class, listener);
	}
    }
    
    /** indicate to all PropertyBasedTableModelListener that a descriptor changed
     *	
     *	@param descriptor a PropertyDeclaration
     *	@param event the event describing the modification
     */
    protected void fireDeclarationChanged(PropertyDeclaration descriptor, PropertyChangeEvent event)
    {
	PropertyBasedTableModelListener[] listeners = (PropertyBasedTableModelListener[])this.eventListenerList.getListeners(PropertyBasedTableModelListener.class);
	
	if ( listeners != null && listeners.length > 0 )
	{
	    for(int i = 0; i < listeners.length; i++)
	    {
		PropertyBasedTableModelListener current = listeners[i];
		
		if ( current != null )
		{
		    current.declarationChanged(this, descriptor, event);
		}
	    }
	}
    }
    
    /** methods that allow to update the array of Properties according to property declarations */
    private void updateArrayProperties()
    {
	synchronized(this.propertyDeclarations)
	{
	    List<String> selectedProperties = new ArrayList<String>(this.propertyDeclarations.size());
	    
	    Iterator<PropertyDeclaration> it = this.propertyDeclarations.iterator();
	    
	    while(it.hasNext())
	    {
		PropertyDeclaration decl = it.next();
		
		if ( decl != null && decl.isEnabled() )
		{
		    selectedProperties.add(decl.getPropertyName());
		}
	    }
	    
	    this.propertiesName = null;
	    
	    if ( selectedProperties.size() > 0 )
	    {
		this.propertiesName = (String[])selectedProperties.toArray(new String[selectedProperties.size()]);
	    }
	    
	    /* clear cache */
	    // TODO : only clear the data of the column that were deleted
	    this.clearCache();
	    
	    this.updateColumnDescriptors(this.propertiesName);
	    
	    this.fireTableStructureChanged();
	}
    }
    
    /* #########################################################################
     * ####################### custom names methods ############################
     * ######################################################################### */
    
    /** set the specific name of the column related to the given property
     *  @param propertyName the name of a property
     *  @param columnName the column name to apply
     */
    public void setCustomColumnName(String propertyName, String columnName)
    {   if ( propertyName == null )
            throw new IllegalArgumentException("propertyName could not be null");
        boolean changeApplied = false;
        if ( columnName != null )
        {   if ( this.customColumnNames == null )
                this.customColumnNames = new HashMap<String, String>();
            this.customColumnNames.put(propertyName, columnName);
            changeApplied = true;
        }
        else
        {   if ( this.customColumnNames != null )
            {   this.customColumnNames.remove(propertyName);
                changeApplied = true;
            }
        }
        
        if ( changeApplied )
        {
            /** find the column that has changed */
            int index = -1;
            
            synchronized(this.propertiesLock)
            {   if ( this.propertiesName != null )
                {   for(int i = 0; i < this.propertiesName.length; i++)
                    {   if ( propertyName.equals(this.propertiesName[i]) )
                        {   index = i;
                            break;
                        }
                    }
                }
            }
            
            if ( index != -1 )
            {   this.fireTableChanged(new TableModelEvent(this, -1, -1, index, TableModelEvent.HEADER_ROW)); }
        }
        
    }
    
    /** remove the custom name of the column related with the given property
     *  @param propertyName the name of a property
     */
    public void resetCustomColumnName(String propertyName)
    {   this.setCustomColumnName(propertyName, null); }
    
    /* #########################################################################
     * ######################### specific methods ##############################
     * ######################################################################### */
    
    /** return the setter method for the given property name
     *  @param srcObj an object
     *  @param propertyName the name of a property
     */
    private Method getSetterMethodForColumn(Object srcObj, String propertyName)
    {   Method m = null;
        
        if ( srcObj != null && propertyName != null )
        {   BeanInfo info = this.getBeanInfoFor(srcObj);
            if ( info != null )
            {   PropertyDescriptor[] descs = info.getPropertyDescriptors();
                
                for(int i = 0; i < descs.length; i++)
                {   PropertyDescriptor current = descs[i];
                    
                    if ( current != null && propertyName.equals(current.getName()) )
                    {   m = current.getWriteMethod(); }
                }
            }
        }
        
        return m;
    }
    
    /** return the setter method for the property name at the given column index
     *  @param srcObj an object
     *  @param columnIndex the index of a column
     */
    private Method getSetterMethodForColumn(Object srcObj, int columnIndex)
    {   return this.getSetterMethodForColumn(srcObj, this.propertiesName[columnIndex]); }
    
    /** get the ColumnDescriptor related to the given property
     *	@param propertyName the name of the property
     *	@return a ColumnDescriptor
     */
    private ColumnDescriptor getColumnDescriptor(String propertyName)
    {
	ColumnDescriptor column = null;
	
	
	if ( this.columnDescs != null )
	{
	    column = this.columnDescs.get(propertyName);
	}
	
	if ( column == null )
	{
	    this.updateColumnDescriptors(propertyName);
	    
	    column = this.columnDescs.get(propertyName);
	}
	
	return column;
    }
    
    /** update the ColumnDescriptor
     *	@param propertyNames the name of the properties
     */
    private synchronized void updateColumnDescriptors(String... propertyNames)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling updateColumnDescriptor(...)");
	}
	
	if ( this.columnDescs == null )
	{
	    this.columnDescs = new HashMap<String, ColumnDescriptor>(20);
	}
	
	if ( propertyNames != null && propertyNames.length > 0 )
	{
	    Class allowedClass = this.getSpecificAllowedClass();
	    
	    if ( allowedClass == null )
	    {
		allowedClass = (this.getList() == null ? null : this.getList().getAllowedClass());
	    }
	    
	    /* get the list of Class to consider during introspection process */
	    List<Class> subClasses = null;

	    if ( allowedClass == null )
	    {   subClasses = TypeInformationProvider.getInstance().getSubClassFor(SibType.class, false, true, true, null); }
	    else if ( allowedClass.isInterface() )
	    {   subClasses = TypeInformationProvider.getInstance().getSubClassFor(SibType.class, false, true, true, allowedClass); }
	    else
	    {   subClasses = TypeInformationProvider.getInstance().getSubClassFor(allowedClass, true, true, true, null); }

	    if ( subClasses == null )
	    {   subClasses = Collections.emptyList(); }
	    
	    if ( subClasses.size() > 0 )
	    {
		for(int i = 0; i < propertyNames.length; i++)
		{
		    String currentPropertyName = propertyNames[i];
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("searching for the property descriptor related to : " + currentPropertyName);
		    }
		    /** get Property descriptor at given index */
		    String nameFromProperty = null;
		    Class  columnClass      = null;

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("found " + subClasses.size() + " subclasses of " + allowedClass);
		    }

		    for(int j = 0; j < subClasses.size(); j++)
		    {   Class currentClass = subClasses.get(j);
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("search in class " + currentClass);
			}

			BeanInfo info = this.getBeanInfoFor(currentClass);
			PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();

			for(int k = 0; k < propertyDescriptors.length; k++)
			{   PropertyDescriptor currentDesc = propertyDescriptors[k];


			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("property for beaninfo : " + info.getClass() + " for class : " + currentClass);
				logger.debug((currentDesc == null ? null : currentDesc.getName()) + " " + currentPropertyName);
			    }
			    if ( currentDesc != null && currentDesc.getName().equals(currentPropertyName) )
			    {   
				nameFromProperty = currentDesc.getDisplayName();
				columnClass      = currentDesc.getPropertyType();
				break;
			    }
			}

			if ( nameFromProperty != null )
			{   break; }
		    }

		    if ( nameFromProperty == null )
		    {   nameFromProperty = this.propertiesName[i]; }

		    ColumnDescriptor cnDesc = new ColumnDescriptor(currentPropertyName);
		    
		    this.columnDescs.put(currentPropertyName, cnDesc);

		    cnDesc.setColumnName(nameFromProperty);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("setting columnClass with " + columnClass + " and columnName with " + nameFromProperty);
		    }

		    if ( columnClass == null )
		    {
			logger.error("unable to retrieve type for property '" + currentPropertyName + "'");
		    }

		    cnDesc.setColumnClass(columnClass);
		}
	    }
        }
    }
    
    /** return the row for the given index
     *  @param index the index of the row
     *  @return a Row or null if failed
     */
    protected Row getRowAt(int index)
    {   Row result = null;
        
        if ( index >= 0 )
        {
            if ( index < this.getRowCount() )
            {
                /** try to find the row in data */
                if ( index < this.data.size() )
                {   WeakReference<Row> ref = this.data.get(index);
                    if ( ref != null )
                    {   result = ref.get(); }
                }
                
                /* if result is null, then create a new Row and feed data vector */
                if ( result == null )
                {   result = new Row();
		    if ( logger.isDebugEnabled() )
		    {	logger.debug("updating row at " + index + " with item : " + this.getItem(index).getClass()); }
                    this.updateRow(this.getItem(index), result);
                    
                    /** feed the vector */
                    int currentVectorSize = this.data.size();
                    if ( index >= currentVectorSize )
                    {   for(int i = currentVectorSize; i < index; i++)
                        {   this.data.add(new WeakReference<Row>(null)); }
                        
                        this.data.add(new WeakReference<Row>(result));
                    }
                    else
                    {   this.data.setElementAt(new WeakReference<Row>(result), index); }
                }
            }
            else
            {   throw new IllegalArgumentException("index must be lower than " + this.getRowCount()); }
        }
        else
        {   throw new IllegalArgumentException("index must be greater or equals to 0"); }
        
        return result;
    }
    
    /** return the BeanInfo to use for a given Object
     *  @param c a Class
     *  @return a BeanInfo or null if not found
     */
    private BeanInfo getBeanInfoFor(Class c)
    {   BeanInfo bi = null;
        
        if ( c != null )
        {   bi = this.beanInfoMap.get(c);
            
            if ( bi == null )
            {   
                SiberiaIntrospector introspector = this.introspectorRef.get();
                if ( introspector == null )
                {   introspector = new org.siberia.SiberiaIntrospector();
                    this.introspectorRef = new SoftReference<SiberiaIntrospector>(introspector);
                }
		if ( logger.isDebugEnabled() )
		{   logger.debug("beaninfo for class " + c); }
		
		/** the instrospector have to see all properties, even those which are related to initial configuration
		 *  if the properties contains such a property
		 *  so, do not use BeanInfoCategory.BASICS but BeanInfoCategory.ALL
		 */
                bi = introspector.getBeanInfo(c, BeanInfoCategory.ALL);
		if ( logger.isDebugEnabled() )
		{   logger.debug("   is " + bi); }
                
                this.beanInfoMap.put(c, bi);
            }
        }
        
        return bi;
    }
    
    /** return the BeanInfo to use for a given Object
     *  @param obj an Object
     *  @return a BeanInfo or null if not found
     */
    private BeanInfo getBeanInfoFor(Object obj)
    {   return (obj == null ? null : this.getBeanInfoFor(obj.getClass())); }
    
    /** method that update a given row according to a source object
     *  @param srcObj the source object
     *  @param row the Row to update
     */
    private void updateRow(Object srcObj, Row row)
    {   if ( srcObj == null )
            throw new IllegalArgumentException("cannot update with a source object = null");
        if ( row == null )
            throw new IllegalArgumentException("cannot update with a null row");
        
        synchronized(this.propertiesLock)
        {   if ( this.propertiesName != null )
            {   for(int i = 0; i < this.propertiesName.length; i++)
                {   String currentProperty = this.propertiesName[i];
                    
                    BeanInfo bInfo = this.getBeanInfoFor(srcObj);
                    
                    row.update(srcObj, currentProperty, i, bInfo);
                }
            }
        }
    }
    
    /** method that update a given row according to a source object
     *  @param srcObj the source object
     *  @param columnIndex the column index
     */
    private void updateRow(Object srcObj, int columnIndex)
    {   if ( srcObj != null )
        {   if ( columnIndex < 0 || columnIndex > this.getColumnCount() )
            {   throw new IllegalArgumentException("illegal columnIndex"); }
            
            /** get the row for given object */
            int rowIndex = this.getIndexOfItem(srcObj);
            if ( rowIndex >= 0 && rowIndex < this.getRowCount() )
            {   Row row = this.getRowAt(rowIndex);
                
                if ( row != null )
                {   row.update(srcObj, this.propertiesName[columnIndex], columnIndex, this.getBeanInfoFor(srcObj)); }
            }
        }
    }
    
    /* #########################################################################
     * ####################### cache managment methods #########################
     * ######################################################################### */
    
    /** reinitialize data */
    private void reinitializeData()
    {   /** clear vector and ensure capacity */
        this.data.ensureCapacity(Math.min(Math.max(this.data.size(), (this.getList() == null ? 0 : this.getList().size())), DATA_REINIT_MAX_SIZE));
        this.data.clear();
    }
    
    /** remove the cache about the item at the given position
     *	@param index
     */
    public void clearCacheAt(int index)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("clearing cache at " + index);
	}
	
	if ( this.data != null )
        {   if ( index >= 0 && index <= this.data.size() )
            {   this.data.remove(index);  }
        }
    }
    
    /** ensure that the cache contains an item for the given index
     *	@param index
     */
    public void ensureCacheExistenceAt(int index)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("ensure existence at " + index);
	}
	
	if ( this.data != null )
        {   if ( index >= 0 && index <= this.data.size() )
            {   this.data.insertElementAt(new WeakReference<Row>(null), index); }
        }
    }
    
    /** clear cache */
    public void clearCache()
    {
	if ( this.data != null )
	{
	    this.data.clear();
	}
    }
    
    /* #########################################################################
     * ####################### List managment methods ##########################
     * ######################################################################### */
    
    /** set the SibList displayed managed by this model
     *  @param list a sibList
     */
    @Override
    public synchronized void setList(SibList list)
    {   if ( list != this.getList() )
        {   
            this.reinitializeData();
            
            super.setList(list);
        }
    }
    
    /** method called when a change is detected on an item contained by the list
     *  this method is always called in the EDT
     *  @param item the object that was modified
     *  @param index the index of the item in the list
     *  @param propertyName the name of the property
     *  @param oldValue the old value of the property
     *  @param newValue the new value of the property
     */
    protected void propertyChangedOnItem(SibType item, int index, String propertyName, Object oldValue, Object newValue)
    {
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("property changed on item at index=" + index + ", property name=" + propertyName + ", old value=" + oldValue + " --> new value=" + newValue);
	}
	
//	new Exception("propertyChangedOnItem(" + index + ", " + propertyName + ", " + oldValue + " --> " + newValue + ")").printStackTrace();
        if ( this.propertiesName != null && propertyName != null )
        {
            /** find the index of the column related to propertyName */
            int columnIndex = this.getColumnIndexOfProperty(propertyName);
	    
            if ( columnIndex >= 0 )
            {   
                Row row = null;
		
		if ( index >= 0 && index < this.getRowCount() )
		{
		    row = this.getRowAt(index);
		}
		
                if ( row != null )
                {   row.update(item, propertyName, columnIndex, this.getBeanInfoFor(item)); }
                
                final int indexTmp    = index;
                final int colIndexTmp = columnIndex;
                
                this.fireTableCellUpdated(indexTmp, colIndexTmp);
            }
        }
    }
    
    /** method called when an item has been inserted in the list
     *  this method is always called in the EDT
     *  @param startIndex the index of the first item removed from the list
     *  @param endIndex the index of the last item removed from the list
     */
    @Override
    public void itemsAddedInList(int startIndex, int endIndex)
    {   
        super.itemsAddedInList(startIndex, endIndex);
	
	if ( this.data != null )
        {   
	    int underLimit = Math.max(startIndex, 0);
	    int maxLimit   = Math.min(endIndex, this.data.size());
	    for(int i = underLimit; i <= maxLimit; i++ )
	    {
		this.data.insertElementAt(new WeakReference<Row>(null), i);
	    }
//	    if ( index >= 0 && index <= this.data.size() )
//            {   this.data.insertElementAt(new WeakReference<Row>(null), index); }
        }
    }
    
    /** method called when an item has been removed from the list
     *  this method is always called in the EDT
     *  @param startIndex the index of the first item removed from the list
     *  @param endIndex the index of the last item removed from the list
     */
    @Override
    public void itemsRemovedFromList(int startIndex, int endIndex)
    {   
        super.itemsRemovedFromList(startIndex, endIndex);
	
	if ( this.data != null )
        {   
	    int underLimit = Math.max(0, startIndex);
	    for(int i = Math.min(endIndex, this.data.size() - 1); i >= underLimit; i-- )
	    {
		this.data.remove(i);
	    }
//	    if ( index >= 0 && index < this.data.size() )
//            {   this.data.remove(index);  }
        }
    }
    
    /* #########################################################################
     * ########################### debug methods ###############################
     * ######################################################################### */
    
    /** method that allow to debug the content of the vector */
    private void printContent()
    {
        System.out.println("printContent");
        
        if ( this.data != null && this.getList() != null )
        {
            System.out.println("rowCount : " + this.getRowCount());
            System.out.println("list size : " + this.getList().size());
            
            for(int i = 0; i < this.getList().size(); i++)
            {   System.out.println("\t(" + i + ") --> " + ((SibType)this.getList().get(i)).getName()); }
            
            System.out.println("affichage du vecteur : ");
            for(int i = 0; i < this.data.size(); i++)
            {   WeakReference<Row> rowRef = this.data.get(i);
                
                System.out.println("\t(" + i + ") --> " + (rowRef == null ? "{ref null}" : rowRef.get()));
            }
        }
        
        System.out.println("printContent");
    }
    
    /* #########################################################################
     * ##################### SiberiaTableModel methods #########################
     * ######################################################################### */
    
    /** return true if the model that is contains data at the given location
     *	@param row the row index
     *	@param column the column index
     *	@return true if the model that is contains data at the given location
     */
    public boolean containsDataAt(int row, int column)
    {
	boolean containsData = false;
	
	/** if an IntrospectionError occured when trying to get the value in the desired row and column, then returns null
	 *  to indicate to the Table that the cell should not be render normally
	 *  else, return this.getcolumnClass(column)
	 */
	Row r = null;
	
	if ( row >= 0 && row < this.getRowCount() )
	{
	    r = this.getRowAt(row);
	}
	
	if ( r != null )
	{
	    containsData = ! r.isReflectingIntrospectionErrorAt(column);
	}
	
	return containsData;
    }
    
    /* #########################################################################
     * ######################## TableModel methods #############################
     * ######################################################################### */

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>.
     * 
     * @param rowIndex	the row whose value is to be queried
     * @param columnIndex 	the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex)
    {   
        Object result = null;
        
        Row r = null;
	
	if ( rowIndex >= 0 && rowIndex < this.getRowCount() )
	{
	    r = this.getRowAt(rowIndex);
	}
	
        if ( r != null )
        {   
	    result = r.getValueAt(columnIndex);
	}
        
        return result;
    }

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	columnIndex	the index of the column
     * @return  the name of the column
     */
    public String getColumnName(int columnIndex)
    {   
	String name = null;
            
        if ( this.propertiesName != null && columnIndex >= 0 && columnIndex < this.propertiesName.length )
        {   String propertyName = this.propertiesName[columnIndex];

	    name = this.getColumnNameForProperty(propertyName);
        }

        if ( name == null )
	{   name = "#"; }
        
        return name;
    }

    /**
     * Returns the name of the column at <code>columnIndex</code>.  This is used
     * to initialize the table's column header name.  Note: this name does
     * not need to be unique; two columns in a table can have the same name.
     *
     * @param	propertyName the name of a property
     * @return  the name of the column
     */
    public String getColumnNameForProperty(String propertyName)
    {   
	String name = null;
	
	if ( this.customColumnNames != null )
	{   name = this.customColumnNames.get(propertyName); }

	if ( name == null )
	{   
	    ColumnDescriptor descriptor = this.getColumnDescriptor(propertyName);
	    
	    if ( descriptor != null )
	    {   name = descriptor.getColumnName(); }
	}
        
        return name;
    }

    /**
     * Returns the most specific superclass for all the cell values 
     * in the column.  This is used by the <code>JTable</code> to set up a 
     * default renderer and editor for the column.
     *
     * @param columnIndex  the index of the column
     * @return the common ancestor class of the object values in the model.
     */
    public Class getColumnClass(int columnIndex)
    {   Class c = null;
        
	if ( columnIndex >= 0 && columnIndex < this.propertiesName.length )
	{
	    ColumnDescriptor descriptor = this.getColumnDescriptor(this.propertiesName[columnIndex]);
	    
            if ( descriptor != null )
            {   c = descriptor.getColumnClass(); }
        }

        if ( c == null )
        {   return Object.class; }
        
        return c;
    }

    /**
     * Returns true if the cell at <code>rowIndex</code> and
     * <code>columnIndex</code>
     * is editable.  Otherwise, <code>setValueAt</code> on the cell will not
     * change the value of that cell.
     *
     * @param	rowIndex	the row whose value to be queried
     * @param	columnIndex	the column whose value to be queried
     * @return	true if the cell is editable
     * @see #setValueAt
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {   
        boolean result = super.isCellEditable(rowIndex, columnIndex);
        
        if ( result )
        {
            /** if type is read only, then return false */
            T t = this.getItem(rowIndex);
            if ( t != null )
            {   if ( t instanceof SibType )
                {   if ( ((SibType)t).isReadOnly() )
                    {   result = false; }
                }
            }
            else
            {   result = false; }

            if ( result )
            {   
		Row r = null;
		
		if ( rowIndex >= 0 && rowIndex < this.getRowCount() )
		{
		    r = this.getRowAt(rowIndex);
		}
                
		if ( r == null )
		{
                    result = false;
		}
                else
		{
                    result = r.isCellEditable(columnIndex);
		}
            }
        }
        
        return result;
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     * 
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount()
    {   return (this.propertiesName == null ? 0 : this.propertiesName.length); }

    /**
     * Sets the value in the cell at <code>columnIndex</code> and
     * <code>rowIndex</code> to <code>aValue</code>.
     *
     * @param	aValue		 the new value
     * @param	rowIndex	 the row whose value is to be changed
     * @param	columnIndex 	 the column whose value is to be changed
     * @see #getValueAt
     * @see #isCellEditable
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
	/** change the value of the related property in the object related to row rowIndex ... */
        T obj = this.getItem(rowIndex);
        if ( obj != null )
        {   
            try
            {   T t = this.getItem(rowIndex);
                Method m = this.getSetterMethodForColumn(t, columnIndex);

                if ( m == null )
                {   
		    ResourceBundle rb = ResourceBundle.getBundle(IntrospectionSibTypeListTableModel.class.getName());
		    this.setValueAtError(rb.getString("cannotSetNewValueTitle"), rb.getString("noSetterMessage"),
					 rb.getString("noSetterDetailedMessage"), rb.getString("noSetterCategory"), null, Level.INFO);
		}
                else
                {   m.invoke(t, aValue);

		    /** ... and change cell value if cell exists */
		    if ( this.data != null )
		    {   if ( rowIndex >= 0 && rowIndex < this.data.size() )
			{   /* get the element at index rowIndex */
			    WeakReference<Row> rowRef = this.data.get(rowIndex);
			    Row row = rowRef.get();

			    if ( row != null )
			    {   if ( row.values != null )
				{   Cell cell = row.values.get(columnIndex);
				    if ( cell != null )
				    {   cell.setValue(aValue); }
				}
			    }
			}
		    }
                }
            }
            catch(Exception e)
            {   
		ResourceBundle rb = ResourceBundle.getBundle(IntrospectionSibTypeListTableModel.class.getName());
		this.setValueAtError(rb.getString("cannotSetNewValueTitle"), rb.getString("introspectionErrorMessage"),
				     rb.getString("introspectionErrorDetailedMessage"), rb.getString("introspectionErrorCategory"), e, Level.INFO);
            }
        }
    }
    
    /** called on a setValueAt when error occur when changing the value of an property by introspection
     *	@param title
     *	@param message
     *	@param detailedMessage
     *	@param category
     *	@param throwable
     *	@param level
     */
    private void setValueAtError(String title, String message, String detailedMessage, String category, Throwable throwable, Level level)
    {
	String titleN		= title;
	String messageN		= message;
	String detailedMessageN = detailedMessage;
	String categoryN	= category;
	
	if ( titleN != null && titleN.trim().length() == 0 )
	{
	    titleN = null;
	}
	if ( messageN != null && messageN.trim().length() == 0 )
	{
	    messageN = null;
	}
	if ( detailedMessageN != null && detailedMessageN.trim().length() == 0 )
	{
	    detailedMessageN = null;
	}
	if ( categoryN != null && categoryN.trim().length() == 0 )
	{
	    categoryN = null;
	}
	
	ErrorEvent evt = new ErrorEvent(this, titleN, messageN, detailedMessageN, categoryN, throwable, level);
	
	this.fireErrorHandlers(evt);
    }
    
    /** ########################################################################
     *  ############################### Inner classes ##########################
     *  ######################################################################## */
    
    /** class that describe the contains of a row */
    private class Row
    {   
        /** map of data */
        private Map<Integer, Cell> values = new HashMap<Integer, Cell>();
        
        /** create a new Row */
        public Row()
        {   }
        
        /** return the value at the given index
         *  @param index an integer
         */
        public Object getValueAt(int index)
        {   
	    Object value = null;
	    
	    Cell c = this.values.get(index);
            if ( c != null )
	    {
                value = c.getValue();
	    }
	    
	    return value;
        }
	
	/** return true if the cell at the given position reflect an introspection error
	 *  @param index the index of the cell
	 *  @return true if the cell at the given position reflect an introspection error
	 */
	public boolean isReflectingIntrospectionErrorAt(int index)
	{
	    boolean result = false;
	    
	    Cell c = this.values.get(index);
	    
	    if ( c == null )
	    {
//		result = true;
	    }
	    else
	    {
		result = ! c.hasData();
	    }
	    
	    return result;
	}
        
        /** return true if the cell at index is editable
         *  @param index an integer
         *  @return true if the cell is editable
         */
        public boolean isCellEditable(int index)
        {   
	    boolean result = false;
	    
	    Cell c = this.values.get(index);
            if ( c != null )
	    {
                result = c.isEditable();
	    }
	    
	    return result;
        }
        
        /** update the value with the given parameter
         *  @param srcObj the object on which introspection will be performed
         *  @param propertyName the name of the property
         *  @param index the index where the value will be stored
         *  @param beanInfo the BeanInfo that will allow introspection
         */
        public void update(Object srcObj, String propertyName, int index, BeanInfo beanInfo)
        {
            if ( srcObj != null && propertyName != null && beanInfo != null && index >= 0 )
            {   PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                
                PropertyDescriptor propertyDesc = null;
                if ( descriptors != null && descriptors.length > 0 )
                {   for(int i = 0; i < descriptors.length; i++)
                    {   PropertyDescriptor current = descriptors[i];
                        
                        if ( current != null )
                        {   if ( propertyName.equals(current.getName()) )
                            {   propertyDesc = current;
                                break;
                            }
                        }
                    }
                }
                
                if ( propertyDesc != null )
                {
                    /** introspection on srcObj */
                    if ( propertyDesc.getReadMethod() != null )
                    {   Cell cell = new Cell();
			
			Object result = null;
			
			try
			{
			    result = propertyDesc.getReadMethod().invoke(srcObj);
			    cell.setEditable(propertyDesc.getWriteMethod() != null);
			}
			catch (IllegalArgumentException ex)
			{
			    result = IntrospectionError.ILLEGAL_ARGUMENT_ERROR;
			}
			catch (InvocationTargetException ex)
			{
			    result = IntrospectionError.INVOCATION_TARGET_ERROR;
			}
			catch (IllegalAccessException ex)
			{
			    result = IntrospectionError.ILLEGAL_ACCESS_ERROR;
			}
			
			cell.setValue(result);
                        
                        this.values.put(index, cell);
                    }
                }
		else
		{
		    /** no property descriptor found --> it is an error */
		    Cell cell = new Cell();
		    
		    cell.setValue(IntrospectionError.NO_PROPERTY_DESCRIPTOR_ERROR);
		    this.values.put(index, cell);
		}
            }
        }
        
        public String toString()
        {
            StringBuffer buffer = new StringBuffer();
            buffer.append("row [");
            
            if ( this.values != null )
            {   
                List<Integer> list = new ArrayList<Integer>(this.values.keySet());
                
                Collections.sort(list);
                
                for(int i = 0; i < list.size(); i++)
                {   int position = list.get(i);
                    
                    Cell cell = this.values.get(position);
                    
                    buffer.append( (cell == null ? null : cell.getValue()) + " @" + position );
                    
                    if ( i < list.size() - 1 )
                    {   buffer.append(", "); }
                }
            }
            
            
            buffer.append("]");
            
            return buffer.toString();
        }
    }
    
    /** class that describe a cell */
    private class Cell
    {   
        /** value of the cell */
        private Object  value    = null;
        
        /** is editable */
        private boolean editable = false;
        
        /** create a new Cell */
        public Cell()
        {   }

        public Object getValue()
        {   
	    Object val = this.value;
	    
	    if ( ! this.hasData() )
	    {
		// TODO
		/* return null --> cell shall not be editable
		 *	perhaps, we could returns a String that explains the reason
		 *
		 */
		val = null;
	    }
	    
	    return val;
	}
	
	/** return true if the cell is considered to have a valid value
	 *  @return a boolean
	 */
	private boolean hasData()
	{
	    return ! (this.value instanceof IntrospectionError);
	}

        public void setValue(Object value)
        {   if ( ! this.hasData() && value != null )
                throw new IllegalStateException("an empty cell could not have a value");
            this.value = value;
        }

        public boolean isEditable()
        {   if ( this.hasData() )
	    {
                return editable;
	    }
            else
	    {
                return false;
	    }
        }

        public void setEditable(boolean editable)
        {   if ( ! this.hasData() && editable )
	    {
                throw new IllegalStateException("an empty cell is non editable");
	    }
            this.editable = editable;
        }
    }
    
    /** class taht help to determine the name of a column */
    protected class ColumnDescriptor
    {
        /** name from PropertyDescriptor */
        private String nameFromProperty = null;
        
        /** custom name */
        private String nameCustom       = null;
        
        /** class of the column */
        private Class  columnClass      = null;
        
        /** name of the property related to this column */
        private String propertyName     = null;
        
        /** create a new ColumnNameDescriptor
         *  @param propertyName the name of the property
         */
        public ColumnDescriptor(String propertyName)
        {   if ( propertyName == null )
                throw new IllegalArgumentException("propertyName must be non null");
            this.propertyName = propertyName;
        }
        
        /** return the name of the property related to this column descriptor
         *  @return a String
         */
        public String getPropertyName()
        {   return this.propertyName; }

        public String getColumnName()
        {   return nameFromProperty; }

        public void setColumnName(String nameFromProperty)
        {   this.nameFromProperty = nameFromProperty; }

        public Class getColumnClass()
        {   return columnClass; }

        public void setColumnClass(Class columnClass)
        {   this.columnClass = columnClass; }
        
        public String toString()
        {   return "ColumnDescriptor : [nameFromProperty=" + this.nameFromProperty + ", customName=" + this.nameCustom + 
                   ", columnClass=" + this.columnClass + ", propertyName=" + this.propertyName + "]";
        }
    }
    
    /** class used in Row to indicate that the value has not been computed */
    private static enum IntrospectionError
    {
	NO_PROPERTY_DESCRIPTOR_ERROR,
	INVOCATION_TARGET_ERROR,
	ILLEGAL_ARGUMENT_ERROR,
	ILLEGAL_ACCESS_ERROR;
    }
}
