/* =============================================================================
 * Siberia launcher
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2008, by Alexis Paris.
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
package org.siberia.splash;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import org.java.plugin.util.ExtendedProperties;


/**
 * First version of a SplashHandler.
 *
 * the window created use an icon, a label to display message and finally, a progress bar.
 *
 * By putting some parameter into boot.properties, the aspect of the splash screen could be modify.<br>
 * parameters are :<br>
 * <ul>
 *    <li>org.java.plugin.boot.splash.enabled : <i>indicate if the splash screen is allowed to be visible (default is true)</i></li>
 *    <li>org.java.plugin.boot.splash.labelBackgroundColor : <i>specify the background color of the label (default is "WHITE")<br>
 *                                                              color has to be declared using HTML forms (#FF0012)</i></li>
 *    <li>org.java.plugin.boot.splash.labelForegroundColor : <i>specify the foreground color of the label (default is "BLACK")<br>
 *                                                              color has to be declared using HTML forms (#FF0012)</i></li>
 *    <li>org.java.plugin.boot.splash.labelOpaque : <i>tell if the label has to be opaque or not (default is false)</i></li>
 *    <li>org.java.plugin.boot.splash.progressBackgroundColor : <i>specify the background color of the progress bar (default is "WHITE")<br>
 *                                                              color has to be declared using HTML forms (#FF0012)</i></li>
 *    <li>org.java.plugin.boot.splash.progressForegroundColor : <i>specify the foreground color of the progress bar (default is "#101796")<br>
 *                                                              color has to be declared using HTML forms (#FF0012)</i></li>
 *    <li>org.java.plugin.boot.splash.progressBorder : <i>specify a border to use for the progress bar ('none', 'raised', 'lowered')</i></li>
 * </ul>
 *
 * @author alexis PARIS
 *
 */
@SuppressWarnings("serial")
public class ProgressionSplashScreen implements org.java.plugin.boot.SplashHandler
{ 
    /** kind of border */
    public static final String BORDER_NONE    = "none";
    public static final String BORDER_RAISED  = "raised";
    public static final String BORDER_LOWERED = "lowered";
    
    /** window */
    private JWindow      window      = null;
    
    /* icone label */
    private JLabel       iconLabel   = null;
    
    /* label */
    private JLabel       label       = null;
    
    /* progress bar */
    private JProgressBar progressBar = null;
    
    /** url of the icon used in the splash screen */
    private URL          iconUrl     = null;
    
    /** activated */
    private boolean      activated   = true;
    
    /** instance */
    public static ProgressionSplashScreen lastSplash = null;

    /** create a new ProgressionSplashScreen */
    public ProgressionSplashScreen()
    {   
        this.window = new JWindow(new Frame());
	
	lastSplash = this;
    }
    
    /* #########################################################################
     * ################### SplashHandler implementation ########################
     * ######################################################################### */

    /**
     * Sets new text caption and optionally display it on the screen.
     * 
     * @param value new text caption
     */
    public void setText(String value)
    {   if ( this.label != null )
	{
            this.label.setText(value);
	}
    }

    /**
     * Sets new image URL and optionally displays it on the splash screen.
     * 
     * @param value new image URL
     */
    public void setImage(URL value)
    {   this.iconUrl = value;
        
        if ( this.iconLabel != null )
	{
            this.iconLabel.setIcon( this.iconUrl == null ? null :  new ImageIcon(this.iconUrl) );
	}
    }

    /**
     * Sets boot progress value and optionally adjust visual progress bar
     * control. The value should be in [0; 1] interval.
     * 
     * @param value new progress value
     */
    public void setProgress(float value)
    {   if ( this.progressBar != null )
            this.progressBar.setValue( Math.min(1000, Math.max(0, (int)(value * 1000))) );
    }

