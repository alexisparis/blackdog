/*
 * ForcableView.java
 *
 * Created on 25 juin 2005, 19:54
 */

package org.siberia.ui.swing.text.numbered;

/**
 *
 * @author alexis
 */
public interface ForcableView {
    
    public void forceSetInsets();
    
    /** tell if the line numbers is to be displayed
     *  @return a boolean
     */
    public boolean isLineNumbersDisplayed();
    
    /** set if the line numbers is to be displayed
     *  @param lineVisible a boolean
     */
    public void setLineNumbersDisplayed(boolean lineVisible);
    
}
