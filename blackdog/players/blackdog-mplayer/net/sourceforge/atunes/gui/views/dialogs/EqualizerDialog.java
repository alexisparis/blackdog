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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.CustomModalFrame;
import net.sourceforge.atunes.kernel.modules.mplayer.EqualizerHandler;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Allows changing equalizer settings for mplayer. Mplayer offers a 10 band equalizer function.
 * @author sylvain
 *
 */
public class EqualizerDialog extends CustomModalFrame {

	private static final long serialVersionUID = 7295438534550341824L;

	private JSlider[] bands;
		
	private JLabel[] labels;
	
	private int[] equalizer;

	/**
	 * Draws the equalizer dialog.
	 * @param owner
	 */
	public EqualizerDialog(JFrame owner) {
		super(owner);
		// Width required by german translation
		setSize(510, 300);
		setTitle(StringUtils.getString(LanguageTool.getString("EQUALIZER"), " - ", Constants.APP_NAME, " ", Constants.APP_VERSION_NUMBER));
		setLocationRelativeTo(null);
		add(getContent());
		setResizable(false);
		GuiUtils.applyComponentOrientation(this);
		enableCloseActionWithEscapeKey();
	}

	private JSlider getNewJSlider() {
		JSlider slider = new JSlider(1, -32, 32, 0);
		slider.setInverted(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(32);
		slider.setMinorTickSpacing(4);

		return slider;
	}
	
	/**
	 * Updates sliders with current equalizer settings
	 */
	private void setSliderValues() {
		int[] eqSettings = EqualizerHandler.getInstance().getEqualizerSettingsToShowInGUI();
		if (eqSettings != null) {
			for (int i = 0; i < 10; i++) {
				bands[i].setValue(eqSettings[i]);
			}
		}
	}
	
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		String[] freqs = {"31Hz","62Hz","125Hz","250Hz","500Hz","1kHz","2kHz","4kHz","8kHz","16kHz"};
		bands = new JSlider[10];
		labels = new JLabel[10];
		
		for (int i = 0; i < 10; i++) {
			bands[i] = getNewJSlider();
			bands[i].addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					System.out.println(bands[0].getValue());
				}
			});
			labels[i] = new JLabel(freqs[i]);
			labels[i].setFont(Fonts.SMALL_FONT);
		}

		JLabel changeWhenStopped = new JLabel(LanguageTool.getString("CAN_ONLY_CHANGE_WHEN_STOPPED"));

		JButton loadPresetButton = new JButton(LanguageTool.getString("LOAD_PRESET"));
		loadPresetButton.setFont(Fonts.BUTTON_FONT);
		loadPresetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] names = EqualizerHandler.getInstance().getPresetsNames();
				
				// Show selector
				SelectorDialog selector = new SelectorDialog(EqualizerDialog.this, LanguageTool.getString("LOAD_PRESET"), names, null);
				selector.setVisible(true);
				
				// Get result
				Integer[] presets = EqualizerHandler.getInstance().getPresetByNameForShowInGUI(selector.getSelection());
				
				for (int i = 0; i < bands.length; i++) {
					bands[i].setValue(presets[i]);
				}
			}
		});
		
		JButton okButton = new JButton(LanguageTool.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			// When OK is clicked, save settings and change application state
			@Override
			public void actionPerformed(ActionEvent e) {
				EqualizerHandler.getInstance().setEqualizerFromGUI(bands);
				EqualizerDialog.this.setVisible(false);
			}
		});
		JButton cancelButton = new JButton(LanguageTool.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EqualizerDialog.this.setVisible(false);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.gridwidth = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;

		for (int i = 0; i < bands.length; i++) {
			c.gridx = i;
			panel.add(bands[i], c);
		}
		
		JLabel l12 = new JLabel("+12db");
		l12.setFont(Fonts.SMALL_FONT);
		JLabel l0 = new JLabel("0");
		l0.setFont(Fonts.SMALL_FONT);
		JLabel lm12 = new JLabel("-12db");
		lm12.setFont(Fonts.SMALL_FONT);
		
		JPanel labelPanel = new JPanel(new GridLayout(3,1,0,50));
		labelPanel.add(l12);
		labelPanel.add(l0);
		labelPanel.add(lm12);
		
		c.gridx = 10;
		panel.add(labelPanel);
		
		
		c.weighty = 0;
		c.gridy = 1;
		
		for (int i = 0; i < labels.length; i++) {
			c.gridx = i;
			panel.add(labels[i], c);
		}
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 11;
		c.fill = GridBagConstraints.SOUTH;
		panel.add(loadPresetButton, c);
		
		c.gridy = 3;
		panel.add(changeWhenStopped, c);
		
		JPanel auxPanel = new JPanel();
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		c.gridy = 4;
		panel.add(auxPanel, c);

		return panel;
	}
	
	/**
	 * Equalizer settings as list
	 * @return Equalizer settings of all 10 bands as list
	 */
	public int[] getEqualizer() {
		return equalizer;
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b)
			setSliderValues();
	}

}