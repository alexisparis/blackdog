/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
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
package org.siberia.trans.ui.editor.impl.plugin;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * Dialog that ask user for license agreement
 *
 * @author  alexis
 */
public class LicenseConfirmationDialog extends javax.swing.JDialog implements ActionListener
{
    /** true if the license is accepted */
    private boolean isAccepted  = false;
    
    /** String used to compute the title of this frame */
    private String  titleFormat = null;
    
    /** Creates new form LicenseConfirmationDialog */
    public LicenseConfirmationDialog(Dialog dialog, boolean modal)
    {
	super(dialog, modal);
	initComponents();
	
	ResourceBundle rb = ResourceBundle.getBundle(LicenseConfirmationDialog.class.getName());
	
	this.licenseEditor.setEditable(false);
	this.licenseEditor.setEditorKit(new HTMLEditorKit());
	this.buttonAccept.setText(rb.getString("labelAcceptButton"));
	this.buttonDecline.setText(rb.getString("labelDeclineButton"));
	
	this.titleFormat = rb.getString("dialogTitleModel");
    }
    
    /** initialize the text of the license
     *	@param license a String in html format representing the license
     */
    public void setLicenseContent(String license)
    {
	this.licenseEditor.setText(license);
	
	System.out.println("document : " + this.licenseEditor.getDocument());
	
	try
	{   this.licenseEditor.getDocument().remove(this.licenseEditor.getDocument().getStartPosition().getOffset(), this.licenseEditor.getDocument().getLength());
	    
	    this.licenseEditor.setText(
		    "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><meta http-equiv=\"con" +
		    "tent-type\" content=\"text/html; charset=utf-8\" /><link rev=\"made\" href=\"mailto:webmasters@gnu.org\" /><lin" +
		    "k rel=\"icon\" type=\"image/png\" href=\"/graphics/gnu-head-mini.png\" /><meta name=\"ICBM\" content=\"42.256233,-71.006581\" " +
		    "/><meta name=\"DC.title\" content=\"gnu.org\" /><title>GNU General Public License - GNU Project - Free Software Foundation (FSF)</title>" +
		    "</html>");
	}
	catch (BadLocationException ex)
	{
	    ex.printStackTrace();
	}
    }
    
    /** set the name and version of the plugin we want the license agreement
     *	@param pluginName the name of the plugin
     *	@param pluginVersion the version of the plugin
     */
    public void setPluginContext(String pluginName, String pluginVersion)
    {
	StringBuffer buffer = new StringBuffer(this.titleFormat.length() + 50);
	buffer.append(this.titleFormat);
	
	int index = -1;
	String repItem = null;
	
	/* replace {plugin-name} with pluginName */
	repItem = "{plugin-name}";
	index = buffer.indexOf(repItem);
	
	if ( index != -1 )
	{
	    buffer.delete(index, index + repItem.length());
	    if ( pluginName != null )
	    {
		buffer.insert(index, pluginName);
	    }
	}
	
	/* replace {plugin-version} with pluginVersion */
	repItem = "{plugin-version}";
	index = buffer.indexOf(repItem);
	
	if ( index != -1 )
	{
	    buffer.delete(index, index + repItem.length());
	    if ( pluginVersion != null )
	    {
		buffer.insert(index, pluginVersion);
	    }
	}
	
	System.out.println("frame title : " + buffer.toString());
	this.setTitle(buffer.toString());
    }
    
    /** return true if the license is accepted 
     *	@return a boolean
     */
    public boolean isLicenseAccepted()
    {
	return this.isAccepted;
    }

    @Override
    public void setVisible(boolean b)
    {
	
	boolean visible = this.isVisible();
	
	if ( visible != b )
	{
	    if ( b )
	    {
		this.isAccepted = false;
		
		this.buttonAccept.addActionListener(this);
		this.buttonDecline.addActionListener(this);
	    }
	    else
	    {
		this.buttonAccept.removeActionListener(this);
		this.buttonDecline.removeActionListener(this);
	    }
	}
	
	super.setVisible(b);
    }
    
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	if ( e.getSource() == this.buttonAccept )
	{
	    this.isAccepted = true;
	    this.setVisible(false);
	}
	else if ( e.getSource() == this.buttonDecline )
	{
	    this.isAccepted = false;
	    this.setVisible(false);
	}
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jScrollPane1 = new javax.swing.JScrollPane();
        licenseEditor = new javax.swing.JEditorPane();
        buttonDecline = new javax.swing.JButton();
        buttonAccept = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jScrollPane1.setViewportView(licenseEditor);

        buttonDecline.setText("Decline");

        buttonAccept.setText("Accept");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(450, Short.MAX_VALUE)
                .addComponent(buttonAccept)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonDecline)
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonDecline)
                    .addComponent(buttonAccept))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
	java.awt.EventQueue.invokeLater(new Runnable()
	{
	    public void run()
	    {
		new LicenseConfirmationDialog(new javax.swing.JDialog(), true).setVisible(true);
	    }
	});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAccept;
    private javax.swing.JButton buttonDecline;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JEditorPane licenseEditor;
    // End of variables declaration//GEN-END:variables
    
}
