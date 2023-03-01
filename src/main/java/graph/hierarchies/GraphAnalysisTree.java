package graph.hierarchies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * GAT for the computation of the splitting nodes subgraphs.
 * @author tobias
 */
public class GraphAnalysisTree{
	private final Pathway pw;
	private final HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> connections = new HashMap<>();
	private BiologicalNodeAbstract root;
	private final HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> children = new HashMap<>();
	private final HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> parents = new HashMap<>();
	private final Set<BiologicalNodeAbstract> allNodes = new HashSet<>();
	
	public GraphAnalysisTree(Pathway pw){
		this.pw = pw;
	}
	
	/**
	 * @return Root of the GAT.
	 */
	public BiologicalNodeAbstract getRootNode(){
		return root;
	}
	
	/**
	 * Checks if node1 is child of node2.
	 * @param child Potential child node
	 * @param parent Potential parent node.
	 * @return true, if child is child of parent in GAT.
	 */
	public boolean isChildOf(BiologicalNodeAbstract child, BiologicalNodeAbstract parent){
		return children.get(parent).contains(child);
	}
	
	/**
	 * Computes the GAT.
	 */
	public void build(){
		// Compute the root node
		allNodes.addAll(pw.getAllGraphNodes());
		BiologicalNodeAbstract rootNode = allNodes.iterator().next();
		for(BiologicalNodeAbstract n : allNodes){
			if(n.isEnvironmentNodeOf(pw)){
				continue;
			}
			rootNode = n;
			break;
		}
		root = rootNode;
		parents.put(root, null);
		children.put(root, new HashSet<>());
		connections.put(root, new HashSet<>());
		
		// Compute the graph analysis tree
		Set<BiologicalNodeAbstract> newLeafs = new HashSet<>();
		newLeafs.add(rootNode);
		Set<BiologicalNodeAbstract> leafs = new HashSet<>();
		while(!newLeafs.isEmpty()){
			leafs.clear();
			leafs.addAll(newLeafs);
			newLeafs.clear();
			for(BiologicalNodeAbstract leaf : leafs){
				connections.put(leaf, new HashSet<>());
				for(BiologicalNodeAbstract neighbor : pw.getGraph().getJungGraph().getNeighbors(leaf)){
					if(children.containsKey(neighbor) && children.get(neighbor).contains(leaf)){
						parents.put(leaf, neighbor);
						continue;
					}
					if(children.containsKey(neighbor)){
						connections.get(leaf).add(neighbor);
						connections.get(neighbor).add(leaf);
					} else {
						children.get(leaf).add(neighbor);
						children.putIfAbsent(neighbor, new HashSet<>());
						connections.putIfAbsent(neighbor, new HashSet<>());
						newLeafs.add(neighbor);
					}
				}
			}
		}
	}
	
	/**
	 * Computes all GAT descendants of a node (included the node itself).
	 * @param bna Input node
	 * @return Set of all descendants plus the input node itself
	 */
	public Set<BiologicalNodeAbstract> getSubSet(BiologicalNodeAbstract bna){
		Set<BiologicalNodeAbstract> set = new HashSet<>();
		set.add(bna);
		for(BiologicalNodeAbstract child : children.get(bna)){
			set.addAll(getSubSet(child));
		}
		return set;
	}
	
	/**
	 * Checks if a node is a leaf node of the GAT
	 * @param bna Input node
	 * @return true, if node is a leaf node of the GAT
	 */
	public boolean isLeaf(BiologicalNodeAbstract bna){
		if(!children.containsKey(bna)){
			return false;
		}
		return children.get(bna).isEmpty();
	}
	
	/**
	 * Compute all leaf nodes of the sub-GAT of the input node.
	 * @param r Input node
	 * @return Set of all leaf nodes of the input node
	 */
	public Set<BiologicalNodeAbstract> getLeafs(BiologicalNodeAbstract r){
		Set<BiologicalNodeAbstract> leafs = new HashSet<>();
		if(isLeaf(r)){
			leafs.add(r);
		} else {
			for(BiologicalNodeAbstract child : children.get(r)){
				leafs.addAll(getLeafs(child));
			}
		}
		return leafs;
	}
	
