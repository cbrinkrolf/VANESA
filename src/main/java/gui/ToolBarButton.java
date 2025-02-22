package gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ToolBarButton extends JButton implements MouseListener {
	private static final long serialVersionUID = 1L;

	public ToolBarButton(ImageIcon icon) {
		super(ImagePath.scaleIcon(icon, 32));
		setProperties();
	}

	public ToolBarButton(String txt) {
		super(txt);
		setProperties();
	}

	private void setProperties() {
		addMouseListener(this);
		setBorder(new LineBorder(Color.black));
		setBorderPainted(false);
		setFocusPainted(false);
		setBackground(Color.LIGHT_GRAY);
		setContentAreaFilled(false);
		revalidate();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(32, 32);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(32, 32);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(32, 32);
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
		if (isEnabled()) {
			setContentAreaFilled(true);
			setBorderPainted(true);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setContentAreaFilled(false);
		setBorderPainted(false);
	}

	public static ToolBarButton create(String imageFileName, String toolTipText, Runnable action) {
		final ToolBarButton button = new ToolBarButton(ImagePath.getInstance().getImageIcon(imageFileName));
		button.setToolTipText(toolTipText);
		button.addActionListener(e -> action.run());
		return button;
	}
}
