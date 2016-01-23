package org.siberia.ui.action;

import org.siberia.type.message.SibInformationMessage;
import org.siberia.type.message.SibMessage;
import org.siberia.utilities.random.Randomizer;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public class SendLongInformationMessageAction extends SendMessageAction
{
    /** Creates a new instance of SendInformationMessageAction */
    public SendLongInformationMessageAction()
    {   super(); }
    
    /** create a new SibMessage
     *	@return a new sibMessage
     */
    protected SibMessage createMessage()
    {
	return new SibInformationMessage();
    }
    
    /** customize the message
     *	@param message a SibMessage
     */
    @Override
    protected void customizeMessage(SibMessage message)
    {
	try
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    buffer.append("<html>");
	    
	    for(int i = 0; i < 5; i++)
	    {
		buffer.append(Randomizer.randomString(Randomizer.randomInteger(2, 45)) + "<br>");
	    }
	    
	    buffer.append("</html>");
	    
	    message.setName(buffer.toString());
	}
	catch(Exception ex)
	{
	    ex.printStackTrace();
	}
    }
}
