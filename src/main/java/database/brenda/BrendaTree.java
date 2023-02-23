package database.brenda;

import biologicalObjects.nodes.BiologicalNodeAbstract;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;

public class BrendaTree {
    private final DefaultTreeModel model;
    private final DefaultMutableTreeNode root;
    private final HashMap<BiologicalNodeAbstract, DefaultMutableTreeNode> enzyme2node = new HashMap<>();
    private final HashMap<DefaultMutableTreeNode, BiologicalNodeAbstract> node2enzyme = new HashMap<>();

    public BrendaTree() {
        root = new DefaultMutableTreeNode("BRENDA Pathway");
        model = new DefaultTreeModel(root);
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public void addNode(DefaultMutableTreeNode parentNode, DefaultMutableTreeNode newChildNode,
                        BiologicalNodeAbstract bna) {
        parentNode.add(newChildNode);
        model.nodeStructureChanged(root);
        model.reload();
        if (!enzyme2node.containsKey(bna))
            enzyme2node.put(bna, newChildNode);
        if (!node2enzyme.containsKey(newChildNode))
            node2enzyme.put(newChildNode, bna);
    }
}
