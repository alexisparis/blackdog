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
 *  $Revision: 3216 $
 */
package org.jajuk.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jajuk.util.log.Log;

/**
 * Manages types ( mp3, ogg...) supported by jajuk
 * <p>
 * static class
 */
public class TypeManager extends ItemManager {
  /** extenssions->types */
  private HashMap<String, Type> hmSupportedTypes = new HashMap<String, Type>(10);

  /** Self instance */
  private static TypeManager singleton;

  /**
   * No constructor available, only static access
   */
  private TypeManager() {
    super();
    // ---register properties---
    // ID
    registerProperty(new PropertyMetaInformation(XML_ID, false, true, false, false, false,
        String.class, null));
    // Name
    registerProperty(new PropertyMetaInformation(XML_NAME, false, true, true, false, false,
        String.class, null));
    // Extension
    registerProperty(new PropertyMetaInformation(XML_TYPE_EXTENSION, false, true, true, false,
        false, String.class, null));
    // Player impl
    registerProperty(new PropertyMetaInformation(XML_TYPE_PLAYER_IMPL, false, true, true, false,
        false, Class.class, null));
    // Tag impl
    registerProperty(new PropertyMetaInformation(XML_TYPE_TAG_IMPL, false, true, true, false,
        false, Class.class, null));
    // Music
    registerProperty(new PropertyMetaInformation(XML_TYPE_IS_MUSIC, false, false, true, false,
        false, Boolean.class, null));
    // Seek
    registerProperty(new PropertyMetaInformation(XML_TYPE_SEEK_SUPPORTED, false, false, true,
        false, false, Boolean.class, null));
    // Tech desc
    registerProperty(new PropertyMetaInformation(XML_TYPE_TECH_DESC, false, false, true, false,
        false, String.class, null));
    // Icon
    registerProperty(new PropertyMetaInformation(XML_TYPE_ICON, false, false, false, false, false,
        String.class, null));
  }

  /**
   * @return singleton
   */
  public static TypeManager getInstance() {
    if (singleton == null) {
      singleton = new TypeManager();
    }
    return singleton;
  }

  /**
   * Register a type jajuk can read
   * 
   * @param type
   */
  public Type registerType(String sName, String sExtension, Class cPlayerImpl, Class cTagImpl) {
    return registerType(sExtension, sName, sExtension, cPlayerImpl, cTagImpl);
  }

  /**
   * Register a type jajuk can read with a known id
   * 
   * @param type
   */
  @SuppressWarnings("unchecked")
  private Type registerType(String sId, String sName, String sExtension, Class cPlayerImpl,
      Class cTagImpl) {
    synchronized (TrackManager.getInstance().getLock()) {
      if (hmSupportedTypes.containsKey(sExtension)) {
        // if the type is already in memory, use it
        return hmSupportedTypes.get(sExtension);
      }
      Type type = null;
      try {
        type = new Type(sId, sName, sExtension, cPlayerImpl, cTagImpl);
        hmItems.put(sId, type);
        hmSupportedTypes.put(type.getExtension(), type);
      } catch (Exception e) {
        Log.error(109, "sPlayerImpl=" + cPlayerImpl + " sTagImpl=" + cTagImpl, e);
      }
      return type;
    }
  }

  /**
   * Tells if the type is supported
   * 
   * @param type
   * @return
   */
  public boolean isExtensionSupported(String sExt) {
    synchronized (TrackManager.getInstance().getLock()) {
      return hmSupportedTypes.containsKey(sExt);
    }
  }

  /**
   * Return type for a given extension
   * 
   * @param sExtension
   * @return
   */
  public Type getTypeByExtension(String sExtension) {
    synchronized (TrackManager.getInstance().getLock()) {
      return hmSupportedTypes.get(sExtension);
    }
  }

  /**
   * Return type for a given technical description
   * 
   * @param sTechDesc
   * @return associated type or null if none found
   */
  public Type getTypeByTechDesc(String sTechDesc) {
    synchronized (TrackManager.getInstance().getLock()) {
      Iterator it = hmSupportedTypes.values().iterator();
      while (it.hasNext()) {
        Type type = (Type) it.next();
        if (type.getStringValue(XML_TYPE_TECH_DESC).equalsIgnoreCase(sTechDesc)) {
          return type;
        }
      }
      return null;
    }
  }

  /**
   * Return all music types
   * 
   * @return
   */
  public ArrayList<Type> getAllMusicTypes() {
    synchronized (TrackManager.getInstance().getLock()) {
      ArrayList<Type> alResu = new ArrayList<Type>(5);
      Iterator it = hmSupportedTypes.values().iterator();
      while (it.hasNext()) {
        Type type = (Type) it.next();
        if (type.getBooleanValue(XML_TYPE_IS_MUSIC)) {
          alResu.add(type);
        }
      }
      return alResu;
    }
  }

  /**
   * Return a list "a,b,c" of registered extensions, used by FileChooser
   * 
   * @return
   */
  public String getTypeListString() {
    synchronized (TrackManager.getInstance().getLock()) {
      StringBuilder sb = new StringBuilder();
      Iterator it = hmSupportedTypes.keySet().iterator();
      while (it.hasNext()) {
        sb.append(it.next());
        sb.append(',');
      }
      sb.deleteCharAt(sb.length() - 1); // remove last ','
      return sb.toString();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.ItemManager#getIdentifier()
   */
  public String getLabel() {
    return XML_TYPES;
  }

  /**
   * @param sID
   *          Item ID
   * @return item
   */
  public Type getTypeByID(String sID) {
    synchronized (getLock()) {
      return (Type) hmItems.get(sID);
    }
  }

  /**
   * 
   * @return types list
   */
  public Set<Type> getTypes() {
    Set<Type> typeSet = new LinkedHashSet<Type>();
    synchronized (getLock()) {
      for (Item item : getItems()) {
        typeSet.add((Type) item);
      }
    }
    return typeSet;
  }

}
