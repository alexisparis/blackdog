/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.factory;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import junit.framework.*;
import org.siberia.bar.action.NullAction;
import org.siberia.bar.factory.BarFactory;
import org.siberia.bar.factory.OrderedElementComparator;
import org.siberia.bar.factory.TypeMenuMerger;
import org.siberia.bar.factory.AbstractMenubarMerger;
import org.siberia.bar.provider.BarProvider;
import org.siberia.bar.provider.FileBarProvider;
import org.siberia.bar.xml.JAXBBarLoader;
import org.siberia.xml.schema.bar.MenuType;
import org.siberia.xml.schema.bar.TypeMenu;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.OrderedElement;
import org.siberia.xml.schema.bar.MenuItem;
import org.siberia.xml.schema.bar.ObjectFactory;
import org.siberia.xml.schema.bar.SeparatorElement;
import org.siberia.xml.schema.bar.ParameterType;
import org.siberia.xml.schema.bar.CheckType;
import org.siberia.xml.schema.bar.CheckMenuItem;
import org.siberia.xml.schema.bar.Toolbar;
import org.siberia.xml.schema.bar.ButtonType;
import org.siberia.xml.schema.bar.ComboType;
import org.siberia.xml.schema.bar.SeparatorSizedElement;


/*
 * RandomizerTest.java
 * JUnit based test
 *
 * Created on 19 mai 2006, 21:08
 */

/**
 *
 * @author alexis
 */
public class BarFactoryTest extends TestCase
{   
    /** path where are stored the xml tests declaration files */
    private static final String DIR_PATH = new String("src.test.java.org.siberia.bar.rc").replace('.', File.separatorChar);
    
