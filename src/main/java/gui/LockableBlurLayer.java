package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class LockableBlurLayer extends JComponent {
	private static final float[] blurMatrix = { 1 / 14f, 2 / 14f, 1 / 14f, 2 / 14f, 2 / 14f, 2 / 14f, 1 / 14f, 2 / 14f,
			1 / 14f };
	private static final long eventMask =
			AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK
					| AWTEvent.KEY_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK;

	private final JComponent view;
	private Component recentFocusOwner;
	private boolean isLocked;
	private boolean isDirty;
	private transient SoftReference<BufferedImage> cachedSubImage;
	private transient SoftReference<BufferedImage> cachedBuffer;

	public LockableBlurLayer(final JComponent view) {
		super.setLayout(new BorderLayout());
		this.view = view;
		super.addImpl(view, null, 0);
		enableEvents(eventMask);
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			}, eventMask);
			return null;
		});
	}

	public JComponent getView() {
		return view;
	}

	@Override
	protected void addImpl(final Component comp, final Object constraints, final int index) {
	}

	@Override
	protected void processFocusEvent(final FocusEvent e) {
		if (e.getID() == FocusEvent.FOCUS_GAINED && e.getOppositeComponent() != null) {
			isDirty = true;
			repaint();
		}
	}

	@SuppressWarnings("unused")
	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(final boolean isLocked) {
		if (this.isLocked == isLocked) {
			return;
		}
		this.isLocked = isLocked;
		final KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		final Component focusOwner = focusManager.getPermanentFocusOwner();
		boolean isFocusInsideLayer = focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, this);
		if (isLocked) {
			if (isFocusInsideLayer && focusManager.getFocusedWindow() == SwingUtilities.getWindowAncestor(this)) {
				recentFocusOwner = focusOwner;
				requestFocusInWindow();
			} else {
				isDirty = true;
			}
			view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			view.setVisible(true);
			if (isFocusInsideLayer && recentFocusOwner != null) {
				recentFocusOwner.requestFocusInWindow();
			}
			recentFocusOwner = null;
			view.setCursor(null);
		}
		repaint();
	}

	@Override
	protected void paintComponent(final Graphics g) {
	}

	@Override
	public void paint(final Graphics g) {
		if (isLocked && g instanceof Graphics2D) {
			final Graphics2D g2 = (Graphics2D) g.create();
			BufferedImage buffer = cachedBuffer == null ? null : cachedBuffer.get();
			final boolean isBufferFormatValid =
					buffer != null && buffer.getWidth() == getWidth() && buffer.getHeight() == getHeight();
			if (!isBufferFormatValid || isDirty) {
				if (!isBufferFormatValid) {
					buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
					cachedBuffer = new SoftReference<>(buffer);
				}
				final Graphics2D bufg = buffer.createGraphics();
				view.setVisible(true);
				super.paint(bufg);
				view.setVisible(false);
				blur(buffer, g2.getClip());
				bufg.dispose();
			}
			g2.drawImage(buffer, 0, 0, null);
			g2.dispose();
			isDirty = false;
		} else {
			super.paint(g);
		}
	}

	public void blur(final BufferedImage buffer, Shape clip) {
		final Rectangle bufferSize = new Rectangle(buffer.getWidth(), buffer.getHeight());
		if (clip == null) {
			clip = bufferSize;
		}
		final Rectangle clipBounds = clip.getBounds().intersection(bufferSize);
		if (clipBounds.isEmpty() || buffer.getWidth() <= clipBounds.x || buffer.getHeight() <= clipBounds.y) {
			return;
		}
		int x = clipBounds.x;
		int y = clipBounds.y;
		int width = clipBounds.width;
		int height = clipBounds.height;
		if (buffer.getWidth() < x + width) {
			width = buffer.getWidth() - x;
		}
		if (buffer.getHeight() < y + height) {
			height = buffer.getHeight() - y;
		}
		BufferedImage subImage = cachedSubImage == null ? null : cachedSubImage.get();
		if (subImage == null || subImage.getWidth() != width || subImage.getHeight() != height) {
			subImage = new BufferedImage(width, height, buffer.getType());
			cachedSubImage = new SoftReference<>(subImage);
		}

		final int[] inPixels = new int[width * height];
		final int[] outPixels = new int[width * height];
		buffer.getRaster().getDataElements(x, y, width, height, inPixels);
		convolve(inPixels, outPixels, width, height);
		buffer.getRaster().setDataElements(x, y, width, height, outPixels);
	}

	private void convolve(final int[] inPixels, final int[] outPixels, final int width, final int height) {
		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float r = 0;
				float g = 0;
				float b = 0;
				for (int row = -1; row <= 1; row++) {
					// Clamp edges
					final int iy = Math.max(0, Math.min(height - 1, y + row)) * width;
					final int matrixOffset = 3 * (row + 1) + 1;
					for (int col = -1; col <= 1; col++) {
						// Clamp edges
						final int ix = Math.max(0, Math.min(width - 1, x + col));
						final float f = blurMatrix[matrixOffset + col];
						final int rgb = inPixels[iy + ix];
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				final int ir = Math.max(0, Math.min(255, (int) (r + 0.5))) << 16;
				final int ig = Math.max(0, Math.min(255, (int) (g + 0.5))) << 8;
				final int ib = Math.max(0, Math.min(255, (int) (b + 0.5)));
				outPixels[index++] = (255 << 24) | ir | ig | ib;
			}
		}
	}
}
