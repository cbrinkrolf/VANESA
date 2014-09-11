package gui;

import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;

import biologicalElements.Pathway;

public class PathwayTree extends JPanel implements TreeSelectionListener{
	
	private JXTree tree = new JXTree(new DefaultMutableTreeNode() );
	private JScrollPane scrollTree=new JScrollPane();
	private DefaultTreeModel model;
	private HashMap<DefaultMutableTreeNode,Pathway> map= new HashMap<DefaultMutableTreeNode,Pathway>();
	private DefaultMutableTreeNode actualNode;
	
	public PathwayTree(){				
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRolloverEnabled(true);
		tree.addTreeSelectionListener(this);
		tree.addHighlighter(new ColorHighlighter());
		tree.expandAll();
		setLayout(new MigLayout("", "[grow]", ""));
		scrollTree.setPreferredSize(new Dimension(300, 200));
		scrollTree.setViewportView(tree);
		add(scrollTree, "wrap 10, align center");
		setVisible(true);
		scrollTree.setVisible(true);
		tree.setVisible(true);
	}
	
	private void buildTree(Pathway rootPW, DefaultMutableTreeNode root) {
		ArrayList<Pathway> childs = rootPW.getChilds();
		for (Pathway pw:childs){
			DefaultMutableTreeNode node =new DefaultMutableTreeNode(pw.getTitle());
			map.put(node, pw);
			root.add(node);
			if (new GraphInstance().getPathway().equals(pw)) 
				actualNode=node;		
			buildTree(pw,node);
		}
		
	}
	
	
	public void revalidateView(){
		tree.removeAll();
		actualNode=null;
		model = (DefaultTreeModel) tree.getModel();
		Pathway pw=new GraphInstance().getPathway();
		if (pw==null) return;
		Pathway rootPW = pw.getRootPathway();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Pathways Tree:");
		model.setRoot(root);
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(rootPW.getTitle());
		root.add(node);
		buildTree(rootPW, node);
		if (actualNode==null) actualNode=node;
		actualNode.setUserObject(pw.getTitle()+"*");
		map.put(node, rootPW);	
		model.nodeStructureChanged(root);
		model.reload();
		tree.expandAll();
		tree.removeTreeSelectionListener(this);
		tree.setSelectionPath(new TreePath(actualNode.getPath()));
		tree.addTreeSelectionListener(this);
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		MainWindow w = MainWindowSingleton.getInstance();
		GraphContainer con =ContainerSingelton.getInstance();
		DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) tree
		.getLastSelectedPathComponent();
		Pathway newPW = map.get(currentNode);
		if (newPW!=null){
			String pwName=w.getCurrentPathway();
			w.removeTab(false);	
			w.returnFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String newPathwayName = con.addPathway(pwName, newPW);
			newPW = con.getPathway(newPathwayName);
			w.addTab(newPW.getTab().getTitelTab());
			w.returnFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			w.updateAllGuiElements();
		}
	}

	public void removeTree() {
		tree.removeAll();
	}
	
}
