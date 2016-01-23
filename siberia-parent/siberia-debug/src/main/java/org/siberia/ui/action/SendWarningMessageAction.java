package org.siberia.ui.action;

import org.siberia.type.message.SibMessage;
import org.siberia.type.message.SibWarningMessage;

/**
 *
 * generate a new random message
 *
 * @author alexis
 */
public class SendWarningMessageAction extends SendMessageAction
{
    /** Creates a new instance of SendInformationMessageAction */
    public SendWarningMessageAction()
    {   super(); }
    
    /** create a new SibMessage
     *	@return a new sibMessage
     */
    protected SibMessage createMessage()
    {
	return new SibWarningMessage();
    }
}
