package graph.hierarchies;

import graph.GraphInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.id.IdentityGenerator.GetGeneratedKeysDelegate;

import biologicalObjects.nodes.BiologicalNodeAbstract;

/**
 * List extension for automatic reconstruction of hierarchical structures.
 * Native ArrayList elements are the elements that are considered for the
 * construction of the hierarchical structures. So, it contains all nodes
 * of the related subtree. These elements can be handled as in standard 
 * ArrayLists. If one element is removed, it is also removed from all 
 * sublists.
 * 
 * @author tobias
 *
 * @param <E> Data type of the criteria values for sorting the elements
 * into a hierarchical structure. Must be equal to the data type of <E>
 * in the related HierarchyListComparator. Additionally, BiologicalNodeAbstract
 * should not be set as <E> due to unexpected behavior using native ArrayList 
 * operations.
 */
public class HierarchyList<E> extends ArrayList<BiologicalNodeAbstract>{
	
	private static final long serialVersionUID = 1392848302093883L;
	/**
	 * Next hierarchy level (children nodes of this HierarchyList).
	 */
	HashMap<E, HierarchyList<E>> hierarchyGroups = new HashMap<E, HierarchyList<E>>();
	
	/**
	 * Root node of this hierarchical level.
	 */
	BiologicalNodeAbstract node;
	
	/**
	 * Related value to this hierarchical level.
	 */
	E value;
	
	/**
	 * Standard List constructor.
	 */
	public HierarchyList(){
		super();
	}
	
	/**
	 * Constructor defining root node and value.
	 * @param n Root node of the related hierarchical level.
	 * @param val Value of the related hierarchical level.
	 */
	public HierarchyList(BiologicalNodeAbstract n, E val){
		super();
		node = n;
		add(n);
		value = val;
	}
	
	/**
	 * Constructor defining value.
	 * @param val Value of the related hierarchical level.
	 */
	public HierarchyList(E val){
		super();
		value = val;
	}

	/**
	 * Add a Children List.
	 * @param arg0 Children list to be added.
	 * @return True, if list was added.
	 */
	public boolean add(HierarchyList<E> arg0){
		if(arg0.getNode()!=null)
			add(arg0.getNode());
		if(arg0.getValue()!=null){
			hierarchyGroups.put(arg0.getValue(),arg0);
			addAll(arg0);
			return true;	
		}
		return false;
	}

	@Override
	public void clear() {
		super.clear();
		hierarchyGroups.clear();
		node=null;
		value=null;
	}

