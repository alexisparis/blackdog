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
package org.siberia.trans.type.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * Declaration of a module dependency
 * 
 * @author alexis
 */
@Bean(  name="plugin",
        internationalizationRef="org.siberia.rc.i18n.type.PluginDependency",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class PluginDependency extends AbstractSibType implements PluginConstants
{
    /** property version constraint */
    public static final String PROPERTY_VERSION_CONSTRAINT = "versionConstraint";
    
    /** version constraint of the plugin */
    @BeanProperty(name=PluginDependency.PROPERTY_VERSION_CONSTRAINT,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginDependency_versionConstraint",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setVersionConstraint",
                  writeMethodParametersClass={VersionConstraint.class},
                  readMethodName="getVersionConstraint",
                  readMethodParametersClass={}
                 )
    private VersionConstraint versionConstraint = new VersionConstraint();
    
    /** Creates a new instance of PluginDependency */
    public PluginDependency()
    {   
        try
        {   
            //this.setNameCouldChange(false);
            this.setMoveable(false);
//            this.setReadOnly(true);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }

    /** return the version constraint of the plugin
     *  @return a VersionConstraint object
     */
    public VersionConstraint getVersionConstraint()
    {   return versionConstraint; }

    /** initialize the version constraint of the software
     *  @param constraint a VersionConstraint
     */
    public void setVersionConstraint(VersionConstraint constraint)
    {   
        if ( constraint != this.getVersionConstraint() )
        {   
	    VersionConstraint oldConstraint = this.getVersionConstraint();
	    
            this.versionConstraint = constraint;
            
            this.firePropertyChange(PROPERTY_VERSION_CONSTRAINT, oldConstraint, this.getVersionConstraint());
        }
    }

    @Override
    public boolean equals(Object t)
    {
	boolean equals = super.equals(t);
	
	if ( equals && t != null && this.getClass().equals(t.getClass()) )
	{
	    /** must be the same structure and have the same version */
	    PluginDependency other = (PluginDependency)t;
	    
	    if ( this.getName() == null )
	    {
		if ( other.getName() != null )
		{
		    equals = false;
		}
	    }
	    else
	    {
		equals = this.getName().equals(other.getName());
	    }
	    
	    if ( equals )
	    {
		if ( this.getVersionConstraint() == null )
		{
		    if ( other.getVersionConstraint() != null )
		    {
			equals = false;
		    }
		}
		else
		{
		    equals = this.getVersionConstraint().equals(other.getVersionConstraint());
		}
	    }
	}
	
	return equals;
    }
}
