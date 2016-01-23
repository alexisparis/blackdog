/*
 * VolumeControlPanelTest.java
 *
 * Created on 22 février 2008, 21:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import org.blackdog.ui.VolumeControlPanel;

/**
 *
 * test the VolumeControlPanel component
 *
 * @author alexis
 */
public class VolumeControlPanelTest
{
    
    /** Creates a new instance of VolumeControlPanelTest */
    public VolumeControlPanelTest()
    {
    }
    
    public static void main(String[] args) throws Exception
    {
	final VolumeControlPanel volumePanel = new VolumeControlPanel();
	
	volumePanel.addPropertyChangeListener(VolumeControlPanel.PROPERTY_VOLUME_LEVEL, new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		System.out.println("volume level : " + evt.getNewValue());
	    }
	});
	
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	JPanel panel = new JPanel();
	
	panel.add(volumePanel);
	
	final JTextField field = new JTextField(10);
	
	panel.add(field);
	
	JButton buttonApply = new JButton("apply");
	buttonApply.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		try
		{
		    int level = Integer.parseInt(field.getText());
		    System.out.println("setting level : " + level);
		    volumePanel.setVolumeLevel( level );
		}
		catch(Exception ec)
		{
		    ec.printStackTrace();
		}
	    }
	});
	
	panel.add(buttonApply);
	
	JButton mutebutton = new JButton("mute");
	mutebutton.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		volumePanel.mute();
		System.out.println("mute...");
	    }
	});
	panel.add(mutebutton);
	
	JButton unmutebutton = new JButton("unmute");
	unmutebutton.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		volumePanel.unmute();
		System.out.println("unmute...");
	    }
	});
	panel.add(unmutebutton);
	
	frame.getContentPane().add(panel);
	
	frame.setSize(400, 400);
	frame.setVisible(true);
	
    }
    
}
