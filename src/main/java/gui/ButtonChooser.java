package gui;

import java.awt.Color;
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
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class ButtonChooser extends JButton implements ActionListener, MouseListener, FocusListener, AncestorListener {
	private static final long serialVersionUID = 1L;
	private int selectedIndex;
	private final List<JButton> buttons;
	private JWindow chooserPopupWindow;
	private Action action;
	private JPanel popupPanel;

	public ButtonChooser(List<Action> actions) {
		super("");
		this.setAction(actions.get(0));
		selectedIndex = 0;
		buttons = new Vector<>(actions.size());
		// this.visibleButtons = buttons;
		for (Action value : actions) {
			JButton button = new JButton(value);
			buttons.add(button);
			button.addActionListener(this);
		}
		addFocusListener(this);
		addMouseListener(this);
		addAncestorListener(this);

		Border thickBorder = new LineBorder(Color.black);
		setBorder(thickBorder);
		setBorderPainted(false);
		setMaximumSize(getPreferredSize());
		setBackground(Color.LIGHT_GRAY);
		setContentAreaFilled(false);
		revalidate();
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
		chooserPopupWindow.getLayeredPane().add(popupPanel, JLayeredPane.POPUP_LAYER);
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
		popupPanel.remove(source);
		popupPanel.add(buttons.get(selectedIndex));
		selectedIndex = newIdx;
		setAction(source.getAction());
		hidePopup();
	}

	public void mouseClicked(MouseEvent e) {
		if (isEnabled() && e.getSource() == this) {
			// GraphInstance g = new GraphInstance();
			try {
				buttons.get(selectedIndex).getAction().actionPerformed(null);
			} catch (NullPointerException ex) {
				System.err.println("Create Pathway first!");
				PopUpDialog.getInstance().show("Error", "Please create a network first!");
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		if (isEnabled()) {
			setContentAreaFilled(true);
			setBorderPainted(true);
		}
	}

	public void mouseExited(MouseEvent e) {
		if (chooserPopupWindow != null && chooserPopupWindow.isVisible()
				&& !chooserPopupWindow.getBounds().contains(e.getLocationOnScreen())) {
			hidePopup();
		}
		setContentAreaFilled(false);
		setBorderPainted(false);
	}

	public void mousePressed(MouseEvent e) {
		if (isEnabled() && e.getSource() == this) {
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
