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

package org.jajuk.util;

import java.util.Iterator;
import java.util.List;

import org.jajuk.base.Item;

/**
 * Filter on meta information
 */
public class Filter {

  /** Key */
  String key;

  /** Value* */
  String sValue;

  /** Human* */
  boolean bHuman = false;

  /** Exact* */
  boolean bExact = false;

  /**
   * Filter constructor
   * 
   * @param key
   *          key (property name). null if the filter is on any property
   * @param sValue
   *          value
   * @param bHuman
   *          is the filter apply value itself or its human representation if
   *          different ?
   * @param bExact
   *          is the filter should match exactly the value ?
   */
  public Filter(String key, String sValue, boolean bHuman, boolean bExact) {
    this.key = key;
    this.sValue = sValue;
    this.bHuman = bHuman;
    this.bExact = bExact;
  }

  public boolean isExact() {
    return bExact;
  }

  public boolean isHuman() {
    return bHuman;
  }

  public String getProperty() {
    return key;
  }

  public String getValue() {
    return sValue;
  }

  /**
   * Filter a list.
   * <p>
   * The same collection is returned with non-matching items removed
   * </p>
   * <p>
   * This filter is not thread safe.
   * </p>
   * 
   * @param in
   *          input list
   * @param filter
   * @return filtered list, void list if none match
   */
  @SuppressWarnings("unchecked")
  public static List<Item> filterItems(List<? extends Item> list, Filter filter) {
    if (filter == null || filter.getValue() == null) {
      return (List<Item>) list;
    }
    // Check if property is not the "fake" any property
    boolean bAny = (filter.getProperty() == null || "any".equals(filter.getProperty()));

    String comparator = null;
    String checked = filter.getValue();
    Iterator it = list.iterator();
    while (it.hasNext()) {
      Item item = (Item) it.next();
      // If none property set, the search if global "any"
      if (bAny) {
        comparator = item.getAny();
      } else {
        if (filter.isHuman()) {
          comparator = item.getHumanValue(filter.getProperty());
        } else {
          comparator = item.getStringValue(filter.getProperty());
        }
      }
      // perform the test
      boolean bMatch = false;
      if (filter.isExact()) {
        bMatch = (comparator.toLowerCase().equals(checked));
      } else {
        // Do not use Regexp matches() method, checked could contain string to
        // be escaped and ignore order if user enters several words
        bMatch = Util.matchesIgnoreCaseAndOrder(checked, comparator);
      }
      if (!bMatch) {
        it.remove();
      }
    }
    return (List<Item>) list;
  }

}
