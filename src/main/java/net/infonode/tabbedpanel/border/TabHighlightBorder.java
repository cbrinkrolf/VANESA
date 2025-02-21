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

// $Id: TabHighlightBorder.java,v 1.19 2005/12/04 13:46:05 jesper Exp $
package net.infonode.tabbedpanel.border;

import net.infonode.gui.colorprovider.ColorProvider;
import net.infonode.gui.colorprovider.FixedColorProvider;
import net.infonode.gui.colorprovider.UIManagerColorProvider;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedUtils;

import javax.swing.border.Border;
import java.awt.*;
import java.io.Serializable;

/**
 * TabHighlightBorder draws a 1 pixel wide highlight on the top and left side of the tab. It will not draw highlight on
 * the side towards a TabbedPanel's content area if the border is constructed with open border.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.19 $
 * @see Tab
 * @see TabbedPanel
 */
public class TabHighlightBorder implements Border, Serializable {
	private static final long serialVersionUID = 1;

	private final ColorProvider color;
	private final boolean openBorder;

	/**
	 * Constructs a TabHighlightBorder with the given color as highlight color
	 *
	 * @param color      the highlight color
	 * @param openBorder when true, no highlighting is drawn on the side towards a TabbedPanel's content area, otherwise
	 *                   false
	 */
	public TabHighlightBorder(Color color, boolean openBorder) {
		this.color = color == null ? UIManagerColorProvider.TABBED_PANE_HIGHLIGHT : new FixedColorProvider(color);
		this.openBorder = openBorder;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		TabbedPanel tabbedPanel = TabbedUtils.getParentTabbedPanel(c);

		if (tabbedPanel != null) {
			g.setColor(color.getColor(c));
			if (!openBorder)
				g.drawLine(x + 1, y, x + width - 2, y);
			g.drawLine(x, y, x, y + height - 2);
		}
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 0, 0);
	}

	public boolean isBorderOpaque() {
		return false;
	}
}
