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
//import java.awt.Container;
//import java.awt.event.MouseEvent;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//import javax.swing.JScrollPane;
//import javax.swing.tree.TreePath;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.event.TreeModelEvent;
//import javax.swing.event.TreeSelectionEvent;
//import org.siberia.eventsystem.item.XMLEventSystemItem;
//import org.siberia.kernel.Kernel;
//import org.siberia.kernel.eventsystem.event.CurrentEntityChangedEvent;
//import org.siberia.kernel.eventsystem.event.EntityModifiedEvent;
//import org.siberia.kernel.eventsystem.event.ImportingEvent;
//import org.siberia.kernel.eventsystem.event.LoadingEvent;
//import org.siberia.kernel.eventsystem.event.ReinitProjectEvent;
//import org.siberia.kernel.eventsystem.event.StructureModifiedEvent;
//import org.siberia.type.ColdType;
//import org.siberia.kernel.eventsystem.listener.CurrentEntityChangedListener;
//import org.siberia.kernel.eventsystem.listener.EntityModificationListener;
//import org.siberia.kernel.eventsystem.listener.ImportingListener;
//import org.siberia.kernel.eventsystem.listener.LoadingListener;
//import org.siberia.kernel.eventsystem.listener.ReinitProjectListener;
//import org.siberia.kernel.eventsystem.listener.StructureModificationListener;
//import org.siberia.type.ColdType;
//import org.siberia.ui.eventsystem.event.ComponentVisibilityEvent;
//import org.siberia.ui.eventsystem.event.CurrentEditorChangedEvent;
//import org.siberia.ui.eventsystem.listener.ComponentVisibilityListener;
//import org.siberia.ui.eventsystem.listener.CurrentEditorChangedListener;
//import org.siberia.ui.swing.tabbedpane.GrowingCloseableTabbedPane;
//import org.siberia.ui.swing.tabbedpane.event.DoubleClickListener;
//import org.siberia.ui.swing.tree.GenericTree;
//import org.siberia.ui.swing.tree.model.ConfigurableTreeModel;
//
///**
// *
// * classe which is responsible for the tree view
// * it contains a JTree and is part of the high level event sub system
// *
// * @author alexis
// */
//public class TreeArea extends    GrowingCloseableTabbedPane
//                      implements LoadingListener,
//                                 ReinitProjectListener,
//                                 ImportingListener,
//                                 StructureModificationListener,
//                                 EntityModificationListener,
//                                 CurrentEntityChangedListener,
//                                 TreeSelectionListener,
//                                 ComponentVisibilityListener,
//                                 CurrentEditorChangedListener,
//                                 DoubleClickListener,
//                                 XMLEventSystemItem
//{
//    /** id --> entity displayed in the tree area */
//    private Map<String, ColdType>  entities   = null;
//    
//    /** map which linked displayed entities with {JScrollPane, JTree} */
//    private Map<String, Navigator> navigators = null;
//    
//    /** map which links object name with label */
//    private Map<String, String>   labels      = null;
//    
//    /** Creates a new instance of IntegratedTree */
//    public TreeArea()
//    {   super(); }
//    
//    /** return the path of the file that describe the configuration
//     *  @return the path of the file that describe the configuration
//     */
//    public String getEventConfigurationFilePath()
//    {   return File.separator + "org" + File.separator + "siberia" + File.separator + "rc" + File.separator +
//               "event" + File.separator + "treearea.xml";
//    }
//    
//    /** add a new Entity to the tree area
//     *  @param type a ColdType
//     *  @param label the label to use
//     *  @param model an implementation of ConfigurableTreeModel
//     */
//    public void add(ColdType type, String label, ConfigurableTreeModel model)
//    {   if ( type != null && model != null )
//        {   Navigator navigator = new Navigator(null, new GenericTree());
//            
//            if ( this.entities == null )
//                this.entities = new HashMap<String, ColdType>();
//            if ( this.navigators == null )
//                this.navigators = new HashMap<String, Navigator>();
//            if ( this.labels == null )
//                this.labels = new HashMap<String, String>();
//            
//            String id = null;
//            
//            do
//            {   id = type.getName();
//                // PENDING : get a unique id
//            }
//            while(this.labels.containsKey(id));
//            
//            model.setRoot(type);
//            navigator.getTree().setModel(model);
//            navigator.setScrollPane(new JScrollPane(navigator.getTree()));
//            
//            this.entities.put(id, type);
//            this.navigators.put(id, navigator);
//            this.labels.put(id, label);
//            
//            navigator.getTree().addTreeSelectionListener(this);
//            
//            /* add the navigator to the tree area */
//            System.out.println("label      : " + label);
//            System.out.println("scrollPane : " + navigator.getScrollPane());
//            System.out.println("tree       : " + navigator.getTree());
//            System.out.println("treemodel  : " + navigator.getTree().getModel().getClass());
//            this.add(label, navigator.getScrollPane());//new javax.swing.JButton(label));//getScrollPane());
//        }
//    }
//    
//    /** add a new Entity to the tree area
//     *  @param type a ColdType
//     *  @param model an implementation of ConfigurableTreeModel
//     */
//    public void add(ColdType type, ConfigurableTreeModel model)
//    {   if ( type != null )
//            this.add(type, type.getName(), model);
//    }
//    
//    /** return the genericTree located at tab numero i
//     *  @param i index of the tab
//     *  @return a GenericTree
//     */
//    private GenericTree getTreeAt(int i)
//    {   GenericTree tree = null;
//        if ( i >= 0 && i < this.getTabCount() )
//        {   Component c = this.getComponentAt(i);
//            if ( c instanceof JScrollPane )
//            {   if ( this.navigators != null )
//                {   Iterator<Navigator> it = this.navigators.values().iterator();
//                    while(it.hasNext())
//                    {   Navigator current = it.next();
//                        if ( current != null )
//                        {   if ( current.getScrollPane() == c )
//                            {   tree = current.getTree();
//                                break;
//                            }       
//                        }
//                    }
//                }
//            }
//        }
//        return tree;
//    }
//    
//    /** return the tree of the selected tab
//     *  @return a GenericTree instance or null if any
//     */
//    private GenericTree getDisplayedTree()
//    {   return this.getTreeAt(this.getSelectedIndex()); }
//    
//    /** return the model of the selected tab
//     *  @return a ConfigurableTreeModel instance or null if any
//     */
//    private ConfigurableTreeModel getDisplayedModel()
//    {   ConfigurableTreeModel model = null;
//        GenericTree tree = this.getDisplayedTree();
//        if ( tree != null )
//        {   if ( tree.getModel() instanceof ConfigurableTreeModel )
//                model = (ConfigurableTreeModel)tree.getModel();
//        }
//        return model;
//    }
//    
//    /** return the root type of the selected tab
//     *  @return a ColdType instance or null if any
//     */
//    private ColdType getDisplayedRoot()
//    {   ColdType type = null;
//        ConfigurableTreeModel model = this.getDisplayedModel();
//        if ( model != null )
//        {   if ( model.getRoot() instanceof ColdType )
//                type = (ColdType)model.getRoot();
//        }
//        return type;
//    }
//    
//    /* return the selected item in the project tree
//     * @return the objects selected in the tree or null if there is any selection
//     **/
//    public ColdType[] getSelectedElement()
//    {   ColdType[] selection = null;
//        GenericTree tree = this.getDisplayedTree();
//        if ( tree != null )
//        {   TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
//            selection = new ColdType[paths.length];
//            int index = 0;
//            for(int i = 0; i < paths.length; i++)
//            {   Object current = paths[i].getLastPathComponent();
//                if ( current instanceof ColdType )
//                {   selection[index++] = (ColdType)current; }
//            }
//        }
//        return selection;
//    }
//    
//    /** fire to all ConfigurableTreeModel that a change occurs on their structure
//     *  @param entity the entity concerned by modification
//     *  @param root the parent of the entity
//     *  @param add a Boolean : <br><ul><li>TRUE if entity has been added</li>
//     *                                 <li>FALSE if entity has been removed</li>
//     *                                  <li>null if it was modified</li></ul>
//     */
//    private void fireTreeModels(ColdType entity, ColdType root, Boolean add)
//    {   if ( this.entities != null )
//        {   if ( entity != null && root != null )
//            {   Iterator<String> ids = this.entities.keySet().iterator();
//                while(ids.hasNext())
//                {   String currentId = ids.next();
//                    ColdType currentType = this.entities.get(currentId);
//                    if ( currentType != null )
//                    {   if ( entity.hasForAncestor(currentType) )
//                        {   
//                            Navigator nav = this.navigators.get(currentId);
//                            if ( nav != null )
//                            {   if ( nav.getTree() != null )
//                                {   if ( nav.getTree().getModel() instanceof ConfigurableTreeModel )
//                                    {   ConfigurableTreeModel model = (ConfigurableTreeModel)nav.getTree().getModel();
//                                        
//                                        if (add = Boolean.TRUE)
//                                        {   TreeModelEvent evt = new TreeModelEvent(this, root.getPath(),
//                                                                                    new int[] {root.getChildrenCount() - 1},
//                                                                                    new ColdType[] {entity});
//                                            model.fireTreeNodesInserted(evt);
//                                        }
//                                        else if ( add = Boolean.FALSE )
//                                        {   TreeModelEvent evt = new TreeModelEvent(this, root.getPath(),
//                                                                                    new int[] {root.indexOfChild(entity)},
//                                                                                    new ColdType[] {entity});
//                                            model.fireTreeNodesRemoved(evt);
//                                        }
//                                        else if ( add = null )
//                                        {   TreeModelEvent evt = new TreeModelEvent(this, entity.getPath());
//                                            model.fireTreeNodesChanged(evt);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//                                           
//    /* ######################################################################
//     * ############### StructureModificationListener impl ###################
//     * ###################################################################### */
//    
//    public void processStructureModification(StructureModifiedEvent e)
//    {   if ( e != null )
//        {   this.fireTreeModels(e.getEntity(), e.getRootEntity(), new Boolean(e.addMode())); }
//    }
//    
//    /** apply selections on the current tree */
//    private void applySelections()
//    {   this.applySelections(this.getDisplayedTree()); }
//    
//    /** apply selections on a given tree
//     *  @param tree a GenericTree
//     */
//    private void applySelections(GenericTree tree)
//    {   ConfigurableTreeModel model = this.getDisplayedModel();
//        if ( tree != null && model != null )
//        {   //tree.getSelectionModel().clearSelection();
//            
//            Set<ColdType> selection = Kernel.getInstance().getResources().getSelectedElements();
//            if ( selection != null )
//            {   Iterator<ColdType> it = selection.iterator();
//                while(it.hasNext())
//                {   ColdType current = it.next();
//                    if ( current != null && model.getRoot() instanceof ColdType)
//                    {   if ( current.hasForAncestor((ColdType)model.getRoot()) )
//                        {   /* select this node */
//                            tree.getSelectionModel().addSelectionPath(new TreePath(current.getPath()));
//                        }
//                    }
//                }
//            }
//        }
//    }
//                                           
//    /* ######################################################################
//     * ##################### ImportingListener impl #########################
//     * ###################################################################### */
//    
//    public void processImport(ImportingEvent e)
//    {   /* PENDING */ }
//                                           
//    /* ######################################################################
//     * ################### ReinitProjectListener impl #######################
//     * ###################################################################### */
//    
//    public void processReinitProject(ReinitProjectEvent e)
//    {   
//        // PENDING : to do
//        //this.linkWithProject( KernelResources.getInstance().getProject() );
//    }
//                                           
//    /* ######################################################################
//     * ############### StructureModificationListener impl ###################
//     * ###################################################################### */
//    
//    public void processEntityModification(EntityModifiedEvent e)
//    {   if ( e != null )
//        {   this.fireTreeModels(e.getEntity(), e.getRootEntity(), null); } 
//    }
//    
//    /* ######################################################################
//     * ############### CurrentEditorChangedListener impl ####################
//     * ###################################################################### */
//    
//    public void processCurrentEditorChange(CurrentEditorChangedEvent e)
//    {   // PENDING: to do
//    }
//    
//    /* ######################################################################
//     * ###################### LoadingListener impl ##########################
//     * ###################################################################### */
//    
//    public void processLoad(LoadingEvent e)
//    {   /** indicates that the tree displaying metaModels has changhed */
//        if ( e.concernMetaModelLoading() )
//        {   System.out.println("fireStructure ...");
//            // PENDING : to do
////            this.modelMM.fireTreeStructureChanged(new TreeModelEvent(this.modelMM, new Object[]{ e.getLoadedEntity()}));
//        }
//    }
//    
//    /* ######################################################################
//     * ############### ComponentVisibilityListener impl #####################
//     * ###################################################################### */
//    
//    public void processChangeVisibility(ComponentVisibilityEvent e)
//    {   
//        // PENDING : to reintegrate
////        if(e.getVisibility())
////         {  if (e.getComponentName().equals("projectTab"))
////                this.add("Project", this.treeViewProject);
////            else if(e.getComponentName().equals("metamodelTab"))
////                this.add("MetaModels", this.treeViewMM);
////         }
////         else
////         {  if (e.getComponentName().equals("projectTab"))
////                this.remove(this.treeViewProject);
////            else if(e.getComponentName().equals("metamodelTab"))
////                this.remove(this.treeViewMM);
////         }
//    }
//                                           
//    /* ######################################################################
//     * ################ CurrentEntityChangeListener impl ####################
//     * ###################################################################### */
//    
//    public void processCurrentEntityChange(CurrentEntityChangedEvent e)
//    {   this.applySelections(); }
//    
//    /* ######################################################################
//     * ################### TreeSelectionListener impl #######################
//     * ###################################################################### */
//                                               
//    /** tell others that the selection on the tree has changed **/
//    public void valueChanged(TreeSelectionEvent e)
//    {   GenericTree tree =this.getDisplayedTree();
//        if ( tree != null )
//        {   TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
//            ColdType[] types = null;
//            if ( paths != null )
//            {   types = new ColdType[paths.length];
//                int index = 0;
//                
//                for(int i = 0; i < paths.length; i++)
//                {   TreePath currentPath = paths[i];
//                    if ( currentPath != null )
//                    {   Object leaf = currentPath.getLastPathComponent();
//                        if ( leaf instanceof ColdType )
//                        {   types[index++] = (ColdType)leaf; }
//                    }
//                }
//            }
//            System.out.println("types : " + types);
//            Kernel.getInstance().getResources().setSelectedElements(types);
//        }
//    }
//    
//    /** ########################################################################
//     *  ################# ClosableTabbedPane implementation ####################
//     *  ######################################################################## */
//
//    /** process a close page event **/
//    public void closeOperation(MouseEvent e, int index)
//    {   String tabLabel = this.getTitleAt(index);
//        this.removeTabAt(index);
//        
//        // PENDING : to integrate
////        if(tabLabel.equals("Project"))
////            this.fireComponentVisibility(new ComponentVisibilityEvent("projectTab", false, this));
////        else if(tabLabel.equals("MetaModels"))
////            this.fireComponentVisibility(new ComponentVisibilityEvent("metamodelTab", false, this));
//    }
//
//    /** process a page change event
//     *  we must use an AWTEvent because, both ActionEvent and MouseEvent
//     *  provoke page changed event
//     **/
//    public void pageChange(AWTEvent e, int oldPosition, int newPosition)
//    {   this.applySelections(this.getTreeAt(newPosition)); }
//    
//    /** describe a navigator, it means an entity that contains references to a JScrollPane
//     *  and GenericTree
//     */
//    private static class Navigator
//    {
//        /** reference to the JScrollPane */
//        private JScrollPane pane = null;
//        
//        /** reference to the GenericTree */
//        private GenericTree tree = null;
//        
//        /** create a new Navigator */
//        public Navigator()
//        {   this(null, null); }
//        
//        /** create a new Navigator
//         *  @param scrollPane a JScrollPane
//         *  @param tree a GenericTree
//         */
//        public Navigator(JScrollPane scrollPane, GenericTree tree)
//        {   this.setScrollPane(scrollPane);
//            this.setTree(tree);
//        }
//
//        public JScrollPane getScrollPane()
//        {   return pane; }
//
//        public void setScrollPane(JScrollPane pane)
//        {   this.pane = pane; }
//
//        public GenericTree getTree()
//        {   return tree; }
//
//        public void setTree(GenericTree tree)
//        {   this.tree = tree; }
//    }
//        
//}
