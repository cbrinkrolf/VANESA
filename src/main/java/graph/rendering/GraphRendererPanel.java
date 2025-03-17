package graph.rendering;

import biologicalObjects.nodes.petriNet.Place;
import graph.Graph;
import graph.GraphEdge;
import graph.GraphEdgeLineStyle;
import graph.GraphNode;
import graph.rendering.shapes.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class GraphRendererPanel<V extends GraphNode, E extends GraphEdge<V>> extends JPanel {
	private static final int SATELLITE_BUTTON_SIZE = 20;
	private static final int MAX_SATELLITE_SIZE = 200;
	private static final float MIN_ZOOM = 0.001f;
	private static final int EDGE_START_TIP_OFFSET = 5;
	private static final int EDGE_END_TIP_OFFSET = 5;
	private static final Font TOKEN_FONT = new Font("Arial", Font.PLAIN, 12);

	private final Graph<V, E> graph;
	private final JButton toggleSatelliteViewButton;
	private final JButton zoomAndCenterButton;
	private boolean satelliteVisible = true;
	private float zoom = 1;
	private float offsetX = 0;
	private float offsetY = 0;
	private float scrollSpeed = 0.1f;
	private final Timer timer = new Timer(1000 / 60, e -> repaint());
	private Point2D mousePressedStartPosition = null;
	private Point2D dragStartOffset = null;
	private final Map<V, Point2D> moveStartNodePositions = new HashMap<>();
	private V hoveredNode = null;
	private E hoveredEdge = null;
	private final Set<V> nodesInsideSelectionShape = new HashSet<>();
	private final Set<E> edgesInsideSelectionShape = new HashSet<>();
	private GraphRendererOperation currentOperation = GraphRendererOperation.NONE;

	public GraphRendererPanel(final Graph<V, E> graph) {
		this.graph = graph;
		setLayout(null);
		toggleSatelliteViewButton = new JButton();
		toggleSatelliteViewButton.addActionListener(e -> satelliteVisible = !satelliteVisible);
		toggleSatelliteViewButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(toggleSatelliteViewButton);
		toggleSatelliteViewButton.setBounds(getWidth() - SATELLITE_BUTTON_SIZE, getHeight() - SATELLITE_BUTTON_SIZE,
				SATELLITE_BUTTON_SIZE, SATELLITE_BUTTON_SIZE);
		zoomAndCenterButton = new JButton();
		zoomAndCenterButton.addActionListener(e -> zoomAndCenterGraph(100));
		zoomAndCenterButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(zoomAndCenterButton);
		zoomAndCenterButton.setBounds(getWidth() - SATELLITE_BUTTON_SIZE, getHeight() - SATELLITE_BUTTON_SIZE * 2,
				SATELLITE_BUTTON_SIZE, SATELLITE_BUTTON_SIZE);
		setOpaque(true);
		setDoubleBuffered(true);
		setBackground(Color.WHITE);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				toggleSatelliteViewButton.setBounds(getWidth() - SATELLITE_BUTTON_SIZE,
						getHeight() - SATELLITE_BUTTON_SIZE, SATELLITE_BUTTON_SIZE, SATELLITE_BUTTON_SIZE);
				zoomAndCenterButton.setBounds(getWidth() - SATELLITE_BUTTON_SIZE,
						getHeight() - SATELLITE_BUTTON_SIZE * 2, SATELLITE_BUTTON_SIZE, SATELLITE_BUTTON_SIZE);
			}
		});
		addMouseWheelListener(this::onZoom);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (hoveredNode != null) {
						graph.selectNodes(e.isShiftDown(), hoveredNode);
					}
					if (hoveredEdge != null) {
						graph.selectEdges(e.isShiftDown(), hoveredEdge);
					}
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				mousePressedStartPosition = e.getPoint();
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (hoveredNode != null || hoveredEdge != null) {
						currentOperation = GraphRendererOperation.NODES_MOVE;
						for (final V node : graph.getSelectedNodes()) {
							moveStartNodePositions.put(node, graph.getNodePosition(node));
						}
					} else {
						if (!e.isShiftDown()) {
							graph.clearSelection();
						}
						currentOperation = GraphRendererOperation.RECTANGLE_SELECTION;
					}
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					currentOperation = GraphRendererOperation.VIEWPORT_DRAG;
					dragStartOffset = new Point2D.Float(offsetX, offsetY);
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (currentOperation == GraphRendererOperation.RECTANGLE_SELECTION) {
					graph.selectNodes(true, nodesInsideSelectionShape);
					graph.selectEdges(true, edgesInsideSelectionShape);
				}
				nodesInsideSelectionShape.clear();
				edgesInsideSelectionShape.clear();
				currentOperation = GraphRendererOperation.NONE;
				moveStartNodePositions.clear();
				mousePressedStartPosition = null;
				dragStartOffset = null;
			}
		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				if (currentOperation == GraphRendererOperation.NODES_MOVE) {
					if (mousePressedStartPosition != null) {
						final var movementX = (e.getX() - mousePressedStartPosition.getX()) / zoom;
						final var movementY = (e.getY() - mousePressedStartPosition.getY()) / zoom;
						for (final V node : moveStartNodePositions.keySet()) {
							final var startPosition = moveStartNodePositions.get(node);
							graph.setNodePosition(node, new Point2D.Double(startPosition.getX() + movementX,
									startPosition.getY() + movementY));
						}
					}
				} else if (currentOperation == GraphRendererOperation.VIEWPORT_DRAG) {
					if (dragStartOffset != null && mousePressedStartPosition != null) {
						final var movementX = e.getX() - mousePressedStartPosition.getX();
						final var movementY = e.getY() - mousePressedStartPosition.getY();
						offsetX = (float) (dragStartOffset.getX() + movementX);
						offsetY = (float) (dragStartOffset.getY() + movementY);
					}
				}
			}
		});
		timer.start();
	}

	@Override
	public void setVisible(boolean visible) {
		final boolean previouslyVisible = super.isVisible();
		super.setVisible(visible);
		if (previouslyVisible && !visible) {
			timer.stop();
		} else if (!previouslyVisible && visible) {
			timer.start();
		}
	}

	private void onZoom(final MouseWheelEvent e) {
		final var canvasBounds = getBounds();
		final Point2D lastMousePositionInViewport = new Point2D.Double(e.getX() - canvasBounds.getCenterX(),
				e.getY() - canvasBounds.getCenterY());
		final var preZoomPosition = new Point2D.Double(lastMousePositionInViewport.getX() * zoom,
				lastMousePositionInViewport.getY() * zoom);
		zoom = Math.max(MIN_ZOOM, zoom + (float) (e.getPreciseWheelRotation() * scrollSpeed));
		final var postZoomPosition = new Point2D.Double(lastMousePositionInViewport.getX() * zoom,
				lastMousePositionInViewport.getY() * zoom);
		offsetX += (float) (preZoomPosition.getX() - postZoomPosition.getX());
		offsetY += (float) (preZoomPosition.getY() - postZoomPosition.getY());
	}

	public float getScrollSpeed() {
		return scrollSpeed;
	}

	public void setScrollSpeed(float scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final var g2d = (Graphics2D) g;
		final var savedTransform = g2d.getTransform();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Previous options for image export: evaluate
		// g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		// g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		// g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		// g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		// g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		// g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		// g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		// g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		final var viewportBounds = getBounds();
		final var mousePosition = getMousePosition();
		if (mousePosition != null) {
			mousePosition.setLocation(mousePosition.x - viewportBounds.getWidth() * 0.5f - offsetX,
					mousePosition.y - viewportBounds.getHeight() * 0.5f - offsetY);
			if (zoom < 1) {
				mousePosition.setLocation(mousePosition.x / zoom, mousePosition.y / zoom);
			}
		}
		// Calculate rectangle selection bounds
		Rectangle2D selectionRectangle = null;
		if (currentOperation == GraphRendererOperation.RECTANGLE_SELECTION) {
			if (mousePressedStartPosition != null && mousePosition != null) {
				final Point2D localStartPosition = new Point2D.Double(
						mousePressedStartPosition.getX() - viewportBounds.getWidth() * 0.5f - offsetX,
						mousePressedStartPosition.getY() - viewportBounds.getHeight() * 0.5f - offsetY);
				if (zoom < 1) {
					localStartPosition.setLocation(localStartPosition.getX() / zoom, localStartPosition.getY() / zoom);
				}
				selectionRectangle = new Rectangle2D.Double(Math.min(localStartPosition.getX(), mousePosition.x),
						Math.min(localStartPosition.getY(), mousePosition.y),
						Math.abs(mousePosition.getX() - localStartPosition.getX()),
						Math.abs(mousePosition.getY() - localStartPosition.getY()));
			}
		}

		g2d.setColor(Color.RED);
		g2d.drawString(String.format("Zoom %.3f", zoom), (float) viewportBounds.getWidth() - 70, 10);
		g2d.drawString(currentOperation.toString(), (float) 10, 10);

		// The viewport origin is centered in the JPanel
		g2d.translate(viewportBounds.getWidth() * 0.5f + offsetX, viewportBounds.getHeight() * 0.5f + offsetY);
		// Only zoom via the graphics API if we are zooming out. Otherwise, positions are simply scaled to keep
		// the shape sizes the same.
		if (zoom < 1) {
			g2d.scale(zoom, zoom);
		}
		final var zoomInFactor = Math.max(1, zoom);
		final var zoomOutFactor = Math.min(1, zoom);

		if (mousePosition != null) {
			g2d.drawLine(mousePosition.x - 20, mousePosition.y, mousePosition.x + 20, mousePosition.y);
			g2d.drawLine(mousePosition.x, mousePosition.y - 20, mousePosition.x, mousePosition.y + 20);
		}

		// Determine the max graph bounds including the current viewport offset and bounds
		double minX = -(offsetX / zoom) - getWidth() / zoom * 0.5f;
		double maxX = -(offsetX / zoom) + getWidth() / zoom * 0.5f;
		double minY = -(offsetY / zoom) - getHeight() / zoom * 0.5f;
		double maxY = -(offsetY / zoom) + getHeight() / zoom * 0.5f;
		// Filter for nodes visible in the viewport and find the top-most node the mouse hovers over
		final List<V> visibleNodes = new ArrayList<>();
		V hoveredNode = null;
		final List<V> nodesInsideSelectionShape = new ArrayList<>();
		for (final V node : graph.getNodes()) {
			final var shape = node.getNodeShape();
			final var bounds = shape.getBounds(node);
			final var rawPosition = graph.getNodePosition(node);
			minX = Math.min(minX, rawPosition.getX() + bounds.getX());
			maxX = Math.max(maxX, rawPosition.getX() + bounds.getMaxX());
			minY = Math.min(minY, rawPosition.getY() + bounds.getY());
			maxY = Math.max(maxY, rawPosition.getY() + bounds.getMaxY());
			final var position = transformIfZoomingIn(rawPosition);
			if (selectionRectangle != null) {
				if (selectionRectangle.contains(position.getX(), position.getY())) {
					nodesInsideSelectionShape.add(node);
				}
			}
			// Skip nodes outside the viewport
			final var localTopLeftX =
					(position.getX() + bounds.getX()) * zoomOutFactor + viewportBounds.getCenterX() + offsetX;
			final var localTopLeftY =
					(position.getY() + bounds.getY()) * zoomOutFactor + viewportBounds.getCenterY() + offsetY;
			if (localTopLeftX + bounds.getWidth() * zoomOutFactor < 0 || localTopLeftX > viewportBounds.getWidth()
					|| localTopLeftY + bounds.getHeight() * zoomOutFactor < 0
					|| localTopLeftY > viewportBounds.getHeight()) {
				continue;
			}
			visibleNodes.add(node);
			if (currentOperation != GraphRendererOperation.RECTANGLE_SELECTION && mousePosition != null
					&& shape.isMouseInside(node,
					new Point2D.Double(mousePosition.x - position.getX(), mousePosition.y - position.getY()))) {
				hoveredNode = node;
			}
		}
		this.hoveredNode = hoveredNode;
		this.nodesInsideSelectionShape.clear();
		this.nodesInsideSelectionShape.addAll(nodesInsideSelectionShape);
		// Find the top-most edge the mouse hovers over, but only if no node is hovered
		final double allowedEdgeHoverDistanceSq = 4 * 4; // TODO: from settings
		E hoveredEdge = null;
		final List<E> edgesInsideSelectionShape = new ArrayList<>();
		if (mousePosition != null && hoveredNode == null) {
			for (final E edge : graph.getEdges()) {
				final var fromPosition = transformIfZoomingIn(graph.getNodePosition(edge.getFrom()));
				final var toPosition = transformIfZoomingIn(graph.getNodePosition(edge.getTo()));
				if (selectionRectangle != null) {
					if (selectionRectangle.contains(fromPosition.getX(), fromPosition.getY())
							&& selectionRectangle.contains(toPosition.getX(), toPosition.getY())) {
						edgesInsideSelectionShape.add(edge);
					}
				} else if (currentOperation != GraphRendererOperation.RECTANGLE_SELECTION) {
					final var closestPointOnEdge = closestPointOnLine(fromPosition, toPosition, mousePosition);
					if (closestPointOnEdge != null) {
						final var offsetToEdgeX = mousePosition.x - closestPointOnEdge.getX();
						final var offsetToEdgeY = mousePosition.y - closestPointOnEdge.getY();
						final var distanceToEdgeSq = offsetToEdgeX * offsetToEdgeX + offsetToEdgeY * offsetToEdgeY;
						if (distanceToEdgeSq <= allowedEdgeHoverDistanceSq) {
							hoveredEdge = edge;
						}
					}
				}
			}
		}
		this.hoveredEdge = hoveredEdge;
		this.edgesInsideSelectionShape.clear();
		this.edgesInsideSelectionShape.addAll(edgesInsideSelectionShape);

		// Render edges
		for (final E edge : graph.getEdges()) {
			final var fromShape = edge.getFrom().getNodeShape();
			final var toShape = edge.getTo().getNodeShape();
			final var fromPosition = transformIfZoomingIn(graph.getNodePosition(edge.getFrom()));
			final var toPosition = transformIfZoomingIn(graph.getNodePosition(edge.getTo()));
			final var nodesOffset = new Point2D.Double(toPosition.getX() - fromPosition.getX(),
					toPosition.getY() - fromPosition.getY());
			final var distance = Math.sqrt(nodesOffset.x * nodesOffset.x + nodesOffset.y * nodesOffset.y);
			if (distance == 0) {
				continue;
			}
			// If the nodes are not overlapping, offset the edge start and end positions by the distance
			// from the from/to node centers to their respective shape borders in the direction of the edge
			final var directionVectorForward = new Point2D.Double(nodesOffset.x / distance, nodesOffset.y / distance);
			final var directionVectorBackward = new Point2D.Double(-directionVectorForward.x,
					-directionVectorForward.y);
			final var fromBoundsDistance = fromShape.getBoundsDistance(edge.getFrom(), directionVectorForward)
					+ EDGE_START_TIP_OFFSET;
			final var toBoundsDistance = toShape.getBoundsDistance(edge.getTo(), directionVectorBackward)
					+ EDGE_END_TIP_OFFSET;
			final double startX = fromPosition.getX() + directionVectorForward.x * fromBoundsDistance;
			final double startY = fromPosition.getY() + directionVectorForward.y * fromBoundsDistance;
			final double endX = toPosition.getX() + directionVectorBackward.x * toBoundsDistance;
			final double endY = toPosition.getY() + directionVectorBackward.y * toBoundsDistance;
			if (graph.isSelected(edge)) {
				g.setColor(Color.BLUE);
			} else if (hoveredEdge == edge || edgesInsideSelectionShape.contains(edge)) {
				g.setColor(Color.ORANGE);
			} else {
				g.setColor(edge.getColor());
			}
			if (edge.getLineStyle() == GraphEdgeLineStyle.DASHED) {
				g2d.setStroke(
						new BasicStroke(edge.getLineThickness(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0,
								new float[] { 10, 10 }, 0));
			} else if (edge.getLineStyle() == GraphEdgeLineStyle.DOTTED) {
				g2d.setStroke(new BasicStroke(edge.getLineThickness(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
						new float[] { 3, 3 }, 0));
			} else {
				g2d.setStroke(new BasicStroke(edge.getLineThickness()));
			}
			g.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
			// Render edge start tip
			final EdgeTipShape fromTipShape = edge.getFromTipShape();
			if (fromTipShape != null) {
				g2d.translate(startX, startY);
				fromTipShape.paint(g2d, edge, directionVectorBackward, EDGE_START_TIP_OFFSET);
				g2d.translate(-startX, -startY);
			}
			// Render edge end tip
			final EdgeTipShape toTipShape = edge.getToTipShape();
			if (toTipShape != null) {
				g2d.translate(endX, endY);
				toTipShape.paint(g2d, edge, directionVectorForward, EDGE_END_TIP_OFFSET);
				g2d.translate(-endX, -endY);
			}
			// Render edge label
			final String label = edge.getNetworkLabel();
			final Font labelFont = new Font("Arial", Font.PLAIN, 12); // TODO: from settings
			final var labelBounds = labelFont.getStringBounds(label, g2d.getFontRenderContext());
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(Math.atan2(directionVectorForward.y, directionVectorForward.x),
					labelBounds.getCenterX(), labelBounds.getCenterY());
			Font rotatedFont = labelFont.deriveFont(affineTransform);
			g2d.setFont(rotatedFont);
			double halfwayX = fromPosition.getX() + directionVectorForward.x * distance * 0.5
					+ directionVectorForward.y * labelBounds.getHeight();
			double halfwayY = fromPosition.getY() + directionVectorForward.y * distance * 0.5
					- directionVectorForward.x * labelBounds.getHeight();
			g.drawString(label, (int) (halfwayX - labelBounds.getCenterX()),
					(int) (halfwayY - labelBounds.getCenterY()));
		}

		// Render nodes
		for (final V node : visibleNodes) {
			final var position = transformIfZoomingIn(graph.getNodePosition(node));
			final var shape = node.getNodeShape();
			final var bounds = shape.getBounds(node);
			Color fillColor = node.getColor();
			Color strokeColor = Color.BLACK;
			if (graph.isSelected(node)) {
				strokeColor = Color.BLUE;
			} else if (hoveredNode == node || nodesInsideSelectionShape.contains(node)) {
				strokeColor = Color.ORANGE;
			}
			g2d.translate(position.getX(), position.getY());
			shape.paint(g2d, node, strokeColor, fillColor);
			g2d.translate(-position.getX(), -position.getY());
			// Render token count inside place
			// TODO: generalize
			if (node instanceof Place) {
				final String tokens = getPlaceTokensText((Place) node);
				g2d.setFont(TOKEN_FONT);
				g2d.setColor(Color.BLACK);
				final var tokensBounds = TOKEN_FONT.getStringBounds(tokens, g2d.getFontRenderContext());
				g.drawString(tokens, (int) (position.getX() - tokensBounds.getCenterX()),
						(int) (position.getY() - tokensBounds.getCenterY()));
			}
			// Render node label
			g2d.setColor(strokeColor);
			final String label = node.getNetworkLabel();
			final Font labelFont = new Font("Arial", Font.PLAIN, 12); // TODO: from settings
			g2d.setFont(labelFont);
			final var labelBounds = labelFont.getStringBounds(label, g2d.getFontRenderContext());
			g.drawString(label, (int) (position.getX() + bounds.getMaxX()),
					(int) (position.getY() + bounds.getMaxY() + labelBounds.getHeight()));
		}

		// Render rectangle selection
		if (currentOperation == GraphRendererOperation.RECTANGLE_SELECTION && selectionRectangle != null) {
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Color.BLUE);
			g2d.drawRect((int) selectionRectangle.getX(), (int) selectionRectangle.getY(),
					(int) selectionRectangle.getWidth(), (int) selectionRectangle.getHeight());
		}

		// Restore the transform
		g2d.setTransform(savedTransform);
		// Render satellite view
		if (satelliteVisible) {
			// Size the satellite viewport to match the main viewport aspect ratio with a maximum side length
			final float aspectRatio = getWidth() / (float) getHeight();
			final int satelliteWidth = aspectRatio < 1 ? (int) (MAX_SATELLITE_SIZE * aspectRatio) : MAX_SATELLITE_SIZE;
			final int satelliteHeight = aspectRatio > 1 ? (int) (MAX_SATELLITE_SIZE / aspectRatio) : MAX_SATELLITE_SIZE;
			final int satelliteTopLeftX = getWidth() - satelliteWidth - SATELLITE_BUTTON_SIZE;
			final int satelliteTopLeftY = getHeight() - satelliteHeight;
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Color.WHITE);
			g2d.fillRect(satelliteTopLeftX, satelliteTopLeftY, satelliteWidth, satelliteHeight);
			g2d.setColor(Color.BLACK);
			g2d.drawRect(satelliteTopLeftX, satelliteTopLeftY, satelliteWidth, satelliteHeight);
			g2d.translate(satelliteTopLeftX + satelliteWidth * 0.5f, satelliteTopLeftY + satelliteHeight * 0.5f);
			// Transform the satellite viewport to fit the whole graph centered
			final double graphWidth = maxX - minX;
			final double graphHeight = maxY - minY;
			final float viewportToSatelliteRatio = Math.min(satelliteWidth, satelliteHeight) / (float) Math.max(
					graphWidth, graphHeight);
			g2d.scale(viewportToSatelliteRatio, viewportToSatelliteRatio);
			g2d.translate(-minX - graphWidth * 0.5f, -minY - graphHeight * 0.5f);
			// Render edges
			g2d.setStroke(new BasicStroke(3));
			for (final E edge : graph.getEdges()) {
				final var fromPosition = graph.getNodePosition(edge.getFrom());
				final var toPosition = graph.getNodePosition(edge.getTo());
				g2d.drawLine((int) fromPosition.getX(), (int) fromPosition.getY(), (int) toPosition.getX(),
						(int) toPosition.getY());
			}
			// Render nodes
			for (final V node : graph.getNodes()) {
				final var position = graph.getNodePosition(node);
				final var shape = node.getNodeShape();
				final var bounds = shape.getBounds(node);
				g2d.fillRect((int) (position.getX() + bounds.getX()), (int) (position.getY() + bounds.getY()),
						(int) bounds.getWidth(), (int) bounds.getHeight());
			}
			// Render camera visible area
			g2d.setColor(Color.RED);
			g2d.setStroke(new BasicStroke(3));
			final double cameraRectWidth = getWidth() / zoom;
			final double cameraRectHeight = getHeight() / zoom;
			final double cameraRectX = -(offsetX / zoom) - cameraRectWidth * 0.5f;
			final double cameraRectY = -(offsetY / zoom) - cameraRectHeight * 0.5f;
			g2d.drawRect((int) cameraRectX, (int) cameraRectY, (int) cameraRectWidth, (int) cameraRectHeight);
		}
		// Restore the transform
		g2d.setTransform(savedTransform);
	}

	private Point2D transformIfZoomingIn(final Point2D p) {
		if (zoom < 1) {
			return p;
		}
		return new Point2D.Double(p.getX() * zoom, p.getY() * zoom);
	}

	private String getPlaceTokensText(final Place place) {
		if (place.isLogical() && place.getLogicalReference() instanceof Place) {
			final Place logicalReference = (Place) place.getLogicalReference();
			return logicalReference.isDiscrete() ? String.valueOf((int) logicalReference.getToken()) : String.valueOf(
					logicalReference.getToken());
		}
		return place.isDiscrete() ? String.valueOf((int) place.getToken()) : String.valueOf(place.getToken());
	}

	/**
	 * Find the closest point on a line. If it lies before the start or beyond the end of the line, null is returned.
	 */
	private Point2D closestPointOnLine(final Point2D lineStart, final Point2D lineEnd, final Point2D p) {
		final var positionToStart = new Point2D.Double(p.getX() - lineStart.getX(), p.getY() - lineStart.getY());
		final var direction = new Point2D.Double(lineEnd.getX() - lineStart.getX(), lineEnd.getY() - lineStart.getY());
		final var directionLength = Math.sqrt(direction.x * direction.x + direction.y * direction.y);
		final var positionToStartLength = Math.sqrt(
				positionToStart.x * positionToStart.x + positionToStart.y * positionToStart.y);
		final var angle = Math.acos(
				(positionToStart.x * direction.x + positionToStart.y * direction.y) / (positionToStartLength
						* directionLength));
		final var distanceOnLine = (float) Math.cos(angle) * positionToStartLength;
		if (distanceOnLine < 0 || distanceOnLine > directionLength)
			return null;
		final var scale = distanceOnLine / directionLength;
		return new Point2D.Double(lineStart.getX() + direction.x * scale, lineStart.getY() + direction.y * scale);
	}

	/**
	 * Zoom and center graph in viewport with a default padding of 100.
	 */
	public void zoomAndCenterGraph() {
		zoomAndCenterGraph(100);
	}

	/**
	 * Zoom and center graph in viewport with the specified padding on all sides.
	 */
	public void zoomAndCenterGraph(int padding) {
		if (graph.getNodeCount() == 0) {
			offsetX = 0;
			offsetY = 0;
			zoom = 1f;
		}
		Double minX = null;
		Double maxX = null;
		Double minY = null;
		Double maxY = null;
		for (final V node : graph.getNodes()) {
			final var bounds = node.getNodeShape().getBounds(node);
			final var rawPosition = graph.getNodePosition(node);
			final var nodeMinX = rawPosition.getX() + bounds.getX();
			final var nodeMaxX = rawPosition.getX() + bounds.getMaxX();
			minX = minX == null ? nodeMinX : Math.min(minX, rawPosition.getX() + bounds.getX());
			maxX = maxX == null ? nodeMaxX : Math.max(maxX, rawPosition.getX() + bounds.getMaxX());
			final var nodeMinY = rawPosition.getY() + bounds.getY();
			final var nodeMaxY = rawPosition.getY() + bounds.getMaxY();
			minY = minY == null ? nodeMinY : Math.min(minY, nodeMinY);
			maxY = maxY == null ? nodeMaxY : Math.max(maxY, rawPosition.getY() + bounds.getMaxY());
		}
		minX -= padding;
		minY -= padding;
		maxX += padding;
		maxY += padding;
		final double width = maxX - minX;
		final double height = maxY - minY;
		zoom = (float) Math.min(getWidth() / width, getHeight() / height);
		offsetX = (float) -(minX + width * 0.5f) * zoom;
		offsetY = (float) -(minY + height * 0.5f) * zoom;
	}
}
