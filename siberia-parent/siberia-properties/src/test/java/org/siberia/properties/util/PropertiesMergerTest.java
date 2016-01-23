/*
 * PropertiesMergerTest.java
 * JUnit based test
 *
 * Created on 6 septembre 2006, 21:03
 */

package org.siberia.properties.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import junit.framework.*;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import org.siberia.xml.JAXBPropertiesLoader;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.DependsOnType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.ExtendedRangeType;
import org.siberia.xml.schema.properties.RefClassType;
import org.siberia.xml.schema.properties.RangeType;
import org.siberia.xml.schema.properties.Values;
import org.siberia.xml.schema.properties.MaximumType;
import org.siberia.xml.schema.properties.MinimumType;
import org.siberia.properties.util.PropertyContainerComparator;

/**
 *
 * @author alexis
 */
public class PropertiesMergerTest extends TestCase
{
    
    public PropertiesMergerTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(PropertiesMergerTest.class);
        
        return suite;
    }

    public void testMerge()
    {
        Set<Properties> set = new HashSet<Properties>();
        
        String dir = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator;
        JAXBPropertiesLoader jaxb = new JAXBPropertiesLoader();
        try
        {   Properties p = jaxb.loadProperties(
                                new FileInputStream(dir + "properties_1.xml"));
            set.add(p);
            
//            JAXBPropertiesLoader.saveProperties(p, 
//                                new File(dir + "properties_1_saved.xml"));
            
            p = jaxb.loadProperties(
                                new FileInputStream(dir + "properties_2.xml"));
            set.add(p);
        }
        catch(Exception e)
        {   e.printStackTrace(); }
        
        PropertiesMerger merger = new PropertiesMerger(set);
        Properties result = merger.merge();
        
//        try
//        {
//            JAXBPropertiesLoader.saveProperties(result, new File(dir + "merged.xml"));
//        }
//        catch(Exception e)
//        {   e.printStackTrace(); }
        
        assertEquals(99, result.getPriority());
        
        List<CategoryType> roots = result.getCategory();
        
        Collections.sort(roots, new PropertyContainerComparator());
        
        assertEquals(3, roots.size());
        
        DependsOnType frame_x_property_depend = null;
        
        for(int i = 0; i < roots.size(); i++)
        {   CategoryType category = roots.get(i);
            
            switch(i)
            {
                case 0 : assertEquals("Kernel", category.getRepr());
                         assertEquals("/org/siberia/rc/img/Kernel.png", category.getIcon());
                         assertEquals("Kernel", category.getLabel());
                         assertEquals(2, category.getOrder());
                         assertEquals("Kernel configuration", category.getDescription());
                         
                         List<PropertyContainer> prop = category.getPropertyAndCategory();
                         assertEquals(0, prop.size());
                         break;
                case 1 : assertEquals("graphic", category.getRepr());
                         assertEquals("/org/siberia/rc/img/Graphics.png", category.getIcon());
                         assertEquals(4, category.getOrder());
                         assertEquals("Graphic", category.getLabel());
                         assertEquals("Graphic configuration", category.getDescription());
                         
                         prop = category.getPropertyAndCategory();
                         assertEquals(1, prop.size());
                         PropertyContainer container = prop.get(0);
                         assertTrue(container instanceof CategoryType);
                         CategoryType subCat = (CategoryType)container;
                         
                         assertEquals("frame", subCat.getRepr());
                         assertEquals("", subCat.getIcon());
                         assertEquals(3, subCat.getOrder());
                         
                         assertEquals("frame positionning", subCat.getLabel());
                         assertEquals("frame parameters", subCat.getDescription());
                         
                         prop = subCat.getPropertyAndCategory();
                         assertEquals(4, prop.size());
                         
                         Collections.sort(prop, new PropertyContainerComparator());
                         
                         for(int j = 0; j < prop.size(); j++)
                         {  PropertyContainer c = prop.get(j);
                            
                            if ( c instanceof PropertyType )
                            {   PropertyType property = (PropertyType)c;
                                
                                switch(j)
                                {
                                    case 0 : assertEquals("frame_x", property.getRepr());
                                             assertEquals("integer", property.getNature().value());
                                             assertEquals(3, property.getOrder());
                                             assertEquals("0", property.getDefault());
                                             
                                             assertEquals("x position", property.getLabel());
                                             assertEquals("the x position applied to frame at launch", property.getDescription());
                                             
                                             List<DependsOnType> depends = property.getDependsOn();
                                             assertEquals(1, depends.size());
                                             
                                             DependsOnType depend = depends.get(0);
                                             frame_x_property_depend = depend;
                                             
                                             assertNull(property.getValues());
                                             assertNull(property.getPattern());
                                             
                                             RangeType range = property.getRange();
                                             assertNotNull(range.getMinimum());
                                             assertNull(range.getMaximum());
                                             
                                             MinimumType min = range.getMinimum();
                                             assertEquals("0", min.getRepr());
                                             break;
                                             
                                    case 1 : assertEquals("frame_y", property.getRepr());
                                             assertEquals("integer", property.getNature().value());
                                             assertEquals(6, property.getOrder());
                                             assertEquals("0", property.getDefault());
                                             
                                             assertEquals("y position", property.getLabel());
                                             assertEquals("the y position applied to frame at launch", property.getDescription());
                                             
                                             depends = property.getDependsOn();
                                             assertEquals(0, depends.size());
                                             
                                             assertNull(property.getValues());
                                             assertNull(property.getPattern());
                                             
                                             range = property.getRange();
                                             assertNotNull(range.getMinimum());
                                             assertNull(range.getMaximum());
                                             
                                             min = range.getMinimum();
                                             assertEquals("0", min.getRepr());
                                             break;
                                             
                                    case 2 : assertEquals("frame_width", property.getRepr());
                                             assertEquals("integer", property.getNature().value());
                                             assertEquals(9, property.getOrder());
                                             assertEquals("800", property.getDefault());
                                             
                                             assertEquals("width", property.getLabel());
                                             assertEquals("the width applied to frame at launch", property.getDescription());
                                             
                                             depends = property.getDependsOn();
                                             assertEquals(0, depends.size());
                                             
                                             if ( frame_x_property_depend != null )
                                             {  int repr = -1;
                                                try
                                                {   repr = Integer.parseInt(frame_x_property_depend.getRepr()); }
                                                catch(NumberFormatException e)
                                                {   e.printStackTrace();
                                                    assertTrue(false);
                                                }
                                                if ( repr != -1 )
                                                    assertEquals(property.getId().intValue(), repr);
                                                else
                                                    assertTrue(false);
                                             }
                                             else
                                                 assertTrue("impossible de verifier le mapping repr --> id", false);
                                             
                                             assertNull(property.getValues());
                                             assertNull(property.getPattern());
                                             
                                             range = property.getRange();
                                             assertNotNull(range.getMinimum());
                                             assertNotNull(range.getMaximum());
                                             
                                             min = range.getMinimum();
                                             assertEquals("0", min.getRepr());
                                             MaximumType max = range.getMaximum();
                                             assertEquals("1600", max.getRepr());
                                             break;
                                             
                                    case 3 : assertEquals("frame_height", property.getRepr());
                                             assertEquals("integer", property.getNature().value());
                                             assertEquals(12, property.getOrder());
                                             assertEquals("600", property.getDefault());
                                             
                                             assertEquals("height", property.getLabel());
                                             assertEquals("the height applied to frame at launch", property.getDescription());
                                             
                                             depends = property.getDependsOn();
                                             assertEquals(0, depends.size());
                                             
                                             assertNull(property.getValues());
                                             assertNull(property.getPattern());
                                             assertNull(property.getRange());
                                             
                                             ExtendedRangeType extendedRange = property.getExtendedRange();
                                             assertNotNull(extendedRange.getMinimum());
                                             assertNotNull(extendedRange.getMaximum());
                                             
                                             min = extendedRange.getMinimum();
                                             assertEquals("0", min.getRepr());
                                             max = extendedRange.getMaximum();
                                             assertEquals("1400", max.getRepr());
                                             
                                             List<RangeType> ranges = extendedRange.getExcluded();
                                             assertEquals(2, ranges.size());
                                             
                                             RangeType excludeRange = ranges.get(0);
                                             assertEquals("800", excludeRange.getMinimum().getRepr());
                                             assertEquals("900", excludeRange.getMaximum().getRepr());
                                             
                                             
                                             excludeRange = ranges.get(1);
                                             assertEquals("950", excludeRange.getMinimum().getRepr());
                                             assertEquals("1000", excludeRange.getMaximum().getRepr());
                                             
                                             break;
                                }
                            }
                            else
                            {   assertTrue(false); }
                         }
                         
                         break;
                         
                case 2 : assertEquals("editor", category.getRepr());
                         assertEquals("/org/siberia/rc/img/gnome-util.png", category.getIcon());
                         assertEquals(6, category.getOrder());
                         assertEquals("Viewer", category.getLabel());
                         assertEquals("External viewers", category.getDescription());
                         
                         prop = category.getPropertyAndCategory();
                         assertEquals(0, prop.size());
                         break;
                default : assertTrue(false);
            }
        }
    }

    public void testUpdate()
    {
        String dir = "src" + File.separator + "test" + File.separator + "java" + File.separator + 
                     "org" + File.separator + "siberia" + File.separator + "rc" + File.separator +
                     "update" + File.separator;
        JAXBPropertiesLoader jaxb = new JAXBPropertiesLoader();
        
        Properties required = null;
        Properties old      = null;
        try
        {   required = jaxb.loadProperties(
                                new FileInputStream(dir + "required_properties.xml"));
            
            old = jaxb.loadProperties(
                                new FileInputStream(dir + "old_properties.xml"));
        }
        catch(Exception e)
        {   e.printStackTrace(); }
        
        PropertiesMerger merger = new PropertiesMerger();
        Properties result = merger.update(required, old);
        
        try
        {
            jaxb.saveProperties(result, new File(dir + "resultat.xml"));
        }
        catch(Exception e)
        {   e.printStackTrace(); }
        
        assertEquals(9, result.getPriority());
        
        List<CategoryType> roots = result.getCategory();
                         
         Collections.sort(roots, new PropertyContainerComparator());
        
        assertEquals(2, roots.size());
        
        for(int i = 0; i < roots.size(); i++)
        {   CategoryType category = roots.get(i);
            
            List<PropertyContainer> prop = null;
            List<DependsOnType> depends = null;
            PropertyType property = null;
            
            switch(i)
            {
                case 0 : assertEquals("graphic", category.getRepr());
                         assertEquals("siberia;1::img/Graphics.png", category.getIcon());
                         assertEquals("Graphic", category.getLabel());
                         assertEquals(4, category.getOrder());
                         assertEquals("Graphic configuration", category.getDescription());
                         
                         prop = category.getPropertyAndCategory();
                         assertEquals(2, prop.size());
                         
                         Collections.sort(prop, new PropertyContainerComparator());
                         
                         for(int j = 0; j < prop.size(); j++)
                         {  PropertyContainer c = prop.get(j);
                            
                            switch(j)
                            {
                                case 0 : property = (PropertyType)c;
                                         assertEquals("frame.main.lookandfeel", property.getRepr());
                                         assertEquals("string", property.getNature().value());

                                         assertEquals("look and feel", property.getLabel());
                                         assertEquals("look and feel to use", property.getDescription());

                                         depends = property.getDependsOn();
                                         assertEquals(0, depends.size());

                                         assertNull(property.getValues());
                                         assertNull(property.getPattern());

                                         assertNull(property.getRange());
                                         break;
                                case 1 : CategoryType cat = (CategoryType)c;
                                         assertEquals("", cat.getIcon());
                                         assertEquals("frame.main", cat.getRepr());
                                         assertEquals(3, cat.getOrder());
                                         assertEquals("frame positionning", cat.getLabel());
                                         assertEquals("frame parameters", cat.getDescription());
                                         
                                         List<PropertyContainer> frameMainContainers = cat.getPropertyAndCategory();
                         
                                         Collections.sort(frameMainContainers, new PropertyContainerComparator());
                                         assertEquals(4, frameMainContainers.size());
                                         
                                         for(int k = 0; k < frameMainContainers.size(); k++)
                                         {
                                             switch(k)
                                             {  case 0 : property = (PropertyType)frameMainContainers.get(k);
                                                                    assertEquals("frame.main.automaticpositioning", property.getRepr());
                                                                    assertEquals("boolean", property.getNature().value());

                                                                    assertEquals("automatic", property.getLabel());
                                                                    assertEquals("true if the all frame positions attributes have to be automatically saved", property.getDescription());

                                                                    assertEquals("false", property.getAppliedValue().getValue());
                                                                    break;
                                                case 1 : property = (PropertyType)frameMainContainers.get(k);
                                                                    assertEquals("frame.main.maximized", property.getRepr());
                                                                    assertEquals("boolean", property.getNature().value());

                                                                    assertEquals("maximized", property.getLabel());
                                                                    assertEquals("true if the frame have to be maximized at launch", property.getDescription());

                                                                    assertEquals("true", property.getAppliedValue().getValue());
                                                                    break;
                                                case 2 : property = (PropertyType)frameMainContainers.get(k);
                                                                    assertEquals("frame.main.width", property.getRepr());
                                                                    assertEquals("integer", property.getNature().value());

                                                                    assertEquals("width", property.getLabel());
                                                                    assertEquals("the width applied to frame at launch", property.getDescription());

                                                                    assertEquals("800", property.getAppliedValue().getValue());
                                                                    break;
                                                case 3 : property = (PropertyType)frameMainContainers.get(k);
                                                                    assertEquals("frame.main.height", property.getRepr());
                                                                    assertEquals("integer", property.getNature().value());

                                                                    assertEquals("height", property.getLabel());
                                                                    assertEquals("the height applied to frame at launch", property.getDescription());
                                                                    
                                                                    RangeType range = property.getRange();
                                                                    assertNotNull(range.getMinimum());
                                                                    assertNotNull(range.getMaximum());

                                                                    MinimumType min = range.getMinimum();
                                                                    assertEquals("0", min.getRepr());
                                                                    MaximumType max = range.getMaximum();
                                                                    assertEquals("1400", max.getRepr());

                                                                    assertEquals("700", property.getAppliedValue().getValue());
                                                                    break;
                                             }
                                         }
                                         break;
                            }
                         }
                         break;
                case 1 : assertEquals("editor", category.getRepr());
                         assertEquals("siberia;1::img/gnome-util.png", category.getIcon());
                         assertEquals("Viewer", category.getLabel());
                         assertEquals(6, category.getOrder());
                         assertEquals("External vsdfiewers", category.getDescription());
                         
                         prop = category.getPropertyAndCategory();
                         assertEquals(2, prop.size());
                         
                         Collections.sort(prop, new PropertyContainerComparator());
                         
                         for(int j = 0; j < prop.size(); j++)
                         {  PropertyContainer c = prop.get(j);
                            
                            switch(j)
                            {   case 0 : CategoryType cat = (CategoryType)prop.get(j);
                                         assertEquals("/org/siberia/rc/img/gnome-globe.png", cat.getIcon());
                                         assertEquals("html", cat.getRepr());
                                         assertEquals(1, cat.getOrder());
                                         assertEquals("html", cat.getLabel());
                                         assertEquals("html viewer parameters", cat.getDescription());
                                         
                                         List<PropertyContainer> htmlContainers = cat.getPropertyAndCategory();
                         
                                         Collections.sort(htmlContainers, new PropertyContainerComparator());
                                         assertEquals(1, htmlContainers.size());
                                         
                                         for(int k = 0; k < htmlContainers.size(); k++)
                                         {
                                             switch(k)
                                             {  case 0 : property = (PropertyType)htmlContainers.get(k);
                                                                    assertEquals("html_viewer", property.getRepr());
                                                                    assertEquals("string", property.getNature().value());

                                                                    assertEquals("html viewer", property.getLabel());
                                                                    assertEquals("the path of an executable html viewer", property.getDescription());

                                                                    assertEquals("", property.getAppliedValue().getValue());
                                                                    assertTrue(property.isManual());
                                                                    break;
                                             }
                                         }
                                         break;
                                case 1 : cat = (CategoryType)prop.get(j);
                                         assertEquals("/org/siberia/rc/img/stock_new-text.png", cat.getIcon());
                                         assertEquals("rtf", cat.getRepr());
                                         assertEquals(5, cat.getOrder());
                                         assertEquals("rtf", cat.getLabel());
                                         assertEquals("rtf viewer parameters", cat.getDescription());
                                         
                                         List<PropertyContainer> rtfContainers = cat.getPropertyAndCategory();
                         
                                         Collections.sort(rtfContainers, new PropertyContainerComparator());
                                         assertEquals(2, rtfContainers.size());
                                         
                                         for(int k = 0; k < rtfContainers.size(); k++)
                                         {
                                             switch(k)
                                             {  case 0 : property = (PropertyType)rtfContainers.get(k);
                                                                    assertEquals("rtf_viewer", property.getRepr());
                                                                    assertEquals("string", property.getNature().value());

                                                                    assertEquals("rtf viewer", property.getLabel());
                                                                    assertEquals("the path of an executable rtf viewer", property.getDescription());

                                                                    assertEquals("", property.getAppliedValue().getValue());
                                                                    assertFalse(property.isManual());
                                                                    break;
                                                case 1 : property = (PropertyType)rtfContainers.get(k);
                                                                    assertEquals("rtf_arguments", property.getRepr());
                                                                    assertEquals("string", property.getNature().value());

                                                                    assertEquals("arguments", property.getLabel());
                                                                    assertEquals("additional arguments to use when opening a rtf document", property.getDescription());

                                                                    assertEquals("", property.getAppliedValue().getValue());
                                                                    assertFalse(property.isManual());
                                                                    break;
                                             }
                                         }
                                         break;
                            }
                         }
            }
        }
    }
    
}
