/* 
 * Siberia bean editor : siberia plugin offer an editor that allow ot modify the caracteristics of a bean
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
package org.siberia.ui.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.siberia.SiberiaBeanEditorPlugin;
import org.siberia.SiberiaIntrospector;
import org.siberia.editor.AuxiliaryEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.editor.AbstractEditor;
import org.siberia.type.SibType;
import org.siberia.type.SibWrapper;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.TypeInformationProvider;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;
import org.siberia.ui.swing.property.ExtendedPropertySheetTable;
import org.siberia.ui.swing.property.ExtendedPropertySheetTableModel;
import org.siberia.ui.swing.property.ExtendedPropertySheetPanel;
import org.siberia.ui.swing.property.ExtendedPropertySheet;
import org.siberia.ui.swing.property.ExtendedProperty;

/**
 *
 * Siberia editor that allow to edit bean properties
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.SibWrapper.class},
                  description="Bean editor",
                  name="Bean editor",
                  launchedInstancesMaximum=1)
public class BeanEditor extends AbstractEditor implements AuxiliaryEditor,
                                                  SibWrapper.WrapListener
{   
    /** the model of data that will be render by the table **/
    private ExtendedPropertySheetPanel         propertyPanel         = null;
    
    /** the table that will render the data **/
    private ExtendedPropertySheetTable         table                 = null;
    
    /** wrapper for the entity currently in charge */
    private SibWrapper                         wrapper               = null;
    
    /** indicates if expert information should be displayed */
    private boolean                            expert                = true;
    
    /** BeanInfoCategory */
    private BeanInfoCategory                   category              = null;
    
    /** current BeanInfo */
    private BeanInfo                           beanInfo              = null;
    
    /*main panel */
    private JPanel                             panel                 = null;
    
    /** soft reference to a SiberiaIntrospector */
    private SoftReference<SiberiaIntrospector> introspectorRef       = new SoftReference<SiberiaIntrospector>(null);
    
    /** working set of classes */
    private Set<Class>                         workingClasses        = null;
    
    /** executor service */
    private ExecutorService                    executor              = Executors.newSingleThreadExecutor();
    
    /** current link with future */
    private Future                             currentLinkWithFuture = null;
    
    /** Creates a new instance of PropertyArea */
    public BeanEditor()
    {   
	super();
	
	BeanInfoCategory category = BeanInfoCategory.BASICS;
	
	if ( ResourceLoader.getInstance().isDebugEnabled() )
	{
	    category = BeanInfoCategory.ALL;
	}
	
	this.setCategory(category);
    }
    
    /** return a SiberiaIntrospector
     *  @return a SiberiaIntrospector
     */
    private SiberiaIntrospector getIntrospector()
    {   SiberiaIntrospector in = this.introspectorRef.get();
        if ( in == null )
        {   in = new SiberiaIntrospector();
            this.introspectorRef = new SoftReference<SiberiaIntrospector>(in);
        }
        
        return in;
    }
        
    /** return the ExtendedPropertySheetPanel table 
     *	@return a ExtendedPropertySheetPanel
     */
    public ExtendedPropertySheetPanel getPropertyPanel()
    {
	return this.propertyPanel;
    }
    
    /** return the ExtendedPropertySheetTable table 
     *	@return a ExtendedPropertySheetTable
     */
    public ExtendedPropertySheetTable getTable()
    {
	return this.table;
    }

    /**
     * indicate that the editor feels modified
     * @param modified true to indicate that the editor feels modified
     */
    @Override
    public void setModified(boolean modified)
    {   /* it is never modified */ }

    /**
     * return true if the editor is modified
     * @return true if the editor is modified
     */
    @Override
    public boolean isModified()
    {   
	/* never modified itself since modifications are considered are dynamically applied */
	return false;
    }

    /** return true if the editor displayed expert information
     *  @return a boolean
     */
    public boolean isExpert()
    {   return expert; }

    /** tell if the editor displayed expert information
     *  @param expert true to indicate to the editor that expert information have to be displayed
     */
    public void setExpert(boolean expert)
    {   if ( expert != this.expert )
        {   this.expert = expert;

            this.updateContext();
        }
    }

    /** return the category used by the editor to choose what kind of information to consider
     *  @return a BeanInfoCategory 
     */
    public BeanInfoCategory getCategory()
    {   return category; }

    /** indicate the category used by the editor to choose what kind of information to consider
     *  if category is null, then BeanInfoCategory.BASICS is considered.
     *  @param category a BeanInfoCategory 
     */
    public void setCategory(BeanInfoCategory category)
    {   BeanInfoCategory tmp = category;
        if ( tmp == null )
            tmp = BeanInfoCategory.BASICS;
        
        if ( tmp != this.category )
        {   
            this.category = tmp;
            
            this.updateContext();
        }
    }
    
    /** update the context. force the table to redisplay
     *
     *  it uses the object referenced by the model if it exists to launch edition
     **/
    protected void updateContext()
    {   if ( this.table != null )
        {   ExtendedPropertySheetTableModel model = this.table.getExtendedTableModel();
            if ( model != null )
            {   this.linkWith(model.getObjects()); }
        }
    }
    
    /** link the property editor with a new instance
     *  force the table to redisplay
     *  @param beans an array of SibType
     **/
    protected void linkWith(final SibType... beans)
    {   
	if ( this.currentLinkWithFuture != null && ! this.currentLinkWithFuture.isDone() )
	{
	    this.currentLinkWithFuture.cancel(true);
	}
	
	this.currentLinkWithFuture = this.executor.submit(new Runnable()
	{
	    public void run()
	    {
		SibType[] copy = beans;

		/** create the BeanInfo for the type and link the model with the current type */
		Icon icon = null;

		if ( copy != null )
		{
		    if ( copy.length == 0 )
		    {
			copy = null;
		    }
		    else
		    {
			if ( workingClasses == null )
			{
			    workingClasses = new HashSet<Class>();
			}
			else
			{
			    workingClasses.clear();
			}

			int validItemsCount = 0;
			SibType firstInstance = null;

			for(int i = 0; i < copy.length; i++)
			{
			    SibType current = copy[i];

			    if ( current != null )
			    {
				if ( firstInstance == null )
				{
				    firstInstance = current;
				}

				validItemsCount ++;
				workingClasses.add(current.getClass());
			    }
			}

			BeanInfo info = null;

			if ( validItemsCount == 0 )
			{
			    copy = null;
			}
			else
			{
			    /* there is at list one non null item, so there is at least one class in this.workingClasses */
			    if ( workingClasses.size() == 1 )
			    {
				try
				{   String resPath = TypeInformationProvider.getIconResource(firstInstance);
				    if ( resPath != null )
				    {   icon = ResourceLoader.getInstance().getIconNamed(resPath); }
				}
				catch (ResourceException ex)
				{   
				    ex.printStackTrace();
				}
			    }
			    else
			    {
				/** use another icon that represents mixed items */
				try
				{
				    icon = ResourceLoader.getInstance().getIconNamed(SiberiaBeanEditorPlugin.PLUGIN_ID + ";1::img/miscellaneous.png");
				}
				catch(ResourceException e)
				{
				    e.printStackTrace();
				}
			    }

			    if ( validItemsCount == 1 )
			    {
				info = getIntrospector().getBeanInfo(firstInstance.getClass(), getCategory(), isExpert(), true);
			    }
			    else
			    {
				/** merge beaninfo declaration with introspector functionnalities
				 *  and create a virtual bean info with the resulting PropertyDescriptor
				 */
				List<BeanInfo> beanInfos = new ArrayList<BeanInfo>(workingClasses.size());

				Iterator<Class> it = workingClasses.iterator();

				while(it.hasNext())
				{
				    Class currentClass = it.next();

				    if ( currentClass != null )
				    {
					beanInfos.add(getIntrospector().getBeanInfo(currentClass, getCategory(), isExpert(), true));
				    }
				}

				List<PropertyDescriptor> properties = getIntrospector().extractGroupSupportingPropertyDescriptors(beanInfos);

				final PropertyDescriptor[] propertysArray = (PropertyDescriptor[])properties.toArray(new PropertyDescriptor[properties.size()]);

				info = new BeanInfo()
				{
				    public BeanInfo[] getAdditionalBeanInfo()
				    {
					return null;
				    }
				    public BeanDescriptor getBeanDescriptor()
				    {
					return null;
				    }
				    public int getDefaultEventIndex()
				    {
					return 0;
				    }
				    public int getDefaultPropertyIndex()
				    {
					return 0;
				    }
				    public EventSetDescriptor[] getEventSetDescriptors()
				    {
					return null;
				    }
				    public Image getIcon(int iconKind)
				    {
					return null;
				    }
				    public MethodDescriptor[] getMethodDescriptors()
				    {
					return null;
				    }
				    public PropertyDescriptor[] getPropertyDescriptors()
				    {
					return propertysArray;
				    }
				};
			    }
			    
			    final BeanInfo infoCopy            = info;
			    final SibType  firstInstanceCopy   = firstInstance;
			    final int      validItemsCountCopy = validItemsCount;

			    /** apply beaninfo */
			    Runnable runnable = new Runnable()
			    {
				public void run()
				{
				    propertyPanel.setBeanInfo(infoCopy);

				    if ( validItemsCountCopy == 1 )
				    {
					updatePropertySheet(firstInstanceCopy);
				    }
				    else
				    {
					updatePropertySheet(beans);
				    }
				    
				    /** indicate to listener that the property table has been updated */
				    
				}
			    };
		    
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

		if ( copy == null )
		{
		    try
		    {
			icon = ResourceLoader.getInstance().getIconNamed(SiberiaBeanEditorPlugin.PLUGIN_ID + ";1::img/none.png");
		    }
		    catch(ResourceException e)
		    {
			e.printStackTrace();
		    }

		    Runnable runnable = new Runnable()
		    {
			public void run()
			{
			    table.getSheetModel().setProperties(new ExtendedProperty[]{});
			    
			    if ( table.getSheetModel() instanceof ExtendedPropertySheetTableModel )
			    {
				((ExtendedPropertySheetTableModel)table.getSheetModel()).setObjects((SibType[])null);
			    }
			}
		    };
		    
		    if ( SwingUtilities.isEventDispatchThread() )
		    {
			runnable.run();
		    }
		    else
		    {
			SwingUtilities.invokeLater(runnable);
		    }
		}

		setIcon(icon);

	//        if ( info != null )
	//        {   
	//            PropertyDescriptor[] descs = info.getPropertyDescriptors();
	////            for(int i = 0; i < descs.length; i++)
	////            {   PropertyDescriptor current = descs[i];
	////                
	////                System.out.println("\ti=" + i + " --> " + current.getName() + " "  + current.getReadMethod() + " " + current.getWriteMethod());
	////            }
	//	    
	//            this.propertyPanel.setBeanInfo(info);
	//            
	//            this.updatePropertySheet(bean);
	//        }
	//        else
	//        {   this.table.getSheetModel().setProperties(new ExtendedProperty[]{}); }
	    }
	});
    }
    
    /** methods which allow to configure table and to apply the value of the given bean<br>
     *  the bean info to used must already have been installed on propertyPanel
     *  @param beans an array of SibTypes
     */
    private void updatePropertySheet(SibType... beans)
    {      
        if ( beans != null && this.table != null )
        {   ExtendedPropertySheetTableModel model = this.table.getExtendedTableModel();
            if ( model != null )
            {   
		this.propertyPanel.readFromObjects(beans);

                this.table.setPreferredScrollableViewportSize(new Dimension(100, 
                                                              Math.max(model.getRowCount() * this.table.getRowHeight(), 50)));
		this.table.revalidate();
		
		this.propertyPanel.setSortingProperties(true);
            }
        }
    }
    
    /* ######################################################################
     * ################### SibWrapper.WrapListener impl #####################
     * ###################################################################### */

    /** the wrapped entity of a SibWrapper has changed
     *  @param wrapper an instance of SibWrapper
     *  @param oldTypes the old wrapped entities
     *  @param newTypes the new wrapped entities
     */
    public void entityWrappedChanged(SibWrapper wrapper, SibType[] oldTypes, SibType[] newTypes)
    {   if ( wrapper == this.wrapper )
        {   this.linkWith(newTypes); }
    }
        
    /* ######################################################################
     * ###################### Editor implementation #########################
     * ###################################################################### */

    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance instanceof SibWrapper )
        {   if ( this.wrapper != null )
                this.wrapper.removeWrapListener(this);
            this.wrapper = (SibWrapper)instance;
            
            if ( this.wrapper != null )
            {   this.wrapper.addWrapListener(this); }
            
            this.linkWith( this.wrapper == null ? null : this.wrapper.getWrappedTypes() );
        }
    }

    @Override
    public SibType getInstance()
    {   return this.wrapper; }

    public Component getComponent()
    {   
	if ( this.panel == null )
	{
	    this.panel = new JPanel();

	    BoxLayout layout = new BoxLayout(this.panel, BoxLayout.PAGE_AXIS);

	    this.panel.setLayout(layout);

	    this.table = new ExtendedPropertySheetTable();

	    this.propertyPanel = new ExtendedPropertySheetPanel(this.table);
//	    this.propertyPanel.setDescriptionVisible(true);
	    this.propertyPanel.setMode(ExtendedPropertySheet.VIEW_AS_FLAT_LIST);

	    this.panel.add(new JScrollPane(this.propertyPanel));
	    
	    if ( this.getInstance() instanceof SibWrapper )
	    {
		this.linkWith( ((SibWrapper)this.getInstance()).getWrappedTypes() );
	    }
	}
	return this.panel;
    }

    public boolean canBeClosed()
    {   return true; }
    
}
