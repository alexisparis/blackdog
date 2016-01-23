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
 *  $Revision: 3454 $
 */

package org.jajuk.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.collections.bidimap.TreeBidiMap;
import org.jajuk.util.Filter;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.Messages;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.log.Log;

/**
 * Managers parent class
 */
public abstract class ItemManager implements ITechnicalStrings {

  /** Items collection* */
  protected TreeBidiMap hmItems = new TreeBidiMap();

  /**
   * Maps item classes -> instance, must be a linked map for ordering (mandatory
   * in commited collection)
   */
  static private LinkedHashMap<Class, ItemManager> hmItemManagers = new LinkedHashMap<Class, ItemManager>(
      10);

  /** Maps properties meta information name and object */
  private LinkedHashMap<String, PropertyMetaInformation> hmPropertiesMetaInformation = new LinkedHashMap<String, PropertyMetaInformation>(
      10);

  /** Manager lock, should be synchronized before any iteration on items */
  private byte[] bLock = new byte[0];

  /**
   * Constructor
   */
  ItemManager() {
    super();
  }

  /**
   * Registrates a new item manager
   * 
   * @param c
   *          Managed item class
   * @param itemManager
   */
  public static void registerItemManager(Class c, ItemManager itemManager) {
    hmItemManagers.put(c, itemManager);
  }

  /**
   * @return identifier used for XML generation
   */
  abstract public String getLabel();

  /**
   * @param sPropertyName
   * @return meta data for given property
   */
  public PropertyMetaInformation getMetaInformation(String sPropertyName) {
    return hmPropertiesMetaInformation.get(sPropertyName);
  }

  /**
   * Return a human representation for a given property name when we don't now
   * item type we work on. Otherwise, use PropertyMetaInformation.getHumanType
   * 
   * @param s
   * @return
   */
  public static String getHumanType(String sKey) {
    String sOut = sKey;
    if (Messages.getInstance().contains(PROPERTY_SEPARATOR + sKey)) {
      return Messages.getString(PROPERTY_SEPARATOR + sKey);
    }
    return sOut;
  }

  /** Remove a property * */
  public void removeProperty(String sProperty) {
    PropertyMetaInformation meta = getMetaInformation(sProperty);
    hmPropertiesMetaInformation.remove(sProperty);
    applyRemoveProperty(meta); // remove this property to all items
  }

  /** Remove a custom property to all items for the given manager */
  @SuppressWarnings("unchecked")
  public void applyRemoveProperty(PropertyMetaInformation meta) {
    synchronized (getLock()) {
      Collection<Item> items = hmItems.values();
      if (items != null) {
        for (Item item : items) {
          item.removeProperty(meta.getName());
        }
      }
    }
  }

  /** Add a custom property to all items for the given manager */
  @SuppressWarnings("unchecked")
  public void applyNewProperty(PropertyMetaInformation meta) {
    synchronized (getLock()) {
      Collection<Item> items = hmItems.values();
      if (items != null) {
        for (Item item : items) {
          item.setProperty(meta.getName(), meta.getDefaultValue());
        }
      }
    }
  }

  /**
   * 
   * @return XML representation of this manager
   */
  public String toXML() {
    StringBuilder sb = new StringBuilder("\t<").append(getLabel() + ">");
    Iterator it = hmPropertiesMetaInformation.keySet().iterator();
    while (it.hasNext()) {
      String sProperty = (String) it.next();
      PropertyMetaInformation meta = hmPropertiesMetaInformation.get(sProperty);
      sb.append('\n' + meta.toXML());
    }
    return sb.append('\n').toString();
  }

  /**
   * @return properties Meta informations
   */
  public Collection<PropertyMetaInformation> getProperties() {
    return hmPropertiesMetaInformation.values();
  }

  /**
   * @return custom properties Meta informations
   */
  public Collection<PropertyMetaInformation> getCustomProperties() {
    ArrayList<PropertyMetaInformation> col = new ArrayList<PropertyMetaInformation>();
    Iterator it = hmPropertiesMetaInformation.values().iterator();
    while (it.hasNext()) {
      PropertyMetaInformation meta = (PropertyMetaInformation) it.next();
      if (meta.isCustom()) {
        col.add(meta);
      }
    }
    return col;
  }

