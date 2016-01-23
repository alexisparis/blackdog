package org.siberia.ui.editor;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.List;
import org.siberia.TypeInformationProvider;
import org.siberia.editor.AdaptedEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.type.Namable;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.AbstractSibType;
import org.siberia.ui.swing.table.model.IntrospectionSibTypeListTableModel;
import org.siberia.ui.swing.tree.model.MutableTreeModel;
import org.siberia.ui.swing.treetable.SibTypeListTreeTable;
import org.siberia.ui.swing.treetable.SibTypeListTreeTablePanel;
import org.siberia.ui.swing.treetable.introspection.IntrospectionSibTypeListTreeTableModel;
import org.siberia.ui.swing.treetable.introspection.PageablePathConverter;

/**
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.DebugTreeTable.class},
                  description="Debugger for table",
                  name="Table debugger",
                  launchedInstancesMaximum=-1)
public class DebugTreeTableEditor extends AdaptedEditor
{
    /**
     * Creates a new instance of DebugTableEditor
     */
    public DebugTreeTableEditor()
    {   super(null, false, false); }

    /**
     * return the component that render the editor
     * 
     * @return a Component
     */
    @Override
    public Component getComponent()
    {   
	/* return a DebugTablePanel */
	DebugTablePanel panel = new DebugSibListTreeTablePanel();
	
        SibList list = new SibList();
        try
        {   list.setName("types list"); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
	for(int i = 0 ; i < 4; i++)
	{
	    /* use introspection */
	    List<Class> classes = TypeInformationProvider.getInstance().getSubClassFor(AbstractSibType.class, true, false);

	    if ( classes != null )
	    {   Iterator<Class> it = classes.iterator();

		while(it.hasNext())
		{   Class current = it.next();

		    if ( current != null )
		    {   try
			{   Object o = current.newInstance();

			    if ( o instanceof SibType )
			    {   try
				{   ((SibType)o).setName(current.getSimpleName() + "_" + i); }
				catch(Exception ex)
				{   logger.info("error when setting name for a type '" + current + "'");
				    ex.printStackTrace();
				}

				list.add(o);
			    }
			}
			catch(Exception e)
			{   logger.error("error when instantiating class '" + current + "'");
			    e.printStackTrace();
			}
		    }
		}
	    }
	}
	
	/** create TableModel */
	IntrospectionSibTypeListTreeTableModel treeTableModel = new IntrospectionSibTypeListTreeTableModel();

	MutableTreeModel treeModel = new MutableTreeModel();
	treeModel.setRoot(list);
	treeTableModel.setInnerConfigurableTreeModel(treeModel);
	
	treeTableModel.getInnerTableModel().setProperties(Namable.PROPERTY_NAME, Namable.PROPERTY_NAME_COULD_CHANGE, 
		Plugin.PROPERTY_VERSION_CHOICE, Plugin.PROPERTY_SHORT_DESC, SibType.PROPERTY_IDENTITY_HASHCODE);

	((SibTypeListTreeTable)panel.getTablePanel().getTable()).setTreeTableModel(treeTableModel);
//	panel.setPreferredColumnsSize(250);

	treeTableModel.setPathConverter(new PageablePathConverter(((SibTypeListTreeTable)panel.getTablePanel().getTable()).getTreeRenderer()));
	
	return panel;
    }
}
