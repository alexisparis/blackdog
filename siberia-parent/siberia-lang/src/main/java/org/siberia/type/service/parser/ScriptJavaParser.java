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
package org.siberia.type.service.parser;

import bsh.EvalError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.siberia.type.lang.SibImport;
import org.siberia.type.service.Parser;

/**
 *
 * Parser for script written in java
 *
 * @author alexis
 */
public class ScriptJavaParser implements Parser
{
    /** list of import */
    private List<SibImport> importList        = null;
    
    /** current list of unresolved symbols */
    private List<String>      unresolvedSymbols = null;
    
    /** last text parsed (waiting for better optimization) */
    private String            lastParsedText    = null;
    
    /** Creates a new instance of ScriptJavaParser */
    public ScriptJavaParser()
    {   }
    
    /** indicates to the parser that last results are not good because context changed */
    public void isNowInconsistent()
    {   this.lastParsedText = null; }
    
    /** returns a list of all unresolved symbols in the given text 
     *  @param text a String
     *  @return a list of String representing the unresolved symbols used in the text
     */
    public List<String> getUnresolvedSymbols(String text)
    {   
        if ( text == null )
        {   this.lastParsedText = null;
            if ( this.unresolvedSymbols != null )
                this.unresolvedSymbols.clear();
            
            return Collections.EMPTY_LIST;
        }
        
        boolean parse = false;
        
        if ( this.lastParsedText == null )
            parse = true;
        else
        {   if ( ! this.lastParsedText.equals(text) )
                parse = true;
        }
        
        if ( parse )
        {   /* reinit unresolved symbols list */
            if ( this.unresolvedSymbols != null )
                this.unresolvedSymbols.clear();
            
            this.parse(text);
        }
        
        return (this.lastParsedText == null ? Collections.EMPTY_LIST : this.unresolvedSymbols );
     }
    
    /** return an Object representing the result of the given script
     *  @param script the script
     *  @return the return object
     */
    public Object eval(String script) throws EvalError
    {   return new bsh.Interpreter().eval(script); }
    
    /** returns all the imports used by the script
     *  @return a list of ColdImport
     */
    public List<SibImport> getImports()
    {   return this.importList; }
    
    /* return a mapping of the instantiation found in the text
     *  @param text the String to parse
     *  @return a Map<String, Class>
     */
    public Map<String, Class> parse(String text)
    {   
        this.lastParsedText = text;
        
        Map<String, Class> objectMapping = new HashMap<String, Class>();
        
        StringTokenizer scriptText = new StringTokenizer(text.substring(this.parseImports(text)), "\n");
        
        /* begin to parse text */
        StringTokenizer instructions = null;
        String          instruction  = null;
        while(scriptText.hasMoreTokens())
        {   instructions = new StringTokenizer(scriptText.nextToken(), ";");
            while(instructions.hasMoreTokens())
            {   instruction = instructions.nextToken();
                
                this.parseInstruction(instruction, objectMapping);
            }
        }
        
//        for(Iterator<String> keys = objectMapping.keySet().iterator(); keys.hasNext();)
//        {   String key = keys.next();
//            System.out.println("\t" + key + " : " + objectMapping.get(key) );
//        }
        
        return objectMapping;
    }
    
    /** try to parse an instruction
     *  @param text a String representing an instruction
     *  @param map a map to update
     */
    private void parseInstruction(String text, Map<String, Class> map)
    {   System.out.println("parsing : " + text);
        String classElement         = null;
        String instantiationElement = null;
        
        int equalPosition = text.indexOf("=");
        StringTokenizer elements = null;
        
        if ( equalPosition != -1 )
        {   elements = new StringTokenizer(text.substring(0, equalPosition), " ");
            
            while(elements.hasMoreTokens())
            {   if ( classElement == null )
                    classElement = elements.nextToken();
                else if ( instantiationElement == null )
                    instantiationElement = elements.nextToken();
                else
                    break;
            }
        }
        else
        {   elements = new StringTokenizer(text, " ");
            
            if ( elements.countTokens() != 2 )
            {   
                /* try to found if the last word represent a class */
                String lastToken = null;
                if ( text.lastIndexOf(".") != -1 )
                    lastToken = text.substring(0, text.lastIndexOf(".") - 1);
                
                while(elements.hasMoreTokens())
                {   lastToken = elements.nextToken(); }
                
                if ( text.lastIndexOf(".") != -1 )
                    lastToken = text.substring(0, text.lastIndexOf("."));
                
                if ( this.unresolvedSymbols == null )
                    this.unresolvedSymbols = new ArrayList<String>();
                this.unresolvedSymbols.add(lastToken);
                
                /* do not continue */
                return;
            }
            else
            {
                while(elements.hasMoreTokens())
                {   if ( classElement == null )
                        classElement = elements.nextToken();
                    else if ( instantiationElement == null )
                        instantiationElement = elements.nextToken();
                    else
                        break;
                }
            }
        }
        
        /* try to find the corresponding class named classElement */
        Class c = this.findClass(classElement);
        
        if (c != null)
            map.put(instantiationElement, c);
        else
        {   if ( this.unresolvedSymbols == null )
                this.unresolvedSymbols = new ArrayList<String>();
            this.unresolvedSymbols.add(classElement);
        }
    }
    
