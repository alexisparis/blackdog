package org.siberia.ui.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * call System.gc()
 *
 * @author alexis
 */
public class CallGCAction extends AbstractAction
{
    /** Creates a new instance of DebugTableAction */
    public CallGCAction()
    {   super(); }

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   
	System.gc();
    }
}
