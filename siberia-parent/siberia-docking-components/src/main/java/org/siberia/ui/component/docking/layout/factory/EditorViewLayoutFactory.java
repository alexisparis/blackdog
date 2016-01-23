/*
 * Siberia docking window : define an editor support based on Infonode docking window framework
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.siberia.ui.component.docking.layout.factory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.ui.component.docking.layout.BorderViewConstraints;
import org.siberia.ui.component.docking.layout.EditorLayoutRule;
import org.siberia.ui.component.docking.layout.EditorViewLayout;
import org.siberia.ui.component.docking.layout.ViewLayout;

/**
 *
 * factory fthat create an EditorViewLayout
 *
 * @author alexis
 */
public class EditorViewLayoutFactory implements ViewLayoutFactory
{
    /** logger */
    private Logger logger = Logger.getLogger(EditorViewLayoutFactory.class);
    
    /** editor prefix */
    private final String PREFIX_EDITOR           = "editor.";
    
    /** constraints prefix */
    private final String PREFIX_CONSTRAINTS      = "constraints.";
    
    /** rules prefix */
    private final String PREFIX_RULES            = "rules.";
    
    private final String HORIZONTAL_ALIGN        = "horizontalAlignment";
    private final String VERTICAL_ALIGN          = "verticalAlignment";
    
    /** rule property strict */
    private final String RULE_PARAMETER_STRICT   = "strict";
    
    /** rule property friend */
    private final String RULE_PARAMETER_FRIEND   = "friend.";
    
    /** rule property enemy */
    private final String RULE_PARAMETER_ENEMY    = "enemy.";
    
    /** rule property applyOn */
    private final String RULE_PARAMETER_APPLY_ON = "applyOn";
    
    /** Creates a new instance of EditorViewLayoutFactory */
    public EditorViewLayoutFactory()
    {	}
    
