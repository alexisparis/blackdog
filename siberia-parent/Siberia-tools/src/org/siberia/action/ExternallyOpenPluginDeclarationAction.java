package org.siberia.action;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.Action;
import javax.swing.JOptionPane;
import org.siberia.plugin.PluginDeclaration;

/**
 *
 * @author alexis
 */
public class ExternallyOpenPluginDeclarationAction extends ReportingAction
{
    
    /** Creates a new instance of ListPluginDirAction */
    public ExternallyOpenPluginDeclarationAction()
    {
        super();
        
        this.putValue(Action.NAME, "Open plugins declaration in default text editor");
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        
        if ( Desktop.isDesktopSupported() )
        {
            Iterator<String> dirs = PluginDeclaration.getPluginsRootDirectory().iterator();

            while(dirs.hasNext())
            {
                File f = new File(dirs.next() + File.separator + "src" +
                                                File.separator + "main" +
                                                File.separator + "resources" + 
                                                File.separator + "plugin.properties");
                try
                {
                    Desktop.getDesktop().open(f);
                } catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Java Desktop not supported by OS",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
