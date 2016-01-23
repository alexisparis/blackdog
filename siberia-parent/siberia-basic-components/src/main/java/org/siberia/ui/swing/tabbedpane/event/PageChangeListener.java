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
package org.siberia.ui.swing.tabbedpane.event;

import java.awt.AWTEvent;
import java.util.EventListener;

/** interface for all class which want to be warned when the selected page changed in a JTabbedPane
 *
 *  @author alexis
 **/
public interface PageChangeListener extends EventListener
{
    /**
     *  process page change event on a tab pane
     *  @param e an AWTEvent, most of the time, it will be an ActionEvent or a MouseEvent
     *  @param oldIndex the index of the selected page before on the tab pane
     *  @param newIndex the index of the new selected page on the tab pane
     **/
    public void pageChange(AWTEvent e, int oldIndex, int newIndex);
    
}
