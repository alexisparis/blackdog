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
package org.siberia.type.attic;

import java.util.HashSet;
import java.util.Set;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * @author alexis
 */
@Bean(  name="SibSet",
        internationalizationRef="org.siberia.rc.i18n.type.SibSet",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibSet extends SibCollection<HashSet<SibType>> implements Set
{
    /** create a new instance of SibSet that could contains SibType<br>
     *  Items can be edited, added and removed.
     */
    public SibSet()
    {   super(); }
    
    /** initialize the collection */
    protected HashSet<SibType> initCollection()
    {   return new HashSet<SibType>(); }
}
