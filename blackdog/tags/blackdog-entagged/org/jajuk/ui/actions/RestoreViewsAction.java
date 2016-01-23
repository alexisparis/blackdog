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

import org.jajuk.ui.perspectives.IPerspective;
import org.jajuk.ui.perspectives.PerspectiveManager;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.IconLoader;
import org.jajuk.util.Messages;
import org.jajuk.util.error.JajukException;

public class RestoreViewsAction extends ActionBase implements ITechnicalStrings {

  private static final long serialVersionUID = 1L;

  RestoreViewsAction() {
    super(Messages.getString("JajukJMenuBar.17"), IconLoader.ICON_REFRESH, true);
    setShortDescription(Messages.getString("JajukJMenuBar.17"));
  }

  public void perform(final ActionEvent e) throws JajukException {
    new Thread() {
      public void run() {
        IPerspective perspective = PerspectiveManager.getCurrentPerspective();
        // Restore local or global views
        perspective.restoreDefaults();
      }
    }.start();

  }
}
