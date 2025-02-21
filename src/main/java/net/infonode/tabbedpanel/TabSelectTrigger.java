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

// $Id: TabSelectTrigger.java,v 1.7 2004/09/28 14:50:49 jesper Exp $

package net.infonode.tabbedpanel;

/**
 * TabSelectTrigger defines what triggers a tab selection in a TabbedPanel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 * @see TabbedPanel
 * @since ITP 1.1.0
 */
public enum TabSelectTrigger {
	/**
	 * Mouse press select trigger. This means that a tab will be selected on mouse pressed (button down).
	 */
	MOUSE_PRESS,

	/**
	 * Mouse release select trigger. This means that a tab will be selected on mouse release (button up).
	 */
	MOUSE_RELEASE
}