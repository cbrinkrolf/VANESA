package database.dawis;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DAWISTree {

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	
	public DAWISTree(){
		root = new DefaultMutableTreeNode("DAWIS Pathway");
		model= new DefaultTreeModel(root);	
	}
	
	public DefaultMutableTreeNode getRoot(){
		return root;
	}
	
	public void addNode(DefaultMutableTreeNode parentNode,DefaultMutableTreeNode newChildNode){	
		parentNode.add(newChildNode);
		model.nodeStructureChanged(root); 
		model.reload();
	}
	
}
