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

// $Id: LayoutUtil.java,v 1.11 2005/02/16 11:28:12 jesper Exp $
package net.infonode.gui.layout;

import java.awt.*;

public class LayoutUtil {
	private LayoutUtil() {
	}

	public static Component[] getVisibleChildren(Container parent) {
		return getVisibleChildren(parent.getComponents());
	}

	public static Component[] getVisibleChildren(Component[] components) {
		int count = 0;

		for (int i = 0; i < components.length; i++)
			if (components[i].isVisible())
				count++;

		Component[] c = new Component[count];
		int index = 0;

		for (int i = 0; i < components.length; i++)
			if (components[i].isVisible())
				c[index++] = components[i];

		return c;
	}

	public static Dimension getInteriorSize(Container container) {
		Insets insets = container.getInsets();
		return new Dimension(container.getWidth() - insets.left - insets.right,
				container.getHeight() - insets.top - insets.bottom);
	}

	public static boolean isDescendingFrom(Component component, Component parent) {
		return component == parent || (component != null && isDescendingFrom(component.getParent(), parent));
	}

	public static Dimension getMaxMinimumSize(Component[] components) {
		int maxWidth = 0;
		int maxHeight = 0;

		for (int i = 0; i < components.length; i++) {
			if (components[i] != null) {
				Dimension min = components[i].getMinimumSize();
				int w = min.width;
				int h = min.height;

				if (maxHeight < h)
					maxHeight = h;

				if (maxWidth < w)
					maxWidth = w;
			}
		}

		return new Dimension(maxWidth, maxHeight);
	}

	public static Dimension getMaxPreferredSize(Component[] components) {
		int maxWidth = 0;
		int maxHeight = 0;

		for (int i = 0; i < components.length; i++) {
			if (components[i] != null) {
				Dimension min = components[i].getPreferredSize();
				int w = min.width;
				int h = min.height;

				if (maxHeight < h)
					maxHeight = h;

				if (maxWidth < w)
					maxWidth = w;
			}
		}

		return new Dimension(maxWidth, maxHeight);
	}

	public static Dimension getMinMaximumSize(Component[] components) {
		int minWidth = Integer.MAX_VALUE;
		int minHeight = Integer.MAX_VALUE;

		for (int i = 0; i < components.length; i++) {
			if (components[i] != null) {
				Dimension min = components[i].getMaximumSize();
				int w = min.width;
				int h = min.height;

				if (minWidth > w)
					minWidth = w;

				if (minHeight > h)
					minHeight = h;
			}
		}

		return new Dimension(minWidth, minHeight);
	}

	public static Dimension add(Dimension dim, Insets insets) {
		return new Dimension(dim.width + insets.left + insets.right, dim.height + insets.top + insets.bottom);
	}
}
