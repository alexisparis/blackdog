/*
 * ManifestSaxParser.java
 *
 * Created on 23 novembre 2007, 20:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.siberia;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Manifest handler
 *
 * @author alexis
 */
public class ManifestHandler extends DefaultHandler
{
    /** version of the plugin */
    private String              version               = null;
    
    /** name of the plugin */
    private String              plugin                = null;
    
    /** plugini18nDeclaration */
    private String              plugini18nDeclaration = null;
    
    /** check mode */
    private String              checkMode             = null;
    
    /** category */
    private String              category              = null;
    
    /** reboot needed */
    private Boolean             rebootNeeded          = null;
    
    /** map linking the name of a required plugin and its version */
    private Map<String, String> requirements          = new HashMap<String, String>();
    
    /** true if we are in requires */
    private boolean             inRequires            = false;
    
    /** Creates a new instance of ManifestSaxParser */
    public ManifestHandler ()
    {	}
    
    /** return the plugin id
     *	@return a String
     */
    public String getPluginId()
    {	return this.plugin; }
    
    /** return the plugin version
     *	@return a String
     */
    public String getPluginVersion()
    {	return this.version; }
    
    /** return the plugini18nDeclaration
     *	@return a String
     */
    public String getPlugini18nDeclaration()
    {	return this.plugini18nDeclaration; }
    
    /** return the check mode
     *	@return a String
     */
    public String getCheckMode()
    {	return this.checkMode; }
    
    /** return the category
     *	@return a String
     */
    public String getCategory()
    {	return this.category; }
    
    /** return true if the reboot is needed
     *	@return a Boolean
     */
    public Boolean isRebootNeeded()
    {	return this.rebootNeeded; }
    
    /** return a Map containing as key required plugin and as values the version required
     *	@retrun a Map
     */
    public Map<String, String> getRequirements()
    {	return this.requirements; }

    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
	super.startElement(uri, localName, qName, attributes);
	
	if ( qName.equals ("plugin") )
	{
	    for(int i = 0; i < attributes.getLength (); i++)
	    {
		String attr = attributes.getQName (i);
		String value = attributes.getValue (i);
		
		if ( attr.equals ("id") )
		{
		    this.plugin = attributes.getValue (i);
		}
		else if ( attr.equals ("version") )
		{
		    this.version = attributes.getValue (i);
		}
	    }
	}
	else if ( qName.equals ("requires") )
	{
	    this.inRequires = true;
	}
	else if ( qName.equals ("import") )
	{
	    if ( this.inRequires )
	    {
		/** add in map */
		String pluginIdRes = null;
		String versionRes  = null;
		
		for(int i = 0; i < attributes.getLength (); i++)
		{
		    String attr = attributes.getQName (i);

		    if ( attr.equals ("plugin-id") )
		    {
			pluginIdRes = attributes.getValue (i);
		    }
		    else if ( attr.equals ("plugin-version") )
		    {
			versionRes = attributes.getValue (i);
		    }
		}
		
		if ( pluginIdRes != null && versionRes != null )
		{
		    this.requirements.put (pluginIdRes, versionRes);
		}
	    }
	}
	else if ( qName.equals ("attribute") )
	{
	    boolean plugini18nDeclarationFound = false;
	    boolean categoryFound              = false;
	    boolean rebootNeededFound          = false;
	    boolean checkModeFound             = false;
	    
	    for(int i = 0; i < attributes.getLength (); i++)
	    {
		String attr = attributes.getQName (i);
		
		if ( attr.equals ("id") )
		{
		    if ( attributes.getValue (i).equals ("plugini18nDeclaration") )
		    {
			plugini18nDeclarationFound = true;
		    }
		    if ( attributes.getValue (i).equals ("category") )
		    {
			categoryFound = true;
		    }
		    if ( attributes.getValue (i).equals ("rebootNeeded") )
		    {
			rebootNeededFound = true;
		    }
		    if ( attributes.getValue (i).equals ("checkMode") )
		    {
			checkModeFound = true;
		    }
		}
	    }
	    
	    if ( plugini18nDeclarationFound || categoryFound || checkModeFound || rebootNeededFound )
	    {
		String value = null;
		
		for(int i = 0; i < attributes.getLength (); i++)
		{
		    String attr = attributes.getQName (i);

		    if ( attr.equals ("value") )
		    {
			value = attributes.getValue (i);
			break;
		    }
		}
		
		if ( value != null )
		{
		    if ( plugini18nDeclarationFound )
		    {
			this.plugini18nDeclaration = value;
		    }
		    if ( categoryFound )
		    {
			this.category = value;
		    }
		    if ( checkModeFound )
		    {
			this.checkMode = value;
		    }
		    if ( rebootNeededFound )
		    {
			this.rebootNeeded = Boolean.parseBoolean (value);
		    }
		}
	    }
	}
    }

    public void endElement (String uri, String localName, String qName) throws SAXException
    {
	super.endElement(uri, localName, qName);
	
	if ( qName.equals ("requires") )
	{
	    this.inRequires = false;
	}
    }
    
}
