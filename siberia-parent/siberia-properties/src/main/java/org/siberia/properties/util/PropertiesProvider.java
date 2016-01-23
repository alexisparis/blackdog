/* 
 * Siberia properties : siberia plugin defining system properties
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
package org.siberia.properties.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.lang.ref.SoftReference;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.siberia.parser.ParseException;
import org.siberia.parser.NoParserException;
import org.siberia.parser.Parser;
import org.siberia.parser.ParserRegistry;
import org.siberia.parser.impl.StringParser;
import org.siberia.parser.impl.IntegerParser;
import org.siberia.parser.impl.DoubleParser;
import org.siberia.parser.impl.FloatParser;
import org.siberia.parser.impl.BooleanParser;
import org.siberia.properties.*;
import org.siberia.xml.JAXBPropertiesLoader;
import org.siberia.xml.schema.properties.CategoryType;
import org.siberia.xml.schema.properties.ObjectFactory;
import org.siberia.xml.schema.properties.Properties;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.PropertyContainer;
import org.w3c.dom.Node;
import java.io.InputStream;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.siberia.xml.schema.properties.NatureType;

/**
 *
 * A class defining static methods to deal with properties.
 * It provides services to get the value of properties but also to set value of properties.
 *
 * @author alexis
 */
public class PropertiesProvider
{   
    /** logger */
    private static      Logger logger        = Logger.getLogger(PropertiesProvider.class);
    
    /** default parser */
    public static final Parser DEFAULT_PARSER = new StringParser();
    
    /** parser registry */
    private static SoftReference<ParserRegistry> parserRegistryRef = new SoftReference(null);
    
    /* create a new PropertyFinder which will search in the general properties */
    private PropertiesProvider()
    {   }
    
    /** return a ParserRegistry
     *  @return a ParserRegistry
     */
    public static ParserRegistry getParserRegistry()
    {   ParserRegistry registry = null;
        
        registry = parserRegistryRef.get();
        
        if ( registry == null )
        {   /* initialize a regisrty that will contains all parser defined */
            
            registry = new ParserRegistry();
            
            StringParser  stringParser = new StringParser();
            IntegerParser intParser    = new IntegerParser();
            BooleanParser boolParser   = new BooleanParser();
            FloatParser   floatParser  = new FloatParser();
            DoubleParser  doubleParser = new DoubleParser();

            registry.registerParser(String.class, stringParser);
            registry.registerParser(Boolean.class, boolParser);
            registry.registerParser(Integer.class, intParser);
            registry.registerParser(Float.class, floatParser);
            registry.registerParser(Double.class, doubleParser);

            registry.registerParser("integer", intParser);
            registry.registerParser("string" , stringParser);
            registry.registerParser("float"  , floatParser);
            registry.registerParser("double"  , doubleParser);
            registry.registerParser("boolean"  , boolParser);
            
            parserRegistryRef = new SoftReference(registry);
        }
        
        return registry;
    }
    
    /** return the Object representing the property for the given set of properties declared in a file
     *  properties stream an InputStream ( for example, a FileInputStream pointed out an xml declaration of properties)
     *  @param stream an InputStream
     *  @param propertyNames an array of name's properties
     *  @return returns an object which type depends on the nature of the resulting properties or null if not found
     */
    public static Object getProperty(InputStream stream, String propertyName)
    {   Object[] results = getProperties(stream, propertyName);
        
        Object result = null;
        if ( results != null && results.length > 0 )
            result = results[0];
        return result;
    }
    
