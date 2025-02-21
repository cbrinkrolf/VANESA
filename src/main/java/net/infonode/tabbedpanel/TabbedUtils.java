/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R)
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

// $Id: TabbedUtils.java,v 1.10 2005/02/16 11:28:15 jesper Exp $
package net.infonode.tabbedpanel;

import java.awt.*;

/**
 * Utility methods
 *
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 */
public class TabbedUtils {
	private TabbedUtils() {
	}

	/**
	 * Gets the tabbed panel for whom the given component is a child
	 *
	 * @param c the component
	 * @return the tabbed panel or null if component is not a child of any tabbed panel
	 */
	public static TabbedPanel getParentTabbedPanel(Component c) {
		while (c != null) {
			if (c instanceof TabbedPanel)
				return (TabbedPanel) c;
			c = c.getParent();
		}

		return null;
	}

	/**
	 * Gets the TabbedPanelContentPanel for whom the given component is a child
	 *
	 * @param c the component
	 * @return the content panel or null if component is not a child of any tabbed panel content panel
	 */
	public static TabbedPanelContentPanel getParentTabbedPanelContentPanel(Component c) {
		while (c != null) {
			if (c instanceof TabbedPanelContentPanel)
				return (TabbedPanelContentPanel) c;

			c = c.getParent();
		}

		return null;
	}
}
