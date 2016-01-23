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
 *  $$Revision: 3308 $$
 */
package org.jajuk.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import org.jajuk.base.File;
import org.jajuk.base.FileManager;
import org.jajuk.services.dj.Ambience;
import org.jajuk.services.dj.AmbienceManager;
import org.jajuk.services.players.FIFO;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.Util;
import org.jajuk.util.error.JajukException;

public class GlobalRandomAction extends ActionBase {

  private static final long serialVersionUID = 1L;

  GlobalRandomAction() {
    super(Messages.getString("JajukWindow.6"), IconLoader.ICON_SHUFFLE_GLOBAL, true);
    String sTooltip = Messages.getString("JajukWindow.23");
    Ambience ambience = AmbienceManager.getInstance().getAmbience(
        ConfigurationManager.getProperty(CONF_DEFAULT_AMBIENCE));
    if (ambience != null) {
      String sAmbience = ambience.getName();
      sTooltip = "<html>" + Messages.getString("JajukWindow.23") + "<p><b>" + sAmbience
          + "</b></p></html>";
    }
    setShortDescription(sTooltip);
  }

  public void perform(ActionEvent evt) throws JajukException {
    new Thread() {
      public void run() {
        Ambience ambience = AmbienceManager.getInstance().getSelectedAmbience();
        List<File> alToPlay = Util.filterByAmbience(FileManager.getInstance()
            .getGlobalShufflePlaylist(), ambience);
        // For perfs (mainly playlist editor view refresh), we set a ceil for
        // tracks number
        if (alToPlay.size() > NB_TRACKS_ON_ACTION) {
          alToPlay = (List<File>) alToPlay.subList(0, NB_TRACKS_ON_ACTION);
        }
        // Push them
        FIFO.getInstance().push(
            Util.createStackItems(alToPlay, ConfigurationManager.getBoolean(CONF_STATE_REPEAT),
                false), false);
      }
    }.start();
  }
}
