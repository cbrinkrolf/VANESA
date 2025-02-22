package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolBarMenuButton extends ToolBarButton {
	/**
	 * Must be non-zero to trigger the mouse exited event of the context menu
	 */
	private static final int CONTEXT_BORDER = 2;
	private final JPopupMenu contextMenu = new JPopupMenu();

	public ToolBarMenuButton(final ImageIcon icon) {
		super(icon);
		initialize();
	}

	public ToolBarMenuButton(final ImageIcon icon, final LayoutManager layout) {
		super(icon);
		contextMenu.setLayout(layout);
		initialize();
	}

	public ToolBarMenuButton(final String text) {
		super(text);
		initialize();
	}

	public ToolBarMenuButton(final String text, final LayoutManager layout) {
		super(text);
		contextMenu.setLayout(layout);
		initialize();
	}

	private void initialize() {
		contextMenu.setBorder(
				BorderFactory.createEmptyBorder(CONTEXT_BORDER, CONTEXT_BORDER, CONTEXT_BORDER, CONTEXT_BORDER));
		contextMenu.setBorderPainted(false);
		contextMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(final MouseEvent e) {
				if (shouldContextMenuClose(e)) {
					contextMenu.setVisible(false);
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(final MouseEvent e) {
				if (!contextMenu.isVisible() && isEnabled()) {
					contextMenu.show(e.getComponent(), -(CONTEXT_BORDER / 2), getHeight());
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				if (shouldContextMenuClose(e)) {
					contextMenu.setVisible(false);
				}
			}

			public void mousePressed(final MouseEvent e) {
				if (!contextMenu.isVisible() && isEnabled()) {
					contextMenu.show(e.getComponent(), -(CONTEXT_BORDER / 2), getHeight());
				}
			}
		});
	}

	private boolean shouldContextMenuClose(final MouseEvent e) {
		final Point p1 = getMousePosition();
		if (p1 != null && p1.x >= 0 && p1.x <= getWidth() && p1.y >= 0 && p1.y <= getHeight()) {
			return false;
		}
		final Point mouse = e.getLocationOnScreen();
		if (!contextMenu.isShowing()) {
			return true;
		}
		final Point contextMenuPos = contextMenu.getLocationOnScreen();
		final Dimension contextMenuSize = contextMenu.getSize();
		return mouse.x < contextMenuPos.x || mouse.x >= contextMenuPos.x + contextMenuSize.width
				|| mouse.y < contextMenuPos.y || mouse.y >= contextMenuPos.y + contextMenuSize.height;
	}

	public void addMenuButton(final ToolBarButton button) {
		contextMenu.add(button);
	}
}
