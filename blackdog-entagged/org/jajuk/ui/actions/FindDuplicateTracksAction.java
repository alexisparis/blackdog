/*
 *  Jajuk
 *  Copyright (C) 2005 The Jajuk Team
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
 *  $$Revision: 3425 $$
 */

package org.jajuk.ui.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.jajuk.Main;
import org.jajuk.base.File;
import org.jajuk.base.Track;
import org.jajuk.base.TrackManager;
import org.jajuk.ui.widgets.DuplicateTracksList;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.Util;

public class FindDuplicateTracksAction extends ActionBase {

  private static final long serialVersionUID = 1L;

  FindDuplicateTracksAction() {
    super(Messages.getString("FindDuplicateTracksAction.2"), IconLoader.ICON_SEARCH, true);
    setShortDescription(Messages.getString("FindDuplicateTracksAction.2"));
  }

  public void perform(ActionEvent evt) throws Exception {
    List<List<File>> duplicateFilesList = new ArrayList<List<File>>();
    for (Track track : TrackManager.getInstance().getTracks()) {
      List<File> trackFileList = track.getFiles();
      if (trackFileList.size() > 1) {
        duplicateFilesList.add(trackFileList);
      }
    }
    if (duplicateFilesList.size() < 1) {
      Messages.showInfoMessage(Messages.getString("FindDuplicateTracksAction.0"));
    } else {
      final JOptionPane optionPane = Util.getNarrowOptionPane(100);
      final JDialog duplicateFiles = optionPane.createDialog(null, Messages
          .getString("FindDuplicateTracksAction.3"));

      duplicateFiles.setMaximumSize(new Dimension(600, 800));

      JButton jbClose = new JButton(Messages.getString("Close"));
      jbClose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          duplicateFiles.dispose();
        }
      });

      // Create and set up the content pane.
      JComponent newContentPane = new DuplicateTracksList(duplicateFilesList, jbClose);
      newContentPane.setOpaque(true);
      duplicateFiles.setContentPane(newContentPane);

      // Display the window.
      duplicateFiles.setModal(true);
      duplicateFiles.setAlwaysOnTop(true);
      duplicateFiles.pack();
      duplicateFiles.setLocationRelativeTo(Main.getWindow());
      duplicateFiles.setVisible(true);
    }
  }

  private String convertToString(List<File> duplicateFilesList) {
    StringBuilder buffer = new StringBuilder();
    for (File file : duplicateFilesList) {
      buffer.append('\t');
      buffer.append(file.getName());
      buffer.append('\n');
    }
    return buffer.toString();
  }
}
