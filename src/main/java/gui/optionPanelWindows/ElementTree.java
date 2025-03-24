package gui.optionPanelWindows;

import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import graph.GraphInstance;
import graph.jung.classes.MyVisualizationViewer;
import net.miginfocom.swing.MigLayout;

public class ElementTree extends JPanel implements TreeSelectionListener {
	private JXTree tree = null;
	private final Map<Integer, BiologicalNodeAbstract> table = new HashMap<>();
	private DefaultMutableTreeNode node;
	private final JScrollPane scrollTree;
	private boolean emptyScrollPane = true;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;

	public ElementTree() {
		setLayout(new MigLayout("ins 0, wrap, fill, debug"));
		scrollTree = new JScrollPane();
		add(scrollTree, "growx, height 200:200:200");
		final JCheckBox centerPickedCheckbox = new JCheckBox("Center picked element in the viewport");
		centerPickedCheckbox.addActionListener(
				e -> GraphInstance.getPathway().getGraph().setAnimatedPicking(centerPickedCheckbox.isSelected()));
		add(centerPickedCheckbox, "growx");
		add(new JSeparator(), "growx");
		final JLabel info = new JLabel("Elements marked by an asterisk (*) contain data base information");
		add(info, "growx");
		setVisible(false);
	}

	public void revalidateTree() {
		if (tree != null) {
			updateTree();
		} else {
			initTree();
		}
		if (emptyScrollPane) {
			scrollTree.setViewportView(tree);
			emptyScrollPane = false;
		}
		scrollTree.setVisible(true);
		setVisible(true);
		revalidate();
	}

	public void removeTree() {
		setVisible(false);
	}

