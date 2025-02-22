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

// $Id: DraggableComponentBox.java,v 1.53 2009/02/05 15:57:56 jesper Exp $

package net.infonode.gui.draggable;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.*;

import net.infonode.gui.*;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.util.Direction;

public class DraggableComponentBox extends JPanel {
	private final boolean componentBoxEnabled = true;

	private final JComponent componentBox;
	private JComponent componentContainer;

	private JComponent outerParentArea = this;
	private boolean scrollEnabled = false;
	private boolean descendingSortOrder = true;

	private boolean doReverseSort = false;
	private boolean mustSort = false;

	private DraggableComponent selectedComponent;
	private DraggableComponent topComponent;
	private ArrayList<DraggableComponentBoxListener> listeners;
	private final ArrayList<DraggableComponent> draggableComponentList = new ArrayList<>(10);
	private final ArrayList<Component> layoutOrderList = new ArrayList<>(10);

	private ScrollButtonBox scrollButtonBox;

	private final DraggableComponentListener draggableComponentListener = new DraggableComponentListener() {
		public void changed(DraggableComponentEvent event) {
			if (event.getType() == DraggableComponentEvent.TYPE_MOVED) {
				sortComponentList(!descendingSortOrder);
			}
			fireChangedEvent(event);
		}

		public void selected(DraggableComponentEvent event) {
			doSelectComponent(event.getSource());
		}

		public void dragged(DraggableComponentEvent event) {
			fireDraggedEvent(event);
		}

		public void dropped(DraggableComponentEvent event) {
			ensureSelectedVisible();
			fireDroppedEvent(event);
		}

		public void dragAborted(DraggableComponentEvent event) {
			ensureSelectedVisible();
			fireNotDroppedEvent(event);
		}
	};

