package gui.optionPanelWindows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import graph.GraphInstance;
import net.miginfocom.swing.MigLayout;
import io.sbml.JSBMLInput;

public class BuildingBlocks implements TreeSelectionListener {
    private final JXTree tree = new JXTree(new DefaultMutableTreeNode());
    private final Map<DefaultMutableTreeNode, File> map = new HashMap<>();
    private final JPanel panel = new JPanel();

    public BuildingBlocks() {
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRolloverEnabled(true);
        tree.addTreeSelectionListener(this);
        tree.addHighlighter(new ColorHighlighter());
        tree.expandAll();
        tree.setRootVisible(false);
        tree.setCellRenderer(new BBTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (map.containsKey(node) && map.get(node).isFile()) {
                        JSBMLInput input = new JSBMLInput(GraphInstance.getPathway());
                        try {
                            input.loadSBMLFile(new FileInputStream(map.get(node)), map.get(node));
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        panel.setLayout(new MigLayout("", "[grow]", ""));
        JScrollPane scrollTree = new JScrollPane();
        scrollTree.setPreferredSize(new Dimension(300, 200));
        scrollTree.setViewportView(tree);
        JButton reload = new JButton("reload");
        reload.addActionListener(arg0 -> revalidateView());
        panel.add(scrollTree, "wrap 10, align left");
        panel.add(reload, "wrap 10, align left");
        panel.setVisible(true);
        scrollTree.setVisible(true);
        tree.setVisible(true);
    }

    public void revalidateView() {
        tree.removeAll();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        model.setRoot(root);
        File[] paths;
        paths = File.listRoots();
        for (File p : paths) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(p);
            root.add(node);
            map.put(node, p);
            buildPaths(node, p);
        }
        model.nodeStructureChanged(root);
        model.reload();
        //		tree.expandAll();
        tree.removeTreeSelectionListener(this);
        tree.setSelectionPath(new TreePath(root.getPath()));
        tree.addTreeSelectionListener(this);
        panel.setVisible(true);
    }


    private void buildPaths(DefaultMutableTreeNode node, File p) {
        File[] files = p.listFiles();
        if (files == null) {
            return;
        }
        List<File> fileList = new ArrayList<>();
        Collections.addAll(fileList, files);
        fileList.sort(new FileSortComparator());
        for (File path : fileList) {
            if (!path.isHidden() && (path.isDirectory() || path.getName().endsWith("sbml") || path.getName().endsWith(
                    "Sbml") || path.getName().endsWith("SBML"))) {
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(path.getName());
                node.add(n);
                map.put(n, path);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (map.containsKey(node) && map.get(node).isDirectory()) {
            node.removeAllChildren();
            buildPaths(node, map.get(node));
        }
    }

    public void removeTree() {
        tree.removeAll();
        panel.setVisible(false);
    }

    private class BBTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 18665434567L;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if (!map.containsKey(node)) {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                    return this;
                }
                if (map.get(node).isDirectory()) {
                    if (expanded)
                        setIcon(UIManager.getIcon("Tree.openIcon"));
                    else
                        setIcon(UIManager.getIcon("Tree.closedIcon"));
                } else {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
            }
            return this;
        }
    }

    private static class FileSortComparator implements Comparator<File> {
        @Override
        public int compare(File arg0, File arg1) {
            if (!arg0.isFile() && arg1.isFile())
                return 1;
            if (arg0.isFile() && !arg1.isFile())
                return -1;
            return String.CASE_INSENSITIVE_ORDER.compare(arg0.getName(), arg1.getName());
        }
    }

    public JPanel getPanel() {
        panel.setVisible(false);
        return panel;
    }
}
