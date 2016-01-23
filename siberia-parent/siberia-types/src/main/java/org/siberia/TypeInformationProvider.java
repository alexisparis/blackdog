/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia;

import javax.swing.Icon;
import org.siberia.type.annotation.bean.Bean;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.BeanConstants;
import org.siberia.utilities.xml.dom.XMLTree;
import org.siberia.utilities.xml.dom.XMLTreeFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.siberia.exception.ResourceException;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * @author alexis
 */
public class TypeInformationProvider
{
    /** logger */
    private static Logger logger = Logger.getLogger(TypeInformationProvider.class);
    /** key defining the display name of a Bean in a ResourceBundle */
    
    /** status indicating that types has been added or removed */
    public static  final String INVALIDATION             = "invalidation";
    
    private static final String NODE_HIERARCHY           = "inheritanceHierarchy";
    public  static final String NODE_CLASS               = "class";
    public  static final String ATTR_NAME                = "name";
    public  static final String ATTR_ABSTRACT            = "abstract";
    public  static final String ATTR_PLUGIN_ID           = "pluginId";
    
    /** path to the inheritance xml file */
    private static final String INHERITANCE_FILE_PATH = System.getProperty("user.home") + File.separator +
                                                        "." + ResourceLoader.getInstance().getApplicationName() + File.separator +
                                                        "inheritanceHierarchy.xml";
    
    
    
    /** weak hashmap that link internationalization ref to ResourceBundle */
    private WeakHashMap<String, ResourceBundle> rbMap        = new WeakHashMap<String, ResourceBundle>();
    
    /** indicates if the inheritance hierarchy has been invalidate */
    private boolean                             invalidate   = false;
    
    /* list of StatusListener */
    private Set<StatusListener>                 listeners    = null;
    
    /** hierarchy */
    private InheritanceHierarchy                hierarchy    = null;
    
    /** map that link a type class to the id of the plugin that declared this type */
    private static Map<String, String>          typePluginId = null;
    
    /** singleton */
    private static TypeInformationProvider      instance     = null;
    
    /** Creates a new instance of TypeInformationProvider */
    private TypeInformationProvider()
    {   }
    
    /** return the singleton
     *  @return a TypeInformationProvider
     */
    public static TypeInformationProvider getInstance()
    {   if ( instance == null )
            instance = new TypeInformationProvider();
        return instance;
    }
    
    /* #########################################################################
     * ############# methods that give information about a class ###############
     * ######################################################################### */
    
    /** return the id of the plugin that declare the given SibType class
     *	@param typeClass a Class implementing SibType
     *  @return the id of he plugin that declare this type
     */
    public static String getPluginDeclaring(Class typeClass)
    {
	String pluginId = null;
	
	if ( typeClass != null )
	{
	    if ( typePluginId == null )
		feedTypePluginMap();
	    pluginId = typePluginId.get(typeClass.getName());
	}
	
	return pluginId;
    }
    
