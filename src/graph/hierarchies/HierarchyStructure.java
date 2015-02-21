package graph.hierarchies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HierarchyStructure<E> extends HashMap<E,E>{	
	
	private static final long serialVersionUID = -1246830965045992677L;
	protected List<E> rootElements = new ArrayList<E>();
	
	public HierarchyStructure(){
		super();
	}
	
	@Override
	public E put(E k, E v){
		if(rootElements.contains(k)){
			rootElements.remove(k);
		}
		if(!containsKey(v)){
			rootElements.add((E) v);
		}
		return super.put(k, v);
	}
	
	public boolean contains(E element){
		if(containsKey(element))
			return true;
		if(containsValue(element))
			return true;
		if(rootElements.contains(element))
			return true;
		return false;
	}
	
}
