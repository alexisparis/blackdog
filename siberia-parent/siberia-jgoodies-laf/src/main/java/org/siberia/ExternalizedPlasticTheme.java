/* 
 * Siberia jgoodies laf : siberia plugin defining jgoodies look and feels
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
package org.siberia;

import com.jgoodies.plaf.plastic.PlasticTheme;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Alexis
 */
public class ExternalizedPlasticTheme extends PlasticTheme
{
    /** map liant code et ColorUIResource */
    private Map<String, ColorUIResource> colorResources = null;
    
    /** map liant code et FontUIResource */
    private Map<String, FontUIResource>  fontResources  = null;
    
    /** ColorUIResource par défaut */
    private ColorUIResource              defaultColorRc = null;
    
    /** FontUIResource par défaut */
    private FontUIResource               defaultFontRc  = null;
    
    /** font 0 */
    private Font                         fontZero       = null;
    
    /** name of the theme */
    private String                       name           = null;
    
    /** Creates a new instance of BlackdogTheme
     *	@param themeName the name of the theme
     *	@param themeProperties an InputStream that contains the theme caracteristics
     */
    public ExternalizedPlasticTheme(String themeName, InputStream themeProperties)
    {
	this.name = themeName;
	
        Properties properties = new Properties();
        
        try
        {   properties.load(themeProperties); }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        
        this.initResources(properties);
        this.initColorResources(properties);
        this.initFontResources(properties);
    }

    public String getName()
    {   return this.name; }
    
    /** initialise les resources g�n�rales
     *  @param properties a Properties where resources caracteristics are stored
     */
    private void initResources(Properties properties)
    {
        if ( properties != null )
        {
            String value = null;
            
            value = properties.getProperty("font.font0");
            if ( value != null && value.trim().length() > 0 )
            {
                this.fontZero = Font.decode(value);
            }
            
            value = properties.getProperty("font.default");
            if ( value != null && value.trim().length() > 0 )
            {
                Font font = Font.decode(value);
                if ( font != null )
                {
                    this.defaultFontRc = new FontUIResource(font);
                }
            }
            
            value = properties.getProperty("color.default");
            if ( value != null && value.trim().length() > 0 )
            {
                Color color = this.decodeColor(value);
                if ( color != null )
                {
                    this.defaultColorRc = new ColorUIResource(color);
                }
            }
            
            value = properties.getProperty("color.PanelBackground");
            if ( value != null && value.trim().length() > 0 )
            {
                Color c = this.decodeColor(value);
                if ( c != null )
                {
                    ColorUIResource colorRc = new ColorUIResource(c);
            
                    UIManager.getDefaults().put("Panel.background", c);
                }
            }
        }
    }

    public void addCustomEntriesToTable(UIDefaults table)
    {
        super.addCustomEntriesToTable(table);
    }
    
    /** return  Font according to a String that represent the component of the color in RGB or RGBA mode
     *  example : 255 255 255 or 0 255 255 125
     *  @param value a String
     *  @return a Color
     */
    private Color decodeColor(String value)
    {
        Color c = null;
        
        if ( value != null )
        {
            String[] values = value.split(" ");
            if ( values.length != 3 && values.length != 4 )
            {
                throw new IllegalArgumentException("color format error with '" + value + "'");
            }
            
            Integer red   = Integer.parseInt(values[0]);
            Integer green = Integer.parseInt(values[1]);
            Integer blue  = Integer.parseInt(values[2]);
            Integer alpha = (values.length >= 4 ? Integer.parseInt(values[3]) : null);
            
            if ( alpha == null )
            {
                c = new Color(red, green, blue);
            }
            else
            {
                c = new Color(red, green, blue, alpha);
            }
        }
        
        return c;
    }
    
    /** initialise les resources li�es aux couleurs
     *  @param properties a Properties where color resources caracteristics are stored
     */
    private void initColorResources(Properties properties)
    {
        this.colorResources = new HashMap<String, ColorUIResource>();
        
        if ( properties != null )
        {   
            String[] keys = new String[]{
                
                "color.WindowTitleInactiveForeground",
                "color.WindowTitleInactiveBackground",
                "color.WindowTitleForeground",
                "color.WindowTitleBackground",
                "color.WindowBackground",

                "color.Primary1",
                "color.Primary2",
                "color.Primary3",
                "color.PrimaryControl",
                "color.PrimaryControlDarkShadow",
                "color.PrimaryControlHighlight",
                "color.PrimaryControlShadow",
                "color.PrimaryControlInfo",

                "color.Secondary1",
                "color.Secondary2",
                "color.Secondary3",

                "color.Black",
                "color.White",

                "color.MenuBackground",
                "color.MenuForeground",
                "color.MenuDisabledForeground",
                "color.MenuSelectedBackground",
                "color.MenuSelectedForeground",
                "color.MenuItemBackground",
                "color.MenuItemSelectedBackground",
                "color.MenuItemSelectedForeground",

                "color.SimpleInternalFrameBackground",
                "color.SimpleInternalFrameForeground",

                "color.Control",
                "color.ControlDarkShadow",
                "color.ControlDisabled",
                "color.ControlHighlight",
                "color.ControlInfo",
                "color.ControlShadow",
                "color.ControlTextColor",

                "color.UserTextColor",
                "color.TextHighlightColor",
                "color.TitleTextColor",
                "color.SystemTextColor",
                "color.HighlightedTextColor",
                "color.InactiveControlTextColor",
                "color.InactiveSystemTextColor",

                "color.SeparatorForeground",
                "color.SeparatorBackground",

                "color.AcceleratorForeground",
                "color.AcceleratorSelectedForeground",

                "color.ToggleButtonCheckColor",
                "color.DesktopColor",
                "color.FocusColor"
            };
            
            String key   = null;
            String value = null;
            Color  color = null;
            
            for(int i = 0; i < keys.length; i++)
            {
                key = keys[i];
                value = properties.getProperty(key);
                
                if ( value != null && value.trim().length() > 0 )
                {
                    color = this.decodeColor(value);
                    if ( color != null )
                    {
                        this.colorResources.put(key, new ColorUIResource(color));
                    }
                }
                else
                {
                    System.out.println("color resource : " + key + " not initialized");
                }
            }
        }
        
        
    }
    
