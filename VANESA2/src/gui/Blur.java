package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;

public class Blur extends JPanel {
	private BlurPane detailPanel;
	private BufferedImage blurBuffer;
	private BufferedImage backBuffer;
	private float alpha = 0.0f;

	Blur(BlurPane detailPanel) {
		setLayout(new GridBagLayout());

		this.detailPanel = detailPanel;
		this.detailPanel.setAlpha(0.0f);
		add(detailPanel, new GridBagConstraints());

		// Should also disable key events...
		addMouseListener(new MouseAdapter() {
		});
	}

	private void createBlur() {
		JRootPane root = SwingUtilities.getRootPane(this);
		blurBuffer = GraphicsUtilities.createCompatibleImage(getWidth(),
				getHeight());
		Graphics2D g2 = blurBuffer.createGraphics();
		root.paint(g2);
		g2.dispose();

		backBuffer = blurBuffer;

		blurBuffer = GraphicsUtilities.createThumbnailFast(blurBuffer,
				getWidth() / 2);
		blurBuffer = new ColorTintFilter(Color.BLACK , (float) 0.40).filter(blurBuffer, null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (isVisible() && blurBuffer != null) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(backBuffer, 0, 0, null);

			g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2.drawImage(blurBuffer, 0, 0, getWidth(), getHeight(), null);
			g2.dispose();
		}
	}
	
	public float getAlpha() {
	    return alpha;
	}

	public void setAlpha(float alpha) {
	    this.alpha = alpha;
	    repaint();
	}

	public void fadeIn() {
	    createBlur();

	    setVisible(true);
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            Animator animator = PropertySetter.createAnimator(
	                400, detailPanel, "alpha", 1.0f);
	            animator.setAcceleration(0.2f);
	            animator.setDeceleration(0.3f);
	            animator.addTarget(
	                new PropertySetter(Blur.this, "alpha", 1.0f));
	            animator.start();
	        }
	    });
	}
	
	public void fadeOut() {
	    createBlur();

	    setVisible(true);
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            Animator animator = PropertySetter.createAnimator(
	                400, detailPanel, "alpha", 0.0f);
	            animator.setAcceleration(0.2f);
	            animator.setDeceleration(0.3f);
	            animator.addTarget(
	                new PropertySetter(Blur.this, "alpha", 0.0f));
	            animator.start();
	        }
	    });
	}
}