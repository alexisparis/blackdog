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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author  alexis
 */
public class VolumeControlPanel extends javax.swing.JPanel
{
    /** property volume level 
     *	from 0 to 1000
     */
    public static final String PROPERTY_VOLUME_LEVEL = "volume-level";
    
    /* icon volume */
    private Icon               volumeIcon           = null;
    
    /* icon mute */
    private Icon               muteIcon             = null;
    
    /** action listener on the volume button */
    private ActionListener     volumeButtonListener = null;
    
    /** change listener on the slider */
    private ChangeListener     sliderChangeListener = null;
    
    /** mouse wheel listener */
    private MouseWheelListener volumeWheelListener  = null;
    
    /** volume level */
    private int                volumeLevel          = 500;
    
    /** Creates new form VolumeControlPanel */
    public VolumeControlPanel()
    {
	initComponents();
	
	this.volumeIcon = this.volumeButton.getIcon();
	
	this.muteIcon = new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/mute_16.png"));
	
	this.volumeButtonListener = new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		int value = 0;
		
		if ( ! volumeButton.isSelected() )
		{
		    value = volumeSlider.getValue();
		}
		setVolumeLevel(value, false, false, false);
		
		refreshStates(false, false, true);
	    }
	};
	
	this.volumeButton.addActionListener(this.volumeButtonListener);
	
	this.sliderChangeListener = new ChangeListener()
	{
	    public void stateChanged(ChangeEvent e)
	    {
		if ( ! volumeSlider.getValueIsAdjusting() )
		{
		    setVolumeLevel(volumeSlider.getValue(), false, false, false);
		    
		    if ( volumeButton.isSelected() )
		    {
			refreshStates(false, true, true);
		    }
		}
	    }
	};
	
	this.volumeWheelListener = new MouseWheelListener()
	{
	    public void mouseWheelMoved(MouseWheelEvent e)
	    {
		int value = volumeSlider.getValue() - (e.getUnitsToScroll() * 6);
		
		setVolumeLevel(value);
		
		
		
//		System.out.println("e units to scroll : " + e.getUnitsToScroll());
	    }
	};
	
	this.volumeSlider.setValue(this.getVolumeLevel());
	
	this.volumeSlider.addMouseWheelListener(this.volumeWheelListener);
	this.volumeSlider.addChangeListener(this.sliderChangeListener);
    }
    
    /** return the volume gain in [0, 1]
     *	@return a double
     */
    public double getVolumeGain()
    {
	return ((double)this.getVolumeLevel()) / ((double)this.volumeSlider.getMaximum());
    }
    
    /** provoke mute */
    public void mute()
    {
	this.setMute(true);
    }
    
    /** provoke unmute */
    public void unmute()
    {
	this.setMute(false);
    }
    
    /** provoke mute change
     *	@param mute true to mute
     */
    public void setMute(boolean mute)
    {
	if ( this.volumeButton.isSelected() != mute )
	{
	    this.volumeButton.doClick();
	}
    }
    
    /** dispose the panel */
    private void dispose()
    {
	if ( this.volumeButtonListener != null )
	{
	    if ( this.volumeButton != null )
	    {
		this.volumeButton.removeActionListener(this.volumeButtonListener);
	    }
	    this.volumeButtonListener = null;
	}
	
	if ( this.sliderChangeListener != null )
	{
	    if ( this.volumeSlider != null )
	    {
		this.volumeSlider.removeChangeListener(this.sliderChangeListener);
	    }
	    this.sliderChangeListener = null;
	}
	
	if ( this.volumeWheelListener != null )
	{
	    if ( this.volumeSlider != null )
	    {
		this.volumeSlider.removeMouseWheelListener(this.volumeWheelListener);
	    }
	    this.volumeWheelListener = null;
	}
	
	this.volumeIcon = null;
	this.muteIcon   = null;
    }
    
    /** return the volume level
     *	@return an integer from 0 to 1000
     */
    public int getVolumeLevel()
    {
	return this.volumeLevel;
    }
    
    /** initialize the volume level
     *	@param level an integer from 0 to 1000
     */
    public void setVolumeLevel(int level)
    {
	this.setVolumeLevel(level, true, true, true);
    }
    
    /** initialize the volume level
     *	@param level an integer from 0 to 1000
     *	@param refreshSlider true to force the refresh of the slider
     *	@param refreshButtonSelection true to force the refresh of the button selected state
     *	@param refreshButtonIcon true to force the refresh of the icon button
     */
    private void setVolumeLevel(int level, boolean refreshSlider, boolean refreshButtonSelection, boolean refreshButtonIcon)
    {
	int _level = level;
	
	if ( _level < this.volumeSlider.getMinimum() )
	{
	    _level = this.volumeSlider.getMinimum();
	}
	if ( _level > this.volumeSlider.getMaximum())
	{
	    _level = this.volumeSlider.getMaximum();
	}
	
	if ( this.getVolumeLevel() != _level )
	{
	    int oldLevel = this.getVolumeLevel();
	    
	    this.volumeLevel = _level;
	    
	    this.firePropertyChange(PROPERTY_VOLUME_LEVEL, oldLevel, this.getVolumeLevel());
	    
	    if ( refreshSlider || refreshButtonSelection || refreshButtonIcon )
	    {
		this.refreshStates(refreshSlider, refreshButtonSelection, refreshButtonIcon);
	    }
	}
    }
    
    /** refresh the panel state according to the volume level
     *	@param refreshSlider true to force the refresh of the slider
     *	@param refreshButtonSelection true to force the refresh of the button selected state
     *	@param refreshButtonIcon true to force the refresh of the icon button
     */
    private void refreshStates(final boolean refreshSlider, final boolean refreshButtonSelection, final boolean refreshButtonIcon)
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		int _level = getVolumeLevel();
	
		if ( refreshSlider )
		{
		    volumeSlider.setValue(_level);
		}
		
		if ( refreshButtonSelection )
		{
		    volumeButton.setSelected( _level == 0 );
		}
		
		if ( refreshButtonIcon )
		{
		    volumeButton.setIcon( volumeButton.isSelected() ? muteIcon : volumeIcon );
		}
	    }
	};
	
	if ( SwingUtilities.isEventDispatchThread() )
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
        volumeButton = new javax.swing.JToggleButton();
        volumeSlider = new javax.swing.JSlider();

        volumeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/blackdog/rc/img/volume_16.png")));
        volumeButton.setBorderPainted(false);
        volumeButton.setContentAreaFilled(false);
        volumeButton.setFocusPainted(false);
        volumeButton.setFocusable(false);

        volumeSlider.setMaximum(1000);
        volumeSlider.setAlignmentX(0.0F);
        volumeSlider.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        volumeSlider.setMaximumSize(new java.awt.Dimension(300, 16));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(volumeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(volumeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
            .addComponent(volumeButton)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton volumeButton;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables
    
}
