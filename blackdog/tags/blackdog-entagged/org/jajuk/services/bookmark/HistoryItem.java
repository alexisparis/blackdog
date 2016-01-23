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
 *  $Revision: 3266 $
 */

package org.jajuk.services.bookmark;

import java.util.Date;

import org.jajuk.base.File;
import org.jajuk.base.FileManager;

/**
 * An history item
 */
public class HistoryItem {
  /** File Id */
  private String sFileId;

  /** Play date */
  private long lDate;

  public HistoryItem(String sFileId, long lDate) {
    this.sFileId = sFileId;
    this.lDate = lDate;
  }

  /**
   * @return Returns the date.
   */
  public long getDate() {
    return lDate;
  }

  /**
   * @param date
   *          The date to set.
   */
  public void setDate(long lDate) {
    this.lDate = lDate;
  }

  /**
   * @return Returns the sFileId.
   */
  public String getFileId() {
    return sFileId;
  }

  /**
   * @param fileId
   *          The sFileId to set.
   */
  public void setFileId(String fileId) {
    sFileId = fileId;
  }

  /**
   * Human readable representation of this history item as read in the history
   * bar
   * 
   * @return String
   */
  public String toString() {
    File file = FileManager.getInstance().getFileByID(getFileId());
    if (file == null) {
      return null;
    }
    StringBuilder sbAuthor = new StringBuilder(file.getTrack().getAuthor().getName2());
    String sDate = History.getInstance().getDateFormatter().format(new Date(getDate()));
    return sbAuthor.append(" / ").append(file.getTrack().getName()).append(" [").append(sDate)
        .append("]").toString();
  }
}
