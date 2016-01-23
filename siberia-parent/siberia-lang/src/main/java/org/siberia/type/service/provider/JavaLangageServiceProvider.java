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
package org.siberia.type.service.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.siberia.type.lang.SibClass;
import org.siberia.type.lang.SibField;
import org.siberia.type.lang.SibImport;
import org.siberia.type.lang.SibInstantiation;
import org.siberia.type.lang.SibMethod;
import org.siberia.type.lang.SibPackage;
import org.siberia.type.lang.SibVirtualPackage;
import org.siberia.type.lang.LangageElement;
import org.siberia.type.SibList;
import org.siberia.type.service.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.siberia.type.service.event.TaskListener;


/**
 *
 * Langage which is able to give information about java langage architecture
 *
 * @author alexis
 */
public class JavaLangageServiceProvider implements ServiceProvider
{
    private static JavaLangageServiceProvider instance         = null;
    
    private static boolean SORT_WHEN_INITIALIZE   = true;
    
    private static String PACKAGE_MAPPING_ROOT    = "packageMapping";
    private static String PACKAGE_MAPPING_PARENT  = "parent";
    private static String PACKAGE_MAPPING_CHILD   = "child";
            
    private static String CLASS_MAPPING_ROOT      = "classMapping";
    private static String CLASS_MAPPING_PARENT    = "parent";
    private static String CLASS_MAPPING_CHILD     = "child";
    
    private static final String DIRECTORY_NAME    = System.getProperty("user.home") + getPathSeparator() + ".siberia";
    private static final String PACKAGE_FILENAME  = DIRECTORY_NAME + getPathSeparator() + "packageMapping.xml";
    private static final String CLASS_FILENAME    = DIRECTORY_NAME + getPathSeparator() + "classMapping.xml";
    
    /** path to the file containing package tree */
    private String                     packageTreePath  = null;
    
    /** path to the file containing class package mappings */
    private String                     classMappingPath = null;
    
    /** comparator used to sort list of results */
    private /*WeakReference<*/Comparator/*>*/  comparator       = null;
    
    private File                       classFile        = null;
    
    private File                       packageFile      = null;
    
    /** current criterion for the research */
    private String                     currentCriterion = null;
    
    /** list to feed with the research results */
    private SibList                  result           = null;
    
    /** indicator if the current research should be canceled */
    private boolean                    shouldReinit     = false;
    
    /** indicator telling if the research is running */
    private boolean                    isRunning        = false;
    
    /** results of parsing */
    private Map<String, Class>         parserResults    = null;
    
    /** list of imports */
    private List<SibImport>          imports          = null;
    
    /** list of TaskListener */
    private List<TaskListener>         taskListeners    = null;
    
    /** Creates a new instance of JavaLangageServiceProvider */
    private JavaLangageServiceProvider()
    {   /* do nothing */ }
    
    /** return the singleton JavaLangageServiceProvider
     *  @return the instance of the class JavaLangageServiceProvider
     */
    public static JavaLangageServiceProvider getInstance()
    {   if ( instance == null )
            instance = new JavaLangageServiceProvider();
        return instance;
    }
    
    /** tell the result of the parsing to the service provider
     *  @param map a map<String, Class>
     */
    public void setParserResult(Map<String, Class> map)
    {   this.parserResults = map; }

    /** initialize the imports used
     *  @param imports a list of SibImport
     */
    public void setImports(List<SibImport> imports)
    {   this.imports = imports; }
    
    /** return the comparator to use to sort list returned by the instance
     *  @return an instance of Comparator
     */
    public Comparator getComparator()
    {   if ( this.comparator == null )
            this.comparator = new /*WeakReference(new*/ LangageElementComparator()/*)*/;
        
        return this.comparator/*.get()*/;
    }
    