  /**
   * @return visible properties Meta informations
   */
  public Collection getVisibleProperties() {
    ArrayList<PropertyMetaInformation> col = new ArrayList<PropertyMetaInformation>();
    Iterator it = hmPropertiesMetaInformation.values().iterator();
    while (it.hasNext()) {
      PropertyMetaInformation meta = (PropertyMetaInformation) it.next();
      if (meta.isVisible()) {
        col.add(meta);
      }
    }
    return col;
  }

  /**
   * Get ItemManager manager with a given attribute name
   * 
   * @param sItem
   * @return
   */
  public static ItemManager getItemManager(String sProperty) {
    if (XML_DEVICE.equals(sProperty)) {
      return DeviceManager.getInstance();
    } else if (XML_TRACK.equals(sProperty)) {
      return TrackManager.getInstance();
    } else if (XML_ALBUM.equals(sProperty)) {
      return AlbumManager.getInstance();
    } else if (XML_AUTHOR.equals(sProperty)) {
      return AuthorManager.getInstance();
    } else if (XML_STYLE.equals(sProperty)) {
      return StyleManager.getInstance();
    } else if (XML_DIRECTORY.equals(sProperty)) {
      return DirectoryManager.getInstance();
    } else if (XML_FILE.equals(sProperty)) {
      return FileManager.getInstance();
    } else if (XML_PLAYLIST_FILE.equals(sProperty)) {
      return PlaylistFileManager.getInstance();
    } else if (XML_PLAYLIST.equals(sProperty)) {
      return PlaylistManager.getInstance();
    } else if (XML_TYPE.equals(sProperty)) {
      return TypeManager.getInstance();
    } else {
      return null;
    }
  }

  /**
   * Get ItemManager manager for given item class
   * 
   * @param class
   * @return associated item manager or null if none was found
   */
  public static ItemManager getItemManager(Class c) {
    return hmItemManagers.get(c);
  }

  /**
   * Return an iteration over item managers
   */
  public static Iterator getItemManagers() {
    return hmItemManagers.values().iterator();
  }

  /**
   * Perform an cleanup : delete useless items
   */
  public void cleanup() {
    synchronized (getLock()) {
      // build used items set
      Set<Item> hsItems = new HashSet<Item>(1000);
      for (Item item : TrackManager.getInstance().getTracks()) {
        Track track = (Track) item;
        if (this instanceof AlbumManager) {
          hsItems.add(track.getAlbum());
        } else if (this instanceof AuthorManager) {
          hsItems.add(track.getAuthor());
        } else if (this instanceof StyleManager) {
          hsItems.add(track.getStyle());
        }
      }
      Iterator it = hmItems.values().iterator();
      while (it.hasNext()) {
        Item item = (Item) it.next();
        // check if this item still maps some tracks
        if (!hsItems.contains(item)) {
          // For styles, keep it even if none track uses it if it is a
          // default style
          if (!(this instanceof StyleManager && !StyleManager.getInstance().getStylesList()
              .contains(item.getName()))) {
            it.remove();
          }
        }
      }
    }
  }

  /**
   * Perform a cleanup for a given item
   */
  public void cleanup(Item item) {
    synchronized (getLock()) {
      if (TrackManager.getInstance().getAssociatedTracks(item).size() == 0) {
        hmItems.remove(item.getID());
      }
    }
  }

  /** Return all registred items with filter applied */
  @SuppressWarnings("unchecked")
  public Collection<Item> getItems(Filter filter) {
    synchronized (getLock()) {
      return Filter.filterItems(new ArrayList<Item>(hmItems.values()), filter);
    }
  }

  /** Remove a given item */
  public synchronized void removeItem(String sID) {
    synchronized (getLock()) {
      hmItems.remove(sID);
    }
  }

  /**
   * Register a new property
   * 
   * @param meta
   */
  public void registerProperty(PropertyMetaInformation meta) {
    hmPropertiesMetaInformation.put(meta.getName(), meta);
  }

