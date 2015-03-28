package graph.hierarchies;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class GraphAnalysisTree{

	private static final long serialVersionUID = 14636342L;
	private Pathway pw;
	private HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> connections = new HashMap<BiologicalNodeAbstract,Set<BiologicalNodeAbstract>>();
	private BiologicalNodeAbstract root;
	private HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> children = new HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>>();
	private HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> parents = new HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract>();	
	
	public GraphAnalysisTree(Pathway pw){
		this.pw = pw;
	}
	
	public void build(){
		
		// Compute the root node
		BiologicalNodeAbstract rootNode = pw.getAllNodes().iterator().next();
		for(BiologicalNodeAbstract n : pw.getAllNodes()){
			if(n.isEnvironmentNodeOf(pw)){
				continue;
			}
			rootNode = n;
			break;
		}
		root = rootNode;
		parents.put(root, null);
		children.put(root, new HashSet<BiologicalNodeAbstract>());
		connections.put(root, new HashSet<BiologicalNodeAbstract>());
		
		// Compute the graph analysis tree
		Set<BiologicalNodeAbstract> newLeafs = new HashSet<BiologicalNodeAbstract>();
		newLeafs.add(rootNode);
		Set<BiologicalNodeAbstract> leafs = new HashSet<BiologicalNodeAbstract>();
		while(!newLeafs.isEmpty()){
			leafs.clear();
			leafs.addAll(newLeafs);
			newLeafs.clear();
				for(BiologicalNodeAbstract leaf : leafs){
					connections.put(leaf, new HashSet<BiologicalNodeAbstract>());
					for(BiologicalNodeAbstract neighbor : pw.getGraph().getJungGraph().getNeighbors(leaf)){
						if(children.containsKey(neighbor) && children.get(neighbor).contains(leaf)){
							parents.put(leaf, neighbor);
							continue;
						}
						if(children.containsKey(neighbor)){
							connections.get(leaf).add(neighbor);
							connections.get(neighbor).add(leaf);
						} else {
//							System.out.println(leaf.getLabel() + "->" + neighbor.getLabel());
							children.get(leaf).add(neighbor);
							children.putIfAbsent(neighbor, new HashSet<BiologicalNodeAbstract>());
							connections.putIfAbsent(neighbor, new HashSet<BiologicalNodeAbstract>());
							newLeafs.add(neighbor);
						}
					}
				}
		}
	}
	
	public boolean isConnected(BiologicalNodeAbstract bna1, BiologicalNodeAbstract bna2){
		if(connections.get(bna1).contains(bna2) || connections.get(bna2).contains(bna1)){
			return true;
		}
		return false;
	}
	
	public boolean isConnected(Set<BiologicalNodeAbstract> set1, Set<BiologicalNodeAbstract> set2){
		Set<BiologicalNodeAbstract> connectionSet1 = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> connectionSet2 = new HashSet<BiologicalNodeAbstract>();
		
		connectionSet1.addAll(set1);
		connectionSet2.addAll(set2);
		
		for(BiologicalNodeAbstract n : set1){
			connectionSet1.addAll(connections.get(n));
		}
		for(BiologicalNodeAbstract n : set2){
			connectionSet2.addAll(connections.get(n));
		}
		connectionSet1.retainAll(connectionSet2);
		if(connectionSet1.isEmpty())
			return false;
		return true;
	}
	
	public Set<BiologicalNodeAbstract> getSubSet(BiologicalNodeAbstract bna){
		Set<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
		set.add(bna);
//		if(bna2treenode.get(bna).isLeaf()){
//			set.add(bna);
//			return set;
//		}
		for(BiologicalNodeAbstract child : children.get(bna)){
			set.addAll(getSubSet(child));
		}
		return set;
	}
	
	public boolean isLeaf(BiologicalNodeAbstract bna){
		if(!children.containsKey(bna)){
			return false;
		}
		if(children.get(bna).isEmpty()){
			return true;
		}
		return false;
	}
	
	public Set<BiologicalNodeAbstract> getLeafs(BiologicalNodeAbstract r){
		Set<BiologicalNodeAbstract> leafs = new HashSet<BiologicalNodeAbstract>();
		if(isLeaf(r)){
			leafs.add(r);
		} else {
			for(BiologicalNodeAbstract child : children.get(r)){
				leafs.addAll(getLeafs(child));
			}
		}
		return leafs;
	}
	
	public int getDepth(BiologicalNodeAbstract node){
		if(parents.get(node)==null){
			return 0;
		}
		return (getDepth(parents.get(node))+1);
	}
	
	public HashMap<Integer, Set<BiologicalNodeAbstract>> getDepthMap(BiologicalNodeAbstract r){
		HashMap<Integer, Set<BiologicalNodeAbstract>> depthMap = new HashMap<Integer, Set<BiologicalNodeAbstract>>();
		int depth = 0;
		depthMap.put(depth, new HashSet<BiologicalNodeAbstract>());
		depthMap.get(depth).add(r);
		HashSet<BiologicalNodeAbstract> childs = new HashSet<BiologicalNodeAbstract>();
		HashSet<BiologicalNodeAbstract> childsCopy = new HashSet<BiologicalNodeAbstract>();
		childs.add(r);
		while(!childs.isEmpty()){
			childsCopy.clear();
			childsCopy.addAll(childs);
			childs.clear();
			for(BiologicalNodeAbstract n : childsCopy){
				depthMap.putIfAbsent(depth, new HashSet<BiologicalNodeAbstract>());
				depthMap.get(depth).add(n);
				childs.addAll(children.get(n));
			}
			depth += 1;
		}
		return depthMap;
	}
	
	private int getMax(Set<Integer> numbers){
		if(numbers == null || numbers.size()<1){
			return 0;
		}
		int max = numbers.iterator().next();
		for(Integer num : numbers){
			if(num>max){
				max = num;
			}
		}
		return max;
	}
	
	private Set<BiologicalNodeAbstract> getAllDescendents(BiologicalNodeAbstract r){
		Set<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();
		set.addAll(children.get(r));
		for(BiologicalNodeAbstract child : children.get(r)){
			set.addAll(getAllDescendents(child));
		}
		return set;
	}
	
	public HashMap<BiologicalNodeAbstract, Collection<Set<BiologicalNodeAbstract>>> getSplittingNodes(){
		
//		printTree();
//		for(BiologicalNodeAbstract key : connections.keySet()){
//			System.out.print(key.getLabel() + "-> ");
//			for(BiologicalNodeAbstract n : connections.get(key)){
//				System.out.print(n.getLabel() + ",");
//			}
//			System.out.println();
//		}
		
		HashMap<BiologicalNodeAbstract, Collection<Set<BiologicalNodeAbstract>>> parts = 
				new HashMap<BiologicalNodeAbstract, Collection<Set<BiologicalNodeAbstract>>>();
//		System.out.println("Root: " + root.getLabel());
		HashMap<Integer, Set<BiologicalNodeAbstract>> depth = getDepthMap(root);
				
		int row = getMax(depth.keySet());
		Set<BiologicalNodeAbstract> thisLevel = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> nodeChildren = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> finishedNodesRow = new HashSet<BiologicalNodeAbstract>();
		Set<BiologicalNodeAbstract> finishedNodes = new HashSet<BiologicalNodeAbstract>();
		Set<Set<BiologicalNodeAbstract>> childLists;
		Set<BiologicalNodeAbstract> childList = new HashSet<BiologicalNodeAbstract>();
		
		while(row>=0){
			thisLevel.clear();
			thisLevel.addAll(depth.get(row));
			for(BiologicalNodeAbstract n : thisLevel){
				
//				System.out.println("NODE " + n.getLabel());
//				for(BiologicalNodeAbstract key : connections.keySet()){
//					System.out.print(key.getLabel() + "-> ");
//					for(BiologicalNodeAbstract n2 : connections.get(key)){
//						System.out.print(n2.getLabel() + ",");
//					}
//					System.out.println();
//				}
					childLists = new HashSet<Set<BiologicalNodeAbstract>>();
					nodeChildren.addAll(children.get(n));
					Set<BiologicalNodeAbstract> parentList = null;
					while(!nodeChildren.isEmpty()){
						childList = new HashSet<BiologicalNodeAbstract>();
						BiologicalNodeAbstract child = nodeChildren.iterator().next();
						childList.add(child);
						nodeChildren.remove(child);
						Set<BiologicalNodeAbstract> childConnections = new HashSet<BiologicalNodeAbstract>();
						childConnections.addAll(connections.get(child));
						while(!childConnections.isEmpty()){
							BiologicalNodeAbstract con = childConnections.iterator().next();
							childConnections.remove(con);
							if(nodeChildren.contains(con)){
								childList.add(con);
								nodeChildren.remove(con);
								for(BiologicalNodeAbstract con2 : connections.get(con)){
									if(!children.get(n).contains(con2) || nodeChildren.contains(con2)){
										childConnections.add(con2);
									}
								}
							} else if(children.get(n).contains(con)){
								continue;
							} else {
								if(parentList == null){
									childList.add(parents.get(n));
									parentList = childList;
								} else {
									parentList.addAll(childList);
									childList = parentList;
								}
								
								Set<BiologicalNodeAbstract> cons = new HashSet<BiologicalNodeAbstract>();
								cons.addAll(connections.get(con));
								if(depth.get(row).contains(con)){
									for(BiologicalNodeAbstract node : cons){
										if(children.get(n).contains(node)){
											connections.get(con).remove(node);
											connections.get(con).add(n);
											connections.get(n).add(con);
										}
									}
								} else {
									for(BiologicalNodeAbstract node : cons){
										if(children.get(n).contains(node)){
											connections.get(parents.get(con)).add(n);
											connections.get(n).add(parents.get(con));
										}
									}
								}
							}
						}
//						for(BiologicalNodeAbstract node : childList){
//							System.out.print(node.getLabel() + ", ");
//						}
//						System.out.println();
						if(childList != parentList){
							childLists.add(childList);
						}
					}
					if(parentList == null && n!=root){
						parentList = new HashSet<BiologicalNodeAbstract>();
						parentList.add(parents.get(n));
					}
					if(n!=root){
						childLists.add(parentList);
					}
					
					parts.put(n, new HashSet<Set<BiologicalNodeAbstract>>());
					parts.put(n, childLists);
					if(parts.get(n).size()<2){
						parts.remove(n);
					} else {
						if(n!=root){
							parentList.addAll(pw.getAllNodes());
							parentList.remove(n);
						}
						Set<BiologicalNodeAbstract> setCopy;
						for(Set<BiologicalNodeAbstract> set : parts.get(n)){
							if(n != root && set!=parentList){
								setCopy = new HashSet<BiologicalNodeAbstract>();
								setCopy.addAll(set);
								for(BiologicalNodeAbstract subNode : setCopy){
									if(children.get(n).contains(subNode)){
										set.addAll(getAllDescendents(subNode));
									}
								}
								parentList.removeAll(set);
							}
						}
					}
					finishedNodesRow.addAll(children.get(n));
				}
			finishedNodes.addAll(finishedNodesRow);
			finishedNodesRow.clear();
			row -= 1;
		}
		
		return parts;
		
//		for(BiologicalNodeAbstract key : parts.keySet()){
//			System.out.println(key.getLabel() + ":");
//			for(Set<BiologicalNodeAbstract> set : parts.get(key)){
//				for(BiologicalNodeAbstract node : set){
//					System.out.print(node.getLabel() + ",");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		System.out.println("done");
	}

	public void printTree(){
		printChildren(root);
	}
	
	private void printChildren(BiologicalNodeAbstract node){
		System.out.print(node.getLabel() + ": ");
		if(isLeaf(node)){
			System.out.println();
			return;
		}
		for(BiologicalNodeAbstract child : children.get(node)){
			System.out.print(child.getLabel() + ",");
		}
		System.out.println();
		for(BiologicalNodeAbstract child : children.get(node)){
			printChildren(child);
		}
	}
}
