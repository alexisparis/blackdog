/*
 * blackdog : audio player / manager
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
package org.blackdog.action.impl;

import org.blackdog.kernel.MusikKernelResources;
import org.siberia.kernel.KernelResources;
import org.siberia.ui.action.impl.ViewGlobalItemAction;

/**
 *
 * Action that allow to visualize the cover search
 *
 * @author alexis
 */
public class ViewCoverSearch extends ViewGlobalItemAction
{   
    /**
     * Creates a new instance of ViewCoverSearch
     */
    public ViewCoverSearch()
    {   super(MusikKernelResources.ID_COVER_SEARCH); }
    
}
