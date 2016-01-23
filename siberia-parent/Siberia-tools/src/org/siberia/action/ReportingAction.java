package org.siberia.action;

import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;

/**
 *
 * @author alexis
 */
public abstract class ReportingAction extends AbstractAction
{
    /** jeditor ou inscrire le rapport */
    private JTextComponent text = null;
    
    /** Creates a new instance of ReportingAction */
    public ReportingAction()
    {   }
    
    /** initialise le composant text ou l'action peut effectuer son rapport
     *  @param text un composant text
     */
    public void setTextComponent(JTextComponent text)
    {
        this.text = text;
    }
    
    /** return le composant text permettant la visualisation du rapport
     *  @return un composant text
     */
    public JTextComponent getTextComponent()
    {
        return this.text;
    }
    
}
