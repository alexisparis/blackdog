/* 
 * Siberia lang : java language utilities
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
package org.siberia.type.service;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.siberia.type.service.parser.ScriptJavaParser;
import org.siberia.type.service.provider.JavaLangageServiceProvider;

/**
 *
 * Action that allows to manage import by building a String representing it
 *
 * @author alexis
 */
public abstract class AbstractImportManager implements ImportManager
{
    /** parser */
    private Parser                parser            = null;
    
    /** map between className and packageName */
    public Map<String, String> mappings          = null;
    
    /**
     * Creates a new instance of AbstractImportManager 
     */
    public AbstractImportManager()
    {   this(null); }
    
    /**
     * Creates a new instance of AbstractImportManager 
     */
    public AbstractImportManager(Parser parser)
    {   this.setParser(parser); }

    public void setParser(Parser parser)
    {   this.parser = parser; }

    public Parser getParser()
    {   return this.parser; }
    
    /** method to add import information
     *  @param className the name of a class
     *  @param packageName the name of the corresponding package
     */
    public void addImportPart(String className, String packageName)
    {   if ( this.mappings == null )
            this.mappings = new HashMap<String, String>();
        this.mappings.put(className, packageName);
    }
    
    /* reset the current results */
    public void reset()
    {   if ( this.mappings != null )
        {   this.mappings.clear(); }
    }

    public abstract ServiceProvider getServiceProvider();
    
    /** return a String representing all the imports to add
     *  @return a String representing all the imports to add
     */
    public String getResultImports()
    {   
        if ( this.mappings == null ) return "";
        
        StringBuffer buf = new StringBuffer();
        
        String currentImport = null;
        Object currentValue  = null;
        for(Iterator<String> imports = this.mappings.keySet().iterator(); imports.hasNext();)
        {   currentImport = imports.next();
            
            currentValue = this.mappings.get(currentImport);
            
            if ( currentValue instanceof String )
            {   buf.append( "import " + ((String)currentValue) + "." + currentImport + ";\n"); }
        }
        
        /* tell the parser that the parsed text has changed */
        this.getParser().isNowInconsistent();
        
        return buf.toString();
    }
}
