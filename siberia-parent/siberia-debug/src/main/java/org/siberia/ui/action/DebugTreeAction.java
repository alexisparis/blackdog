package org.siberia.ui.action;

import java.beans.PropertyVetoException;
import java.util.List;
import org.siberia.type.DebugTree;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;
import org.siberia.ui.action.impl.TypeReferenceEditingAction;

/**
 *
 * Action that launch the debug tool for tree
 *
 * @author alexis
 */
public class DebugTreeAction extends TypeReferenceEditingAction<DebugTree>
{
    /** Creates a new instance of DebugTableAction */
    public DebugTreeAction()
    {   super(); }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof ColdAbout
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected DebugTree createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
	
	DebugTree type = new DebugTree();
	
        try
        {   type.setName("Debug tree"); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        return type;
    }
}
