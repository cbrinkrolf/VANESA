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

// $Id: InsetsUtil.java,v 1.14 2005/12/04 13:46:04 jesper Exp $
package net.infonode.gui;

import java.awt.*;

public class InsetsUtil {
	public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

	private InsetsUtil() {
	}

	public static Insets sub(Insets i1, Insets i2) {
		return new Insets(i1.top - i2.top, i1.left - i2.left, i1.bottom - i2.bottom, i1.right - i2.right);
	}

	public static Insets add(Insets i, Insets i2) {
		return new Insets(i.top + i2.top, i.left + i2.left, i.bottom + i2.bottom, i.right + i2.right);
	}

	public static Insets max(Insets insets1, Insets insets2) {
		return new Insets(Math.max(insets1.top, insets2.top), Math.max(insets1.left, insets2.left),
				Math.max(insets1.bottom, insets2.bottom), Math.max(insets1.right, insets2.right));
	}

	public static Insets copy(Insets insets) {
		return insets == null ? null : new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}
}

