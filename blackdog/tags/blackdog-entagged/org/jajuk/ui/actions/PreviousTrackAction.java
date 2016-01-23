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
import java.util.ArrayList;

import org.jajuk.base.WebRadio;
import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.services.players.FIFO;
import org.jajuk.services.players.Player;
import org.jajuk.services.webradio.WebRadioManager;
import org.jajuk.util.EventSubject;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.log.Log;

/**
 * Action class for jumping to the previous track. Installed keystroke:
 * <code>CTRL + LEFT ARROW</code>.
 */
public class PreviousTrackAction extends ActionBase {

  private static final long serialVersionUID = 1L;

  PreviousTrackAction() {
    super(Messages.getString("JajukWindow.13"), IconLoader.ICON_PLAYER_PREVIOUS, "F9", false, true);
    setShortDescription(Messages.getString("JajukWindow.29"));
  }

  public void perform(ActionEvent evt) {
    // check modifiers to see if it is a movement inside track, between
    // tracks or between albums
    if (evt != null &&
    // evt == null when using hotkeys
        (evt.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) {
      ActionManager.getAction(JajukAction.PREVIOUS_ALBUM).actionPerformed(evt);
    } else {
      // if playing a radio, launch next radio station
      if (FIFO.getInstance().isPlayingRadio()) {
        final ArrayList<WebRadio> radios = new ArrayList<WebRadio>(WebRadioManager.getInstance()
            .getWebRadios());
        int index = radios.indexOf(FIFO.getInstance().getCurrentRadio());
        if (index == 0) {
          index = radios.size() - 1;
        } else {
          index--;
        }
        final int i = index;
        new Thread() {
          public void run() {
            FIFO.getInstance().launchRadio(radios.get(i));
          }
        }.start();
      } else {
        new Thread() {
          public void run() {
            synchronized (FIFO.MUTEX) {
              try {
                FIFO.getInstance().playPrevious();
              } catch (Exception e) {
                Log.error(e);
              }
              // Player was paused, reset pause button when
              // changing of
              // track
              if (Player.isPaused()) {
                Player.setPaused(false);
                ObservationManager.notify(new Event(EventSubject.EVENT_PLAYER_RESUME));
              }
            }
          }
        }.start();

      }
    }
  }
}
