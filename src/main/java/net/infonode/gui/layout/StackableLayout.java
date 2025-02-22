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
	private final Container container;
	private Component component;
	private final boolean autoShowFirstComponent;
	private boolean useSelectedComponentSize;

	public StackableLayout(Container container, boolean autoShowFirstComponent) {
		this.container = container;
		this.autoShowFirstComponent = autoShowFirstComponent;
	}

	public void setUseSelectedComponentSize(boolean useSelectedComponentSize) {
		if (this.useSelectedComponentSize != useSelectedComponentSize) {
			this.useSelectedComponentSize = useSelectedComponentSize;
			ComponentUtil.validate(container);
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		return LayoutUtil.add(LayoutUtil.getMinMaximumSize(target.getComponents()), target.getInsets());
	}

	public void invalidateLayout(Container target) {
	}

	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		comp.setVisible(autoShowFirstComponent && comp.getParent().getComponentCount() == 1);
		if (comp.isVisible()) {
			component = comp;
		}
	}

	public void addLayoutComponent(String name, Component comp) {
		addLayoutComponent(comp, null);
	}

	public void removeLayoutComponent(Component comp) {
		if (comp == component) {
			component = null;
		}
		comp.setVisible(true);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return LayoutUtil.add(useSelectedComponentSize
				? component == null ? new Dimension(0, 0) : component.getPreferredSize()
				: LayoutUtil.getMaxPreferredSize(parent.getComponents()), parent.getInsets());
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(0, 0);
	}

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
		final Component oldComponent = component;
		component = c;
		boolean hasFocus = oldComponent != null && LayoutUtil.isDescendingFrom(
				KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner(), oldComponent);
		if (oldComponent != null) {
			oldComponent.setVisible(false);
		}
		if (component != null) {
			component.setVisible(true);
			if (hasFocus) {
				ComponentUtil.smartRequestFocus(component);
			}
		}
	}

}
