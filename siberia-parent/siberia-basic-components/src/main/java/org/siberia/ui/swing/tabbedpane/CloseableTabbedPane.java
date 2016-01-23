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
package org.siberia.ui.swing.tabbedpane;

import java.util.EventListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.AWTEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import org.siberia.ui.swing.tabbedpane.event.DoubleClickListener;
import org.siberia.ui.swing.tabbedpane.event.CloseListener;
import org.siberia.ui.swing.tabbedpane.event.ShowPopupListener;
import org.siberia.ui.swing.tabbedpane.event.PageChangeListener;
import org.siberia.ui.swing.tabbedpane.ui.CloseableTabbedPaneUI;

/**
 * A abstract extended JTabbedPane with internal closeable page hability
 * It contains all needed to send events to entities of type : 
 *          <p> - CloseListener.</p>
 *          <p> - DoubleClickListener.</p>
 *          <p> - PageChangeListener.</p>
 *
 * Every subclass has to implements : 
 *          <p> - public void closeOperation(MouseEvent e, int index)</p>
 *          <p> - public void doubleClickOperation(MouseEvent e, int index)</p>
 *          <p> - public void pageChange(AWTEvent e, int oldSelection, int newSelection)</p>
 *
 * In the future, this will alow page extending...
 * 
 * @author alexis
 */

