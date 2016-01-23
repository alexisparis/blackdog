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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.siberia.properties.XmlCategory;
import org.siberia.properties.XmlProperty;
import org.siberia.properties.XmlPropertyContainer;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;
import org.siberia.ui.properties.PropertyEditor;

/**
 *
 * property renderer for XmlCategory
 *
 * @author alexis
 */
public class CategoryPropertyRenderer extends JPanel
{   
    /** mode debug */
    private static final boolean DEBUG = false;
    
    /** left border */
    private static final int LEFT_BORDER = 15;
    
    /** category */
    private XmlCategory category = null;
    
    /** Creates a new instance of CategoryPropertyRenderer
     *  @param property an instance of XmlCategory
     *  @param ignoreCategory true if it must only render the sub items of the given category
     */
    public CategoryPropertyRenderer(XmlCategory category, boolean ignoreCategory)
    {   
	super(new GridBagLayout());
	
        this.category = category;
        
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        this.setAlignmentX(0.0f);
        this.setAlignmentY(0.0f);
        
        int y = 1;
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = gbc.LINE_START;
        
        if ( ! ignoreCategory )
        {
            gbc.gridx = 1;
            gbc.gridy = y;
            gbc.gridheight = 1;
            gbc.gridwidth  = 1;
	    gbc.anchor = gbc.WEST;
//            gbc.anchor = gbc.FIRST_LINE_START;
//            gbc.weightx = 1;
//            gbc.weighty = 0;
            gbc.insets     = new Insets(0, 0, 3, 0);

            JLabel title = new JLabel("<html><u><font color=\"#FF0000\">" + category.getLabel() + "</font></u></html>");
	    
	    if ( DEBUG )
	    {
		title.setBackground(Color.PINK);
		title.setOpaque(true);
	    }
	    
            title.setHorizontalTextPosition(SwingConstants.RIGHT);
            title.setAlignmentX(0.0f);
            
            if ( category.getIcon() != null )
            {   if ( category.getIcon().trim().length() > 0 )
                {   try
                    {   title.setIcon(ResourceLoader.getInstance().getIconNamed(category.getIcon())); }
                    catch(ResourceException e)
                    {   e.printStackTrace(); }
                }
            }
            
            this.add(title, gbc);
            
            y += 2;
        }
        
        int separatorStart = y;
        
        /* add elements recursively */
        if ( this.category != null )
        {   if ( this.category.getItems() != null )
            {   Iterator<XmlPropertyContainer> it = this.category.getItems().iterator();
                
                while(it.hasNext())
                {   Object current = it.next();
                    
                    boolean added = false;
                    
                    if ( current instanceof XmlProperty )
                    {   
			XmlProperty property = (XmlProperty)current;
			
                        PropertyEditor editor = EditorPropertiesComponentFactory.editProperty(property);
                        
                        if ( editor != null )
                        {
                            /* create property label */
                            JLabel label = EditorPropertiesComponentFactory.createLabel(property);
                            editor.setLabel(label);
                            
                            JComponent[] components = new JComponent[2];
                            components[0] = label;
                            components[1] = editor.getComponent();
                            
                            for(int i = 0; i < components.length; i++)
                            {   JComponent component = components[i];
                                if ( component != null )
                                {   if ( !added )
                                        added = true;
                                    
                                    gbc = new GridBagConstraints();
                                    
                                    gbc.gridx      = i + 1;
                                    gbc.gridy      = y;
                                    gbc.gridheight = 1;
                                    gbc.gridwidth  = 1;
//                                    gbc.weightx = 1;
//                                    gbc.weighty = 0;
                                    gbc.anchor = gbc.FIRST_LINE_START;
				    
				    gbc.anchor = gbc.WEST;
				    
                                    gbc.insets     = new Insets(0, LEFT_BORDER, 0, 0);
                                    component.setAlignmentX(0.0f);
                                    component.setAlignmentY(0.0f);
                                    this.add(component, gbc);
                                }
                            }
                        }
                    }
                    else if ( current instanceof XmlCategory )
                    {   
                        gbc = new GridBagConstraints();
                        
                        gbc.gridx = 1;
                        gbc.gridy = y;
                        gbc.gridheight = 1;
                        gbc.gridwidth  = 10;
                        gbc.insets     = new Insets(0, LEFT_BORDER, 0, 0);
			gbc.anchor = gbc.WEST;
//                        gbc.weightx = 1;
//                        gbc.weighty = 0;
                        added = true;
                        JPanel panel = EditorPropertiesComponentFactory.renderCategory( (XmlCategory)current);
//                        JPanel panel = EditorPropertiesComponentFactory.renderCategoryItems( (XmlCategory)current );
			
                        panel.setAlignmentX(0.0f);
                        panel.setAlignmentY(0.0f);
			
			if ( DEBUG )
			{
			    panel.setBackground(Color.GREEN);
			
			    System.out.println("sub category : " + ((XmlCategory)current).getLabel());
			    
			    System.out.println("ajout avec contrainte : ");
			    System.out.println("\tgridx : " + gbc.gridx);
			    System.out.println("\tgridy : " + gbc.gridy);
			    System.out.println("\tanchor : " + gbc.anchor);
			    System.out.println("\tfill : " + gbc.fill);
			    System.out.println("\tgridheight : " + gbc.gridheight);
			    System.out.println("\tgridwidth : " + gbc.gridwidth);
			    System.out.println("\tinsets : " + gbc.insets);
			    System.out.println("\tipadx : " + gbc.ipadx);
			    System.out.println("\tipady : " + gbc.ipady);
			    System.out.println("\tweightx : " + gbc.weightx);
			    System.out.println("\tweighty : " + gbc.weighty);

			    System.out.println("panel properties : ");
			    System.out.println("\tpanel align x : " + panel.getAlignmentX());
			    System.out.println("\tpanel align y : " + panel.getAlignmentY());
			    System.out.println("\tborder : " + panel.getBorder());
			    System.out.println("\tinsets : " + panel.getInsets());
			}
                        
                        this.add(panel, gbc);
                    }
                    
                    if ( added )
                    {   
			y += 2;
                        gbc = new GridBagConstraints();
                        gbc.gridy = y;
                        this.add(new JToolBar.Separator(new Dimension(2, 2)), gbc);
                        
                        y += 1;
                        
                        if ( current instanceof XmlCategory )
                        {   
			    gbc = new GridBagConstraints();
			    gbc.gridy = y;
                            this.add(new JToolBar.Separator(new Dimension(2, 2)), gbc);

                            y += 1;
                        }
                    }
                }
                
                /* add a JSeparator to group elements of this category */
//                gbc.gridx      = 1;
//                gbc.gridy      = separatorStart;
//                gbc.gridwidth  = 1;
//                gbc.gridheight = y - separatorStart;
//                gbc.insets     = new Insets(0, 0, 0, 0);
//                
//                System.out.println("height : " + (y - separatorStart));
//                this.add(new JSeparator(SwingConstants.VERTICAL), gbc);
            }
        }
           
        /** add a virtual label with weight at 1 to disable space distribution to previous items */
        gbc = new GridBagConstraints();

        gbc.gridx      = 999;
        gbc.gridy      = 999;
        gbc.weightx = 1.0f;
        gbc.weighty = 1.0f;
        this.add(new JLabel(""), gbc);
    }
}
