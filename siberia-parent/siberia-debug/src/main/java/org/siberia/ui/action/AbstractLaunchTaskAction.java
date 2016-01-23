package org.siberia.ui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.kernel.Kernel;
import org.siberia.type.task.SibTask;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public abstract class AbstractLaunchTaskAction extends GenericAction
{
    public static char NAME = 'a';
    
    /** Creates a new instance of DebugTableAction */
    public AbstractLaunchTaskAction()
    {   super(); }
    
    /** create a new SibTask
     *	@return a new SibTask
     */
    protected abstract SibTask createTask();

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   
	EditorLaunchContext context = new DefaultEditorLaunchContext();
	
	SibTask msg = this.createTask();
	
	try
	{
	    msg.setName("" + NAME);
	    NAME += 1;
	}
	catch(PropertyVetoException ec)
	{
	    ec.printStackTrace();
	}
	
	context.setItem(msg);

	try
	{   Kernel.getInstance().getResources().edit(context); }
	catch (Exception ex)
	{   
	    ex.printStackTrace();
	}
    }
}
