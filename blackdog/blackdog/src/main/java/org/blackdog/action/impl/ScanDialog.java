/*
 * blackdog : audio player / manager
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.action.impl;

import java.util.ResourceBundle;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * Dialog that represent the state of the scan
 *
 * @author  alexis
 */
public class ScanDialog extends javax.swing.JDialog
{
    /** ratio pattern */
    private String ratioLabelPattern = null;
    
    /** total file count */
    private int    totalFileCount    = 0;
    
    /** current file processed count */
    private int    processedCount    = 0;
    
    /** Creates new form ScanDialog */
    public ScanDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        this.setLocationRelativeTo(parent);
        
        ResourceBundle rb = ResourceBundle.getBundle(ScanDialog.class.getName());
        
        this.ratioLabelPattern = rb.getString("ratioLabelPattern");
        
        this.directoryLabel.setText(rb.getString("directoryLabel"));
        
        this.setTitle( rb.getString("scanDialogTitle") );
	
	this.directoryNameLabel.setText("");
	this.ratioLabel.setText("");
    }
    
    /** set the name of the current directory being processed
     *  @param path the file path of the current directory being processed
     */
    public void setCurrentDirectoryPath(String path)
    {   this.directoryNameLabel.setText(path); }
    
    /** set the total file count
     *  @param count an integer
     */
    public void setTotalFileCount(int count)
    {   
        if ( this.totalFileCount != count )
        {   
            this.totalFileCount = count;
            
            this.updateRatioLabel();
        }
    }
    
    /** return the label that print the current directory name
     *  @return a JLabel
     */
    public JLabel getDirectoryLabel()
    {   return this.directoryNameLabel; }
    
    /** set the number of file which has been registered by the scan process
     *  @param count the number of file which has been registered by the scan process
     */
    public void setFileProcessedCount(int count)
    {   if ( count != this.processedCount )
        {
            this.processedCount = count;
            
            this.updateRatioLabel();
        }
    }
    
    /** m??thode that update the text of the ratio label and the state of the progress bar */
    private void updateRatioLabel()
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		if ( ratioLabelPattern != null )
		{
		    int realProcessedFileCount = Math.min(processedCount, totalFileCount);

		    String s = ratioLabelPattern.replace("{fileCountProcessed}", Integer.toString(realProcessedFileCount));
		    s        = s.replace("{fileCountTotal}", Integer.toString(totalFileCount));

		    ratioLabel.setText(s);

		    double ratio = ((double)realProcessedFileCount) / ((double)totalFileCount);

		    progressBar.setValue( (int)(ratio * progressBar.getMaximum()) );
		}
	    }
	};
	
	if( SwingUtilities.isEventDispatchThread() )
	{
	    runnable.run();
	}
	else
	{
	    SwingUtilities.invokeLater(runnable);
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
        progressBar = new javax.swing.JProgressBar();
        directoryLabel = new javax.swing.JLabel();
        ratioLabel = new javax.swing.JLabel();
        directoryNameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Scan");
        progressBar.setMaximum(1000);

        directoryLabel.setText("Directory : ");

        ratioLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        ratioLabel.setText("ratio");

        directoryNameLabel.setText("dir");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ratioLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(directoryLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directoryNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(directoryLabel)
                    .addComponent(directoryNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ratioLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                new ScanDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel directoryLabel;
    private javax.swing.JLabel directoryNameLabel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel ratioLabel;
    // End of variables declaration//GEN-END:variables
    
}
