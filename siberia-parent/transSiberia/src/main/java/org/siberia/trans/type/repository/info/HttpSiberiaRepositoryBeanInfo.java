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
package org.siberia.trans.type.repository.info;

import org.siberia.trans.type.repository.HttpSiberiaRepository;

/**
 *
 * Specific BeanInfo for HttpSiberiaRepository
 *
 * @author alexis
 */
public class HttpSiberiaRepositoryBeanInfo extends AbstractURLSiberiaRepositoryBeanInfo
{
    
    /** Creates a new instance of HttpSiberiaRepositoryBeanInfo */
    public HttpSiberiaRepositoryBeanInfo()
    {
	super(HttpSiberiaRepository.class);
    }
    
}
