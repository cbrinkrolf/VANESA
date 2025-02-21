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

// $Id: ButtonFactory.java,v 1.23 2005/12/04 13:46:04 jesper Exp $
package net.infonode.gui;

import net.infonode.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class ButtonFactory {
	private ButtonFactory() {
	}

	private static class ButtonHighlighter implements ComponentListener, HierarchyListener {
		private final JButton button;
		private final Border pressedBorder;
		private final Border highlightedBorder;
		private final Border normalBorder;
		private boolean rollover;
		private long rolloverStart; // Ugly hack to avoid false rollover callbacks which occur when the button is moved

		ButtonHighlighter(JButton button, int padding) {
			this.button = button;

			normalBorder = new EmptyBorder(padding + 2, padding + 2, padding + 2, padding + 2);
			pressedBorder = new EmptyBorder(padding + 2, padding + 2, padding, padding);
			highlightedBorder = new EmptyBorder(padding + 1, padding + 1, padding + 1, padding + 1);

			button.setContentAreaFilled(false);
			setNormalState();

			button.addChangeListener(e -> {
				rollover = (System.currentTimeMillis() - rolloverStart) > 20 && this.button.getModel().isRollover();
				update();

				if (ButtonHighlighter.this.button.getModel().isRollover())
					rolloverStart = 0;
			});

			button.addHierarchyListener(this);
			button.addComponentListener(this);
		}

		private void setNormalState() {
			button.setBackground(null);
			button.setOpaque(false);
			button.setBorder(normalBorder);
			rollover = false;
		}

		public void componentHidden(ComponentEvent e) {
			setNormalState();
			rolloverStart = System.currentTimeMillis();
		}

		public void componentMoved(ComponentEvent e) {
			setNormalState();
			rolloverStart = System.currentTimeMillis();
		}

		public void componentResized(ComponentEvent e) {
			setNormalState();
			rolloverStart = System.currentTimeMillis();
		}

		public void componentShown(ComponentEvent e) {
			setNormalState();
			rolloverStart = System.currentTimeMillis();
		}

		public void hierarchyChanged(HierarchyEvent e) {
			setNormalState();
			rolloverStart = System.currentTimeMillis();
		}

		private void update() {
			boolean pressed = button.getModel().isArmed();

			if (button.isEnabled() && (rollover || pressed)) {
				button.setOpaque(true);
				Color backgroundColor = ComponentUtil.getBackgroundColor(button.getParent());
				backgroundColor = backgroundColor == null
						? UIManagerUtil.getColor("control", Color.LIGHT_GRAY)
						: backgroundColor;
				button.setBackground(ColorUtil.mult(backgroundColor, pressed ? 0.8 : 1.15));

				button.setBorder(pressed
						? new CompoundBorder(new LineBorder(ColorUtil.mult(backgroundColor, 0.3)), pressedBorder)
						: new CompoundBorder(new LineBorder(ColorUtil.mult(backgroundColor, 0.5)), highlightedBorder));
			} else {
				setNormalState();
			}
		}
	}

	public static JButton createFlatHighlightButton(Icon icon, String tooltipText, int padding, ActionListener action) {
		final JButton b = new JButton(icon) {
			public void setUI(ButtonUI ui) {
				super.setUI(new BasicButtonUI() {
					@Override
					protected void installDefaults(AbstractButton b) {
					}
				});
			}
		};
		b.setVerticalAlignment(SwingConstants.CENTER);
		b.setToolTipText(tooltipText);
		b.setMargin(new Insets(0, 0, 0, 0));
		new ButtonHighlighter(b, padding);

		b.setRolloverEnabled(true);

		if (action != null)
			b.addActionListener(action);

		return b;
	}
}
