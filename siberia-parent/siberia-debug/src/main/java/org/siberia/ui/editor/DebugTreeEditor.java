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
@Editor(relatedClasses={org.siberia.type.DebugTree.class},
                  description="Debugger for tree",
                  name="Tree debugger",
                  launchedInstancesMaximum=-1)
public class DebugTreeEditor extends AdaptedEditor
{
    /**
     * Creates a new instance of DebugTableEditor
     */
    public DebugTreeEditor()
    {   super(null, false, false); }

    /**
     * return the component that render the editor
     * 
     * @return a Component
     */
    @Override
    public Component getComponent()
    {   
	return new DebugTreePanel();
    }
}
