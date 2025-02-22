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

// $Id: TabbedPanel.java,v 1.167 2005/12/04 13:46:05 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.*;
import net.infonode.gui.border.HighlightBorder;
import net.infonode.gui.draggable.*;
import net.infonode.gui.icon.button.DropDownIcon;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.tabbedpanel.border.TabAreaLineBorder;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * <p>
 * A TabbedPanel is a component that handles a group of components in a notebook like manor. Each component is
 * represented by a {@link Tab}. A tab is a component itself that defines how the tab will be rendered. The tab also
 * holds a reference to the content component associated with the tab. The tabbed panel is divided into two areas, the
 * tab area where the tabs are displayed and the content area where the tab's content component is displayed.
 * </p>
 *
 * <p>
 * The demo program for InfoNode Tabbed Panel on
 * <a href="http://www.infonode.net/index.html?itpdemo" target="_blank">
 * www.infonode.net</a> demonstrates and explains most of the tabbed panel's features.
 * </p>
 *
 * <p>
 * Tabs can be added, inserted, removed, selected, highlighted, dragged and moved.
 * </p>
 *
 * <p>
 * The tabbed panel support tab placement in a horizontal line above or under the content area or a vertical row to the
 * left or to the right of the content area. The tab line can be laid out as either scrolling or compression. If the
 * tabs are too many to fit in the tab area and scrolling is enabled, then the mouse wheel is activated and
 * scrollbuttons are shown so that the tabs can be scrolled. Compression means that the tabs will be downsized to fit
 * into the visible tab area.
 * </p>
 *
 * <p>
 * It is possible to display a button in the tab area next to the tabs that shows a drop down list (called tab drop down
 * list) with all the tabs where it is possible to select a tab. This is for example useful when the tabbed panel
 * contains a large amount of tabs or if some tabs are scrolled out. The drop down list can show a text and an icon for
 * a tab. The text is retrieved by calling toString() on the tab.
 * </p>
 *
 * <p>
 * It is possible to set an array of components (called tab area components) to be shown next to the tabs in the tab
 * area, the same place where the drop down list and the scrollbuttons are shown. This for example useful for adding
 * buttons to the tabbed panel.
 * </p>
 *
 * <p>
 * It is possible to add a {@link TabListener} and receive events when a tab is added, removed, selected, deselected,
 * highlighted, dehighlighted, moved, dragged, dropped or drag is aborted. The listener will receive events for all the
 * tabs in the tabbed panel. A tabbed panel will trigger selected, deselected, highlighted and dehighlighted even if for
 * example the selected tab is null (no selected tab), i.e. null will be treated as if it was a tab.
 * </p>
 *
 * <p>
 * A tabbed panel calls the hover listeners in the following order:
 * <ol>
 * <li>The hover listener for the tabbed panel itself.
 * <li>The hover listener for the tab area or the content area depending on where the
 * mouse pointer is located.
 * <li>The hover listener for the tab area components area if the mouse pointer is over
 * that area.
 * </ol>
 * When the tabbed panel is no longer hovered, the hover listenrs are called in the
 * reverse order.
 * </p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.167 $
 * @see Tab
 * @see TitledTab
 * @see TabListener
 */
public class TabbedPanel extends JPanel {

	private final DraggableComponentBox draggableComponentBox = new DraggableComponentBox();
	private ArrayList<TabListener> listeners;
	private Tab highlightedTab;

	private boolean settingHighlighted;
	private boolean mouseEntered = false;
	private boolean removingSelected = false;

	private ScrollButtonBox scrollButtonBox;

	private final GridBagConstraints constraints = new GridBagConstraints();
	private final GridBagLayout tabAreaLayoutManager = new GridBagLayout() {
		public void layoutContainer(Container parent) {
			setTabAreaComponentsButtonsVisible();
			super.layoutContainer(parent);

			// Overlap if tab area is too narrow to fit both tabAreaComponentsPanel and draggableComponentBox
			if (tabAreaComponentsPanel.isVisible()) {
				if (tabAreaContainer.getWidth() < tabAreaComponentsPanel.getPreferredSize().getWidth()) {
					draggableComponentBox.setSize(0, draggableComponentBox.getHeight());
					tabAreaComponentsPanel.setSize(tabAreaContainer.getWidth(), tabAreaComponentsPanel.getHeight());
				}
			}
		}
	};

