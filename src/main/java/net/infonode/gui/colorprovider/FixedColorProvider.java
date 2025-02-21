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

// $Id: FixedColorProvider.java,v 1.8 2009/02/05 15:57:56 jesper Exp $
package net.infonode.gui.colorprovider;

import java.awt.*;

/**
 * A {@link ColorProvider} which always returns the same color.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 */
public class FixedColorProvider implements ColorProvider {
	private static final long serialVersionUID = 1;

	/**
	 * A provider for the color black.
	 */
	public static final FixedColorProvider BLACK = new FixedColorProvider(Color.BLACK);

	/**
	 * A provider for the color white.
	 */
	public static final FixedColorProvider WHITE = new FixedColorProvider(Color.WHITE);

	private final Color color;

	/**
	 * Constructor.
	 *
	 * @param color the color which this provider will return
	 */
	public FixedColorProvider(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Color getColor(Component component) {
		return getColor();
	}
}
