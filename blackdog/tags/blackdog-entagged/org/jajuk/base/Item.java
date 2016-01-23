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

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;

import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.Util;
import org.jajuk.util.log.Log;
import org.xml.sax.Attributes;

/**
 * Generic property handler, parent class for all items
 * <p>
 * Note that some properties can be omitted (not in properties object), in this
 * case, we take default value given in meta infos, this can decrease collection
 * file size
 * </p>
 */
abstract public class Item implements Serializable, ITechnicalStrings {

  /**
   * We cache the ID to avoid getting it from properties for CPU performance
   * reasons
   */
  String sID;

  /**
   * We cache the name to avoid getting it from properties for CPU performance
   * reasons
   */
  String name;

  /**
   * Item properties, singleton use very high load factor as this size will not
   * change often
   */
  private LinkedHashMap<String, Object> properties = new LinkedHashMap<String, Object>(2, 1f);

  /**
   * Constructor
   * 
   * @param sId
   *          element ID
   * @param sName
   *          element name
   */
  Item(final String sId, final String sName) {
    this.sID = sId;
    setProperty(XML_ID, sId);
    this.name = sName;
    setProperty(XML_NAME, sName);
  }

  /**
   * @return
   */
  public String getID() {
    return this.sID;
  }

  /**
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Item hashcode (used by the equals method)
   */
  public int hashCode() {
    return getID().hashCode();
  }

  /**
   * Get item description (HTML)
   * 
   * @return item description
   */
  abstract public String getDesc();

  /**
   * Equal method to check two albums are identical
   * 
   * @param otherAlbum
   * @return
   */
  public boolean equals(Object otherItem) {
    if (otherItem == null || !(otherItem instanceof Item)) {
      return false;
    }
    return getID().equals(((Item) otherItem).getID());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#getProperties()
   */
  public LinkedHashMap<String, Object> getProperties() {
    return properties;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#getProperty(java.lang.String)
   */
  public Object getValue(String sKey) {
    Object out = getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return getDefaultValue(sKey);
    }
    return out;
  }

  public long getLongValue(String sKey) {
    Long out = (Long) getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return (Long) getDefaultValue(sKey);
    }
    return out;
  }

