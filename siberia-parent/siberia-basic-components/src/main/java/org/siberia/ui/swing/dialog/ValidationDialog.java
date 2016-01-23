/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.dialog;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.Sizes;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * Dialog with validation and annulation habilities
 *
 * @author alexis
 */
public abstract class ValidationDialog extends AbstractDialog
                                       implements WindowListener
{
    /** panel which is the parent of the buttons **/
    private JPanel validAnnul;
    
    /*** panel for the content **/
    private JPanel content;
    
    /** validation button **/
    private JButton valid;
    
    /** cancel button **/
    private JButton annule;
    
    /** action listener **/
    private ValidationPanelListener listener;
    
    /** answer of the dialog */
    private int answer = JOptionPane.CANCEL_OPTION;
    
    /** Creates a new instance of ValidationDialog */
    public ValidationDialog(Window frame, String title)
    {   super(frame, title);
        
        this.addWindowListener(this);
        
        this.validAnnul = new JPanel();
        
        this.listener = new ValidationPanelListener();
        
        this.valid  = new JButton("OK");
        this.annule = new JButton("cancel");
        this.valid.addActionListener(this.listener);
        this.annule.addActionListener(this.listener);
	
	this.valid.setFocusable(true);
        
        this.content = new JPanel();
        
        this.getPanel().setLayout(new FormLayout("pref", "pref, pref"));
        
        CellConstraints cc = new CellConstraints();
        this.getPanel().add(this.content, cc.xy(1, 1));
    }
    
    /** return the answer of the dialog box
     *	@return JOptionPane.CANCEL_OPTION or JOptionPane.OK_OPTION or JOptionPane.CLOSED_OPTION
     */
    public int getAnswer()
    {
	return this.answer;
    }
    
    /** set the answer of the dialog box
     *	@param answer JOptionPane.CANCEL_OPTION or JOptionPane.OK_OPTION or JOptionPane.CLOSED_OPTION
     */
    public void setAnswer(int answer)
    {
	
	if ( answer != JOptionPane.OK_OPTION && answer != JOptionPane.CANCEL_OPTION && answer != JOptionPane.CLOSED_OPTION )
	{
	    throw new IllegalArgumentException("invalid answer " + answer);
	}
	
//	if ( answer == JOptionPane.OK_OPTION )
//	{
//	    new Exception("setting ok for dialog " + this).printStackTrace();
//	}
//	else if ( answer == JOptionPane.CANCEL_OPTION )
//	{
//	    new Exception("setting cancel for dialog " + this).printStackTrace();
//	}
//	if ( answer == JOptionPane.CLOSED_OPTION )
//	{
//	    new Exception("setting closed for dialog " + this).printStackTrace();
//	}
	
	this.answer = answer;
    }

    public void setVisible(boolean b)
    {
	boolean wasVisible = this.isVisible();
	
	if ( b && ! wasVisible )
	{
	    this.setAnswer(JOptionPane.CANCEL_OPTION);
	}
	
	super.setVisible(b);
    }
    
    /** build the validation panel according to the size of the content panel **/
    private void buildValidationPanel()
    {   FormLayout layout = new FormLayout("", "15dlu");
        
        int maxButton = Math.max(this.valid.getPreferredSize().width, this.annule.getPreferredSize().width);
        
        int sizeColumn = (int)(this.getContentPanel().getPreferredSize().width -
                                2 * maxButton) / 3;
        
        ColumnSpec colSpace = new ColumnSpec(Sizes.pixel(sizeColumn));
        ColumnSpec colButton = new ColumnSpec(Sizes.pixel(maxButton));
        layout.appendColumn(colSpace);
        layout.appendColumn(colButton);
        layout.appendColumn(colSpace);
        layout.appendColumn(colButton);
        layout.appendColumn(colSpace);
        
        this.validAnnul.setLayout(layout);
        
        CellConstraints cc = new CellConstraints();
        this.validAnnul.add(this.valid, cc.xy(2, 1, cc.FILL, cc.FILL));
        this.validAnnul.add(this.annule, cc.xy(4, 1, cc.FILL, cc.FILL));
        this.getPanel().add(this.validAnnul, cc.xy(1, 2));
    }
    
    /** return the panel which will contain the main graphical objects of the dialog window
     *  that means without the validation buttons
     *  @return a JPanel
     **/
    public JPanel getContentPanel()
    {   return this.content; }
    
    /** display the dialog on the window **/
    public void display()
    {   /* creation of the validation panel */
        this.buildValidationPanel();
        
        super.display();
    }
    
    /* method that tell if valid can be performed
     *	@return true if valid can be performed without error
     */
    public boolean couldValide()
    {   return true; }
    
    /* method to process the valid action */
    public void valid()
    {   /* do nothing */ }
    
    /* method to process the cancel action */
    public void cancel()
    {   /* do nothing */ }
    
     public void windowActivated(WindowEvent e)
     {  }
     
    public void	windowClosed(WindowEvent e)
    {   }
    
    public void windowClosing(WindowEvent e)
    {   
	this.setAnswer(JOptionPane.CLOSED_OPTION);
	this.cancel();
    }
    
    public void windowDeactivated(WindowEvent e)
    {   }
    
    public void	windowDeiconified(WindowEvent e)
    {   }
    
    public void	windowIconified(WindowEvent e)
    {   }
    
    public void	windowOpened(WindowEvent e)
    {   }
    
    /** private inner class to care about event on button **/
    private class ValidationPanelListener implements ActionListener
    {
        /** catch event on validation buttons **/
        public void actionPerformed(ActionEvent e)
        {   if (e.getSource() == ValidationDialog.this.valid)
            {   
		if ( couldValide() )
		{
		    setAnswer(JOptionPane.OK_OPTION);
		    ValidationDialog.this.valid();
		    ValidationDialog.this.setVisible(false);
		    ValidationDialog.this.dispose();
		}
            }
            else if (e.getSource() == ValidationDialog.this.annule)
            {   
		setAnswer(JOptionPane.CANCEL_OPTION);
		ValidationDialog.this.cancel();        
                ValidationDialog.this.setVisible(false);
		ValidationDialog.this.dispose();
            }
        }
    }
}
