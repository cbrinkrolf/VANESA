package graph.layouts.hebLayout;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class HierarchyList extends AbstractList<BiologicalNodeAbstract>implements List<BiologicalNodeAbstract>,
RandomAccess, Cloneable, java.io.Serializable{
	
	List<HierarchyList> hierarchyList = new ArrayList<HierarchyList>();
	List<BiologicalNodeAbstract> allElements = new ArrayList<BiologicalNodeAbstract>();
	HashMap<Integer, HierarchyList> hierarchyGroups = new HashMap<Integer, HierarchyList>();
	HierarchyListComparator stdComparator;
	BiologicalNodeAbstract node;
	
	public HierarchyList(){
		
	}
	
//	public HierarchyList(HierarchyListComparator hlc){
//		
//	}
	
	public HierarchyList(BiologicalNodeAbstract n){
		node = n;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1392848302093883L;

	@Override
	public boolean add(BiologicalNodeAbstract arg0) {
		return allElements.add(arg0);
	}
	
	public boolean add(HierarchyList arg0){
		if(arg0.getNode()==null)
			return false;
		allElements.add(arg0.getNode());
		hierarchyList.add(arg0);
		hierarchyGroups.put(arg0.getNode().getID(),arg0);
		return true;	
	}

	@Override
	public void add(int arg0, BiologicalNodeAbstract arg1) {
		if(hierarchyGroups.keySet().contains(arg0)){
			allElements.add(arg1);
			hierarchyGroups.get(arg0).add(new HierarchyList(arg1));
		}		
	}

	@Override
	public boolean addAll(Collection<? extends BiologicalNodeAbstract> arg0) {
		for(BiologicalNodeAbstract n : arg0){
			add(n);
		}
		return false;
	}

	@Override
	public boolean addAll(int arg0,
			Collection<? extends BiologicalNodeAbstract> arg1) {
		if(!hierarchyGroups.keySet().contains(arg0)){
			return false;
		}
		for(BiologicalNodeAbstract n : arg1){
			hierarchyGroups.get(arg0).add(new HierarchyList(n)); 
		}
		return false;
	}

	@Override
	public void clear() {
		allElements.clear();
		hierarchyList.clear();
		hierarchyGroups.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		if(arg0 instanceof BiologicalNodeAbstract){
			return allElements.contains(arg0);
		}
		if(arg0 instanceof Integer){
			return hierarchyGroups.containsKey(arg0);
		}
		if(arg0 instanceof HierarchyList){
			return hierarchyGroups.containsValue(arg0);
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for(Object obj : arg0){
			if(!contains(obj)){
				return false;
			}
		}
		return true;
	}

	@Override
	public BiologicalNodeAbstract get(int arg0) {
		if(hierarchyGroups.containsKey(arg0)){
			return hierarchyGroups.get(arg0).getNode();
		}
		return null;
	}

	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		
		return allElements.isEmpty();
	}

	@Override
	public Iterator<BiologicalNodeAbstract> iterator() {
		return allElements.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<BiologicalNodeAbstract> listIterator() {
		return allElements.listIterator();
	}

	@Override
	public ListIterator<BiologicalNodeAbstract> listIterator(int arg0) {
		return allElements.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0) {
		if(arg0 instanceof BiologicalNodeAbstract){
			hierarchyGroups.remove(((BiologicalNodeAbstract) arg0).getID());
			return (allElements.remove(arg0));
		}
		if(arg0 instanceof Integer){
			hierarchyGroups.remove(arg0);
			return (allElements.remove(hierarchyGroups.get(arg0).getNode()));
		}
		if(arg0 instanceof HierarchyList){
			hierarchyGroups.remove(((HierarchyList) arg0).getNode().getID());
			return (allElements.remove(((HierarchyList) arg0).getNode()));
		}
		return false;
	}

	@Override
	public BiologicalNodeAbstract remove(int arg0) {
		if(!hierarchyGroups.keySet().contains(arg0)){
			return null;
		}
		BiologicalNodeAbstract n = hierarchyGroups.get(arg0).getNode();
		hierarchyGroups.remove(arg0);
		allElements.remove(hierarchyGroups.get(arg0).getNode());
		return n;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		if(!containsAll(arg0))
			return false;
		for(Object o : arg0){
			remove(o);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BiologicalNodeAbstract set(int arg0, BiologicalNodeAbstract arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return allElements.size();
	}

	@Override
	public List<BiologicalNodeAbstract> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		return allElements.toArray();
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		return allElements.toArray(arg0);
	}
	
	public BiologicalNodeAbstract getNode(){
		return node;
	}

	
	public void sort(HierarchyListComparator comp){
		hierarchyList = new ArrayList<HierarchyList>();
		hierarchyGroups = new HashMap<Integer, HierarchyList>();

		for(BiologicalNodeAbstract n : allElements){
			if(!allElements.contains(n)){
				continue;
			}
			
			BiologicalNodeAbstract parent = comp.findGroup(n);
			
			if(parent==null || !allElements.contains(parent)){
				HierarchyList newGroup = new HierarchyList(n);
				hierarchyGroups.put(n.getID(), newGroup);
			}
			else if(hierarchyGroups.containsKey(parent.getID())){
				hierarchyGroups.get(parent.getID()).add(new HierarchyList(n));
			} 
			else {
				hierarchyGroups.put(parent.getID(), new HierarchyList(parent)).add(new HierarchyList(n));
			}
		}

	}
	
}
