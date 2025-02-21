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

// $Id: BaseContainer.java,v 1.13 2005/12/04 13:46:03 jesper Exp $
package net.infonode.gui.panel;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;

public class BaseContainer extends JPanel {
	private Color foreground;
	private Color background;
	private Font font;
	private Color overridedBackground;
	private Color overridedForeground;
	private Font overridedFont;
	private boolean forcedOpaque;
	private boolean opaque = true;

	private static final PanelUI UI = new PanelUI() {
	};

	public BaseContainer() {
		this(true);
	}

	public BaseContainer(boolean opaque) {
		this(opaque, new BorderLayout());
	}

	public BaseContainer(LayoutManager l) {
		this(true, l);
	}

	public BaseContainer(final boolean opaque, LayoutManager l) {
		super(l);

		this.forcedOpaque = opaque;

		updateOpaque();
	}

	public void setUI(PanelUI ui) {
		Color oBackground = overridedBackground;
		Color oForeground = overridedForeground;
		Font oFont = overridedFont;

		oBackground = null;
		oForeground = null;
		oFont = null;

		setBackground(null);
		setForeground(null);
		setFont(null);

		super.setUI(ui);

		background = getBackground();
		foreground = getForeground();
		font = getFont();

		overridedBackground = oBackground;
		overridedForeground = oForeground;
		overridedFont = oFont;

		if (!forcedOpaque)
			super.setUI(UI);

		updateBackground();
		updateForeground();
		updateFont();
	}

	public void setForcedOpaque(final boolean forcedOpaque) {
		if (this.forcedOpaque != forcedOpaque) {
			this.forcedOpaque = forcedOpaque;

			updateUI();
			updateOpaque();
		}
	}

	// Overrided

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;

		updateOpaque();
	}

	protected void paintComponent(Graphics g) {
		if (forcedOpaque)
			super.paintComponent(g);
	}

	public void setForeground(Color fg) {
		this.foreground = fg;

		updateForeground();
	}

	public void setBackground(Color bg) {
		this.background = bg;

		updateBackground();
	}

	public void setFont(Font font) {
		this.font = font;

		updateFont();
	}

	public void setOverridedBackground(Color bg) {
		this.overridedBackground = bg;

		updateBackground();
	}

	private void updateBackground() {
		super.setBackground(overridedBackground == null ? background : overridedBackground);
	}

	private void updateForeground() {
		super.setForeground(overridedForeground == null ? foreground : overridedForeground);
	}

	private void updateFont() {
		super.setFont(overridedFont == null ? font : overridedFont);
	}

	private void updateOpaque() {
		super.setOpaque(forcedOpaque ? opaque : forcedOpaque);
	}
}
