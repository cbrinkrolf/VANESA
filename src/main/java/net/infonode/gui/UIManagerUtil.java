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

// $Id: UIManagerUtil.java,v 1.6 2005/12/04 13:46:04 jesper Exp $
package net.infonode.gui;

import net.infonode.util.ColorUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class UIManagerUtil {
	private UIManagerUtil() {
	}

	public static Insets getInsets(String key, Insets insets) {
		Insets i = InsetsUtil.copy(UIManager.getInsets(key));
		return i == null ? insets : i;
	}

	public static Color getColor(String key, String defaultKey, Color defaultColor) {
		Color i = ColorUtil.copy(UIManager.getColor(key));

		if (i != null)
			return i;

		i = ColorUtil.copy(UIManager.getColor(defaultKey));
		return i == null ? defaultColor : i;
	}

	public static Color getColor(String key, Color defaultColor) {
		Color c = ColorUtil.copy(UIManager.getColor(key));
		return c == null ? defaultColor : c;
	}
}