  public double getDoubleValue(String sKey) {
    Double out = (Double) getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return (Double) getDefaultValue(sKey);
    }
    return out;
  }

  /**
   * Return String value for String type values. We assume that given property
   * is a String. If you are not sure, use Util.parse method
   */
  public String getStringValue(String sKey) {
    String out = (String) getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return (String) getDefaultValue(sKey);
    }
    return out;
  }

  public boolean getBooleanValue(String sKey) {
    Boolean out = (Boolean) getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return (Boolean) getDefaultValue(sKey);
    }
    return out;
  }

  public Date getDateValue(String sKey) {
    Date out = (Date) getProperties().get(sKey);
    // look at properties to check the given property is known
    if (out == null) {
      // no? take property default
      return (Date) getDefaultValue(sKey);
    }
    return out;
  }

  public Object getDefaultValue(String sKey) {
    PropertyMetaInformation meta = getMeta(sKey);
    return meta.getDefaultValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#containsKey(java.lang.String)
   */
  public boolean containsProperty(String sKey) {
    return properties.containsKey(sKey) && properties.get(sKey) != null
        && !properties.get(sKey).equals("");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#setProperty(java.lang.String, java.lang.String)
   */
  public void setProperty(String sKey, Object oValue) {
    getProperties().put(sKey, oValue);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#getAny()
   */
  public String getAny() {
    StringBuilder sb = new StringBuilder(100);
    LinkedHashMap properties = getProperties();
    Iterator it = properties.keySet().iterator();
    while (it.hasNext()) {
      String sKey = (String) it.next();
      String sValue = getHumanValue(sKey);
      if (sValue != null) {
        PropertyMetaInformation meta = getMeta(sKey);
        if (!meta.isVisible()) { // computes "any" only on
          // visible items
          continue;
        }
        sb.append(sValue);
      }

    }
    return sb.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#setDefaultProperty(java.lang.String,
   *      java.lang.String)
   */
  public void populateDefaultProperty(PropertyMetaInformation meta) {
    properties.put(meta.getName(), meta.getDefaultValue());
  }

  /**
   * Return an XML representation of this item
   * 
   * @return
   */
  public String toXml() {
    try {
      StringBuilder sb = new StringBuilder("\t\t<").append(getLabel());
      sb.append(getPropertiesXml());
      sb.append("/>\n");
      return sb.toString();
    } catch (Exception e) {
      // catch any error here because it can prevent
      // collection to commit
      Log.error(e);
      return "";
    }
  }

  /**
   * @return an identifier used to generate XML representation of this item
   */
  abstract String getLabel();

  /**
   * 
   * @return XML representation for item properties
   */
  private String getPropertiesXml() {
    LinkedHashMap<String, Object> properties = getProperties();
    StringBuilder sb = new StringBuilder();
    for (String sKey : properties.keySet()) {
      String sValue = null;
      Object oValue = properties.get(sKey);
      if (oValue != null) {
        PropertyMetaInformation meta = getMeta(sKey);
        try {
          sValue = Util.format(oValue, meta, false);
        } catch (Exception e) { // should not occur
          Log.error(e);
        }
        sValue = Util.formatXML(sValue); // make sure to remove
        // non-XML characters
      }
      sb.append(' ');
      sb.append(Util.formatXML(sKey));
      sb.append("='");
      sb.append(sValue);
      sb.append("'");
    }
    return sb.toString();
  }

  /**
   * Set all personnal properties of an XML file for an item (doesn't overwrite
   * existing properties for perfs)
   * 
   * @param attributes :
   *          list of attributes for this XML item
   */
  public void populateProperties(Attributes attributes) {
    for (int i = 0; i < attributes.getLength(); i++) {
      String sProperty = attributes.getQName(i);
      if (!properties.containsKey(sProperty)) {
        String sValue = attributes.getValue(i);
        PropertyMetaInformation meta = getMeta(sProperty);
        try {
          if (meta != null) {
            setProperty(sProperty, Util.parse(sValue, meta.getType()));
          }
        } catch (Exception e) {
          Log.error(137, sProperty, e);
        }
      }
    }
  }

  /**
   * @param properties
   *          The properties to set.
   */
  public void setProperties(LinkedHashMap<String, Object> properties) {
    this.properties = properties;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#removeProperty(java.lang.String)
   */
  public void removeProperty(String sKey) {
    LinkedHashMap properties = getProperties();
    if (properties.containsKey(sKey)) {
      properties.remove(sKey);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Item#displayProperty()
   */
  public void displayProperties() {
  }

  /**
   * Default implementation for this method, simply return standard value
   */
  public String getHumanValue(String sKey) {
    try {
      return Util.format(getValue(sKey), getMeta(sKey), true);
    } catch (Exception e) {
      Log.error(e);
      return "";
    }
  }

  /**
   * @param sProperty
   *          Property name
   * @return Meta information for current item and given property name
   */
  public PropertyMetaInformation getMeta(String sProperty) {
    return ItemManager.getItemManager(this.getClass()).getMetaInformation(sProperty);
  }

  /**
   * Clone all properties from a given properties list but not overwrite
   * constructor properties
   * 
   * @param propertiesSource
   */
  public void cloneProperties(Item propertiesSource) {
    Iterator it = propertiesSource.getProperties().keySet().iterator();
    while (it.hasNext()) {
      String sProperty = (String) it.next();
      if (!getMeta(sProperty).isConstructor()) {
        this.properties.put(sProperty, propertiesSource.getValue(sProperty));
      }
    }
  }

  /**
   * @return an icon representation for this item or null if none available
   */
  abstract public ImageIcon getIconRepresentation();
}
