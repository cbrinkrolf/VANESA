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

// $Id: TitledTab.java,v 1.89 2009/02/05 15:57:56 jesper Exp $
package net.infonode.tabbedpanel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;

import net.infonode.gui.InsetsUtil;
import net.infonode.gui.border.FocusBorder;
import net.infonode.gui.layout.StackableLayout;
import net.infonode.gui.panel.BaseContainer;
import net.infonode.tabbedpanel.border.TabAreaLineBorder;
import net.infonode.tabbedpanel.border.TabHighlightBorder;

/**
 * <p>A TitledTab is a tab that has support for text, icon and a custom Swing component
 * (called title component). Titled tab supports several properties that makes it possible to change the look (borders,
 * colors, insets), layout (up, down, left, right).</p>
 *
 * <p>Titled tab has a line based layout, i.e. the text, icon and title component are
 * laid out in a line. The layout of the tab can be rotated, i.e. the text and the icon will be rotated 90, 180 or 270
 * degrees. The title component will not be rotated but moved so that the line layout will persist.</p>
 *
 * <p>A titled tab has 3 rendering states:
 * <ul>
 * <li>Normal - The tab is selectable but not yet selected
 * <li>Highlighted - The tab is either highlighted or selected
 * <li>Disabled - The tab is disabled and cannot be selected or highlighted
 * </ul>Most of the properties for the tab can be configured for each of the tab rendering
 * states.</p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.89 $
 */
public class TitledTab extends Tab {
	private static final PanelUI UI = new PanelUI() {
	};

	private class StatePanel extends BaseContainer {
		private final BaseContainer panel = new BaseContainer();
		private final BaseContainer titleComponentPanel = new BaseContainer(false, new BorderLayout());
		private final JLabel label = new JLabel() {
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				String text = getText();
				Icon tmpIcon = getIcon();
				if (text == null || tmpIcon == null) {
					setText(" ");
					// TODO MF: setIcon(icon);
					d = new Dimension(d.width, super.getPreferredSize().height);

					setText(text);
					setIcon(tmpIcon);
				}
				return d;
			}
		};
		private JComponent titleComponent;

		public StatePanel(Border focusBorder) {
			super(false, new BorderLayout());

			label.setBorder(focusBorder);
			label.setMinimumSize(new Dimension(0, 0));
			label.setIconTextGap(UIManager.getInt("TabbedPane.textIconGap"));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.CENTER);

			panel.add(label, BorderLayout.CENTER);
			add(panel, BorderLayout.CENTER);

