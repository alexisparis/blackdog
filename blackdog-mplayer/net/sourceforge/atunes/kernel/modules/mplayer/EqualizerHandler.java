package net.sourceforge.atunes.kernel.modules.mplayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JSlider;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.utils.log4j.LogCategories;
import net.sourceforge.atunes.utils.log4j.Logger;

public class EqualizerHandler {

	private static EqualizerHandler instance;

	private Logger logger = new Logger();

	private Map<String, Integer[]> presets;

	public static EqualizerHandler getInstance() {
		if (instance == null)
			instance = new EqualizerHandler();
		return instance;
	}

	private EqualizerHandler() {
		presets = getPresetsFromBundle();
	}

	/**
	 * Returns presets loaded from properties file. Keys are transformed to be
	 * shown on GUI
	 * 
	 * @return
	 */
	private Map<String, Integer[]> getPresetsFromBundle() {
		Map<String, Integer[]> result = new HashMap<String, Integer[]>();

		try {
			PropertyResourceBundle presetsBundle = new PropertyResourceBundle(new FileInputStream(Constants.EQUALIZER_PRESETS_FILE));
			Enumeration<String> keys = presetsBundle.getKeys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String preset = presetsBundle.getString(key);

				// Transform key
				key = key.replace('.', ' ');

				// Parse preset
				StringTokenizer st = new StringTokenizer(preset, ",");
				Integer[] presetsArray = new Integer[10];
				int i = 0;
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					presetsArray[i++] = Integer.parseInt(token);
				}

				result.put(key, presetsArray);
			}

		} catch (IOException ioe) {
			logger.error(LogCategories.PLAYER, ioe);
		} catch (NumberFormatException nfe) {
			logger.error(LogCategories.PLAYER, nfe);
		}

		return result;
	}

	/**
	 * @return the presets
	 */
	public String[] getPresetsNames() {
		List<String> names = new ArrayList<String>(presets.keySet());
		Collections.sort(names);
		return names.toArray(new String[] {});
	}

	/**
	 * Returns presets for a preset name
	 * 
	 * @param presetName
	 * @return
	 */
	public Integer[] getPresetByNameForShowInGUI(String presetName) {
		Integer[] preset = presets.get(presetName);
		// As preset is transformed to be shown in GUI, we must clone Integer[]
		// in order that this transformation does not affect original preset value
		Integer[] clonedPreset = new Integer[10];
		for (int i = 0; i < preset.length; i++) {
			clonedPreset[i] = preset[i] - 31;
		}
		return clonedPreset;
	}

	/**
	 * Gets preset from GUI and sets into application state
	 * 
	 * @param bands
	 */
	public void setEqualizerFromGUI(JSlider[] bands) {
		float[] eqSettings = new float[10];

		// Transform from [-32,32] to [-12,12] with float values and inversion
		for (int i = 0; i < bands.length; i++) {
			eqSettings[i] = bands[i].getValue() * 12 / -32f;
		}

		PlayerHandler.getInstance().setEqualizer(eqSettings);
	}

	/**
	 * Returns bands transformed in [-32,32] scale to be shown in GUI
	 * 
	 * @return
	 */
	public int[] getEqualizerSettingsToShowInGUI() {
		float[] eqSettings = PlayerHandler.getInstance().getEqualizer();
		int[] result = new int[eqSettings.length];

		// Transform from [-12,12] to [-32,32] with int values and inversion
		for (int i = 0; i < eqSettings.length; i++) {
			result[i] = (int) (eqSettings[i] * 32 / -12);
		}

		return result;
	}
}