    /** return a ViewLayout according to the properties given
     *	@param properties a Properties object
     *	@return a ViewLayout
     */
    public ViewLayout createViewLayout(Properties properties)
    {
	EditorViewLayout layout = new EditorViewLayout();
	
	if ( properties != null )
	{
	    /* map of editor declaration */
	    Map<String, Class> editorClasses = new HashMap<String, Class>(10);
	    
	    /* list of properties items that start with constraints. */
	    List<String>        constraints  = new ArrayList<String>(10);
	    
	    /** map of rules */
	    Map<String, Map<String, String>> rules = new HashMap<String, Map<String, String>>(20);
	    
	    /* map linking editor name and related Class */
	    Enumeration enu = properties.propertyNames();
	    
	    while(enu.hasMoreElements())
	    {
		Object current = enu.nextElement();
		
		if ( current instanceof String )
		{
		    String property = (String)current;
		    
		    if ( property.startsWith(PREFIX_EDITOR) )
		    {
			String editorKindName = property.substring(PREFIX_EDITOR.length());
			
			if ( editorKindName != null && editorKindName.length() > 0 )
			{
			    /* get the value and try to load the related class */
			    String classname = properties.getProperty(property);
			    
			    try
			    {
				Class c = ResourceLoader.getInstance().getClass(classname);
				
				editorClasses.put(editorKindName, c);
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("register editor name '" + editorKindName + "' for class " + c);
				}
			    }
			    catch(ResourceException e)
			    {
				logger.error("could not load siberia class representation '" + classname + "'", e);
			    }
			}
		    }
		    else if ( property.startsWith(PREFIX_CONSTRAINTS) )
		    {
			String currentConstraint = property.substring(PREFIX_CONSTRAINTS.length());
			
			if ( currentConstraint != null )
			{
			    constraints.add(currentConstraint);
			}
		    }
		    else if ( property.startsWith(PREFIX_RULES) )
		    {
			String currentRule = property.substring(PREFIX_RULES.length());
			
			int pointIndex = currentRule.indexOf('.');
			
			if ( pointIndex < 0 )
			{
			    logger.error("could not parse rules '" + currentRule + "'");
			}
			else
			{
			    String ruleName = currentRule.substring(0, pointIndex);
			    String parameter = currentRule.substring(pointIndex + 1);
			    
			    Map<String, String> parameters = rules.get(ruleName);
			    
			    if ( parameters == null )
			    {
				parameters = new HashMap<String, String>(10);
				rules.put(ruleName, parameters);
			    }
			    
			    parameters.put(parameter, properties.getProperty(PREFIX_RULES + ruleName + "." + parameter));
			}
		    }
		}
	    }
	    
	    /** initialize position constraints for all editor registered */
	    Iterator<Map.Entry<String, Class>> editorEntries = editorClasses.entrySet().iterator();
	    
	    while(editorEntries.hasNext())
	    {
		Map.Entry<String, Class> currentEntry = editorEntries.next();
		
		if ( currentEntry != null )
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("building constraint for editor : " + currentEntry.getKey());
		    }
		    
		    if ( currentEntry.getKey() != null && currentEntry.getValue() != null )
		    {
			int hConstraint = BorderViewConstraints.NONE;
			int vConstraint = BorderViewConstraints.NONE;
			
			/* search for all constraints, those which are applied on the current editor
			 *  they starts with the name of the editor declaration
			 */
			
			StringBuffer buffer = null;
			
			if ( logger.isDebugEnabled() )
			{
			    buffer = new StringBuffer();
			}
			
			for(int i = 0; i < constraints.size(); i++)
			{   
			    String currentConstraint = constraints.get(i);
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("current constraint is : " + currentConstraint);
			    }
			    
			    if ( currentConstraint == null )
			    {
				
			    }
			    else
			    {
				if ( currentConstraint.startsWith(currentEntry.getKey()) )
				{
				    String constraintKey = currentConstraint.substring(currentEntry.getKey().length() + 1);
				    
				    if ( HORIZONTAL_ALIGN.equals(constraintKey) )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("found horizontal constraint");
					}
					
					/* get the value for this property */
					String value = properties.getProperty(PREFIX_CONSTRAINTS + currentConstraint);
					
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("horizontal constraint value is '" + value + "'");
					}
					
					if ( value == null )
					{
					    logger.error("could not parse horizontal alignment null for editor " + currentEntry.getKey());
					}
					else
					{
					    if ( value.equals("east") )
					    {
						hConstraint = BorderViewConstraints.EAST;
						if ( buffer != null )
						{
						    buffer.append("[hconstraint=east]");
						}
					    }
					    else if ( value.equals("west") )
					    {
						hConstraint = BorderViewConstraints.WEST;
						if ( buffer != null )
						{
						    buffer.append("[hconstraint=west]");
						}
					    }
					    else if ( value.equals("none") )
					    {
						hConstraint = BorderViewConstraints.NONE;
						if ( buffer != null )
						{
						    buffer.append("[hconstraint=none]");
						}
					    }
					    else if ( value.equals("center") )
					    {
						hConstraint = BorderViewConstraints.CENTER;
						if ( buffer != null )
						{
						    buffer.append("[hconstraint=center]");
						}
					    }
					    else
					    {
						logger.warn("could not parse horizontal alignment '" + value + "' for editor " + currentEntry.getKey());
					    }
					}
				    }
				    else if ( VERTICAL_ALIGN.equals(constraintKey) )
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("found vertical constraint");
					}
					/* get the value for this property */
					String value = properties.getProperty(PREFIX_CONSTRAINTS + currentConstraint);
					
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("vertical constraint value is '" + value + "'");
					}
					
					if ( value == null )
					{
					    logger.error("could not parse vertical alignment null for editor " + currentEntry.getKey());
					}
					else
					{
					    if ( value.equals("south") )
					    {
						vConstraint = BorderViewConstraints.SOUTH;
						if ( buffer != null )
						{
						    buffer.append("[vconstraint=south]");
						}
					    }
					    else if ( value.equals("north") )
					    {
						vConstraint = BorderViewConstraints.NORTH;
						if ( buffer != null )
						{
						    buffer.append("[vconstraint=north]");
						}
					    }
					    else if ( value.equals("none") )
					    {
						vConstraint = BorderViewConstraints.NONE;
						if ( buffer != null )
						{
						    buffer.append("[vconstraint=none]");
						}
					    }
					    else if ( value.equals("center") )
					    {
						vConstraint = BorderViewConstraints.CENTER;
						if ( buffer != null )
						{
						    buffer.append("[vconstraint=center]");
						}
					    }
					    else
					    {
						logger.warn("could not parse vertical alignment '" + value + "' for editor " + currentEntry.getKey());
					    }
					}
				    }
				    else
				    {
					logger.warn("could not parse constraint " + constraintKey + " for editor " + currentEntry.getValue());
				    }
				}
			    }
			}
			
			layout.setPosition(currentEntry.getValue(), new BorderViewConstraints(hConstraint, vConstraint));
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("setting position for " + currentEntry.getValue() + " with constraints (" + hConstraint +
					    ", " + vConstraint + ") {" + (buffer == null ? null : buffer.toString()) + "}");
			}
		    }
		}
	    }
	    
	    /** let's parse rules */
	    
	    Iterator<Map.Entry<String, Map<String, String>>> rulesEntries = rules.entrySet().iterator();
	    while(rulesEntries.hasNext())
	    {
		Map.Entry<String, Map<String, String>> ruleEntries = rulesEntries.next();
		
		if ( ruleEntries != null && ruleEntries.getValue() != null )
		{ 
		    /** create a rule with all this parameter */
		    EditorLayoutRule rule = null;
		    
		    String strictValue = ruleEntries.getValue().get(RULE_PARAMETER_STRICT);
		    
		    Boolean strict = null;
		    
		    if ( strictValue != null )
		    {
			strict = Boolean.parseBoolean(strictValue);
		    }
		    
		    if ( strict == null )
		    {
			rule = new EditorLayoutRule();
		    }
		    else
		    {
			rule = new EditorLayoutRule(strict);
		    }
		    
		    ruleEntries.getValue().remove(RULE_PARAMETER_STRICT);
		    
		    if ( logger.isDebugEnabled() )
		    {
			StringBuffer buffer = new StringBuffer();
			
			Iterator<String> paraemtersKeys = ruleEntries.getValue().keySet().iterator();
			while(paraemtersKeys.hasNext())
			{
			    String current = paraemtersKeys.next();
			    
			    buffer.append(current);
			    
			    if ( paraemtersKeys.hasNext() )
			    {
				buffer.append(", ");
			    }
			}
			
			buffer.insert(0, "[");
			buffer.append("]");
			
			logger.debug(ruleEntries.getKey() + " parameters : " + buffer.toString());
		    }
		    
		    String applyOnEditor = ruleEntries.getValue().get(RULE_PARAMETER_APPLY_ON);
		    
		    if ( applyOnEditor == null )
		    {
			logger.error("rule '" + ruleEntries.getKey() + "' miss parameter '" + RULE_PARAMETER_APPLY_ON + "' --> ignored");
		    }
		    else
		    {
			Class c = editorClasses.get(applyOnEditor);
			
			if ( c == null )
			{
			    logger.error("rule '" + ruleEntries.getKey() + "' apply on editor '" + applyOnEditor + "' which seems to be undefined --> ignored");
			}
			else
			{
			    ruleEntries.getValue().remove(RULE_PARAMETER_APPLY_ON);
			    
			    /* loop on all parameters and initailize friends and enemies */
			    Iterator<Map.Entry<String, String>> relationsParameters = ruleEntries.getValue().entrySet().iterator();
			    
			    while(relationsParameters.hasNext())
			    {
				Map.Entry<String, String> relationParameter = relationsParameters.next();
				
				if ( relationParameter != null )
				{
				    if ( relationParameter.getKey() == null )
				    {
					logger.error("unable to parse relational editor parameter null");
				    }
				    else if ( relationParameter.getKey().startsWith(RULE_PARAMETER_ENEMY) )
				    {
					/* search for the class related to relationParameter value */
					if ( relationParameter.getValue() == null )
					{
					    logger.warn("unable to associate a enemy Editor class with name null for rule '" + ruleEntries.getKey() + "'");
					}
					else
					{
					    Class enemyClass = editorClasses.get(relationParameter.getValue());
					    
					    if ( enemyClass == null )
					    {
						logger.error("rule '" + ruleEntries.getKey() + "' could not add '" + ruleEntries.getKey() + "' as an enemy --> it is undefined");
					    }
					    else
					    {
						rule.addEnemyClass(enemyClass);
						
						if ( logger.isDebugEnabled() )
						{
						    logger.error(relationParameter.getValue() + " added as enemy for rule '" + ruleEntries.getKey() + "'");
						    
						}
					    }
					}
				    }
				    else if ( relationParameter.getKey().startsWith(RULE_PARAMETER_FRIEND) )
				    {
					/* search for the class related to relationParameter value */
					if ( relationParameter.getValue() == null )
					{
					    logger.warn("unable to associate a friendly Editor class with name null for rule '" + ruleEntries.getKey() + "'");
					}
					else
					{
					    Class friendClass = editorClasses.get(relationParameter.getValue());
					    
					    if ( friendClass == null )
					    {
						logger.error("rule '" + ruleEntries.getKey() + "' could not add '" + ruleEntries.getKey() + "' as a friend --> it is undefined");
					    }
					    else
					    {
						rule.addFriendClass(friendClass);
						
						if ( logger.isDebugEnabled() )
						{
						    logger.error(relationParameter.getValue() + " added as friend for rule '" + ruleEntries.getKey() + "'");
						    
						}
					    }
					}
				    }
				}
			    }
			    
			    layout.setRule(c, rule);
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("rule '" + ruleEntries.getKey() + "' added to layout");
			    }
			}
		    }
		}
	    }
	}
	
	return layout;
    }
    
}