    /** process a jar file to feed both classMapping and packageMapping
     *  @param jarFile an instance of JarFile
     *  @param classMapping a mapping for identifying class' package
     *  @param packageMapping a mapping for identifying package children
     */
    public void processJarFile(JarFileProhibitingAcces jarFile, Map<String, List<String>> classMapping,
                                                Map<String, List<String>> packageMapping)
    {
        JarEntry        currentEntry     = null;
        String          currentEntryName = null;
        
        for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();)
        {   currentEntry = entries.nextElement();
            
            if ( ! jarFile.isRecommendedToBeUsed(currentEntry.getName()) ) continue;
            
            if ( currentEntry.getName().endsWith(".class") )
            {   
                currentEntryName = currentEntry.getName().substring(0, currentEntry.getName().lastIndexOf("."));
                currentEntryName = currentEntryName.replaceAll("/", ".");
                boolean isInnerClass = false;
                if ( currentEntryName.indexOf("$") != -1 )
                {   /* test if the inner class is public or not */
                    isInnerClass = true;
                    try
                    {   Class loaded = JavaLangageServiceProvider.class.getClassLoader().loadClass(currentEntryName);
                        if ( loaded.getModifiers() != Modifier.PUBLIC )
                            continue;
                    }
                    catch(ClassNotFoundException e){}
                }
                
                int lastPointPosition = currentEntryName.lastIndexOf(".");
                
                String currentKey = currentEntryName.substring(lastPointPosition + 1);
                
                if ( isInnerClass )
                    currentKey = currentKey.replaceAll("$", ".");
                
                if ( classMapping != null )
                {
                    List<String> list = classMapping.get(currentKey);

                    /** add elements to the mapping of classes */
                    if ( list != null )
                        list.add(currentEntryName.substring(0, lastPointPosition));
                    else
                    {   list = new ArrayList<String>();
                        list.add(currentEntryName.substring(0, lastPointPosition));
                        classMapping.put(currentKey, list);
                    }
                }
                
                /** decompose package to fill the package mappings */
                if ( packageMapping != null )
                {
                    String currentPackage = currentEntryName.substring(0, lastPointPosition);
                    char[] chars = currentPackage.toCharArray();
                    List<Integer> pointPositions = new ArrayList<Integer>();

                    for(int i = 0; i < chars.length; i++)
                    {   if ( chars[i] == '.' )
                            pointPositions.add(i);
                    }
                    pointPositions.add(currentPackage.length());

                    for(int i = 0; i < pointPositions.size() - 1; i++)
                    {   List<String> toSave  = null;
                        String toAdd = currentPackage.substring(pointPositions.get(i) + 1, pointPositions.get(i + 1));
                        if ( packageMapping.containsKey( currentPackage.substring(0, pointPositions.get(i)) ) )
                        {   toSave = packageMapping.get(currentPackage.substring(0, pointPositions.get(i)));
                            if ( ! toSave.contains(toAdd) )
                                toSave.add(toAdd);
                        }
                        else
                        {    toSave = new ArrayList<String>();
                             toSave.add( toAdd );
                             packageMapping.put( currentPackage.substring(0, pointPositions.get(i)), toSave );
                        }
                    }
                }
            }
        }
    }
    
    /* create a DOM Document according to a package map (between String and lists of String)
     *  @param map the map 
     *  @return a Dom Document
     */
    private Document createPackageMappingDocument(Map<String, List<String>> map)
    {   return this.createMappingDocument(map, PACKAGE_MAPPING_ROOT, PACKAGE_MAPPING_PARENT, PACKAGE_MAPPING_CHILD); }
    
    /* create a DOM Document according to a class map (between String and lists of String)
     *  @param map the map 
     *  @return a Dom Document
     */
    private Document createClassMappingDocument(Map<String, List<String>> map)
    {   return this.createMappingDocument(map, CLASS_MAPPING_ROOT, CLASS_MAPPING_PARENT, CLASS_MAPPING_CHILD); }
    
    /* create a DOM Document according to a map between String and lists of String
     *  @param map the map 
     *  @param rootTag the name of the root tag
     *  @param parentTag the name of generated key map tag
     *  @param childTag the name of generated Stringplaced in the values of the map
     *  @return a Dom Document
     */
    private Document createMappingDocument(Map<String, List<String>> map, String rootTag, String parentTag, String childTag)
    {   DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        
        try
        {
            DocumentBuilder factory  = builderFactory.newDocumentBuilder();
            Document        document = factory.newDocument();
            
            document.setXmlVersion("1.0");
            document.setXmlStandalone(true);
            
            org.w3c.dom.Element rootElement = document.createElement(rootTag);
            
            org.w3c.dom.Element currentPackage = null;
            String              currentKey     = null;
            
            Iterator<String> keys = null;
            
            if ( SORT_WHEN_INITIALIZE )
            {   /* create a list which can be sorted */
                List<String> keyList = new ArrayList<String>(map.size());

                for(Iterator<String> keysTmp = map.keySet().iterator(); keysTmp.hasNext();)
                    keyList.add(keysTmp.next());

                Collections.sort(keyList);
                
                keys = keyList.iterator();
            }
            else
                keys = map.keySet().iterator();
            
            while(keys.hasNext())
            {   currentKey = keys.next();
                currentPackage = document.createElement(parentTag);
                currentPackage.setAttribute("name", currentKey);
                
                org.w3c.dom.Element currentChild = null;
                for(Iterator<String> children = map.get(currentKey).iterator(); children.hasNext();)
                {   currentChild = document.createElement(childTag);
                    currentChild.setAttribute("name", children.next());
                    
                    currentPackage.appendChild(currentChild);
                }
                
                rootElement.appendChild(currentPackage);
            }
            
            document.appendChild(rootElement);
            return document;
            
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return null;
    }
    
    /** save a given document representing a package mapping into a file corresponding to the given fileName
     *  @param doc a document containing the package mapping data
     *  @param fileName the path where to create a corresponding file
     */
    public void saveMappingDocument(Document doc, String fileName)
    {   
        try
        {   Source source = new DOMSource(doc);
            
            File f = new File(fileName);
            
            System.out.println("fileName : " + fileName);
            
            if ( ! f.getParentFile().exists() )
                f.getParentFile().mkdirs();
            
            if ( ! f.exists() )
                f.createNewFile();
            
            File file = new File(fileName);
            Result resultat = new StreamResult(file);
            // Configuration du transformer
            TransformerFactory fabrique = TransformerFactory.newInstance();
            Transformer transformer = fabrique.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            // Transformation
            transformer.transform(source, resultat);
        }
        catch(Exception e)
        {   e.printStackTrace(); }
    }
    
    /** return an array of JarFile which are ressources for this provider
     *  @return an Array of JarFile
     */
    public JarFileProhibitingAcces[] getJarRessources()
    {   JarFileProhibitingAcces[] jarToLoad = null;
        
        try
        {
            String[] javaForbidden = new String[]{"com", "org", "sun"};
            jarToLoad = new JarFileProhibitingAcces[]
            {   new JarFileProhibitingAcces(new File(System.getProperty("java.home") + 
                                                    this.getPathSeparator() + "lib" +
                                                    this.getPathSeparator() + "rt.jar"), 
                                                    javaForbidden),
                new JarFileProhibitingAcces(new File("/home/alexis/myDarkProject/metamodelisation/dist/metamodelisation.jar"), null)
            };
            return jarToLoad;
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return null;
    }
    
    /** return the path separator for this system
     *  @return a String representing the path separator
     */
    private static String getPathSeparator()
    {   return File.separator; }
    
    /** initialize the package mapping ressource */
    private void initializePackageMap()
    {   Map<String, List<String>> packageMap = new HashMap<String, List<String>>();
        
        JarFileProhibitingAcces[] jarToLoad = this.getJarRessources();
        
        for(int i = 0; i < jarToLoad.length; i++)
            this.processJarFile(jarToLoad[i], null, packageMap);
        
        Document packageDocument = this.createClassMappingDocument(packageMap);
        
        this.saveMappingDocument(packageDocument, PACKAGE_FILENAME);
    }
    
    /** initialize the class mapping ressource */
    private void initializeClassMap()
    {   Map<String, List<String>> classMap   = new HashMap<String, List<String>>();
        
        JarFileProhibitingAcces[] jarToLoad = this.getJarRessources();
        
        for(int i = 0; i < jarToLoad.length; i++)
            this.processJarFile(jarToLoad[i], classMap, null);
        
        Document classDocument   = this.createClassMappingDocument(classMap);
        
        this.saveMappingDocument(classDocument, CLASS_FILENAME);
    }
    
    /** create all resources needed by the service provider */
    public void initialize()
    {   
        Map<String, List<String>> packageMap = new HashMap<String, List<String>>();
        Map<String, List<String>> classMap   = new HashMap<String, List<String>>();

        JarFileProhibitingAcces[] jarToLoad = this.getJarRessources();

        for(int i = 0; i < jarToLoad.length; i++)
            this.processJarFile(jarToLoad[i], classMap, packageMap);

        Document classDocument   = this.createClassMappingDocument(classMap);
        Document packageDocument = this.createPackageMappingDocument(packageMap);

        this.saveMappingDocument(classDocument, CLASS_FILENAME);
        this.saveMappingDocument(packageDocument, PACKAGE_FILENAME);
    }
    
    /** tell if the given package String representation exists
     *  @param packageName the name of a package
     *  @return true if it exists
     */
    public boolean isPackageValid(String packageName)
    {   /* make sure that all ressources exist */
        if ( this.packageFile == null )
        {
            this.packageFile = new File(PACKAGE_FILENAME);
            
            if ( ! this.packageFile.getParentFile().exists() )
                this.packageFile.getParentFile().mkdirs();
            
            if ( ! packageFile.exists() )
                this.initializePackageMap();
        }
        else if ( ! this.packageFile.exists() )
            this.initializePackageMap();
        
        try
        {   InputSource inputSource = new InputSource(new FileInputStream(this.packageFile));
            XPathFactory xPathFact = XPathFactory.newInstance();
            XPath path = xPathFact.newXPath();
        
            /* compile expression */
            XPathExpression xExpress = path.compile("/" + PACKAGE_MAPPING_ROOT + "/" +
                                                    PACKAGE_MAPPING_PARENT + "[@name='" + packageName + "']");

            return ((Boolean)xExpress.evaluate(inputSource, XPathConstants.BOOLEAN)).booleanValue();
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return false;
    }
    
    /** returns a list of package name that contains a class named className
     *  @param className a name of a class
     *  @return a list of string representing the name of packages that contains a class named className
     */
    public List<String> getPackagesNameContainingClass(String className)
    {   /* make sure that all ressources exist */
        if ( this.classFile == null )
        {
            this.classFile = new File(CLASS_FILENAME);
            
            if ( ! this.classFile.getParentFile().exists() )
                this.classFile.getParentFile().mkdirs();
            
            if ( ! classFile.exists() )
                this.initializeClassMap();
//                this.initializePackageMap();
        }
        else if ( ! this.classFile.exists() )
            this.initializeClassMap();
//            this.initializePackageMap();
        
        List<String> packages = null;
        
        try
        {   InputSource inputSource = new InputSource(new FileInputStream(classFile));
            XPathFactory xPathFact = XPathFactory.newInstance();
            XPath path = xPathFact.newXPath();
        
            /* compile expression */
            String expression = "/" + CLASS_MAPPING_ROOT + "/" +
                                               CLASS_MAPPING_PARENT + "[@name ='" + className + "']" +
                                               "/*";
            
            XPathExpression xExpress = path.compile(expression);

            NodeList list = (NodeList)xExpress.evaluate(inputSource, XPathConstants.NODESET);
            
            for(int i = 0; i < list.getLength(); i++)
            {   
                for(int j = 0; j < ((Node)list.item(i)).getAttributes().getLength(); j++)
                {   String currentAttribute = ((Node)list.item(i)).getAttributes().item(j).toString();

                    int pos = currentAttribute.indexOf("\"");
                    
                    if ( pos != -1 )
                    {   if ( packages == null )
                            packages = new ArrayList<String>();
                        packages.add(currentAttribute.substring(pos+ 1, currentAttribute.length() - 1));
                    }
                }
                
            }
        }
        catch(Exception e){ e.printStackTrace();}
        
        return (packages == null ? Collections.EMPTY_LIST : packages);
    }
    
    /** return a list of package's name with can be considered as children of the given package name
     *  @param packageName
     *  @param subPackageNameBeginning the beginning of an eventual subPackage
     *  @param packageList a list of packages
     */
    public void addChildrenPackageFor(String packageName, String subPackageNameBeginning, SibList packageList)
    {   
        if ( packageList == null )
            return;
        
        int startListPosition  = packageList.size();
        
        /* make sure that all ressources exist */
        if ( this.packageFile == null )
        {
            this.packageFile = new File(PACKAGE_FILENAME);
            if ( ! packageFile.exists() )
                this.initializePackageMap();
        }
        else if ( ! this.packageFile.exists() )
            this.initializePackageMap();
        
        try
        {   InputSource inputSource = new InputSource(new FileInputStream(packageFile));
            XPathFactory xPathFact = XPathFactory.newInstance();
            XPath path = xPathFact.newXPath();
        
            /* compile expression */
            XPathExpression xExpress = null;
            
            boolean searchRootPackage = true;
            
            if ( packageName != null && (packageName == null ? false : packageName.length() > 0) )
            {   xExpress = path.compile("/" + PACKAGE_MAPPING_ROOT + "/" +
                                                    PACKAGE_MAPPING_PARENT + "[@name='" + packageName + 
                                                    "']" + "/*[starts-with(@name,'" + subPackageNameBeginning + "')]");
                searchRootPackage = false;
            }
            else
                xExpress = path.compile("/" + PACKAGE_MAPPING_ROOT + "/" +
                                                    PACKAGE_MAPPING_PARENT + "[not(contains(@name,'.'))][starts-with" +
                                                    "(@name,'" + subPackageNameBeginning + "')]");

            NodeList list = (NodeList)xExpress.evaluate(inputSource, XPathConstants.NODESET);
            
            for(int i = 0; i < list.getLength(); i++)
            {   SibPackage pack = null;
                for(int j = 0; j < ((Node)list.item(i)).getAttributes().getLength(); j++)
                {   String currentAttribute = ((Node)list.item(i)).getAttributes().item(j).toString();
                    if ( currentAttribute.startsWith("name") )
                    {   if ( ! searchRootPackage )
                        {
                            pack = new SibPackage();
                            pack.setJavaPackage(Package.getPackage( (searchRootPackage ? "" : (packageName + ".")) +
                                currentAttribute.substring(currentAttribute.indexOf("\"") + 1, currentAttribute.length() - 1)));
                        }
                        else
                        {   /* create a virtual package */
                            pack = new SibVirtualPackage();
                         
                            // a revoir, constructeur cass
//                            currentAttribute.substring(currentAttribute.indexOf("\"") + 1, currentAttribute.length() - 1)
                        }
                        
                        if ( pack.getJavaPackage() != null || searchRootPackage )
                        {   packageList.add(pack);
                            //System.out.println("pack : " + pack.getCompletionObject());
                        }
                        else
                        {   /* trying to create a virtual package */    // XXAP a revoir sans doute
                            pack = new SibVirtualPackage();
                            Package javaPack = Package.getPackage(currentAttribute.substring(currentAttribute.indexOf("\"") + 1, currentAttribute.length() - 1));
                            pack.setJavaPackage(javaPack);
                            packageList.add(pack);
//                            System.err.println("WARNING : package " + (searchRootPackage ? "" : (packageName + ".")) +
//                                currentAttribute.substring(currentAttribute.indexOf("\"") + 1, currentAttribute.length() - 1) + 
//                                    " could not be loaded");
                        }
                        
                        if ( this.shouldReinit )
                            return;
                    }
                }
            }
            /* sort the list */
            if ( ! SORT_WHEN_INITIALIZE )
            {
                try
                {   Collections.sort(packageList.subList(startListPosition, packageList.size()), this.getComparator()); }
                catch(IndexOutOfBoundsException e){ }
                catch(IllegalArgumentException ex){ }
            }
        }
        catch(Exception e){ e.printStackTrace(); }
    }
    
    /** return a list of package's name with can be considered as children of the given package name
     *  @param packageName (could be null if no package is specified)
     *  @param classNameBeginning the beginning of an eventual class name
     *  @param classList a list of classes
     */
    public void addClassFor(String packageName, String classNameBeginning, SibList classList)
    {   /* make sure that all ressources exist */
        if ( this.classFile == null )
        {
            this.classFile = new File(CLASS_FILENAME);
            if ( ! classFile.exists() )
                this.initializeClassMap();
        }
        else if ( ! this.classFile.exists() )
            this.initializeClassMap();
        
        int startListPosition  = classList.size();
        
        try
        {   InputSource inputSource = new InputSource(new FileInputStream(classFile));
            XPathFactory xPathFact = XPathFactory.newInstance();
            XPath path = xPathFact.newXPath();
        
            /* compile expression */
            boolean noPackageSpecified = true;
            String expression = null;
            if ( packageName != null && (packageName == null ? false : packageName.length() > 0 ) )
            {   expression = "/" + CLASS_MAPPING_ROOT + "/" +
                                                    CLASS_MAPPING_PARENT + "[starts-with(@name,'" + classNameBeginning + "')]" +
                                                    "/" + CLASS_MAPPING_CHILD + "[@name='" + packageName + "']/parent::*";
                noPackageSpecified = false;
            }
            else
                expression = "/" + CLASS_MAPPING_ROOT + "/" +
                                                    CLASS_MAPPING_PARENT + "[starts-with(@name,'" + classNameBeginning + "')]";// + "/self::*";
             
            XPathExpression xExpress = path.compile(expression);

            NodeList list = (NodeList)xExpress.evaluate(inputSource, XPathConstants.NODESET);
            
            if ( ! noPackageSpecified )
            {
                List<String> packageIndependantClassList = null;

                for(int i = 0; i < list.getLength(); i++)
                {   SibClass clas = null;

                    if ( ! noPackageSpecified )
                    {   for(int j = 0; j < ((Node)list.item(i)).getAttributes().getLength(); j++)
                        {   String currentAttribute = ((Node)list.item(i)).getAttributes().item(j).toString();

                            if ( currentAttribute.startsWith("name") )
                            {   /* good attribute ... load the corresponding class */
                                String className = currentAttribute.substring(currentAttribute.indexOf("\"") + 1,
                                                                              currentAttribute.length() - 1);
                                //System.out.println(className);
                                Class newClass = null;
                                try
                                    {   newClass = this.getClass().forName(packageName + "." + className); }
                                catch(ClassNotFoundException cnfe)
                                {   System.err.println("WARNING : class " + className + " could not be loaded");
                                    continue;
                                }

                                clas = new SibClass();
                                clas.setJavaClass(newClass);
                                classList.add(clas);
                            }
                        }
                    }

                    if ( this.shouldReinit )
                        return;
                }
            }
            else // use SAX
            {    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
                 SAXParser        parser        = parserFactory.newSAXParser();
                 
                 if ( classList == null )
                     classList = new SibList();
                 
                 ClassHandler     handler       = new ClassHandler(classNameBeginning, classList, list.getLength());
                        
                 try
                 {  parser.parse(classFile, handler);

                    do
                    {   Thread.sleep(10); }
                    while(handler.isRunning());
                 }
                 catch(DoneParsingException e)
                 {  }
            }
            /* sort the list */
            if ( ! SORT_WHEN_INITIALIZE )
            {
                try
                {   Collections.sort(classList.subList(startListPosition, classList.size() - 1),
                                     this.getComparator());
                }
                catch(IndexOutOfBoundsException e){ }
                catch(IllegalArgumentException ex){ }
            }
        }
        catch(Exception e){ e.printStackTrace(); }
    }
    
    /** feed the list with methods and attributes according to the token
     *  @param lastToken 
     *  @param result the list to feed
     *  @param isStatic determine if we have to find static elements
     */
    protected void addMethodsAndAttributes(String lastToken, SibList result, boolean isStatic)
    {   this.addMethodsAndAttributes(lastToken, null, result, isStatic); }
    
    /** feed the list with methods and attributes according to the token
     *  @param lastToken 
     *  @param startingClass the class to start parsing on
     *  @param result the list to feed
     *  @param isStatic determine if we have to find static elements
     */
    protected void addMethodsAndAttributes(String lastToken, Class startingClass, SibList result, boolean isStatic)
    {   if ( lastToken == null ) return;
        int pointIndex = lastToken.indexOf(".");
        if ( pointIndex == -1 )
        {   /* feed the list with instances which name starts with lastToken */
            String currentInstance = null;
            if ( this.parserResults != null )
            {
                for(Iterator<String> res = this.parserResults.keySet().iterator(); res.hasNext();)
                {   currentInstance = res.next();

                    if ( currentInstance.startsWith(lastToken) )
                    {   result.add(new SibInstantiation(currentInstance, this.parserResults.get(currentInstance))); }
                }
            }
        }
//        else
        {   Class c = this.evaluateMethod(startingClass, lastToken.equals(".") ? "": lastToken);
            int lastPointPosition = lastToken.lastIndexOf(".");
            int lastParPosition   = lastToken.lastIndexOf("(");
            String m = lastToken.substring(lastPointPosition + 1,
                                          (lastParPosition > lastPointPosition ? lastParPosition : lastToken.length()));
            
            
            /* find on class c non static methods and attributes that starts with m */
            if ( c == null ) return;
            try
            {
                Field[] fields        = c.getDeclaredFields();
                List<Field> listField = new ArrayList<Field>(fields.length);
                Field currentField    = null;
                for(int i = 0; i < fields.length; i++)
                {   currentField = fields[i];
                    if ( Modifier.isStatic(currentField.getModifiers()) != isStatic )
                        continue;
                    
                    if ( Modifier.isPublic(currentField.getModifiers()) )
                        listField.add(currentField);
                    
                    if ( this.shouldReinit )
                        return;
                }
                
                /* sort */
                Collections.sort(listField, new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {   if ( ! (o1 instanceof Field) ) return -1;
                        if ( ! (o2 instanceof Field) ) return 1;
                        
                        return ((Field)o1).getName().compareTo(((Field)o2).getName());
                    }
                });
                    
                for(Iterator<Field> f = listField.iterator(); f.hasNext();)
                {   currentField = f.next();
                    
                    if ( currentField.getName().startsWith(m) )
                    {   SibField fi = new SibField();
                        fi.setJavaField(currentField);
                        result.add(fi);
                    }

                    if ( this.shouldReinit )
                        return;
                }
                
                Method[] methods        = c.getDeclaredMethods();
                List<Method> listMethod = new ArrayList<Method>(methods.length);
                Method currentMethod    = null;
                for(int i = 0; i < methods.length; i++)
                {   currentMethod = methods[i];
                    
                    if ( Modifier.isStatic(currentMethod.getModifiers()) != isStatic )
                        continue;
                    
                    if ( Modifier.isPublic(currentMethod.getModifiers()) )
                        listMethod.add(currentMethod);
                    
                    if ( this.shouldReinit )
                        return;
                }
                
                /* sort */
                Collections.sort(listMethod, new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {   if ( ! (o1 instanceof Method) ) return -1;
                        if ( ! (o2 instanceof Method) ) return 1;
                        
                        return ((Method)o1).getName().compareTo(((Method)o2).getName());
                    }
                });
                
                for(Iterator<Method> f = listMethod.iterator(); f.hasNext();)
                {   currentMethod = f.next();
                    
                    if ( currentMethod.getName().startsWith(m) )
                    {   SibMethod me = new SibMethod();
                        me.setJavaMethod(currentMethod);
                        result.add(me);
                    }

                    if ( this.shouldReinit )
                        return;
                }
            }
            catch(Exception e){ e.printStackTrace(); }
        }
    }
    
    /** evaluate the given portion of text and if it represents a primitive type return the corresponding class
     *  @param primitive the portion of text to check
     *  @return a class
     */
    private Class evaluatePrimitiveType(String primitive)
    {
        /* try to find if the given 'methodName' element is a primitive type */
        if ( primitive.startsWith("\"") && primitive.endsWith("\"") )
            return String.class;
        if ( primitive.startsWith("'") && primitive.endsWith("'") && primitive.length() == 1 )
            return Character.TYPE;
        if ( primitive.equals("true") || primitive.equals("false") )
            return Boolean.TYPE;
        
        if ( primitive.indexOf(".") == -1 )
        {   
            try
            {   Byte.parseByte(primitive);
                return Byte.TYPE;
            }
            catch(NumberFormatException e){ }
            try
            {   Short.parseShort(primitive);
                return Short.TYPE;
            }
            catch(NumberFormatException e){ }
            try
            {   Integer.parseInt(primitive);
                return Integer.TYPE;
            }
            catch(NumberFormatException e){ }
            try
            {   Long.parseLong(primitive);
                return Long.TYPE;
            }
            catch(NumberFormatException e){ }
        }
        else
        {   
            try
            {   Float.parseFloat(primitive);
                return Float.TYPE;
            }
            catch(NumberFormatException e){ }
            try
            {   Double.parseDouble(primitive);
                return Double.TYPE;
            }
            catch(NumberFormatException e){ }
            
        }

        return null;
    }
    
    /** return true if the first class is compatible with the second class
     *  @param clas1
     *  @param clas2
     *  @return true if the first class is compatible with the second class
     */
    private boolean isCompatible(Class clas1, Class clas2)
    {   
        if ( clas1 == null || clas2 == null ) return false;
        
        if ( clas2.isAssignableFrom(clas1) ) return true;
        
        
        /* believe something's wrong with all that !!!!...... see later */
        if ( clas2 == Short.TYPE )
        {   if ( clas1 == Byte.TYPE ) return true; }
        else if ( clas2 == Integer.TYPE )
        {   if ( clas1 == Byte.TYPE || clas1 == Short.TYPE ) return true; }
        else if ( clas2 == Long.TYPE )
        {   if ( clas1 == Byte.TYPE || clas1 == Short.TYPE || clas1 == Integer.TYPE ) return true; }
        else if ( clas2 == Float.TYPE )
        {   if ( clas1 == Byte.TYPE || clas1 == Short.TYPE || clas1 == Integer.TYPE || clas1 == Long.TYPE) return true; }
        else if ( clas2 == Double.TYPE )
        {   if ( clas1 == Byte.TYPE || clas1 == Short.TYPE || clas1 == Integer.TYPE || clas1 == Long.TYPE || clas1 == Float.TYPE) return true; }
        
        return false;
    }
    
    /* evaluate the type of the object returned by a method by passing it the parameters
     * return null if an error occured
     *  @param c the class
     *  @param methodName the name of a method
     */
    private Class evaluateMethod(Class c, String methodName)
    {   
        System.out.println("evaluateMethod(" + c + ", " + methodName + ")");
        
        if ( c == null )
        {   
            /* if parsing is null or empty, return */
            if ( this.parserResults == null )     return null;
            if ( this.parserResults.size() == 0 ) return null;

            String instantiation = null;
            for(Iterator<String> instantiations = this.parserResults.keySet().iterator(); instantiations.hasNext();)
            {   instantiation = instantiations.next();

                if ( methodName.startsWith(instantiation) )
                {   /* process the rest of the String to discover the final Class to apply */
                    try
                    {   return this.evaluateMethod(this.parserResults.get(instantiation),
                                methodName.substring(instantiation.length() + 1));
                    }
                    catch(StringIndexOutOfBoundsException sioobe){  return null; }
                }
            }
            
            Class primitiveClass = this.evaluatePrimitiveType(methodName);
            
            if ( primitiveClass != null ) return primitiveClass;
        }
        
        int pointIndex = methodName.indexOf(".");
        
        if ( pointIndex == -1 /*|| pointIndex == methodName.lastIndexOf(".")*/ ) return c;
        
        String mName = methodName.substring(0, pointIndex);
        
        /* mName must be like XXX(AAA, BBB, ...) */
        int parBegPosition = mName.indexOf("(");
        
        if ( parBegPosition != -1 )
        {   
            /* TO DO : PROCESS CONSTRUCTORS ________________________________________________________________________________________ */
            
            /* resolve parameters */
            int parEndPosition = mName.lastIndexOf(")");
            String methodName2 = mName.substring(parBegPosition + 1, parEndPosition).replaceAll(" ", "");
            StringTokenizer params = new StringTokenizer(methodName2, ",");
            Class[] paramList = new Class[params.countTokens()];
            
            Class paramClass = null;
            int i = 0;
            while(params.hasMoreTokens())
            {   paramClass = this.evaluateMethod(null, params.nextToken());
                
                
                if ( paramClass == null )
                    return null;
                
                paramList[i] = paramClass;
                i++;
            }
            
            try
            {   Method m = this.getMethodFor(c, mName.substring(0, parBegPosition), paramList);
                
                if ( m != null )
                    return m.getReturnType();
                else
                    return null;
            }
            catch(Exception e)
            { e.printStackTrace();return null; }
        }
        else
        {   try
            {   Field f = c.getDeclaredField(mName);
                return this.evaluateMethod(f.getDeclaringClass(), methodName.substring(pointIndex + 1));
            }
            catch(Exception e){ return null; }
        }
    }
    
    /** method which allow to find the method for the given arguments
     *  @param clas the class where to find the method
     *  @param methodName the name of the method
     *  @param arguments
     */
    private Method getMethodFor(Class clas, String methodName, Class[] arguments)
    {   try
        {   return clas.getDeclaredMethod(methodName, arguments); }
        catch(NoSuchMethodException nsme){  /* do nothing */ }
        
        /* else see all public methods which name is methodName */
        List<Method> methodsCandidates = null;
        Method       currentMethod     = null;
        try
        {   for(int i = 0; i < clas.getDeclaredMethods().length; i++)
            {   currentMethod = clas.getDeclaredMethods()[i];
                
                if ( currentMethod.getName().equals(methodName) )
                {   if ( Modifier.isPublic( currentMethod.getModifiers() ) )
                    {   if ( currentMethod.getParameterTypes().length == arguments.length )
                        {   if ( methodsCandidates == null )
                                methodsCandidates = new ArrayList<Method>();
                            methodsCandidates.add(currentMethod);
                        }
                    }
                }
            }
            
            if ( methodsCandidates == null )
                return null;
            
            /* search in the candidates methods if the type of arguments are compatible, if so, return */
            for(Iterator<Method> methods = methodsCandidates.iterator(); methods.hasNext(); )
            {   currentMethod = methods.next();
                
                for(int i = 0; i < currentMethod.getParameterTypes().length; i++)
                {   if ( ! this.isCompatible( arguments[i], currentMethod.getParameterTypes()[i] ) )
                        continue;
                    if ( i == currentMethod.getParameterTypes().length - 1 )
                        return currentMethod;
                }
            }
        }
        catch(SecurityException se)
        {   /* do nothing */ }

        return null;
    }
    
    /* */
    private Class evaluateClass(String input)
    {   return null; }
    
    /** configure the research
     *  @param input the criterion to use in the research
     *  @param list the list to fill
     */
    public void configureResearch(String input, SibList list)
    {   this.currentCriterion = input;
        this.result           = list;
    }
    
    public void run()
    {   
        if ( isRunning )
            return;
        
        if ( this.currentCriterion == null || this.result == null ) return;
        
        this.shouldReinit = true;
        
        while(shouldReinit)
        {
            this.isRunning = true;
            
            this.shouldReinit = false;

            /** process input to get the two last words */
            boolean newSpecified = false;
            String lastToken = null;

            int lastSpacePosition = this.currentCriterion.lastIndexOf(" ");
            if ( lastSpacePosition != -1 )
                lastToken = this.currentCriterion.substring(lastSpacePosition).trim();
            else
                lastToken = this.currentCriterion.trim();
            
            /* find if the token begin with a class name and '.' */
            Class cs = this.seemsToBeStatic(lastToken);
            
            if ( cs == null )
            {   this.addMethodsAndAttributes(lastToken, this.result, false); }
            else
            {   /** process lastToken to filter the class name */
                String bufString = cs.getName().substring(cs.getName().lastIndexOf('.') + 1);
                this.addMethodsAndAttributes(lastToken.substring(bufString.length()), cs, this.result, true);
            }

            /* add package if it conveys */
            if ( lastToken.indexOf(".") == -1 )
            {   this.addChildrenPackageFor(null, lastToken, this.result);
                if ( this.shouldReinit )
                {   this.result.clear(); }
                
                this.addClassFor(null, lastToken, this.result);
                if ( this.shouldReinit )
                {   this.result.clear(); }
            }
            else
            {   int separation = lastToken.lastIndexOf(".");
                this.addChildrenPackageFor(lastToken.substring(0, separation), lastToken.substring(separation + 1), this.result );
                if ( this.shouldReinit )
                {   this.result.clear(); }
                this.addClassFor( lastToken.substring(0, separation), lastToken.substring(separation + 1), this.result );
                if ( this.shouldReinit )
                {   this.result.clear(); }
            }
        }
        
        this.fireTaskFinished();
        this.isRunning = false;
    }
    
    /** tells if the given token begins with the name of class plus '.'
     *  @param token the token to check
     *  @return the class or null if it does not conveys
     */
    private Class seemsToBeStatic(String token)
    {   /* get the last element after the last '.' */
        if ( ! token.endsWith(".") ) return null;
        
        String cutString = token.substring(0, token.length() - 1);
        int lastPointPosition = cutString.lastIndexOf('.');
        String className = null;
        
        className = token.substring(0, token.length() - 1).substring(lastPointPosition + 1);
        
        /* if the class name is fully completed, return that class representation */
        if ( ! className.equals(cutString) )
        {   try
            {   return this.getClass().forName(className); }
            catch(ClassNotFoundException e)
            {   /* try to load it */
                try
                {   return this.getClass().getClassLoader().getSystemClassLoader().loadClass(className); }
                catch(ClassNotFoundException e2)
                {   return null; }
            }
        }
        
        if ( this.imports == null ) return null;
        
        /* for all imports try to find the corresponding class */
        SibImport currentImport = null;
        
        for(Iterator<SibImport> imports = this.imports.iterator(); imports.hasNext();)
        {   currentImport = imports.next();
            
            try
            {   return this.getClass().forName(currentImport.getOwnedClass() + "." + className); }
            catch(ClassNotFoundException e)
            {   /* try to load it */
                try
                {   return this.getClass().getClassLoader().getSystemClassLoader().loadClass(currentImport.getOwnedClass() + "." + className); }
                catch(ClassNotFoundException e2)
                {   }
            }
        }
        
        return null;
    }
    
    public boolean isRunning()
    {   return this.isRunning; }
    
    public void stop()
    {   this.isRunning = false;
        this.shouldReinit = false;
    }
    
    /** indicates to the provider that its current task should reinit */
    public void shouldReinit()
    {   this.shouldReinit = true; }
    
    /* #########################################################################
     * ######################### Task implementation ###########################
     * ######################################################################### */
    
    /** add a listener
     *  @param listener an instance of TaskListener
     */
    public void addTaskListener(TaskListener listener)
    {   if ( listener == null ) return;
        
        if ( this.taskListeners == null )
            this.taskListeners = new ArrayList<TaskListener>();
        
        this.taskListeners.add(listener);
    }
    
    /** remove a listener
     *  @param listener an instance of TaskListener
     */
    public void removeTaskListener(TaskListener listener)
    {   if ( this.taskListeners == null ) return;
        
        this.taskListeners.remove(listener);
    }
    
    /** prevents that it finished */
    public void fireTaskFinished()
    {   if ( this.taskListeners == null ) return;
        
        for(Iterator<TaskListener> listeners = this.taskListeners.iterator(); listeners.hasNext();)
            listeners.next().taskFinished(this);
    }
    
    
    /* #########################################################################
     * ############# Handler to performing feeding of class list ###############
     * ######################################################################### */
    
     private class ClassHandler extends DefaultHandler
     {
         private boolean          isInParent       = false;

         private String           className        = null;
         
         private SibList        classList        = null;
         
         private String           nameBeginning    = null;
         
         private boolean          isRunning        = false;
         
         private int              occurencesToFind = -1;
         
         private int              occurencesFunded = 0;
         
         /** create a new class Handler
          * @param nameBegin the beginning of the name of classes that we have to find
          * @param list a list to put new element into
          * @param occurencesToFind number of parent element to find
          */
         public ClassHandler(String nameBegin, SibList list, int occurencesToFind)
         {   super();
            
             this.nameBeginning    = nameBegin;
             this.classList        = list;
             this.occurencesToFind = occurencesToFind;
         }
         
         private void testIfDone() throws SAXException
         {  if ( JavaLangageServiceProvider.this.shouldReinit )
            {   this.isRunning = false;
                throw new DoneParsingException();
            }
         }

         public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
         {   if ( qName.equals(CLASS_MAPPING_PARENT) && attr.getValue("name").startsWith(this.nameBeginning) )
             {   this.isInParent = true;
                 this.className = attr.getValue("name");
             }
             else if ( this.isInParent )
             {   if ( qName.equals(CLASS_MAPPING_CHILD) )
                 {   Class newClass = null;
                     this.testIfDone();
                     try
                     {   newClass = this.getClass().forName(attr.getValue("name") + "." + this.className); }
                     catch(ClassNotFoundException cnfe)
                     {   System.err.println("WARNING : class " + attr.getValue("name") + "." + this.className + " could not be loaded");
                         return;
                     }
                     catch(java.lang.UnsatisfiedLinkError z){   return; }
                     catch(java.lang.NoClassDefFoundError e){   return; }
                     
                     if ( classList == null )
                         throw new RuntimeException("Trying to parse with an instance of ClassHandler but list was null");
                     
                     this.testIfDone();
                     SibClass s = new SibClass();
                     s.setJavaClass(newClass);
                     classList.add(s);
                     this.testIfDone();
                 }
             }
         }

         public void endElement(String uri, String localName, String qName) throws SAXException
         {   this.testIfDone();
             
             if ( qName.equals(CLASS_MAPPING_PARENT) )
             {   this.isInParent = false;
                 this.occurencesFunded++;
             }
         }
         
         public void startDocument() throws SAXException
         {   this.isRunning = true;
             this.occurencesFunded = 0;
         }
         
         public void endDocument() throws SAXException
         {   this.isRunning = false; }
         
         /** tell if the handler is running
          * @return true if the handler is running
          */
         public boolean isRunning()
         {   
             if ( JavaLangageServiceProvider.this.shouldReinit )
                 return false;
             
             if ( this.occurencesToFind == -1 )
                return this.isRunning;
             else
                return this.occurencesFunded == this.occurencesToFind;
         }
     };
     
     public class DoneParsingException extends SAXException
     {  }
    
    /* #########################################################################
     * ####################### Extended Jar File ###############################
     * ######################################################################### */
     
     private class JarFileProhibitingAcces extends JarFile
     {
         private List<String> packageForbidden = null;
         
         public JarFileProhibitingAcces(File file, String[] notAllowedPackage) throws IOException
         {  super(file);
            
            if ( notAllowedPackage != null )
            {   this.packageForbidden = new ArrayList<String>(notAllowedPackage.length);
                
                for(int i = 0; i < notAllowedPackage.length; i++)
                    this.packageForbidden.add(notAllowedPackage[i]);
            }
         }
         
         /** indicates if the package is recommended to be used
          * @param pack the string name of a super package
          * @return true if the package is recommended to be used
          */
         public boolean isRecommendedToBeUsed(String pack)
         {  if ( this.packageForbidden == null ) return true;
            
            for(Iterator<String> k = this.packageForbidden.iterator(); k.hasNext();)
            {   if ( pack.startsWith(k.next()) )
                    return false;
            }
            return true;
         }
         
     }
     
    /* #########################################################################
     * ################## Comparator for LangageElement ########################
     * ######################################################################### */
     
     private class LangageElementComparator implements Comparator
     {   public int compare(Object o1, Object o2)
         {   if ( ! (o1 instanceof LangageElement) || o1 == null)
                 return -1;
             if ( ! (o2 instanceof LangageElement) || o2 == null)
                 return 1;
             return ((LangageElement)o1).getCompletionObject().compareTo(((LangageElement)o2).getCompletionObject());
         }
     }
    
    public static void main(String[] args)
    {   System.out.println("lieu d'installation de la machine java : " + System.getProperty("java.home"));
        
        JavaLangageServiceProvider.getInstance().initialize();
        
        JavaLangageServiceProvider.getInstance().getPackagesNameContainingClass("String");
//         JavaLangageServiceProvider.getInstance().getChildrenPackageFor("java", "");
        
//         JavaLangageServiceProvider.getInstance().getClassFor("", "String");
//         JavaLangageServiceProvider.getInstance().getChildrenPackageFor(null, null);
//        System.out.println("length : " + a.getLength());
        
    }
}
