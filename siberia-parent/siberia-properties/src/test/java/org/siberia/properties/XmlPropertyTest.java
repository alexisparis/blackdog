/*
 * ColdXmlPropertyTest.java
 * JUnit based test
 *
 * Created on 30 ao�t 2006, 22:14
 */

package org.siberia.properties;

import junit.framework.*;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.siberia.parser.NoParserException;
import org.siberia.xml.schema.properties.NatureType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;

/**
 *
 * @author alexis
 */
public class XmlPropertyTest extends TestCase
{
    
    public XmlPropertyTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(XmlPropertyTest.class);
        
        return suite;
    }

    public void testAddDependingProperty()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        PropertyType xmlMasterProperty = null;
        XmlProperty masterProperty = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        property = new XmlProperty(xmlProperty);
        
        
        xmlMasterProperty = factory.createPropertyType();
        xmlMasterProperty.setId(20);
        masterProperty = new XmlProperty(xmlMasterProperty);
        
        property.addDependingProperty(masterProperty);
        
        List<Long> ids = property.getMasterPropertiesId();
        
//        assertEquals(1, ids.size());
//        assertEquals(masterProperty, ids.get(0));
    }

    public void testGetInnerProperty()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        PropertyType xmlMasterProperty = null;
        XmlProperty masterProperty = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setAppliedValue(new PropertyType.AppliedValue());
        xmlProperty.setApplyAction("apply_1");
        xmlProperty.setDependAction("depend_action_1");
        xmlProperty.setId(10);
        xmlProperty.setNature(NatureType.STRING);
        xmlProperty.setPattern(factory.createPatternType());
        xmlProperty.setRepr("repr_1");
        xmlProperty.setLabel("label_1");
        xmlProperty.setDescription("desc_1");
        xmlProperty.setIcon("icon_1.ico");
        property = new XmlProperty(xmlProperty);
        
        assertTrue(property.getInnerProperty().getAppliedValue() != null);
        assertEquals("apply_1", property.getInnerProperty().getApplyAction());
        assertEquals("depend_action_1", property.getInnerProperty().getDependAction());
        assertEquals(10, property.getInnerProperty().getId().intValue());
        assertEquals(NatureType.STRING, property.getInnerProperty().getNature());
        assertNotNull(property.getInnerProperty().getPattern());
        assertNull(property.getInnerProperty().getValues());
        assertNull(property.getInnerProperty().getRange());
        assertEquals("repr_1", property.getInnerProperty().getRepr());
        assertEquals("label_1", property.getInnerProperty().getLabel());
        assertEquals("desc_1", property.getInnerProperty().getDescription());
        assertEquals("icon_1.ico", property.getInnerProperty().getIcon());
    }

    public void testGetPropertyContainer()
    {   /* done */ }

    public void testIsDependingOnOthersProperties()
    {   
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        PropertyType xmlMasterProperty = null;
        XmlProperty masterProperty = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        property = new XmlProperty(xmlProperty);
        
        
        xmlMasterProperty = factory.createPropertyType();
        xmlMasterProperty.setId(20);
        masterProperty = new XmlProperty(xmlMasterProperty);
        
        assertFalse(property.isDependingOnOthersProperties());
        
        property.addDependingProperty(masterProperty);
        
//        assertTrue(property.isDependingOnOthersProperties());
    }

    public void testGetMasterPropertiesId()
    {   /* done */ }

    public void testGetId()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        property = new XmlProperty(xmlProperty);
        
        assertEquals(10, property.getId());
        
        xmlProperty.setId(20);
        
        assertEquals(20, property.getId());
    }

    public void testGetRepr()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        xmlProperty.setRepr("repr");
        property = new XmlProperty(xmlProperty);
        
        assertEquals("repr", property.getRepr());
        
        xmlProperty.setRepr("repr_1");
        
        assertEquals("repr_1", property.getRepr());
    }

    public void testGetNature()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        xmlProperty.setNature(NatureType.FLOAT);
        property = new XmlProperty(xmlProperty);
        
        assertEquals(NatureType.FLOAT.value(), property.getNature());
        
        xmlProperty.setNature(NatureType.STRING);
        
        assertEquals(NatureType.STRING.value(), property.getNature());
    }

    public void testGetDefaultValue()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        xmlProperty.setId(10);
        xmlProperty.setDefault("a");
        property = new XmlProperty(xmlProperty);
        
        assertEquals("a", property.getDefaultValue());
        
        xmlProperty.setDefault("b");
        
        assertEquals("b", property.getDefaultValue());
    }

    public void testGetCurrentValue()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        PropertyType.AppliedValue value = new PropertyType.AppliedValue();
        value.setValue("a");
        xmlProperty.setId(10);
        xmlProperty.setAppliedValue(value);
        property = new XmlProperty(xmlProperty);
        
        assertEquals("a", property.getCurrentValue());
        
        value.setValue("b");
        
        assertEquals("b", property.getCurrentValue());
    }

    public void testSetValue()
    {
        PropertyType xmlProperty = null;
        XmlProperty property = null;
        ObjectFactory factory = new ObjectFactory();
        
        xmlProperty = factory.createPropertyType();
        PropertyType.AppliedValue value = new PropertyType.AppliedValue();
        value.setValue("a");
        xmlProperty.setId(10);
        xmlProperty.setAppliedValue(value);
        property = new XmlProperty(xmlProperty);
        try
        {
            
            property.setValue("b");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        assertEquals("b", value.getValue());
        try
        {
            
            property.setValue("c");
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        assertEquals("c", value.getValue());
    }
    
}