    /** return an information linked to a SibType Class
     *  @param typeClass a Class implementing SibType
     *  @param kind of information to returns (static constants declared in BeanConstants)
     */
    public static Object getInformation(Class typeClass, String kind)
    {   Object o = null;
        if ( SibType.class.isAssignableFrom(typeClass) && kind != null )
        {   Bean t = (Bean)typeClass.getAnnotation(Bean.class);
            
            if ( t != null )
            {
                if ( kind.equals(BeanConstants.BEAN_NAME) )
                {   o = t.name(); }
                else if ( kind.equals(BeanConstants.BEAN_EXPERT) )
                {   o = t.expert(); }
                else if ( kind.equals(BeanConstants.BEAN_HIDDEN) )
                {   o = t.hidden(); }
                else if ( kind.equals(BeanConstants.BEAN_PREFERRED) )
                {   o = t.preferred(); }
                else
                {   /** get the resource bundle declared by typeClass */
                    String i18nRef = t.internationalizationRef();
                    
                    if ( i18nRef != null )
                    {   /** load ResourceBundle */
                        ResourceBundle rb = null;
                        
                        if ( typePluginId == null )
                            feedTypePluginMap();
                        String pluginId = typePluginId.get(typeClass.getName());
                        ClassLoader cl = null;
                        
                        /** if the class type is not declared in the plugin declaration, perhaps, the name is given like a siberia classname siberia::org.siberia.??? */
                        if ( pluginId == null )
                        {   PluginClass c = new PluginClass(i18nRef);
                            
                            if ( c.getPlugin() != null && c.getPlugin().trim().length() > 0 )
                            {   i18nRef = c.getClassName();
                                cl = ResourceLoader.getInstance().getPluginClassLoader(c.getPlugin());
                            }
                        }
                        else
                        {   cl = ResourceLoader.getInstance().getPluginClassLoader(pluginId); }
                        
                        if ( cl == null )
                        {   rb = ResourceBundle.getBundle(i18nRef); }
                        else
                        {   rb = ResourceBundle.getBundle(i18nRef, Locale.getDefault(), cl); }

                        if ( rb != null )
                        {   o = rb.getString(kind); }
                    }
                }
            }
            
            /* if not found, try to find it on superClass */
            if ( o == null && ! typeClass.equals(Object.class) )
            {   o = getInformation(typeClass.getSuperclass(), kind); }
        }
        
        return o;
    }
    
    /** return an information linked to a SibType
     *  @param class a Class implementing SibType
     *  @param kind of information to returns (static constants declared in BeanConstants)
     */
    public static Object getInformation(SibType type, String kind)
    {   return (type == null ? null : getInformation(type.getClass(), kind)); }
    
    /** return an icon information linked to a SibType Class
     *  @param class a Class implementing SibType
     *  @param kind of information to returns (static constants declared in BeanConstants)
     *  @return a String representing the path to the icon
     */
    public static String getIconInformation(Class typeClass, String kind)
    {   Object o = getInformation(typeClass, kind);
        if ( o instanceof String )
            return (String)o;
        return null;
    }
    
    /** return an icon information linked to a SibType instance
     *  @param type an Object
     *  @param kind of information to returns (static constants declared in BeanConstants)
     *  @return a String representing the path to the icon
     */
    public static String getIconInformation(Object type, String kind)
    {   return (type == null ? null : getIconInformation(type.getClass(), kind)); }
    
    /** return the string representation of the icon resource color 16 <br>
     *  linked to a type
     *  @param class a Class implementing SibType
     */
    public static String getIconInformation(Class typeClass)
    {   Object o = getInformation(typeClass, BeanConstants.BEAN_PLUGIN_ICON_COLOR_16);
        String path = null;
        if ( o instanceof String )
        {   path = (String)o; }
        
        return path;
    }
    
    /** return the string representation of the icon resource color 16 <br>
     *  linked to a type
     *  @param class a Class implementing SibType
     *
     *  @deprecated use getIconInformation
     */
    @Deprecated
    public static String getIconResource(Class typeClass)
    {   return getIconInformation(typeClass); }
    
    /** return the string representation of the icon resource color 16 <br>
     *  linked to a type
     *  @param type an Object
     */
    public static String getIconInformation(Object type)
    {   return (type == null ? null : getIconInformation(type.getClass())); }
    
    /** return the string representation of the icon resource color 16 <br>
     *  linked to a type
     *  @param type an Object
     *
     *  @deprecated use getIconInformation
     */
    @Deprecated
    public static String getIconResource(Object type)
    {   return getIconInformation(type); }
    