	private void initTree() {
		final Pathway pw = GraphInstance.getPathway();
		tree = new JXTree();
		root = new DefaultMutableTreeNode("Pathway Elements");
		model = (DefaultTreeModel) tree.getModel();
		model.setRoot(root);
		node = new DefaultMutableTreeNode("Nodes");
		root.add(node);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRolloverEnabled(true);
		tree.addTreeSelectionListener(this);
		// tree.setHighlighters(HighlighterFactory.createSimpleStriping());
		tree.addHighlighter(new ColorHighlighter());
		tree.expandAll();
		final Vector<String> v = new Vector<>();
		final Map<String, BiologicalNodeAbstract> currentTable = new HashMap<>();
		// sorting
		int i = 0;
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				final String lbl = bna.getLabel().isEmpty() ? "id_" + bna.getID() : bna.getLabel();
				v.add(lbl + i);
				currentTable.put(lbl + i, bna);
				i++;
			}
		}
		Collections.sort(v);
		i = 0;
		for (String s : v) {
			final BiologicalNodeAbstract bna = currentTable.get(s);
			final String lbl = bna.getLabel().isEmpty() ? "id_" + bna.getID() : bna.getLabel();
			final DefaultMutableTreeNode vertexNode;
			if (bna.hasBrendaNode() || bna.hasKEGGNode()) {
				vertexNode = new DefaultMutableTreeNode(lbl + " *");
			} else {
				vertexNode = new DefaultMutableTreeNode(lbl);
			}
			addChildNodes(bna, vertexNode);
			node.add(vertexNode);
			table.put(i, bna);
			i++;
		}
		model.nodeStructureChanged(root);
		model.reload();
	}

	private void addChildNodes(final BiologicalNodeAbstract vertex, final DefaultMutableTreeNode n) {
		if (vertex.getAllGraphNodes().isEmpty()) {
			return;
		}
		for (final BiologicalNodeAbstract child : vertex.getAllGraphNodes()) {
			if (!vertex.getEnvironment().contains(child)) {
				final String lbl = child.getLabel().isEmpty() ? "id_" + child.getID() : child.getLabel();
				final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(lbl);
				addChildNodes(child, childNode);
				n.add(childNode);
			}
		}
	}

	private void updateTree() {
		table.clear();
		model.removeNodeFromParent(node);
		final Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			return;
		}
		node = new DefaultMutableTreeNode("Nodes");
		root.add(node);
		final Vector<String> v = new Vector<>();
		final Hashtable<String, BiologicalNodeAbstract> currenTable = new Hashtable<>();
		int i = 0;
		for (final BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				final String lbl = bna.getLabel().isEmpty() ? "id_" + bna.getID() : bna.getLabel();
				v.add(lbl + i);
				currenTable.put(lbl + i, bna);
				i++;
			}
		}

		Collections.sort(v);
		i = 0;
		for (final String s : v) {
			final BiologicalNodeAbstract bna = currenTable.get(s);
			final String lbl = bna.getLabel().isEmpty() ? "id_" + bna.getID() : bna.getLabel();
			final DefaultMutableTreeNode vertexNode;
			if (bna.hasBrendaNode() || bna.hasKEGGNode()) {
				vertexNode = new DefaultMutableTreeNode(lbl + " *");
			} else {
				vertexNode = new DefaultMutableTreeNode(lbl);
			}
			addChildNodes(bna, vertexNode);
			node.add(vertexNode);
			table.put(i, bna);
			i++;
		}
		model.nodeStructureChanged(root);
		model.reload();
		tree.expandAll();
	}

	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (currentNode == null || currentNode.getParent() == null)
			// Nothing is selected.
			return;
		while (!currentNode.getParent().equals(node)) {
			currentNode = (DefaultMutableTreeNode) currentNode.getParent();
			if (currentNode == null || currentNode.getParent() == null)
				// Nothing is selected.
				return;
		}
		Object nodeInfo = currentNode.getUserObject();
		// if (currentNode.isLeaf() && !nodeInfo.toString().equals("Nodes")) {
		if (!currentNode.isRoot() && !nodeInfo.toString().equals("Nodes")) {
			BiologicalNodeAbstract bna = table.get(node.getIndex(currentNode));
			final MyVisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract> vv = GraphInstance.getPathway()
					.getGraph().getVisualizationViewer();
			// final VisualizationViewer<BiologicalNodeAbstract,BiologicalEdgeAbstract> vv =
			// (VisualizationViewer<BiologicalNodeAbstract, BiologicalEdgeAbstract>)
			// e.getSource();
			// vv.getPickedVertexState().getPicked().size() ;
			// if (vv.getPickedVertexState().getPicked().size() == 1) {
			if (GraphInstance.getPathway().getGraph().isAnimatedPicking()) {
				vv.getPickedVertexState().clear();
				vv.getPickedVertexState().pick(bna, true);
				Layout<BiologicalNodeAbstract, BiologicalEdgeAbstract> layout = vv.getGraphLayout();
				Point2D q = layout.apply(vv.getPickedVertexState().getPicked().iterator().next());
				Point2D lvc = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
				final double dx = (lvc.getX() - q.getX()) / 10;
				final double dy = (lvc.getY() - q.getY()) / 10;
				Runnable animator = () -> {
					for (int i = 0; i < 10; i++) {
						vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
						try {
							Thread.sleep(100);
						} catch (InterruptedException ignored) {
						}
					}
				};
				Thread thread = new Thread(animator);
				thread.start();
				// }
				// }
			} else {
				Runnable animator = () -> {
					double nodesize = bna.getNodeSize();
					for (int i = 0; i < 10; i++) {
						bna.setNodeSize(bna.getNodeSize() + (0.1 / vv.getScale()));
						vv.repaint();
						try {
							Thread.sleep(50);
						} catch (InterruptedException ignored) {
						}
					}
					for (int i = 0; i < 10; i++) {
						bna.setNodeSize(bna.getNodeSize() - (0.1 / vv.getScale()));
						vv.repaint();
						try {
							Thread.sleep(50);
						} catch (InterruptedException ignored) {
						}
					}
					// if zoom level changes during animation: reset nodesize
					bna.setNodeSize(nodesize);
					vv.repaint();
				};
				Thread thread = new Thread(animator);
				thread.start();
				// vv.getPickedVertexState().clear();
				vv.getPickedVertexState().pick(bna, true);
			}
			// picking.animatePicking(v, box.isSelected());
		}
	}
}