	private final JPanel tabAreaContainer = new JPanel(tabAreaLayoutManager) {
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();

			if (getTabCount() == 0) {
				Insets insets = getInsets();
				Dimension d2 = tabAreaComponentsPanel.getPreferredSize();
				d = new Dimension(insets.left + insets.right + d2.width, insets.top + insets.bottom + d2.height);
			}

			return d;
		}
	};

	private final JPanel tabAreaComponentsPanel = new JPanel(new DirectionLayout()) {
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			Insets insets = getInsets();

			if (ComponentUtil.hasVisibleChildren(this)) {
				int maxHeight = ComponentUtil.getPreferredMaxHeight(getComponents()) + insets.top + insets.bottom;
				return new Dimension(d.width, maxHeight);
			}

			return new Dimension(0, 0);
		}
	};

	private final DraggableComponentBoxListener draggableComponentBoxListener = new DraggableComponentBoxListener() {
		public void componentSelected(DraggableComponentBoxEvent event) {
			if (event.getDraggableComponent() != event.getOldDraggableComponent()) {
				Tab tab = findTab(event.getDraggableComponent());
				setHighlightedTab(tab);
				Tab oldTab = findTab(event.getOldDraggableComponent());
				fireSelectedEvent(tab, oldTab);
				if (removingSelected) {
					removingSelected = false;
					if (oldTab != null)
						oldTab.setTabbedPanel(null);
				}
			}

			tabAreaContainer.repaint();
		}

		public void componentRemoved(DraggableComponentBoxEvent event) {
			Tab tab = findTab(event.getDraggableComponent());
			if (highlightedTab == tab)
				highlightedTab = null;

			setTabAreaComponentsButtonsVisible();
			updateTabAreaVisibility();
			tabAreaContainer.repaint();
			fireRemovedEvent(tab);
		}

		public void componentAdded(DraggableComponentBoxEvent event) {
			updateTabAreaVisibility();
			tabAreaContainer.repaint();
			fireAddedEvent(findTab(event.getDraggableComponent()));
		}

		public void componentDragged(DraggableComponentBoxEvent event) {
			fireDraggedEvent(findTab(event.getDraggableComponent()),
					event.getDraggableComponentEvent().getMouseEvent());
		}

		public void componentDropped(DraggableComponentBoxEvent event) {
			if (!draggableComponentBox.contains(event.getDraggableComponentBoxPoint()))
				setHighlightedTab(findTab(draggableComponentBox.getSelectedDraggableComponent()));
			fireDroppedEvent(findTab(event.getDraggableComponent()),
					event.getDraggableComponentEvent().getMouseEvent());
		}

		public void componentDragAborted(DraggableComponentBoxEvent event) {
			fireNotDroppedEvent(findTab(event.getDraggableComponent()));
		}

		public void changed(DraggableComponentBoxEvent event) {
			if (event.getDraggableComponentEvent() != null) {
				int type = event.getDraggableComponentEvent().getType();

				if (type == DraggableComponentEvent.TYPE_PRESSED) {
					if (highlightedTab != null)
						setHighlightedTab(findTab(event.getDraggableComponent()));
				} else if (type == DraggableComponentEvent.TYPE_RELEASED) {
					setHighlightedTab(getSelectedTab());
				} else if (type == DraggableComponentEvent.TYPE_DISABLED && highlightedTab != null
						&& highlightedTab.getDraggableComponent() == event.getDraggableComponent())
					setHighlightedTab(null);
				else if (type == DraggableComponentEvent.TYPE_ENABLED
						&& draggableComponentBox.getSelectedDraggableComponent() == event.getDraggableComponent())
					setHighlightedTab(findTab(event.getDraggableComponent()));
				else if (type == DraggableComponentEvent.TYPE_MOVED) {
					tabAreaContainer.repaint();
					fireTabMoved(findTab(event.getDraggableComponent()));
				}
			} else {
				// Scrolling
				tabAreaContainer.repaint();
			}
		}
	};

	private void updatePropertiesForTabAreaLayoutConstraints() {
		setTabAreaLayoutConstraints(draggableComponentBox, 0, GridBagConstraints.HORIZONTAL, 1, 1);
		setTabAreaLayoutConstraints(tabAreaComponentsPanel, 1, GridBagConstraints.NONE, 0, 0);
		((DirectionLayout) tabAreaComponentsPanel.getLayout()).setDirection(Direction.RIGHT);
		draggableComponentBox.setComponentDirection();
	}

	private void updateTabAreaVisibility() {
		tabAreaContainer.setVisible(true);
	}

	/**
	 * Constructs a TabbedPanel with a TabbedPanelContentPanel as content area component and with default
	 * TabbedPanelProperties
	 *
	 * @see TabbedPanelContentPanel
	 */
	public TabbedPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		tabAreaComponentsPanel.setOpaque(true);
		draggableComponentBox.setOuterParentArea(tabAreaContainer);
		tabAreaContainer.add(tabAreaComponentsPanel);
		tabAreaContainer.add(draggableComponentBox);
		final JPanel contentPanel = new TabbedPanelContentPanel(this, new TabContentPanel(this));
		draggableComponentBox.addListener(draggableComponentBoxListener);
		final ShadowPanel componentsPanel = new ShadowPanel();
		componentsPanel.setBorder(null);
		componentsPanel.add(contentPanel, BorderLayout.CENTER);
		add(componentsPanel, BorderLayout.CENTER);
		// General
		draggableComponentBox.setScrollEnabled(true);
		JButton dropDownButton = ButtonFactory.createFlatHighlightButton(null, null, 0, null);
		dropDownButton.setIcon(new DropDownIcon(Color.black, TabbedUIDefaults.getButtonIconSize()));
		dropDownButton.setDisabledIcon(null);
		final TabDropDownList dropDownList = new TabDropDownList(this, dropDownButton);
		tabAreaComponentsPanel.add(dropDownList, scrollButtonBox == null ? 0 : 1);
		tabAreaComponentsPanel.revalidate();

		updatePropertiesForTabAreaLayoutConstraints();
		componentsPanel.add(tabAreaContainer, BorderLayout.SOUTH);
		componentsPanel.revalidate();

		draggableComponentBox.setComponentSpacing(-1);
		draggableComponentBox.setDepthSortOrder(true);
		// Tab area
		tabAreaContainer.setBorder(null);
		tabAreaContainer.setBackground(TabbedUIDefaults.getContentAreaBackground());
		// Tab area components area
		updatePropertiesForTabAreaLayoutConstraints();
		tabAreaComponentsPanel.setBorder(new CompoundBorder(new TabAreaLineBorder(TabbedUIDefaults.getDarkShadow()),
				new HighlightBorder(TabbedUIDefaults.getHighlight())));
		tabAreaComponentsPanel.setBackground(TabbedUIDefaults.getContentAreaBackground());
	}

	/**
	 * <p>
	 * Add a tab. The tab will be added after the last tab.
	 * </p>
	 *
	 * <p>
	 * If the tab to be added is the only tab in this tabbed panel and the property "Auto Select Tab" is enabled then
	 * the tab will become selected in this tabbed panel after the tab has been added.
	 * </p>
	 *
	 * @param tab tab to be added
	 */
	public void addTab(Tab tab) {
		if (tab != null && !draggableComponentBox.containsDraggableComponent(tab.getDraggableComponent())) {
			tab.setTabbedPanel(this);
			draggableComponentBox.insertDraggableComponent(tab.getDraggableComponent(), -1);
			checkIfOnlyOneTab(true);
		}
	}

	/**
	 * Removes a tab
	 *
	 * @param tab tab to be removed from this TabbedPanel
	 */
	public void removeTab(Tab tab) {
		if (tab != null && tab.getTabbedPanel() == this) {
			if (getSelectedTab() != tab) {
				tab.setTabbedPanel(null);
			} else {
				removingSelected = true;
			}
			draggableComponentBox.removeDraggableComponent(tab.getDraggableComponent());
		}
		checkIfOnlyOneTab(false);
	}

	/**
	 * Selects a tab, i.e. displays the tab's content component in this tabbed panel's content area
	 *
	 * @param tab tab to select. Tab must be a member (added/inserted) of this tabbed panel.
	 */
	public void setSelectedTab(Tab tab) {
		if (getSelectedTab() == tab)
			return;

		if (tab != null) {
			if (tab.isEnabled() && getTabIndex(tab) > -1) {
				if (tab.getDraggableComponent() == draggableComponentBox.getSelectedDraggableComponent()) {
					setHighlightedTab(tab);
				} else {
					tab.setSelected(true);
				}
			}
		} else {
			draggableComponentBox.selectDraggableComponent(null);
		}
	}

	/**
	 * Gets the selected tab, i.e. the tab who's content component is currently displayed in this tabbed panel's content
	 * area
	 *
	 * @return the selected tab or null if no tab is selected in this tabbed panel
	 */
	public Tab getSelectedTab() {
		return findTab(draggableComponentBox.getSelectedDraggableComponent());
	}

	/**
	 * Sets which tab that should be highlighted, i.e. signal highlighted state to the tab
	 *
	 * @param highlightedTab tab that should be highlighted or null if no tab should be highlighted. The tab must be a
	 *                       member (added/inserted) of this tabbed panel.
	 */
	public void setHighlightedTab(Tab highlightedTab) {
		if (!settingHighlighted) {
			settingHighlighted = true;
			Tab oldTab = this.highlightedTab;
			Tab newTab = null;
			if (oldTab != highlightedTab)
				draggableComponentBox.setTopComponent(
						highlightedTab != null ? highlightedTab.getDraggableComponent() : null);
			if (highlightedTab != null) {
				if (getTabIndex(highlightedTab) > -1) {
					this.highlightedTab = highlightedTab;
					if (oldTab != null && oldTab != highlightedTab) {
						oldTab.setHighlighted(false);
					}

					if (oldTab != highlightedTab)
						if (highlightedTab.isEnabled()) {
							highlightedTab.setHighlighted(true);
						} else {
							highlightedTab.setHighlighted(false);
							this.highlightedTab = null;
						}

					if (highlightedTab.isEnabled() && highlightedTab != oldTab)
						newTab = highlightedTab;

					if (oldTab != highlightedTab)
						fireHighlightedEvent(newTab, oldTab);
				}
			} else if (oldTab != null) {
				this.highlightedTab = null;
				oldTab.setHighlighted(false);
				fireHighlightedEvent(null, oldTab);
			}

			settingHighlighted = false;
		}
	}

	/**
	 * Gets the highlighted tab
	 *
	 * @return the highlighted tab or null if no tab is highlighted in this tabbed panel
	 */
	public Tab getHighlightedTab() {
		return highlightedTab;
	}

	/**
	 * Gets the number of tabs
	 *
	 * @return number of tabs
	 */
	public int getTabCount() {
		return draggableComponentBox.getDraggableComponentCount();
	}

	/**
	 * Gets the tab at index
	 *
	 * @param index index of tab
	 * @return tab at index
	 * @throws ArrayIndexOutOfBoundsException if there is no tab at index
	 */
	public Tab getTabAt(int index) {
		DraggableComponent component = draggableComponentBox.getDraggableComponentAt(index);
		return component == null ? null : (Tab) component.getComponent();
	}

	/**
	 * Gets the index for tab
	 *
	 * @param tab tab
	 * @return index or -1 if tab is not a member of this TabbedPanel
	 */
	public int getTabIndex(Tab tab) {
		return tab == null ? -1 : draggableComponentBox.getDraggableComponentIndex(tab.getDraggableComponent());
	}

	/**
	 * Adds a TablListener that will receive events for all the tabs in this TabbedPanel
	 *
	 * @param listener the TabListener to add
	 */
	public void addTabListener(TabListener listener) {
		if (listeners == null)
			listeners = new ArrayList<>(2);

		listeners.add(listener);
	}

	/**
	 * Removes a TabListener
	 *
	 * @param listener the TabListener to remove
	 */
	public void removeTabListener(TabListener listener) {
		if (listeners != null) {
			listeners.remove(listener);

			if (listeners.isEmpty())
				listeners = null;
		}
	}

	DraggableComponentBox getDraggableComponentBox() {
		return draggableComponentBox;
	}

	private void setTabAreaLayoutConstraints(JComponent c, int gridx, int fill, double weightx, double weighty) {
		constraints.gridx = gridx;
		constraints.gridy = 0;
		constraints.fill = fill;
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		constraints.anchor = GridBagConstraints.NORTH;

		tabAreaLayoutManager.setConstraints(c, constraints);
	}

	private Tab findTab(DraggableComponent draggableComponent) {
		return draggableComponent == null ? null : (Tab) draggableComponent.getComponent();
	}

	private void checkIfOnlyOneTab(boolean inc) {
		if (getTabCount() == 1) {
			draggableComponentBox.setScrollEnabled(false);
			updateScrollButtons();
		} else if (inc && getTabCount() == 2) {
			draggableComponentBox.setScrollEnabled(true);
			updateScrollButtons();
		}
	}

	private void setTabAreaComponentsButtonsVisible() {
		if (scrollButtonBox != null) {
			boolean visible = draggableComponentBox.getInnerSize().getWidth() > calcScrollWidth();
			scrollButtonBox.setVisible(visible);

			if (!visible) {
				scrollButtonBox.setButton1Enabled(false);
				scrollButtonBox.setButton2Enabled(true);
			}
		}

		tabAreaComponentsPanel.setVisible(ComponentUtil.hasVisibleChildren(tabAreaComponentsPanel));
	}

	private int calcScrollWidth() {
		Insets componentsPanelInsets = tabAreaComponentsPanel.getInsets();
		boolean componentsVisible = ComponentUtil.isOnlyVisibleComponent(scrollButtonBox);
		int insetsWidth = tabAreaComponentsPanel.isVisible() && componentsVisible ? componentsPanelInsets.left
				+ componentsPanelInsets.right : 0;
		int componentsPanelWidth = tabAreaComponentsPanel.isVisible() ? (
				(int) tabAreaComponentsPanel.getPreferredSize().getWidth() - insetsWidth - (scrollButtonBox.isVisible()
						? scrollButtonBox.getWidth()
						: 0)) : 0;
		Insets areaInsets = tabAreaContainer.getInsets();
		return tabAreaContainer.getWidth() - componentsPanelWidth - areaInsets.left - areaInsets.right;
	}

	private void updateScrollButtons() {
		ScrollButtonBox oldScrollButtonBox = scrollButtonBox;
		scrollButtonBox = draggableComponentBox.getScrollButtonBox();
		if (oldScrollButtonBox != scrollButtonBox) {
			if (oldScrollButtonBox != null) {
				tabAreaComponentsPanel.remove(oldScrollButtonBox);
			}

			if (scrollButtonBox != null) {
				scrollButtonBox.setVisible(false);
				tabAreaComponentsPanel.add(scrollButtonBox, 0);
			}

			tabAreaComponentsPanel.revalidate();
		}
	}

	private void fireTabMoved(Tab tab) {
		if (listeners != null) {
			TabEvent event = new TabEvent(this, tab);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabMoved(event);
		}
	}

	private void fireDraggedEvent(Tab tab, MouseEvent mouseEvent) {
		if (listeners != null) {
			TabDragEvent event = new TabDragEvent(this, EventUtil.convert(mouseEvent, tab));
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabDragged(event);
		}
	}

	private void fireDroppedEvent(Tab tab, MouseEvent mouseEvent) {
		if (listeners != null) {
			TabDragEvent event = new TabDragEvent(this, EventUtil.convert(mouseEvent, tab));
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabDropped(event);
		}
	}

	private void fireNotDroppedEvent(Tab tab) {
		if (listeners != null) {
			TabEvent event = new TabEvent(this, tab);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabDragAborted(event);
		}
	}

	private void fireSelectedEvent(Tab tab, Tab oldTab) {
		if (listeners != null) {
			{
				TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
				Object[] l = listeners.toArray();
				for (int i = 0; i < l.length; i++)
					((TabListener) l[i]).tabDeselected(event);
			}
			{
				TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
				Object[] l = listeners.toArray();
				for (int i = 0; i < l.length; i++)
					((TabListener) l[i]).tabSelected(event);
			}
		}
	}

	private void fireHighlightedEvent(Tab tab, Tab oldTab) {
		if (listeners != null) {
			{
				TabStateChangedEvent event = new TabStateChangedEvent(this, this, oldTab, oldTab, tab);
				Object[] l = listeners.toArray();
				for (int i = 0; i < l.length; i++)
					((TabListener) l[i]).tabDehighlighted(event);
			}
			{
				TabStateChangedEvent event = new TabStateChangedEvent(this, this, tab, oldTab, tab);
				Object[] l = listeners.toArray();
				for (int i = 0; i < l.length; i++)
					((TabListener) l[i]).tabHighlighted(event);
			}
		}
	}

	private void fireAddedEvent(Tab tab) {
		if (listeners != null) {
			TabEvent event = new TabEvent(this, tab);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabAdded(event);
		}
	}

	private void fireRemovedEvent(Tab tab) {
		if (listeners != null) {
			TabRemovedEvent event = new TabRemovedEvent(this, tab, this);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((TabListener) l[i]).tabRemoved(event);
		}
	}

	protected void processMouseEvent(MouseEvent event) {
		if (event.getID() == MouseEvent.MOUSE_ENTERED) {
			if (!mouseEntered) {
				mouseEntered = true;
				super.processMouseEvent(event);
			}
		} else if (event.getID() == MouseEvent.MOUSE_EXITED) {
			if (!contains(event.getPoint())) {
				mouseEntered = false;
				super.processMouseEvent(event);
			}
		} else
			super.processMouseEvent(event);
	}

	void doProcessMouseEvent(MouseEvent event) {
		processMouseEvent(SwingUtilities.convertMouseEvent((Component) event.getSource(), event, this));
	}

	void doProcessMouseMotionEvent(MouseEvent event) {
		processMouseMotionEvent(SwingUtilities.convertMouseEvent((Component) event.getSource(), event, this));
	}

	private static class ShadowPanel extends JPanel {
		ShadowPanel() {
			super(new BorderLayout());
			setCursor(null);
		}
	}
}