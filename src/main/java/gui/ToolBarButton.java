package gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class ToolBarButton extends JButton implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private Color hoverBackgroundColor = new Color(3, 59, 90).brighter();
	//private Color pressedBackgroundColor = Color.PINK;

	public ToolBarButton(Icon icon) {
		super(icon);
		setProperties();
	}

	public ToolBarButton(String txt) {
		super(txt);
		setProperties();
	}

	private void setProperties() {
		this.addMouseListener(this);
		Border thickBorder = new LineBorder(Color.black);
		this.setBorder(thickBorder);
		//this.setBorder(null);
		this.setBorderPainted(false);
		// this.setPreferredSize(this.getMinimumSize());
		this.setMaximumSize(this.getPreferredSize());
		// this.setSize(10, 10);
		this.setBackground(Color.LIGHT_GRAY);
		this.setContentAreaFilled(false);
		
		this.revalidate();

		// this.setOpaque(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setContentAreaFilled(true);
		this.setBorderPainted(true);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
	}

	/*
	 * @Override protected void paintComponent(Graphics g) { if
	 * (getModel().isPressed()) { g.setColor(pressedBackgroundColor); } else if
	 * (getModel().isRollover()) { g.setColor(hoverBackgroundColor); } else {
	 * g.setColor(getBackground()); } g.fillRect(0, 0, getWidth(), getHeight());
	 * super.paintComponent(g); }
	 * 
	 * @Override public void setContentAreaFilled(boolean b) { }
	 * 
	 * public Color getHoverBackgroundColor() { return hoverBackgroundColor; }
	 * 
	 * public void setHoverBackgroundColor(Color hoverBackgroundColor) {
	 * this.hoverBackgroundColor = hoverBackgroundColor; }
	 * 
	 * public Color getPressedBackgroundColor() { return pressedBackgroundColor;
	 * }
	 * 
	 * public void setPressedBackgroundColor(Color pressedBackgroundColor) {
	 * this.pressedBackgroundColor = pressedBackgroundColor; }
	 */
}
