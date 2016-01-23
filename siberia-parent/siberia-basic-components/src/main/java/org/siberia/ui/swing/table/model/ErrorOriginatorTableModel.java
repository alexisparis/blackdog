/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.ui.swing.error.ErrorHandler;
import org.siberia.ui.swing.error.ErrorOriginator;

/**
 *
 * TableModel that can warn for errors
 *
 * @author alexis
 */
public abstract class ErrorOriginatorTableModel extends AbstractSiberiaTableModel implements ErrorOriginator
{
    
    /** list of ErrorHandler */
    private List<ErrorHandler> errorHandlers = null;
    
    /** Creates a new instance of ErrorOriginatorTableModel */
    public ErrorOriginatorTableModel()
    {	}
    
    /* #########################################################################
     * #################### ErrorOriginator implementation #####################
     * ######################################################################### */
    
    /** warn ErrorHandler */
    protected void fireErrorHandlers(ErrorEvent e)
    {   
	if ( this.errorHandlers != null )
        {   for(int i = 0; i < this.errorHandlers.size(); i++)
            {   ErrorHandler handler = this.errorHandlers.get(i);
                
                if ( handler != null )
                {   handler.handleError(e); }
            }
        }
    }

    /**
     * remove a new ErrorHandler
     * 
     * @param handler an ErrorHandler
     */
    public void removeErrorHandler(ErrorHandler handler)
    {   if ( handler != null && this.errorHandlers != null )
        {   this.errorHandlers.remove(handler); }
    }

    /**
     * add a new ErrorHandler
     * 
     * @param handler an ErrorHandler
     */
    public void addErrorHandler(ErrorHandler handler)
    {   if ( handler != null )
        {   if ( this.errorHandlers == null )
                this.errorHandlers = new ArrayList<ErrorHandler>();
            this.errorHandlers.add(handler);
        }
    }
    
}