  /**
   * Change any item
   * 
   * @param itemToChange
   * @param sKey
   * @param oValue
   * @param filter:
   *          files we want to deal with
   * @return the changed item
   */
  public static Item changeItem(Item itemToChange, String sKey, Object oValue, HashSet filter)
      throws JajukException {
    if (Log.isDebugEnabled()) {
      Log.debug("Set " + sKey + "=" + oValue.toString() + " to " + itemToChange);
    }
    Item newItem = itemToChange;
    if (itemToChange instanceof File) {
      File file = (File) itemToChange;
      if (XML_NAME.equals(sKey)) { // file name
        newItem = FileManager.getInstance().changeFileName((File) itemToChange, (String) oValue);
      } else if (XML_TRACK.equals(sKey)) { // track name
        newItem = TrackManager.getInstance().changeTrackName(file.getTrack(), (String) oValue,
            filter);
      } else if (XML_STYLE.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackStyle(file.getTrack(), (String) oValue,
            filter);
      } else if (XML_ALBUM.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackAlbum(file.getTrack(), (String) oValue,
            filter);
      } else if (XML_AUTHOR.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackAuthor(file.getTrack(), (String) oValue,
            filter);
      } else if (XML_TRACK_COMMENT.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackComment(file.getTrack(), (String) oValue,
            filter);
      } else if (XML_TRACK_ORDER.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackOrder(file.getTrack(), (Long) oValue,
            filter);
      } else if (XML_YEAR.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackYear(file.getTrack(),
            String.valueOf(oValue), filter);
      } else if (XML_TRACK_RATE.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackRate(file.getTrack(), (Long) oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
      // Get associated track file
      if (newItem instanceof Track) {
        file.setTrack((Track) newItem);
        newItem = file;
      }
    } else if (itemToChange instanceof PlaylistFile) {
      if (XML_NAME.equals(sKey)) { // playlistfile name
        newItem = PlaylistFileManager.getInstance().changePlaylistFileName(
            (PlaylistFile) itemToChange, (String) oValue);
      }
    } else if (itemToChange instanceof Directory) {
      if (XML_NAME.equals(sKey)) { // file name
        // TBI newItem =
        // DirectoryManager.getInstance().changeDirectoryName((Directory)itemToChange,(String)oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
    } else if (itemToChange instanceof Device) {
      itemToChange.setProperty(sKey, oValue);
    } else if (itemToChange instanceof Playlist) {
      itemToChange.setProperty(sKey, oValue);
    } else if (itemToChange instanceof Track) {
      if (XML_NAME.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackName((Track) itemToChange, (String) oValue,
            filter);
      } else if (XML_STYLE.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackStyle((Track) itemToChange,
            (String) oValue, filter);
      } else if (XML_ALBUM.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackAlbum((Track) itemToChange,
            (String) oValue, filter);
      } else if (XML_AUTHOR.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackAuthor((Track) itemToChange,
            (String) oValue, filter);
      } else if (XML_TRACK_COMMENT.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackComment((Track) itemToChange,
            (String) oValue, filter);
      } else if (XML_TRACK_ORDER.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackOrder((Track) itemToChange, (Long) oValue,
            filter);
      } else if (XML_YEAR.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackYear((Track) itemToChange,
            String.valueOf(oValue), filter);
      } else if (XML_TRACK_RATE.equals(sKey)) {
        newItem = TrackManager.getInstance().changeTrackRate((Track) itemToChange, (Long) oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
    } else if (itemToChange instanceof Album) {
      if (XML_NAME.equals(sKey)) {
        newItem = AlbumManager.getInstance().changeAlbumName((Album) itemToChange, (String) oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
    } else if (itemToChange instanceof Author) {
      if (XML_NAME.equals(sKey)) {
        newItem = AuthorManager.getInstance().changeAuthorName((Author) itemToChange,
            (String) oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
    } else if (itemToChange instanceof Style) {
      if (XML_NAME.equals(sKey)) {
        newItem = StyleManager.getInstance().changeStyleName((Style) itemToChange, (String) oValue);
      } else { // others properties
        itemToChange.setProperty(sKey, oValue);
      }
    } else if (itemToChange instanceof Year) {
      itemToChange.setProperty(sKey, oValue);
    }
    return newItem;
  }

  /**
   * 
   * @return MUTEX used to avoid concurrent access to items
   */
  public byte[] getLock() {
    return bLock;
  }

  /**
   * 
   * @return number of item
   */
  public int getElementCount() {
    synchronized (getLock()) {
      return hmItems.size();
    }
  }

  /**
   * @param sID
   *          Item ID
   * @return Item
   */
  public Item getItemByID(String sID) {
    synchronized (getLock()) {
      return (Item) hmItems.get(sID);
    }
  }

  /** Return all registred items */
  @SuppressWarnings("unchecked")
  protected Set<Item> getItems() {
    synchronized (getLock()) {
      return hmItems.inverseBidiMap().keySet();
    }
  }

}
