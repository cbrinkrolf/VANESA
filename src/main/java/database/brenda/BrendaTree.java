package database.brenda;

import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class BrendaTree {

	private DefaultTreeModel model;
	private DefaultMutableTreeNode root;
	private HashMap<BiologicalNodeAbstract, DefaultMutableTreeNode> enzyme2node = new HashMap<BiologicalNodeAbstract, DefaultMutableTreeNode>();
	private HashMap<DefaultMutableTreeNode, BiologicalNodeAbstract> node2enzyme = new HashMap<DefaultMutableTreeNode, BiologicalNodeAbstract>();
	
	public BrendaTree(){
		root = new DefaultMutableTreeNode("BRENDA Pathway");
		model= new DefaultTreeModel(root);	
	}
	
	public DefaultMutableTreeNode getRoot(){
		return root;
	}
	
	public void addNode(DefaultMutableTreeNode parentNode,DefaultMutableTreeNode newChildNode, BiologicalNodeAbstract bna){
		
		parentNode.add(newChildNode);
		model.nodeStructureChanged(root); 
		model.reload();
		if(!enzyme2node.containsKey(bna))
			enzyme2node.put(bna, newChildNode);
		if(!node2enzyme.containsKey(newChildNode))
			node2enzyme.put(newChildNode, bna);
	}
	
	public BiologicalNodeAbstract getEnzyme(DefaultMutableTreeNode node){
		return node2enzyme.get(node);
	}
	
	public DefaultMutableTreeNode getTreeNode(BiologicalNodeAbstract bna){
		return enzyme2node.get(bna);
	}
	
	public void printTree(){
		Enumeration<TreeNode> children = getRoot().children();
		while(children.hasMoreElements()){
			printSubTree((DefaultMutableTreeNode) children.nextElement());
		}
	}
	
	private void printSubTree(DefaultMutableTreeNode node){
		if(node.getParent() != getRoot())
			System.out.println(node2enzyme.get(node.getParent()).getLabel() + "->" + node2enzyme.get(node).getLabel());
		else
			System.out.println("root" + "->" + node2enzyme.get(node).getLabel());
		Enumeration<TreeNode> children = node.children();
		while(children.hasMoreElements()){
			printSubTree((DefaultMutableTreeNode) children.nextElement());
		}
		if(!node.isLeaf())
			System.out.println();
	}
	
}
