package org.siberia.ui.action;

import java.beans.PropertyVetoException;
import java.util.List;
import org.siberia.type.DebugTable;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.ui.action.impl.*;
import org.siberia.ui.editor.DebugTableEditor;
import org.siberia.utilities.util.Parameter;
import org.siberia.ui.action.impl.TypeReferenceEditingAction;

/**
 *
 * Action that launch the debug tool for table
 *
 * @author alexis
 */
public class DebugTableAction extends TypeReferenceEditingAction<DebugTable>
{
    /** Creates a new instance of DebugTableAction */
    public DebugTableAction()
    {   super(); }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof ColdAbout
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected DebugTable createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
	
	DebugTable type = new DebugTable();
	
        try
        {   type.setName("Debug table"); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        return type;
    }
}
