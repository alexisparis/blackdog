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
package org.siberia.ui.swing.split;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * Split pane that allow to grow on of its composite component
 *
 * @author alexis
 */
public class SplitPane extends JSplitPane
{
    /** tell if this splitPane support growing */
    private boolean   supportGrowing   = true;
    
    /** action to be done if growing is wanted */
    private Action    growingAction    = null;
    
    /** key associated with growing order */
    private KeyStroke growingKeyStroke = null;
    
    /** divider location before growing action */
    private int       dividerLocation  = 0;
    
    /** Creates a new instance of ColdSplitPane */
    public SplitPane()
    {   this(true); }
    
    /** Creates a new instance of ColdSplitPane
     *  @param supportGrowing true if this splitPane support growing
     */
    public SplitPane(boolean supportGrowing)
    {   this(supportGrowing, JSplitPane.HORIZONTAL_SPLIT); }
    
    /** Creates a new instance of ColdSplitPane
     *  @param supportGrowing true if this splitPane support growing
     *
     *  @param newOrientation  <code>JSplitPane.HORIZONTAL_SPLIT</code> or
     *                        <code>JSplitPane.VERTICAL_SPLIT</code>
     *  @exception IllegalArgumentException if <code>orientation</code>
     *		is not one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
     */
    public SplitPane(boolean supportGrowing, int newOrientation)
    {   super(newOrientation);
        this.setSupportGrowing(supportGrowing);
        this.growingKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.SHIFT_MASK);
        
        this.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    /** tell if this splitPane support growing
     *  @return true if this splitPane support growing
     */
    public boolean isSupportGrowing()
    {   return supportGrowing; }

    /** indicate if this splitPane support growing
     *  @param supportGrowing true if this splitPane support growing
     */
    public void setSupportGrowing(boolean supportGrowing)
    {   //if ( this.supportGrowing != supportGrowing )
        {   this.supportGrowing = supportGrowing;
            
            System.out.println("keyStroke : " + this.growingKeyStroke);
            
            /* add or remove growing listener */
            if ( this.supportGrowing )
            {   if ( this.growingAction == null )
                    this.growingAction = new GrowingAction();
                if ( this.growingKeyStroke == null )
                    this.growingKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, InputEvent.SHIFT_MASK);
                //WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(this.growingKeyStroke, "Grow");
                this.getActionMap().put("Grow", new GrowingAction());//this.growingAction);
            }
            else
            {   this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(this.growingKeyStroke);
                this.getActionMap().remove("Grow");
            }
        }
    }
    
    /** return the first component of the splitPane, that means, the top component if orientation is JSplitPane.VERTICAL_SPLIT<br>
     *  or the right component if orientation is JSplitPane.HORIZONTAL_SPLIT
     *  @return a component
     */
    protected Component getFirstComponent()
    {   if ( this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
            return this.getLeftComponent();
        else if ( this.getOrientation() == JSplitPane.VERTICAL_SPLIT )
            return this.getTopComponent();
        return null;
    }
    
    /** return the second component of the splitPane, that means, the bottom component if orientation is JSplitPane.VERTICAL_SPLIT<br>
     *  or the left component if orientation is JSplitPane.HORIZONTAL_SPLIT
     *  @return a component
     */
    protected Component getSecondComponent()
    {   if ( this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
            return this.getRightComponent();
        else if ( this.getOrientation() == JSplitPane.VERTICAL_SPLIT )
            return this.getBottomComponent();
        return null;
    }
    
    /** return the caracterizing size of a given component that is the first or second component
     *  @param component the first or second component
     *  @return an integer representing its current width or height according to splitPane orientation or -1 <br>
     *          if the given component is null
     */
    private int getSizeFor(Component component)
    {   if ( component != null )
        {   if ( this.getOrientation() == JSplitPane.HORIZONTAL_SPLIT )
                return component.getSize().width;
            else if ( this.getOrientation() == JSplitPane.VERTICAL_SPLIT )
                return component.getSize().height;
        }
        return -1;
    }
    
    /** process growing event */
    protected void processGrow()
    {   Component focusedComponent = FocusManager.getCurrentManager().getFocusOwner();
        
        Component c1 = SplitPane.this.getFirstComponent();
        Component c2 = SplitPane.this.getSecondComponent();
        
                
//        if ( this.getParent() instanceof ColdSplitPane )
//        {   ((ColdSplitPane)this.getParent()).processGrow(); }
        
        System.out.println("processGrow on " + this.getName());
        
        if ( c1 != null && c2 != null )
        {   int wantedDividerLocation = -1;
            if ( SwingUtilities.isDescendingFrom(focusedComponent, c1) )
            {   if ( this.getSizeFor(c2) > 0 )
                {   wantedDividerLocation = 5000; }
                else
                {   wantedDividerLocation = this.dividerLocation; }
            }
            else if ( SwingUtilities.isDescendingFrom(focusedComponent, c2) )
            {   if ( this.getSizeFor(c1) > 0 )
                {   wantedDividerLocation = 0; }
                else
                {   wantedDividerLocation = this.dividerLocation; }
            }
            
            if ( wantedDividerLocation != -1 )
            {   this.dividerLocation = this.getDividerLocation();
                System.out.println("\twanted divider location : " + wantedDividerLocation);
                this.setDividerLocation(wantedDividerLocation);
                System.out.println("\t--> divider location : " + this.getDividerLocation());
            }
            if ( this.getParent() instanceof SplitPane )
            {   ((SplitPane)this.getParent()).processGrow(); }
        }
    }
    
    public void setRightComponent(Component comp)
    {
        new Exception("calling setRightComponent").printStackTrace();
        super.setRightComponent(comp);
    }
        
    /** class defining the actionto be done if the user wants some part of the split pane to grow up */
    protected class GrowingAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {   SplitPane.this.processGrow(); }
    }
}
