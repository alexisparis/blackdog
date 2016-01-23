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
 *  $$Revision: 3454 $$
 */

package org.jajuk.ui.wizard;

import info.clearthought.layout.TableLayout;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jajuk.base.Device;
import org.jajuk.base.DeviceManager;
import org.jajuk.base.DirectoryManager;
import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.ui.widgets.InformationJPanel;
import org.jajuk.ui.widgets.JajukFileChooser;
import org.jajuk.util.EventSubject;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukFileFilter;
import org.jajuk.util.Messages;
import org.jajuk.util.filters.DirectoryFilter;
import org.jajuk.util.log.Log;

/**
 * Device creation wizard
 */
public class DeviceWizard extends JFrame implements ActionListener, ITechnicalStrings {
  private static final long serialVersionUID = 1L;

  JPanel jpMain;

  JPanel jp1;

  JLabel jlType;

  JComboBox jcbType;

  JLabel jlName;

  JTextField jtfName;

  JLabel jlUrl;

  JTextField jtfUrl;

  JButton jbUrl;

  JCheckBox jcbRefresh;

  JCheckBox jcbAutoMount;

  JLabel jlAutoRefresh;

  JFormattedTextField jftfAutoRefresh;

  JLabel jlMinutes;

  JCheckBox jcboxSynchronized;

  JComboBox jcbSynchronized;

  JPanel jp2;

  ButtonGroup bgSynchro;

  JRadioButton jrbBidirSynchro;

  JRadioButton jrbUnidirSynchro;

  JPanel jpButtons;

  JButton jbOk;

  JButton jbCancel;

  /** New device flag */
  private boolean bNew = true;

  /** Current device */
  Device device;

  /** All devices expect itself */
  ArrayList<Device> alDevices = new ArrayList<Device>(10);

  /** Initial URL* */
  private String sInitialURL;

