/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.bar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import org.apache.log4j.Logger;
import org.siberia.bar.customizer.DefaultBarCustomizer;
import org.siberia.env.SiberExtension;
import org.siberia.ResourceLoader;
import org.siberia.exception.ResourceException;
import org.siberia.env.PluginResources;
import org.siberia.ui.action.ParameterizedAction;
import org.siberia.xml.schema.bar.ParameterType;
import org.siberia.utilities.util.Parameter;

/**
 *
 * @author alexis
 */
public class PluginBarCustomizer<E> extends DefaultBarCustomizer<E>
{
    /** logger */
    private Logger logger = Logger.getLogger(PluginBarCustomizer.class);
    
    /** create an icon according to the representation of an icon defined in the xml bar definition or 
     *	in an internationzalition resource
     *	@param iconRepresentation
     *	@return an Icon or null
     */
    protected Icon createIcon(String iconRepresentation)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createIcon(String)");
	    logger.debug("calling createIcon(String) with " + iconRepresentation);
	}
	
	Icon icon = null;

	try
	{
	    icon = ResourceLoader.getInstance().createIcon(
		    ResourceLoader.getInstance().getRcResource(iconRepresentation));
	}
	catch (org.siberia.exception.ResourceException e)
	{
	    logger.error("unable to get icon from '" + iconRepresentation + "'", e);
	}

	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createIcon(String) returns " + icon);
	    logger.debug("exiting createIcon(String)");
	}
	
	return icon;
    }

    /** return an instance of of the class represented by the given class name reference<br>
     *	the reference could be a classic classname of any kind of form if your customizer is able to load the 
     *	related class
     *	@param classNameRef a reference to a class
     *	@return an Action
     */
    @Override
    protected Action createAction(String classNameRef)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createAction(String)");
	    logger.debug("calling createAction(String) with " + classNameRef);
	}
	
	Action action = null;

	if (classNameRef != null)
	{
	    try
	    {
		Class c = ResourceLoader.getInstance().getClass(classNameRef);
		action = (Action) c.newInstance();
	    }
	    catch (Exception e)
	    {
		logger.warn("unable to find class '" + classNameRef + "' with resource loader", e);
	    }
	}

	if (action == null)
	{
	    logger.info("could not create action --> ask super class to create action with classNameRef=" + classNameRef);
	    action = super.createAction(classNameRef);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createAction(String) returns " + action);
	    logger.debug("exiting createAction(String)");
	}

	return action;
    }

    /** customize action
     *	@param action an Action
     *  @param parameters a list of parameter
     */
    @Override
    protected void customizeAction(Action action, List parameters)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering customizeAction(Action, List)");
	    logger.debug("calling customizeAction(Action, List) with action = " + action);
	}
	if (action instanceof ParameterizedAction)
	{
	    List<Parameter> wrappers = null;
	    if (parameters != null && parameters.size() > 0)
	    {
		wrappers = new ArrayList<Parameter>(parameters.size());

		Iterator it = parameters.iterator();
		while (it.hasNext())
		{
		    Object current = it.next();

		    if (current instanceof ParameterType)
		    {
			ParameterType p = (ParameterType) current;
			wrappers.add(new Parameter(p.getName(), p.getValue()));
		    }
		}
	    }
	    if (wrappers == null)
	    {
		wrappers = Collections.emptyList();
	    }
	    ((ParameterizedAction) action).setParameters(wrappers);
	}
	else
	{
	    super.customizeAction(action, parameters);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting customizeAction(Action, List)");
	}
    }
}
