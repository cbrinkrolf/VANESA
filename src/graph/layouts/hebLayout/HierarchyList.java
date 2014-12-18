package graph.layouts.hebLayout;

import graph.jung.classes.MyGraph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;

import biologicalObjects.nodes.BiologicalNodeAbstract;

public class HierarchyList<E> extends AbstractList<BiologicalNodeAbstract>implements List<BiologicalNodeAbstract>,
RandomAccess, Cloneable, java.io.Serializable{
	
	List<HierarchyList<E>> hierarchyList = new ArrayList<HierarchyList<E>>();
	List<BiologicalNodeAbstract> allElements = new ArrayList<BiologicalNodeAbstract>();
	HashMap<E, HierarchyList<E>> hierarchyGroups = new HashMap<E, HierarchyList<E>>();
	HierarchyListComparator stdComparator;
	BiologicalNodeAbstract node;
	E value;
	
	public HierarchyList(){
		
	}
	
//	public HierarchyList(HierarchyListComparator hlc){
//		
//	}
	
	public HierarchyList(BiologicalNodeAbstract n, E val){
		node = n;
		value = val;
	}
	
	public HierarchyList(E val){
		value = val;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1392848302093883L;

	@Override
	public boolean add(BiologicalNodeAbstract arg0) {
		return allElements.add(arg0);
	}
	
	public boolean add(HierarchyList<E> arg0){
		if(arg0.getNode()!=null)
			allElements.add(arg0.getNode());
		hierarchyList.add(arg0);
		hierarchyGroups.put(arg0.getValue(),arg0);
		return true;	
	}

	@Override
	public void add(int arg0, BiologicalNodeAbstract arg1) {
			allElements.add(arg0, arg1);
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
		return allElements.addAll(arg0, arg1);
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
		if(arg0 instanceof HierarchyList<?>){
			hierarchyGroups.remove(((HierarchyList<?>) arg0).getNode().getID());
			return (allElements.remove(((HierarchyList<?>) arg0).getNode()));
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
	
	public E getValue(){
		return value;
	}
	
	private BiologicalNodeAbstract coarse(boolean isInitialCall){
		Set<BiologicalNodeAbstract> childNodes = new HashSet<BiologicalNodeAbstract>();
		if(getNode()!=null)
			childNodes.add(getNode());
		for(E key : hierarchyGroups.keySet()){
			HierarchyList<E> l = hierarchyGroups.get(key);
			if(l.size()>0){
				childNodes.add(l.coarse(false));
			} else {
				childNodes.add(l.getNode());
			}
		}
		if(childNodes.size()>1 && !isInitialCall){
			if(getNode()!=null){
				BiologicalNodeAbstract coarseNode =  BiologicalNodeAbstract.coarse(childNodes, null, getNode().getLabel());
				coarseNode.setRootNode(getNode());
				return coarseNode;
			}
			return BiologicalNodeAbstract.coarse(childNodes, null, getValue().toString());
		}
		return getNode();
	}
	
	public void coarse(){
		coarse(true);
	}
	
	public void setNode(BiologicalNodeAbstract n){
		node = n;
	}
	
	public List<HierarchyList<E>> getSubLists(){
		return hierarchyList;
	}
	
	

	
	public void sort(HierarchyListComparator<? extends E> comp){
		hierarchyList = new ArrayList<HierarchyList<E>>();
		hierarchyGroups = new HashMap<E, HierarchyList<E>>();
		HashMap<E, HierarchyList<E>> valueToList = new HashMap<E, HierarchyList<E>>();
		List<HierarchyList<E>> finalLists = new ArrayList<HierarchyList<E>>();
		E value;
		E subvalue;
		
		for(BiologicalNodeAbstract n : allElements){
			value = comp.getValue(n);
			if(!valueToList.containsKey(value)){
				HierarchyList<E> l = new HierarchyList<E>(value);
				valueToList.put(value, l);
				finalLists.add(l);
			}
		}

		value = null;
		subvalue = null;
		
		for(BiologicalNodeAbstract n : allElements){
			value = comp.getValue(n);
			subvalue = comp.getSubValue(n);
			if(valueToList.containsKey(subvalue)){
				if(subvalue!=null && !subvalue.equals(value)){
					valueToList.get(subvalue).setNode(n);
					valueToList.get(value).add(valueToList.get(subvalue));
					finalLists.remove(valueToList.get(subvalue));
				} else {
					valueToList.get(value).setNode(n);
				}
			} else {
				valueToList.get(value).add(new HierarchyList<E>(n,subvalue));
			}
		}
		for(HierarchyList<E> l : finalLists){
			add(l);
		}
		printSubLists();
	}
	
	public void printSubLists(){
		if(getNode()!=null){
			System.out.println(value + " " + getNode().getLabel());
		} else {
			System.out.println(value);
		}
		for(HierarchyList l : getSubLists()){
			l.printSubLists();
		}
	}
	
}
