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
package org.blackdog.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.blackdog.type.base.RepeatMode;

/**
 *
 * @author  alexis
 */
public class AudioReaderPanel extends javax.swing.JPanel
{
    
    /** Creates new form PlayerPanel */
    public AudioReaderPanel()
    {
        initComponents();
        
        ResourceBundle rb = ResourceBundle.getBundle(AudioReaderPanel.class.getName());
        
        if ( rb != null )
        {   this.previousButton.setToolTipText(rb.getString("previousButton.tooltip"));
            this.playButton.setToolTipText(rb.getString("playButton.tooltip"));
            this.pauseButton.setToolTipText(rb.getString("pauseButton.tooltip"));
            this.stopButton.setToolTipText(rb.getString("stopButton.tooltip"));
            this.nextButton.setToolTipText(rb.getString("nextButton.tooltip"));
            
            ComboBoxModel model = new EnumComboBoxModel(RepeatMode.class);
            this.repeatCombo.setModel(model);
            this.repeatCombo.setRenderer(new EnumListCellRenderer());
	    this.repeatCombo.setSelectedItem(RepeatMode.ALL);
            
            this.shuffleCheckbox.setText(rb.getString("shuffle.label"));
            
            this.songLabel.setText("");
            this.timeLabel.setText("");
            
            this.songSlider.setValue(0);
        }
        
        /** add listener */
        ActionListener listener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if ( e.getSource() == stopButton )
                {   playButton.setSelected(false);
                    pauseButton.setSelected(false);
                }
                else if ( e.getSource() instanceof JToggleButton )
                {   
		    boolean selected = ((JToggleButton)e.getSource()).isSelected();
                    JToggleButton other = null;
                    
                    if ( e.getSource() == playButton )
                    {   
			if ( selected )
			{
			    other = pauseButton;
			}
		    }
                    else if ( e.getSource() == pauseButton )
                    {   other = playButton; }
                    
                    if ( other != null )
                    {   other.setSelected( ! selected ); }
                }
            }
        };
        
        this.stopButton.addActionListener(listener);
        this.playButton.addActionListener(listener);
        this.pauseButton.addActionListener(listener);
    }
    
    /** returns the song label
     *	@return a JLabel
     */
    public JLabel getSongLabel()
    {
	return this.songLabel;
    }
    
    /** returns the time label
     *	@return a JLabel
     */
    public JLabel getTimeLabel()
    {
	return this.timeLabel;
    }
    
    /** return the seleted repeat mode 
     *	@return a RepeatMode
     */
    public RepeatMode getSelectedRepeatMode()
    {
	RepeatMode mode = null;
	
	if ( this.getRepeatModeCombo() != null )
	{
	    Object o = this.getRepeatModeCombo().getSelectedItem();
	    
	    if ( o instanceof RepeatMode )
	    {
		mode = (RepeatMode)o;
	    }
	}
	
	if ( mode == null )
	{
	    mode = RepeatMode.NONE;
	}
	
	return mode;
    }
    
    /** return the VolumeControlPanel
     *	@param a VolumeControlPanel
     */
    public VolumeControlPanel getVolumeControlPanel()
    {
	return this.volumeControl;
    }
    
    /** return the song slider
     *  @return a JSlider
     */
    public JSlider getSongSlider()
    {
	return this.songSlider;
    }
    
    /** return the checkbox shuffle
     *  @return a JCheckBox
     */
    public JCheckBox getShuffleCheckBox()
    {
        return this.shuffleCheckbox;
    }
    
    /** return the combo box that allow to choose the repeat mode
     *  @return a JComboBox
     */
    public JComboBox getRepeatModeCombo()
    {
        return this.repeatCombo;
    }
    
    /** return the play button
     *  @return the play button
     */
    public JToggleButton getPlayButton()
    {   return this.playButton; }
    
    /** return the pause button
     *  @return the pause button
     */
    public JToggleButton getPauseButton()
    {   return this.pauseButton; }
    
    /** return the stop button
     *  @return the stop button
     */
    public JButton getStopButton()
    {   return this.stopButton; }
    
    /** return the previous button
     *  @return the previous button
     */
    public JButton getPreviousButton()
    {   return this.previousButton; }
    
    /** return the next button
     *  @return the next button
     */
    public JButton getNextButton()
    {   return this.nextButton; }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        songSlider = new javax.swing.JSlider();
        previousButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        repeatCombo = new javax.swing.JComboBox();
        shuffleCheckbox = new javax.swing.JCheckBox();
        playButton = new javax.swing.JToggleButton();
        pauseButton = new javax.swing.JToggleButton();
        timeLabel = new javax.swing.JLabel();
        volumeControl = new org.blackdog.ui.VolumeControlPanel();
        songLabel = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(800, 500));
        setMinimumSize(new java.awt.Dimension(345, 130));
        setPreferredSize(new java.awt.Dimension(345, 130));
        songSlider.setMaximum(1000);
        songSlider.setMaximumSize(null);
        songSlider.setMinimumSize(null);
        songSlider.setPreferredSize(null);

        previousButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/previous.png")));
        previousButton.setBorderPainted(false);
        previousButton.setFocusPainted(false);
        previousButton.setFocusable(false);
        previousButton.setRequestFocusEnabled(false);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/stop.png")));
        stopButton.setBorderPainted(false);
        stopButton.setFocusPainted(false);
        stopButton.setFocusable(false);
        stopButton.setRequestFocusEnabled(false);

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/next.png")));
        nextButton.setBorderPainted(false);
        nextButton.setFocusPainted(false);
        nextButton.setFocusable(false);
        nextButton.setRequestFocusEnabled(false);

        repeatCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        shuffleCheckbox.setText("jCheckBox2");
        shuffleCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        shuffleCheckbox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        shuffleCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(repeatCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 114, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shuffleCheckbox))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(repeatCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(shuffleCheckbox)
                .addContainerGap())
        );

        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/play.png")));
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        playButton.setFocusable(false);
        playButton.setRequestFocusEnabled(false);

        pauseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/pause.png")));
        pauseButton.setBorderPainted(false);
        pauseButton.setFocusPainted(false);
        pauseButton.setFocusable(false);
        pauseButton.setRequestFocusEnabled(false);

        timeLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        timeLabel.setText("2:45 sur 5:30");

        songLabel.setBackground(new java.awt.Color(102, 255, 102));
        songLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        songLabel.setText("toto cotugnio - la mama");
        songLabel.setAutoscrolls(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(songSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(previousButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(volumeControl, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(songLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(previousButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nextButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stopButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pauseButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(playButton, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(volumeControl, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4)
                .addComponent(songLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(songSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton nextButton;
    private javax.swing.JToggleButton pauseButton;
    private javax.swing.JToggleButton playButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JComboBox repeatCombo;
    private javax.swing.JCheckBox shuffleCheckbox;
    private javax.swing.JLabel songLabel;
    private javax.swing.JSlider songSlider;
    private javax.swing.JButton stopButton;
    private javax.swing.JLabel timeLabel;
    private org.blackdog.ui.VolumeControlPanel volumeControl;
    // End of variables declaration//GEN-END:variables
    
}