    /** initialise les resources li�es aux fonts
     *  @param properties a Properties where font resources caracteristics are stored
     */
    private void initFontResources(Properties properties)
    {
        this.fontResources = new HashMap<String, FontUIResource>();
        
        if ( properties != null )
        {   
            String[] keys = new String[]{
                "font.SubTextFont"    ,
                "font.MenuTextFont"   ,
                "font.ControlTextFont",
                "font.Font"           ,
                "font.SystemTextFont" ,
                "font.TitleTextFont"  ,
                "font.UserTextFont"   ,
                "font.WindowTitleFont"
            };
            
            String key   = null;
            String value = null;
            Font   font  = null;
            
            for(int i = 0; i < keys.length; i++)
            {
                key = keys[i];
                value = properties.getProperty(key);
                
                if ( value != null && value.trim().length() > 0 )
                {
                    font = Font.decode(value);
                    if ( font != null )
                    {
                        this.fontResources.put(key, new FontUIResource(font));
                    }
                }
                else
                {
                    System.out.println("font resource : " + key + " not initialized");
                }
            }
        }
    }
    
    /** m�thode retournant un ColorUIResource en fonction d'un code
     *  @param uiRcCode le code d'un ColorUIResource
     *  @return un ColorUIResource
     */
    private ColorUIResource getColorUIResource(String uiRcCode)
    {   ColorUIResource rc = this.colorResources.get(uiRcCode);
        
        if ( rc == null )
        {   rc = this.defaultColorRc; }
        
        return rc;
    }
    
    /** m�thode retournant un FontUIResource en fonction d'un code
     *  @param uiRcCode le code d'un FontUIResource
     *  @return un FontUIResource
     */
    private FontUIResource getFontUIResource(String uiRcCode)
    {   FontUIResource rc = this.fontResources.get(uiRcCode);
        
        if ( rc == null )
        {   rc = this.defaultFontRc; }
        
        return rc;
    }

    protected Font getFont0()
    {   return this.fontZero; }
    
    /* ###########################################################
     * #################### ColorUIResources #####################
     * ########################################################### */

    public ColorUIResource getWindowTitleInactiveForeground()
    {   return this.getColorUIResource("color.WindowTitleInactiveForeground"); }

    public ColorUIResource getWindowTitleInactiveBackground()
    {   return this.getColorUIResource("color.WindowTitleInactiveBackground"); }

    public ColorUIResource getWindowTitleForeground()
    {   return this.getColorUIResource("color.WindowTitleForeground"); }

    public ColorUIResource getWindowTitleBackground()
    {   return this.getColorUIResource("color.WindowTitleBackground"); }

    public ColorUIResource getWindowBackground()
    {   return this.getColorUIResource("color.WindowBackground"); }

    public ColorUIResource getUserTextColor()
    {   return this.getColorUIResource("color.UserTextColor"); }

    public ColorUIResource getTextHighlightColor()
    {   return this.getColorUIResource("color.TextHighlightColor"); }

    public ColorUIResource getSeparatorForeground()
    {   return this.getColorUIResource("color.SeparatorForeground"); }

    public ColorUIResource getSeparatorBackground()
    {   return this.getColorUIResource("color.SeparatorBackground"); }

    public ColorUIResource getPrimaryControlShadow()
    {   return this.getColorUIResource("color.PrimaryControlShadow"); }

    public ColorUIResource getPrimaryControlInfo()
    {   return this.getColorUIResource("color.PrimaryControlInfo"); }

    protected ColorUIResource getSecondary1()
    {   return this.getColorUIResource("color.Secondary1"); }

    protected ColorUIResource getPrimary3()
    {   return this.getColorUIResource("color.Primary3"); }

    protected ColorUIResource getPrimary2()
    {   return this.getColorUIResource("color.Primary2"); }

    protected ColorUIResource getPrimary1()
    {   return this.getColorUIResource("color.Primary1"); }

