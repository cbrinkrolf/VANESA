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

// $Id: AbstractButtonIcon.java,v 1.10 2005/02/16 11:28:11 jesper Exp $
package net.infonode.gui.icon.button;

import net.infonode.gui.ComponentUtil;
import net.infonode.util.ColorUtil;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public abstract class AbstractButtonIcon implements Icon, Serializable {
	private static final long serialVersionUID = 1;

	private final int size;
	private Color defaultColor = null;
	private boolean enabled = true;

	public AbstractButtonIcon(Color color, int size) {
		this.defaultColor = color;
		this.size = size;
	}

	public AbstractButtonIcon(int size, boolean enabled) {
		this.size = size;
		this.enabled = enabled;
	}

	public int getIconWidth() {
		return size;
	}

	public int getIconHeight() {
		return size;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Color oldColor = g.getColor();
		Color color = defaultColor == null ? (enabled ? c.getForeground() : UIManager.getColor(
				"Button.disabledForeground")) : defaultColor;
		if (color == null)
			color = ColorUtil.blend(ComponentUtil.getBackgroundColor(c), c.getForeground(), 0.5f);

		g.setColor(color);
		paintIcon(c, g, x, y, x + size - 1, y + size - 1);

		g.setColor(oldColor);
	}

	protected void paintIcon(Component c, Graphics g, int x1, int y1, int x2, int y2) {
	}
}