	public DraggableComponentBox() {
		super(new BorderLayout());
		// Fix minimum size when flipping direction
		final DirectionLayout layout = new DirectionLayout(Direction.RIGHT) {
			public Dimension minimumLayoutSize(Container parent) {
				Dimension min = super.minimumLayoutSize(parent);
				Dimension pref = super.preferredLayoutSize(parent);
				return new Dimension(min.width, pref.height);
			}

			public void layoutContainer(Container parent) {
				if (componentBoxEnabled) {
					doSort();
					super.layoutContainer(parent);
				}
			}

			public Dimension preferredLayoutSize(Container parent) {
				doSort();
				return super.preferredLayoutSize(parent);
			}
		};

		layout.setLayoutOrderList(layoutOrderList);

		componentBox = new JPanel(layout) {
			public boolean isOptimizedDrawingEnabled() {
				return getComponentSpacing() >= 0;
			}
		};

		componentBox.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				fireChangedEvent();
			}
		});

		initialize();
	}

	public void addListener(DraggableComponentBoxListener listener) {
		if (listeners == null)
			listeners = new ArrayList<>(2);

		listeners.add(listener);
	}

	public void insertDraggableComponent(DraggableComponent component, int index) {
		component.setLayoutOrderList(layoutOrderList);

		component.addListener(draggableComponentListener);
		if (index < 0) {
			layoutOrderList.add(component.getComponent());
			componentBox.add(component.getComponent());
		} else {
			layoutOrderList.add(index, component.getComponent());
			componentBox.add(component.getComponent(), index);
		}

		sortComponentList(!descendingSortOrder);

		draggableComponentList.add(component);
		component.setOuterParentArea(outerParentArea);
		componentBox.revalidate();

		fireAddedEvent(component);
		if (layoutOrderList.size() == 1 && selectedComponent == null && component.isEnabled())
			doSelectComponent(component);

		updateScrollButtons();
	}

	private void updateScrollButtons() {
		if (scrollButtonBox != null) {
			ScrollableBox scrollableBox = (ScrollableBox) componentContainer;
			scrollButtonBox.setButton1Enabled(!scrollableBox.isLeftEnd());
			scrollButtonBox.setButton2Enabled(!scrollableBox.isRightEnd());
		}
	}

	public void selectDraggableComponent(DraggableComponent component) {
		if (component == null) {
			if (selectedComponent != null) {
				DraggableComponent oldSelected = selectedComponent;
				selectedComponent = null;
				fireSelectedEvent(selectedComponent, oldSelected);
			}
		} else
			component.select();
	}

	public void removeDraggableComponent(DraggableComponent component) {
		if (component != null && draggableComponentList.contains(component)) {
			int index = layoutOrderList.indexOf(component.getComponent());
			component.removeListener(draggableComponentListener);
			if (component == topComponent)
				topComponent = null;
			if (layoutOrderList.size() > 1 && selectedComponent != null) {
				if (selectedComponent == component) {
					int selectIndex = findSelectableComponentIndex(index);
					if (selectIndex > -1)
						selectDraggableComponent(findDraggableComponent(layoutOrderList.get(selectIndex)));
					else
						selectedComponent = null;
				}
			} else {
				if (selectedComponent != null) {
					DraggableComponent oldSelected = selectedComponent;
					selectedComponent = null;
					fireSelectedEvent(selectedComponent, oldSelected);
				}
			}
			draggableComponentList.remove(component);
			layoutOrderList.remove(component.getComponent());
			componentBox.remove(component.getComponent());
			componentBox.revalidate();
			//componentBox.validate();
			component.setLayoutOrderList(null);

			sortComponentList(!descendingSortOrder);

			updateScrollButtons();

			fireRemovedEvent(component);
		}
	}

	public boolean containsDraggableComponent(DraggableComponent component) {
		return draggableComponentList.contains(component);
	}

	public DraggableComponent getSelectedDraggableComponent() {
		return selectedComponent;
	}

	public int getDraggableComponentCount() {
		return layoutOrderList.size();
	}

	public DraggableComponent getDraggableComponentAt(int index) {
		return index < layoutOrderList.size() ? findDraggableComponent((Component) layoutOrderList.get(index)) : null;
	}

	public int getDraggableComponentIndex(DraggableComponent component) {
		return layoutOrderList.indexOf(component.getComponent());
	}

	public Component[] getBoxComponents() {
		return componentBox.getComponents();
	}

	public void setDepthSortOrder(boolean descending) {
		if (descending != this.descendingSortOrder) {
			this.descendingSortOrder = descending;
			sortComponentList(!descending);
			doSort();
		}
	}

	public void setScrollEnabled(boolean scrollEnabled) {
		if (scrollEnabled != this.scrollEnabled) {
			this.scrollEnabled = scrollEnabled;
			initialize();
		}
	}

	public int getComponentSpacing() {
		return getDirectionLayout().getComponentSpacing();
	}

	public void setComponentSpacing(int componentSpacing) {
		if (componentSpacing != getDirectionLayout().getComponentSpacing()) {
			if (getComponentSpacing() < 0 && componentSpacing >= 0) {
				DraggableComponent tmp = topComponent;
				sortComponentList(false);
				topComponent = tmp;
			}
			getDirectionLayout().setComponentSpacing(componentSpacing);
			sortComponentList(!descendingSortOrder);
			componentBox.revalidate();
		}
	}

	public void setComponentDirection() {
		getDirectionLayout().setDirection(Direction.RIGHT);
	}

	public void setTopComponent(DraggableComponent topComponent) {
		if (topComponent != this.topComponent) {
			this.topComponent = topComponent;

			sortComponentList(!descendingSortOrder);
		}
	}

	public ScrollButtonBox getScrollButtonBox() {
		return scrollButtonBox;
	}

	public void setOuterParentArea(JComponent outerParentArea) {
		this.outerParentArea = outerParentArea;
	}

	public Dimension getMaximumSize() {
		if (scrollEnabled)
			return getPreferredSize();
		return new Dimension((int) super.getMaximumSize().getWidth(), (int) getPreferredSize().getHeight());
	}

	public Dimension getInnerSize() {
		boolean mustSort = this.mustSort;
		this.mustSort = false;
		Dimension d = scrollEnabled ? componentBox.getPreferredSize() : componentBox.getSize();
		this.mustSort = mustSort;
		return d;
	}

	// Prevents focus problems when adding/removing focused component while sorting when spacing < 0
	private void setIgnoreAddRemoveNotify(boolean ignore) {
		for (int i = 0; i < draggableComponentList.size(); i++)
			draggableComponentList.get(i).setIgnoreAddNotify(ignore);
	}

	private void doSort() {
		if (mustSort && getComponentSpacing() < 0 && componentBox.getComponentCount() > 0) {
			setIgnoreAddRemoveNotify(true);

			mustSort = false;
			Component c;
			Component tc = topComponent != null ? topComponent.getComponent() : null;

			int index = 0;
			if (tc != null) {
				if (componentBox.getComponent(0) != tc) {
					componentBox.remove(tc);
					componentBox.add(tc, index);
				}
				index++;
			}

			int size = layoutOrderList.size();
			for (int i = 0; i < size; i++) {
				c = layoutOrderList.get(doReverseSort ? size - i - 1 : i);
				if (c != tc) {
					if (componentBox.getComponent(index) != c) {
						componentBox.remove(c);
						componentBox.add(c, index);
					}
					index++;
				}
			}

			setIgnoreAddRemoveNotify(false);
		}
	}

	private void sortComponentList(boolean reverseSort) {
		this.doReverseSort = reverseSort;
		mustSort = true;
	}

	private void doSelectComponent(DraggableComponent component) {
		if (selectedComponent != null) {
			DraggableComponent oldSelected = selectedComponent;
			selectedComponent = component;
			ensureSelectedVisible();
			fireSelectedEvent(selectedComponent, oldSelected);
		} else {
			selectedComponent = component;
			ensureSelectedVisible();
			fireSelectedEvent(selectedComponent, null);
		}
	}

	private int findSelectableComponentIndex(int index) {
		int selectIndex = -1;
		int k;
		for (int i = 0; i < layoutOrderList.size(); i++) {
			if ((findDraggableComponent(layoutOrderList.get(i))).isEnabled() && i != index) {
				k = selectIndex;
				selectIndex = i;
				if (k < index && selectIndex > index)
					return selectIndex;
				else if (k > index && selectIndex > index)
					return k;
			}
		}

		return selectIndex;
	}

	private DraggableComponent findDraggableComponent(Component c) {
		for (int i = 0; i < draggableComponentList.size(); i++)
			if (draggableComponentList.get(i).getComponent() == c)
				return draggableComponentList.get(i);

		return null;
	}

	private DirectionLayout getDirectionLayout() {
		return (DirectionLayout) componentBox.getLayout();
	}

	private void initialize() {
		if (componentContainer != null)
			remove(componentContainer);

		DirectionLayout layout = getDirectionLayout();
		layout.setCompressing(!scrollEnabled);

		if (scrollEnabled) {
			scrollButtonBox = new ScrollButtonBox();

			final ScrollableBox scrollableBox = new ScrollableBox(componentBox);
			scrollableBox.setLayoutOrderList(layoutOrderList);
			scrollButtonBox.addListener(new ScrollButtonBoxListener() {
				public void scrollButton1() {
					scrollableBox.scrollLeft(1);
				}

				public void scrollButton2() {
					scrollableBox.scrollRight(1);
				}
			});

			scrollableBox.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					scrollButtonBox.setButton1Enabled(!scrollableBox.isLeftEnd());
					scrollButtonBox.setButton2Enabled(!scrollableBox.isRightEnd());
				}
			});

			scrollButtonBox.setButton1Enabled(!scrollableBox.isLeftEnd());
			scrollButtonBox.setButton2Enabled(!scrollableBox.isRightEnd());

			scrollableBox.addScrollableBoxListener(new ScrollableBoxListener() {
				public void scrolledLeft(ScrollableBox box) {
					scrollButtonBox.setButton1Enabled(!box.isLeftEnd());
					scrollButtonBox.setButton2Enabled(true);
				}

				public void scrolledRight(ScrollableBox box) {
					scrollButtonBox.setButton1Enabled(true);
					scrollButtonBox.setButton2Enabled(!box.isRightEnd());
				}

				public void changed(ScrollableBox box) {
					fireChangedEvent();
				}
			});
			componentContainer = scrollableBox;
		} else {
			scrollButtonBox = null;
			componentContainer = componentBox;
		}

		componentContainer.setAlignmentY(0);
		add(componentContainer, BorderLayout.CENTER);

		revalidate();
	}

	private void ensureSelectedVisible() {
		SwingUtilities.invokeLater(() -> {
			if (scrollEnabled && selectedComponent != null) {
				((ScrollableBox) componentContainer).ensureVisible(
						layoutOrderList.indexOf(selectedComponent.getComponent()));
			}
		});
	}

	private void fireDraggedEvent(DraggableComponentEvent e) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e,
					SwingUtilities.convertPoint(e.getSource().getComponent(), e.getMouseEvent().getPoint(), this));
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentDragged(event);
		}
	}

	private void fireDroppedEvent(DraggableComponentEvent e) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e,
					SwingUtilities.convertPoint(e.getSource().getComponent(), e.getMouseEvent().getPoint(), this));
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentDropped(event);
		}
	}

	private void fireNotDroppedEvent(DraggableComponentEvent e) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentDragAborted(event);
		}
	}

	private void fireSelectedEvent(DraggableComponent component, DraggableComponent oldDraggableComponent) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component, oldDraggableComponent);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentSelected(event);
		}
	}

	private void fireAddedEvent(DraggableComponent component) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentAdded(event);
		}
	}

	private void fireRemovedEvent(DraggableComponent component) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).componentRemoved(event);
		}
	}

	private void fireChangedEvent(DraggableComponentEvent e) {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).changed(event);
		}
	}

	private void fireChangedEvent() {
		if (listeners != null) {
			DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this);
			Object[] l = listeners.toArray();
			for (int i = 0; i < l.length; i++)
				((DraggableComponentBoxListener) l[i]).changed(event);
		}
	}
}