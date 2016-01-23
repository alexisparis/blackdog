package org.siberia.ui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.kernel.Kernel;
import org.siberia.kernel.editor.NoLauncherFoundException;
import org.siberia.type.message.SibInformationMessage;
import org.siberia.type.message.SibMessage;
import org.siberia.utilities.random.Randomizer;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public abstract class SendMessageAction extends GenericAction
{
    /** Creates a new instance of DebugTableAction */
    public SendMessageAction()
    {   super(); }
    
    /** create a new SibMessage
     *	@return a new sibMessage
     */
    protected abstract SibMessage createMessage();

    /** method called when the action has to be performed
     *  @param e an ActionEvent
     */
    public void actionPerformed(ActionEvent e)
    {   
	EditorLaunchContext context = new DefaultEditorLaunchContext();
	
	SibMessage msg = this.createMessage();
	
	this.customizeMessage(msg);
	
	context.setItem(msg);

	try
	{   Kernel.getInstance().getResources().edit(context); }
	catch (Exception ex)
	{   
	    ex.printStackTrace();
	}
    }
    
    /** customize the message
     *	@param message a SibMessage
     */
    protected void customizeMessage(SibMessage message)
    {
	try
	{
	    message.setName(Randomizer.randomString(Randomizer.randomInteger(2, 45)));
	}
	catch(Exception ex)
	{
	    ex.printStackTrace();
	}
    }
}
