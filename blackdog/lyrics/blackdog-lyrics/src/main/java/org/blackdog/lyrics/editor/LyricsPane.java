/*
 * blackdog lyrics : define editor and systems to get lyrics for a song
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
package org.blackdog.lyrics.editor;

import java.awt.Color;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import org.siberia.utilities.awt.ColorTranslator;

/**
 *
 * @author alexis
 */
public class LyricsPane extends JEditorPane
{
    
    /** Creates a new instance of LyricsPane */
    public LyricsPane()
    {
    }
    
    @Override
    public void updateUI()
    {
	super.updateUI();

	/** modify style sheet to use another color for the text */
	HTMLEditorKit editorKit = new HTMLEditorKit();

	Color c = this.getForeground();
	editorKit.getStyleSheet().addRule("BODY {color: " + ColorTranslator.getStringRepresentationOf(c) + "}"); //FF0000

	String text = this.getText();

	this.setEditorKit(editorKit);

	this.setText(text);
    }
    
}
