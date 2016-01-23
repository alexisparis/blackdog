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

package org.jajuk.services.events;

import java.util.Set;

import org.jajuk.util.EventSubject;

/**
 * GoF Observer pattern Observer
 */
public interface Observer {

  /**
   * Action to be done when receiving an event with this ID
   * 
   * @param lEventID
   *          Event ID, maps a subject and details
   */
  public void update(Event event);

  public Set<EventSubject> getRegistrationKeys();
}
