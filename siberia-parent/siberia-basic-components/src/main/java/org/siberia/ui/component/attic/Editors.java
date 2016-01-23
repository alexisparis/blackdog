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
package org.siberia.ui.component.attic;
//package org.siberia.ui.component.attic;
//
//import java.awt.AWTEvent;
//import java.awt.Component;
//import java.awt.event.MouseEvent;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import javax.swing.Icon;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import org.siberia.kernel.eventsystem.event.CurrentEntityChangedEvent;
//import org.siberia.kernel.eventsystem.event.EntityModifiedEvent;
//import org.siberia.kernel.eventsystem.event.EntityMovedEvent;
//import org.siberia.kernel.eventsystem.event.ReinitProjectEvent;
//import org.siberia.kernel.eventsystem.event.StructureModifiedEvent;
//import org.siberia.ui.eventsystem.event.CurrentEditorChangedEvent;
//import org.siberia.ui.eventsystem.event.ExtraEditingEvent;
//import org.siberia.ui.eventsystem.listener.CurrentEditorChangedListener;
//import org.siberia.ui.eventsystem.listener.ExtraEditingListener;
//import org.siberia.ui.swing.tabbedpane.event.DoubleClickListener;
//import org.siberia.type.ColdText;
//import org.siberia.kernel.eventsystem.listener.CurrentEntityChangedListener;
//import org.siberia.kernel.eventsystem.listener.EntityModificationListener;
//import org.siberia.kernel.eventsystem.listener.EntityMovedListener;
//import org.siberia.kernel.eventsystem.listener.ReinitProjectListener;
//import org.siberia.kernel.eventsystem.listener.StructureModificationListener;
//import org.siberia.ui.editor.Editor;
//import org.siberia.eventsystem.item.XMLEventSystemItem;
//import org.siberia.ui.editor.impl.text.ColdTextEditor;
//import org.siberia.type.AbstractColdType;
//import org.siberia.type.ColdType;
//import org.siberia.ui.swing.tabbedpane.GrowingCloseableTabbedPane;
//import org.siberia.util.ResourceLoader;
//
//
///**
// *
// * Extended CloseableTabbedPane which able to display Editor
// * It manage calls to : 
// *              <code>initializeEditor()</code>
// *              <code>updateRelativeEntity()</code>
// * to keep editor and their relative entity up to date
// *
// * @author alexis
// */
//public class Editors extends GrowingCloseableTabbedPane implements StructureModificationListener,
//                                                                   EntityModificationListener,
//                                                                   EntityMovedListener,
//                                                                   CurrentEntityChangedListener, // PENDING
////                                                               CurrentEditorChangedSource,
//                                                                   ReinitProjectListener,
//                                                                   CurrentEditorChangedListener,
//                                                                   DoubleClickListener,
//                                                                   ExtraEditingListener,
//        
//                                                                   ChangeListener,
//                                                                   XMLEventSystemItem
//{   
//    /** list of CurrentEditorChangedListener **/
//    private List<CurrentEditorChangedListener>    currentEditorChangedListeners    = new ArrayList<CurrentEditorChangedListener>();
//    
//    /** mapping between abstract entities and their corresponding editor
//     *  which are currently displayed in the tabbed pane
//     **/
//    private Map entitiesMapping                      = new HashMap();
//    
//    /** Creates a new instance of TabbedPane */
//    public Editors()
//    {   super();
//        
//        this.setFocusTraversalKeysEnabled(true);
//    }
//    
//    /** return the path of the file that describe the configuration
//     *  @return the path of the file that describe the configuration
//     */
//    public String getEventConfigurationFilePath()
//    {   return File.separator + "org" + File.separator + "siberia" + File.separator + "rc" + File.separator +
//               "event" + File.separator + "editors.xml";
//    }
//    
//    /** initialize a new corresponding editor
//     *  for the instance.
//     *  editor and instance are placed in the entitiesMapping.
//     * @param editor instance of Editor
//     **/
//    public void add(Editor editor)
//    {   
//        /* add it to the diagram structure changed listeners if it is required */
////        if ( editor instanceof DiagramStructureChangedListener )
////            this.addDiagramStructureChangedListener( (DiagramStructureChangedListener)editor );
////        if ( editor instanceof DiagramSelectionChangedListener )
////        {   this.addDiagramSelectionChangedListener( (DiagramSelectionChangedListener)editor );
////            if ( editor instanceof DiagramSelectionChangedSource )
////                ((DiagramSelectionChangedSource)editor).addDiagramSelectionChangedListener(this);
////        }
////        if ( editor instanceof DiagramEntityChangedListener )
////        {   this.addDiagramEntityChangedListener( (DiagramEntityChangedListener)editor );
////            if ( editor instanceof DiagramEntityChangedSource )
////                ((DiagramEntityChangedSource)editor).addDiagramEntityChangedListener(this);
////        }
//        
//        /* add the editor to the tabbed pane **/
//        if (editor != null)
//        {   
//            /* tell editor to initialize itself */
//            editor.initializeEditor();
//            try
//            {   Icon icon = ResourceLoader.getInstance().getIconForType(editor.getInstance().getClass());
//                this.addTab(editor.getInstance().getName(), icon, editor.getComponent());
//            }
//            catch(Exception e)
//            {   this.add(editor.getInstance().getName(), editor.getComponent()); }
//            
//            /* select the new tab --> it's placed in the last tab */
//            this.setSelectedIndex(this.getTabCount() - 1);
//            
//            /* update the mapping */
//            this.entitiesMapping.put(editor.getInstance(), editor);
//        }
//    }
//    
//    /** initialize a new corresponding editor
//     *  for the instance.
//     *  editor and instance are placed in the entitiesMapping.
//     * @param instance ColdType instance related to the editor we want to add
//     **/
//    public void add(ColdType instance)
//    {   /* create an associated editor */
//        Editor editor = null;
//        if (instance instanceof ColdText)
//            editor = new ColdTextEditor((ColdText)instance);
////        else if (instance instanceof ColdDiagram)
////        {   editor = new DiagramEditor((ColdDiagram)instance); }
////        else if (instance instanceof ColdNodeAppearance)
////        {   editor = new NodeAppearanceEditor((ColdNodeAppearance)instance); }
////        else if (instance instanceof ColdEdgeAppearance)
////        {   editor = new EdgeAppearanceEditor((ColdEdgeAppearance)instance); }
////        else if (instance instanceof ColdNamespace)
////        {   editor = new ColdNamespaceEditor((ColdNamespace)instance); }
//        
//        this.add(editor);
//    }
//    
//    /** try to display the corresponding editor of instance and
//     *  if not find, a new editor is created
//     * @param instance ColdType instance related to the editor we want to display
//     **/
//    public void display(ColdType instance)
//    {   
//        /* search for the instance in the mapping */
//        Editor editor = (Editor)this.entitiesMapping.get(instance);
//        
//        /* else show the right editor */
//        this.display(editor);
//        
//        /* if the editor does not exist for the moment, we have to create it and to display it on the tab pane */
//        if (editor == null) this.add(instance);
//    }
//    
//    /** try to display the corresponding editor
//     *  @param editor an existing editor to display in the tabbedPane
//     */
//    public void display(Editor editor)
//    {   
//        if (editor != null)
//        {   this.setSelectedComponent(editor.getComponent());
//        
//            /* tell editor to initialize itself */
//            editor.initializeEditor();
//            
//            return;
//        }
//    }
//    
//    /** remove the editor associated with the instance
//     * @param instance ColdType instance related to the editor we want to remove
//     **/
//    public void remove(ColdType instance)
//    {   /* search for the instance in the mapping */
//        Component editor = (Component)this.entitiesMapping.get(instance);
//        
//        /** no corresponding editor was fund */
//        if ( editor == null ) return;
//        
//        this.entitiesMapping.remove(instance);
//        
//        /* tell the corresponding editor to save changes */
//        ((Editor)editor).save();
//        
//        if (editor != null)
//        {   /* remove it to the diagram structure changed listeners if it is required */
////            if ( editor instanceof DiagramStructureChangedListener )
////                this.removeDiagramStructureChangedListener( (DiagramStructureChangedListener)editor );
////            if ( editor instanceof DiagramSelectionChangedListener )
////                this.removeDiagramSelectionChangedListener( (DiagramSelectionChangedListener)editor );
////            if ( editor instanceof DiagramSelectionChangedSource )
////                ((DiagramSelectionChangedSource)editor).removeDiagramSelectionChangedListener(this);
////            if ( editor instanceof DiagramEntityChangedListener )
////                this.removeDiagramEntityChangedListener( (DiagramEntityChangedListener)editor );
////            if ( editor instanceof DiagramEntityChangedSource )
////                ((DiagramEntityChangedSource)editor).removeDiagramEntityChangedListener(this);
//            
//            this.remove(editor);
//        }
//    }
//    
//    /** remove the editor placed in position index
//     *  @param index index of the Component in the tab to remove
//     **/
//    public void remove(int index)
//    {   Component wantedEditor = this.getComponentAt(index);
//        
//        if ( this.entitiesMapping.containsKey(this.getInstanceOf(wantedEditor)) )
//            this.entitiesMapping.remove(this.getInstanceOf(wantedEditor));
//            
//        this.removeTabAt(index);
//        
//        if ( wantedEditor != null )
//        {   
//            if ( wantedEditor instanceof Editor )
//            {
//                /* remove it to the diagram structure changed listeners if it is required */
////                if ( wantedEditor instanceof DiagramStructureChangedListener )
////                    this.removeDiagramStructureChangedListener( (DiagramStructureChangedListener)wantedEditor );
////                if ( wantedEditor instanceof DiagramSelectionChangedListener )
////                    this.removeDiagramSelectionChangedListener( (DiagramSelectionChangedListener)wantedEditor );
//            }
//        }
//        
//        /* tell the corresponding editor to save changes */
//        ((Editor)wantedEditor).save();
//    }
//    
//    /** return the instance associated with the editor or null if not fund
//     *  @param editor Component the editor associated with the instance we want to get
//     **/
//    public AbstractColdType getInstanceOf(Component editor)
//    {   Iterator objects = this.entitiesMapping.keySet().iterator();
//        while(objects.hasNext())
//        {   Object current = objects.next();
//            if (this.entitiesMapping.get(current) == editor) return (AbstractColdType)current;
//        }
//        return null;
//    }
//    
//    /** remove all pages of the tabbedpane **/
//    public void removeAll()
//    {   super.removeAll();
//        
//        /* reinit the mapping */
//        this.entitiesMapping.clear();
//    }
// 
//    /** process a close page event **/
//    public void closeOperation(MouseEvent e, int index)
//    {   this.remove(index); }
//    
//    /** process a page selection event **/
//    public void pageChange(AWTEvent e, int oldSelection, int newSelection)
//    {   ((Editor)this.getComponentAt(oldSelection)).save();
//        
//        /* TO MODIFY : the UI already care about page selection changed by changing the index
//         * calling display will redo this action but allow to care about editor reload at
//         * one place : in the display method
//         */
//        this.display( ((Editor)this.getComponentAt(newSelection)).getInstance() );
//        
//        /* tell editor changed listener that a new editor is currently being used */
//        // PENDING
////        CurrentEditorChangedEvent evt = new CurrentEditorChangedEvent((Editor)this.getComponentAt(newSelection),
////                                                                      this);
////        this.fireCurrentEditorChanged(evt);
//    }
//    
//    /* ######################################################################
//     * #################### ExtraEditingListener impl #######################
//     * ###################################################################### */
//    
//    public void processEditing(ExtraEditingEvent e)
//    {   System.out.println("appel a processEditing");
//        Editor editor = null;
//        
//        if ( e.concernsATypeOfEditor() )
//        {   /* remove all the extra editors of this kind */
//            
//            for(int i = 0; i < this.getTabCount(); i++)
//            {   if ( e.getClassEditor().isAssignableFrom(this.getComponentAt(i).getClass()) )
//                    this.remove(i);
//            }
//        }
//        else
//        {   if ( e.removeMode() )
//            {   this.remove(e.getEditor().getInstance()); }
//            else
//            {   
//                this.add(e.getEditor().getComponent());
//            }
//        }
//    }
//    
//    /* ######################################################################
//     * ######################## ChangeListener impl #########################
//     * ###################################################################### */
//    
//    public void stateChanged(ChangeEvent e)
//    {   /* get the new editor which has to be displayed */
//        ((Editor)this.getSelectedComponent()).initializeEditor();
//        this.display( ((Editor)this.getSelectedComponent()).getInstance() );
//    }
//    
//    /* ######################################################################
//     * #################### ReinitProjectListener impl ######################
//     * ###################################################################### */
//    
//    public void processReinitProject(ReinitProjectEvent e)
//    {   /* close all tabs */
//        this.removeAll();
//    }
//    
//    /* ######################################################################
//     * ################# StructureModificationListener impl #################
//     * ###################################################################### */
//    
//    public void processStructureModification(StructureModifiedEvent e)
//    {   /** if the modification affect a metaModel or an ASGEntityLink, prevent editors */
////        DiagramStructureChangedEvent dse = null;
//        
////        if ( e.getEntity() instanceof MetaModel || e.getEntity() instanceof ColdASG )
////        {   /* on all editors */
////            /** A revoir pour les ASG */
////            
////            if ( this.entitiesMapping != null )
////            {   Object current = null;
////                for(Iterator it = this.entitiesMapping.values().iterator(); it.hasNext();)
////                {   current = it.next();
////                    
//////                    if ( current != null && current instanceof DiagramEditor )
//////                    {   ((DiagramEditor)current).refresh(); }
////                }
////            }
////        }
////        else if ( e.getEntity() instanceof ColdEntityLink )
////        {   /* get the parent diagram */
////            ColdDiagram diagram = (ColdDiagram)((ColdEntityLink)e.getEntity()).getParent().getParent();
////            
////            // PENDING
//////            dse = new DiagramStructureChangedEvent( (ColdEntityLink)e.getEntity(), diagram, this, e.getModificationMode() );
//////            this.fireDiagramStructureChanged(dse);
////        }
////        else if ( e.getEntity() instanceof ColdDiagram )
////        {   /* search for the diagram if it appears and remove it */
////            this.remove(e.getEntity());
////        }
////        else if ( e.getEntity() instanceof ColdEntity )
////        {   
////            /* search for editors related to e.getNewEntity()' children and remove them */            
////            if ( ! e.removeMode() ) return;
////            
////            /* test for all instances that are being edited if they are related to this ASGEntity */
////            AbstractColdType current = null;
////            for(Iterator<AbstractColdType> it = this.entitiesMapping.keySet().iterator(); it.hasNext(); )
////            {   current = it.next();
////                
////                if ( current.hasForAncestor(e.getEntity()) )
////                    this.remove(current);
////            }
////        }
//    }
//    
//    /* ######################################################################
//     * ##################### EntityMovedListener impl #######################
//     * ###################################################################### */
//        
//    public void processEntityMove(EntityMovedEvent e)
//    {   /* do nothing for the moment */ }
//    
//    /* ######################################################################
//     * ################## EntityModificationListener impl ###################
//     * ###################################################################### */
//    
//    public void processEntityModification(EntityModifiedEvent e)
//    {   // PENDING : ca va rien casser?
////        if ( this.entitiesMapping.containsKey(e.getEntity()) )
////        {   int index = this.indexOfComponent( (Component)this.entitiesMapping.get(e.getEntity()) );
////            this.setTitleAt( index, e.getEntity().getName() );
////        }
////        
////        
////        
////        if ( e.getEntity() instanceof ColdEntityLink || e.getEntity().hasForAncestor(ColdEntityLink.class) )
////        {   ColdEntityLink link = null;
////            
////            if ( e.getEntity() instanceof ColdEntityLink )
////                link = (ColdEntityLink)e.getEntity();
////            else
////                link = (ColdEntityLink)e.getEntity().getFirstAncestorOfType(ColdEntityLink.class);
////            
////            
////            DiagramEntityChangedEvent event = new DiagramEntityChangedEvent( link, null, this );
////            this.fireDiagramEntityChanged(event);
////        }
////        if ( e.getEntity() instanceof ColdEntity || e.getEntity().hasForAncestor(ColdEntity.class) )
////        {   ColdEntity entity = null;
////            
////            if ( e.getEntity() instanceof ColdEntity )
////                entity = (ColdEntity)e.getEntity();
////            else
////                entity = (ColdEntity)e.getEntity().getFirstAncestorOfType(ColdEntity.class);
////            
////            Object current = null;
////            for (Iterator it = entity.getLinks(); it.hasNext(); )
////            {   current = it.next();
////                
////                if ( current instanceof ColdEntityLink )
////                {   DiagramEntityChangedEvent event = new DiagramEntityChangedEvent( (ColdEntityLink)current, null, this );
////                    this.fireDiagramEntityChanged(event);
////                }
////            }
////        }   
//    }
//    
//    /* ######################################################################
//     * ################# CurrentEditorChangedListener impl ##################
//     * ###################################################################### */
//    
//    public void processCurrentEditorChange(CurrentEditorChangedEvent e)
//    {   /* we have to select the given editor */
//        this.display(e.getEditor());
//    }
//    
//    /* ######################################################################
//     * ################# CurrentEntityChangedListener impl ##################
//     * ###################################################################### */
//    
//    public void processCurrentEntityChange(CurrentEntityChangedEvent e)
//    {   // PENDING : ca va rien casse
////        /* try to add a new editor to the tabbed pane if it is possible */
////        ColdType entity = e.getNewEntity();
////        
////        if ( entity instanceof ColdEntityLink )
////        {   DiagramSelectionChangedEvent evt = new DiagramSelectionChangedEvent( (ColdEntityLink)entity, this );
////            this.fireDiagramSelectionChanged(evt);
////        }
////        
////        /* select one diagram that contains the given entity */
////        
////        if(entity instanceof ColdNodeLink)
////        {   /* test if the node appears in one diagram */
////            Iterator entities = this.entitiesMapping.keySet().iterator();
////            
////            while(entities.hasNext())
////            {   ColdType current = (ColdType)entities.next();
////                
////                if ( ! (current instanceof ColdDiagram) ) continue;
////                
////                if ( ! ((ColdDiagram)current).getASGNodeLinkList().contains(entity) ) continue;
////                
////                /* we must select the corresponding editor */
////                DiagramEditor editor = (DiagramEditor) this.entitiesMapping.get(current);
////                
////                this.display( (ColdDiagram)current );
////                
////                return;
////            }
////        }
////        
////        /* if an ASGEdge was selected, display the first editor containing it and select its figure */
////        else if(entity instanceof ColdEdgeLink)
////        {   /* test if the node appears in one diagram */
////            Iterator entities = this.entitiesMapping.entrySet().iterator();
////            
////            while(entities.hasNext())
////            {   ColdType current = (ColdType)entities.next();
////                
////                if ( ! (current instanceof ColdDiagram) ) continue;
////                
////                if ( ! ((ColdDiagram)current).getASGEdgeLinkList().contains(entity) ) continue;
////                
////                /* we must select the corresponding editor */
////                DiagramEditor editor = (DiagramEditor) this.entitiesMapping.get(current);
////                
////                this.display( (ColdDiagram)current );
////                
////                return;
////            }
////        }
////        
////        /* try to display the entity if it has its own editor */
////        this.display(entity);
//    }
//
//}
