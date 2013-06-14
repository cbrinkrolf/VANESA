package gui;

//import edu.uci.ics.jung.graph.Vertex;
import graph.GraphInstance;
import graph.animations.AnimatedPicking;

import java.awt.Dimension;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

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

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;


public class ElementTree implements TreeSelectionListener {

	JXTree tree = null;
	Hashtable table = new Hashtable();
	DefaultMutableTreeNode node;
	AnimatedPicking picking;
	JScrollPane scrollTree;
	boolean emptyScrollPane = true;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private JPanel p;
	private JCheckBox box = new JCheckBox(
			"Center picked element in the viewport");

	public ElementTree() {
		scrollTree = new JScrollPane();
		scrollTree.setPreferredSize(new Dimension(350, 200));
	}

	public void recalculateTree() {

		GraphInstance graphInstance = new GraphInstance();
		graphInstance.getPathway().getAllNodeDescriptions();
		revalidateTree();

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
		p.setVisible(true);
		p.revalidate();

	}

	public void removeTree() {
		p.setVisible(false);
	}

	public JPanel getScrollTree() {
		MigLayout layout = new MigLayout("", "[grow]", "");
		p = new JPanel();
		p.setLayout(layout);
		p.add(scrollTree, "wrap 10, align center");
		scrollTree.setPreferredSize(new Dimension(300, 200));
		p.add(box, "align left, wrap 8");
		p.add(new JSeparator(), "wrap 5, growx, span,");
		JLabel info = new JLabel(
				"Elements marked by an asterisk (*) contain data base information");
		p.add(info, "wrap 5, align center");
		p.setVisible(false);
		return p;
	}

	public void initTree() {

		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		picking = new AnimatedPicking();

		tree = new JXTree();
		root = new DefaultMutableTreeNode("Pathway Elements");
		model = (DefaultTreeModel) tree.getModel();
		model.setRoot(root);
		node = new DefaultMutableTreeNode("Nodes");

		root.add(node);

		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRolloverEnabled(true);
		tree.addTreeSelectionListener(this);
		// tree.setHighlighters(HighlighterFactory.createSimpleStriping());
		tree.addHighlighter(new ColorHighlighter());
		tree.expandAll();

		HashSet set = pw.getAllNodes();
		Iterator it = set.iterator();

		Vector v = new Vector();
		Hashtable currenTable = new Hashtable();

		int i = 0;
		while (it.hasNext()) {

			Object ob = it.next();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ob;

			v.add(bna.getLabel() + i);
			currenTable.put(bna.getLabel() + i, bna);
			i++;
		}

		Collections.sort(v);
		Iterator it2 = v.iterator();
		i = 0;

		while (it2.hasNext()) {

			String object = it2.next().toString();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) currenTable
					.get(object);

			if (bna.hasBrendaNode() || bna.hasKEGGNode()) {
				node.add(new DefaultMutableTreeNode(bna.getLabel() + " *"));
			} else {
				node.add(new DefaultMutableTreeNode(bna.getLabel()));
			}

			table.put(i, bna);
			i++;
		}

		model.nodeStructureChanged(root);
		model.reload();
	}

	public void updateTree() {

		table.clear();
		model.removeNodeFromParent(node);

		GraphInstance graphInstance = new GraphInstance();
		Pathway pw = graphInstance.getPathway();
		node = new DefaultMutableTreeNode("Nodes");
		root.add(node);

		HashSet set = pw.getAllNodes();
		Iterator it = set.iterator();

		Vector v = new Vector();
		Hashtable currenTable = new Hashtable();

		int i = 0;
		while (it.hasNext()) {

			Object ob = it.next();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) ob;
			v.add(bna.getLabel() + i);
			currenTable.put(bna.getLabel() + i, bna);
			i++;
		}

		Collections.sort(v);
		Iterator it2 = v.iterator();
		i = 0;

		while (it2.hasNext()) {

			String object = it2.next().toString();
			BiologicalNodeAbstract bna = (BiologicalNodeAbstract) currenTable
					.get(object);

			if (bna.hasBrendaNode() || bna.hasKEGGNode()) {
				node.add(new DefaultMutableTreeNode(bna.getLabel() + " *"));
			} else {
				node.add(new DefaultMutableTreeNode(bna.getLabel()));
			}

			table.put(i, bna);
			i++;
		}

		model.nodeStructureChanged(root);
		model.reload();
		tree.expandAll();
	}

	public void valueChanged(TreeSelectionEvent e) {

		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (currentNode == null)
			// Nothing is selected.
			return;

		Object nodeInfo = currentNode.getUserObject();
		if (currentNode.isLeaf() && !nodeInfo.toString().equals("Nodes")) {

			Vertex v = ((BiologicalNodeAbstract) table.get(node
					.getIndex(currentNode))).getVertex();
			picking.animatePicking(v, box.isSelected());
		} else {
			return;
		}

	}

}
