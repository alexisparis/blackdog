/*
 * PropertyValueValidatorTest.java
 * JUnit based test
 *
 * Created on 5 septembre 2006, 22:05
 */

package org.siberia.properties.util;

import junit.framework.*;
import org.siberia.properties.*;
import org.siberia.xml.schema.properties.Borne;
import org.siberia.xml.schema.properties.PatternType;
import org.siberia.xml.schema.properties.RangeType;
import org.siberia.xml.schema.properties.ExtendedRangeType;
import org.siberia.xml.schema.properties.ValueType;
import org.siberia.xml.schema.properties.Values;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.NatureType;
import org.siberia.xml.schema.properties.MinimumType;
import org.siberia.xml.schema.properties.MaximumType;

/**
 *
 * @author alexis
 */
public class PropertyValueValidatorTest extends TestCase
{
    
    public PropertyValueValidatorTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PropertyValueValidatorTest.class);
        
        return suite;
    }
    
    public void testIsValidAccordingToNature()
    {
        org.siberia.xml.schema.properties.ObjectFactory factory = 
                            new org.siberia.xml.schema.properties.ObjectFactory();
        PropertyType type = factory.createPropertyType();
        XmlProperty property = new XmlProperty(type);
        
        type.setNature(NatureType.STRING);
        assertTrue(PropertyValueValidator.isValidAccordingToNature("", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("a", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature(" ", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("boolean", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("1", property));
        
        type.setNature(NatureType.FLOAT);
        assertFalse(PropertyValueValidator.isValidAccordingToNature("", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("a", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature(" ", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("boolean", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("1", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("1.37", property));
        
        type.setNature(NatureType.INTEGER);
        assertFalse(PropertyValueValidator.isValidAccordingToNature("", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("a", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature(" ", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("boolean", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("1", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("1.37", property));
        
        type.setNature(NatureType.BOOLEAN);
        assertFalse(PropertyValueValidator.isValidAccordingToNature("", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("a", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature(" ", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("true", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("TRUE", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("false", property));
        assertTrue(PropertyValueValidator.isValidAccordingToNature("FALSE", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("1", property));
        assertFalse(PropertyValueValidator.isValidAccordingToNature("1.37", property));
    }
    
    public void testIsValidWithValues()
    {
        org.siberia.xml.schema.properties.ObjectFactory factory = 
                            new org.siberia.xml.schema.properties.ObjectFactory();
        PropertyType type = factory.createPropertyType();
        XmlProperty property = new XmlProperty(type);
        
        type.setNature(NatureType.STRING);
        
        ValueType value = null;
        Values values = factory.createValues();
        
        value = factory.createValueType();
        value.setRepr("a");
        values.getValue().add(value);
        value = factory.createValueType();
        value.setRepr("b");
        values.getValue().add(value);
        
        type.setValues(values);
        
        assertFalse(PropertyValueValidator.isValid("e", property, values));
        assertFalse(PropertyValueValidator.isValid("", property, values));
        assertTrue(PropertyValueValidator.isValid("a", property, values));
        assertTrue(PropertyValueValidator.isValid("b", property, values));
        
        type.setNature(NatureType.FLOAT);
        
        values.getValue().clear();
        
        value = factory.createValueType();
        value.setRepr("1");
        values.getValue().add(value);
        value = factory.createValueType();
        value.setRepr("1.568");
        values.getValue().add(value);
        
        assertTrue(PropertyValueValidator.isValid("1", property, values));
        assertTrue(PropertyValueValidator.isValid("1.568", property, values));
        assertFalse(PropertyValueValidator.isValid("a", property, values));
        assertFalse(PropertyValueValidator.isValid("b", property, values));
    }
    
    public void testIsValidWithRange()
    {
        org.siberia.xml.schema.properties.ObjectFactory factory = 
                            new org.siberia.xml.schema.properties.ObjectFactory();
        PropertyType type = factory.createPropertyType();
        XmlProperty property = new XmlProperty(type);
        RangeType range = factory.createRangeType();
        MinimumType min = factory.createMinimumType();
        MaximumType max = factory.createMaximumType();
        
        range.setMaximum(max);
        range.setMinimum(min);
        
        type.setRange(range);
        
        type.setNature(NatureType.STRING);
        
        // ["abcdef", "kkkk"[
        min.setInclude(true);
        min.setRepr("abcdef");
        max.setInclude(false);
        max.setRepr("kkkk");
        
        assertTrue(PropertyValueValidator.isValid("abcdef", property, range));
        assertFalse(PropertyValueValidator.isValid("abcdee", property, range));
        assertTrue(PropertyValueValidator.isValid("bbb", property, range));
        assertTrue(PropertyValueValidator.isValid("c", property, range));
        assertTrue(PropertyValueValidator.isValid("k", property, range));
        assertTrue(PropertyValueValidator.isValid("kkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("m", property, range));
        
        // ["abcdef", "kkkk"]
        max.setInclude(true);
        
        assertTrue(PropertyValueValidator.isValid("abcdef", property, range));
        assertFalse(PropertyValueValidator.isValid("abcdee", property, range));
        assertTrue(PropertyValueValidator.isValid("bbb", property, range));
        assertTrue(PropertyValueValidator.isValid("c", property, range));
        assertTrue(PropertyValueValidator.isValid("k", property, range));
        assertTrue(PropertyValueValidator.isValid("kkk", property, range));
        assertTrue(PropertyValueValidator.isValid("kkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("m", property, range));
        
        // ["abcdef", "kkkk"[
        min.setInclude(true);
        min.setRepr("kkk");
        max.setInclude(false);
        max.setRepr("aaa");
        
        assertFalse(PropertyValueValidator.isValid("abcdef", property, range));
        assertFalse(PropertyValueValidator.isValid("abcdee", property, range));
        assertFalse(PropertyValueValidator.isValid("bbb", property, range));
        assertFalse(PropertyValueValidator.isValid("c", property, range));
        assertFalse(PropertyValueValidator.isValid("k", property, range));
        assertFalse(PropertyValueValidator.isValid("kkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("m", property, range));
        
        type.setNature(NatureType.INTEGER);
        
        // [5, 15[
        min.setInclude(true);
        min.setRepr("5");
        max.setInclude(false);
        max.setRepr("15");
        
        assertFalse(PropertyValueValidator.isValid("0", property, range));
        assertFalse(PropertyValueValidator.isValid("2", property, range));
        assertTrue(PropertyValueValidator.isValid("5", property, range));
        assertTrue(PropertyValueValidator.isValid("6", property, range));
        assertTrue(PropertyValueValidator.isValid("10", property, range));
        assertTrue(PropertyValueValidator.isValid("12", property, range));
        assertTrue(PropertyValueValidator.isValid("14", property, range));
        assertFalse(PropertyValueValidator.isValid("15", property, range));
        assertFalse(PropertyValueValidator.isValid("16", property, range));
        assertFalse(PropertyValueValidator.isValid("1025", property, range));
        
        // ]5, 15]
        min.setInclude(false);
        max.setInclude(true);
        
        assertFalse(PropertyValueValidator.isValid("0", property, range));
        assertFalse(PropertyValueValidator.isValid("2", property, range));
        assertFalse(PropertyValueValidator.isValid("5", property, range));
        assertTrue(PropertyValueValidator.isValid("6", property, range));
        assertTrue(PropertyValueValidator.isValid("10", property, range));
        assertTrue(PropertyValueValidator.isValid("12", property, range));
        assertTrue(PropertyValueValidator.isValid("14", property, range));
        assertTrue(PropertyValueValidator.isValid("15", property, range));
        assertFalse(PropertyValueValidator.isValid("16", property, range));
        assertFalse(PropertyValueValidator.isValid("1025", property, range));
        
        type.setNature(NatureType.FLOAT);
        
        // [5.02, 15.005[
        min.setInclude(true);
        min.setRepr("5.02");
        max.setInclude(false);
        max.setRepr("15.005");
        
        assertFalse(PropertyValueValidator.isValid("0", property, range));
        assertFalse(PropertyValueValidator.isValid("2", property, range));
        assertFalse(PropertyValueValidator.isValid("5.01", property, range));
        assertTrue(PropertyValueValidator.isValid("5.02", property, range));
        assertTrue(PropertyValueValidator.isValid("6", property, range));
        assertTrue(PropertyValueValidator.isValid("10", property, range));
        assertTrue(PropertyValueValidator.isValid("12", property, range));
        assertTrue(PropertyValueValidator.isValid("14", property, range));
        assertTrue(PropertyValueValidator.isValid("15", property, range));
        assertTrue(PropertyValueValidator.isValid("15.0049", property, range));
        assertFalse(PropertyValueValidator.isValid("15.005", property, range));
        assertFalse(PropertyValueValidator.isValid("16", property, range));
        assertFalse(PropertyValueValidator.isValid("1025", property, range));
    }
    
    public void testIsValidWithExtendedRange()
    {
        org.siberia.xml.schema.properties.ObjectFactory factory = 
                            new org.siberia.xml.schema.properties.ObjectFactory();
        PropertyType type = factory.createPropertyType();
        XmlProperty property = new XmlProperty(type);
        ExtendedRangeType range = factory.createExtendedRangeType();
        RangeType excludedRange = factory.createRangeType();
        MinimumType min = factory.createMinimumType();
        MaximumType max = factory.createMaximumType();
        MinimumType minExcluded = factory.createMinimumType();
        MaximumType maxExcluded = factory.createMaximumType();
        
        excludedRange.setMaximum(maxExcluded);
        excludedRange.setMinimum(minExcluded);
        
        range.getExcluded().add(excludedRange);
        
        range.setMaximum(max);
        range.setMinimum(min);
        
        type.setRange(range);
        
        type.setNature(NatureType.STRING);
        
        // ["abcdef", "kkkk"[ but not in ["e", "f"]
        min.setInclude(true);
        min.setRepr("abcdef");
        max.setInclude(false);
        max.setRepr("kkkk");
        minExcluded.setInclude(true);
        minExcluded.setRepr("e");
        maxExcluded.setInclude(true);
        maxExcluded.setRepr("f");
        
        assertTrue(PropertyValueValidator.isValid("abcdef", property, range));
        assertTrue(PropertyValueValidator.isValid("d", property, range));
        assertFalse(PropertyValueValidator.isValid("e", property, range));
        assertFalse(PropertyValueValidator.isValid("ea", property, range));
        assertFalse(PropertyValueValidator.isValid("f", property, range));
        assertTrue(PropertyValueValidator.isValid("fa", property, range));
        assertTrue(PropertyValueValidator.isValid("c", property, range));
        assertTrue(PropertyValueValidator.isValid("k", property, range));
        assertTrue(PropertyValueValidator.isValid("kkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("kkkkk", property, range));
        assertFalse(PropertyValueValidator.isValid("m", property, range));
        
        type.setNature(NatureType.INTEGER);
        
        // [2, 15[ but not in [6, 8[
        min.setInclude(true);
        min.setRepr("2");
        max.setInclude(false);
        max.setRepr("15");
        minExcluded.setInclude(true);
        minExcluded.setRepr("6");
        maxExcluded.setInclude(false);
        maxExcluded.setRepr("8");
        
        assertFalse(PropertyValueValidator.isValid("0", property, range));
        assertTrue(PropertyValueValidator.isValid("2", property, range));
        assertTrue(PropertyValueValidator.isValid("5", property, range));
        assertFalse(PropertyValueValidator.isValid("6", property, range));
        assertFalse(PropertyValueValidator.isValid("7", property, range));
        assertTrue(PropertyValueValidator.isValid("8", property, range));
        assertTrue(PropertyValueValidator.isValid("9", property, range));
        assertTrue(PropertyValueValidator.isValid("14", property, range));
        assertFalse(PropertyValueValidator.isValid("15", property, range));
        assertFalse(PropertyValueValidator.isValid("20", property, range));
        assertFalse(PropertyValueValidator.isValid("254", property, range));
    }
    
    public void testIsValidWithPattern()
    {
        org.siberia.xml.schema.properties.ObjectFactory factory = 
                            new org.siberia.xml.schema.properties.ObjectFactory();
        PropertyType type = factory.createPropertyType();
        XmlProperty property = new XmlProperty(type);
        PatternType pattern = factory.createPatternType();
        
        pattern.setValue("a*b");
        type.setNature(NatureType.STRING);
        type.setPattern(pattern);
        
        assertTrue(PropertyValueValidator.isValid("ab", property, pattern));
        assertTrue(PropertyValueValidator.isValid("aab", property, pattern));
        assertFalse(PropertyValueValidator.isValid("dd", property, pattern));
        assertFalse(PropertyValueValidator.isValid("dsf", property, pattern));
        assertTrue(PropertyValueValidator.isValid("b", property, pattern));
        assertTrue(PropertyValueValidator.isValid("aaab", property, pattern));
        assertTrue(PropertyValueValidator.isValid("aaaab", property, pattern));
        assertFalse(PropertyValueValidator.isValid("ghgf", property, pattern));
        assertFalse(PropertyValueValidator.isValid("fghfgh", property, pattern));
        assertFalse(PropertyValueValidator.isValid("ghsfjuy", property, pattern));
    }
    
}
