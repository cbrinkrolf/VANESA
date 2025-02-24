package graph.jung.graphDrawing;

import java.awt.*;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import configurations.GraphSettings;
import edu.uci.ics.jung.visualization.renderers.VertexLabelRenderer;

/**
 * Node label renderer based on a JLabel.
 */
public class MyVertexLabelRenderer extends JLabel implements VertexLabelRenderer, Serializable {
	private static final long serialVersionUID = -165604788205640735L;
	private static final Border noFocusBorder = new EmptyBorder(0, 0, 0, 0);

	private final GraphSettings settings = GraphSettings.getInstance();
	private final Color pickedVertexLabelColor;
	private boolean rotateEdgeLabels;
	private boolean disabled;

	public MyVertexLabelRenderer(Color pickedVertexLabelColor) {
		this(pickedVertexLabelColor, true);
	}

	public MyVertexLabelRenderer(Color pickedVertexLabelColor, boolean rotateEdgeLabels) {
		super();
		this.pickedVertexLabelColor = pickedVertexLabelColor;
		this.rotateEdgeLabels = rotateEdgeLabels;
		setOpaque(true);
		setBorder(noFocusBorder);
	}

	public boolean isRotateEdgeLabels() {
		return rotateEdgeLabels;
	}

	public void setRotateEdgeLabels(boolean rotateEdgeLabels) {
		this.rotateEdgeLabels = rotateEdgeLabels;
	}

	/**
	 * Overrides <code>JComponent.setBackground</code> to assign the unselected-background color to the specified
	 * color.
	 */
	@Override
	public Color getForeground() {
		if (settings != null && settings.isBackgroundColor()) {
			return Color.WHITE;
		}
		return super.getForeground();
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Notification from the <code>UIManager</code> that the look and feel [L&F] has changed. Replaces the current UI
	 * object with the latest version from the
	 * <code>UIManager</code>.
	 *
	 * @see JComponent#updateUI
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		setForeground(null);
		setBackground(null);
	}

	@Override
	public void paint(Graphics g) {
		if (!disabled) {
			super.paint(g);
		}
	}

	@Override
	public <BiologicalNodeAbstract> Component getVertexLabelRendererComponent(JComponent vv, Object value, Font font,
																			  boolean isSelected,
																			  BiologicalNodeAbstract bna) {
		super.setForeground(vv.getForeground());
		if (isSelected)
			setForeground(pickedVertexLabelColor);
		super.setBackground(vv.getBackground());
		if (font != null) {
			// setFont(vv.getFont());
			setFont(font);
		} else {
			setFont(vv.getFont());
		}
		setIcon(null);
		setBorder(noFocusBorder);
		setText(value == null ? "" : value.toString());
		return this;
	}

	/*
	 * The following methods are overridden as a performance measure to to prune
	 * code-paths are often called in the case of renders but which we know are
	 * unnecessary. Great care should be taken when writing your own renderer to
	 * weigh the benefits and drawbacks of overriding methods like these.
	 */

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public boolean isOpaque() {
		final Color back = getBackground();
		Component p = getParent();
		if (p != null) {
			p = p.getParent();
		}
		boolean colorMatch = back != null && p != null && back.equals(p.getBackground()) && p.isOpaque();
		return !colorMatch && super.isOpaque();
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void validate() {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void revalidate() {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void repaint(Rectangle r) {
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		// Strings get interned...
		if ("text".equals(propertyName)) {
			super.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Overridden for performance reasons. See the
	 * <a href="#override">Implementation Note</a> for more information.
	 */
	@Override
	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}
}
