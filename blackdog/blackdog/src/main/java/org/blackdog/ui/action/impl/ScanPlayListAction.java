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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.awl.WizardConstants;
import org.blackdog.action.impl.ScanDialog;
import org.blackdog.scan.AudioScanner;
import org.blackdog.type.PlayList;
import org.blackdog.type.Scannable;
import org.blackdog.type.ScannablePlayList;
import org.siberia.type.SibType;
import org.siberia.type.SibCollection;
import org.siberia.ui.UserInterface;
import org.siberia.ui.action.AbstractSingleTypeAction;
import org.siberia.ui.action.impl.wizard.CreationWizard;

/**
 *
 * Action that allow to launch scan on a PlayList that implements Scannable
 *
 * @author alexis
 */
public class ScanPlayListAction<E extends SibType> extends AbstractSingleTypeAction<E>
{
    /* logger */
    private static Logger logger = Logger.getLogger(ScanPlayListAction.class);
    
    /** Creates a new instance of ScanPlayListAction */
    public ScanPlayListAction()
    {   super(); }

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   Object item = this.getType();
        if ( item instanceof ScannablePlayList )
        {   
            ScannablePlayList scannable = (ScannablePlayList)item;
            
            final ScanDialog dialog = new ScanDialog(UserInterface.getInstance().getFrame(), false);

            final AudioScanner scanner = new AudioScanner(dialog,
                                                          scannable);

            scanner.execute();

            /* show dialog box */
            dialog.setVisible(true);
            
        }
    }
    
    /** set the types related to this action
     *  @return an array of SibType
     */
    @Override
    public void setTypes(E[] types)
    {   super.setTypes(types);
        
        boolean enabled = false;
        
        if ( this.getType() instanceof PlayList )
        {   
            if ( this.getType() instanceof Scannable )
            {
                if ( ((Scannable)this.getType()).isScannable() )
                {
                    enabled = true;
                }
                else
                {   logger.debug(this.getClass().getName() + " not enabled because this scannable playlist does not actually accept scan"); }
            }
            else
            {   logger.debug(this.getClass().getName() + " not enabled because this playlist does not implements " + Scannable.class); }
        }
        else
        {   logger.debug(this.getClass().getName() + " not enabled because related item is not a PlayList (" + this.getType() + ")"); }
        
        this.setEnabled(enabled);
    }
    
}
