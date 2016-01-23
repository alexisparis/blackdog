package org.siberia.ui.swing.text.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import org.siberia.type.lang.SibImport;

/**
 *
 * @author alexis
 */
public interface SpellChecker
{
    /** run the spellChecker, configure it with a given criterion and place it with the given point
     *  @param input the criterion
     *  @param point the location to display the spellChecker
     */
    public void run(String input, Point point);
    
    /** tell the spellChecker about a parsing result
     *  @param map a map<String, Class>
     */
    public void setParsingResults(Map<String, Class> map);
    
    /** set the import used
     *  @param imports a list of SibImport
     */
    public void setImports(List<SibImport> imports);
    
    /** set the sentence delimiter for this kind of spellChecker
     *  @param delimiter the sentence delimiter
     */
    public void setSentenceDelimiter(String delimiter);
    
    /** get the sentence delimiter for this kind of spellChecker
     *  @return the sentence delimiter
     */
    public String getSentenceDelimiter();
    
    /** update spellChecker list according with a given criterion
     *  @param input the new criterion
     */
    public void updateList(String input);
    
    /** indictaes if the spellChecker is currently running
     *  @return true if the spellChecker is currently running
     */
    public boolean isRunning();
    
    /** set the graphical invoker for the spellChecker
     *  @param component the invoker
     */
    public void setInvoker(Component component);
    
    /** stop the spellChecker */
    public void stop();
    
    /** set the actionListener that will be warned when a choice will be made */
    public void setActionListener(ActionListener actionListener);
    
    /** modify the position of the spellChecker */
    public void updatePosition(Point p);
    
    /** return the current selected object */
    public String getCurrentWord();
    
    /** modify the size of the spellChecker */
    public void updateSize();
    
    /** select the next element in the spellChecker */
    public void selectNext();
    
    /** select the previous element in the spellChecker */
    public void selectPrevious();
    
    /** select the next page in the spellChecker */
    public void nextPage();
    
    /** select the previous page in the spellChecker */
    public void previousPage();
}
