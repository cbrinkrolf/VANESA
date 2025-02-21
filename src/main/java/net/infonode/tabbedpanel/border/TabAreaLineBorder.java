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

// $Id: TabAreaLineBorder.java,v 1.19 2005/12/04 13:46:05 jesper Exp $
package net.infonode.tabbedpanel.border;

import net.infonode.gui.colorprovider.*;
import net.infonode.tabbedpanel.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.Serializable;

/**
 * TabAreaLineBorder draws a 1 pixel wide border on all sides except the side towards the content area of a tabbed
 * panel.
 *
 * @author $Author: jesper $
 * @author $Author: jesper $
 * @version $Revision: 1.19 $
 * @see Tab
 * @see TabbedPanel
 * @since ITP 1.1.0
 */
public class TabAreaLineBorder implements Border, Serializable {
	private static final long serialVersionUID = 1;

	private final ColorProvider color;

	/**
	 * Constructs a TabAreaLineBorder with color based on the look and feel
	 */
	public TabAreaLineBorder() {
		this(null);
	}

	/**
	 * Constructs a TabAreaLineBorder with the give color
	 *
	 * @param color color for the border
	 */
	public TabAreaLineBorder(Color color) {
		this.color = color == null ? new ColorProviderList(UIManagerColorProvider.TABBED_PANE_DARK_SHADOW,
				UIManagerColorProvider.CONTROL_DARK_SHADOW, FixedColorProvider.BLACK) : new FixedColorProvider(color);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Insets insets = getBorderInsets(c);
		g.setColor(color.getColor(c));

		if (insets.top == 1)
			g.drawLine(x, y, x + width - 1, y);

		if (insets.bottom == 1)
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

		if (insets.left == 1)
			g.drawLine(x, y, x, y + height - 1);

		if (insets.right == 1)
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
	}

	@Override
	public Insets getBorderInsets(Component c) {
		if (c instanceof JComponent && ((JComponent) c).getComponentCount() == 0)
			return new Insets(0, 0, 0, 0);
		return new Insets(0, 1, 1, 1);
	}
}
