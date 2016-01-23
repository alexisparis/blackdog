/*
 *  Jajuk
 *  Copyright (C) 2003 The Jajuk Team
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $Revision: 3454 $
 */
package org.jajuk.ui.widgets;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.jajuk.ui.helpers.FontManager;
import org.jajuk.ui.helpers.FontManager.JajukFont;
import org.jajuk.ui.perspectives.IPerspective;
import org.jajuk.ui.perspectives.PerspectiveManager;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.Util;
import org.jdesktop.swingx.JXPanel;

/**
 * Menu bar used to choose the current perspective.
 */
public class PerspectiveBarJPanel extends JXPanel implements ITechnicalStrings {

  private static final long serialVersionUID = 1L;

  /** Perspectives tool bar* */
  private JToolBar jtbPerspective;

  /** Self instance */
  static private PerspectiveBarJPanel pb;

  /**
   * Perspective button
   */
  private ArrayList<JButton> alButtons = new ArrayList<JButton>(10);

  /**
   * Singleton access
   * 
   * @return
   */
  public static synchronized PerspectiveBarJPanel getInstance() {
    if (pb == null) {
      pb = new PerspectiveBarJPanel();
    }
    return pb;
  }

  /**
   * Constructor for PerspectiveBarJPanel.
   */
  private PerspectiveBarJPanel() {
    update();
  }

  /**
   * update contents
   * 
   */
  public void update() {
    // Perspectives tool bar
    jtbPerspective = new JToolBar(JToolBar.VERTICAL);
    Iterator<IPerspective> it = PerspectiveManager.getPerspectives().iterator();
    int index = 0;
    while (it.hasNext()) {
      final IPerspective perspective = it.next();
      Font font = FontManager.getInstance().getFont(JajukFont.PERSPECTIVES);
      int iconSize = ConfigurationManager.getInt(CONF_PERSPECTIVE_ICONS_SIZE);
      JButton jb = new JButton(perspective.getIcon());
      jb.setToolTipText(perspective.getDesc());
      jb.setBorder(new EmptyBorder(5, 5, 0, 5));
      if (iconSize >= 32) {
        int glyphSize = font.getSize();
        // Limit perspective label to icon width
        String desc = Util.getLimitedString(perspective.getDesc(), 3 + (iconSize / glyphSize));
        // No text for icon < 32 pixels in width: too narrow
        jb.setText(desc);
      }
      jb.setVerticalTextPosition(JButton.BOTTOM);
      jb.setHorizontalTextPosition(JButton.CENTER);
      jb.setFont(font);
      jb.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // no thread, it causes ugly screen repaint
          PerspectiveManager.setCurrentPerspective(perspective.getID());
        }
      });
      jtbPerspective.add(jb);
      alButtons.add(jb);
      index++;
    }
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    JScrollPane jsp = new JScrollPane(jtbPerspective);
    jsp.setBorder(null);
    jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    add(jsp);
  }

  /**
   * Show selected perspective
   * 
   * @param perspective
   */
  public void setActivated(IPerspective perspective) {
    Collection<IPerspective> perspectives = PerspectiveManager.getPerspectives();
    Iterator<JButton> it = alButtons.iterator();
    Iterator<IPerspective> it2 = perspectives.iterator();
    while (it.hasNext()) {
      final JButton jb = it.next();
      IPerspective perspective2 = it2.next();
      if (perspective2.equals(perspective)) { // this perspective is
        // selected
        jb.setSelected(true);
      } else {
        jb.setSelected(false);
      }
    }
  }

  /**
   * ToString() method
   */
  @Override
  public String toString() {
    return getClass().getName();
  }
}