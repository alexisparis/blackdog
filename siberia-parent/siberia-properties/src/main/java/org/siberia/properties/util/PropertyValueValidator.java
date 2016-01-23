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

import java.util.Iterator;
import org.apache.log4j.Logger;
import org.siberia.parser.ParseException;
import org.siberia.parser.Parser;
import org.siberia.parser.ParserRegistry;
import org.siberia.properties.*;
import org.siberia.xml.schema.properties.Borne;
import org.siberia.xml.schema.properties.PatternType;
import org.siberia.xml.schema.properties.ExtendedRangeType;
import org.siberia.xml.schema.properties.PropertyType;
import org.siberia.xml.schema.properties.RangeType;
import org.siberia.xml.schema.properties.ValueType;
import org.siberia.xml.schema.properties.Values;

/**
 *
 * Class defining static methods which indicates if a value is valid according to a Property definition.<br>
 * This class also define static method to evaluate condition with Range, ExtendedRange, Pattern, set of values.
 *
 * @author alexis
 */
public class PropertyValueValidator
{
    /** logger */
    private static Logger logger = Logger.getLogger(PropertyValueValidator.class);
    
    /** comparator elements */
    private static final int EQ  = 1; // =
    private static final int LT  = 2; // <
    private static final int GT  = 4; // >
    
    /** Creates a new instance of PropertyValueValidator */
    private PropertyValueValidator()
    {   }
    
