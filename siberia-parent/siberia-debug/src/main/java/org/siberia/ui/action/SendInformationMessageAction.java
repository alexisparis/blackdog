package org.siberia.ui.action;

import org.siberia.type.message.SibInformationMessage;
import org.siberia.type.message.SibMessage;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public class SendInformationMessageAction extends SendMessageAction
{
    /** Creates a new instance of SendInformationMessageAction */
    public SendInformationMessageAction()
    {   super(); }
    
    /** create a new SibMessage
     *	@return a new sibMessage
     */
    protected SibMessage createMessage()
    {
	return new SibInformationMessage();
    }
}
