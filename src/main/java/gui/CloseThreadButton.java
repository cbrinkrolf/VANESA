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

public class CloseThreadButton extends JButton implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ProgressBar bar;
	
	public CloseThreadButton(ProgressBar bar) {
		this.bar=bar;
		int size = 17;
		setPreferredSize(new Dimension(size, size));
		setToolTipText("cancel");
		// Make the button looks the same for all Laf's
		setUI(new BasicButtonUI());
		// Make it transparent
		setContentAreaFilled(false);
		// No need to be focusable
		setFocusable(false);
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(true);
		// Making nice rollover effect
		// we use the same listener for all buttons
		addMouseListener(buttonMouseListener);
		setRolloverEnabled(true);
		// Close the proper tab by clicking the button
		addActionListener(this);
	}

	// we don't want to update UI for this button
	@Override
	public void updateUI() {
	}

	// paint the cross
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
			g2.setColor(Color.RED);
		}
		int delta = 6;
		g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta
				- 1);
		g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta
				- 1);
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
				button.setBorderPainted(true);
			}
		}
	};

	public void actionPerformed(ActionEvent e) {
		bar.closeThread();
	}
}
