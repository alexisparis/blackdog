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
package org.siberia.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.FocusManager;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 *
 * General text component verifier
 *
 * @author alexis
 */
public abstract class TextFieldVerifier extends InputVerifier implements ActionListener,
                                                                             KeyListener,
                                                                             FocusListener
{
    
    /** selection color */
    private Color   selectionColor      = null;
    
    /** background color */
    private Color   backgroundColor     = null;
    
    /** current valid value */
    private String  lastValidValue      = null;
    
    /** indicate if the component currently checked lost focus temporarly */
    private boolean focusLostTemporarly = false;
    
    /** return true if the component's value is considered as correct
     *  @param input a JComponent
     *  @return true if the component's value is considered as correct
     */
    public abstract boolean verify(JComponent input);
    
    /** apply the value
     *  @param input a JComponent
     *  @param value an Object
     *  @return true if the value has been applied
     */
    public abstract boolean applyValue(Component input, Object value);
    
    /** apply this verifier to a JTextField
     *  @param textComponent a JTextField
     */
    public void applyTo(JTextField field)
    {   if ( field != null )
        {   field.setInputVerifier(this);

            field.addActionListener(this);
            field.addKeyListener(this);
            field.addFocusListener(this);
        }
    }
    
    /** called when user would like the initial value to be restored
     *  @param e a KeyEvent
     */
    public void escape(KeyEvent e)
    {   if ( e.getSource() instanceof JTextComponent )
        {   ((JTextComponent)e.getSource()).setText( this.lastValidValue == null ? "" : this.lastValidValue ); }
    }

    public boolean shouldYieldFocus(JComponent input)
    {   boolean ok = verify(input);

        if ( input instanceof JTextComponent )
        {   if ( this.selectionColor == null )
                this.selectionColor = ((JTextComponent)input).getSelectionColor();
            if ( this.backgroundColor == null )
                this.backgroundColor = ((JTextComponent)input).getBackground();
            
            ((JTextComponent)input).setBackground(ok ? this.backgroundColor : Color.RED);
//            ((JTextComponent)input).setSelectionColor(ok ? this.selectionColor : Color.RED);
            
            /* plus de s�lection */
            ((JTextComponent)input).setSelectionStart(0);
            int endPos = 0;
            if ( ! ok )
                endPos = ((JTextComponent)input).getDocument().getLength();
            ((JTextComponent)input).setSelectionEnd(endPos);
        }

        return ok;
    }
    
    public void actionPerformed(ActionEvent e)
    {   if ( e.getSource() instanceof JTextComponent )
        {   JTextComponent source = (JTextComponent)e.getSource();
            shouldYieldFocus(source);
            source.selectAll();
        }
    }
    
    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key typed event.
     */
    public void keyTyped(KeyEvent e)
    {   }

    /**
     * Invoked when a key has been pressed. 
     * See the class description for {@link KeyEvent} for a definition of 
     * a key pressed event.
     */
    public void keyPressed(KeyEvent e)
    {   //System.out.println("keyPressed " + e.getKeyCode());
        if ( e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER )
        {   if ( e.getKeyCode() == KeyEvent.VK_ESCAPE )
                this.escape(e);
            if ( e.getSource() instanceof JComponent )
            {   shouldYieldFocus((JComponent)e.getSource());
                if ( e.getSource() instanceof JTextComponent )
                    ((JTextComponent)e.getSource()).selectAll();
                FocusManager.getCurrentManager().focusNextComponent();
            }
        }
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of 
     * a key released event.
     */
    public void keyReleased(KeyEvent e)
    {   }
    
    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(FocusEvent e)
    {   
	if ( ! this.focusLostTemporarly )
	{
	    if ( e.getComponent() instanceof JTextComponent )
		this.lastValidValue = ((JTextComponent)e.getComponent()).getText();
	}
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(FocusEvent e)
    {   
	this.focusLostTemporarly = e.isTemporary();
	
//	System.out.println("lost focus temporarly ? " + this.focusLostTemporarly);
	
	if ( ! this.focusLostTemporarly )
	{
	    if ( e.getComponent() instanceof JTextComponent )
	    {   this.applyValue( ((JTextComponent)e.getComponent()),
				 ((JTextComponent)e.getComponent()).getText() );
	    }
	}
    }
    
}
