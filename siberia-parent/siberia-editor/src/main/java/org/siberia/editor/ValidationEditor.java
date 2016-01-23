/* 
 * Siberia editor : siberia plugin defining editor framework
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.editor;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.siberia.editor.exception.ClosingRefusedException;
import org.siberia.type.SibType;

/**
 *
 * @author alexis
 */
public abstract class ValidationEditor extends AbstractEditor
{
    /** validation panel */
    private JPanel         validationPanel  = null;
    
    /** validation button */
    private JButton        validationButton = null;
    
    /** cancel button */
    private JButton        cancelButton     = null;
    
    /** interval in pixel between two buttons */
    private int            buttonInterval   = 15;
    
    /** action listener on buttons */
    private ActionListener listener         = null;
    
    /** indicate if the validation panel is to be used */
    private boolean        useValidation    = true;
    
    /** Creates a new instance of ValidationEditor */
    public ValidationEditor()
    {   this(null); }
    
    /** build a new ValidationEditor
     *  @param instance SibType instance associated with the editor
     **/
    public ValidationEditor(SibType instance)
    {   super(instance); }
    
    /** method that return the validation
     *  create it if it is not initialized
     *  @return a JPanel
     */
    protected JPanel getValidationPanel()
    {   if ( this.validationPanel == null )
        {   FormLayout layout = new FormLayout("fill:pref:grow, pref, " + this.buttonInterval + "px, pref, " +
                                "fill:pref:grow", "pref");
            layout.setColumnGroups(new int[][]{{1, 5}});
            this.validationPanel = new JPanel(layout);

            this.validationButton = new JButton("Valider");
            this.cancelButton     = new JButton("Annuler");
            
            this.listener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    boolean quit = false;
		    
                    if ( e.getSource() == validationButton )
                    {   if ( isModified() )
                        {   save(); }
                        
                        quit = true;
                    }
                    else if ( e.getSource() == cancelButton )
                    {   if ( isModified() )
                        {   cancel(); }
                        quit = true;
                    }
                    
                    if ( quit )
                    {   
                        try
                        {
                            checkCloseAllowed();
                            
                            aboutToBeClosed();
                            
                            close();
                        }
                        catch(ClosingRefusedException ex)
                        {   /** indicate to the editor that it could not be closed */
                            closingRefused();

                            /** provide messages to the interface */
                            JOptionPane.showMessageDialog(SwingUtilities.getRoot(getComponent()), ex.getMessage(),
                                                          "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            };
            
            /** add actionlistener */
            this.getValidationButton().addActionListener(this.listener);
            this.getCancelButton().addActionListener(this.listener);

            /* add buttons to the validation panel */
            CellConstraints cc = new CellConstraints();

            this.validationPanel.add(this.validationButton, cc.xy(2, 1));
            this.validationPanel.add(this.cancelButton, cc.xy(4, 1));
        }
        
        return this.validationPanel;
    }
    
    /** return the validation button
     *  @return a JButton or null if the validation panel is not initialized
     */
    public JButton getValidationButton()
    {   return this.validationButton; }
    
    /** return the cancel button
     *  @return a JButton or null if the validation panel is not initialized
     */
    public JButton getCancelButton()
    {   return this.cancelButton; }
    
    /** return the interval in pixel between buttons
     *  @return a positive integer
     */
    public int getButtonsInterval()
    {   return this.buttonInterval; }
    
    /** set the interval in pixels between button
     *  @param interval a positive integer
     */
    public void setButtonsInterval(int interval)
    {   if ( interval < 0 )
            throw new IllegalArgumentException("interval have to be positive");
        
        this.buttonInterval = interval;
        
        if ( this.validationPanel != null )
        {   FormLayout layout = (FormLayout)this.validationPanel.getLayout();
            layout.removeColumn(3);
            layout.insertColumn(3, new ColumnSpec(this.buttonInterval + "px"));

            this.validationPanel.revalidate();
        }
    }

    /** indicates if the validation panel is to be used
     *  @return true if the validation panel is to be used
     */
    public boolean useValidation()
    {   return useValidation; }

    /** tell if the validation panel is to be used
     *  @param useValidation true if the validation panel is to be used
     */
    public void setUseValidation(boolean useValidation)
    {   this.useValidation = useValidation; }
    
}
