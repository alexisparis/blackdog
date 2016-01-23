/* 
 * Siberia binding : siberia plugin defining persistence services
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
package org.siberia.binding.constraint;

/**
 *
 * @author alexis
 */
public class DataBaseBindingConstraint implements BindingConstraint
{
    /** the name of the property that will occurs on the constraints */
    private String             propertyName    = null;
    
    /** the relation describing the constraint */
    private ConstraintRelation relation        = null;
    
    /** Object describing the constraint to apply on the property */
    private Object             constraintValue = null;
    
    /** indicate if this constraint is active */
    private boolean            active          = true;
    
    /** Creates a new instance of DataBaseBindingConstraint
     *	@param propertyName the name of a property that will occurs on the constraints
     *	@param relation the relation describing the constraint
     *	@param constraint an Object describing the constraint to apply on the property
     */
    public DataBaseBindingConstraint(String propertyName, ConstraintRelation relation, Object constraint)
    {	
	this.propertyName = propertyName;
	this.relation = relation;
	this.constraintValue = constraint;
    }
    
    /** return the property that will occurs on the constraints
     *	@return the name of a property
     */
    public String getPropertyName()
    {
	return this.propertyName;
    }
    
    /** return the relation describing the constraint
     *	@return a Constraintrelation
     */
    public ConstraintRelation getRelation()
    {
	return this.relation;
    }
    
    /** return the constraint to apply on the property
     *	@return an Object
     */
    public Object getConstraintValue()
    {
	return this.constraintValue;
    }

    /** return true if this constraint is active
     *	@return a boolean
     */
    public boolean isActive()
    {
	return active;
    }

    /** indicate true if this constraint is active
     *	@return a boolean
     */
    public void setActive(boolean active)
    {
	this.active = active;
    }
    
}
