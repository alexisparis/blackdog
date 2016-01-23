package org.siberia.ui.editor;

import java.awt.Component;
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

/**
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.DebugTable.class},
                  description="Debugger for table",
                  name="Table debugger",
                  launchedInstancesMaximum=-1)
public class DebugTableEditor extends AdaptedEditor
{
    /**
     * Creates a new instance of DebugTableEditor
     */
    public DebugTableEditor()
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
	DebugTablePanel panel = new DebugSibListTablePanel();
	
	/** create TableModel */
	IntrospectionSibTypeListTableModel model = new IntrospectionSibTypeListTableModel();
	model.setProperties(Namable.PROPERTY_NAME, Namable.PROPERTY_NAME_COULD_CHANGE, Plugin.PROPERTY_VERSION_CHOICE,
			    Plugin.PROPERTY_SHORT_DESC, SibType.PROPERTY_IDENTITY_HASHCODE);
	
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
				{   ((SibType)o).setName(current.getSimpleName());// + "_" + i);
				}
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
	
	model.setList(list);
	
	/* customize panel */
	panel.getTablePanel().setModel(model);
	
	
	return panel;
    }
}
