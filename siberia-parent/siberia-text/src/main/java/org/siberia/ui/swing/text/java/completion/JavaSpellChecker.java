package org.siberia.ui.swing.text.java.completion;
import org.siberia.type.service.provider.JavaLangageServiceProvider;
import org.siberia.type.service.ServiceProvider;
import org.siberia.ui.swing.text.util.AbstractSpellChecker;

/**
 *
 * @author alexis
 */
public class JavaSpellChecker extends AbstractSpellChecker
{
    
    /** Creates a new instance of JavaSpellChecker */
    public JavaSpellChecker()
    {   super();
        
        this.setSentenceDelimiter(";");
    }
    
    /** return a ServiceProvider needed by the spellChecker
     *  @return a ServiceProvider needed by the spellChecker
     */
    protected ServiceProvider getServiceProvider()
    {   return JavaLangageServiceProvider.getInstance(); }
    
}
