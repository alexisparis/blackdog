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
package org.blackdog.ui.action.impl;

import org.blackdog.kernel.MusikKernelResources;
import org.siberia.ui.action.impl.ViewResourceAction;

/**
 *
 * Allow user to re-open the scanning parameters if it was closed or to given focus to it if it exists
 * 
 * @author alexis
 */
public class ViewScanningParametersAction extends ViewResourceAction
{
    
    /** Creates a new instance of EditPlayLists */
    public ViewScanningParametersAction()
    {   super(MusikKernelResources.ID_SCANNING); }
    
}
