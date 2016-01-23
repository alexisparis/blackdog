package org.siberia.ui.editor;

import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.TablePanel;

/**
 *
 * @author alexis
 */
public class DebugSibListTablePanel extends DebugTablePanel<SibListTablePanel>
{
    
    /** Creates a new instance of DebugSibListTablePanel */
    public DebugSibListTablePanel()
    {	}
    
    
    /** create the TablePanel
     *	@return a TablePanel
     */
    protected SibListTablePanel createPageablePanel()
    {
	SibListTablePanel panel = new SibListTablePanel();
	
	panel.setConfigurationRelativeFilePath("debugTablePane.properties");
	
	return panel;
    }
    
}
