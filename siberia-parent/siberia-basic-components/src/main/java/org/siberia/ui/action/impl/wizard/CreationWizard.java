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
package org.siberia.ui.action.impl.wizard;

import com.l2fprod.common.propertysheet.PropertySheetTable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.awl.DefaultWizardPageDescriptor;
import org.awl.DefaultWizard;
import org.awl.Wizard;
import org.awl.WizardConstants;
import org.awl.WizardPageDescriptor;
import org.awl.event.WizardModelAdapter;
import org.awl.exception.PageDescriptorChangingException;
import org.awl.exception.UnregisteredDescriptorException;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaIntrospector;
import org.siberia.TypeInformationProvider;
import org.siberia.type.SibWrapper;
import org.siberia.type.Namable;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.ui.swing.property.ExtendedPropertySheetPanel;
import org.siberia.ui.swing.property.ExtendedPropertySheetTable;
import org.siberia.ui.swing.property.event.PropertySheetPanelEvent;
import org.siberia.ui.swing.property.event.PropertySheetPanelListener;

/**
 *
 * Wizard that allow to create a type
 *
 * @author alexis
 */
public class CreationWizard extends DefaultWizard
{    
    /** logger */
    private static Logger logger     = Logger.getLogger(CreationWizard.class);
    
    /** the collection related to this wizard */
    private SibCollection                   collection        = null;
    
    /** the type customization page */
    private TypeCustomizationPageDescriptor customizationPage = null;
    
    /** Creates a new instance of CreationWizard
     *	@param frame a Frame
     *  @param collection the collection where the new item will be added later
     */
    public CreationWizard(Frame frame, final SibCollection collection)
    {
        this(frame, collection, null);
    }
    
    /** Creates a new instance of CreationWizard
     *	@param frame a Frame
     *  @param collection the collection where the new item will be added later
     *  @param proposedClass the class to propose to instantiate (if not null, do not ask the class to instantiate)
     */
    public CreationWizard(Frame frame, final SibCollection collection, Class proposedClass)
    {   
        super(frame);
        
        if ( collection == null )
        {
            throw new IllegalArgumentException("collection cannot be null");
        }
	
	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	org.siberia.ui.awl.WizardPreparator.prepareWizard(this);
        
        this.collection = collection;
        
	ResourceBundle rb = ResourceBundle.getBundle(CreationWizard.class.getName());
        this.setTitle(rb.getString("wizard.title"));
        
        List<Class> classesTmp = null;
        
        if ( collection.isAcceptingSubClassesItem() )
        {
            classesTmp = TypeInformationProvider.getInstance().getSubClassFor(
                                                collection.getAllowedClass(), true, false);
        }
        else
        {
            classesTmp = new ArrayList<Class>(1);
            classesTmp.add(collection.getAllowedClass());
        }
        
        final List<Class> classes = classesTmp;
        
        logger.debug("getting " + classes.size() + " kind of items that are allowed to appear in the list");
        
        /** add pages */
        final ChooseClassPageDescriptor chooseType = new ChooseClassPageDescriptor(classes);
        chooseType.setPreviousDescriptorId(WizardConstants.STARTING_DESCRIPTOR_ID);
        chooseType.setNextDescriptorId("2");
        
        this.customizationPage = new TypeCustomizationPageDescriptor();
        
        this.customizationPage.setPreviousDescriptorId("1");
        this.customizationPage.setNextDescriptorId(WizardConstants.TERMINAL_DESCRIPTOR_ID);
        
        this.registerWizardPanel("1", chooseType);
        this.registerWizardPanel("2", this.customizationPage);
        
        Class classToPropose = proposedClass;
        
        if ( classToPropose == null && classes.size() == 1 )
        {
            classToPropose = classes.get(0);
        }
        
        if ( classToPropose != null )
        {   this.setClassToInstantiate(classToPropose); }
        
        /** add a WizardModelListener to be sure that when we try to go to
         *  second stage, a selection is made in the list
         */
        this.getModel().addWizardModelListener(new WizardModelAdapter()
        {
            /** method that is called to verify that all WizardModelListener agree to change the current descriptor
             *  @param currentDescriptor the current descriptor
             *  @param candidate the descriptor we try to set as current descriptor
             *
             *  @exception PageDescriptorChangingException if the listener refused the change
             */
            public void checkCurrentDescriptorChanging(WizardPageDescriptor currentDescriptor, WizardPageDescriptor candidate)
                    throws PageDescriptorChangingException
            {   if ( currentDescriptor == chooseType )
                {   /* verify that a selection is made and indicate to second page the class to use */
                    Class c = chooseType.getSelectedClass();
                    
                    if ( c != null )
                    {   setTypeToCustomize(customizationPage, c); }
                    else
                    {   throw new PageDescriptorChangingException(this, "select a kind of type"); }
                }
                else if ( currentDescriptor == customizationPage && candidate != chooseType )
                {   /* check that there is no other item called like new item
                     * and check that the name is not empty
                     */
                    boolean nameEmpty = false;
                    if ( customizationPage.getType().getName() == null )
                    {   nameEmpty = true; }
                    else if ( customizationPage.getType().getName().trim().length() == 0 )
                    {   nameEmpty = true; }
                    
		    /** if name is empty and the property name can be fullfill
		     *	then indicate to user to give a name
		     */
		    if ( customizationPage.allowToSpecifyProperty(SibType.PROPERTY_NAME) )
		    {
			if ( nameEmpty )
			{   throw new PageDescriptorChangingException(this, "Provide a valid name"); }

			SibType otherType = collection.getChildNamed(customizationPage.getType().getName());
			if ( otherType != null )
			{   throw new PageDescriptorChangingException(this,
						"Another item is already called '" + 
						customizationPage.getType().getName() + "'");
			}
		    }
                }
            }
        });
    }
    