    /** return the string representation of the display name of the class
     *  @param class a Class implementing SibType
     */
    public static String getDisplayName(Class typeClass)
    {   Object o = getInformation(typeClass, BeanConstants.BEAN_DISPLAY_NAME);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the display name of the type
     *  @param type a SibType
     */
    public static String getDisplayName(SibType type)
    {   return (type == null ? null : getDisplayName(type.getClass())); }
    
    /** return the string representation of the description of the class
     *  @param class a Class implementing SibType
     */
    public static String getDescription(Class typeClass)
    {   Object o = getInformation(typeClass, BeanConstants.BEAN_DESCRIPTION);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the description of the type
     *  @param type a SibType
     */
    public static String getDescription(SibType type)
    {   return (type == null ? null : getDescription(type.getClass())); }
    
    /** return the string representation of the short description of the class
     *  @param class a Class implementing SibType
     */
    public static String getShortDescription(Class typeClass)
    {   Object o = getInformation(typeClass, BeanConstants.BEAN_SHORT_DESCRIPTION);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the short description of the type
     *  @param type a SibType
     */
    public static String getShortDescription(SibType type)
    {   return (type == null ? null : getShortDescription(type.getClass())); }
    
    /** return an information linked to the property of a field in a SibType Class
     *  @param class a Class implementing SibType
     *  @param fieldName the name of the field
     *  @param kind of information to returns (static constants declared in BeanConstants)
     */
    public static Object getPropertyInformation(Class typeClass, String fieldName, String kind)
    {   Object o = null;
        if ( SibType.class.isAssignableFrom(typeClass) && fieldName != null )
        {   try
            {   Field field = typeClass.getDeclaredField(fieldName);
                
                BeanProperty t = (BeanProperty)field.getAnnotation(BeanProperty.class);
                
                if ( t != null )
                {
                    if ( kind.equals(BeanConstants.BEAN_NAME) )
                    {   o = t.name(); }
                    else if ( kind.equals(BeanConstants.BEAN_EXPERT) )
                    {   o = t.expert(); }
                    else if ( kind.equals(BeanConstants.BEAN_HIDDEN) )
                    {   o = t.hidden(); }
                    else if ( kind.equals(BeanConstants.BEAN_PREFERRED) )
                    {   o = t.preferred(); }
                    else if ( kind.equals(BeanConstants.BEAN_PROPERTY_BOUND) )
                    {   o = t.bound(); }
                    else if ( kind.equals(BeanConstants.BEAN_PROPERTY_CONSTRAINED) )
                    {   o = t.constrained(); }
                    else
                    {   /** get the resource bundle declared by typeClass */
                        String i18nRef = t.internationalizationRef();
                        if ( i18nRef != null )
                        {   /** load ResourceBundle */
                            ResourceBundle rb = null;
                            if ( typePluginId == null )
                                feedTypePluginMap();
                            String pluginId = typePluginId.get(typeClass.getName());

                            ClassLoader cl = ResourceLoader.getInstance().getPluginClassLoader(pluginId);

                            if ( cl == null )
                            {   rb = ResourceBundle.getBundle(i18nRef); }
                            else
                            {   rb = ResourceBundle.getBundle(i18nRef, Locale.getDefault(), cl); }

                            if ( rb != null )
                            {   o = rb.getString(kind); }
                        }
                    }
                }
            }
            catch(Exception e)
            {   //e.printStackTrace();
            }

            /* if not found, try to find it on superClass */
            if ( o == null && ! typeClass.equals(Object.class) )
            {   o = getPropertyInformation(typeClass.getSuperclass(), fieldName, kind); }
        }
        
        return o;
    }
    
    /** return an information linked to the property of a field in a SibType instance
     *  @param class a Class implementing SibType
     *  @param fieldName the name of the field
     *  @param kind of information to returns (static constants declared in BeanConstants)
     */
    public static Object getPropertyInformation(SibType type, String fieldName, String kind)
    {   return (type == null ? null : getInformation(type.getClass(), kind)); }
    
    /** return the string representation of the display name of a property of the class
     *  @param class a Class implementing SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyDisplayName(Class typeClass, String fieldName)
    {   Object o = getPropertyInformation(typeClass, fieldName, BeanConstants.BEAN_DISPLAY_NAME);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the display name of a property of the type
     *  @param type a SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyDisplayName(SibType type, String fieldName)
    {   return (type == null ? null : getPropertyDisplayName(type.getClass(), fieldName)); }
    
    /** return the string representation of the description of the class
     *  @param class a Class implementing SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyDescription(Class typeClass, String fieldName)
    {   Object o = getPropertyInformation(typeClass, fieldName, BeanConstants.BEAN_DESCRIPTION);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the description of the type
     *  @param type a SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyDescription(SibType type, String fieldName)
    {   return (type == null ? null : getPropertyDescription(type.getClass(), fieldName)); }
    
    /** return the string representation of the short description of the class
     *  @param class a Class implementing SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyShortDescription(Class typeClass, String fieldName)
    {   Object o = getPropertyInformation(typeClass, fieldName, BeanConstants.BEAN_SHORT_DESCRIPTION);
        String name = null;
        if ( o instanceof String )
        {   name = (String)o; }
        
        return name;
    }
    
    /** return the string representation of the short description of the type
     *  @param type a SibType
     *  @param fieldName the name of the field
     */
    public static String getPropertyShortDescription(SibType type, String fieldName)
    {   return (type == null ? null : getPropertyShortDescription(type.getClass(), fieldName)); }
    
    /* #########################################################################
     * ###### methods that give information about relation between classes #####
     * ######################################################################### */
    
    /** return the root ClassElement
     *  @return a ClassElement
     */
    public ClassElement getRootClassElement()
    {   if ( this.hierarchy == null )
        {   this.createInheritanceHierarchy(); }
        return hierarchy.getRoot();
    }
    
    /** add a StatusListener to this entity
     *  @param listener an instance of StatusListener
     */
    public void addInvalidationListener(StatusListener listener)
    {   if ( listener != null )
        {   if ( listeners == null )
                listeners = new HashSet<StatusListener>();
            listeners.add(listener);
        }
    }
    
    /** remove a StatusListener to this entity
     *  @param listener an instance of StatusListener
     */
    public void removeInvalidationListener(StatusListener listener)
    {   if ( listeners != null && listener != null )
        {   listeners.remove(listener); }
    }
    
    /** fire a status changed
     *  @param property the name of the property
     */
    private void fireStatusChanged(String property)
    {   if ( listeners != null )
        {   Iterator<StatusListener> it = listeners.iterator();
            while(it.hasNext())
                it.next().statusChanged(property);
        }
    }
    
    /** return a list of class that inherits the given class (include abstract classes)
     *  @param superClass a Class that will be the super class for all returned class
     *  @param includeCurrent true if superClass is allowed to be included
     *  @return a list of class
     */
    public List<Class> getSubClassFor(Class superClass, boolean includeCurrent)
    {   return getSubClassFor(superClass, includeCurrent, true); }
    
    /** return a list of class that inherits the given class and implements the given interface (include abstract classes)
     *  @param superClass a Class that will be the super class for all returned class
     *  @param includeCurrent true if superClass is allowed to be included
     *  @param acceptAbstract true if abstract class have to be included
     *  @return a list of class
     */
    public List<Class> getSubClassFor(Class superClass, boolean includeCurrent, boolean acceptAbstract)
    {   return getSubClassFor(superClass, includeCurrent, acceptAbstract, null); }
    
    /** return a list of class that inherits the given class and implements the given interface
     *  @param superClass a Class that will be the super class for all returned class
     *  @param includeCurrent true if superClass is allowed to be included
     *  @param acceptAbstract true if abstract class have to be included
     *  @param interfaces an array of interfaces that have to implements all returned classes
     *  @return a list of class
     */
    public List<Class> getSubClassFor(Class superClass, boolean includeCurrent, boolean acceptAbstract, Class... interfaces)
    {   return getSubClassFor(superClass, includeCurrent, acceptAbstract, false, interfaces); }
    
    /** return a list of class that inherits the given class and implements the given interface
     *  @param superClass a Class that will be the super class for all returned class
     *  @param includeCurrent true if superClass is allowed to be included
     *  @param acceptAbstract true if abstract class have to be included
     *  @param includeHidden true to include the class declared as hidden
     *  @param interfaces an array of interfaces that have to implements all returned classes
     *  @return a list of class
     */
    public List<Class> getSubClassFor(Class superClass, boolean includeCurrent, boolean acceptAbstract, boolean includeHidden, Class... interfaces)
    {   
        StringBuffer buffer = new StringBuffer();
        buffer.append("calling(getSubClassFor(superclass=" + superClass + ", includeCurrent=" + includeCurrent + ", acceptAbstract=" + acceptAbstract);
        buffer.append(", interfaces=[");
        
        if ( interfaces != null )
        {   for(int i = 0; i < interfaces.length; i++)
            {   buffer.append(interfaces[i]);
                
                if ( i < interfaces.length - 1 )
                {   buffer.append(";"); }
            }
        }
        
        buffer.append("])");
        
        logger.debug(buffer.toString());
        
        if ( shouldRewriteInheritanceHierarchy() )
            createInheritanceHierarchy();
        
        List<Class> classes = null;
        if ( superClass != null )
        {   
            if ( includeCurrent )
            {   if ( acceptAbstract || ! Modifier.isAbstract(superClass.getModifiers()) )
                {   /* test interfaces */
                    if ( interfaces != null )
                    {   boolean ok = true;
                        for(int i = 0; i < interfaces.length; i++)
                        {   Class intf = interfaces[i];
                            if ( intf != null )
                            {   if ( ! intf.isAssignableFrom(superClass) )
                                {   ok = false;
                                    break;
                                }
                            }
                        }
                        if ( ok )
                        {   if ( classes == null )
                                classes = new ArrayList<Class>();
                            classes.add(superClass);
                        }
                    }
                    else
                    {   if ( classes == null )
                            classes = new ArrayList<Class>();
                        classes.add(superClass);
                    }
                }
            }
            
            /* used X path to obtains classes */
            try
            {   
                /** ensure that the inheritance file is created */
                if ( this.shouldRewriteInheritanceHierarchy() )
                {   this.createInheritanceHierarchy(); }
                
                InputSource inputSource = new InputSource(new FileInputStream(INHERITANCE_FILE_PATH));
                XPathFactory xPathFact = XPathFactory.newInstance();
                XPath path = xPathFact.newXPath();

                /* compile expression */
                String expression = "//" + NODE_CLASS +
                                    "[@name ='" + superClass.getName() + "']" + "/*";

                XPathExpression xExpress = path.compile(expression);

                NodeList list = (NodeList)xExpress.evaluate(inputSource, XPathConstants.NODESET);
                
                logger.debug("xpath expression '" + expression + "' gives " + list.getLength() + " nodes");

                if ( classes == null )
                {   classes = new ArrayList<Class>(); }
                
                for(int i = 0; i < list.getLength(); i++)
                {   processClassNode(list.item(i), acceptAbstract, includeHidden, classes, interfaces); }
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        
        if ( classes == null )
            return Collections.EMPTY_LIST;
        return classes;
    }
    
    /** add information if the node represent a class 
     *  @param node a xml node
     *  @param acceptAbstract true if abstract class have to be included
     *  @param includeHidden true to include the class declared as hidden
     *  @param list the list to fill
     *  @param interfaces an array of interfaces that have to implements all returned classes
     */
    private void processClassNode(Node node, boolean acceptAbstract, boolean includeHidden, List<Class> list, Class... interfaces)
    {   logger.debug("calling processClassNode(" + node + ", acceptAbstract=" + acceptAbstract + ", includeHidden=" + includeHidden + ")");
        if ( node != null && list != null )
        {   if ( node.getNodeName().equals(NODE_CLASS) )
            {   Node attrAbstract = node.getAttributes().getNamedItem(ATTR_ABSTRACT);
                if ( attrAbstract != null )
                {   
                    boolean isAbstract = Boolean.parseBoolean(attrAbstract.getNodeValue());
                    
                    if ( acceptAbstract || ! isAbstract )//  ! (isAbstract != acceptAbstract && ! acceptAbstract) )
                    {   try
                        {   Class c = ResourceLoader.getInstance().getClass(node.getAttributes().
                                                getNamedItem(ATTR_PLUGIN_ID).getNodeValue() + "::" + node.getAttributes().
                                                getNamedItem(ATTR_NAME).getNodeValue());
                            logger.debug("node related to class " + c);
                            
                            boolean accepted = true;
                            if ( interfaces != null )
                            {
                               
                                for(int i = 0 ; i < interfaces.length; i++)
                                {
                                    Class currentInterface = interfaces[i];
                                    
                                    if ( currentInterface != null && ! currentInterface.isAssignableFrom(c) )
                                    {
                                        logger.debug("class " + c + " does not implements " + currentInterface + " --> " + c + " ignored");
                                        accepted = false;
                                        break;
                                    }
                                }
                            }
                            
                            if ( accepted )
                            {
                                if ( ! includeHidden )
                                {
                                    /** try to get the annotation Bean */
                                    Bean beanAnnotation = (Bean)c.getAnnotation(Bean.class);
                                    
                                    if ( beanAnnotation != null )
                                    {
                                        if ( beanAnnotation.hidden() )
                                        {
                                            accepted = false;
                                            logger.debug("class " + c + " is declared as hidden --> ignored");
                                        }
                                    }
                                }
                            }
                            
                            if ( accepted )
                            {
                                logger.debug("adding class " + c);
                                list.add(c);
                            }
                            else
                            {
                                logger.debug("rejecting class " + c);
                            }
                        }
                        catch(ResourceException e)
                        {   e.printStackTrace();
                            
                            logger.error("error received", e);
                        }
                    }
                            
                    /* process child node even if the class is abstract and we don't care about abstract class */
                    NodeList children = node.getChildNodes();
                    if ( children != null )
                    {   
                        logger.debug("processing sub nodes");
                        for(int i = 0; i < children.getLength(); i++)
                            processClassNode(children.item(i), acceptAbstract, includeHidden, list, interfaces);
                    }
                }
            }
            
        }
    }
    
    /** invalidate the current inheritance hierarchy */
    public void invalidate()
    {   invalidate = true;
        
        fireStatusChanged(INVALIDATION);
    }
    
    /** tell if the hierarchy file has to be rewritten
     *  @return true if the hierarchy file has to be rewritten
     */
    private boolean shouldRewriteInheritanceHierarchy()
    {   
        boolean rewrite = true;
//        if ( invalidate )
//            return true;
//        File f = new File(INHERITANCE_FILE_PATH);
//        if ( ! f.exists() )
//            return true;
//        return false;
        
        // PENDING : a revoir
        logger.debug("rewrite iheritance declaration ? " + rewrite);
        return rewrite;
    }
    
    /** feed the map typePlugin */
    private static void feedTypePluginMap()
    {   
        typePluginId = new HashMap<String, String>();
        
        /** inspect plugin to look for type extensions */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(SiberiaTypesPlugin.TYPE_PLUGIN_ID);
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
                try
                {   Class typeClass = ResourceLoader.getInstance().getClass(
                                            currentExtension.getStringParameterValue("class"));
                    
                    /** complete the map typePluginId */
                    typePluginId.put(typeClass.getName(), currentExtension.getPluginId());
                }
                catch(ResourceException e)
                {   e.printStackTrace(); }                
            }
        }
    }
    
    /** permet d'alimenter un fichier xml d�crivant la relation d'h�ritage existant pour les types */
    private void createInheritanceHierarchy()
    {   this.hierarchy = new InheritanceHierarchy(SibType.class, SiberiaTypesPlugin.PLUGIN_ID);
        
        /** inspect plugin to look for type extensions */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(SiberiaTypesPlugin.TYPE_PLUGIN_ID);
        logger.debug("getting " + (extensions == null ? 0 : extensions.size()) + " types definition to integrate in inheritance declaration");
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            
            /** create a map that contains classname as key and pluginId as value */
            Map<String, String> pluginOwners = new HashMap<String, String>(extensions.size());
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
                try
                {   PluginClass pluginClass = new PluginClass(currentExtension.getStringParameterValue("class"));
                    pluginOwners.put(pluginClass.getClassName(), pluginClass.getPlugin());
                    logger.debug("plugin declaring type '" + pluginClass.getClassName() + "' --> '" + pluginClass.getPlugin() + "'");
                }
                catch(IllegalArgumentException e)
                {   logger.error("unable to proceed type definition : " + 
                            currentExtension.getStringParameterValue("class"), e);
                }
            }
            
            ClassElement.typePluginId = pluginOwners;
            
            /** feed hierarchy */
            it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
                try
                {   String completeName = currentExtension.getStringParameterValue("class");
                    PluginClass pluginClass = new PluginClass(completeName);
                    Class typeClass = ResourceLoader.getInstance().getClass(completeName);
                    
                    this.hierarchy.addClass(typeClass, pluginClass.getPlugin());
                    logger.debug("found type definition {" + typeClass + ", " + pluginClass.getPlugin() + "}");
                }
                catch(ResourceException e)
                {   e.printStackTrace(); }                
            }
        }
        
        /** save the hierarchy */
        logger.debug("marshalling inheritance declaration");
        this.hierarchy.marshall(INHERITANCE_FILE_PATH);
        
        invalidate = false;
    }
    
