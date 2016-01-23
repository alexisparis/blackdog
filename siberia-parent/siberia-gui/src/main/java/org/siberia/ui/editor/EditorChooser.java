/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.editor;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.ListDataListener;
import org.apache.log4j.Logger;
import org.siberia.kernel.Kernel;
import org.siberia.env.PluginContext;
import org.siberia.type.SibType;
import org.siberia.editor.launch.EditorLaunchContext;

/**
 *
 * @author alexis
 */
public class EditorChooser
{
    /* logger */
    private static Logger logger = Logger.getLogger(EditorChooser.class);
    
    /** list of classes */
    private Map<Integer, Class>     editorClasses = null;
    
    /** list of types */
    private List<SibType>          types         = null;
    
    /** map linking position and JComboBox */
    private Map<Integer, JComboBox> boxes         = null;
    
    /** Creates a new instance of EditorChooser
     *  @param contexts an array of EditorLaunchContext
     */
    public EditorChooser(EditorLaunchContext... contexts)
    {   
        /** build a List of types according to launch contexts */
        this.types = new ArrayList<SibType>(contexts == null ? 0 : contexts.length);
        
        if ( contexts != null )
        {   for(int i = 0; i < contexts.length; i++)
            {   EditorLaunchContext currentContext = contexts[i];
                
                if ( currentContext == null )
                {   this.types.add(null); }
                else
                {   this.types.add(currentContext.getItem()); }
            }
        }
        
        this.editorClasses = new HashMap<Integer, Class>    (this.types.size());
        this.boxes         = new HashMap<Integer, JComboBox>(this.types.size());
        
        /** feed the editor list */
        int index = 1;
        PluginContext context = Kernel.getInstance().getPluginContext();
        for(int i = 0; i < this.types.size(); i++)
        {   SibType type = this.types.get(i);
            
            Set<PluginContext.EditorDescriptor> descs = context.getEditorsFor(type.getClass());
            
            logger.debug("Editor for " + type.getClass() + " : ");
            Iterator<PluginContext.EditorDescriptor> it = descs.iterator();
            while(it.hasNext())
            {   PluginContext.EditorDescriptor current = it.next();
                
                logger.debug("\teditor class : " + current.getEditorClass());
            }
            
            if ( descs.size() == 0 )
                this.editorClasses.put(i, null);
            else if ( descs.size() == 1 )
                this.editorClasses.put(i, descs.iterator().next().getEditorClass());
            else
            {   /* build a JComboBox to allow user to choose */
                ComboBoxModel model = new EditorComboBoxModel(new ArrayList<PluginContext.EditorDescriptor>(descs));
                
                JComboBox box = new JComboBox(model);
                
                this.boxes.put(i, box);
            }
        }
    }
    
    /** create a Container representing the question to ask
     *  @return a Container
     */
    public void createContainer(Container container)
    {   FormLayout layout = new FormLayout("15px, pref, 5px, pref, 15px", "");
        container.setLayout(layout);
        
        if ( this.boxes != null )
        {   Iterator<Integer> positions = this.boxes.keySet().iterator();
            
            int line = 1;
            CellConstraints cc = new CellConstraints();
            
            while(positions.hasNext())
            {   int position = positions.next();
                
                JLabel label = new JLabel();
                SibType type = this.types.get(position);
                if ( type != null )
                {   label.setText(type.getName()); }
                JComboBox box = this.boxes.get(position);
                
                /* update the layout */
                layout.appendRow(new RowSpec("pref"));
                layout.appendRow(new RowSpec("5px"));
                
                container.add(label, cc.xy(2, line));
                container.add(box, cc.xy(4, line));
                
                line += 2;
            }
        }
    }
    
    /** return true if all editor has been fixed
     *  @return true if all editor has been fixed
     */
    public boolean isFixed()
    {   return this.editorClasses.size() == this.types.size(); }
    
    /** consider the value of the component to decide which editor to use for a given type */
    public void fix()
    {   if ( this.boxes != null )
        {   Iterator<Integer> positions = this.boxes.keySet().iterator();
            while(positions.hasNext())
            {   Integer currentPosition = positions.next();
                
                JComboBox box = this.boxes.get(currentPosition);
                if ( box != null )
                {   ComboBoxModel model = box.getModel();
                    
                    if ( model instanceof EditorComboBoxModel )
                    {   if ( this.editorClasses == null )
                            this.editorClasses = new HashMap<Integer, Class>();
                        this.editorClasses.put(currentPosition, ((EditorComboBoxModel)model).getSelectedEditorClass() );
                    }
                }
            }
        }
    }
    
    /** return a List of editor class to edit the types
     *  @return a List of Class
     */
    public List<Class> getEditorClasses()
    {   /* build a list according to the map */
        List<Class> classes = null;
        if ( this.editorClasses != null )
        {   List<Integer> positions = new ArrayList<Integer>(this.editorClasses.keySet());
            /* sort the list */
            Collections.sort(positions);
            
            classes = new ArrayList<Class>();
            Iterator<Integer> it = positions.iterator();
            
            while(it.hasNext())
            {   classes.add(this.editorClasses.get(it.next())); }
        }
        
        if ( classes == null )
            classes = Collections.EMPTY_LIST;
        return classes;
    }
    
    /** specific ComboboxModel */
    private class EditorComboBoxModel implements ComboBoxModel
    {
        /** ListDataListener */
        private List<ListDataListener>               listeners = null;
        
        /** List of EditorDescriptor */
        private List<PluginContext.EditorDescriptor> editors   = null;
        
        /** selected item */
        private PluginContext.EditorDescriptor       selection = null;
        
        public EditorComboBoxModel(List<PluginContext.EditorDescriptor> editors)
        {   this.editors = editors;
            
            if ( this.editors != null )
            {   if ( this.editors.size() > 0 )
                    this.selection = this.editors.get(0);
            }
        }
        
        public Class getSelectedEditorClass()
        {   Class c = null;
            if ( this.selection != null )
                c = this.selection.getEditorClass();
            return c;
        }
        
        public void setSelectedItem(Object anItem)
        {   if ( this.editors != null )
            {   Iterator<PluginContext.EditorDescriptor> it = this.editors.iterator();
                while(it.hasNext())
                {   PluginContext.EditorDescriptor current = it.next();
                    
                    if ( current.getName().equals(anItem) )
                    {   this.selection = current;
                        break;
                    }
                }
            }
        }

        public Object getElementAt(int index)
        {   Object result = null;
            if ( this.editors != null )
            {   if ( index >= 0 && index < this.editors.size() )
                {   result = this.editors.get(index).getName(); }
            }
            return result;
        }

        public void removeListDataListener(ListDataListener l)
        {   if ( this.listeners != null )
                this.listeners.remove(l);
        }

        public void addListDataListener(ListDataListener l)
        {   if ( this.listeners == null )
                this.listeners = new ArrayList<ListDataListener>();
            this.listeners.add(l);
        }

        public int getSize()
        {   int size = 0;
            if ( this.editors != null )
                size = this.editors.size();
            return size;
        }

        public Object getSelectedItem()
        {   Object selected = null;
            if ( this.selection != null )
                selected = this.selection.getName();
            return selected;
        }
        
    }
    
}