    @Override
    public void dispose()
    {
	super.dispose();
	
	if ( this.customizationPage != null )
	{
	    this.customizationPage.dispose();
	    this.customizationPage = null;
	}
    }
    
    /** method that force the wizard to create a kind of item according to the given class
     *  @param c a Class
     *  @exception IllegalArgumentException if an instance of the given class cannot be added to the list
     */
    private void setClassToInstantiate(Class c)
    {
        if ( c == null )
        {
            throw new IllegalArgumentException("class cannot be null");
        }
        
        if ( ! this.collection.itemAllowed(c) )
        {
            throw new IllegalArgumentException("the class '" + c + "' is not allowed by the collection named '" + this.collection.getName() + "'");
        }
        
        try
        {   this.setTypeToCustomize(this.customizationPage, c);

            this.getModel().setCurrentDescriptor("2");
        }
        catch (UnregisteredDescriptorException ex)
        {   ex.printStackTrace(); }
        catch (PageDescriptorChangingException ex)
        {   ex.printStackTrace(); }
    }
    
    /** indicate to a TypeCustomizationPageDescriptor the kind of item to customize
     *  @param customizationPage a TypeCustomizationPageDescriptor
     *  @param c a Class
     */
    private void setTypeToCustomize(TypeCustomizationPageDescriptor customizationPage, Class c) throws PageDescriptorChangingException
    {
        try
        {   Object o = c.newInstance();
            if ( o instanceof SibType )
            {   
		customizationPage.setType( (SibType)o );
	    }
            else
            {
                logger.error("instantiating a class that is not implementing Sibtype");
                throw new PageDescriptorChangingException(this, "not a SibType");
            }
        }
        catch(Exception e)
        {   logger.error("unable to instantiate class '" + c + "'", e);
            throw new PageDescriptorChangingException(this, "cannot be instantiated");
        }
    }
    
    /** return the object created by the wizard
     *  @return a SibType
     */
    public SibType getCreatedObject()
    {   TypeCustomizationPageDescriptor page = (TypeCustomizationPageDescriptor)this.getModel().getDescriptor("2");
        
        return page.getType();
    }
    
    /** page that allow to customize configuration parameters of a type */
    private class TypeCustomizationPageDescriptor extends DefaultWizardPageDescriptor
	    implements PropertySheetPanelListener
    {
        /** the type to modify */
        private SibType			   type		= null;
	
	/** property sheet */
	private ExtendedPropertySheetPanel panel	= null;
	
	/** introspector */
	private SiberiaIntrospector        introspector = null;
        
        public TypeCustomizationPageDescriptor()
        {   super("Customize");
	    
	    this.introspector = new SiberiaIntrospector();
            
	    this.panel = new ExtendedPropertySheetPanel(new ExtendedPropertySheetTable());
	    this.panel.addPropertySheetPanelListener(this);
            
            this.setComponent(this.panel);
        }

        /** initialize the type that is to be ruled by this page
         *  @param type an Object
         */
        public void setType(final SibType type)
        {   
	    this.type = type;
	    
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    BeanInfo info = null;

		    if ( type != null )
		    {
			boolean debug = ResourceLoader.getInstance().isDebugEnabled();

			info = introspector.getBeanInfo(type.getClass(), debug ? BeanInfoCategory.ALL : BeanInfoCategory.CONFIGURATION, 
							     debug, true);
		    }

		    panel.setBeanInfo(info);

		    panel.readFromObjects(new SibType[]{type});

		    panel.getTable().setPreferredScrollableViewportSize(new Dimension(100, 
							     Math.max(panel.getTable().getModel().getRowCount() * panel.getTable().getRowHeight(), 50)));
		    panel.getTable().revalidate();

		    panel.setSortingProperties(true);
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
	
        /** return the type that was ruled by this page
         *  @return an Object
         */
        public SibType getType()
        {   
	    return this.type;
	}
	
	void dispose()
	{
	    this.type = null;
	    this.introspector = null;
	    
	    if ( this.panel != null )
	    {
		this.panel.removePropertySheetPanelListener(this);
		this.panel = null;
	    }
	}
	
	/** try to launch edition on the row name */
	public void tryToEditName()
	{
	    if ( this.panel != null )
	    {
		/* start edition on the name property if it exists 
		 * well, workaround to edit cell.
		 *  
		 *  if the following is made :
		 *
		 *
		 *  Runnable runnable = new Runnable()
	         *	{
		 *	    public void run()
		 *	    {
		 *		boolean result = beanEditor.getTable().editAtProperty(Namable.PROPERTY_NAME);
		 *		System.out.println("edit property ? " + result);
		 *		System.out.println("table is showing ? " + beanEditor.getTable().isShowing());
		 *	    }
		 *	};
		 *
		 *   if ( SwingUtilities.isEventDispatchThread() )
		 *   {
		 *	runnable.run();
		 *   }
		 *   else
		 *   {
		 *	SwingUtilities.invokeLater(runnable);
		 *   }
		 *
		 *  the cell is not ever edited...
		 *
		 *  TODO
		 */
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			Runnable runnable = new Runnable()
			{
			    public void run()
			    {
				Runnable runnable = new Runnable()
				{
				    public void run()
				    {
					boolean result = ((ExtendedPropertySheetTable)panel.getTable()).editAtProperty(Namable.PROPERTY_NAME);
				    }
				};
				SwingUtilities.invokeLater(runnable);
			    }
			};
			SwingUtilities.invokeLater(runnable);
		    }
		};

		SwingUtilities.invokeLater(runnable);
	    }
	}
	
