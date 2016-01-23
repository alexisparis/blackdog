/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
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
 */

package net.sourceforge.atunes.gui;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.log4j.Logger;

import org.jvnet.lafwidget.animation.FadeConfigurationManager;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.ClassicBorderPainter;
import org.jvnet.substance.color.ColorScheme;
import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceConstants.TabContentPaneBorderKind;
import org.jvnet.substance.watermark.SubstanceNullWatermark;

public class LookAndFeelSelector {

	public static Logger logger = new Logger();

	public static final String DEFAULT_THEME = "Default";

	private static Map<String, String> themes = setListOfThemes();

	public static Object[] getListOfThemes() {
		Object[] result = themes.keySet().toArray();
		Arrays.sort(result);
		return result;
	}

	private static Map<String, String> setListOfThemes() {
		Map<String, String> result = new HashMap<String, String>();

		result.put("Aqua", "org.jvnet.substance.theme.SubstanceAquaTheme");
		result.put("Pink", "org.jvnet.substance.theme.SubstanceBarbyPinkTheme");
		result.put("Green", "org.jvnet.substance.theme.SubstanceBottleGreenTheme");
		result.put("Brown", "org.jvnet.substance.theme.SubstanceBrownTheme");
		result.put("Charcoal", "org.jvnet.substance.theme.SubstanceCharcoalTheme");
		result.put("Creme", "org.jvnet.substance.theme.SubstanceCremeTheme");
		result.put("Default", "");
		result.put("DarkViolet", "org.jvnet.substance.theme.SubstanceDarkVioletTheme");
		result.put("DesertSand", "org.jvnet.substance.theme.SubstanceDesertSandTheme");
		result.put("Ebony", "org.jvnet.substance.theme.SubstanceEbonyTheme");
		result.put("JadeForest", "org.jvnet.substance.theme.SubstanceJadeForestTheme");
		result.put("LightAqua", "org.jvnet.substance.theme.SubstanceLightAquaTheme");
		result.put("LimeGreen", "org.jvnet.substance.theme.SubstanceLimeGreenTheme");
		result.put("Olive", "org.jvnet.substance.theme.SubstanceOliveTheme");
		result.put("Orange", "org.jvnet.substance.theme.SubstanceOrangeTheme");
		result.put("Purple", "org.jvnet.substance.theme.SubstancePurpleTheme");
		result.put("Raspberry", "org.jvnet.substance.theme.SubstanceRaspberryTheme");
		result.put("Sepia", "org.jvnet.substance.theme.SubstanceSepiaTheme");
		result.put("SteelBlue", "org.jvnet.substance.theme.SubstanceSteelBlueTheme");
		result.put("SunGlare", "org.jvnet.substance.theme.SubstanceSunGlareTheme");
		result.put("Sunset", "org.jvnet.substance.theme.SubstanceSunsetTheme");
		result.put("Terracotta", "org.jvnet.substance.theme.SubstanceTerracottaTheme");
		result.put("Ultramarine", "org.jvnet.substance.theme.SubstanceUltramarineTheme");

		return result;
	}

	public static void setLookAndFeel(String theme) {
		if (Kernel.IGNORE_LOOK_AND_FEEL)
			return;

		try {
			// Use Business as base Look And Feel
			UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());

			if (themes.containsKey(theme))
				SubstanceLookAndFeel.setCurrentTheme(themes.get(theme));
			else
				SubstanceLookAndFeel.setCurrentTheme(themes.get(DEFAULT_THEME));

			// Get border color
			ColorScheme scheme = SubstanceLookAndFeel.getActiveColorScheme();
			Color borderColor = scheme.getMidColor();
			GuiUtils.setBorderColor(borderColor);

			FadeConfigurationManager.getInstance().allowFades(SubstanceLookAndFeel.TREE_DECORATIONS_ANIMATION_KIND);

			SubstanceLookAndFeel.setCurrentWatermark(new SubstanceNullWatermark());

			//SubstanceLookAndFeel.setCurrentTitlePainter(new ClassicTitlePainter());
			UIManager.put(SubstanceLookAndFeel.NO_EXTRA_ELEMENTS, Boolean.TRUE);
			UIManager.put(SubstanceLookAndFeel.TABBED_PANE_VERTICAL_ORIENTATION, Boolean.TRUE);
			UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, TabContentPaneBorderKind.SINGLE_FULL);

			//TODO Maybe better to write custom skins?
			UIManager.put(SubstanceLookAndFeel.BORDER_PAINTER_PROPERTY, new ClassicBorderPainter());
			// adjust background color of text fields when dark theme is used
			if (theme.equals("Charcoal") || theme.equals("Ebony") || theme.equals("DarkViolet") || theme.equals("JadeForest") || theme.equals("Ultramarine")) {
				UIManager.put("TextField.background", scheme.getMidColor());
				UIManager.put("PasswordField.background", scheme.getMidColor());
				UIManager.put("TextArea.background", scheme.getMidColor());
				UIManager.put("ComboBox.background", scheme.getMidColor());
			}

			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		} catch (Exception e) {
			logger.internalError(e);
		}
	}
}
