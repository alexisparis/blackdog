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
 *  $Revision: 3454 $
 */

package org.jajuk.base;

import java.text.DateFormat;
import java.util.Comparator;

import org.jajuk.util.Util;

/**
 * 
 * Multi-method track comparator
 */
public class TrackComparator implements Comparator<Track> {
  /**
   * Sorting method
   */
  private int iSortingMethod = 0;

  /** sorting methods constants */
  public static final int STYLE_AUTHOR_ALBUM = 0;

  public static final int AUTHOR_ALBUM = 1;

  public static final int ALBUM = 2;

  public static final int YEAR_ALBUM = 3;

  public static final int DISCOVERY_ALBUM = 4;

  public static final int ORDER = 5;

  private static final DateFormat formatter = Util.getAdditionDateFormatter();

  /**
   * Constructor
   * 
   * @param iSortingMethod
   *          Sorting method
   */
  public TrackComparator(int iSortingMethod) {
    this.iSortingMethod = iSortingMethod;
  }

  /**
   * 
   * @param track
   * @return Hashcode string used to compare two tracks in accordance with the
   *         sorting method
   */
  private String getCompareString(Track track) {
    String sHashCompare = null;
    // comparison based on style, author, album, name and year to
    // differentiate 2 tracks with all the same attributes
    // note we need to use year because in sorted set, we must differentiate
    // 2 tracks with different years
    switch (iSortingMethod) {
    // Style/author/album
    case STYLE_AUTHOR_ALBUM:
      sHashCompare = new StringBuilder().append(track.getStyle().getName2()).append(
          track.getAuthor().getName2())// need 2 spaces to make
          // a right sorting (ex:
          // Rock and Rock & Roll)
          .append(track.getAlbum().getName2()).append(track.getName()).toString();
      break;
    // Author/album
    case AUTHOR_ALBUM:
      // need 2 spaces to make a right sorting (ex: Rock and Rock & Roll)
      sHashCompare = new StringBuilder().append(track.getAuthor().getName2()).append(
          track.getAlbum().getName2()).append(track.getName()).toString();
      break;
    // Album
    case ALBUM:
      sHashCompare = new StringBuilder().append(track.getAlbum().getName2())
          .append(track.getName()).toString();
      break;
    // Year / album
    case YEAR_ALBUM:
      sHashCompare = new StringBuilder().append(Util.padNumber(track.getYear().getValue(), 10))
          .append(track.getName()).toString();
      break;
    // discovery date / album
    case DISCOVERY_ALBUM:
      sHashCompare = new StringBuilder().append(formatter.format(track.getDiscoveryDate())).append(
          track.getAlbum().getName2()).append(track.getName()).toString();
      break;
    // Order / track name
    case ORDER:
      sHashCompare = new StringBuilder().append(
          Util.padNumber(track.getOrder(), 5) + track.getName()).toString();
      break;
    }
    return sHashCompare;
  }

  /**
   * Tracks compare
   * 
   * @param arg0
   * @param arg1
   * @return
   */
  public int compare(Track track1, Track track2) {
    if (track1.equals(track2)) {
      return 0;
    }
    // if track # is given, sort by # in a same album, otherwise, sort
    // alphabetically
    if (track2.getAlbum().equals(track1.getAlbum())
        && track2.getAuthor().equals(track1.getAuthor())
        && track2.getStyle().equals(track1.getStyle()) && (track1.getOrder() != track2.getOrder())) {
      // do not use year as an album can contains tracks with
      // different year but we want to keep order
      return (int) (track1.getOrder() - track2.getOrder());
    }
    String sHashCompare = getCompareString(track1);
    String sHashCompareOther = getCompareString(track2);
    // never return 0 here, because bidimap needs to distinct items
    int comp = sHashCompare.compareToIgnoreCase(sHashCompareOther);
    if (comp == 0) {
      return sHashCompare.compareTo(sHashCompareOther);
    }
    return comp;
  }
}
