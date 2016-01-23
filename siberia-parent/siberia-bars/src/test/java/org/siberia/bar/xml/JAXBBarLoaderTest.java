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
package org.siberia.bar.xml;

import java.io.File;
import java.io.FileInputStream;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
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
import org.siberia.xml.schema.bar.I18NResources;
import org.siberia.xml.schema.bar.I18NResource;


/**
 *
 * @author alexis
 */
public class JAXBBarLoaderTest extends TestCase
{   
    /** path where are stored the xml tests declaration files */
    private static final String DIR_PATH = new String("src.test.java.org.siberia.bar.rc").replace('.', File.separatorChar);
    
    public JAXBBarLoaderTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(JAXBBarLoaderTest.class);
        return suite;
    }
    
    public void testLoadMenubar() throws Exception
    {   String[] files = new String[]{"menubar_1.xml", "menubar_2.xml"};
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        for(int i = 0; i < files.length; i++)
        {   try
            {   Menubar bar = jaxb.loadMenubar(new FileInputStream(DIR_PATH + File.separator + files[i]));
                assertTrue(true);
		
		if ( i == 0 )
		{
		    I18NResources res = bar.getI18NResources();
		    
		    assertEquals(1, res.getI18NResource().size());
		}
		else if ( i == 1 )
		{
		    I18NResources res = bar.getI18NResources();
		    
		    assertEquals(2, res.getI18NResource().size());
		}
            }
            catch(Exception e)
            {   e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    public void testLoadToolBar() throws Exception
    {   String[] files = new String[]{"toolbar_1.xml", "toolbar_2.xml", "toolbar_3.xml"};
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        for(int i = 0; i < files.length; i++)
        {   try
            {   jaxb.loadToolbar(new FileInputStream(DIR_PATH + File.separator + files[i]));
                assertTrue(true);
            }
            catch(Exception e)
            {   e.printStackTrace();
                assertTrue(false);
            }
        }
    }

    public void testLoadTypeMenu() throws Exception
    {   String[] files = new String[]{"typeMenu_1.xml", "typeMenu_2.xml"};
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        for(int i = 0; i < files.length; i++)
        {   try
            {   jaxb.loadTypeMenu(new FileInputStream(DIR_PATH + File.separator + files[i]));
                assertTrue(true);
            }
            catch(Exception e)
            {   e.printStackTrace();
                assertTrue(false);
            }
        }
    }
    
    public void testSaveMenubar() throws Exception
    {   ObjectFactory factory = new ObjectFactory();
        
        Menubar bar = null;
        File f = null;
        JAXBBarLoader jaxb = new JAXBBarLoader();
        
        bar = factory.createMenubar();
        f = new File(DIR_PATH + File.separator + "gen" + File.separator + "menubar_1.xml");
        try
        {   jaxb.saveMenubar(bar, f);
            assertTrue(true);
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }
    
    public void testSaveToolbar() throws Exception
    {   ObjectFactory factory = new ObjectFactory();
        
        Toolbar bar = null;
        File f = null;
        
        bar = factory.createToolbar();
        f = new File(DIR_PATH + File.separator + "gen" + File.separator + "toolbar_1.xml");
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {   jaxb.saveToolbar(bar, f);
            assertTrue(true);
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }
    
    public void testSaveTypeMenubar() throws Exception
    {   ObjectFactory factory = new ObjectFactory();
        
        TypeMenu menu = null;
        File f = null;
        
        menu = factory.createTypeMenu();
        f = new File(DIR_PATH + File.separator + "gen" + File.separator + "typemenu_1.xml");
        
        JAXBBarLoader jaxb = new JAXBBarLoader();
        try
        {   jaxb.saveTypeMenu(menu, f);
            assertTrue(true);
        }
        catch(Exception e)
        {   e.printStackTrace();
            assertTrue(false);
        }
    }
    
}
