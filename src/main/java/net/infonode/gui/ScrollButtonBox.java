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

// $Id: ScrollButtonBox.java,v 1.17 2005/12/04 13:46:04 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.icon.button.ArrowIcon;
import net.infonode.gui.icon.button.BorderIcon;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.panel.BaseContainer;
import net.infonode.tabbedpanel.TabbedUIDefaults;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ScrollButtonBox extends BaseContainer {
	private boolean button1Enabled;
	private boolean button2Enabled;

	private ArrayList<ScrollButtonBoxListener> listeners;

	public ScrollButtonBox() {
		super(false, new DirectionLayout(Direction.RIGHT));

		addMouseWheelListener(e -> {
			if (e.getWheelRotation() < 0)
				fireButton1();
			else
				fireButton2();
		});

		int iconSize = TabbedUIDefaults.getButtonIconSize();
		AbstractButton leftButton = ButtonFactory.createFlatHighlightButton(null, null, 0, null);
		leftButton.setIcon(new ArrowIcon(iconSize, Direction.LEFT));
		leftButton.setDisabledIcon(new BorderIcon(new ArrowIcon(iconSize - 2, Direction.LEFT, false), 1));
		AbstractButton rightButton = ButtonFactory.createFlatHighlightButton(null, null, 0, null);
		rightButton.setIcon(new ArrowIcon(iconSize, Direction.RIGHT));
		rightButton.setDisabledIcon(new BorderIcon(new ArrowIcon(iconSize - 2, Direction.RIGHT, false), 1));
		add(leftButton);
		add(rightButton);

		leftButton.setFocusable(false);
		rightButton.setFocusable(false);

		leftButton.setEnabled(button1Enabled);
		rightButton.setEnabled(button2Enabled);

		ActionListener button1Listener = e -> fireButton1();
		leftButton.addActionListener(button1Listener);
		ActionListener button2Listener = e -> fireButton2();
		rightButton.addActionListener(button2Listener);

		if (getParent() != null)
			ComponentUtil.validate(getParent());
	}

	public void setButton1Enabled(boolean enabled) {
		this.button1Enabled = enabled;
		if (getComponentCount() > 0)
			getComponent(0).setEnabled(enabled);
	}

	public void setButton2Enabled(boolean enabled) {
		this.button2Enabled = enabled;
		if (getComponentCount() > 0)
			getComponent(1).setEnabled(enabled);
	}

	public void addListener(ScrollButtonBoxListener listener) {
		if (listeners == null)
			listeners = new ArrayList<>(2);

		listeners.add(listener);
	}

	private void fireButton1() {
		if (listeners != null) {
			Object[] l = listeners.toArray();

			for (int i = 0; i < l.length; i++)
				((ScrollButtonBoxListener) l[i]).scrollButton1();
		}
	}

	private void fireButton2() {
		if (listeners != null) {
			Object[] l = listeners.toArray();

			for (int i = 0; i < l.length; i++)
				((ScrollButtonBoxListener) l[i]).scrollButton2();
		}
	}
}
