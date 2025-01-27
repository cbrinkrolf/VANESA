package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

import net.infonode.tabbedpanel.titledtab.TitledTab;

public class CloseButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 7696441214267807002L;

	public CloseButton() {
		int size = 17;
		setPreferredSize(new Dimension(size, size));
		setToolTipText("close this tab");
		// Make the button looks the same for all Laf's
		setUI(new BasicButtonUI());
		// Make it transparent
		setContentAreaFilled(false);
		setFocusable(false);
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(false);
		// we use the same listener for all buttons
		addMouseListener(buttonMouseListener);
		// Making nice rollover effect
		setRolloverEnabled(true);
		// Close the proper tab by clicking the button
		addActionListener(this);
	}

	// we don't want to update UI for this button
	@Override
	public void updateUI() {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		// shift the image for pressed buttons
		if (getModel().isPressed()) {
			g2.translate(1, 1);
		}
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.WHITE);
		if (getModel().isRollover()) {
			g2.setColor(Color.YELLOW);
		}
		int delta = 6;
		g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
		g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
		g2.dispose();
	}

	private final static MouseListener buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};

	public void actionPerformed(ActionEvent e) {
		final TitledTab tab = (TitledTab) getParent().getParent().getParent().getParent().getParent();
		MainWindow.getInstance().removeTab(tab.getIndex());
	}
}