			panel.setForcedOpaque(true);
		}

		public void setTitleComponent(JComponent titleComponent) {
			JComponent oldTitleComponent = this.titleComponent;
			this.titleComponent = null;
			if (oldTitleComponent != null && oldTitleComponent.getParent() == titleComponentPanel)
				titleComponentPanel.remove(oldTitleComponent);
			this.titleComponent = titleComponent;
			updateLayout();
		}

		public void activateTitleComponent() {
			if (titleComponent != null) {
				if (titleComponent.getParent() != titleComponentPanel) {
					if (titleComponent.getParent() != null)
						titleComponent.getParent().remove(titleComponent);
					titleComponentPanel.add(titleComponent, BorderLayout.CENTER);
				}
			} else {
				titleComponentPanel.removeAll();
			}
		}

		public void activate() {
			remove(panel);
			eventPanel.add(panel, BorderLayout.CENTER);
			add(eventPanel, BorderLayout.CENTER);
		}

		public void deactivate() {
			remove(eventPanel);
			eventPanel.remove(panel);
			add(panel, BorderLayout.CENTER);
		}

		public Dimension getPreferredSize() {
			activateTitleComponent();

			return getAdjustedSize(super.getPreferredSize());
		}

		public Dimension getMinimumSize() {
			activateTitleComponent();

			return getAdjustedSize(super.getMinimumSize());
		}

		public Dimension getMaximumSize() {
			activateTitleComponent();
			return super.getMaximumSize();
		}

		private Dimension getAdjustedSize(Dimension d) {
			return d;
		}

		private void updateLayout() {
			if (titleComponent != null) {
				panel.remove(titleComponentPanel);
				panel.add(titleComponentPanel, BorderLayout.EAST);
				titleComponentPanel.setBorder(new EmptyBorder(0, UIManager.getInt("TabbedPane.textIconGap"), 0, 0));

				panel.revalidate();
			} else {
				panel.remove(titleComponentPanel);
				titleComponentPanel.removeAll();

				panel.revalidate();
			}
		}

		public void setBorders(Border outerBorder, Border innerBorder) {
			setBorder(outerBorder);
			panel.setBorder(innerBorder);
		}

		public void setForegroundColor(final Color color) {
			label.setForeground(color);
			setForeground(color);
		}

		public void setBackgroundColor(final Color color) {
			panel.setBackground(color);
		}

		public String getText() {
			return label.getText();
		}

		public void setText(final String text) {
			label.setText(text);
		}

		public Icon getIcon() {
			return label.getIcon();
		}

		public void setIcon(final Icon icon) {
			label.setIcon(icon);
		}
	}

	private final BaseContainer eventPanel = new BaseContainer(false, new BorderLayout()) {

		public boolean contains(int x, int y) {
			return getComponentCount() > 0 && getComponent(0).contains(x, y);
		}

		public boolean inside(int x, int y) {
			return getComponentCount() > 0 && getComponent(0).inside(x, y);
		}

	};

	public boolean contains(int x, int y) {
		Point p = SwingUtilities.convertPoint(this, new Point(x, y), eventPanel);
		return eventPanel.contains(p.x, p.y);
	}

	public boolean inside(int x, int y) {
		Point p = SwingUtilities.convertPoint(this, new Point(x, y), eventPanel);
		return eventPanel.inside(p.x, p.y);
	}

	private final StatePanel normalStatePanel;
	private final StatePanel highlightedStatePanel;
	private final StatePanel disabledStatePanel;

	private ArrayList<MouseListener> mouseListeners;
	private ArrayList<MouseMotionListener> mouseMotionListeners;
	private final StackableLayout layout;
	private StatePanel currentStatePanel;

	/**
	 * Constructs a TitledTab with a text, icon, content component and title component.
	 *
	 * @param text             text or null for no text. The text will be applied to the normal state properties
	 * @param icon             icon or null for no icon. The icon will be applied to the normal state properties
	 * @param contentComponent content component or null for no content component
	 * @param titleComponent   title component or null for no title component. The title component will be applied to
	 *                         all the states
	 */
	public TitledTab(String text, Icon icon, JComponent contentComponent, JComponent titleComponent) {
		super(contentComponent);
		super.setOpaque(false);

		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				repaint();
			}

			public void focusLost(FocusEvent e) {
				repaint();
			}
		});

		Insets notRaised = new Insets(0, 0, 2, 0);
		Border normalBorder = new EmptyBorder(notRaised);

		Border normalBorder2 = new TabAreaLineBorder();
		Border highlightedBorder = new CompoundBorder(new TabAreaLineBorder(),
				new TabHighlightBorder(TabbedUIDefaults.getHighlight(), true));

		Insets maxInsets = InsetsUtil.max(getBorderInsets(normalBorder2), getBorderInsets(highlightedBorder));
		Insets normalInsets = TabbedUIDefaults.getTabInsets();

		int normalLowered = Math.min(normalInsets.top, 2);
		Border innerNormalBorder = getInnerBorder(normalBorder2, normalInsets, -normalLowered, maxInsets);
		Border innerHighlightBorder = getInnerBorder(highlightedBorder, normalInsets, 2 - normalLowered, maxInsets);
		Border innerDisabledBorder = getInnerBorder(normalBorder2, normalInsets, -normalLowered, maxInsets);

		FocusBorder focusBorder = new FocusBorder(this);
		normalStatePanel = new StatePanel(focusBorder);
		normalStatePanel.setForegroundColor(TabbedUIDefaults.getNormalStateForeground());
		normalStatePanel.setBackgroundColor(TabbedUIDefaults.getNormalStateBackground());
		normalStatePanel.setBorders(normalBorder, innerNormalBorder);
		// TODO MF: normalStatePanel.setInsets(TabbedUIDefaults.getTabInsets());

		highlightedStatePanel = new StatePanel(focusBorder);
		highlightedStatePanel.setForegroundColor(TabbedUIDefaults.getNormalStateForeground());
		highlightedStatePanel.setBackgroundColor(TabbedUIDefaults.getHighlightedStateBackground());
		highlightedStatePanel.setBorders(null, innerHighlightBorder);

		disabledStatePanel = new StatePanel(focusBorder);
		disabledStatePanel.setForegroundColor(TabbedUIDefaults.getDisabledForeground());
		disabledStatePanel.setBackgroundColor(TabbedUIDefaults.getDisabledBackground());
		disabledStatePanel.setBorders(normalBorder, innerDisabledBorder);

		layout = new StackableLayout(this) {
			public void layoutContainer(Container parent) {
				super.layoutContainer(parent);
				StatePanel visibleStatePanel = (StatePanel) getVisibleComponent();
				visibleStatePanel.activateTitleComponent();
			}
		};

		setLayout(layout);

		add(normalStatePanel);
		add(highlightedStatePanel);
		add(disabledStatePanel);

		setText(text);
		setIcon(icon);
		setTitleComponent(titleComponent);

		eventPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				updateFocus(TabSelectTrigger.MOUSE_PRESS);
			}

			public void mouseReleased(MouseEvent e) {
				updateFocus(TabSelectTrigger.MOUSE_RELEASE);
			}

			private void updateFocus(TabSelectTrigger trigger) {
				if (isEnabled() && getTabbedPanel() != null && TabSelectTrigger.MOUSE_PRESS == trigger) {
					Component focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

					if (focusedComponent instanceof TitledTab
							&& ((TitledTab) focusedComponent).getTabbedPanel() == getTabbedPanel())
						requestFocusInWindow();
					else if (isSelected() || TabbedUtils.getParentTabbedPanel(focusedComponent) != getTabbedPanel())
						requestFocusInWindow();
				}
			}
		});

		setEventComponent(eventPanel);

		MouseListener mouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseListener) l[i]).mouseClicked(event);
				}
			}

			public void mousePressed(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseListener) l[i]).mousePressed(event);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseListener) l[i]).mouseReleased(event);
				}
			}

			public void mouseEntered(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseListener) l[i]).mouseEntered(event);
				}
			}

			public void mouseExited(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseListener) l[i]).mouseExited(event);
				}
			}
		};

		MouseMotionListener mouseMotionListener = new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (mouseMotionListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseMotionListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseMotionListener) l[i]).mouseDragged(event);
				}
			}

			public void mouseMoved(MouseEvent e) {
				if (mouseMotionListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					Object[] l = mouseMotionListeners.toArray();
					for (int i = 0; i < l.length; i++)
						((MouseMotionListener) l[i]).mouseMoved(event);
				}
			}
		};

		eventPanel.addMouseListener(mouseListener);
		eventPanel.addMouseMotionListener(mouseMotionListener);

		setFocusableComponent(this);
		layout.setUseSelectedComponentSize(false);
		updateCurrentStatePanel();
	}

	/**
	 * <p>Sets the title component.</p>
	 *
	 * <p>This method is a convenience method for setting the same title component for
	 * all states.</p>
	 *
	 * @param titleComponent the title component or null for no title component
	 */
	public void setTitleComponent(JComponent titleComponent) {
		normalStatePanel.setTitleComponent(titleComponent);
		highlightedStatePanel.setTitleComponent(titleComponent);
		disabledStatePanel.setTitleComponent(titleComponent);
	}

	/**
	 * <p>Sets if this TitledTab should be highlighted or not.</p>
	 *
	 * <p><strong>Note:</strong> This will only have effect if this TitledTab
	 * is enabled and a member of a tabbed panel.</p>
	 *
	 * @param highlighted true for highlight, otherwise false
	 */
	public void setHighlighted(boolean highlighted) {
		super.setHighlighted(highlighted);
		updateCurrentStatePanel();
	}

	/**
	 * <p>
	 * Sets if this TitledTab should be enabled or disabled
	 * </p>
	 *
	 * <p>
	 * <strong>Note:</strong> since ITP 1.5.0 this method will change the enabled property for this tab.
	 * Enabled/disabled can be controlled by modifying the property or this method.
	 * </p>
	 *
	 * @param enabled true for enabled, otherwise false
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateCurrentStatePanel();
	}

	/**
	 * Gets the text
	 *
	 * @return the text or null if no text
	 */
	public String getText() {
		return normalStatePanel.getText();
	}

	/**
	 * Sets the text
	 *
	 * @param text the text or null for no text
	 */
	public void setText(String text) {
		normalStatePanel.setText(text);
		highlightedStatePanel.setText(text);
		disabledStatePanel.setText(text);
	}

	/**
	 * Gets the icon
	 *
	 * @return the icon or null if no icon
	 */
	public Icon getIcon() {
		return normalStatePanel.getIcon();
	}

	/**
	 * Sets the icon
	 *
	 * @param icon the icon or null for no icon
	 */
	public void setIcon(Icon icon) {
		normalStatePanel.setIcon(icon);
		highlightedStatePanel.setIcon(icon);
		disabledStatePanel.setIcon(icon);
	}

	/**
	 * Gets the text for the normal state.
	 *
	 * Same as getText().
	 *
	 * @return the text or null if no text
	 * @see #getText
	 * @since ITP 1.1.0
	 */
	public String toString() {
		return getText();
	}

	/**
	 * Adds a MouseListener to receive mouse events from this TitledTab.
	 *
	 * @param l the MouseListener
	 */
	public synchronized void addMouseListener(MouseListener l) {
		if (mouseListeners == null)
			mouseListeners = new ArrayList<>(2);
		mouseListeners.add(l);
	}

	/**
	 * Removes a MouseListener
	 *
	 * @param l the MouseListener to remove
	 */
	public synchronized void removeMouseListener(MouseListener l) {
		if (mouseListeners != null) {
			mouseListeners.remove(l);

			if (mouseListeners.isEmpty())
				mouseListeners = null;
		}
	}

	/**
	 * Gets the mouse listeners
	 *
	 * @return the mouse listeners
	 */
	public synchronized MouseListener[] getMouseListeners() {
		MouseListener[] listeners = new MouseListener[0];

		if (mouseListeners != null) {
			Object[] l = mouseListeners.toArray();
			listeners = new MouseListener[l.length];
			for (int i = 0; i < l.length; i++)
				listeners[i] = (MouseListener) l[i];
		}

		return listeners;
	}

	/**
	 * Adds a MouseMotionListener to receive mouse events from this TitledTab.
	 *
	 * @param l the MouseMotionListener
	 */
	public synchronized void addMouseMotionListener(MouseMotionListener l) {
		if (mouseMotionListeners == null)
			mouseMotionListeners = new ArrayList<>(2);

		mouseMotionListeners.add(l);
	}

	/**
	 * Removes a MouseMotionListener
	 *
	 * @param l the MouseMotionListener to remove
	 */
	public synchronized void removeMouseMotionListener(MouseMotionListener l) {
		if (mouseMotionListeners != null) {
			mouseMotionListeners.remove(l);

			if (mouseMotionListeners.isEmpty())
				mouseMotionListeners = null;
		}
	}

	/**
	 * Gets the mouse motion listeners
	 *
	 * @return the mouse motion listeners
	 */
	public synchronized MouseMotionListener[] getMouseMotionListeners() {
		MouseMotionListener[] listeners = new MouseMotionListener[0];

		if (mouseMotionListeners != null) {
			Object[] l = mouseMotionListeners.toArray();
			listeners = new MouseMotionListener[l.length];
			for (int i = 0; i < l.length; i++)
				listeners[i] = (MouseMotionListener) l[i];
		}

		return listeners;
	}

	private Insets getBorderInsets(Border border) {
		return border == null ? InsetsUtil.EMPTY_INSETS : border.getBorderInsets(this);
	}

	private Border getInnerBorder(Border border, Insets insets, int raised, Insets maxInsets) {
		if (maxInsets != null)
			insets = InsetsUtil.add(insets, InsetsUtil.sub(maxInsets, getBorderInsets(border)));

		Border innerBorder = new EmptyBorder(InsetsUtil.add(insets, new Insets(raised, 0, 0, 0)));
		return border == null ? innerBorder : new CompoundBorder(border, innerBorder);
	}

	private void updateCurrentStatePanel() {
		StatePanel newStatePanel = normalStatePanel;
		if (!isEnabled())
			newStatePanel = disabledStatePanel;
		else if (isHighlighted())
			newStatePanel = highlightedStatePanel;

		eventPanel.setToolTipText(newStatePanel.getToolTipText());

		if (currentStatePanel != newStatePanel) {
			if (currentStatePanel != null)
				currentStatePanel.deactivate();
			currentStatePanel = newStatePanel;
			currentStatePanel.activate();
		}
		layout.showComponent(currentStatePanel);
	}

	private MouseEvent convertMouseEvent(MouseEvent e) {
		Point p = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), TitledTab.this);
		return new MouseEvent(TitledTab.this, e.getID(), e.getWhen(), e.getModifiers(), (int) p.getX(), (int) p.getY(),
				e.getClickCount(), !e.isConsumed() && e.isPopupTrigger(), e.getButton());
	}

	public void setUI(PanelUI ui) {
		if (getUI() != UI)
			super.setUI(UI);
	}

	public void updateUI() {
		setUI(UI);
	}

	public void setOpaque(boolean opaque) {
		// Ignore
	}
}