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

// $Id: TabDropDownList.java,v 1.18 2005/12/04 13:46:05 jesper Exp $

package net.infonode.tabbedpanel;

import net.infonode.gui.PopupList;

import javax.swing.*;

/**
 * @author Bjorn Lind
 * @version $Revision: 1.18 $ $Date: 2005/12/04 13:46:05 $
 * @since ITP 1.1.0
 */
public class TabDropDownList extends PopupList {
	private final TabbedPanel tabbedPanel;

	private final TabListener tabListener = new TabAdapter() {
		public void tabAdded(TabEvent event) {
			if (event.getTab().getTabbedPanel().getTabCount() == 2)
				setVisible(true);
		}

		public void tabRemoved(TabRemovedEvent event) {
			if (event.getTabbedPanel().getTabCount() == 1)
				setVisible(false);
		}
	};

	public TabDropDownList(final TabbedPanel tabbedPanel, AbstractButton button) {
		super(button);
		this.tabbedPanel = tabbedPanel;

		addPopupListListener(l -> {
			int numTabs = tabbedPanel.getTabCount();
			Tab[] tabs = new Tab[numTabs];
			for (int i = 0; i < numTabs; i++) {
				tabs[i] = tabbedPanel.getTabAt(i);
			}
			getList().setListData(tabs);
			getList().setSelectedValue(tabbedPanel.getSelectedTab(), true);
		});

		addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				tabbedPanel.setSelectedTab((Tab) getList().getSelectedValue());
		});

		tabbedPanel.addTabListener(tabListener);
		setVisible(tabbedPanel.getTabCount() > 1);

		setOpaque(false);
	}

	public void dispose() {
		tabbedPanel.removeTabListener(tabListener);
	}
}