	/**
	 * Compute the depth of a node in the GAT.
	 * @param node Input node
	 * @return Depth of the input node in the GAT
	 */
	public int getDepth(BiologicalNodeAbstract node){
		if(parents.get(node)==null){
			return 0;
		}
		return (getDepth(parents.get(node))+1);
	}
	
	/**
	 * Compute the Mapping required for the automated reconstruction by splitting nodes.
	 * @return The Mapping just as required for the automated reconstruction (each node mapping
	 * on its parent node)
	 */
	public HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> getSplittingNodesMapping(){
		// Mapping for the automated reconstruction (to be returned).
		HashMap<BiologicalNodeAbstract, BiologicalNodeAbstract> coarseMapping = new HashMap<>();
		
		// Mapping for the minimal subnetwork size of a node it is part of.
		HashMap<BiologicalNodeAbstract, Integer> minimalSubnetwork = new HashMap<>();
		
		// Subsets of all splitting nodes
		HashMap<BiologicalNodeAbstract, Collection<Set<BiologicalNodeAbstract>>> subsets = new HashMap<>();
		
		// Separated network parts (pairwise) of each node.
		HashMap<BiologicalNodeAbstract, HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>>> separatedNetworkParts = new HashMap<>();
		
		// HashMap with divided children/parent subnetworks (only contains the direct child/parent node)
		HashMap<BiologicalNodeAbstract, Collection<Pair<BiologicalNodeAbstract>>> pairwiseconnectedSubpathways = new HashMap<>();
		
		// Map for each depth of the GAT (depth -> Set of nodes)
		HashMap<Integer, Set<BiologicalNodeAbstract>> depths = new HashMap<>();
		int maxDepth = 0;
		for(BiologicalNodeAbstract node : getSubSet(getRootNode())){
			Integer nodeDepth = getDepth(node);
			if(!depths.containsKey(nodeDepth)){
				depths.put(nodeDepth, new HashSet<>());
			}
			depths.get(nodeDepth).add(node);
			if(nodeDepth>maxDepth){
				maxDepth = getDepth(node);
			}
		}
		
		// Nodes of the current level
		Set<BiologicalNodeAbstract> currentLevel = new HashSet<>();
		
		// Nodes of the previous level
		Set<BiologicalNodeAbstract> deeperLevel = getLeafs(root);
		
		// Connections of the current level
		Set<Pair<BiologicalNodeAbstract>> curCon = new HashSet<>();
		
		// Set for abstracting edges on the next level
		Set<Pair<BiologicalNodeAbstract>> newCon = new HashSet<>();
		
		// Initialize edges of the deepest level
		for(BiologicalEdgeAbstract edge : pw.getAllEdges()){
			if(deeperLevel.contains(edge.getFrom()) || deeperLevel.contains(edge.getTo())){
				if(parents.get(edge.getFrom()) != edge.getTo() && parents.get(edge.getTo()) != edge.getFrom()){
					curCon.add(new Pair<>(edge.getFrom(), edge.getTo()));
				}
			}
		}
		
		// connections for the leaf nodes (empty)
		for(BiologicalNodeAbstract leafNode : deeperLevel){
			pairwiseconnectedSubpathways.put(leafNode, new HashSet<>());
		}
		// current depth
		int depth = maxDepth;
		// Iteration through all GAT levels
		while(depth>0){
			depth -= 1;
			// Update nodes for the next layer
			deeperLevel.clear();
			deeperLevel.addAll(currentLevel);
			currentLevel.clear();
			currentLevel.addAll(depths.get(depth));
			
			// Initialize connections for all nodes of the current level
			for(BiologicalNodeAbstract node : currentLevel){
				pairwiseconnectedSubpathways.put(node, new HashSet<>());
			}
			
			// Iterate through all current connections
			for(Pair<BiologicalNodeAbstract> edge : curCon){
				
				Pair<BiologicalNodeAbstract> pair;
				
				// If edge connects two nodes of deeper Level
				if(deeperLevel.contains(edge.getFirst()) && deeperLevel.contains(edge.getSecond())){
					if(parents.get(edge.getFirst())==parents.get(edge.getSecond())){
						pair = new Pair<>(edge.getFirst(), edge.getSecond());
						pairwiseconnectedSubpathways.get(parents.get(edge.getFirst())).add(pair);
					} else {
						pair = new Pair<>(edge.getFirst(), parents.get(parents.get(edge.getFirst())));
						pairwiseconnectedSubpathways.get(parents.get(edge.getFirst())).add(pair);

						pair = new Pair<>(edge.getSecond(), parents.get(parents.get(edge.getSecond())));
						pairwiseconnectedSubpathways.get(parents.get(edge.getSecond())).add(pair);
					}
					
				// "Diagonal" edge from first node
				} else if(deeperLevel.contains(edge.getFirst())) {
					pair = new Pair<>(edge.getFirst(), parents.get(parents.get(edge.getFirst())));
					pairwiseconnectedSubpathways.get(parents.get(edge.getFirst())).add(pair);
					
				// "Diagonal" edge from second node
				} else if(deeperLevel.contains(edge.getSecond())) {
					pair = new Pair<>(edge.getSecond(), parents.get(parents.get(edge.getSecond())));
					pairwiseconnectedSubpathways.get(parents.get(edge.getSecond())).add(pair);
				}
			}
			// Abstract edges to the next GAT level
			newCon.clear();
			for(Pair<BiologicalNodeAbstract> edge : curCon){
				if(currentLevel.contains(edge.getFirst()) && deeperLevel.contains(edge.getSecond())){
					newCon.add(new Pair<>(edge.getFirst(), parents.get(edge.getSecond())));
				} else if(deeperLevel.contains(edge.getFirst()) && currentLevel.contains(edge.getSecond())){
					newCon.add(new Pair<>(parents.get(edge.getFirst()), edge.getSecond()));
				} else if(currentLevel.contains(edge.getFirst()) && currentLevel.contains(edge.getSecond())){
					newCon.add(edge);
				} else if(deeperLevel.contains(edge.getFirst()) && deeperLevel.contains(edge.getSecond())){
					newCon.add(new Pair<>(parents.get(edge.getFirst()), parents.get(edge.getSecond())));
				}
			}
			// Add edges of the next GAT level
			for(BiologicalEdgeAbstract edge : pw.getAllEdges()){
				if((currentLevel.contains(edge.getFrom()) || currentLevel.contains(edge.getTo())) && 
						(!deeperLevel.contains(edge.getFrom()) && !deeperLevel.contains(edge.getTo()))){
					if(parents.get(edge.getFrom()) != edge.getTo() && parents.get(edge.getTo()) != edge.getFrom()){
						newCon.add(new Pair<>(edge.getFrom(), edge.getTo()));
					}
				}
			}
			curCon.clear();
			curCon.addAll(newCon);
			curCon.removeIf(e -> e.getFirst() == e.getSecond());
			newCon.clear();
		}
		// Compute connections of the GAT root
		for(Pair<BiologicalNodeAbstract> edge : curCon){
			Pair<BiologicalNodeAbstract> pair = new Pair<>(edge.getFirst(), edge.getSecond());
			pairwiseconnectedSubpathways.get(parents.get(edge.getSecond())).add(pair);
		}
		
		// Merge the pairwise connections of each node to common sets if they are connected
		for(BiologicalNodeAbstract node : allNodes){
			separatedNetworkParts.put(node, new HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>>());
			HashMap<BiologicalNodeAbstract, Set<BiologicalNodeAbstract>> networkParts = separatedNetworkParts.get(node);
			for(Pair<BiologicalNodeAbstract> edge : pairwiseconnectedSubpathways.get(node)){
				if(!networkParts.containsKey(edge.getFirst()) && !networkParts.containsKey(edge.getSecond())){
					HashSet<BiologicalNodeAbstract> set = new HashSet<>();
					set.add(edge.getFirst());
					set.add(edge.getSecond());
					networkParts.put(edge.getFirst(), set);
					networkParts.put(edge.getSecond(), set);
				} else if(!networkParts.containsKey(edge.getSecond())){
					networkParts.get(edge.getFirst()).add(edge.getSecond());
					networkParts.put(edge.getSecond(), networkParts.get(edge.getFirst()));
				} else if(!networkParts.containsKey(edge.getFirst())){
					networkParts.get(edge.getSecond()).add(edge.getFirst());
					networkParts.put(edge.getFirst(), networkParts.get(edge.getSecond()));
				} else {
					networkParts.get(edge.getFirst()).addAll(networkParts.get(edge.getSecond()));
					networkParts.put(edge.getSecond(), networkParts.get(edge.getFirst()));
				}
			}
			for(BiologicalNodeAbstract child : children.get(node)){
				HashSet<BiologicalNodeAbstract> childSet = new HashSet<>();
				childSet.add(child);
				networkParts.putIfAbsent(child, childSet);
			}
			if(node!=root){
				HashSet<BiologicalNodeAbstract> childSet = new HashSet<>();
				childSet.add(parents.get(node));
				networkParts.putIfAbsent(parents.get(node), childSet);
			}
		}
		// Select only the subsets of splitting nodes.
		for(BiologicalNodeAbstract key : separatedNetworkParts.keySet()){
			if(separatedNetworkParts.get(key).values().size()>1){
				subsets.put(key, new HashSet<>());
				subsets.get(key).addAll(separatedNetworkParts.get(key).values());
				if(subsets.get(key).size()<=1){
					subsets.remove(key);
				}
			}
		}
				
		// Add all subnodes to the particular set (TODO: this step requires too long).
		HashSet<BiologicalNodeAbstract> tempSet = new HashSet<>();
		for(BiologicalNodeAbstract key : subsets.keySet()){
			int largestSet = 0;
			for(Set<BiologicalNodeAbstract> subnet : subsets.get(key)){
				tempSet.clear();
				tempSet.addAll(subnet);
				for(BiologicalNodeAbstract node : tempSet){
					if(isChildOf(node, key)){
						subnet.addAll(getSubSet(node));
					} else {
						HashSet<BiologicalNodeAbstract> theRest = new HashSet<>();
						theRest.addAll(getSubSet(getRootNode()));
						theRest.removeAll(getSubSet(key));
						subnet.addAll(theRest);
					}
				}
				if(subnet.size()>largestSet){
					largestSet = subnet.size();
				}
			}
			for(Set<BiologicalNodeAbstract> set : subsets.get(key)){
				if(set.size()>=largestSet){
					continue;
				}
				for(BiologicalNodeAbstract n : set){
					if(!coarseMapping.containsKey(n)){
						coarseMapping.put(n, key);
						minimalSubnetwork.put(n, set.size());
					} else {
						Integer smallestValue = Integer.MAX_VALUE;
						if(minimalSubnetwork.get(n)<smallestValue){
							smallestValue = minimalSubnetwork.get(n);
						}
						if(smallestValue > set.size()){
							coarseMapping.put(n, key);
							minimalSubnetwork.put(n, set.size());
						}
					}
				}
			}
		}
		//return HashMap
		return coarseMapping;		
	}

	/**
	 * Print the tree structure (for debugging)
	 */
	public void printTree(){
		printChildren(root);
	}
	
	/**
	 * Print a GAT subtree (for debugging, used by printTree())
	 */
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