	/** return true if the page allow to specify the given property
	 *  @param propertyName the name of the property
	 *  @return true if the page allow to specify the given property
	 */
	public boolean allowToSpecifyProperty(String propertyName)
	{
	    boolean result = false;
	    
	    if ( this.panel != null )
	    {
		result = ((ExtendedPropertySheetTable)this.panel.getTable()).allowToSpecifyProperty(propertyName);
	    }
	    
	    
	    return result;
	}
	
	/* #####################################################################
	 * ################### BeanEditorListener impl #########################
	 * ##################################################################### */
	
	/** indicate that the bean editor has updated its table content
	 *	@param event a BeanEditorEvent
	 */
	public void tableContentUpdated(PropertySheetPanelEvent event)
	{
	    if ( event != null && event.getSource() == this.panel )
	    {
		this.tryToEditName();
	    }
	}
    }
    
    /** page that allow to choose the type to create */
    private class ChooseClassPageDescriptor extends DefaultWizardPageDescriptor
    {
        /** list */
        private JList list = null;
        
        public ChooseClassPageDescriptor(final List<Class> classes)
        {   super("Choose kind");
            
            /* build content */
            JPanel panel = new JPanel();
            BoxLayout layout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
            panel.setLayout(layout);
           
            /* build custom model */
            ListModel model = new AbstractListModel()
            {
                public Object getElementAt(int index)
                {   return classes.get(index); }
                
                public int getSize()
                {   return classes.size(); }
            };
            
            /** build a ListModel with this list */
            this.list = new JList(model);
            
            /** place renderer */
            list.setCellRenderer(new DefaultListCellRenderer()
            {
                /** map that keep in memory the display name associated with classes */
                private Map<Class, String> nameMap = new HashMap<Class, String>();
                
                public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus)
                {   Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    
                    if ( c instanceof JLabel && value instanceof Class )
                    {   String name = this.nameMap.get( (Class)value );
                        
                        if ( name == null )
                        {   name = TypeInformationProvider.getDisplayName( (Class)value );
                            
                            /** feed the map */
                            this.nameMap.put( (Class)value, name );
                        }
                        
                        ((JLabel)c).setText(name);
                    }
                    
                    return c;
                }
            });
            
            final JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
            
            list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
            {
                public void valueChanged(ListSelectionEvent e)
                {   
                    if ( e.getSource() == list.getSelectionModel() )
                    {   int index = list.getSelectionModel().getMinSelectionIndex();
                        
                        logger.debug("new index selected in the list : " + index);
                        
                        Class c = null;
                        if ( index >= 0 && index < list.getModel().getSize() )
                        {   c = (Class)list.getModel().getElementAt(index); }
                        
                        logger.debug("new related class selected in the list : " + c);
                        
                        String textAreaContent = "";
                        if ( c != null )
                        {   textAreaContent = TypeInformationProvider.getDescription(c); }
                        
                        logger.debug("setting description '" + textAreaContent + "'");
                        
                        textPane.setText(textAreaContent);
                    }
                }
            });
            
            if ( list.getModel().getSize() > 0 )
            {   list.getSelectionModel().setSelectionInterval(0, 0); }
            
            panel.add(new JScrollPane(this.list));
            panel.add(new JScrollPane(textPane));
            
            this.setComponent(panel);
        }
        
        /** return the selected class or null if no selection
         *  @return the selected class
         */
        public Class getSelectedClass()
        {   Class c = null;
            if ( this.list != null )
            {   int index = this.list.getSelectionModel().getMinSelectionIndex();
                
                if ( index >= 0 && index < this.list.getModel().getSize() )
                {   Object o = this.list.getModel().getElementAt(index);
                    
                    if ( o instanceof Class )
                    {   c = (Class)o; }
                }
            }
            
            return c;
        }
    }
    
}