    /** find a Class corresponding to the given name
     *  @param name the name of a class
     *  @return a Class
     */
    private Class findClass(String name)
    {   
        Class cl = null;
        
        try
        {   cl = this.getClass().forName(name); }
        catch(ClassNotFoundException e){    }
        
        if ( cl != null ) return null;
        
        SibImport currentImport = null;
        if ( this.importList != null )
        {   for(Iterator<SibImport> imports = this.importList.iterator(); imports.hasNext();)
            {   currentImport = imports.next();
                
                boolean loaded = false;

                try
                {   cl = this.getClass().forName(currentImport.getOwnedClass() + "." + name);
                    return cl;
                }
                catch(ClassNotFoundException e){   }
                
                /* try to load it */
                try
                {   cl = this.getClass().getClassLoader().getSystemClassLoader().loadClass(
                            currentImport.getOwnedClass() + "." + name);
                    return cl;
                }
                catch(Exception e2)
                {   //System.err.println("unable to load " + currentImport.getOwnedClass() + "." + name);
                }
            }
        }
        
        return null;
    }
    
    
    /** feed the list of imports used in the script
     *  @param text the String to parse
     *  @return the beginning position of the the text without imports
     */
    private int parseImports(String text)
    {   int position = 0;
        if ( this.importList != null )
            this.importList.clear();
        
        int currentSeparatorPosition = text.indexOf(";");
        
        if ( currentSeparatorPosition == -1 ) return 0;
        
        String      currentSentence = null;
        SibImport currentImport   = null;
        
        while(currentSeparatorPosition != -1 && position < currentSeparatorPosition)
        {   
            currentSentence = text.substring(position, currentSeparatorPosition).trim();
            currentSentence.replaceAll("\n", " ");
            currentImport = this.parseImport(currentSentence);
            
            if ( ! currentSentence.startsWith("import ") ) break;
            
            /* update positions */
            position = currentSeparatorPosition + 1;
            currentSeparatorPosition = text.substring(position).indexOf(";") + position;
            
            if ( currentImport == null ) continue;
            
            if ( this.importList == null )
                this.importList = new ArrayList<SibImport>();
            
            /** test if it already exists */
            boolean exists = false;
            
            for(int i = 0; i < this.importList.size(); i++)
            {   
                if ( this.importList.get(i).getName().equals(currentImport.getName()) )
                {   exists = true;
                    break;
                }
            }
            
            if ( ! exists )
                this.importList.add(currentImport);
        }
        
        return position;
    }
    
    /** parse the String to return an ColdImport
     *  @param text a String representing a sentence without space at beginning and end
     *  @return an ColdImport
     */
    private SibImport parseImport(String text)
    {   if ( text.length() < 6 )
            return null;
        String pack = text.substring(6).trim();
        if ( pack.contains(" ") ) return null;
        
        int lastPointPosition = pack.lastIndexOf(".");
        if ( lastPointPosition == -1 ) return null;
        pack = pack.substring(0, lastPointPosition);
        
        Package pac = Package.getPackage(pack);
        if ( pac == null ) return null;
        SibImport imp = new SibImport();
        imp.setJavaPackage(pac);
        return imp;
    }
    
    public static void main(String[] args)
    {   String text = "import java.lang.*;" +
                "import \norg.siberia.type.*;import java.util.*;" +
                "import javax.swing.event.*; String a = new String();" +
                "String b;ArrayList list = new ArrayList(5);ColdType k;" +
                "this.parse(String hihi)";
        
        ScriptJavaParser parser = new ScriptJavaParser();
        
        Map<String, Class> m = parser.parse(text);
        
        for(int i = 0; i < parser.importList.size(); i++)
            System.out.println("i : " + i + parser.importList.get(i).toString());
        
        System.out.println("");
        
        for(Iterator<String> keys = m.keySet().iterator(); keys.hasNext();)
        {   String currentKey = keys.next();
            System.out.println(currentKey + " --> " + m.get(currentKey).getName());
        }
    }
}
