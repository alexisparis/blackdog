package exempleplugin;

import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.components.interactivity.*;

/**
 * Hello world!
 * @goal sayhi
 */
public class AppMojo extends AbstractMojo
{
    
    /**
     * inputHandler
     * @component
     */
    private InputHandler inputHandler;
    
    public void execute () throws MojoExecutionException
    {
//	getLog ().info ("Hello, world. say hi please : ");
	
	try
	{
	    String line = inputHandler.readLine();
//	    readMultipleLines
//	    getLog ().info ("you said : " + inputvalue);
	}
	catch(Exception e)
	{
	    e.printStackTrace ();
	}
    }
}

