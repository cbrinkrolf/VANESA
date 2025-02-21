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

// $Id: HighlightBorder.java,v 1.17 2005/12/04 13:46:03 jesper Exp $
package net.infonode.gui.border;

import net.infonode.gui.colorprovider.*;

import javax.swing.border.Border;
import java.awt.*;
import java.io.Serializable;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.17 $
 */
public class HighlightBorder implements Border, Serializable {
	private static final long serialVersionUID = 1;

	private final ColorProvider colorProvider;

	public HighlightBorder(Color color) {
		colorProvider = color == null
				? new ColorMultiplier(BackgroundPainterColorProvider.INSTANCE, 1.70)
				: new FixedColorProvider(color);
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 0, 0);
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		g.setColor(colorProvider.getColor(c));
		g.drawLine(x, y, x + width - 2, y);
		g.drawLine(x, y, x, y + height - 2);
	}
}