public abstract class CloseableTabbedPane extends    JTabbedPane
                                          implements CloseListener,
                                                     DoubleClickListener,
                                                     PageChangeListener
{
    /** index for the 'selected' page **/
    private int overTabIndex = -1;

    /** instance for the graphical representation **/
    private CloseableTabbedPaneUI tabbedPaneUI;

    /** list of close page listeners **/
    private List                  closePageListeners;

    /** list of double click on page' label listeners **/
    private List                  doubleClickListeners;

    /** list of page change listeners **/
    private List                  pageChangeListeners;

    /**
     * Creates the <code>CloseableTabbedPane</code>
     */
    public CloseableTabbedPane()
    {   super();
        super.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        super.setTabPlacement(JTabbedPane.TOP);

        /* define the used UI */
        super.setUI(new CloseableTabbedPaneUI());

        /* define the list of listeners */
        this.closePageListeners   = new ArrayList();
        this.doubleClickListeners = new ArrayList();
        this.pageChangeListeners  = new ArrayList();

        /* define the listener for close and double click event **/
        this.addCloseListener(this);
        this.addDoubleClickListener(this);
        this.addPageChangeListener(this);
        
        /* alt RIGHT and alt LEFT catching event */
        Action previousAction = new AbstractAction()
        {   public void actionPerformed(ActionEvent a)
            {   int selectedIndex = CloseableTabbedPane.this.getSelectedIndex();
                
                if ( selectedIndex != 0 )
                    CloseableTabbedPane.this.setSelectedIndex(selectedIndex - 1);
                else
                    CloseableTabbedPane.this.setSelectedIndex(CloseableTabbedPane.this.getTabCount() - 1);
            }
        };
        Action nextAction = new AbstractAction()
        {   public void actionPerformed(ActionEvent a)
            {   int selectedIndex = CloseableTabbedPane.this.getSelectedIndex();
                
                if ( selectedIndex != CloseableTabbedPane.this.getTabCount() - 1 )
                    CloseableTabbedPane.this.setSelectedIndex(selectedIndex + 1);
                else
                    CloseableTabbedPane.this.setSelectedIndex(0);
            }
        };
        
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_MASK), "previousEditor");
        this.getActionMap().put("previousEditor", previousAction);
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_MASK), "nextEditor");
        this.getActionMap().put("nextEditor", nextAction);
    }

    /**
     * Returns the index of the last tab on which the mouse did an action.
     */
    public int getOverTabIndex()
    {   return overTabIndex; }

    /**
     * Returns <code>true</code> if the close icon is enabled.
     */
    public boolean isCloseEnabled()
    {   return tabbedPaneUI.isCloseEnabled(); }

    /**
     * Sets whether the tabbedPane should have a close icon or not.
     * 
     * @param b
     *            whether the tabbedPane should have a close icon or not
     */
    public void setCloseIcon(boolean b)
    {   tabbedPaneUI.setCloseIcon(b); }

    /** add a new page close listener
     *  @param l a new instance implementing CloseListener
     **/
    public void addCloseListener(CloseListener l)
    {   this.closePageListeners.add(l); }

    /** remove a page close listener
     *  @param l an instance implementing CloseListener
     **/
    public void removeCloseListener(CloseListener l)
    {   this.closePageListeners.remove(l); }

    /** add a new page double click listener
     *  @param l a new instance implementing DoubleClickListener
     **/
    public void addDoubleClickListener(DoubleClickListener l)
    {   this.doubleClickListeners.add(l); }

    /** remove a double click listener
     *  @param l an instance implementing DoubleClickListener
     **/
    public void removeDoubleClickListener(DoubleClickListener l)
    {   this.doubleClickListeners.remove(l); }

    /** add a new page double click listener
     *  @param l a new instance implementing PageChangeListener
     **/
    public void addPageChangeListener(PageChangeListener l)
    {   this.pageChangeListeners.add(l); }

    /** remove a double click listener
     *  @param l an instance implementing PageChangeListener
     **/
    public void removePageChangeListener(PageChangeListener l)
    {   this.pageChangeListeners.remove(l); }

    /** a new selectino page event occurs, process it **/
    public void firePageChanged(AWTEvent e, int oldSelectedPage, int newSelectionPage)
    {   if (e.getSource() != ((CloseableTabbedPaneUI)this.getUI()).getScrollableTabPane()) return;

        /* else tells listeners that the selected page changed */
        Iterator it = this.pageChangeListeners.iterator();
        while(it.hasNext())
        {   ((PageChangeListener)it.next()).pageChange(e, oldSelectedPage, newSelectionPage); }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>CloseListener</code>. The method also updates the
     * <code>overTabIndex</code> of the tabbedPane with a value coming from
     * the UI. This method method is called each time a <code>MouseEvent</code>
     * is received from the UI when the user clicks on the close icon of the tab
     * which index is <code>overTabIndex</code>.
     * 
     * @param e
     *            the <code>MouseEvent</code> to be sent
     * @param overTabIndex
     *            the index of a tab, usually the tab over which the mouse is
     * 
     */
    public void fireCloseTabEvent(MouseEvent e, int overTabIndex)
    {   /* update context */
        this.overTabIndex = overTabIndex;

        /* and tell others the new */
        Iterator it = this.closePageListeners.iterator();
        while(it.hasNext())
        {   ((CloseListener)it.next()).closeOperation(e, overTabIndex); }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>DoubleClickListener</code>. The method also updates the
     * <code>overTabIndex</code> of the tabbedPane with a value coming from
     * the UI. This method method is called each time a <code>MouseEvent</code>
     * is received from the UI when the user double-clicks on the tab which
     * index is <code>overTabIndex</code>.
     * 
     * @param e
     *            the <code>MouseEvent</code> to be sent
     * @param overTabIndex
     *            the index of a tab, usually the tab over which the mouse is
     * 
     */
    public void fireDoubleClickTabEvent(MouseEvent e, int overTabIndex)
    {   /* update context */
        this.overTabIndex = overTabIndex;

        /* tell others that a double click has been detected */
        Iterator it = this.doubleClickListeners.iterator();
        while(it.hasNext())
        {   ((DoubleClickListener)it.next()).doubleClickOperation(e, overTabIndex); }
    }

    /**
     * Sends a <code>MouseEvent</code>, whose source is this tabbedpane, to
     * every <code>PopupOutsideListener</code>. The method also sets the
     * <code>overTabIndex</code> to -1. This method method is called each time
     * a <code>MouseEvent</code> is received from the UI when the user
     * right-clicks on the inactive part of a tabbedPane.
     * 
     * @param e
     *            the <code>MouseEvent</code> to be sent
     * 
     */
    public void firePopupOutsideTabEvent(MouseEvent e)
    {   this.overTabIndex = -1;

        EventListener popupListeners[] = getListeners(ShowPopupListener.class);
        for (int i = 0; i < popupListeners.length; i++)
        {   ((ShowPopupListener) popupListeners[i]).popupOutsideOperation(e); }
    }

    /** process a close page event **/
    public abstract void closeOperation(MouseEvent e, int index);

    /** process a double click on a label of a page **/
    public abstract void doubleClickOperation(MouseEvent e, int index);

    /** process a page change event
     *  we must use an AWTEvent because, both ActionEvent and MouseEvent
     *  provoke page changed event
     **/
    public abstract void pageChange(AWTEvent e, int oldPosition, int newPosition);

}

