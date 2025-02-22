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

// $Id: TabbedPanelContentPanel.java,v 1.60 2009/02/05 15:57:55 jesper Exp $
package net.infonode.tabbedpanel;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.infonode.gui.draggable.DraggableComponentBoxAdapter;
import net.infonode.gui.draggable.DraggableComponentBoxEvent;
import net.infonode.gui.draggable.DraggableComponentEvent;
import net.infonode.tabbedpanel.border.OpenContentBorder;

/**
 * A TabbedPanelContentPanel is a component that holds a container for tab content components. It can be configured
 * using properties that specifies the look for the content panel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.60 $
 * @see TabbedPanel
 * @see Tab
 */
public class TabbedPanelContentPanel extends JPanel {
	private final TabbedPanel tabbedPanel;
	private final JPanel shapedPanel;
	private boolean okToRepaintBorder = false;

	/**
	 * Constructs a TabbedPanelContentPanel
	 *
	 * @param tabbedPanel the TabbedPanel that this content panel should be the content area component for
	 * @param component   a component used as container for the tabs' content components
	 */
	public TabbedPanelContentPanel(final TabbedPanel tabbedPanel, TabContentPanel component) {
		super(new BorderLayout());
		setOpaque(true);
		this.tabbedPanel = tabbedPanel;

		shapedPanel = new JPanel(new BorderLayout()) {
			protected void processMouseEvent(MouseEvent event) {
				super.processMouseEvent(event);
				tabbedPanel.doProcessMouseEvent(event);
			}

			protected void processMouseMotionEvent(MouseEvent event) {
				super.processMouseMotionEvent(event);
				tabbedPanel.doProcessMouseMotionEvent(event);
			}
		};
		shapedPanel.setOpaque(true);
		shapedPanel.addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) == HierarchyEvent.SHOWING_CHANGED) {
				if (shapedPanel.isDisplayable()) {
					SwingUtilities.invokeLater(() -> okToRepaintBorder = true);
				} else {
					okToRepaintBorder = false;
				}
			}
		});

		shapedPanel.add(component, BorderLayout.CENTER);
		add(shapedPanel, BorderLayout.CENTER);

		Insets insets = TabbedUIDefaults.getContentAreaInsets();
		Border innerBorder = new EmptyBorder(insets);
		shapedPanel.setBorder(new CompoundBorder(
				new OpenContentBorder(TabbedUIDefaults.getDarkShadow(), TabbedUIDefaults.getHighlight()), innerBorder));
		shapedPanel.setBackground(TabbedUIDefaults.getContentAreaBackground());

		tabbedPanel.getDraggableComponentBox().addListener(new DraggableComponentBoxAdapter() {
			public void changed(DraggableComponentBoxEvent event) {
				if (event.getDraggableComponent() == null
						|| event.getDraggableComponentEvent().getType() == DraggableComponentEvent.TYPE_UNDEFINED) {
					repaintBorder();
				}
			}
		});

		tabbedPanel.addTabListener(new TabAdapter() {
			public void tabAdded(TabEvent event) {
				repaintBorder();
			}

			public void tabRemoved(TabRemovedEvent event) {
				repaintBorder();
			}

			public void tabSelected(TabStateChangedEvent event) {
				repaintBorder();
			}

			public void tabDeselected(TabStateChangedEvent event) {
				repaintBorder();
			}

			public void tabDehighlighted(TabStateChangedEvent event) {
				repaintBorder();
			}

			public void tabHighlighted(TabStateChangedEvent event) {
				repaintBorder();
			}

			public void tabMoved(TabEvent event) {
				repaintBorder();
			}
		});
	}

	/**
	 * Gets the tabbed panel that this component is the content area component for
	 *
	 * @return the tabbed panel
	 */
	public TabbedPanel getTabbedPanel() {
		return tabbedPanel;
	}

	private void repaintBorder() {
		if (okToRepaintBorder) {
			final Rectangle r = new Rectangle(0, shapedPanel.getHeight() - shapedPanel.getInsets().bottom - 1,
					shapedPanel.getWidth(), shapedPanel.getHeight());
			shapedPanel.repaint(r);
		}
	}
}
