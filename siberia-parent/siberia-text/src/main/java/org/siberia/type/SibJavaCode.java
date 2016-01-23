package org.siberia.type;

import org.siberia.SiberiaTextPlugin;
import org.siberia.type.annotation.bean.Bean;

/**
 *  Class representing java code
 *
 *  @author alexis
 */
@Bean(  name="java code",
        internationalizationRef="org.siberia.rc.i18n.type.SibJavaCode",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibJavaCode extends SibCode
{
    /** create a new java code */
    public SibJavaCode()
    {   super();
        this.textType = "text/java"; 
        this.setSupportSpellCheck(true);
        this.setSupportImportManagement(true);
        this.setCommentSequence("//");
    }
    
    /** returns the class that can parse the content of the object
     *  @return the class responsible for the parsing of the content
     */
    public Class getParserClass()
    {   return org.siberia.type.service.parser.ScriptJavaParser.class; }
    
    /** returns the class that can manage import
     *  @return the class responsible for management of import
     */
    public Class getImportManagerClass()
    {   return org.siberia.type.service.importmanager.JavaImportManager.class; }
}
