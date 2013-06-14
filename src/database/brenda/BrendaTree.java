package database.brenda;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class BrendaTree {

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	
	public BrendaTree(){
		root = new DefaultMutableTreeNode("BRENDA Pathway");
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
