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
 *  $Revision: 3454 $
 */
package org.jajuk.ui.perspectives;

import java.awt.Container;
import java.util.Set;

import javax.swing.ImageIcon;

import org.jajuk.ui.views.IView;

/**
 * Representation of a perspective
 */

public interface IPerspective {

  /**
   * @return the perspective's id
   */
  public String getID();

  public abstract String getDesc();

  /**
   * Return the icon
   * 
   * @return perspective icon
   */
  public abstract ImageIcon getIcon();

  /**
   * Set icon
   * 
   * @param iconURL
   *          icon
   */
  public void setIcon(ImageIcon icon);

  /**
   * @return Arraylist views registered in the perspective.
   */
  public Set<IView> getViews();

  /**
   * @return Returns the desktop.
   */
  public Container getContentPane();

  /**
   * Serialize the perspective
   */
  public void commit() throws Exception;

  /**
   * Deserialize the perspective
   */
  public void load() throws Exception;

  /**
   * Restore defaults views
   */
  public void restoreDefaults();

  /**
   * As been selected flag
   */
  public void setAsBeenSelected(boolean b);

}