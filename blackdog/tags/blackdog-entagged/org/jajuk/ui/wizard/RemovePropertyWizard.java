/*
 *  Jajuk
 *  Copyright (C) 2007 The Jajuk Team
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
 *  $$Revision: 3266 $$
 */

package org.jajuk.ui.wizard;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JComboBox;

import org.jajuk.base.ItemManager;
import org.jajuk.base.PropertyMetaInformation;
import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.util.EventSubject;
import org.jajuk.util.Messages;

/**
 * 
 * Remove property wizard
 */
public class RemovePropertyWizard extends CustomPropertyWizard {
  private static final long serialVersionUID = 1L;

  JComboBox jcbName;

  /**
   * Constructor
   */
  public RemovePropertyWizard() {
    super(Messages.getString("RemovePropertyWizard.0"));
    jcbName = new JComboBox();
    populate();// create default UI
    jcbName.addItemListener(this);
    populateProperties();// fill properties combo with properties for
    // default item
    int iXSeparator = 10;
    int iYSeparator = 20;
    double[][] dSize = { { iXSeparator, 0.5, iXSeparator, 0.5, iXSeparator },
        { iYSeparator, 20, iYSeparator, 20, iYSeparator } };
    jpMain.setLayout(new TableLayout(dSize));
    jpMain.add(jlItemChoice, "1,1");
    jpMain.add(jcbItemChoice, "3,1");
    jpMain.add(jlName, "1,3");
    jpMain.add(jcbName, "3,3");
    getContentPane().add(jpMain);
    getContentPane().add(okp);
    getContentPane().add(Box.createVerticalStrut(10));
    getRootPane().setDefaultButton(okp.getOKButton());
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent ae) {
    if (ae.getSource().equals(this.okp.getOKButton())) {
      ItemManager im = getItemManager();
      String sProperty = jcbName.getSelectedItem().toString();
      im.removeProperty(sProperty);
      Properties properties = new Properties();
      properties.put(DETAIL_CONTENT, sProperty);
      Event event = new Event(EventSubject.EVENT_CUSTOM_PROPERTIES_REMOVE, properties);
      ObservationManager.notify(event);
      dispose();
    } else if (ae.getSource().equals(this.okp.getCancelButton())) {
      dispose();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
   */
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == jcbItemChoice) {
      populateProperties();
    }
    // update OK button state
    if (jcbItemChoice.getSelectedIndex() != -1 && jcbName.getSelectedIndex() != -1) {
      okp.getOKButton().setEnabled(true);
      okp.getOKButton().requestFocusInWindow();
    } else {
      okp.getOKButton().setEnabled(false);
    }
  }

  public void populateProperties() {
    // clear combo
    jcbName.removeAllItems();
    // refresh properties list for this item
    ItemManager im = getItemManager();
    if (im != null) {
      Iterator it = im.getCustomProperties().iterator();
      while (it.hasNext()) {
        PropertyMetaInformation meta = (PropertyMetaInformation) it.next();
        jcbName.addItem(meta.getName());
      }
    }
  }

}