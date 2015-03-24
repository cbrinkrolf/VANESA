package gui;

import graph.GraphInstance;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import xmlInput.sbml.JSBMLinput;

public class BuildingBlocks extends JPanel implements TreeSelectionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JXTree tree = new JXTree(new DefaultMutableTreeNode() );
	private JScrollPane scrollTree=new JScrollPane();
	private DefaultTreeModel model;
	private HashMap<DefaultMutableTreeNode, File> map = new HashMap<DefaultMutableTreeNode, File>();
	private JButton reload;
	
	public BuildingBlocks(){				
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRolloverEnabled(true);
		tree.addTreeSelectionListener(this);
		tree.addHighlighter(new ColorHighlighter());
		tree.expandAll();
		tree.setRootVisible(false);
        tree.setCellRenderer(new BBTreeCellRenderer());
        tree.addMouseListener(new MouseAdapter() {
        	   public void mouseClicked(MouseEvent e) {
        	      if(e.getClickCount() == 2) {
        	    	  DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        	         if(map.containsKey(node) && map.get(node).isFile()){
        	        	 JSBMLinput input = new JSBMLinput(GraphInstance.getPathwayStatic());
        	 			input.loadSBMLFile(map.get(node));
        	         }
        	      }
        	   }
        	});
		setLayout(new MigLayout("", "[grow]", ""));
		scrollTree.setPreferredSize(new Dimension(300, 200));
		scrollTree.setViewportView(tree);
		reload = new JButton("reload");
		reload.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				revalidateView();
				
			}
			
		});
		add(scrollTree, "wrap 10, align left");
		add(reload, "wrap 10, align left");
		setVisible(true);
		scrollTree.setVisible(true);
		tree.setVisible(true);
	}
	
	public void revalidateView(){
		tree.removeAll();
		model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
		model.setRoot(root);
		File[] paths;
		paths = File.listRoots();
		for (File p : paths){
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(p);
			root.add(node);
			map.put(node,p);
			buildPaths(node,p);
		}
		model.nodeStructureChanged(root);
		model.reload();
//		tree.expandAll();
		tree.removeTreeSelectionListener(this);
		tree.setSelectionPath(new TreePath(root.getPath()));
		tree.addTreeSelectionListener(this);
	}


	private void buildPaths(DefaultMutableTreeNode node, File p) {
		if(p.listFiles()==null){
			return;
		}
		List<File> fileList = new ArrayList<File>();
		File[] files = p.listFiles();
		for(int i = 0; i<files.length; i++){
			fileList.add(files[i]);
		}
		fileList.sort(new FileSortComparator());
		
		for(File path : fileList){
			if(!path.isHidden() && (path.isDirectory() || path.getName().endsWith("sbml") || path.getName().endsWith("Sbml") || path.getName().endsWith("SBML"))){
				DefaultMutableTreeNode n = new DefaultMutableTreeNode(path.getName());
				node.add(n);
				map.put(n,path);
			}
		}
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
		if(map.containsKey(node) && map.get(node).isDirectory()){
			node.removeAllChildren();
			buildPaths(node, map.get(node));
		}
	}

	public void removeTree() {
		tree.removeAll();
	}
	
	 // this is what you want
    private class BBTreeCellRenderer extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 18665434567L;

		@Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                if(!map.containsKey(node)){
                	setIcon(UIManager.getIcon("FileView.fileIcon"));
                	return this;
                }
                if (map.get(node).isDirectory()) {
                	if(expanded)
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
    
    private class FileSortComparator implements Comparator<File>{

		@Override
		public int compare(File arg0, File arg1) {
			if(!arg0.isFile() && arg1.isFile())
				return 1;
			if(arg0.isFile() && !arg1.isFile())
				return -1;
			return String.CASE_INSENSITIVE_ORDER.compare(arg0.getName(), arg1.getName());
		}
    	
    }
	
}