    public BarFactoryTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(BarFactoryTest.class);
        return suite;
    }

    public void testOrderedComparator() throws Exception
    {   ObjectFactory factory = new ObjectFactory();
        
        List<OrderedElement> elts = new ArrayList<OrderedElement>();
        
        OrderedElement elt = null;
        
        elt = factory.createOrderedElement();
        elt.setOrder(52);
        elts.add(elt);
        
        elt = factory.createOrderedElement();
        elt.setOrder(40);
        elts.add(elt);
        
        elt = factory.createOrderedElement();
        elt.setOrder(35);
        elts.add(elt);
        
        elt = factory.createOrderedElement();
        elt.setOrder(0);
        elts.add(elt);
        
        elt = factory.createOrderedElement();
        elt.setOrder(10);
        elts.add(elt);
        
        Collections.sort(elts, new OrderedElementComparator());
        
        Iterator<OrderedElement> it = elts.iterator();
        int index = 0;
        while(it.hasNext())
        {   elt = it.next();
            switch(index)
            {
                case 0 : assertEquals(0, elt.getOrder().intValue());
                         break;
                case 1 : assertEquals(10, elt.getOrder().intValue());
                         break;
                case 2 : assertEquals(35, elt.getOrder().intValue());
                         break;
                case 3 : assertEquals(40, elt.getOrder().intValue());
                         break;
                case 4 : assertEquals(52, elt.getOrder().intValue());
                         break;
                default : assertTrue(false);
            }
            
            index ++;
        }
    }
    
    public void testMergeToolbars() throws Exception
    {
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {
            Toolbar tool_1 = jaxb.loadToolbar(new FileInputStream(DIR_PATH + File.separator + "toolbar_1.xml"));
            Toolbar tool_2 = jaxb.loadToolbar(new FileInputStream(DIR_PATH + File.separator + "toolbar_2.xml"));
            
            List<Toolbar> tools = new ArrayList<Toolbar>();
            tools.add(tool_1);
            tools.add(tool_2);
            
            List<OrderedElement> list = new BarFactory().mergeToolbars(tools);
            Iterator<OrderedElement> elts = list.iterator();
            
            /* elts has been sorted by the merge operation */
            int index = 0;
            while(elts.hasNext())
            {   OrderedElement elt = elts.next();
                
                switch(index)
                {
                    case 0 : assertTrue(elt instanceof CheckType);
                             CheckType check = (CheckType)elt;
                             assertEquals("Save as", check.getLabel());
                             assertEquals("A", check.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", check.getActionClass());
                             assertEquals(0, check.getParameter().size());
                             break;
                    case 1 : assertTrue(elt instanceof ButtonType);
                             ButtonType button = (ButtonType)elt;
                             assertEquals("Select all", button.getLabel());
                             assertEquals("A", button.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", button.getActionClass());
                             assertEquals(0, button.getParameter().size());
                             break;
                    case 2 : assertTrue(elt instanceof ButtonType);
                             button = (ButtonType)elt;
                             assertEquals("Redo", button.getLabel());
                             assertEquals("R", button.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", button.getActionClass());
                             assertEquals(0, button.getParameter().size());
                             break;
                    case 3 : assertTrue("elt is " + elt, elt instanceof SeparatorElement);
                             break;
                    case 4 : assertTrue(elt instanceof ButtonType);
                             button = (ButtonType)elt;
                             assertEquals("Save", button.getLabel());
                             assertEquals("S", button.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", button.getActionClass());
                             assertEquals(1, button.getParameter().size());
                             assertEquals("a", button.getParameter().get(0).getName());
                             assertEquals("fre", button.getParameter().get(0).getValue());
                             break;
                    case 5 : assertTrue("elt is " + elt, elt instanceof SeparatorSizedElement);
                             break;
                    case 6 : assertTrue("elt is " + elt, elt instanceof SeparatorElement);
                             break;
                    case 7 : assertTrue(elt instanceof ButtonType);
                             button = (ButtonType)elt;
                             assertEquals("Print...", button.getLabel());
                             assertEquals("P", button.getMnemonic());
                             assertEquals("org.siberia.ui.action.NaaaullAction", button.getActionClass());
                             assertEquals(0, button.getParameter().size());
                             break;
                    case 8 : assertTrue(elt instanceof ComboType);
                             ComboType combo = (ComboType)elt;
                             assertEquals("Exit", combo.getLabel());
                             assertEquals("X", combo.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", combo.getActionClass());
                             assertEquals(0, combo.getParameter().size());
                             break;
                    default : assertTrue(false);
                }
                
                index++;
            }
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }
    
    public void testFeedToolbar() throws Exception
    {   
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {   Toolbar bar = jaxb.loadToolbar(new FileInputStream(DIR_PATH + File.separator + "toolbar_3.xml"));
            
            Collections.sort(bar.getItems().getButtonOrCheckOrCombo(), new OrderedElementComparator());
            
            JToolBar toolbar = new JToolBar();
            
            new BarFactory().feedToolbar(toolbar, bar);
            
            /* verify that all components are installed in toolbar */
            for(int i = 0; i < toolbar.getComponentCount(); i++)
            {   Component c = toolbar.getComponent(i);
                switch(i)
                {
                    case 0 : assertTrue(c instanceof JCheckBox);
                             JCheckBox check = (JCheckBox)c;
                             assertEquals("Save as", check.getText());
                             assertEquals(KeyEvent.VK_A, check.getMnemonic());
                             assertTrue(check.getAction() instanceof org.siberia.bar.action.NullAction);
                             break;
                    case 1 : assertTrue(c instanceof JButton);
                             JButton button = (JButton)c;
                             assertEquals("Redo", button.getText());
                             assertEquals(KeyEvent.VK_R, button.getMnemonic());
                             assertTrue(button.getAction() instanceof org.siberia.bar.action.NullAction);
                             break;
                    case 2 : assertTrue(c instanceof JToolBar.Separator);
                             break;
                    case 3 : assertTrue(c instanceof JToolBar.Separator);
                             JToolBar.Separator sep = (JToolBar.Separator)c;
                             // cannot test these values because of updateUI which modify all
//                             assertEquals(4, sep.getSeparatorSize().height);
//                             assertEquals(7, sep.getSeparatorSize().width);
                             break;
                    case 4 : assertTrue(c instanceof JButton);
                             button = (JButton)c;
                             assertEquals("Print...", button.getText());
                             assertEquals(KeyEvent.VK_P, button.getMnemonic());
                             assertTrue(button.getAction() instanceof org.siberia.bar.action.NullAction);
                             break;
                    default : assertTrue(false);
                }           
            }
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }
    
    public void testCreateMenu() throws Exception
    {
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {
            Menubar bar_1 = jaxb.loadMenubar(new FileInputStream(DIR_PATH + File.separator + "menubar_1.xml"));
            
            Collections.sort(bar_1.getMenus().getMenu(), new OrderedElementComparator());
            
            for(int index = 0; index < bar_1.getMenus().getMenu().size(); index++)
            {   JMenu menu = new BarFactory().createMenu(bar_1.getMenus().getMenu().get(index));

                if ( index == 0 )
                {   assertEquals("File", menu.getText());
                    assertEquals(KeyEvent.VK_F, menu.getMnemonic());
                    
                    assertEquals(6, menu.getItemCount());
                    
                    for(int i = 0; i < menu.getItemCount(); i++)
                    {   Component c = menu.getItem(i);
                        switch(i)
                        {   case 0 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item = (JMenuItem)c;
                                     assertEquals("Save", item.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control S"), item.getAccelerator());
                                     assertEquals(KeyEvent.VK_S, item.getMnemonic());
                                     assertTrue(item.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            case 1 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item2 = (JMenuItem)c;
                                     assertEquals("Save as", item2.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control A"), item2.getAccelerator());
                                     assertEquals(KeyEvent.VK_A, item2.getMnemonic());
                                     assertTrue(item2.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            case 2 : assertNull(c);
                                     break;
                            case 3 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item3 = (JMenuItem)c;
                                     assertEquals("Print...", item3.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control P"), item3.getAccelerator());
                                     assertEquals(KeyEvent.VK_P, item3.getMnemonic());
                                     assertTrue(item3.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            case 4 : assertNull(c);
                                     break;
                            case 5 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item4 = (JMenuItem)c;
                                     assertEquals("Exit", item4.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control Q"), item4.getAccelerator());
                                     assertEquals(KeyEvent.VK_X, item4.getMnemonic());
                                     assertTrue(item4.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            default : assertTrue(false);
                        }
                    }
                }
                else if ( index == 1 )
                {   assertEquals("Edit", menu.getText());
                    
                    assertEquals(3, menu.getItemCount());
                    
                    for(int i = 0; i < menu.getItemCount(); i++)
                    {   Component c = menu.getItem(i);
                        switch(i)
                        {   case 0 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item = (JMenuItem)c;
                                     assertEquals("Redo", item.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control U"), item.getAccelerator());
                                     assertEquals(KeyEvent.VK_R, item.getMnemonic());
                                     assertTrue(item.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            case 1 : assertNull(c);
                                     break;
                            case 2 : assertTrue(c instanceof JMenuItem);
                                     JMenuItem item2 = (JMenuItem)c;
                                     assertEquals("Select all", item2.getText());
                                     assertEquals(KeyStroke.getKeyStroke("control A"), item2.getAccelerator());
                                     assertEquals(KeyEvent.VK_A, item2.getMnemonic());
                                     assertTrue(item2.getAction() instanceof org.siberia.bar.action.NullAction);
                                     break;
                            default : assertTrue(false);
                        }
                    }
                }
                else
                    assertTrue(false);
            }
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testMergeMenuType() throws Exception
    {
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {
            TypeMenu type_1 = jaxb.loadTypeMenu(new FileInputStream(DIR_PATH + File.separator + "typeMenu_1.xml"));
            TypeMenu type_2 = jaxb.loadTypeMenu(new FileInputStream(DIR_PATH + File.separator + "typeMenu_2.xml"));
            
            List<TypeMenu> types = new ArrayList<TypeMenu>();
            types.add(type_1);
            types.add(type_2);
            
            TypeMenu merged = new TypeMenuMerger(types).mergeType();
            
            Comparator comparator = new OrderedElementComparator();
            
            Collections.sort(merged.getItems().getMenuOrItemOrCheck(), comparator);
            
            Iterator<OrderedElement> elts = merged.getItems().getMenuOrItemOrCheck().iterator();
            int index = 0;
            
            while(elts.hasNext())
            {   OrderedElement elt = elts.next();
                
                switch(index)
                {
                    case 0 : assertTrue( elt instanceof CheckMenuItem );
                             CheckMenuItem check = (CheckMenuItem)elt;
                             assertEquals("Simple web browser", check.getLabel());
                             assertEquals("alt O", check.getShortcut());
                             assertEquals("B", check.getMnemonic());
                             assertEquals("org.siberia.ui.action.impl.WebBrowserEditingAction", check.getActionClass());
                             assertEquals(0, check.getParameter().size());
                             break;
                    case 1 : assertTrue( elt instanceof MenuItem );
                             MenuItem item = (MenuItem)elt;
                             assertEquals("Redo", item.getLabel());
                             assertEquals("control D", item.getShortcut());
                             assertEquals("R", item.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                             assertEquals(0, item.getParameter().size());
                             break;
                    case 2 : assertTrue( elt instanceof MenuItem );
                             MenuItem item2 = (MenuItem)elt;
                             assertEquals("Debug console", item2.getLabel());
                             assertEquals(null, item2.getShortcut());
                             assertEquals("G", item2.getMnemonic());
                             assertEquals("org.siberia.ui.action.impl.DebugSessionEditingAction", item2.getActionClass());
                             assertEquals(0, item2.getParameter().size());
                             break;
                    case 3 : assertTrue( elt instanceof MenuType );
                             MenuType menu = (MenuType)elt;
                             assertEquals("Edit", menu.getLabel());
                             
                             Collections.sort(menu.getMenuOrItemOrCheck(), comparator);
                             
                             Iterator<OrderedElement> it = menu.getMenuOrItemOrCheck().iterator();
                             int subIndex = 0;
                             while(it.hasNext())
                             {
                                 OrderedElement subElt = it.next();
                                 switch(subIndex)
                                 {  case 0 : assertTrue(subElt instanceof MenuItem);
                                             MenuItem subItem = (MenuItem)subElt;
                                             assertEquals("Undo", subItem.getLabel());
                                             assertEquals("control Z", subItem.getShortcut());
                                             assertEquals("U", subItem.getMnemonic());
                                             assertEquals("org.siberia.bar.action.NullAction", subItem.getActionClass());
                                             assertEquals("platform::img/Add.png", subItem.getIcon());
                                             assertEquals(0, subItem.getParameter().size());
                                             break;
                                    case 1 : assertTrue(subElt instanceof MenuItem);
                                             MenuItem subItem2 = (MenuItem)subElt;
                                             assertEquals("Redo", subItem2.getLabel());
                                             assertEquals("control U", subItem2.getShortcut());
                                             assertEquals("R", subItem2.getMnemonic());
                                             assertEquals("org.siberia.bar.action.NullAction", subItem2.getActionClass());
                                             assertNull(subItem2.getIcon());
                                             assertEquals(0, subItem2.getParameter().size());
                                             break;
                                    case 2 : assertTrue(subElt instanceof SeparatorElement);
                                             break;
                                    case 3 : assertTrue(subElt instanceof MenuItem);
                                             MenuItem subItem3 = (MenuItem)subElt;
                                             assertEquals("Select all", subItem3.getLabel());
                                             assertEquals("control A", subItem3.getShortcut());
                                             assertEquals("A", subItem3.getMnemonic());
                                             assertEquals("org.siberia.bar.action.NullAction", subItem3.getActionClass());
                                             assertNull(subItem3.getIcon());
                                             assertEquals(0, subItem3.getParameter().size());
                                             break;
                                    case 4 : assertTrue(subElt instanceof SeparatorElement);
                                             break;
                                    case 5 : assertTrue(subElt instanceof MenuItem);
                                             MenuItem subItem4 = (MenuItem)subElt;
                                             assertEquals("Properties", subItem4.getLabel());
                                             assertNull(subItem4.getShortcut());
                                             assertEquals("P", subItem4.getMnemonic());
                                             assertEquals("org.siberia.bar.action.NullAction", subItem4.getActionClass());
                                             assertNull(subItem4.getIcon());
                                             List<ParameterType> sublist = subItem4.getParameter();
                                             assertEquals(0, sublist.size());
                                             break;
                                     default : assertTrue(false);
                                 }
                                 
                                 subIndex ++;
                             }
                             break;
                    case 4 : assertTrue( elt instanceof MenuItem );
                             MenuItem item3 = (MenuItem)elt;
                             assertEquals("Options", item3.getLabel());
                             assertEquals(null, item3.getShortcut());
                             assertEquals("O", item3.getMnemonic());
                             assertEquals("org.siberia.ui.action.impl.PropertiesEditingAction", item3.getActionClass());
                             assertEquals(1, item3.getParameter().size());
                             assertEquals("id", item3.getParameter().get(0).getName());
                             assertEquals("0", item3.getParameter().get(0).getValue());
                             break;
                    case 5 : assertTrue( elt instanceof CheckMenuItem);
                             CheckMenuItem item4 = (CheckMenuItem)elt;
                             /* select all already define control A as shortcut and is placed before save as,
                              * so the shortcut will be inhibited
                              */
                             assertEquals("Save as", item4.getLabel());
                             assertEquals("", item4.getShortcut());
                             assertEquals("A", item4.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", item4.getActionClass());
                             assertNull(item4.getIcon());
                             assertEquals(0, item4.getParameter().size());
                             break;
                    case 6 : assertTrue(elt instanceof SeparatorElement);
                             break;
                    case 7 : assertTrue(elt instanceof MenuItem);
                             MenuItem subItem6 = (MenuItem)elt;
                             assertEquals("Print...", subItem6.getLabel());
                             /** typeMenu_2 is master, and one of its items define control Z as shortcut, so Print...
                              *  is not able to use it
                              */
                             assertEquals("", subItem6.getShortcut());
                             assertEquals("P", subItem6.getMnemonic());
                             assertEquals("org.siberia.bar.action.NullAction", subItem6.getActionClass());
                             assertNull(subItem6.getIcon());
                             assertEquals(0, subItem6.getParameter().size());
                             break;
                    default : assertTrue(false);
                }
                
                index++;
            }
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }

    public void testMergeMenubar() throws Exception
    {
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {
            Menubar bar_1 = jaxb.loadMenubar(new FileInputStream(DIR_PATH + File.separator + "menubar_1.xml"));
            Menubar bar_2 = jaxb.loadMenubar(new FileInputStream(DIR_PATH + File.separator + "menubar_2.xml"));
            
            List<Menubar> bars = new ArrayList<Menubar>();
            bars.add(bar_1);
            bars.add(bar_2);
            
            Menubar merged = new MenubarMerger(bars).merge();

            Comparator comparator = new OrderedElementComparator();
            /* parcours du menuType */
            List<MenuType> list = merged.getMenus().getMenu();
            Collections.sort(list, comparator);
            for(int i = 0; i < list.size(); i++)
            {   MenuType menu = list.get(i);
                if ( i == 0 )
                {   assertEquals("File", menu.getLabel());
                    assertEquals("F", menu.getMnemonic());
                    assertEquals(10, (int)menu.getOrder());
                    
                    List<OrderedElement> subElts = menu.getMenuOrItemOrCheck();
                    
                    Collections.sort(subElts, comparator);
                    
                    OrderedElement current = null;
                    Iterator<OrderedElement> it = subElts.iterator();
                    int index = 0;
                    while(it.hasNext())
                    {   current = it.next();
                        
                        if ( current instanceof MenuItem )
                        {   MenuItem item = (MenuItem)current;
                            switch(index)
                            {   case 0 : assertEquals("Save", item.getLabel());
                                         assertEquals(40, (int)item.getOrder());
                                         assertEquals("S", item.getMnemonic());
                                         assertEquals("control S", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         assertNull(item.getIcon());
                                         List<ParameterType> sublist = item.getParameter();
                                         assertEquals(1, sublist.size());
                                         ParameterType param = sublist.get(0);
                                         assertEquals("a", param.getName());
                                         assertEquals("fre", param.getValue());
                                         break;
                                case 1 : assertEquals("Save as", item.getLabel());
                                         assertEquals(50, (int)item.getOrder());
                                         assertEquals("A", item.getMnemonic());
                                         assertEquals("control A", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> list2 = item.getParameter();
                                         assertEquals(0, list2.size());
                                         break;
                                case 3 : assertEquals("Print...", item.getLabel());
                                         assertEquals(100, (int)item.getOrder());
                                         assertEquals("P", item.getMnemonic());
                                         assertEquals("control P", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> list3 = item.getParameter();
                                         assertEquals(0, list3.size());
                                         break;
                                case 5 : assertEquals("Exit", item.getLabel());
                                         assertEquals(200, (int)item.getOrder());
                                         assertEquals("X", item.getMnemonic());
                                         assertEquals("control Q", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> list4 = item.getParameter();
                                         assertEquals(0, list4.size());
                                         break;
                                default : assertTrue(false);
                            }
                        }
                        else if ( current instanceof SeparatorElement )
                        {   SeparatorElement separator = (SeparatorElement)current;
                            switch(index)
                            {   case 2 : assertEquals(60, (int)separator.getOrder());
                                         break;
                                case 4 : assertEquals(150, (int)separator.getOrder());
                                         break;
                                default : assertTrue(false);
                            }
                        }
                        
                        index++;
                    }
                }
                else if ( i == 1 )
                {   assertEquals("Edit", menu.getLabel());
                    assertEquals(20, (int)menu.getOrder());
                    
                    List<OrderedElement> subElts = menu.getMenuOrItemOrCheck();
                    
                    Collections.sort(subElts, comparator);
                    
                    OrderedElement current = null;
                    Iterator<OrderedElement> it = subElts.iterator();
                    int index = 0;
                    while(it.hasNext())
                    {   current = it.next();
                    
                        if ( current instanceof MenuItem )
                        {   MenuItem item = (MenuItem)current;
                            switch(index)
                            {   case 0 : assertEquals("Undo", item.getLabel());
                                         assertEquals(10, (int)item.getOrder());
                                         assertEquals("U", item.getMnemonic());
                                         assertEquals("platform::img/Add.png", item.getIcon());
                                         assertEquals("control Z", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> sublist = item.getParameter();
                                         assertEquals(0, sublist.size());
                                         break;
                                case 1 : assertEquals("Redo", item.getLabel());
                                         assertEquals(11, (int)item.getOrder());
                                         assertEquals("R", item.getMnemonic());
                                         assertEquals("control U", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> sublist1 = item.getParameter();
                                         assertEquals(0, sublist1.size());
                                         break;
                                case 3 : assertEquals("Select all", item.getLabel());
                                         assertEquals(80, (int)item.getOrder());
                                         assertEquals("A", item.getMnemonic());
                                         assertEquals("control A", item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> sublist2 = item.getParameter();
                                         assertEquals(0, sublist2.size());
                                         break;
                                case 5 : assertEquals("Properties", item.getLabel());
                                         assertEquals(130, (int)item.getOrder());
                                         assertEquals("P", item.getMnemonic());
                                         assertNull(item.getShortcut());
                                         assertEquals("org.siberia.bar.action.NullAction", item.getActionClass());
                                         List<ParameterType> sublist3 = item.getParameter();
                                         assertEquals(0, sublist3.size());
                                         break;
                                default : assertTrue(false);
                            }
                        }
                        else if ( current instanceof SeparatorElement )
                        {   SeparatorElement separator = (SeparatorElement)current;
                            switch(index)
                            {   case 2 : assertEquals(30, (int)separator.getOrder());
                                         break;
                                case 4 : assertEquals(90, (int)separator.getOrder());
                                         break;
                                default : assertTrue(false);
                            }
                        }
                        
                        index++;
                    }
                }
                else if ( i == 2 )
                {   assertEquals("Tools", menu.getLabel());
                    assertEquals("T", menu.getMnemonic());
                    assertEquals(100, (int)menu.getOrder());
                    
                    List<OrderedElement> subElts = menu.getMenuOrItemOrCheck();
                    
                    Collections.sort(subElts, comparator);
                    
                    OrderedElement current = null;
                    Iterator<OrderedElement> it = subElts.iterator();
                    int index = 0;
                    while(it.hasNext())
                    {   current = it.next();
                    
                        if ( current instanceof MenuItem )
                        {   MenuItem item = (MenuItem)current;
                            switch(index)
                            {   case 0 : assertEquals("Simple web browser", item.getLabel());
                                         assertEquals(10, (int)item.getOrder());
                                         assertEquals("B", item.getMnemonic());
                                         assertEquals("alt O", item.getShortcut());
                                         assertEquals("org.siberia.ui.action.impl.WebBrowserEditingAction", item.getActionClass());
                                         List<ParameterType> sublist = item.getParameter();
                                         assertEquals(0, sublist.size());
                                         break;
                                case 1 : assertEquals("Debug console", item.getLabel());
                                         assertEquals(20, (int)item.getOrder());
                                         assertEquals("G", item.getMnemonic());
                                         assertNull(item.getShortcut());
                                         assertEquals("org.siberia.ui.action.impl.DebugSessionEditingAction", item.getActionClass());
                                         List<ParameterType> sublist1 = item.getParameter();
                                         assertEquals(0, sublist1.size());
                                         break;
                                case 2 : assertEquals("Options", item.getLabel());
                                         assertEquals(30, (int)item.getOrder());
                                         assertEquals("O", item.getMnemonic());
                                         assertNull(item.getShortcut());
                                         assertEquals("org.siberia.ui.action.impl.PropertiesEditingAction", item.getActionClass());
                                         List<ParameterType> sublist2 = item.getParameter();
                                         assertEquals(1, sublist2.size());
                                         ParameterType param = sublist2.get(0);
                                         assertEquals("id", param.getName());
                                         assertEquals("0", param.getValue());
                                         break;
                                default : assertTrue(false);
                            }
                        }
                        
                        index ++;
                    }
                }
                else
                    assertFalse("more than 3 menus", true);
            }
        }
        catch (FileNotFoundException ex)
        {   ex.printStackTrace(); }
        catch (JAXBException ex)
        {   ex.printStackTrace(); }
    }
    
    public void testCreatePopup() throws Exception
    {
	JPopupMenu popup = new BarFactory().createContextMenu(new FileBarProvider(
		new File(DIR_PATH + File.separator + "typeMenu_3.xml")));

	assertEquals(3, popup.getComponentCount());

	for(int i = 0; i < popup.getComponentCount(); i++)
	{
	    Component c = popup.getComponent(i);

	    if ( i == 0 )
	    {
		if ( c instanceof JMenu )
		{
		    JMenu menu = (JMenu)c;

		    assertEquals("menuA", menu.getText());

		    assertEquals(1, menu.getMenuComponentCount());

		    assertTrue( (menu.getMenuComponent(0) instanceof JMenuItem) );
		    assertEquals("itemA1", ((JMenuItem)menu.getMenuComponent(0)).getText());
		    assertTrue( ( ((JMenuItem)menu.getMenuComponent(0)).getAction() instanceof NullAction ) );
		}
		else
		{
		    assertTrue(false);
		}
	    }
	    else if ( i == 1 )
	    {
		if ( c instanceof JMenuItem )
		{
		    assertEquals("item2", ((JMenuItem)c).getText());
		    assertTrue( ( ((JMenuItem)c).getAction() instanceof NullAction ) );
		}
		else
		{
		    assertTrue(false);
		}
	    }
	    else if ( i == 2 )
	    {
		if ( c instanceof JPopupMenu.Separator )
		{
		    assertTrue(true);
		}
		else
		{
		    assertTrue(false);
		}
	    }
	    else
	    {
		assertTrue(false);
	    }
	}
    }
}
