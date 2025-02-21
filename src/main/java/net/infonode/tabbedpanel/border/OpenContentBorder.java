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

// $Id: OpenContentBorder.java,v 1.31 2009/02/24 13:49:23 jesper Exp $
package net.infonode.tabbedpanel.border;

import java.awt.*;
import java.io.Serializable;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.infonode.gui.colorprovider.ColorProvider;
import net.infonode.gui.colorprovider.FixedColorProvider;
import net.infonode.gui.colorprovider.UIManagerColorProvider;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedUtils;

/**
 * <p>
 * OpenContentBorder is a border that draws a 1 pixel wide line border around a component that is used as content area
 * component in a tabbed panel. The border also optionally draws a highlight inside the line on the top and left sides
 * of the component. It is open, i.e. no content border will be drawn where the highlighted tab in the tabbed panel is
 * located.
 * </p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.31 $
 * @see TabbedPanel
 */
public class OpenContentBorder implements Border, Serializable {
	private static final long serialVersionUID = 1;

	private final ColorProvider lineColor;
	private final ColorProvider highlightColorProvider;

	/**
	 * Constructs a OpenContentBorder with highlight and with the given colors as line color and highlight color.
	 *
	 * @param color          the line color
	 * @param highlightColor the highlight color
	 */
	public OpenContentBorder(Color color, Color highlightColor) {
		this.lineColor = color == null ? UIManagerColorProvider.TABBED_PANE_DARK_SHADOW : new FixedColorProvider(color);
		this.highlightColorProvider = highlightColor == null ? null : new FixedColorProvider(highlightColor);
	}

	private static Point getTabBounds(Component c, Tab tab, int x) {
		Rectangle r = tab.getVisibleRect();
		r = SwingUtilities.convertRectangle(tab, r, c);
		int start = Math.max(x, r.x);
		int end = start + r.width - 1;
		return new Point(start, end);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		TabbedPanel tabbedPanel = TabbedUtils.getParentTabbedPanelContentPanel(c).getTabbedPanel();

		if (c != null && tabbedPanel != null) {
			Tab tab = tabbedPanel.getHighlightedTab();
			int tabStart = -1;
			int tabEnd = -1;
			int clipOffset = 0;
			if (tab != null) {
				Point p = getTabBounds(c, tab, x);
				tabStart = p.x;
				tabEnd = p.y;

				Rectangle visible = tab.getVisibleRect();
				int tabWidth = (int) visible.getWidth();
				clipOffset = tab.getWidth() > tabWidth ? -1 : 0;
			}

			Color color = lineColor.getColor(c);
			Color hc1 = highlightColorProvider == null ? null : highlightColorProvider.getColor(c);

			if (color != null) {
				g.setColor(color);
				drawLine(g, x, y, x + width - 1, y);
			}

			if (highlightColorProvider != null) {
				g.setColor(hc1);
				drawLine(g, x + 1, y + 1, x + width - 3, y + 1);
			}

			if (color != null) {
				g.setColor(color);
				drawLine(g, x, y + 1, x, y + height - 1);
			}

			if (highlightColorProvider != null) {
				g.setColor(hc1);
				drawLine(g, x + 1, y + 2, x + 1, y + height - tabStart == 0 ? 1 : 3);
			}

			if (color != null) {
				g.setColor(color);

				drawLine(g, x + width - 1, y + 1, x + width - 1, y + height - 1);

				if (tab != null) {
					g.setColor(color);
					drawLine(g, x + 1, y + height - 1, tabStart - 1 + 1, y + height - 1);
					drawLine(g, tabEnd - clipOffset, y + height - 1, x + width - 2, y + height - 1);
				} else {
					drawLine(g, x + 1, y + height - 1, x + width - 2, y + height - 1);
				}
			}
		}
	}

	private static void drawLine(Graphics g, int x1, int y1, int x2, int y2) {
		if (x2 < x1 || y2 < y1)
			return;

		g.drawLine(x1, y1, x2, y2);
	}

	public Insets getBorderInsets(Component c) {
		int hInset = highlightColorProvider != null ? 1 : 0;
		return new Insets(1 + hInset, 1 + hInset, 1, 1);
	}

	public boolean isBorderOpaque() {
		return true;
	}

}
