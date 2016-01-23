/* 
 * Siberia editor : siberia plugin defining editor framework
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
package org.siberia.editor;


import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

import com.jgoodies.forms.layout.*;
import java.awt.Component;
import java.awt.LayoutManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;


/**
 * 
 * abstract class to build a generic editor
 * it consists of a toolbar (optional), a general content in a scrollPane and a statusbar (optional)
 *
 * sequential use : 
 * <p>- creation of an abstractEditor.</p>
 * <p><i>whatever order : </i></p>
 * <p>   - configure the toolbar by using setToolbar method. <i>optional</i></p>
 * <p>   - configure the statusbar by using setStatusbar method. <i>optional</i></p>
 * <p>   - specify the main content by using configureContent method.</p>
 * <p>- call buildingCompleted method to finish building process.</p>
 * 
 * It must implements : 
 *      <p> - public void initializeEditor()</p>
 *      <p>             initialize the editor with the values of the associated instance.</p>
 *      <p> - public void modifyRelativeEntity()</p>
 *      <p>             update the content of the associated entity.</p>
 *
 * @author alexis
 */
public abstract class AdaptedEditor extends ValidationEditor implements org.siberia.editor.Editor
{
    public static int JGOODIES_LAYOUT = 0;
    public static int BOX_LAYOUT      = 1;
    
    /** logger */
    protected Logger                logger            = Logger.getLogger(this.getClass());
    
    /** type of the layout */
    private   int                   layoutType        = JGOODIES_LAYOUT;
    
    /** where to add the main aspect of the editor **/
    protected JScrollPane           content           = null;
    
    /** the layout of the editor **/
    private   FormLayout            formLayout        = null;
    
    /** specify if a toolbar has to be build **/
    private   boolean               hasToolbar        = false;
    
    /** specify if the main content has been build **/
    private   boolean               contentConfigured = false;
    
    /** specify if a statusbar has to be build **/
    private   boolean               hasStatusbar      = false;
    
    /** the toolbar **/
    private   JToolBar              toolbar           = null;
    
    /** define if a single editor of this type can be viewed */
    private   boolean               isSingle          = false;
    
//    /** the statusbar **/
//    private AbstractStatusbar statusbar;
    
    /** the row specification for the toolbar **/
    private static String   toolbarConstraint    = "pref";
    /** the column specification for the main content **/
    private static String   contentConstraint    = "fill:pref:grow";
    /** the column specification for the validation panel **/
    private static String   validationConstraint = "pref";
    /** the column specification for the statusbar **/
    private static String   statusbarConstraint  = "15dlu";
    
    
    /* TO DO : add status bar */
    
    /** default constructor which create the generic content, the toolbar
     *  and soon, it will build a statusbar too
     *  @param instance SibType instance associated with the editor
     **/
    public AdaptedEditor(SibType instance)
    {   this(instance, true, false); }
    
    /** Creates a new instance of AbstractEditor
     *  @param instance the SibType instance associated with the editor
     *  @param addToolbar specify if we want to add a toolbar
     *  @param addStatusbar specify if we want to add a statusbar
     **/
    public AdaptedEditor(SibType instance, boolean addToolbar, boolean addStatusbar)
    {   super(instance);
        
        this.formLayout = new FormLayout("pref:grow", "");
        
        /* if we have to add a toolBar, create another rowSpec **/
        if (addToolbar)
        {   RowSpec toolSpec = new RowSpec(AdaptedEditor.toolbarConstraint);
            this.formLayout.appendRow(toolSpec);
            this.hasToolbar = true;
        }
        
        /* add a new RowSpec for the fundamental content of the editor */
        this.formLayout.appendRow(new RowSpec(AdaptedEditor.contentConstraint));
        
        /** add a new RowSpec for the validation panel */
        if ( this.useValidation() )
        {   this.formLayout.appendRow(new RowSpec(AdaptedEditor.validationConstraint)); }
        
        if (addStatusbar)
        {   RowSpec statusSpec = new RowSpec(AdaptedEditor.statusbarConstraint);
            this.formLayout.appendRow(statusSpec);
            this.hasStatusbar = true;
        }
    }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {   return this.getPanel(); }
    
    /** define the type of layout to be used
     *  @param type one of static constants defined in AbstractEditor : <br>
     *                  - JGOODIES_LAYOUT.<br>
     *                  - BOX_LAYOUT.<br>
     */
    protected void setLayoutType(int type)
    {   if ( type == BOX_LAYOUT || type == JGOODIES_LAYOUT )
            this.layoutType = type;
        else
            throw new IllegalArgumentException("type is not allowed");
    }
    
