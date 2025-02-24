package gui.optionPanelWindows;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import biologicalElements.Pathway;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import net.miginfocom.swing.MigLayout;

public class PathwayTree extends JPanel implements TreeSelectionListener {
	private final JXTree tree = new JXTree(new DefaultMutableTreeNode());
	private final HashMap<DefaultMutableTreeNode, Pathway> map = new HashMap<>();
	private DefaultMutableTreeNode actualNode;

	public PathwayTree() {
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRolloverEnabled(true);
		tree.addTreeSelectionListener(this);
		tree.addHighlighter(new ColorHighlighter());
		tree.expandAll();
		setLayout(new MigLayout("ins 0, wrap, fill"));
		final JScrollPane scrollTree = new JScrollPane();
		scrollTree.setViewportView(tree);
		add(scrollTree, "growx, height 200:200:200");
		setVisible(false);
	}

	private void buildTree(Pathway rootPW, DefaultMutableTreeNode root) {
		ArrayList<Pathway> children = rootPW.getChildren();
		for (Pathway pw : children) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(pw.getTitle());
			map.put(node, pw);
			root.add(node);
			if (GraphInstance.getPathway().equals(pw))
				actualNode = node;
			buildTree(pw, node);
		}
	}

	public void revalidateView() {
		tree.removeAll();
		actualNode = null;
		final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		final Pathway pw = GraphInstance.getPathway();
		if (pw == null)
			return;
		final Pathway rootPW = pw.getRootPathway();
		final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Pathways Tree:");
		model.setRoot(root);
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootPW.getTitle());
		root.add(node);
		buildTree(rootPW, node);
		if (actualNode == null)
			actualNode = node;
		actualNode.setUserObject(pw.getTitle() + "*");
		map.put(node, rootPW);
		model.nodeStructureChanged(root);
		model.reload();
		tree.expandAll();
		tree.removeTreeSelectionListener(this);
		tree.setSelectionPath(new TreePath(actualNode.getPath()));
		tree.addTreeSelectionListener(this);
		setVisible(true);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		MainWindow w = MainWindow.getInstance();
		GraphContainer con = GraphContainer.getInstance();
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		Pathway newPW = map.get(currentNode);
		if (newPW != null) {
			String pwName = w.getCurrentPathway();
			w.removeTab(false);
			w.setCursor(Cursor.WAIT_CURSOR);
			String newPathwayName = con.addPathway(pwName, newPW);
			newPW = con.getPathway(newPathwayName);
			w.addTab(newPW.getTab());
			w.setCursor(Cursor.DEFAULT_CURSOR);
			w.updateAllGuiElements();
		}
	}

	public void removeTree() {
		tree.removeAll();
		setVisible(false);
	}
}
