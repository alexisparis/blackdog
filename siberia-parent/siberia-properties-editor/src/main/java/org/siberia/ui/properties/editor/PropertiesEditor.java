/* 
 * Siberia properties editor : siberia plugin defining properties editor
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
package org.siberia.ui.properties.editor;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.siberia.properties.SiberiaProperties;
import org.siberia.properties.XmlProperties;
import org.siberia.properties.XmlCategory;
//import org.siberia.properties.PropertiesManager;
import org.siberia.editor.ValidationEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.properties.XmlProperty;
import org.siberia.type.event.HierarchicalPropertyChangeEvent;
import org.siberia.type.event.HierarchicalPropertyChangeListener;
import org.siberia.ui.swing.properties.PropertiesToolBar;
import org.siberia.xml.schema.properties.Properties;


/**
 *
 *  Editor for properties. Just need to create a SiberiaProperties instance which specify<br>
 *  the id of the properties.
 *
 *  This editor represents the rootCategories as buttons in a
 *  vertical toolbar filled with togglebuttons.
 *
 *  For one Category, the properties and subcategories are
 *  displayed in a single panel.
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.properties.SiberiaProperties.class},
                  description="Editor for properties",
                  name="Properties editor",
                  launchedInstancesMaximum=1)
public class PropertiesEditor extends ValidationEditor implements HierarchicalPropertyChangeListener
{
    /** logger */
    private Logger                              logger          = Logger.getLogger(PropertiesEditor.class);
    
    /** map linking buttons and scrollpane containing related properties */
    private Map<JToggleButton, JScrollPane>     panelMappings        = null;
    
    /** map linking JToggleButton and XmlCategories */
    private Map<JToggleButton, XmlCategory> categories               = null;
    
    /** toolbar for all toggle buttons */
    private PropertiesToolBar                   toolbar              = null;
    
    /** panel containing all graphical elements */
    private JPanel                              main                 = null;
    
    /** action listener on JTogleButton */
    private ActionListener                      toggleListener       = null;
    
    /** reference to the properties displayed by this editor */
    private Properties                          xmlProperties        = null;
    
    /** xml properties */
    private XmlProperties                       wrappedXmlProperties = null;
    
    /** panel that contains categories renderer */
    private JPanel                              categoriesPanel      = null;
            
    /**
     * Creates a new instance of PropertiesEditor */
    public PropertiesEditor()
    {   super(); }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {   
	if ( this.getInstance() instanceof SiberiaProperties )
        {   
	    SiberiaProperties properties = (SiberiaProperties)this.getInstance();
	    
            try
            {   this.xmlProperties = properties.getRelatedProperties(); }
            catch(Exception e)
            {   logger.error("unable to load properties id=" + properties.getPropertiesId(), e); }

            this.main = new JPanel();
	    this.main.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = null;
	    
            this.toolbar = new PropertiesToolBar();
            JPanel toolPanel = new JPanel();
            this.toolbar.setOrientation(JToolBar.VERTICAL);
            toolPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            toolPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            toolPanel.add(this.toolbar);
	    
	    gbc = new GridBagConstraints();
	    gbc.gridx = 1;
	    gbc.gridy = 1;
	    gbc.fill = gbc.BOTH;
	    JScrollPane scrollToolPanel = new JScrollPane(toolPanel);
	    scrollToolPanel.setMinimumSize(new Dimension(130, 100));
	    
            this.main.add(scrollToolPanel, gbc);
            
            this.wrappedXmlProperties = new XmlProperties(this.xmlProperties);
            
            JToggleButton firstButton = null;
            
            Iterator<XmlCategory> categories = this.wrappedXmlProperties.categories();
            while(categories.hasNext())
            {   XmlCategory category = categories.next();
                JToggleButton currentButton = this.addRootCategory(category);
                currentButton.setName(category.getInnerCategory().getRepr());
                
                if ( firstButton == null )
                {   firstButton = currentButton; }
            }

            /* resize all JToggleButtons */
            if ( this.categories != null )
            {   int preferredHeight = -1;
                int preferredWidth  = -1;
                Iterator<JToggleButton> buttons = this.categories.keySet().iterator();
                while(buttons.hasNext())
                {   Dimension currentSize = buttons.next().getPreferredSize();
                    if ( currentSize.height > preferredHeight )
                        preferredHeight = currentSize.height;
                    if ( currentSize.width > preferredWidth )
                        preferredWidth = currentSize.width;
                }

                if ( preferredHeight != -1 && preferredWidth != -1 )
                {   buttons = this.categories.keySet().iterator();
                    Dimension dim = new Dimension(preferredWidth, preferredHeight);
                    while(buttons.hasNext())
                    {   JToggleButton button = buttons.next();
                        button.setMinimumSize(dim);
                        button.setMaximumSize(dim);
                    }
                }
            }
            
            /** update the properties state */
            this.wrappedXmlProperties.updateStates();
            
            /* add me as a HierarchicalPropertyChangeListener to be warned when a property value has changed */
            categories = this.wrappedXmlProperties.categories();
            while(categories.hasNext())
            {   XmlCategory category = categories.next();
                
                if ( category != null )
                {   category.addHierarchicalPropertyChangeListener(this); }
            }
	    
	    gbc = new GridBagConstraints();
	    gbc.gridx = 2;
	    gbc.gridy = 2;
	    gbc.fill = gbc.BOTH;
	    gbc.weightx = 0.0f;
	    gbc.weighty = 0.0f;
	    this.main.add(new JToolBar.Separator(new Dimension(2, 2)), gbc);
	    
	    gbc = new GridBagConstraints();
	    gbc.gridx = 1;
	    gbc.gridy = 3;
	    gbc.gridwidth = 3;
	    gbc.gridheight = 1;
	    gbc.fill = gbc.BOTH;
	    gbc.anchor = gbc.CENTER;
	    gbc.weightx = 0.0f;
	    gbc.weighty = 0.0f;
//	    this.getValidationPanel().setAlignmentX(0.5f);
            this.main.add(this.getValidationPanel(), gbc);
            
            this.categoriesPanel = new JPanel(new CardLayout());
            
	    gbc = new GridBagConstraints();
	    gbc.gridx = 3;
	    gbc.gridy = 1;
	    gbc.fill = gbc.BOTH;
	    gbc.weightx = 1.0f;
	    gbc.weighty = 1.0f;
            this.main.add(this.categoriesPanel, gbc);
            
            if ( firstButton != null )
            {   this.displayPanelFor(firstButton); }
            
            return this.main;
        }
        return null;
    }
    
    /** add a toggle button to the toolbar
     *  @param rootCategory an instance of XmlCategory
     *  @return the button that was created for the category
     */
    public JToggleButton addRootCategory(XmlCategory rootCategory)
    {   JToggleButton button = null;
        if ( rootCategory != null )
        {   button = EditorPropertiesComponentFactory.renderRootCategory(rootCategory);
            if ( button != null )
            {   
                if ( this.toggleListener == null )
                    this.createActionListener();
                button.addActionListener(this.toggleListener);

                this.toolbar.add(button);
		
                if ( this.categories == null )
                    this.categories = new HashMap<JToggleButton, XmlCategory>();
                this.categories.put(button, rootCategory);
            }
        }
        return button;
    }
    
    /** display the category panel for a given JToggleButton
     *  @param button an instance of JToggleButton
     */
    private void displayPanelFor(JToggleButton button)
    {
        if ( button != null )
        {
            /** get the name of the button */
            String name = button.getName();
            
            CardLayout layout = (CardLayout)this.categoriesPanel.getLayout();
                        
            /* verify if the panel has already been created */
	    
            if ( panelMappings == null )
	    {
		panelMappings = new HashMap<JToggleButton, JScrollPane>();
	    }
	    
            JScrollPane scroll = panelMappings.get(button);

	    if ( scroll == null )
	    {   /* get the XmlCategory related to the button and create an associated renderer */
		XmlCategory category = categories.get( button );
		if ( category != null )
		{   JPanel panel = EditorPropertiesComponentFactory.renderCategoryItems(category);

		    scroll = new JScrollPane(panel);
		    this.categoriesPanel.add(scroll, name);

		    panelMappings.put( button, scroll);
		}
	    }

	    layout.show(this.categoriesPanel, name);
            
            /** manage button selection */
            if ( this.panelMappings != null )
            {   Iterator<JToggleButton> buttons = this.panelMappings.keySet().iterator();
                while(buttons.hasNext())
                {   JToggleButton current = buttons.next();

                    if ( current == button )
                    {
                        if ( ! button.isSelected() )
                        {
                            button.setSelected(true);
                        }
                    }
                    else
                    {
                        current.setSelected(false);
                    }
                }
            }
        }
    }
    
    /** create the action listener on rootCategoryType renderer */
    private void createActionListener()
    {
        this.toggleListener = new ActionListener()
        {   
            public void actionPerformed(ActionEvent e)
            {   
                if ( e.getSource() instanceof JToggleButton)
                {
                    displayPanelFor( (JToggleButton)e.getSource() );
                }
            }
        };
    }
    
    /** remove listeners */
    private void removeListeners()
    {
	/** remove hierarchical property change listener and restore old values */
	if ( this.wrappedXmlProperties != null )
	{
	    Iterator<XmlCategory> categories = this.wrappedXmlProperties.categories();
	    while(categories.hasNext())
	    {   XmlCategory category = categories.next();

		if ( category != null )
		{   category.removeHierarchicalPropertyChangeListener(this); }
	    }
	}
    }
    
    /* #########################################################################
     * ######## implementation of abstract m�thods from AbstractEditor #########
     * ######################################################################### */
    
    /** method to update the caracteristics of the associated entity according
     *  to the value of the graphical components in the editor
     *
     *  It must be executed when the editor will be closed
     *
     */
    @Override
    public void save()
    {   
        super.save();
        
        if ( this.getInstance() instanceof SiberiaProperties )
        {   ((SiberiaProperties)this.getInstance()).save(); }
	
	this.removeListeners();
        
        /** always call confirm values after savinf properties
         *  to allow traditional call to PropertiesManager to get properties value
         */
        if ( this.wrappedXmlProperties != null )
        {   this.wrappedXmlProperties.confirmValues(); }
    }
    
    /** method that indicate that the modifications that occurs in this editor
     *  should no longer to update the caracteristics of the associated entity according
     *  to the value of the graphical components in the editor
     *
     *  It must be executed when the editor will be closed
     *
     */
    @Override
    public void cancel()
    {
        super.cancel();
        
	this.removeListeners();
	
        if ( this.wrappedXmlProperties != null )
        {   this.wrappedXmlProperties.restoreOldValues(); }
    }
    
    /* #########################################################################
     * ################## HierarchicalPropertyChangeListener ###################
     * ######################################################################### */
    
    /** called when a HierarchicalPropertyChangeEvent was throwed by
     *  an object listener by this one
     *  @param eventa HierarchycalPropertyChangeEvent
     */
    public void propertyChange(HierarchicalPropertyChangeEvent event)
    {   
	if ( event.getSource() instanceof XmlCategory )
        {   
	    /* if the property that has changed was the value... */
            if ( event.getPropertyChangeEvent().getPropertyName().equals(XmlProperty.PROP_VALUE) )
            {   
		if ( ! this.isModified() )
                {   
		    this.setModified(true);
		}
            }
        }
    }
}
