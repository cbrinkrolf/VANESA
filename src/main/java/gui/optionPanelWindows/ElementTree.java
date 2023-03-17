package gui.optionPanelWindows;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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

public class ElementTree implements TreeSelectionListener, ActionListener {
	private JXTree tree = null;
	private final Map<Integer, BiologicalNodeAbstract> table = new HashMap<>();
	private DefaultMutableTreeNode node;
	private final JScrollPane scrollTree;
	private boolean emptyScrollPane = true;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private JPanel panel;
	private final JCheckBox box = new JCheckBox("Center picked element in the viewport");

	public ElementTree() {
		scrollTree = new JScrollPane();
		scrollTree.setPreferredSize(new Dimension(350, 200));
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
		panel.setVisible(true);
		panel.revalidate();
	}

	public void removeTree() {
		panel.setVisible(false);
	}

	public JPanel getScrollTree() {
		MigLayout layout = new MigLayout("", "[grow]", "");
		panel = new JPanel();
		panel.setLayout(layout);
		panel.add(scrollTree, "wrap 10, align center");
		scrollTree.setPreferredSize(new Dimension(300, 200));
		box.setActionCommand("animated");
		box.addActionListener(this);
		panel.add(box, "align left, wrap 8");
		panel.add(new JSeparator(), "wrap 5, growx, span,");
		JLabel info = new JLabel("Elements marked by an asterisk (*) contain data base information");
		panel.add(info, "wrap 5, align center");
		panel.setVisible(false);
		return panel;
	}

	private void initTree() {
		Pathway pw = GraphInstance.getPathway();
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
		Vector<String> v = new Vector<>();
		Map<String, BiologicalNodeAbstract> currentTable = new HashMap<>();
		// sorting
		int i = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				String lbl;
				if (bna.getLabel().length() == 0) {
					lbl = "id_" + bna.getID();
				} else {
					lbl = bna.getLabel();
				}
				v.add(lbl + i);
				currentTable.put(lbl + i, bna);
				i++;
			}
		}
		Collections.sort(v);
		i = 0;
		for (String s : v) {
			BiologicalNodeAbstract bna = currentTable.get(s);
			String lbl;
			if (bna.getLabel().length() == 0) {
				lbl = "id_" + bna.getID();
			} else {
				lbl = bna.getLabel();
			}
			DefaultMutableTreeNode vertexNode;
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

	private void addChildNodes(BiologicalNodeAbstract vertex, DefaultMutableTreeNode n) {
		if (vertex.getAllGraphNodes().isEmpty()) {
			return;
		}
		for (BiologicalNodeAbstract child : vertex.getAllGraphNodes()) {
			if (!vertex.getEnvironment().contains(child)) {
				String lbl;
				if (child.getLabel().length() == 0) {
					lbl = "id_" + child.getID();
				} else {
					lbl = child.getLabel();
				}
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(lbl);
				addChildNodes(child, childNode);
				n.add(childNode);
			}
		}
	}

	private void updateTree() {
		table.clear();
		model.removeNodeFromParent(node);
		Pathway pw = GraphInstance.getPathway();
		if (pw == null) {
			return;
		}
		node = new DefaultMutableTreeNode("Nodes");
		root.add(node);
		Vector<String> v = new Vector<String>();
		Hashtable<String, BiologicalNodeAbstract> currenTable = new Hashtable<String, BiologicalNodeAbstract>();
		int i = 0;
		for (BiologicalNodeAbstract bna : pw.getAllGraphNodes()) {
			if (!bna.isLogical()) {
				String lbl;
				if (bna.getLabel().length() == 0) {
					lbl = "id_" + bna.getID();
				} else {
					lbl = bna.getLabel();
				}
				v.add(lbl + i);
				currenTable.put(lbl + i, bna);
				i++;
			}
		}

		Collections.sort(v);
		i = 0;
		for (String s : v) {
			BiologicalNodeAbstract bna = currenTable.get(s);
			String lbl;
			if (bna.getLabel().length() == 0) {
				lbl = "id_" + bna.getID();
			} else {
				lbl = bna.getLabel();
			}
			DefaultMutableTreeNode vertexNode;
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
					double nodesize = bna.getNodesize();
					for (int i = 0; i < 10; i++) {
						bna.setNodesize(bna.getNodesize() + (0.1 / vv.getScale()));
						vv.repaint();
						try {
							Thread.sleep(50);
						} catch (InterruptedException ignored) {
						}
					}
					for (int i = 0; i < 10; i++) {
						bna.setNodesize(bna.getNodesize() - (0.1 / vv.getScale()));
						vv.repaint();
						try {
							Thread.sleep(50);
						} catch (InterruptedException ignored) {
						}
					}
					// if zoom level changes during animation: reset nodesize
					bna.setNodesize(nodesize);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("animated")) {
			GraphInstance.getPathway().getGraph().setAnimatedPicking(box.isSelected());
		}
	}
}