    /** return the Objects representing the property for the given set of properties declared in a file
     *  properties stream an InputStream ( for example, a FileInputStream pointed out an xml declaration of properties)
     *  @param stream an InputStream
     *  @param propertyNames an array of name's properties
     *  @return returns an array of object whioch type depends on their declared nature
     */
    public static Object[] getProperties(InputStream stream, String... propertyNames)
    {   Object[] values = null;
        if( stream != null )
        {   if ( propertyNames != null )
            {   if ( propertyNames.length > 0 )
                {   values = new Object[propertyNames.length];
                    try
                    {   
                        InputSource inputSource = new InputSource(stream);
                        XPathFactory xPathFact = XPathFactory.newInstance();
                        XPath path = xPathFact.newXPath();

                        /* compile expression 
                         * fucking default namespace !!!
                         *
                         * we have to search for all properties whose repr attribute is equals to one of the item
                         * of array propertyNames
                         */
                        StringBuffer expression = new StringBuffer("//*[name()='property' and ");
                        if ( propertyNames.length > 1 )
                            expression.append("(");

                        for(int i = 0; i < propertyNames.length; i++)
                        {   expression.append("@repr='" + propertyNames[i] + "'");
                            if ( i < propertyNames.length - 1 )
                                expression.append(" or ");
                        }
                        if ( propertyNames.length > 1 )
                            expression.append(")");

                        expression.append("]");

                        XPathExpression xExpress = path.compile(expression.toString());

                        NodeList nodes = (NodeList)xExpress.evaluate(inputSource, XPathConstants.NODESET);

                        /* build a map linking repr value and property data */
                        Map<String, PropertyData> linkingMap = new HashMap<String, PropertyData>();

                        if ( nodes != null )
                        {   /* search for the property named name */
                            for(int j = 0; j < nodes.getLength(); j++)
                            {   Node currentNode = nodes.item(j);
                                if ( currentNode != null )
                                {   Node reprNode   = currentNode.getAttributes().getNamedItem("repr");
                                    Node natureNode = currentNode.getAttributes().getNamedItem("nature");
                                                                        
                                    if ( reprNode != null && natureNode != null )
                                    {   String repr = reprNode.getNodeValue();
                                        String nature = natureNode.getNodeValue();

                                        if ( currentNode.getChildNodes() != null )
                                        {   for(int k = 0; k < currentNode.getChildNodes().getLength(); k++)
                                            {   Node childNode = currentNode.getChildNodes().item(k);
                                                if ( childNode.getNodeName().equals("appliedValue") )
                                                {   if (childNode.getChildNodes() != null )
                                                    {   if ( childNode.getChildNodes().getLength() >= 1 )
                                                        {   try
                                                            {   PropertyData data = new PropertyData(childNode.getChildNodes().item(0).getNodeValue(),
                                                                                          nature);
                                                                linkingMap.put(repr, data);
                                                            }
                                                            catch(Exception e)
                                                            {   logger.warn("nature : " + nature + " is not a valid NatureType enumeration item", e); }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {   if ( reprNode == null )
                                            logger.warn("unable to get repr value for node " + currentNode);
                                        if ( natureNode == null )
                                            logger.warn("unable to get nature value for node " + currentNode);
                                    }
                                }
                            }
                        }
                        
                        for(int i = 0; i < propertyNames.length; i++)
                        {   PropertyData property = linkingMap.get(propertyNames[i]);
                            
                            Object toInsert = null;
                            
                            if ( property != null )
                            {
                                try
                                {   toInsert = property.parseObject(); }
                                catch(Exception e)
                                {   e.printStackTrace();
                                    /* use default parser if failed */
                                    try
                                    {   toInsert = DEFAULT_PARSER.parse(property.getValue()); }
                                    catch(ParseException pe)
                                    {   logger.warn("unable to parse value '" + property.getValue() + "' with default parser " +
                                                                DEFAULT_PARSER, pe);
                                    }
                                }
                            }

                            values[i] = toInsert;
                        }
                    }
                    catch(Exception e)
                    {   e.printStackTrace();
                        PropertiesProvider.logger.error("Unable to retrieve properties value", e); }
                }
            }
        }
        else
        {   logger.error("trying to get a property from a null InputStream"); }
        return values;
    }
    
    /** set the value of the property.<br/>
     *  properties are saved only if all values have been applied
     *  @param filePath the path to the file containing the Properties declaration
     *  @param propertyName the name of the property to set
     *  @param value the value to set
     *  @boolean return true if all values has been set
     */
    public static boolean setProperty(String filePath, String propertyName, Object value)
    {   return setProperties(filePath, new String[]{propertyName}, new Object[]{value}, true); }
    
    /** set the value of the properties.<br/>
     *  properties are saved only if all values have been applied
     *  @param filePath the path to the file containing the Properties declaration
     *  @param propertyNames the name of the properties to set
     *  @param values the value to set
     *  @boolean return true if all values has been set
     */
    public static boolean setProperties(String filePath, String[] propertyNames, Object[] values)
    {   return setProperties(filePath, propertyNames, values, true); }
    
    /** set the value of the properties
     *  @param filePath the path to the file containing the Properties declaration
     *  @param propertyNames the name of the properties to set
     *  @param values the value to set
     *  @param saveIfAllSucceed indicates if properties must only be saved if all values have been applied
     *  @boolean return true if all values has been set
     */
    public static boolean setProperties(String filePath, String[] propertyNames, Object[] values, boolean saveIfAllSucceed)
    {   
        boolean success = false;
        if ( filePath != null && propertyNames != null && values != null )
        {   
            if ( propertyNames.length == values.length )
            {
                File f = new File(filePath);

                if ( f.exists() )
                {   
                    JAXBPropertiesLoader jaxbProperties = new JAXBPropertiesLoader();
                    Properties properties = null;
                    
                    /** load properties */
                    try
                    {   properties = jaxbProperties.loadProperties(new FileInputStream(f)); }
                    catch(Exception e)
                    {   logger.error("unable to set Properties in file : " + filePath, e); }

                    if ( properties != null )
                    {   List<CategoryType> rootCatList = properties.getCategory();
                        
                        boolean errorOccurred = false;
                        
                        if ( rootCatList != null )
                        {   Iterator<CategoryType> it = rootCatList.iterator();
                            
                            while(it.hasNext())
                            {   if ( ! setProperties(it.next(), propertyNames, values) && ! errorOccurred )
                                {   errorOccurred = true; }
                            }
                        }
                        
                        boolean commit = true;
                        
                        if ( ! errorOccurred )
                        {   /** if all elements of array propertyNames are null, then it means that all properties
                             *  have been set
                             */
                            StringBuffer buffer = new StringBuffer();
                            boolean containsNonNull = false;
                            for(int i = 0; i < propertyNames.length; i++)
                            {   String currentPropertyName = propertyNames[i];
                                if ( currentPropertyName != null )
                                {   if ( ! containsNonNull )
                                        containsNonNull = true;
                                    buffer.append( (buffer.length() == 0 ? "" : ", ") + currentPropertyName );
                                }
                            }
                            
                            if ( containsNonNull )
                            {   success = false;
                                
                                logger.error("unable to set values for properties [" + buffer.toString() + "]");
                            }
                            else
                            {   success = true; }
                        }
                        else
                        {   success = ! errorOccurred; }
                        
                        if ( ! success && saveIfAllSucceed )
                        {   commit = false; }
                        
                        if ( commit )
                        {   try
                            {   jaxbProperties.saveProperties(properties, f); }
                            catch(Exception e)
                            {   logger.error("unable to save properties at path " + filePath, e); }
                        }
                        else
                        {   logger.error("properties will not be saved"); }
                    }
                }
            }
            else
            {   throw new IllegalArgumentException("propertyNames and values do not have the same length"); }
        }
        return success;
    }
    
    /** set the value of the properties
     *  @param cat a PropertyContainer
     *  @param propertyNames the name of the properties to set
     *  @param values the value to set
     *  @boolean return false if error occurred ( for example, we find a Properties but the value to apply was not correct )
     */
    private static boolean setProperties(PropertyContainer container, String[] propertyNames, Object[] values)
    {   boolean noError = true;
        
        if ( container != null && propertyNames != null && values != null )
        {   
            if ( propertyNames.length == values.length )
            {   
                if ( container instanceof PropertyType )
                {   PropertyType property = (PropertyType)container;
                    
                    String repr = property.getRepr();
                    
                    /** if the list contains repr, then we find one property */
                    for(int i = 0; i < propertyNames.length; i++)
                    {   String currentProperty = propertyNames[i];
                        
                        if ( currentProperty != null && currentProperty.equals(repr) )
                        {   /* see if the value to apply is to be valid */
                            String value = values[i].toString();
                            if ( PropertyValueValidator.isValid(value, property) )
                            {   if ( property.getAppliedValue() == null )
                                {   
                                    //try
                                    {   property.setAppliedValue(new ObjectFactory().createPropertyTypeAppliedValue()); }
                                }
                                property.getAppliedValue().setValue(value);
                                
                                /** set null at the current index and break */
                                propertyNames[i] = null;
                            }
                            else
                            {   /* marked error and continue */
                                logger.error("unable to set value '" + value + "' for property '" + repr +" --> invalid");
                                noError = false;
                            }
                        }
                    }
                    
                }
                else if ( container instanceof CategoryType )
                {   List<PropertyContainer> list = ((CategoryType)container).getPropertyAndCategory();
                    if ( list != null )
                    {   Iterator<PropertyContainer> it = list.iterator();
                        while(it.hasNext())
                        {   if ( ! setProperties(it.next(), propertyNames, values) )
                            {   noError = false; }
                        }
                    }
                }
            }
            else
            {   throw new IllegalArgumentException("propertyNames and values do not have the same length"); }
        }
        
        return noError;
    }
    
    /** entity used when retrieving value for a given property.<br/>
     *  it links the default string value with the nature of the property
     */
    private static class PropertyData
    {
        /** the current String value if the property */
        private String     value  = null;
        
        /** the nature of the property. this will guide us to determine the parser to use */
        private NatureType nature = null;
        
        /** create a new PropertyData
         *  @param value the current value of the property
         *  @param nature the nature of the property
         */
        public PropertyData(String value, String nature) throws IllegalArgumentException
        {
            this.value = value;
            
            this.nature = NatureType.fromValue(nature);
        }
        
        /** get the value of the property
         *  @return the value of the property
         */
        public String getValue()
        {   return this.value; }
        
        /** return the NatureType of the property
         *  @return a NatureType
         */
        public NatureType getNature()
        {   return this.nature; }
        
        /** return the parsed Object resulting from the value property
         *  @return the parsed Object resulting from the value property
         *
         *  @exception NoParserException when no parser defined for the nature
         *              of tyhe property
         */
        public Object parseObject() throws NoParserException
        {   Object o = null;
            Parser parser = PropertiesProvider.getParserRegistry().
                                getParser(this.nature.value());
            if ( parser != null )
            {   try
                {   o = parser.parse(this.value); }
                catch(Exception e)
                {   logger.error("error when parsing '" + this.value +
                                        "' with parser " + parser);
                }
            }
            else
                throw new NoParserException(this.nature.value());
            return o;
        }
    }
}
