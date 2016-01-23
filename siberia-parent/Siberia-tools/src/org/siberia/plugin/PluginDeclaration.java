package org.siberia.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * give information about registered plugins
 * 
 * @author alexis
 */
public class PluginDeclaration
{
    
    /** Creates a new instance of PluginDeclaration */
    private PluginDeclaration()
    {
        
    }
    
    public static Set<String> getPluginsRootDirectory()
    {
        Set<String> dirs = new HashSet<String>();
        
        File input = new File("src/plugins.properties");
        
        try
        {   
            BufferedReader reader = new BufferedReader(new FileReader(input));
            
            String line = reader.readLine();
            
            while(line != null)
            {
                dirs.add(line);
                line = reader.readLine();
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        return dirs;
    }
    
    public static void main(String[] args)
    {
        Iterator<String> dirs = getPluginsRootDirectory().iterator();
        while(dirs.hasNext())
        {
            System.err.println("dir : " + dirs.next());
        }
        
    }
    
}
