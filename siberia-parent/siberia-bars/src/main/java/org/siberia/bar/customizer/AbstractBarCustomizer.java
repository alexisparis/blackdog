/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.customizer;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.siberia.bar.i18n.I18NResources;
import org.siberia.bar.i18n.I18nResolver;

/**
 *
 * @author alexis
 */
public abstract class AbstractBarCustomizer<E> implements BarCustomizer<E>
{
    /** logger */
    protected Logger              logger          = Logger.getLogger(this.getClass());
    
    /** avoid successive separators */
    private   boolean             avoidSuccSep    = true;
    
    /** font to use */
    private   Font                font            = new Font("Arial", Font.PLAIN, 11);
    
    /** internationalization resolver */
    private   I18nResolver        i18nResolver    = null;
    
    /** list of internationaliaztion resources */
    private   List<I18NResources> resources       = null;
    
    /** Creates a new instance of AbstractBarCustomizer */
    public AbstractBarCustomizer()
    {	}
    
    /** indicate if succesive separators should be avoided
     *	@param avoid a boolean
     */
    protected void setSuccessiveSeparatorsAvoided(boolean avoid)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setSuccessiveSeparatorsAvoided(boolean)");
	}
	if ( avoid != this.avoidSuccessiveSeparators() )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing set avoid successive separators to " + avoid);
	    }
	    this.avoidSuccSep = avoid;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setSuccessiveSeparatorsAvoided(boolean)");
	}
    }
    
    /** return true if successive separator should be avoided
     *	@return a boolean
     */
    public boolean avoidSuccessiveSeparators()
    {	return this.avoidSuccSep; }
    
    /** set the font to use
     *	@param font a Font
     */
    public void setFont(Font font)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering SetFont(Font)");
	}
	if ( font == null )
	{   throw new IllegalArgumentException("font could not be null"); }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("changing font from " + this.getFont() + " to " + font);
	}
	
	this.font = font;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting SetFont(Font)");
	}
    }
    
    /** return the font to use
     *	@return a Font
     */
    public Font getFont()
    {	return this.font; }
    
    /* #########################################################################
     * ####################### I18N related methods ############################
     * ######################################################################### */
    
    /** set the I18nResolver related to this customizer
     *	@param resolver a I18nResolver
     */
    public void setI18NResolver(I18nResolver resolver)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering setI18NResolver(I18NResolver)");
	}
	if ( resolver != this.getI18NResolver() )
	{	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("changing i18n resolver from " + this.getI18NResolver() + " to " + resolver);
	    }
	    this.i18nResolver = resolver;
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting setI18NResolver(I18NResolver)");
	}
    }
    
    /** return the I18nResolver related to this customizer
     *	@return a I18nResolver
     */
    public I18nResolver getI18NResolver()
    {
	return this.i18nResolver;
    }
    
    /** return a String representing the internationalized value linked to the given key<br>
     *	this method searchs in all resources according to their priority
     *	if the key is found on a resources, then the search stopped
     *	@param key the key to search
     *	@return a String or null if not found
     */
    protected String getInternationalizedValue(String key)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering getInternationalizedValue(String)");
	    logger.debug("calling getInternationalizedValue(" + key + ")");
	}
	
	String value = null;
	
	I18nResolver resolver = this.getI18NResolver();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("using resolver " + resolver);
	}
	
	if ( key != null )
	{
	    if ( resolver == null )
	    {
		logger.info("no resolver set : internationalization resources won't be taken into account");
	    }
	    else
	    {
		List<I18NResources> list = this.getResources();
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("considering " + list.size() + " internationalization resources");
		}

		for(int i = 0; i < list.size() && value == null; i++)
		{
		    I18NResources rc = list.get(i);
		    if ( rc != null )
		    {
			ResourceBundle rb = resolver.getResource(rc);

			if ( rb == null )
			{
			    // print traces ??!!??
			}
			else
			{
			    try
			    {
				value = rb.getString(key);
			    }
			    catch(MissingResourceException e)
			    {   
				// well, ask another resources
			    }
			}
		    }
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("getInternationalizedValue(" + key + ") returns " + value);
	    logger.debug("exiting getInternationalizedValue(String)");
	}
	
	return value;
    }
    
    /** return a new List containing all the I18NResources sorted by priority<br>
     *	the first item is the resources to ask first
     *	@return a sorted List of I18NResources
     */
    protected synchronized List<I18NResources> getResources()
    {
	List<I18NResources> list = this.resources;
	
	if ( list == null )
	{
	    list = Collections.emptyList();
	}
	
	return list;
    }
    
    /** register a I18N resources
     *	@param resources a I18NResources
     */
    public synchronized void registerI18NResources(I18NResources resources)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering registerI18NResources(I18NResources)");
	    logger.debug("calling registerI18NResources with resources of code " + (resources == null ? null : resources.getCode()) + " with " +
			 "priority = " + (resources == null ? "unknown" : resources.getPriority()));
	}
	if ( resources != null )
	{
	    if ( this.resources == null )
	    {
		this.resources = new ArrayList<I18NResources>();
	    }
	    
	    /* determine the index where to insert the new element */
	    int index = 0;
	    
	    if ( this.resources.size() > 0 )
	    {
		boolean insertionFound = false;
		
		for(int i = 0; i < this.resources.size(); i++)
		{
		    int compare = resources.compareTo(this.resources.get(i));
		    
		    if ( compare < 0 )
		    {
			/** here we must insert it */
			index = i;
			insertionFound = true;
		    }
		}
		
		if ( ! insertionFound )
		{
		    index = this.resources.size();
		}
	    }
	    
	    this.resources.add(index, resources);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("inserting internationalization resources at index " + index);
		logger.debug("resources count after insertion : " + this.resources.size());
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting registerI18NResources(I18NResources)");
	}
    }
    
    /** unregister a I18N resources
     *	@param resources a I18NResources
     */
    public void unregisterI18NResources(I18NResources resources)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering unregisterI18NResources(I18NResources)");
	    logger.debug("calling unregisterI18NResources with resources of code " + (resources == null ? null : resources.getCode()));
	}
	if ( this.resources != null )
	{
	    if ( this.resources.remove(resources) )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("adding a I18NResources with code " + (resources == null ? null : resources.getCode()));
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting unregisterI18NResources(I18NResources)");
	}
    }
    
    /** clear internationalization resources */
    public void clearI18NResources()
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering clearI18NResources()");
	}
	if ( this.resources != null )
	{
	    this.resources.clear();
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting clearI18NResources()");
	}
    }
}
