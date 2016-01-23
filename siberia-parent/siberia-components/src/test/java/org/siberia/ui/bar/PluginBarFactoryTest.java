package org.siberia.ui.bar;

import junit.framework.*;
//import org.siberia.xml.schema.bar.MenuType;
//import org.siberia.xml.schema.bar.TypeMenu;
//import org.siberia.xml.schema.bar.Menubar;
//import org.siberia.xml.schema.bar.OrderedElement;
//import org.siberia.xml.schema.bar.MenuItem;
//import org.siberia.xml.schema.bar.ObjectFactory;
//import org.siberia.xml.schema.bar.SeparatorElement;
//import org.siberia.xml.schema.bar.ParameterType;
//import org.siberia.xml.schema.bar.CheckType;
//import org.siberia.xml.schema.bar.CheckMenuItem;
//import org.siberia.xml.schema.bar.Toolbar;
//import org.siberia.xml.schema.bar.ButtonType;
//import org.siberia.xml.schema.bar.ComboType;
//import org.siberia.xml.schema.bar.SeparatorSizedElement;

/**
 *
 * @author alexis
 */
public class PluginBarFactoryTest extends TestCase
{   
    public PluginBarFactoryTest(String testName)
    {   super(testName); }

    protected void setUp() throws Exception
    {   }

    protected void tearDown() throws Exception
    {   }

    public static Test suite()
    {   TestSuite suite = new TestSuite(PluginBarFactoryTest.class);
        return suite;
    }

    public void testGetBaseClass() throws Exception
    {   
        Object[] objects = null;
       assertEquals(null, PluginBarFactory.getInstance().getBaseClass(objects));
       
       objects = new Object[]{null};
       assertEquals(null, PluginBarFactory.getInstance().getBaseClass(objects));
       
       objects = new Object[]{new java.util.ArrayList(), new java.util.HashSet()};
       assertEquals(java.util.AbstractCollection.class, PluginBarFactory.getInstance().getBaseClass(objects));
    }
    
}
