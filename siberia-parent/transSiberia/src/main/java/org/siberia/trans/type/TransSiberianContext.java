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
package org.siberia.trans.type;

import org.siberia.trans.TransSiberia;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.type.AbstractSibType;
import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;

/**
 *
 * @author alexis
 */
@Bean(  name="TransSiberian context",
        internationalizationRef="org.siberia.rc.i18n.type.TransSiberianContext",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class TransSiberianContext extends SibList
{
    /** property transSiberia */
    public static final String PROPERTY_TRANSSIBERIA      = "trans-siberia";
    
    /** transSiberia */
    @BeanProperty(name=PROPERTY_TRANSSIBERIA,
                  internationalizationRef="org.siberia.rc.i18n.property.TransSiberianContext_transsiberian",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setTransSiberia",
                  writeMethodParametersClass={TransSiberia.class},
                  readMethodName="getTransSiberia",
                  readMethodParametersClass={}
                 )
    private TransSiberia transSiberian = null;
    
    /** Creates a new instance of TransSiberianContext */
    public TransSiberianContext()
    {	
	this.setAllowedClass(PluginBuild.class);
    }
    
    /** return the transSiberia of the context
     *	@return a TransSiberia
     */
    public TransSiberia getTransSiberia()
    {	return this.transSiberian; }
    
    /** initialize the transSiberia of the context
     *	@param trans a TransSiberia
     */
    public void setTransSiberia(TransSiberia trans)
    {	
	if ( trans != this.getTransSiberia() )
	{
	    TransSiberia old = this.getTransSiberia();
	    
	    this.transSiberian = trans;
	    
	    this.firePropertyChange(PROPERTY_TRANSSIBERIA, old, this.getTransSiberia());
	}
    }
    
}
