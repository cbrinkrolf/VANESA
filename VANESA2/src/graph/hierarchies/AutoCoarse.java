package graph.hierarchies;

import gui.MainWindowSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class AutoCoarse {
	
	public static void coarseSeperatedSubgraphs(Pathway pw){
		
		// Take only inner nodes.
		Set<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		
		if(pw.isBNA()){
			nodes.addAll(((BiologicalNodeAbstract) pw).getInnerNodes());
		} else {
			nodes.addAll(pw.getAllNodes());
		}
		
		// Initialisation of neighbors and Subgraph lists.
		HashMap<Integer, List<List<BiologicalNodeAbstract>>> subgraphs = new HashMap<Integer, List<List<BiologicalNodeAbstract>>>();
		List<BiologicalNodeAbstract> subgraph = new ArrayList<BiologicalNodeAbstract>();
		List<List<BiologicalNodeAbstract>> theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
		HashMap<Integer, List<BiologicalNodeAbstract>> neighbors = new HashMap<Integer, List<BiologicalNodeAbstract>>();
		List<BiologicalNodeAbstract> neigh = new ArrayList<BiologicalNodeAbstract>();
		
		for(BiologicalNodeAbstract n : nodes){
			neigh = new ArrayList<BiologicalNodeAbstract>();
			neigh.addAll(pw.getGraph().getJungGraph().getNeighbors(n));
			// delete environment nodes.
			neigh.retainAll(nodes);
			neighbors.put(n.getID(), neigh);
			theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
			for(BiologicalNodeAbstract neighbor : neigh){
				subgraph = new ArrayList<BiologicalNodeAbstract>();
				subgraph.add(neighbor);
				theSubgraphs.add(subgraph);
			}
			subgraphs.put(n.getID(),theSubgraphs);
		}
		
		HashSet<BiologicalNodeAbstract> sortedNodes = new HashSet<BiologicalNodeAbstract>();
		HashSet<BiologicalNodeAbstract> retainCheck = new HashSet<BiologicalNodeAbstract>();
		boolean somethingChanged= true;
		for(BiologicalNodeAbstract n : nodes){
			somethingChanged= true;
//			System.out.println(n.getLabel() + ":");
			neigh = neighbors.get(n.getID());
			sortedNodes = new HashSet<BiologicalNodeAbstract>();
			while(somethingChanged){
//				System.out.print("sorted nodes:");
//				for(BiologicalNodeAbstract nd : sortedNodes){
//					System.out.print(nd.getLabel() + ",");
//				}
//				System.out.println();
				if(subgraphs.get(n.getID()).size()<2){
					break;
				}
				somethingChanged = false;
				theSubgraphs = new ArrayList<List<BiologicalNodeAbstract>>();
				theSubgraphs.addAll(subgraphs.get(n.getID()));
				for(List<BiologicalNodeAbstract> sg : theSubgraphs){
					HashSet<BiologicalNodeAbstract> newNodeCopy = new HashSet<BiologicalNodeAbstract>();
					newNodeCopy.addAll(sg);
					for(BiologicalNodeAbstract newNode : newNodeCopy){
//						System.out.println("newnode:" + newNode.getLabel());
							HashSet<BiologicalNodeAbstract> newElements = new HashSet<BiologicalNodeAbstract>();
							newElements.addAll(neighbors.get(newNode.getID()));
							newElements.remove(n);
							newElements.removeIf(p -> sg.contains(p));
//							System.out.print("newElements");
//							for(BiologicalNodeAbstract nd : newElements){
//								System.out.print(nd.getLabel() + ",");
//							}
//							System.out.println();
							retainCheck = new HashSet<BiologicalNodeAbstract>();
							retainCheck.addAll(newElements);
							retainCheck.retainAll(sortedNodes);
							if(retainCheck.size()!=0){
								sortedNodes.removeAll(sg);
								subgraphs.get(n.getID()).remove(sg);
								somethingChanged = true;
								break;
							}
							if(!newElements.isEmpty()){
								sg.addAll(newElements);
								sortedNodes.add(newNode);
								somethingChanged = true;
							}
					}
				}
			}
			if(subgraphs.get(n.getID()).size()<2){
				subgraphs.remove(n.getID());
			} else {
				List<BiologicalNodeAbstract> largestList = new ArrayList<BiologicalNodeAbstract>();
				for(List<BiologicalNodeAbstract> l : subgraphs.get(n.getID())){
					if(l.size()>largestList.size()){
						largestList = l;
					}
				}
				subgraphs.get(n.getID()).remove(largestList);
			}
		}
		
//		List<BiologicalNodeAbstract> largestList = new ArrayList<BiologicalNodeAbstract>();
//		for(Integer key : subgraphs.keySet()){
//			largestList = new ArrayList<BiologicalNodeAbstract>();
//			for(List<BiologicalNodeAbstract> l : subgraphs.get(key)){
//				if(l.size()>largestList.size()){
//					largestList = l;
//				}
//			}
//			subgraphs.get(key).remove(largestList);
//		}
		
//		for(BiologicalNodeAbstract node : nodes){
//			if(subgraphs.keySet().contains(node.getID())){
//				System.out.println(node.getLabel() + ":");
//				for(List<BiologicalNodeAbstract> l : subgraphs.get(node.getID())){
//					for(BiologicalNodeAbstract nd : l){
//						System.out.print(nd.getLabel() + ",");
//					}
//				System.out.println();
//				}
//			System.out.println();
//			}
//		}
		
		class HLC implements HierarchyListComparator<Integer> {
			
			public HLC() {
			}

			public Integer getValue(BiologicalNodeAbstract n) {
				Integer currentSize = Integer.MAX_VALUE;
				Integer parentID = n.getID();
				for(Integer key : subgraphs.keySet()){
					for(List<BiologicalNodeAbstract> l : subgraphs.get(key)){
						if(l.contains(n)){
							if(l.size()<currentSize){
								currentSize = l.size();
								parentID = key;
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
		l.addAll(nodes);
		l.sort(new HLC());
		l.coarse();
		
	}
}