    protected ColorUIResource getWhite()
    {   return this.getColorUIResource("color.White"); }

    public ColorUIResource getToggleButtonCheckColor()
    {   return this.getColorUIResource("color.ToggleButtonCheckColor"); }

    public ColorUIResource getTitleTextColor()
    {   return this.getColorUIResource("color.TitleTextColor"); }

    protected ColorUIResource getBlack()
    {   return this.getColorUIResource("color.Black"); }

    public ColorUIResource getMenuForeground()
    {   return this.getColorUIResource("color.MenuForeground"); }

    public ColorUIResource getMenuItemBackground()
    {   return this.getColorUIResource("color.MenuItemBackground"); }

    public ColorUIResource getMenuItemSelectedBackground()
    {   return this.getColorUIResource("color.MenuItemSelectedBackground"); }

    public ColorUIResource getMenuItemSelectedForeground()
    {   return this.getColorUIResource("color.MenuItemSelectedForeground"); }

    public ColorUIResource getSimpleInternalFrameBackground()
    {   return this.getColorUIResource("color.SimpleInternalFrameBackground"); }

    public ColorUIResource getSimpleInternalFrameForeground()
    {   return this.getColorUIResource("color.SimpleInternalFrameForeground"); }

    public ColorUIResource getSystemTextColor()
    {   return this.getColorUIResource("color.SystemTextColor"); }

    protected ColorUIResource getSecondary2()
    {   return this.getColorUIResource("color.Secondary2"); }

    protected ColorUIResource getSecondary3()
    {   return this.getColorUIResource("color.Secondary3"); }

    public ColorUIResource getAcceleratorForeground()
    {   return this.getColorUIResource("color.AcceleratorForeground"); }

    public ColorUIResource getAcceleratorSelectedForeground()
    {   return this.getColorUIResource("color.AcceleratorSelectedForeground"); }

    public ColorUIResource getControl()
    {   return this.getColorUIResource("color.Control"); }

    public ColorUIResource getControlDarkShadow()
    {   return this.getColorUIResource("color.ControlDarkShadow"); }

    public ColorUIResource getControlDisabled()
    {   return this.getColorUIResource("color.ControlDisabled"); }

    public ColorUIResource getControlHighlight()
    {   return this.getColorUIResource("color.ControlHighlight"); }

    public ColorUIResource getControlInfo()
    {   return this.getColorUIResource("color.ControlInfo"); }

    public ColorUIResource getControlShadow()
    {   return this.getColorUIResource("color.ControlShadow"); }

    public ColorUIResource getControlTextColor()
    {   return this.getColorUIResource("color.ControlTextColor"); }

    public ColorUIResource getDesktopColor()
    {   return this.getColorUIResource("color.DesktopColor"); }

    public ColorUIResource getFocusColor()
    {   return this.getColorUIResource("color.FocusColor"); }

    public ColorUIResource getHighlightedTextColor()
    {   return this.getColorUIResource("color.HighlightedTextColor"); }

    public ColorUIResource getInactiveControlTextColor()
    {   return this.getColorUIResource("color.InactiveControlTextColor"); }

    public ColorUIResource getInactiveSystemTextColor()
    {   return this.getColorUIResource("color.InactiveSystemTextColor"); }

    public ColorUIResource getMenuBackground()
    {   return this.getColorUIResource("color.MenuBackground"); }

    public ColorUIResource getMenuDisabledForeground()
    {   return this.getColorUIResource("color.MenuDisabledForeground"); }

    public ColorUIResource getMenuSelectedBackground()
    {   return this.getColorUIResource("color.MenuSelectedBackground"); }

    public ColorUIResource getMenuSelectedForeground()
    {   return this.getColorUIResource("color.MenuSelectedForeground"); }

    public ColorUIResource getPrimaryControl()
    {   return this.getColorUIResource("color.PrimaryControl"); }

    public ColorUIResource getPrimaryControlDarkShadow()
    {   return this.getColorUIResource("color.PrimaryControlDarkShadow"); }

    public ColorUIResource getPrimaryControlHighlight()
    {   return this.getColorUIResource("color.PrimaryControlHighlight"); }
    
    /* ###########################################################
     * ##################### FontUIResources #####################
     * ########################################################### */

    public FontUIResource getSubTextFont()
    {   return this.getFontUIResource("font.SubTextFont"); }

    public FontUIResource getMenuTextFont()
    {   return this.getFontUIResource("font.MenuTextFont"); }

    public FontUIResource getControlTextFont()
    {   return this.getFontUIResource("font.ControlTextFont"); }

    protected FontUIResource getFont()
    {   return this.getFontUIResource("font.Font"); }

    public FontUIResource getSystemTextFont()
    {   return this.getFontUIResource("font.SystemTextFont"); }

    public FontUIResource getTitleTextFont()
    {   return this.getFontUIResource("font.TitleTextFont"); }

    public FontUIResource getUserTextFont()
    {   return this.getFontUIResource("font.UserTextFont"); }

    public FontUIResource getWindowTitleFont()
    {   return this.getFontUIResource("font.WindowTitleFont"); }
    
}
