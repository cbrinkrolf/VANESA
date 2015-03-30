package graph.hierarchies;

import graph.algorithms.RandomConnectedGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class AutoCoarse {
	
//	public static void coarseSeperatedSubgraphs(Pathway pw){
//		
//		// Take only inner nodes.
//		Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
//		
//		if(pw.isBNA()){
//			nodes.addAll(((BiologicalNodeAbstract) pw).getInnerNodes());
//		} else {
//			nodes.addAll(pw.getAllNodes());
//		}
//		
//		// Initialisation of neighbors and Subgraph lists.
//		HashMap<Integer, List<List<BiologicalNodeAbstract>>> subgraphs = new HashMap<Integer, List<List<BiologicalNodeAbstract>>>();
//		List<BiologicalNodeAbstract> subgraph = new ArrayList<BiologicalNodeAbstract>();
//		List<List<BiologicalNodeAbstract>> theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
//		HashMap<Integer, List<BiologicalNodeAbstract>> neighbors = new HashMap<Integer, List<BiologicalNodeAbstract>>();
//		List<BiologicalNodeAbstract> neigh = new ArrayList<BiologicalNodeAbstract>();
//		
//		for(BiologicalNodeAbstract n : nodes){
//			neigh = new ArrayList<BiologicalNodeAbstract>();
//			neigh.addAll(pw.getGraph().getJungGraph().getNeighbors(n));
//			// delete environment nodes.
//			neigh.retainAll(nodes);
//			neighbors.put(n.getID(), neigh);
//			theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
//			for(BiologicalNodeAbstract neighbor : neigh){
//				subgraph = new ArrayList<BiologicalNodeAbstract>();
//				subgraph.add(neighbor);
//				theSubgraphs.add(subgraph);
//			}
//			subgraphs.put(n.getID(),theSubgraphs);
//		}
//		
//		// Compute seperating nodes with more than 2 neighbors
//		HashSet<BiologicalNodeAbstract> sortedNodes = new HashSet<BiologicalNodeAbstract>();
//		HashSet<BiologicalNodeAbstract> retainCheck = new HashSet<BiologicalNodeAbstract>();
//		boolean somethingChanged= true;
//		for(BiologicalNodeAbstract n : nodes){
//			if(neighbors.get(n.getID()).size()<3){
//				subgraphs.remove(n.getID());
//				continue;
//			}
//			somethingChanged= true;
//			neigh = neighbors.get(n.getID());
//			sortedNodes = new HashSet<BiologicalNodeAbstract>();
//			while(somethingChanged){
//				if(subgraphs.get(n.getID()).size()<2){
//					break;
//				}
//				somethingChanged = false;
//				theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
//				theSubgraphs.addAll(subgraphs.get(n.getID()));
//				for(List<BiologicalNodeAbstract> sg : theSubgraphs){
//					boolean somethingChangedInThisSubgraph = false;
//					HashSet<BiologicalNodeAbstract> newNodeCopy = new HashSet<BiologicalNodeAbstract>();
//					newNodeCopy.addAll(sg);
//					for(BiologicalNodeAbstract newNode : newNodeCopy){
//							HashSet<BiologicalNodeAbstract> newElements = new HashSet<BiologicalNodeAbstract>();
//							newElements.addAll(neighbors.get(newNode.getID()));
//							newElements.remove(n);
//							newElements.removeIf(p -> sg.contains(p));
//							retainCheck = new HashSet<BiologicalNodeAbstract>();
//							retainCheck.addAll(newElements);
//							retainCheck.retainAll(sortedNodes);
//							if(retainCheck.size()!=0){
//								sortedNodes.removeAll(sg);
//								subgraphs.get(n.getID()).remove(sg);
//								somethingChanged = true;
//								somethingChangedInThisSubgraph = true;
//								break;
//							}
//							if(!newElements.isEmpty()){
//								sg.addAll(newElements);
//								sortedNodes.add(newNode);
//								somethingChangedInThisSubgraph = true;
//								somethingChanged = true;
//							}
//					}
//					// If only 2 subgraphs left and one of them is not changing any more, we're done.
//					if(!somethingChangedInThisSubgraph && subgraphs.get(n.getID()).size()==2){
//						List<BiologicalNodeAbstract> othersg = theSubgraphs.get(0);
//						if(othersg==sg){
//							othersg = theSubgraphs.get(1);
//						}
//						othersg.clear();
//						othersg.addAll(nodes);
//						othersg.remove(n);
//						othersg.removeAll(sg);
//						somethingChanged = false;
//						break;
//					}
//				}
//			}
//			if(subgraphs.get(n.getID()).size()<2){
//				subgraphs.remove(n.getID());
//			} else {
//				List<BiologicalNodeAbstract> largestList = new ArrayList<BiologicalNodeAbstract>();
//				for(List<BiologicalNodeAbstract> l : subgraphs.get(n.getID())){
//					if(l.size()>largestList.size()){
//						largestList = l;
//					}
//				}
//				subgraphs.get(n.getID()).remove(largestList);
//			}
//		}
//		System.out.println("done1");
//		
////		List<BiologicalNodeAbstract> largestList = new ArrayList<BiologicalNodeAbstract>();
////		for(Integer key : subgraphs.keySet()){
////			largestList = new ArrayList<BiologicalNodeAbstract>();
////			for(List<BiologicalNodeAbstract> l : subgraphs.get(key)){
////				if(l.size()>largestList.size()){
////					largestList = l;
////				}
////			}
////			subgraphs.get(key).remove(largestList);
////		}
//		
////		for(BiologicalNodeAbstract node : nodes){
////			if(subgraphs.keySet().contains(node.getID())){
////				System.out.println(node.getLabel() + ":");
////				for(List<BiologicalNodeAbstract> l : subgraphs.get(node.getID())){
////					for(BiologicalNodeAbstract nd : l){
////						System.out.print(nd.getLabel() + ",");
////					}
////				System.out.println();
////				}
////			System.out.println();
////			}
////		}
//		
//		class HLC implements HierarchyListComparator<Integer> {
//			
//			public HLC() {
//			}
//
//			public Integer getValue(BiologicalNodeAbstract n) {
//				Integer currentSize = Integer.MAX_VALUE;
//				Integer parentID = n.getID();
//				for(Integer key : subgraphs.keySet()){
//					for(List<BiologicalNodeAbstract> l : subgraphs.get(key)){
//						if(l.contains(n)){
//							if(l.size()<currentSize){
//								currentSize = l.size();
//								parentID = key;
//							}
//						break;
//						}
//					}
//				}
//				return parentID;
//			}
//
//			public Integer getSubValue(BiologicalNodeAbstract n) {
//				return n.getID();
//			}
//		}
//
//		HierarchyList<Integer> l = new HierarchyList<Integer>();
//		l.addAll(nodes);
//		l.sort(new HLC());
//		System.out.println("done2");
//		l.coarse();
//		System.out.println("done3");
//		
//	}
	
	
	
	
	
	
//	public static void coarseSeperatedSubgraphs(Pathway pw){
//		int node = 100;
//		int edges = node;
////		while(node <=3200){
//			while(edges <= (node*(node-1)/2)){
//				if(edges<node){
//					edges *= 2;
//					continue;
//				}
//				System.out.println(node + " Nodes, " + edges + " Edges");
//				for(int i=0; i<5; i++){
//					Pathway p = RandomConnectedGraph.generateRandomGraph(node, edges, false, 1, 1);
//					coarseSeperatedSubgraphs2(p);
//					System.out.print("");
//					coarseSeperatedSubgraphs2(p);
//					System.out.print("");
//					coarseSeperatedSubgraphs2(p);
//					System.out.print("");
//					coarseSeperatedSubgraphs2(p);
//					System.out.print("");
//					coarseSeperatedSubgraphs2(p);
//					System.out.println();
//				}
//				System.out.println();
//				System.out.println();
//				edges *= 2;
//			}
////			node *=2;
////			edges = node;
////		}
//	}
	
public static void coarseSeperatedSubgraphs(Pathway pw){
	
		// Build Graph Analysis Tree
	
//		long timeStart = System.currentTimeMillis();
		GraphAnalysisTree gat = new GraphAnalysisTree(pw);
		gat.build();
//		gat.printTree();
//		long timeEnd = System.currentTimeMillis();
//		System.out.println("Tree: " + (timeEnd-timeStart) + " millis");
		HashMap<BiologicalNodeAbstract, Collection<Set<BiologicalNodeAbstract>>> map = gat.getSplittingNodes();
//		long timeEnd2 = System.currentTimeMillis();
//		System.out.println("Search: " + (timeEnd2-timeEnd) + " millis");
//		System.out.print((timeEnd2-timeStart) + ",");
//		for(BiologicalNodeAbstract key : map.keySet()){
//			System.out.println(key.getLabel() + ":");
//			for(Set<BiologicalNodeAbstract> set : map.get(key)){
//				for(BiologicalNodeAbstract node : set){
//					System.out.print(node.getLabel() + ",");
//				}
//				System.out.println();
//			}
//		}
//
		class HLC implements HierarchyListComparator<Integer> {
			
			public HLC() {
			}

			public Integer getValue(BiologicalNodeAbstract n) {
				Integer currentSize = Integer.MAX_VALUE;
				Integer parentID = n.getID();
				for(BiologicalNodeAbstract key : map.keySet()){
					for(Set<BiologicalNodeAbstract> l : map.get(key)){
						if(l.contains(n)){
							if(l.size()<currentSize){
								currentSize = l.size();
								parentID = key.getID();
							}
						break;
						}
					}
				}
				return parentID;
			}

			public Integer getSubValue(BiologicalNodeAbstract n) {
				return n.getID();
			}
		}

		HierarchyList<Integer> l = new HierarchyList<Integer>();
		l.addAll(pw.getAllNodes());
		l.sort(new HLC());
		l.coarse();
//		timeEnd = System.currentTimeMillis();
//		System.out.println("Runtime: " + (timeEnd-timeStart) + " millis");		
	}
}