    /** return true if PropertyValueValidator consider that it is valid
     *  @param valueToApply a String representing the value to apply
     *  @param Property an instance of PropertyType
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, PropertyType property)
    {   return isValid(valueToApply, new XmlProperty(property)); }
    
    /** return true if PropertyValueValidator consider that it is valid
     *  @param valueToApply a String representing the value to apply
     *  @param Property an instance of ColdXmlProperty
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property)
    {   boolean valid = true;
        if ( valueToApply != null )
        {   if ( property != null )
            {   if ( property.getInnerProperty() != null )
                {
                    /* test according to nature */
                    if ( PropertyValueValidator.isValidAccordingToNature(valueToApply, property) )
                    {
                        if ( property.getInnerProperty().getValues() != null )
                        {   if ( ! PropertyValueValidator.isValid(valueToApply, property, property.getInnerProperty().getValues() ) )
                            {   logger.debug("value '" + valueToApply + "' invalid according to values");
                                return false;
                            }
                        }
                        
                        if ( property.getInnerProperty().getPattern() != null )
                        {   if ( ! PropertyValueValidator.isValid(valueToApply, property,
                                                                   property.getInnerProperty().getPattern()) )
                            {   logger.debug("value '" + valueToApply + "' invalid according to pattern " +
                                                        property.getInnerProperty().getPattern().getValue());
                                return false;
                            }
                        }
                        
                        if ( property.getInnerProperty().getExtendedRange() != null )
                        {   if ( ! PropertyValueValidator.isValid(valueToApply, property,
                                                                   property.getInnerProperty().getExtendedRange()) )
                            {   logger.debug("value '" + valueToApply + "' invalid according to extended range ");
                                return false;
                            }
                        }
                        else if ( property.getInnerProperty().getRange() != null )
                        {   if ( ! PropertyValueValidator.isValid(valueToApply, property,
                                                                   property.getInnerProperty().getRange()) )
                            {   logger.debug("value '" + valueToApply + "' invalid according to range ");
                                return false;
                            }
                        }
                    }
                    else
                    {   logger.debug("value '" + valueToApply + "' invalid according to property type " + property.getNature());
                        valid = false;
                    }
                }
                else
                    valid = false;
            }
            else
                valid = false;
        }
        else
            valid = false;
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the nature
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param values a list of value node
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValidAccordingToNature(String valueToApply, XmlProperty property)
    {   boolean valid = false;
        
        Parser parser = PropertyValueValidator.getParser(property.getNature());
        if ( parser != null )
        {   try
            {   parser.parse(valueToApply);
                valid = true;
            }
            catch(ParseException e)
            {   logger.debug("unable to parse value '" + valueToApply + "' with parser " + parser); }
        }
        else
        {   logger.debug("no parser fund for nature " + property.getNature()); }
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the values
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param values a list of value node
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property, Values values)
    {   boolean valid = false;
        if ( values != null )
        {   
            Iterator it = values.getValue().iterator();
            while(it.hasNext())
            {   Object current = it.next();
                if ( current instanceof ValueType )
                {   valid = PropertyValueValidator.isValid(valueToApply, property, (ValueType)current);
                    if ( valid )
                        break;
                }
            }
        
            if ( ! valid )
            {   logger.debug("could not find '" + valueToApply + "' in values of property '" + property.getRepr() + "'"); }
        }
        
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the values
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param value a value node
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property, ValueType value)
    {   boolean valid = false;
        if ( value != null )
        {   valid = PropertyValueValidator.compare(valueToApply, value.getRepr(), property.getNature(), EQ); }
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the values
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param range a RangeType node
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property, RangeType range)
    {   boolean valid = false;
        if ( range != null )
        {   valid = PropertyValueValidator.isValid(valueToApply, property, range.getMaximum(), LT);
            if ( ! valid )
            {   logger.debug("value '" + valueToApply + "' is greater than maximum '" + range.getMaximum().getRepr() + "'");
                return valid;
            }
            valid = PropertyValueValidator.isValid(valueToApply, property, range.getMinimum(), GT);
            if ( ! valid )
            {   logger.debug("value '" + valueToApply + "' is lower than minimum '" + range.getMaximum().getRepr() + "'");
                return valid;
            }
            
            if ( range instanceof ExtendedRangeType )
            {   ExtendedRangeType r = (ExtendedRangeType)range;
                
                /* test ecluded */
                if ( r.getExcluded() != null )
                {   Iterator it = r.getExcluded().iterator();
                    while(it.hasNext())
                    {   Object current = it.next();
                        if ( current instanceof RangeType )
                        {   RangeType t = (RangeType)current;
                            
                            if ( PropertyValueValidator.isValid(valueToApply, property, t) )
                            {   
                                StringBuffer rangeRepr = new StringBuffer();
                                
                                rangeRepr.append(t.getMinimum().isInclude() ? "[" : "]");
                                rangeRepr.append(t.getMinimum().getRepr() + ", " + t.getMaximum().getRepr());
                                rangeRepr.append(t.getMaximum().isInclude() ? "[" : "]");
                                
                                logger.debug("value '" + valueToApply + "' appears in excluded range " + rangeRepr.toString());
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the values
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param pattern a Pattern
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property, PatternType pattern)
    {   boolean valid = false;
        if ( pattern != null )
        {   try
            {   java.util.regex.Pattern pat     = java.util.regex.Pattern.compile(pattern.getValue());
                java.util.regex.Matcher matcher = pat.matcher(valueToApply);
                valid = matcher.find();
            }
            catch(Exception e)
            {   logger.error("unable to use pattern on '" + valueToApply + "' with pattern '" + pattern.getValue() + "'"); }
        }
        return valid;
    }
    
    /** return true if PropertyValueValidator consider that it is valid according to the values
     *  @param valueToApply a non null String representing the value to apply
     *  @param Property a non null instance of ColdXmlProperty
     *  @param borne an instance of Borne
     *  @param critera : an integer created with & of EQ or LT or GT
     *  @return true if PropertyValueValidator consider that it is valid
     */
    public static boolean isValid(String valueToApply, XmlProperty property, Borne borne, int critera)
    {   boolean valid = false;
        if ( borne != null )
        {   int critere = critera;
            if ( borne.isInclude() )
                critere = critere | EQ;
            valid = PropertyValueValidator.compare(valueToApply, borne.getRepr(), property.getNature(), critere);
        }
        else
            valid = true;
        return valid;
    }
    
    /** return a parser for a given nature
     *  @param nature
     *  @return a PropertyParser or null if not found
     */
    private static Parser getParser(String nature)
    {   Parser p = PropertiesProvider.getParserRegistry().getParser(nature);
        if ( p == null )
            p = PropertiesProvider.DEFAULT_PARSER;
        return p;
    }
    
    /** return the opposite critera for the given critera
     *  @param critera : an integer created with & of EQ or LT or GT
     *  @return an integer
     */
    private static int not(int critera)
    {   return Math.max(7 - critera, 0); }
    
    /** test a value according to the given elements and given critera
     *  @param valueToApply the value to apply
     *  @param compareValue the value to compare
     *  @param nature 
     *  @param critera : an integer created with & of EQ or LT or GT
     *  @return true if applyValue is valid acording to the given context
     */
    private static boolean compare(String valueToApply, String compareValue, String nature, int critera)
    {   boolean valid = false;
        if ( valueToApply != null )
        {   if ( compareValue != null )
            {   if ( critera == EQ + LT + GT )
                    valid = true;
                else if ( critera == 0 )
                    valid = false;
                else
                {   Integer compareResult = null;
                    
                    Parser parser = PropertyValueValidator.getParser(nature);
                    if ( parser != null )
                    {   try
                        {   Comparable apply   = parser.parse(valueToApply);
                            Comparable compare = parser.parse(compareValue);

                            compareResult = apply.compareTo(compare);
                        }
                        catch(ParseException e)
                        {   }
                    }
                    
                    if ( compareResult != null )
                    {   /* compare succeed */
                        if ( compareResult == 0 && ((critera & EQ) != 0) )
                            valid = true;
                        if ( ! valid )
                        {   if ( compareResult < 0 && ((critera & LT) != 0) )
                                valid = true;
                            if ( ! valid )
                            {   if ( compareResult > 0 && ((critera & GT) != 0) )
                                    valid = true;
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }
    
}