    /** return the type of layout to be used
     *  @return
     *                  <ul><li>JGOODIES_LAYOUT or</li><li>BOX_LAYOUT</li></ul>
     */
    protected int getLayoutType()
    {   return this.layoutType; }
    
    /** all the element has been configured, so add graphical part to this
     *  At this step, every graphical component must have been initialized
     *  So call, setToolbar(...), setStatusbar(..) and configureContent(...) before ...
     **/
    public void buildingCompleted() throws EditorBuildingFailedException
    {   /* create jgoodies constraints */
        CellConstraints cc = null;
        
        LayoutManager layer = null;
        
        if ( this.getLayoutType() == AdaptedEditor.BOX_LAYOUT )
            layer = new BoxLayout(this.getPanel(), BoxLayout.PAGE_AXIS);
        else
        {   cc = new CellConstraints();
            layer = this.formLayout;
        }
            
        this.getPanel().setLayout(layer);
        
        if (this.hasToolbar)
        {   
            if (this.toolbar == null)
                throw new EditorBuildingFailedException("trying to build a toolbar's editor which is null");   
            if ( this.getLayoutType() == AdaptedEditor.JGOODIES_LAYOUT )
                this.getPanel().add(this.toolbar, cc.xy(1, 1, cc.LEFT, cc.TOP));
            else if ( this.getLayoutType() == AdaptedEditor.BOX_LAYOUT )
            {   this.toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
//                this.toolbar.setMaximumSize(this.toolbar.getPreferredSize());
                this.getPanel().add(this.toolbar);
            }
        }
        
        if (! this.contentConfigured)
            throw new EditorBuildingFailedException("trying to build a content's editor which is not initialized");
        
        if ( this.getLayoutType() == AdaptedEditor.JGOODIES_LAYOUT )
            this.getPanel().add(this.content, cc.xy(1, (this.hasToolbar ? 2 : 1), cc.FILL, cc.FILL));
        else if ( this.getLayoutType() == AdaptedEditor.BOX_LAYOUT )
            this.getPanel().add(this.content);
        
        if ( this.useValidation() )
        {   if ( this.getLayoutType() == AdaptedEditor.JGOODIES_LAYOUT )
                this.getPanel().add(this.getValidationPanel(), cc.xy(1, (this.hasToolbar ? 3 : 2), cc.FILL, cc.FILL));
            else if ( this.getLayoutType() == AdaptedEditor.BOX_LAYOUT )
                this.getPanel().add(this.getValidationPanel());
        }
        
//        if (this.hasStatusbar)
//        {   if (this.statusbar == null)
//                throw new EditorBuildingFailedException("trying to build a statusbar which is null")
//            this.add(this.statusbar, cc.xy(1, 3));
//        }
    }
    
    /** set the main content of the editor
     *  @param compo the main graphical object in the editor
     **/
    public void configureContent(JComponent compo)
    {   this.content = new JScrollPane(compo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        this.content.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.contentConfigured = true;
    }
    
    /** return the toolbar of the editor
     *  @return toolbar instance of AbstractToolbar
     **/
    public JToolBar getToolbar()
    {   return this.toolbar; }
    
    /** specify the toolbar to be added to the editor
     *  @param toolbar the toolbar to be added in the editor area
     **/
    public void setToolbar(JToolBar toolbar)
    {   this.toolbar = toolbar;
        /* if this.toolbar is not null, so the column has already been added */
        if (this.hasToolbar) return;
        
        /* else we have to create a new RowSpec */
        RowSpec toolSpec = new RowSpec(AdaptedEditor.toolbarConstraint);
        this.formLayout.insertRow(1, toolSpec);
        this.hasToolbar = true;
    }

    
    /** specify the statusbar to be added to the editor
     *  @param statusbar the statusbar to be added in the editor area
     **/
//    public void setStatusbar(AbstractStatusBar statusbar)
//    {   this.statusbar = statusbar;
//        /* if this.toolbar is not null, so the column has already been added */
//        if (this.hasStatusbar) return;
//        
//        /* else we have to create a new RowSpec */
//        RowSpec statusSpec = new RowSpec(AbstractEditor.statusbarConstraint);
//        int position = 2;
//        if ( this.hasToolbar) position = 3;
//        this.layout.insertRow(position, statusSpec);
//        this.hasStatusbar = true;
//    }
    
    /** Exception class which is throwed when an error is detecting in the buildingCompleted method **/
    public class EditorBuildingFailedException extends Exception
    {   public EditorBuildingFailedException(String message)
        {   super(message); }        
    }
}
