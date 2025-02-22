package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

public class CloseButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 7696441214267807002L;

	private final GraphTab tab;
	private boolean hovered = false;

	public CloseButton(final GraphTab tab) {
		this.tab = tab;
		setPreferredSize(new Dimension(18, 18));
		setToolTipText("close this tab");
		// Make the button looks the same for all Laf's
		setUI(new BasicButtonUI());
		// Make it transparent
		setContentAreaFilled(false);
		setBorder(null);
		setBorderPainted(false);
		// we use the same listener for all buttons
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovered = true;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				hovered = false;
				repaint();
			}
		});
		// Close the proper tab by clicking the button
		addActionListener(this);
	}

	// we don't want to update UI for this button
	@Override
	public void updateUI() {
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setStroke(new BasicStroke(2));
		g2.setColor(hovered ? Color.RED : Color.BLACK);
		int inset = 4;
		int sizeHalf = Math.min(getWidth() - inset * 2, getHeight() - inset * 2) / 2;
		int wHalf = getWidth() / 2;
		int hHalf = getHeight() / 2;
		g2.drawLine(wHalf - sizeHalf, hHalf - sizeHalf, wHalf + sizeHalf, hHalf + sizeHalf);
		g2.drawLine(wHalf - sizeHalf, hHalf + sizeHalf, wHalf + sizeHalf, hHalf - sizeHalf);
		g2.dispose();
	}

	public void actionPerformed(ActionEvent e) {
		MainWindow.getInstance().removeTab(tab.getIndex());
	}
}