  /**
   * Device wizard by default, is used for void configuration
   */
  public DeviceWizard() {
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowActivated(final WindowEvent e) {
        jtfName.requestFocusInWindow();
      }
    });
    setTitle(Messages.getString("DeviceWizard.0"));
    setLocation(org.jajuk.Main.getWindow().getX() + 100, org.jajuk.Main.getWindow().getY() + 100);
    jpMain = new JPanel();
    jpMain.setLayout(new BoxLayout(jpMain, BoxLayout.Y_AXIS));
    jp1 = new JPanel();
    jp1.setBorder(BorderFactory.createEmptyBorder(25, 15, 0, 15));
    final int iX_SEPARATOR = 5;
    final double size1[][] = { { 0.5, iX_SEPARATOR, 0.45, iX_SEPARATOR, 40 },
        { 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 20 } };
    jp1.setLayout(new TableLayout(size1));
    jlType = new JLabel(Messages.getString("DeviceWizard.1"));
    jcbType = new JComboBox();
    final Iterator itDevicesTypes = DeviceManager.getInstance().getDeviceTypes();
    while (itDevicesTypes.hasNext()) {
      jcbType.addItem(itDevicesTypes.next());
    }
    jlName = new JLabel(Messages.getString("DeviceWizard.2"));
    jtfName = new JTextField();
    jtfName.setToolTipText(Messages.getString("DeviceWizard.45"));
    jlUrl = new JLabel(Messages.getString("DeviceWizard.3"));
    jtfUrl = new JTextField();
    jtfUrl.setToolTipText(Messages.getString("DeviceWizard.46"));
    jbUrl = new JButton(IconLoader.ICON_OPEN_FILE);
    jbUrl.setToolTipText(Messages.getString("DeviceWizard.43"));
    jbUrl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    jbUrl.addActionListener(this);
    // we disable focus for url and mount url buttons to facilitate
    // navigation
    jbUrl.setFocusable(false);
    jcbRefresh = new JCheckBox(Messages.getString("DeviceWizard.7"));
    jcbRefresh.setToolTipText(Messages.getString("DeviceWizard.48"));
    jcbRefresh.addActionListener(this);
    jcbAutoMount = new JCheckBox(Messages.getString("DeviceWizard.8"));
    jcbAutoMount.setToolTipText(Messages.getString("DeviceWizard.49"));
    jcbAutoMount.addActionListener(this);
    jlAutoRefresh = new JLabel(Messages.getString("DeviceWizard.53"));
    jlAutoRefresh.setToolTipText(Messages.getString("DeviceWizard.50"));
    jlMinutes = new JLabel(Messages.getString("DeviceWizard.54"));
    jftfAutoRefresh = new JFormattedTextField(NumberFormat.getNumberInstance());
    // mininum delay is half a minute
    jftfAutoRefresh.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(final PropertyChangeEvent e) {
        final String prop = e.getPropertyName();
        if (prop.equals(JOptionPane.VALUE_PROPERTY)) {
          final double value = Double.valueOf(jftfAutoRefresh.getText().replace(',', '.'));
          jftfAutoRefresh.setValue(value);
          if ((value < 0) || ((value < 0.5d) && (value != 0))) {
            jftfAutoRefresh.setValue(0.5d);
          }
        }
      }
    });
    jftfAutoRefresh.setToolTipText(Messages.getString("DeviceWizard.50"));
    jcboxSynchronized = new JCheckBox(Messages.getString("DeviceWizard.10"));
    jcboxSynchronized.setToolTipText(Messages.getString("DeviceWizard.51"));
    jcboxSynchronized.addActionListener(this);
    jcbSynchronized = new JComboBox();
    // populate combo
    final Iterator<Device> it = DeviceManager.getInstance().getDevices().iterator();
    while (it.hasNext()) {
      final Device device2 = it.next();
      alDevices.add(device2);
      jcbSynchronized.addItem(device2.getName());
    }
    jcbSynchronized.setEnabled(false);
    jcbSynchronized.setToolTipText(Messages.getString("DeviceWizard.52"));
    // Default automount behavior
    jcbType.addActionListener(this);
    bgSynchro = new ButtonGroup();
    jrbUnidirSynchro = new JRadioButton(Messages.getString("DeviceWizard.11"));
    jrbUnidirSynchro.setToolTipText(Messages.getString("DeviceWizard.12"));
    jrbUnidirSynchro.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
    jrbUnidirSynchro.setEnabled(false);
    jrbUnidirSynchro.addActionListener(this);
    jrbBidirSynchro = new JRadioButton(Messages.getString("DeviceWizard.13"));
    jrbBidirSynchro.setToolTipText(Messages.getString("DeviceWizard.14"));
    jrbBidirSynchro.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
    jrbBidirSynchro.setEnabled(false);
    jrbBidirSynchro.addActionListener(this);
    bgSynchro.add(jrbBidirSynchro);
    bgSynchro.add(jrbUnidirSynchro);
    jp1.add(jlType, "0,0");
    jp1.add(jcbType, "2,0");
    jp1.add(jlName, "0,2");
    jp1.add(jtfName, "2,2");
    jp1.add(jlUrl, "0,4");
    jp1.add(jtfUrl, "2,4");
    jp1.add(jbUrl, "4,4");
    jp1.add(jlAutoRefresh, "0,6");
    jp1.add(jftfAutoRefresh, "2,6");
    jp1.add(jlMinutes, "4,6");
    jp1.add(jcbRefresh, "0,8");
    jp1.add(jcbAutoMount, "0,10");
    jp1.add(jcboxSynchronized, "0,12");
    jp1.add(jcbSynchronized, "2,12");
    final double size2[][] = { { 0.99 }, { 20, 20, 20, 20, 20, 20, 20 } };
    jp2 = new JPanel();
    jp2.setLayout(new TableLayout(size2));
    jp2.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
    jp2.add(jrbUnidirSynchro, "0,1");
    jp2.add(jrbBidirSynchro, "0,3");
    if (jcbSynchronized.getItemCount() == 0) {
      jcboxSynchronized.setEnabled(false);
      jcbSynchronized.setEnabled(false);
      jrbBidirSynchro.setEnabled(false);
    }
    // buttons
    jpButtons = new JPanel();
    jpButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
    jbOk = new JButton(Messages.getString("Ok"));
    jbOk.requestFocusInWindow();
    jbOk.addActionListener(this);
    jbCancel = new JButton(Messages.getString("Cancel"));
    jbCancel.addActionListener(this);
    jpButtons.add(jbOk);
    jpButtons.add(jbCancel);

    jpMain.add(jp1);
    jpMain.add(jp2);
    jpMain.add(Box.createVerticalGlue());
    jpMain.add(jpButtons);
    getRootPane().setDefaultButton(jbOk);
    setContentPane(jpMain);
    pack();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(final ActionEvent e) {
    if (e.getSource() == jcboxSynchronized) {
      if (jcboxSynchronized.isSelected()) {
        jcbSynchronized.setEnabled(true);
        jrbBidirSynchro.setEnabled(true);
        jrbUnidirSynchro.setEnabled(true);
      } else {
        jcbSynchronized.setEnabled(false);
        jrbBidirSynchro.setEnabled(false);
        jrbUnidirSynchro.setEnabled(false);
      }
    } else if (e.getSource() == jbOk) {
      // surface checks
      try {
        jftfAutoRefresh.commitEdit();
      } catch (final ParseException e1) {
        Messages.showErrorMessage(137);
        setVisible(true);
        return;
      }
      if (jtfUrl.getText().trim().equals("")) {
        Messages.showErrorMessage(21);
        setVisible(true);
        return;
      }
      if (jtfName.getText().trim().equals("")) {
        Messages.showErrorMessage(22);
        setVisible(true);
        return;
      }
      // check device availability (test name only if new device)
      final int code = DeviceManager.getInstance().checkDeviceAvailablity(jtfName.getText(),
          jcbType.getSelectedIndex(), jtfUrl.getText(), bNew);
      if (code != 0) {
        Messages.showErrorMessage(code);
        setVisible(true); // display wizard window which has been
        // hidden by the error window
        return;
      }
      if (bNew) {
        device = DeviceManager.getInstance().registerDevice(jtfName.getText(),
            jcbType.getSelectedIndex(), jtfUrl.getText());
      }
      device.setProperty(ITechnicalStrings.XML_DEVICE_AUTO_MOUNT, jcbAutoMount.isSelected());
      device.setProperty(ITechnicalStrings.XML_DEVICE_AUTO_REFRESH, new Double(jftfAutoRefresh
          .getValue().toString()));
      device.setProperty(ITechnicalStrings.XML_TYPE, new Long(jcbType.getSelectedIndex()));
      device.setUrl(jtfUrl.getText());
      if (jcbSynchronized.isEnabled() && (jcbSynchronized.getSelectedItem() != null)) {
        device.setProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_SOURCE, alDevices.get(
            jcbSynchronized.getSelectedIndex()).getID());
        if (jrbBidirSynchro.isSelected()) {
          device.setProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_MODE,
              ITechnicalStrings.DEVICE_SYNCHRO_MODE_BI);
        } else {
          device.setProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_MODE,
              ITechnicalStrings.DEVICE_SYNCHRO_MODE_UNI);
        }
      } else { // no synchro
        device.removeProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_MODE);
        device.removeProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_SOURCE);
      }
      // Force deep refresh if it is a new device or if URL changed
      if (jcbRefresh.isSelected() && bNew) {
        try {
          // Drop existing directory to avoid phantom directories if
          // existing device
          DirectoryManager.getInstance().removeDirectory(device.getID());
          device.refresh(true);
        } catch (final Exception e2) {
          Log.error(112, device.getName(), e2);
          Messages.showErrorMessage(112, device.getName());
        }
      } else if (sInitialURL != null && !sInitialURL.equals(jtfUrl.getText())) {
        // If user changed the URL, force refresh
        try {
          // try to remount the device
          device.mount();
          // Keep previous references when changing device url
          device.refreshCommand(false, false);
          // Force a cleanup *after* the refresh
          device.cleanRemovedFiles();
        } catch (final Exception e2) {
          Log.error(112, device.getName(), e2);
          Messages.showErrorMessage(112, device.getName());
        }
      }
      ObservationManager.notify(new Event(EventSubject.EVENT_DEVICE_REFRESH));
      dispose();
      if (bNew) {
        InformationJPanel.getInstance().setMessage(Messages.getString("DeviceWizard.44"),
            InformationJPanel.INFORMATIVE);
      }
    } else if (e.getSource() == jbCancel) {
      dispose(); // close window
    } else if (e.getSource() == jbUrl) {
      final JajukFileChooser jfc = new JajukFileChooser(new JajukFileFilter(DirectoryFilter
          .getInstance()));
      jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jfc.setDialogTitle(Messages.getString("DeviceWizard.43"));
      jfc.setMultiSelectionEnabled(false);
      final String sUrl = jtfUrl.getText();
      if (!sUrl.equals("")) {
        // if url is already set, use it as root directory
        jfc.setCurrentDirectory(new File(sUrl));
      }
      final int returnVal = jfc.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final java.io.File file = jfc.getSelectedFile();
        jtfUrl.setText(file.getAbsolutePath());
      }
    } else if (e.getSource() == jcbType) {
      switch (jcbType.getSelectedIndex()) {
      case 0: // directory
        jcbAutoMount.setSelected(true);
        if (bNew) {
          jftfAutoRefresh.setValue(0.5d);
        }
        break;
      case 1: // file cd
        jcbAutoMount.setSelected(false);
        if (bNew) {
          jftfAutoRefresh.setValue(0d);
        }
        break;
      case 2: // network drive
        jcbAutoMount.setSelected(true);
        // no auto-refresh by default for network drive
        if (bNew) {
          jftfAutoRefresh.setValue(0d);
        }
        break;
      case 3: // ext dd
        jcbAutoMount.setSelected(true);
        if (bNew) {
          jftfAutoRefresh.setValue(3d);
        }
        break;
      case 4: // player
        jcbAutoMount.setSelected(false);
        if (bNew) {
          jftfAutoRefresh.setValue(3d);
        }
        break;
      }
    }
  }

  /**
   * Update widgets for device property state
   */
  public void updateWidgets(final Device device) {
    bNew = false;
    setTitle(Messages.getString("DeviceWizard.0") + " : " + device.getName());
    this.device = device;
    jcbSynchronized.removeAllItems();
    alDevices.clear();
    // set default values for widgets
    updateWidgetsDefault();
    final Iterator<Device> it = DeviceManager.getInstance().getDevices().iterator();
    while (it.hasNext()) {
      final Device device2 = it.next();
      if (!device2.equals(device)) {
        alDevices.add(device2);
        jcbSynchronized.addItem(device2.getName());
      }
    }
    // then, specifics
    jcbType.setSelectedItem(device.getDeviceTypeS());
    jtfName.setText(device.getName());
    jtfName.setEnabled(false); // device name cannot be changed
    jtfUrl.setText(device.getUrl());
    sInitialURL = device.getUrl();
    jcbRefresh.setEnabled(false); // no instant refresh for updates
    jcbRefresh.setSelected(false);
    jcbAutoMount.setSelected(true);
    if (device.getBooleanValue(ITechnicalStrings.XML_DEVICE_AUTO_MOUNT)) {
      jcbAutoMount.setSelected(true);
    } else {
      jcbAutoMount.setSelected(false);
    }
    jftfAutoRefresh.setValue(device.getDoubleValue(ITechnicalStrings.XML_DEVICE_AUTO_REFRESH));
    if (jcbSynchronized.getItemCount() == 0) {
      jcboxSynchronized.setEnabled(false);
      jcbSynchronized.setEnabled(false);
      jrbBidirSynchro.setEnabled(false);
    }
    if (device.containsProperty(ITechnicalStrings.XML_DEVICE_SYNCHRO_SOURCE)) {
      final String sSynchroSource = device
          .getStringValue(ITechnicalStrings.XML_DEVICE_SYNCHRO_SOURCE);
      jrbBidirSynchro.setEnabled(true);
      jrbUnidirSynchro.setEnabled(true);
      jcboxSynchronized.setSelected(true);
      jcboxSynchronized.setEnabled(true);
      jcbSynchronized.setEnabled(true);
      jcbSynchronized.setSelectedIndex(alDevices.indexOf(DeviceManager.getInstance().getDeviceByID(
          sSynchroSource)));
      if (ITechnicalStrings.DEVICE_SYNCHRO_MODE_BI.equals(device
          .getValue(ITechnicalStrings.XML_DEVICE_SYNCHRO_MODE))) {
        jrbBidirSynchro.setSelected(true);
      } else {
        jrbUnidirSynchro.setSelected(true);
      }
    }
  }

  /**
   * Update widgets for default state
   */
  public void updateWidgetsDefault() {
    jcbRefresh.setSelected(true);
    jcbAutoMount.setSelected(true);
    jftfAutoRefresh.setValue(0.5d);
    jcboxSynchronized.setSelected(false);
    jrbUnidirSynchro.setSelected(true);// default synchro mode
    jrbBidirSynchro.setEnabled(false);
  }
}