    /**
     * Configures this handler instance. This method is called ones immediately
     * after handler instantiation.
     * 
     * @param config handler configuration data, here included all configuration
     *               parameters which name starts with
     *               <code>org.java.plugin.boot.splash.</code> prefix
     */
    public void configure(ExtendedProperties config)
    {   
        Color   fgLabel         = Color.BLACK;
        Color   bgLabel         = Color.WHITE;
        boolean labelOpaque     = false;
        
        Color   bgProgressColor = Color.WHITE;
        Color   fgProgressColor = Color.BLUE;
        
        String  progressBorder  = BORDER_RAISED;
        
        
        if ( config != null )
        {   Object o = null;
            
            o = config.getProperty("labelBackgroundColor");
            if ( o instanceof String )
            {   try
                {   bgLabel = Color.decode( (String)o ); }
                catch(NumberFormatException e)
                {   e.printStackTrace(); }
            }
            
            o = config.getProperty("labelForegroundColor");
            if ( o instanceof String )
            {   try
                {   fgLabel = Color.decode( (String)o ); }
                catch(NumberFormatException e)
                {   e.printStackTrace(); }
            }
            
            o = config.getProperty("labelOpaque");
            if ( o instanceof String )
            {   labelOpaque = Boolean.parseBoolean( (String)o ); }
            
            o = config.getProperty("progressBackgroundColor");
            if ( o instanceof String )
            {   try
                {   bgProgressColor = Color.decode( (String)o ); }
                catch(NumberFormatException e)
                {   e.printStackTrace(); }
            }
            
            o = config.getProperty("progressForegroundColor");
            if ( o instanceof String )
            {   try
                {   fgProgressColor = Color.decode( (String)o ); }
                catch(NumberFormatException e)
                {   e.printStackTrace(); }
            }
            
            o = config.getProperty("progressBorder");
            if ( o instanceof String )
            {   progressBorder = (String)o; }
            
            o = config.getProperty("enabled");
            if ( o instanceof String )
            {   this.activated = Boolean.parseBoolean( (String)o ); }
        }
        
        /** build graphical items */
        
        this.window.getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 1;
        cc.gridy = 1;
	cc.gridheight = 2;
	cc.gridwidth = 1;
	cc.fill = cc.BOTH;
        cc.weightx = 1.0d;
        cc.weighty = 1.0d;
        
        this.iconLabel = new JLabel();
        if ( this.iconUrl != null )
            this.iconLabel.setIcon(new ImageIcon(this.iconUrl));
        this.window.getContentPane().add(this.iconLabel, cc);
            
        this.label = new JLabel("\u00A0");
        this.label.setOpaque(labelOpaque);

        label.setForeground(fgLabel);
        label.setBackground(bgLabel);

        this.progressBar = new JProgressBar(0, 1000);
        this.progressBar.setBackground(bgProgressColor);
        this.progressBar.setForeground(fgProgressColor);

        if ( progressBorder.equalsIgnoreCase(BORDER_RAISED) )
        {   this.progressBar.setBorder(BorderFactory.createRaisedBevelBorder()); }
        else if ( progressBorder.equalsIgnoreCase(BORDER_LOWERED) )
        {   this.progressBar.setBorder(BorderFactory.createLoweredBevelBorder()); }
        else
        {   this.progressBar.setBorder(null); }

        cc = new GridBagConstraints();
        cc.gridx = 1;
        cc.gridy = 2;
	cc.gridheight = 1;
	cc.gridwidth = 1;
        cc.weightx = 1.0d;
        cc.weighty = 0.0d;
        cc.anchor  = cc.LAST_LINE_END;
        cc.fill    = cc.HORIZONTAL;
	
	JPanel panel = new JPanel(new GridLayout(2, 1));
	panel.setOpaque(false);
	
        panel.add(this.label);
	panel.add(this.progressBar);
	
	javax.swing.border.EmptyBorder border = new javax.swing.border.EmptyBorder(0, 4, 4, 4);
	
	panel.setBorder(border);
	
        this.window.getContentPane().add(panel, cc);
	
	this.window.getContentPane().setComponentZOrder(panel, 0);
	this.window.getContentPane().setComponentZOrder(this.iconLabel, 1);
        
        /* first message is ... */
        ResourceBundle rb = ResourceBundle.getBundle("org.siberia.rc.misc", Locale.getDefault());
        this.setText(rb.getString("loadModuleMessage"));
    }

    /**
     * 
     * 
     * @return current text caption
     */
    public String getText()
    {   String text = null;
        if ( this.label != null )
            text = this.label.getText();
        return text;
    }

    /**
     * 
     * 
     * @return boot progress value that is normalized to [0; 1] interval
     */
    public float getProgress()
    {   int value = this.progressBar.getValue();
        if ( value == 0 )
            return value;
        else
            return value / 1000;
    }

    /**
     * Useful method to get access to handler internals. The actually returned
     * object depends on handler implementation.
     * 
     * @return original implementation of this handler, usually you return
     *         <code>this</code> (useful for handler wrappers)
     */
    public Object getImplementation()
    {   return this; }

    /**
     * 
     * 
     * @return current image URL
     */
    public URL getImage()
    {   return this.iconUrl; }

    /**
     * Shows/hides splash screen.
     * 
     * @param value <code>true</code> to show splash screen, <code>false</code>
     *              - to hide and dispose it
     */
    public void setVisible(boolean value)
    {	
        if ( this.activated )
        {   if ( value )
            {   this.window.pack(); 
                // centre le splash screen
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                this.window.setLocation(screenSize.width / 2 - (this.window.getSize().width / 2),
                        screenSize.height / 2 - (this.window.getSize().height / 2)); 
            }

            this.window.setVisible(value);
        }
        else
        {   if ( this.isVisible() )
            {   this.window.setVisible(false); }
        }
    }

    /**
     * 
     * 
     * @return <code>true</code> if splash screen is displayed now
     */
    public boolean isVisible()
    {   return this.window.isVisible(); }
}
 