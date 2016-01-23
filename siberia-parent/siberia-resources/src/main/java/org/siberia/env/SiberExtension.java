/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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
package org.siberia.env;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;

/**
 *
 * Entity that represents a standalone object extension definition<br/>
 * in plugin.xml file.
 *
 * Therefore, it acts as a wrapper for elements extension id, extended point id and parameters
 *
 * @author alexis
 */
public class SiberExtension
{
    /** inner extension */
    private Extension extension = null;
    
    /** id of the plugin that declare this extension */
    private String    pluginId  = null;
    
    /** Creates a new instance of SiberExtension
     *  @param extension an Extension
     *  @param id id of the plugin that declare this extension
     */
    public SiberExtension(Extension extension, String id)
    {   if ( extension == null )
            throw new IllegalArgumentException("extension must be non null");
        if ( id == null )
            throw new IllegalArgumentException("extension must have a non null plugin id");
        this.extension = extension;
        this.pluginId  = id;
    }
    
    /** return the id of the plugin that declare this extension
     *  @return a String
     */
    public String getPluginId()
    {   return this.pluginId; }
    
    /** return the id of the extension point
     *  @return the id of the extension point 
     */
    public String getExtensionPointId()
    {   return this.extension.getId(); }
    
    /** return the id of the extended point
     *  @return the id of the extended point 
     */
    public String getExtendedPointId()
    {   return this.extension.getExtendedPointId(); }
    
    public Parameter getParameter(String... keys)
    {   return this.getParameter(null, keys); }
    
    public List<Parameter> getParameters(String... keys)
    {   return this.getParameters(null, keys); }
    
    /** return a String representing the value associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return a String representing the value associated with the given key parameter or null if not found
     */
    public String getStringParameterValue(String... keys)
    {   String value = null;
        Parameter param = this.getParameter(keys);
        if ( param != null )
            value = param.valueAsString();
        return value;
    }
    
    /** return the Strings representing the values associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return Strings representing the values associated with the given key parameter or null if not found
     */
    public String[] getStringParameterValues(String... keys)
    {   String[] values = null;
        List<Parameter> params = this.getParameters(keys);
        if ( params != null )
        {   values = new String[params.size()];
            
            for(int i = 0 ; i < params.size(); i++)
            {   values[i] = params.get(i).valueAsString(); }
        }
        
        return values;
    }
    
    /** return a Boolean representing the value associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return a Boolean representing the value associated with the given key parameter or null if not found
     */
    public Boolean getBooleanParameterValue(String... keys)
    {   Boolean value = null;
        Parameter param = this.getParameter(keys);
        if ( param != null )
            value = param.valueAsBoolean();
        return value;
    }
    
    /** return Booleans representing the values associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return Booleans representing the values associated with the given key parameter or null if not found
     */
    public Boolean[] getBooleanParameterValues(String... keys)
    {   Boolean[] values = null;
        List<Parameter> params = this.getParameters(keys);
        if ( params != null )
        {   values = new Boolean[params.size()];
            
            for(int i = 0 ; i < params.size(); i++)
            {   values[i] = params.get(i).valueAsBoolean(); }
            
        }
        
        return values;
    }
    
    /** return a Number representing the value associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return a Number representing the value associated with the given key parameter or null if not found
     */
    public Number getNumberParameterValue(String... keys)
    {   Number value = null;
        Parameter param = this.getParameter(keys);
        if ( param != null )
            value = param.valueAsNumber();
        return value;
    }
    
    /** return Numbers representing the values associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return Numbers representing the values associated with the given key parameter or null if not found
     */
    public Number[] getNumberParameterValues(String... keys)
    {   Number[] values = null;
        List<Parameter> params = this.getParameters(keys);
        if ( params != null )
        {   values = new Number[params.size()];
            
            for(int i = 0 ; i < params.size(); i++)
            {   values[i] = params.get(i).valueAsNumber(); }
        }
        return values;
    }
    
    /** return an URL representing the value associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return a Boolean representing the value associated with the given key parameter or null if not found
     */
    public URL getURLParameterValue(String... keys)
    {   URL value = null;
        Parameter param = this.getParameter(keys);
        if ( param != null )
            value = param.valueAsUrl();
        return value;
    }
    
    /** return an URL representing the value associated with the given key parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return a Boolean representing the value associated with the given key parameter or null if not found
     */
    public URL[] getURLParameterValues(String... keys)
    {   URL[] values = null;
        List<Parameter> params = this.getParameters(keys);
        if ( params != null )
        {   values = new URL[params.size()];
            
            for(int i = 0 ; i < params.size(); i++)
            {   values[i] = params.get(i).valueAsUrl(); }
        }
        return values;
    }
    
    /** ########################################################################
     *  ############################# generic methods ##########################
     *  ######################################################################## */
    
    /** return the parameter which path on a given Parameter is represented as an array of String
     *  @param parameter the parameter where to find the sub parameter
     *  @params keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return the parameter related to the path represented by the keys array
     */
    public Parameter getParameter(Parameter parameter, String... keys)
    {   List<Parameter> list = this.getParameters(parameter, true, keys);
        
        Parameter result = null;
        
        if ( list != null && list.size() > 0 )
        {   result = list.get(0); }
        
        return result;
    }
    
    /** return the parameters which path on a given Parameter is represented as an array of String
     *  @param parameter the parameter where to find the sub parameter
     *  @param stopWhenOneFound indicate if we only need to find the first parameter
     *  @param keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return the parameter related to the path represented by the keys array
     */
    private List<Parameter> getParameters(Parameter parameter, String... keys)
    {   return this.getParameters(parameter, false, keys); }
    
    /** return the parameters which path on a given Parameter is represented as an array of String
     *  @param parameter the parameter where to find the sub parameter
     *  @param stopWhenOneFound indicate if we only need to find the first parameter
     *  @param keys it's an array of String representing the path to the required parameter.<br/>
     *      for example : if keys is {"a", "b"}, it will find the value of the sub parameter with key 'b' on a root parameter with key equals to 'a'
     *  @return the parameter related to the path represented by the keys array
     */
    private List<Parameter> getParameters(Parameter parameter, boolean stopWhenOneFound, String... keys)
    {   List<Parameter> params = null;
        if ( keys != null )
        {   if ( keys.length > 0 )
            {   Collection collec = parameter == null ? this.extension.getParameters(keys[0]) :
                                                        parameter.getSubParameters(keys[0]);

                if ( collec != null )
                {   if ( collec.size() > 0 )
                    {   Iterator it = collec.iterator();
                        while(it.hasNext())
                        {   Object current = it.next();

                            if ( current instanceof Parameter )
                            {   Parameter currentParam = (Parameter)current;

                                String[] newPath = new String[keys.length - 1];

                                System.arraycopy(keys, 1, newPath, 0, keys.length - 1);

                                List<Parameter> subParams = this.getParameters(currentParam, stopWhenOneFound, newPath);

                                if ( subParams != null )
                                {   if ( params == null )
                                    {   params = new ArrayList<Parameter>(); }

                                    params.addAll(subParams);
                                    
                                    if ( stopWhenOneFound )
                                    {   break; }
                                }
                            }
                        }
                    }
                }
            }
            else
            {   if ( params == null )
                {   params = new ArrayList<Parameter>(); }
                
                params.add(parameter);
            }
        }
        else
            params = Collections.emptyList();
        return params;
    }
    
    
}
