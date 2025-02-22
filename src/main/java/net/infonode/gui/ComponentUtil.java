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

// $Id: ComponentUtil.java,v 1.25 2005/12/04 13:46:04 jesper Exp $

package net.infonode.gui;

import javax.swing.*;
import java.awt.*;

public class ComponentUtil {
	private ComponentUtil() {
	}

	public static int getComponentIndex(Component component) {
		if (component != null && component.getParent() != null) {
			Container c = component.getParent();
			for (int i = 0; i < c.getComponentCount(); i++) {
				if (c.getComponent(i) == component)
					return i;
			}
		}

		return -1;
	}

	public static Color getBackgroundColor(Component component) {
		if (component == null)
			return null;

		return component.isOpaque() ? component.getBackground() : getBackgroundColor(component.getParent());
	}

	public static int getVisibleChildrenCount(Component c) {
		if (!(c instanceof Container))
			return 0;

		int count = 0;
		Container container = (Container) c;

		for (int i = 0; i < container.getComponentCount(); i++)
			if (container.getComponent(i).isVisible())
				count++;

		return count;
	}

	public static boolean hasVisibleChildren(Component c) {
		return getVisibleChildrenCount(c) > 0;
	}

	public static boolean isOnlyVisibleComponent(Component c) {
		return c != null && c.isVisible() && getVisibleChildrenCount(c.getParent()) == 1;
	}

	/**
	 * Requests focus unless the component already has focus. For some weird reason calling
	 * {@link Component#requestFocusInWindow()}when the component is focus owner changes focus owner to another
	 * component!
	 *
	 * @param component the component to request focus for
	 * @return true if the component has focus or probably will get focus, otherwise false
	 */
	public static boolean requestFocus(Component component) {
		return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == component
				|| component.requestFocusInWindow();
	}

	/**
	 * Requests focus for a component. If that's not possible it's {@link FocusTraversalPolicy}is checked. If that
	 * doesn't work all it's children is recursively checked with this method.
	 *
	 * @param component the component to request focus for
	 * @return the component which has focus or probably will obtain focus, null if no component will receive focus
	 */
	public static Component smartRequestFocus(Component component) {
		if (requestFocus(component))
			return component;

		if (component instanceof JComponent) {
			FocusTraversalPolicy policy = ((JComponent) component).getFocusTraversalPolicy();

			if (policy != null) {
				Component focusComponent = policy.getDefaultComponent((Container) component);

				if (focusComponent != null && requestFocus(focusComponent)) {
					return focusComponent;
				}
			}
		}

		if (component instanceof Container) {
			Component[] children = ((Container) component).getComponents();

			for (int i = 0; i < children.length; i++) {
				component = smartRequestFocus(children[i]);

				if (component != null)
					return component;
			}
		}

		return null;
	}

	/**
	 * Calculates preferred max height for the given components without checking isVisible.
	 *
	 * @param components Components to check
	 * @return max height
	 */
	public static int getPreferredMaxHeight(Component[] components) {
		int height = 0;
		for (int i = 0; i < components.length; i++) {
			int k = (int) components[i].getPreferredSize().getHeight();
			if (k > height)
				height = k;
		}
		return height;
	}

	public static void validate(Component c) {
		if (c instanceof JComponent)
			c.revalidate();
		else
			c.validate();
	}
}