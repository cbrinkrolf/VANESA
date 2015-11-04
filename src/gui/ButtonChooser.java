/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * 
 * @author star
 */
public class ButtonChooser extends ToolBarButton implements ActionListener,
		MouseListener, FocusListener, AncestorListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int selectedIndex;
	private List<JButton> buttons;
	private JWindow chooserPopupWindow;
	//private boolean popupInited;
	private Action action;
	//private List<JButton> visibleButtons;
	private JPanel popupPanel;

	public ButtonChooser(List<Action> actions) {
		super("");
		this.setAction(actions.get(0));
		selectedIndex = 0;
		buttons = new Vector<JButton>(actions.size());
		//this.visibleButtons = buttons;
		JButton button;
		for (int i = 0; i < actions.size(); i++) {
			button = new JButton(actions.get(i));
			buttons.add(button);
			button.addActionListener(this);
		}
		addFocusListener(this);
		addMouseListener(this);
		addAncestorListener(this);
	}

	private void initPopup() {
		popupPanel = new JPanel();
		GridLayout layout = new GridLayout(buttons.size() - 1, 1);
		popupPanel.setLayout(layout);
		for (int i = 1; i < buttons.size(); i++) {
			popupPanel.add(buttons.get(i));
		}
		chooserPopupWindow = new JWindow(getParentWindow(this));
		Dimension dim = popupPanel.getPreferredSize();
		dim.height = getHeight();
		popupPanel.setSize(dim);
		popupPanel.setLocation(0, 0);
		popupPanel.setVisible(true);
		chooserPopupWindow.setFocusableWindowState(false);
		chooserPopupWindow.addFocusListener(this);
		chooserPopupWindow.setSize(popupPanel.getSize());
		chooserPopupWindow.getLayeredPane().add(popupPanel,
				JLayeredPane.POPUP_LAYER);
	}

	public void showPopup() {
		if (this.popupPanel == null) {
			this.initPopup();
		}
		Point location = new Point(this.getLocationOnScreen());
		location.translate(-this.popupPanel.getPreferredSize().width, 0);
		chooserPopupWindow.setLocation(location);
		chooserPopupWindow.setVisible(true);
	}

	public void hidePopup() {
		if (chooserPopupWindow != null) {
			chooserPopupWindow.setVisible(false);
		}
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		hidePopup();
	}

	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		int newIdx = buttons.indexOf(source);
		this.popupPanel.remove(source);
		this.popupPanel.add(buttons.get(selectedIndex));
		this.selectedIndex = newIdx;
		this.setAction(source.getAction());
		hidePopup();
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this) {
			//GraphInstance g = new GraphInstance();
			try {
				this.buttons.get(this.selectedIndex).getAction()
						.actionPerformed(null);
			} catch (NullPointerException ex) {
				System.err.println("Create Pathway first!");
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		if (chooserPopupWindow != null
				&& chooserPopupWindow.isVisible()
				&& !this.chooserPopupWindow.getBounds().contains(
						e.getLocationOnScreen())) {
			hidePopup();
		}
		super.mouseExited(e);
	}

	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this) {
			if (chooserPopupWindow != null && chooserPopupWindow.isVisible()) {
				hidePopup();
			} else {
				showPopup();
			}
		}
	}

	public void ancestorMoved(AncestorEvent event) {
		hidePopup();
	}

	public void ancestorAdded(AncestorEvent event) {
	}

	public void ancestorRemoved(AncestorEvent event) {
	}

	private Window getParentWindow(Component owner) {
		Window window = null;

		if (owner instanceof Window) {
			window = (Window) owner;
		} else if (owner != null) {
			window = SwingUtilities.getWindowAncestor(owner);
		}
		if (window == null) {
			window = new Frame();
		}
		return window;
	}

	@Override
	public void setAction(Action action) {
		this.action = action;
		this.setIcon((Icon) action.getValue(Action.SMALL_ICON));
		setToolTipText((String) action.getValue(Action.SHORT_DESCRIPTION));
	}

	@Override
	public Action getAction() {
		return action;
	}
}
// public int getSelectedIndex() {
// return selectedIndex;
// }
//
// // public void setSelectedIndex(int index) {
// // if (index >= 0 && index < buttons.size()) {
// // selectedIndex = index;
// // setIcon(visibleButtons.get(selectedIndex).getIcon());
// // }
// // }
//
// public JButton getSelected() {
// if (buttons == null || selectedIndex >= buttons.size()) {
// return null;
// }
// return visibleButtons.get(selectedIndex);
// }
//
// public void setSelected(JButton button) {
// int index = visibleButtons.indexOf(button);
// if (index != -1) {
// selectedIndex = index;
// setIcon(((JButton) visibleButtons.get(selectedIndex)).getIcon());
// }
// }