    /* #########################################################################
     * ######################### inner classes #################################
     * ######################################################################### */
    
    /** describe links of inheritance for a given class */
    private static class InheritanceHierarchy
    {
        /** root element */
        private ClassElement root     = null;
        
        /** create a new InheritanceHierarchy
         *  @param c the base class
         *  @param pluginId
         */
        public InheritanceHierarchy(Class c, String pluginId)
        {   this.root = new ClassElement(c, pluginId); }
        
        /** return the base class
         *  @return the base class
         */
        public Class getBaseClass()
        {   if ( this.root == null )
                return null;
            return this.root.getRelatedClass();
        }
        
        /** return the root ClassElement
         *  @return a ClassElement
         */
        public ClassElement getRoot()
        {   return this.root; }
        
        /** add a new element class
         *  @param _c a new class
         *  @param pluginId the id of the plugin that declare the class
         *  @return the resulting ClassElement
         */
        public ClassElement addClass(Class _c, String pluginId)
        {   return this.root.addClass(_c, pluginId); }
        
        /** save the hierarchy
         *  @path the path of the file
         */
        public void marshall(String path)
        {   XMLTree manager = XMLTreeFactory.createManager();
            manager.createTreeNode(NODE_HIERARCHY);
            
            logger.debug("inheritance root item : " + this.root);
            
            if ( this.root != null )
                this.root.marshall(manager);
            
            File f = new File(path);
            
            if ( ! f.exists() )
            {   File parentFile = f.getParentFile();
                
                if ( ! parentFile.exists() )
                {   parentFile.mkdirs(); }
                
                try
                {   f.createNewFile(); }
                catch(IOException e)
                {   logger.error("unable to create '" + f + "'", e); }
            }
            
            f = manager.saveTreeNode(path);
            logger.debug("inheritance declaration file created : " + f + ", exists ? " + f.exists());
        }
    }
    
    /** class declaring a TypeInfoProvider status listener */
    public static interface StatusListener
    {
        /** indicate that the status of the provider has changed
         *  @param propertyName the name of the property
         */
        public void statusChanged(String propertyName);
        
    }
}