	@Override
	public boolean contains(Object arg0) {
		if(arg0 instanceof BiologicalNodeAbstract){
			return super.contains(arg0);
		}
		if(arg0 instanceof HierarchyList){
			return hierarchyGroups.containsValue(arg0);
		}
		return hierarchyGroups.containsKey(arg0);
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
	public boolean remove(Object arg0) {
		boolean ret = false;
		ret = super.remove(arg0) ? true : ret;
		ret = hierarchyGroups.remove(arg0)!=null ? true : ret;
		if(hierarchyGroups.values().contains(arg0)){
			List<E> keySet = new ArrayList<E>();
			keySet.addAll(hierarchyGroups.keySet());
			for(E val : keySet){
				if(hierarchyGroups.get(val).contains(arg0)){
					ret = hierarchyGroups.remove(val)!=null ? true : ret;
				}
			}
		}
		if(ret){
			for(HierarchyList<E> v : hierarchyGroups.values()){
				v.remove(arg0);
			}
		}
		return ret;
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
	
	/**
	 * Returns the BNA that belongs to the given value if present in this HierarchyList.
	 * @param arg0 The value
	 * @return The BNA
	 */
	public BiologicalNodeAbstract get(E arg0) {
		if(hierarchyGroups.containsKey(arg0)){
			return hierarchyGroups.get(arg0).getNode();
		}
		if(value.equals(arg0)){
			return node;
		}
		return null;
	}
	
	public BiologicalNodeAbstract getNode(){
		return node;
	}
	
	public E getValue(){
		return value;
	}
	
	/**
	 * Automatic coarsing based on the hierarchyGroups.
	 * Should only be called from public coarse() method.
	 * @param isInitialCall Has always to be set "true" if called from outside.
	 * @return Hierarchical BNA created by the method call.
	 */
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
				BiologicalNodeAbstract coarseNode =  BiologicalNodeAbstract.coarse(childNodes, null, getNode().getLabel(), getNode(), false);
				return coarseNode;
			}
			return BiologicalNodeAbstract.coarse(childNodes, null, getValue().toString());
		} else if(childNodes.size()==1){
			return childNodes.iterator().next();
		}
		return getNode();
	}
	
	/**
	 * Coarse the BNAs as sorted.
	 */
	public void coarse(){
		coarse(true);
		GraphInstance.getPathwayStatic().getRootPathway().updateMyGraph();
	}
	
	public void setNode(BiologicalNodeAbstract n){
		node = n;
	}
	
	public Collection<HierarchyList<E>> getSubLists(){
		return hierarchyGroups.values();
	}
	
	/**
	 * Automatic sorting of all contained BNAs.
	 * @param comp
	 */
	public void sort(HierarchyListComparator<? extends E> comp){
		hierarchyGroups = new HashMap<E, HierarchyList<E>>();
		HashMap<E, HierarchyList<E>> valueToList = new HashMap<E, HierarchyList<E>>();
		List<HierarchyList<E>> assignedLists = new ArrayList<HierarchyList<E>>();
		E value;
		E subvalue;
		
		for(BiologicalNodeAbstract n : this){
			value = comp.getValue(n);
			subvalue = comp.getSubValue(n);
			if(GraphInstance.getPathwayStatic().getRootNode() == n){
				setNode(n);
				this.value = subvalue;
				valueToList.put(subvalue, this);
				continue;
			}
			if(!valueToList.containsKey(value)){
				HierarchyList<E> l = new HierarchyList<E>(value);
				valueToList.put(value, l);
			}
		}

		value = null;
		subvalue = null;
		
		List<BiologicalNodeAbstract> elements = new ArrayList<BiologicalNodeAbstract>();
		elements.addAll(this);
		
		for(BiologicalNodeAbstract n : elements){
			if(getNode()==n){
				continue;
			}
			value = comp.getValue(n);
			subvalue = comp.getSubValue(n);
			if(valueToList.containsKey(subvalue)){
				if(value!=null && !subvalue.equals(value)){
					valueToList.get(subvalue).setNode(n);
					valueToList.get(value).add(valueToList.get(subvalue));
					assignedLists.add(valueToList.get(subvalue));
				} else {
					valueToList.get(subvalue).setNode(n);
					add(valueToList.get(subvalue));
					assignedLists.add(valueToList.get(subvalue));
				}
			} else {
				valueToList.get(value).add(new HierarchyList<E>(n,subvalue));
			}
		}

		for(HierarchyList<E> list : valueToList.values()){
			if(!assignedLists.contains(list) && list != this)
				add(list);
		}
		
//		printSubLists(0);
	}
	
	/**
	 * Method to sort over multiple hierarchical levels without refering to 
	 * other nodes.
	 * IMPORTANT: For multilayer sort with other nodes as references
	 * (e.g. in depth search), use default sort()-Method.
	 * @param comp
	 */
	public void sort(HierarchyListComparator<? extends E> comp, HierarchyStructure<? extends E> struc){
		hierarchyGroups = new HashMap<E, HierarchyList<E>>();
		HashMap<E, HierarchyList<E>> valueToList = new HashMap<E, HierarchyList<E>>();
		E value;
		E subvalue;
		HashSet<BiologicalNodeAbstract> nodes = new HashSet<BiologicalNodeAbstract>();
		nodes.addAll(this);
		for(BiologicalNodeAbstract n : nodes){
			subvalue = comp.getSubValue(n);
			value = comp.getValue(n);
			HierarchyList<E> list = new HierarchyList<E>(n,subvalue);
			valueToList.put(subvalue,list);
			if(value == null){
				add(list);
				continue;
			}
			while(value!=null){
				HierarchyList<E> lastList = list;
				if(!valueToList.containsKey(value)){
					list = new HierarchyList<E>(value);
					valueToList.put(value, list);
					list.add(lastList);
					if(struc.containsKey(value)){
						value = struc.get(value);
						continue;
					} else {
						add(list);
						break;
					}
				}
				valueToList.get(value).add(list);
				break;
			}
		}
//		printSubLists(0);
	}
	
	public void printSubLists(int level){

		if(getNode()!=null){
			System.out.println(level + " " + value + " " + getNode().getLabel());
		} else {
			System.out.println(level + " " + value);
		}
		for(HierarchyList<E> l : getSubLists()){
//			if(level<5)
				l.printSubLists(level+1);
		}
	}
	
}
