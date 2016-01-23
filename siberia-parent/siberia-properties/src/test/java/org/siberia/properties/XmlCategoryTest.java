/*
 * ColdXmlCategoryTest.java
 * JUnit based test
 *
 * Created on 30 aout 2006, 21:32
 */

package org.siberia.properties;

import junit.framework.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.siberia.xml.schema.properties.NatureType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;

/**
 *
 * @author alexis
 */
public class XmlCategoryTest extends TestCase
{
    
    public XmlCategoryTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(XmlCategoryTest.class);
        
        return suite;
    }

    public void testGetInnerCategory()
    {   
        XmlCategory instance = null;
        CategoryType xmlCat = null;
        CategoryType result = null;
        
        ObjectFactory factory = new ObjectFactory();
        
        xmlCat = factory.createCategoryType();
        xmlCat.setLabel("label");
        xmlCat.setOrder(10);
        xmlCat.setIcon("/icon.ico");
        xmlCat.setDescription("a simple properties container");
        instance = new XmlCategory(xmlCat);
        result = instance.getInnerCategory();
        assertEquals("label", result.getLabel());
        assertEquals(10, result.getOrder());
        assertEquals("/icon.ico", result.getIcon());
        assertEquals("a simple properties container", result.getDescription());
        
        xmlCat = factory.createCategoryType();
        xmlCat.setLabel("label_2");
        xmlCat.setOrder(20);
        xmlCat.setIcon("/icon_2.ico");
        xmlCat.setDescription("another properties container");
        instance = new XmlCategory(xmlCat);
        result = instance.getInnerCategory();
        assertEquals("label_2", result.getLabel());
        assertEquals(20, result.getOrder());
        assertEquals("/icon_2.ico", result.getIcon());
        assertEquals("another properties container", result.getDescription());
        
    }

    public void testGetPropertyContainer()
    {   /* done */ }

    public void testGetItems()
    {
        XmlCategory instance = null;
        CategoryType xmlCat = null;
        CategoryType xmlSubCat = null;
        CategoryType result = null;
        PropertyType property_1 = null;
        PropertyType property_2 = null;
        
        ObjectFactory factory = new ObjectFactory();
        
        xmlCat = factory.createCategoryType();
        xmlCat.setLabel("label");
        xmlCat.setOrder(10);
        xmlCat.setIcon("/icon.ico");
        xmlCat.setDescription("a simple properties container");
        
        xmlSubCat = factory.createCategoryType();
        xmlSubCat.setLabel("sublabel");
        xmlSubCat.setOrder(40);
        xmlSubCat.setIcon("/subicon.ico");
        xmlSubCat.setDescription("a simple sub properties container");
        
        property_1 = factory.createPropertyType();
        property_1.setAppliedValue(new PropertyType.AppliedValue());
        property_1.setApplyAction("apply_1");
        property_1.setDependAction("depend_action_1");
        property_1.setId(10);
        property_1.setNature(NatureType.STRING);
        property_1.setPattern(factory.createPatternType());
        property_1.setRepr("repr_1");
        property_1.setLabel("label_1");
        property_1.setDescription("desc_1");
        property_1.setIcon("icon_1.ico");
        
        property_2 = factory.createPropertyType();
        property_2.setAppliedValue(null);
        property_2.setApplyAction("apply_2");
        property_2.setDependAction("depend_action_2");
        property_2.setId(30);
        property_2.setNature(NatureType.BOOLEAN);
        property_2.setRange(factory.createRangeType());
        property_2.setRepr("repr_2");
        property_2.setLabel("label_2");
        property_2.setDescription("desc_2");
        property_2.setIcon("icon_2.ico");
        
        xmlCat.getPropertyAndCategory().add(xmlSubCat);
        xmlCat.getPropertyAndCategory().add(property_1);
        xmlCat.getPropertyAndCategory().add(property_2);
        
        instance = new XmlCategory(xmlCat);
        
        Iterator<XmlPropertyContainer> it = instance.getItems().iterator();
        boolean property_1_found = false;
        boolean property_2_found = false;
        boolean sub_1_found      = false;
        while(it.hasNext())
        {   PropertyContainer container = it.next().getPropertyContainer();
            
            if ( container.getLabel().equals("label_1") )
            {   property_1_found = true;
                PropertyType property = (PropertyType)container;
                
                assertTrue(property.getAppliedValue() instanceof PropertyType.AppliedValue);
                assertEquals("apply_1", property.getApplyAction());
                assertEquals("depend_action_1", property.getDependAction());
                assertEquals(10, property.getId().intValue());
                assertEquals(NatureType.STRING, property.getNature());
                assertNotNull(property.getPattern());
                assertNull(property.getValues());
                assertNull(property.getRange());
                assertEquals("repr_1", property.getRepr());
                assertEquals("desc_1", property.getDescription());
                assertEquals("icon_1.ico", property.getIcon());
            }
            else if ( container.getLabel().equals("label_2") )
            {   property_2_found = true;
                PropertyType property = (PropertyType)container;
                
                assertEquals("apply_2", property.getApplyAction());
                assertEquals("depend_action_2", property.getDependAction());
                assertEquals(30, property.getId().intValue());
                assertEquals(NatureType.BOOLEAN, property.getNature());
                assertNull(property.getPattern());
                assertNull(property.getValues());
                assertNotNull(property.getRange());
                assertEquals("repr_2", property.getRepr());
                assertEquals("desc_2", property.getDescription());
                assertEquals("icon_2.ico", property.getIcon());
            }
            else
            {   CategoryType cat = (CategoryType)container;
                
                sub_1_found = true;
                
                assertEquals("sublabel", cat.getLabel());
                assertEquals(40, cat.getOrder());
                assertEquals("/subicon.ico", cat.getIcon());
                assertEquals("a simple sub properties container", cat.getDescription());
            }
        }
        
        if ( ! property_1_found )
            assertTrue(false);
        if ( ! property_2_found )
            assertTrue(false);
        if ( ! sub_1_found )
            assertTrue(false);
    }

    public void testRecursiveProperties()
    {
        XmlCategory instance = null;
        CategoryType xmlCat = null;
        CategoryType xmlSubCat = null;
        CategoryType result = null;
        PropertyType property_1 = null;
        PropertyType property_2 = null;
        PropertyType property_1_1 = null;
        
        ObjectFactory factory = new ObjectFactory();
        
        xmlCat = factory.createCategoryType();
        xmlCat.setLabel("label");
        
        xmlSubCat = factory.createCategoryType();
        xmlSubCat.setLabel("sublabel");
        
        property_1 = factory.createPropertyType();
        property_1.setLabel("label_1");
        
        property_2 = factory.createPropertyType();
        property_2.setLabel("label_2");
        
        property_1_1 = factory.createPropertyType();
        property_1_1.setLabel("label_1_1");
        
        xmlSubCat.getPropertyAndCategory().add(property_1_1);
        xmlCat.getPropertyAndCategory().add(xmlSubCat);
        xmlCat.getPropertyAndCategory().add(property_1);
        xmlCat.getPropertyAndCategory().add(property_2);
        
        instance = new XmlCategory(xmlCat);
        
        Iterator<XmlProperty> it = instance.recursiveProperties().iterator();
        boolean property_1_found = false;
        boolean property_1_1_found = false;
        boolean property_2_found = false;
        while(it.hasNext())
        {   PropertyContainer container = it.next().getPropertyContainer();
            
            if ( container.getLabel().equals("label_1") )
            {   property_1_found = true; }
            else if ( container.getLabel().equals("label_2") )
            {   property_2_found = true; }
            else if ( container.getLabel().equals("label_1_1") )
            {   property_1_1_found = true; }
        }
        
        if ( ! property_1_found )
            assertTrue(false);
        if ( ! property_2_found )
            assertTrue(false);
        if ( ! property_1_1_found )
            assertTrue(false);
    }
    
}
