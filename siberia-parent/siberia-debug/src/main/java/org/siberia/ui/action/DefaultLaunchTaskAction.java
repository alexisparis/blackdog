package org.siberia.ui.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import org.apache.log4j.Logger;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.kernel.Kernel;
import org.siberia.type.task.AbstractSibTask;
import org.siberia.type.task.SibTask;
import org.siberia.type.task.SibTaskStatus;
import org.siberia.utilities.random.Randomizer;

/**
 *
 * generate a new task
 *
 * @author alexis
 */
public class DefaultLaunchTaskAction extends AbstractLaunchTaskAction
{
    /** logger */
    private Logger logger = Logger.getLogger(DefaultLaunchTaskAction.class);
    
    /** Creates a new instance of DebugTableAction */
    public DefaultLaunchTaskAction()
    {   super(); }
    
    /** create a new SibTask
     *	@return a new SibTask
     */
    protected SibTask createTask()
    {
	int _length = 10000;
	
	try
	{
	    _length = Randomizer.randomInteger(1000, 10000);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	
	final int length = _length;
	
	SibTask task = new AbstractSibTask()
	{
	    public void _run()
	    {
		long time = System.currentTimeMillis();
		
		while( (System.currentTimeMillis() - time) < length && ! SibTaskStatus.STOPPED.equals(this.getStatus()) )
		{
		    
		    /* determine progression */
		    float ratio = (  Math.abs(((float)length) - ((float)(System.currentTimeMillis() - time))) ) / ((float)length);
		    ratio *= 100;
		    ratio = 100 - ratio;
		    
		    this.setProgression( (int)ratio );
		    
		    try
		    {
			Thread.sleep(500);
		    }
		    catch (InterruptedException ex)
		    {	ex.printStackTrace(); }
		}
		
		if ( SibTaskStatus.STOPPED.equals(this.getStatus()) )
		{
		    logger.info("task '" + this.getName() + "' stopped with progression : " + this.getProgression());
		}
		else
		{
		    this.setProgression(100);
		}
	    }
	};
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("launching task deterministic=" + task.isDeterministic() + " stoppable=" + task.isStoppable() + " length(ms)=" + length);
	}
	
	return task;
    }
}
