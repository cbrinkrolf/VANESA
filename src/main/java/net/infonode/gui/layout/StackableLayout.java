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

// $Id: StackableLayout.java,v 1.23 2005/12/04 13:46:03 jesper Exp $
package net.infonode.gui.layout;

import net.infonode.gui.ComponentUtil;

import java.awt.*;

public class StackableLayout implements LayoutManager2 {
	private Component component;

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return LayoutUtil.add(LayoutUtil.getMinMaximumSize(target.getComponents()), target.getInsets());
	}

	@Override
	public void invalidateLayout(Container target) {
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		comp.setVisible(false);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		comp.setVisible(false);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		if (comp == component) {
			component = null;
		}
		comp.setVisible(true);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return LayoutUtil.add(LayoutUtil.getMaxPreferredSize(parent.getComponents()), parent.getInsets());
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(0, 0);
	}

	@Override
	public void layoutContainer(Container parent) {
		Insets parentInsets = parent.getInsets();
		Dimension size = LayoutUtil.getInteriorSize(parent);
		for (Component value : parent.getComponents()) {
			value.setBounds(parentInsets.left, parentInsets.top, size.width, size.height);
		}
	}

	public Component getVisibleComponent() {
		return component;
	}

	public void showComponent(Component c) {
		if (component == c) {
			return;
		}
		boolean hasFocus = component != null && LayoutUtil.isDescendingFrom(
				KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), component);
		if (component != null) {
			component.setVisible(false);
		}
		component = c;
		if (component != null) {
			component.setVisible(true);
			if (hasFocus) {
				ComponentUtil.smartRequestFocus(component);
			}
		}
	}
}
