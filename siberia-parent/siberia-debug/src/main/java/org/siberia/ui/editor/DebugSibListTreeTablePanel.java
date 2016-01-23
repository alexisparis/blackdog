package org.siberia.ui.editor;

import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.TablePanel;
import org.siberia.ui.swing.treetable.SibTypeListTreeTablePanel;

/**
 *
 * @author alexis
 */
public class DebugSibListTreeTablePanel extends DebugTablePanel<SibTypeListTreeTablePanel>
{
    
    /** Creates a new instance of DebugSibListTablePanel */
    public DebugSibListTreeTablePanel()
    {	}
    
    
    /** create the SibTypeListTreeTablePanel
     *	@return a SibTypeListTreeTablePanel
     */
    protected SibTypeListTreeTablePanel createPageablePanel()
    {
	SibTypeListTreeTablePanel panel = new SibTypeListTreeTablePanel();
	panel.setConfigurationRelativeFilePath("debugTreeTablePane.properties");
	return panel;
    }
    
}
