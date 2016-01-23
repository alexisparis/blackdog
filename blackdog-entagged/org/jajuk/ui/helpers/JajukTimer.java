/*
 *  Jajuk
 *  Copyright (C) 2004 The Jajuk Team
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
 *  $Revision: 3266 $
 */

package org.jajuk.ui.helpers;

import java.util.ArrayList;
import java.util.Iterator;

import org.jajuk.base.File;
import org.jajuk.services.players.FIFO;
import org.jajuk.services.players.Player;
import org.jajuk.services.players.StackItem;

/**
 * This class is a convenient timer class, mainly for UI
 * <p>
 * Singleton
 * </p>
 */
public class JajukTimer {

  /** Self instance */
  private static JajukTimer timer;

  /** Total time to play in secs */
  private long lTimeToPlay = 0;

  /** A default heartbet time in ms */
  public static final int DEFAULT_HEARTBEAT = 800;

  /**
   * @return JajukTimer singleton
   */
  public static JajukTimer getInstance() {
    if (timer == null) {
      timer = new JajukTimer();
    }
    return timer;
  }

  /** Private constructor */
  private JajukTimer() {
  }

  /**
   * Add time of the given file
   * 
   * @param file
   */
  public void addTrackTime(File file) {
    if (file != null) {
      lTimeToPlay += file.getTrack().getDuration();
    }
  }

  /**
   * Add time of the given set of files
   * 
   * @param alFiles
   */
  public void addTrackTime(ArrayList alFiles) {
    Iterator it = alFiles.iterator();
    while (it.hasNext()) {
      Object o = it.next();
      if (o != null && o instanceof File) {
        addTrackTime((File) o);
      } else {
        File file = ((StackItem) o).getFile();
        addTrackTime(file);
      }
    }
  }

  /**
   * Remove time of the given file
   * 
   * @param file
   */
  public void removeTrackTime(File file) {
    if (file != null) {
      lTimeToPlay -= file.getTrack().getDuration();
    }
  }

  /**
   * Remove time of the given set of files
   * 
   * @param alFiles
   */
  public void removeTrackTime(ArrayList alFiles) {
    Iterator it = alFiles.iterator();
    while (it.hasNext()) {
      File file = (File) it.next();
      if (file != null) {
        removeTrackTime((File) it.next());
      }
    }
  }

  /**
   * @return Current track ellapsed time in secs
   */
  public long getCurrentTrackEllapsedTime() {
    return Player.getElapsedTime() / 1000;
  }

  /**
   * @return Current track position in % (ex:0.2)
   */
  public float getCurrentTrackPosition() {
    return Player.getCurrentPosition();
  }

  /**
   * @return Current track total time in secs
   */
  public long getCurrentTrackTotalTime() {
    return Player.getCurrentLength() / 1000;
  }

  /**
   * 
   * @return FIFO total time to be played in secs ( includes current track time
   *         to play). Returns -1 if repeat mode
   */
  public long getTotalTimeToPlay() {
    if (FIFO.getInstance().containsRepeat()) {
      // if repeat mode, total
      // time has no sense
      return -1;
    }
    return lTimeToPlay - getCurrentTrackEllapsedTime();
    // total time to play equals total time to play -
    // current track elapsed time
  }

  /**
   * Reset timer
   * 
   */
  public void reset() {
    lTimeToPlay = 0;
  }
}
