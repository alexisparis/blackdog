package org.siberia.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.Action;
import org.siberia.plugin.PluginDeclaration;

/**
 *
 * @author alexis
 */
public class ListPluginDirAction extends ReportingAction
{
    
    /** Creates a new instance of ListPluginDirAction */
    public ListPluginDirAction()
    {
        super();
        
        this.putValue(Action.NAME, "List plugins directory");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        this.getTextComponent().setText("");

        StringBuffer buffer = new StringBuffer(500);
        buffer.append("Root directory of registered plugins : \n\n");

        Iterator<String> dirs = PluginDeclaration.getPluginsRootDirectory().iterator();

        while(dirs.hasNext())
        {
            buffer.append(dirs.next() + "\n");
        }

        this.getTextComponent().setText(buffer.toString());
    }
    
}
