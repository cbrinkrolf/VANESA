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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.PanelUI;

import net.infonode.gui.InsetsUtil;
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

	private static class StatePanel extends JPanel {
		private final JPanel panel = new JPanel(new BorderLayout());
		private final JPanel titleComponentPanel = new JPanel(new BorderLayout());
		private final JLabel label = new JLabel();
		private JComponent titleComponent;

		public StatePanel() {
			super(new BorderLayout());
			panel.setOpaque(false);
			label.setIconTextGap(UIManager.getInt("TabbedPane.textIconGap"));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			label.setHorizontalAlignment(JLabel.LEFT);
			label.setVerticalAlignment(JLabel.CENTER);
			panel.add(label, BorderLayout.CENTER);
			add(panel, BorderLayout.CENTER);
		}

		public void setTitleComponent(final JComponent titleComponent) {
			if (this.titleComponent != null && this.titleComponent.getParent() == titleComponentPanel) {
				titleComponentPanel.remove(this.titleComponent);
			}
			this.titleComponent = titleComponent;
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

		public void activateTitleComponent() {
			if (titleComponent != null) {
				if (titleComponent.getParent() != titleComponentPanel) {
					if (titleComponent.getParent() != null) {
						titleComponent.getParent().remove(titleComponent);
					}
					titleComponentPanel.add(titleComponent, BorderLayout.CENTER);
				}
			} else {
				titleComponentPanel.removeAll();
			}
		}

		@Override
		public Dimension getPreferredSize() {
			activateTitleComponent();
			return super.getPreferredSize();
		}

		@Override
		public Dimension getMinimumSize() {
			activateTitleComponent();
			return super.getMinimumSize();
		}

		@Override
		public Dimension getMaximumSize() {
			activateTitleComponent();
			return super.getMaximumSize();
		}

		public void setBorders(final Border outerBorder, final Border innerBorder) {
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

	private final StatePanel statePanel = new StatePanel();
	private final Color normalStateForegroundColor;
	private final Color highlightedStateForegroundColor;
	private final Color disabledStateForegroundColor;
	private final Color normalStateBackgroundColor;
	private final Color highlightedStateBackgroundColor;
	private final Color disabledStateBackgroundColor;
	private final Border normalStateOuterBorder;
	private final Border normalStateInnerBorder;
	private final Border highlightedStateOuterBorder;
	private final Border highlightedStateInnerBorder;
	private final Border disabledStateOuterBorder;
	private final Border disabledStateInnerBorder;
	private ArrayList<MouseListener> mouseListeners;
	private ArrayList<MouseMotionListener> mouseMotionListeners;

	/**
	 * Constructs a TitledTab with a text, icon, content component and title component.
	 *
	 * @param text             text or null for no text. The text will be applied to the normal state properties
	 * @param icon             icon or null for no icon. The icon will be applied to the normal state properties
	 * @param contentComponent content component or null for no content component
	 */
	public TitledTab(final String text, final Icon icon, final JComponent contentComponent) {
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

		final Border normalBorder = new EmptyBorder(new Insets(0, 0, 2, 0));
		final Border normalBorder2 = new TabAreaLineBorder();
		final Border highlightedBorder = new CompoundBorder(new TabAreaLineBorder(),
				new TabHighlightBorder(TabbedUIDefaults.getHighlight(), true));
		final Insets maxInsets = InsetsUtil.max(getBorderInsets(normalBorder2), getBorderInsets(highlightedBorder));
		final Insets normalInsets = TabbedUIDefaults.getTabInsets();
		final int normalLowered = Math.min(normalInsets.top, 2);
		normalStateForegroundColor = TabbedUIDefaults.getNormalStateForeground();
		highlightedStateForegroundColor = TabbedUIDefaults.getNormalStateForeground();
		disabledStateForegroundColor = TabbedUIDefaults.getDisabledForeground();
		normalStateBackgroundColor = TabbedUIDefaults.getNormalStateBackground();
		highlightedStateBackgroundColor = TabbedUIDefaults.getHighlightedStateBackground();
		disabledStateBackgroundColor = TabbedUIDefaults.getDisabledBackground();
		normalStateOuterBorder = normalBorder;
		normalStateInnerBorder = getInnerBorder(normalBorder2, normalInsets, -normalLowered, maxInsets);
		highlightedStateOuterBorder = null;
		highlightedStateInnerBorder = getInnerBorder(highlightedBorder, normalInsets, 2 - normalLowered, maxInsets);
		disabledStateOuterBorder = normalBorder;
		disabledStateInnerBorder = getInnerBorder(normalBorder2, normalInsets, -normalLowered, maxInsets);

		setLayout(new BorderLayout());
		add(statePanel, BorderLayout.CENTER);
		setText(text);
		setIcon(icon);

		MouseListener mouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseListeners);
					for (final var l : listeners)
						l.mouseClicked(event);
				}
			}

			public void mousePressed(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseListeners);
					for (final var l : listeners)
						l.mousePressed(event);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseListeners);
					for (final var l : listeners)
						l.mouseReleased(event);
				}
			}

			public void mouseEntered(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseListeners);
					for (final var l : listeners)
						l.mouseEntered(event);
				}
			}

			public void mouseExited(MouseEvent e) {
				if (mouseListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseListeners);
					for (final var l : listeners)
						l.mouseExited(event);
				}
			}
		};

		MouseMotionListener mouseMotionListener = new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				if (mouseMotionListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseMotionListeners);
					for (final var l : listeners)
						l.mouseDragged(event);
				}
			}

			public void mouseMoved(MouseEvent e) {
				if (mouseMotionListeners != null) {
					MouseEvent event = convertMouseEvent(e);
					final var listeners = new ArrayList<>(mouseMotionListeners);
					for (final var l : listeners)
						l.mouseMoved(event);
				}
			}
		};
		statePanel.addMouseListener(mouseListener);
		statePanel.addMouseMotionListener(mouseMotionListener);
		setFocusableComponent(this);
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
		statePanel.setTitleComponent(titleComponent);
	}

	/**
	 * <p>Sets if this TitledTab should be highlighted or not.</p>
	 *
	 * <p><strong>Note:</strong> This will only have effect if this TitledTab
	 * is enabled and a member of a tabbed panel.</p>
	 *
	 * @param highlighted true for highlight, otherwise false
	 */
	@Override
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
	@Override
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
		return statePanel.getText();
	}

	/**
	 * Sets the text
	 *
	 * @param text the text or null for no text
	 */
	public void setText(String text) {
		statePanel.setText(text);
	}

	/**
	 * Gets the icon
	 *
	 * @return the icon or null if no icon
	 */
	public Icon getIcon() {
		return statePanel.getIcon();
	}

	/**
	 * Sets the icon
	 *
	 * @param icon the icon or null for no icon
	 */
	public void setIcon(Icon icon) {
		statePanel.setIcon(icon);
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
	@Override
	public String toString() {
		return statePanel.getText();
	}

	/**
	 * Adds a MouseListener to receive mouse events from this TitledTab.
	 *
	 * @param l the MouseListener
	 */
	@Override
	public synchronized void addMouseListener(MouseListener l) {
		if (mouseListeners == null) {
			mouseListeners = new ArrayList<>(2);
		}
		mouseListeners.add(l);
	}

	/**
	 * Removes a MouseListener
	 *
	 * @param l the MouseListener to remove
	 */
	@Override
	public synchronized void removeMouseListener(MouseListener l) {
		if (mouseListeners != null) {
			mouseListeners.remove(l);
			if (mouseListeners.isEmpty()) {
				mouseListeners = null;
			}
		}
	}

	/**
	 * Gets the mouse listeners
	 *
	 * @return the mouse listeners
	 */
	@Override
	public synchronized MouseListener[] getMouseListeners() {
		if (mouseListeners != null) {
			return mouseListeners.toArray(new MouseListener[0]);
		}
		return new MouseListener[0];
	}

	/**
	 * Adds a MouseMotionListener to receive mouse events from this TitledTab.
	 *
	 * @param l the MouseMotionListener
	 */
	@Override
	public synchronized void addMouseMotionListener(MouseMotionListener l) {
		if (mouseMotionListeners == null) {
			mouseMotionListeners = new ArrayList<>(2);
		}
		mouseMotionListeners.add(l);
	}

	/**
	 * Removes a MouseMotionListener
	 *
	 * @param l the MouseMotionListener to remove
	 */
	@Override
	public synchronized void removeMouseMotionListener(MouseMotionListener l) {
		if (mouseMotionListeners != null) {
			mouseMotionListeners.remove(l);
			if (mouseMotionListeners.isEmpty()) {
				mouseMotionListeners = null;
			}
		}
	}

	/**
	 * Gets the mouse motion listeners
	 *
	 * @return the mouse motion listeners
	 */
	@Override
	public synchronized MouseMotionListener[] getMouseMotionListeners() {
		if (mouseMotionListeners != null) {
			return mouseMotionListeners.toArray(new MouseMotionListener[0]);
		}
		return new MouseMotionListener[0];
	}

	private Insets getBorderInsets(final Border border) {
		return border == null ? InsetsUtil.EMPTY_INSETS : border.getBorderInsets(this);
	}

	private Border getInnerBorder(final Border border, Insets insets, final int raised, final Insets maxInsets) {
		insets = InsetsUtil.add(insets, InsetsUtil.sub(maxInsets, getBorderInsets(border)));
		return new CompoundBorder(border, new EmptyBorder(InsetsUtil.add(insets, new Insets(raised, 0, 0, 0))));
	}

	private void updateCurrentStatePanel() {
		if (!isEnabled()) {
			statePanel.setForegroundColor(disabledStateForegroundColor);
			statePanel.setBackgroundColor(disabledStateBackgroundColor);
			statePanel.setBorders(disabledStateOuterBorder, disabledStateInnerBorder);
		} else if (isHighlighted()) {
			statePanel.setForegroundColor(highlightedStateForegroundColor);
			statePanel.setBackgroundColor(highlightedStateBackgroundColor);
			statePanel.setBorders(highlightedStateOuterBorder, highlightedStateInnerBorder);
		} else {
			statePanel.setForegroundColor(normalStateForegroundColor);
			statePanel.setBackgroundColor(normalStateBackgroundColor);
			statePanel.setBorders(normalStateOuterBorder, normalStateInnerBorder);
		}
	}

	private MouseEvent convertMouseEvent(MouseEvent e) {
		Point p = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), TitledTab.this);
		return new MouseEvent(TitledTab.this, e.getID(), e.getWhen(), e.getModifiers(), (int) p.getX(), (int) p.getY(),
				e.getClickCount(), !e.isConsumed() && e.isPopupTrigger(), e.getButton());
	}

	@Override
	public void setUI(PanelUI ui) {
		if (getUI() != UI) {
			super.setUI(UI);
		}
	}

	@Override
	public void updateUI() {
		setUI(UI);
	}
}