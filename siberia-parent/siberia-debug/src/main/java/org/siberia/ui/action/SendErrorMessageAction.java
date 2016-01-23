package org.siberia.ui.action;

import org.siberia.type.message.SibErrorMessage;
import org.siberia.type.message.SibMessage;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public class SendErrorMessageAction extends SendMessageAction
{
    /** Creates a new instance of SendInformationMessageAction */
    public SendErrorMessageAction()
    {   super(); }
    
    /** create a new SibMessage
     *	@return a new sibMessage
     */
    protected SibMessage createMessage()
    {
	return new SibErrorMessage();
    }
}
