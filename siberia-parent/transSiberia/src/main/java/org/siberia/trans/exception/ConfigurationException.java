/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.exception;

/**
 *
 * Exception throwed when error occurred when reading TransSiberia configuration file
 *
 * @author alexis
 */
public class ConfigurationException extends Exception
{
    /** Creates a new instance of ConfigurationException */
    public ConfigurationException()
    {   this("TransSiberia configuration file seems to be damaged : " +
                            "Downloading plugins is impossible"); }
    
    /** Creates a new instance of ConfigurationException
     *  @param msg a message
     */
    public ConfigurationException(String msg)
    {   super(msg); }
    
}
