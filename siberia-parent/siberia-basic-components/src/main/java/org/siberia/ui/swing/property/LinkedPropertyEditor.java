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
package org.siberia.ui.swing.property;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * Property renderer that contains a button
 *  to indicate that a dialog or something else exists to perform some changes on a property
 *
 * @author alexis
 */
public abstract class LinkedPropertyEditor<U extends LinkedPropertyRenderer> extends AbstractPropertyEditor
{
    /** inner Linked property renderer */
    private U         renderer  = null;
    
    /** Component representing the custom editor */
    private Component component = null;
    
    /** Creates a new instance of LinkedPropertyRenderer
     *	@param renderer a LinkedPropertyRenderer
     */
    protected LinkedPropertyEditor(U renderer)
    {
	this.renderer = renderer;
	
	/** create component */
	this.component = this.renderer.getTableCellRendererComponent(null, null, false, false, 0, 0);
	
	this.renderer.getLinkButton().addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		linkActionPerformed(e);
	    }
	});
	this.renderer.getLinkButton().setVisible(true);
    }

    @Override
    public Component getCustomEditor()
    {
	return this.component;
    }
    
    /** return the renderer used 
     *	@return a LinkedPropertyRenderer
     */
    protected U getRenderer()
    {
	return this.renderer;
    }
    
    /** method called when the button is pressed
     *	@param event an ActionEvent representing the event that occured on the link button
     */
    protected abstract void linkActionPerformed(ActionEvent event);
    
    
}
