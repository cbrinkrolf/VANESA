package util;

import java.util.HashMap;
import java.util.Set;

import biologicalObjects.edges.petriNet.PNArc;

public class TripleHashMap<KEY1, KEY2, KEY3, V> {

	private HashMap<KEY1, DoubleHashMap<KEY2, KEY3, V>> map;

	public TripleHashMap() {
		map = new HashMap<KEY1, DoubleHashMap<KEY2, KEY3, V>>();

	}

	public void put(KEY1 k1, KEY2 k2, KEY3 k3, V v) {
		if (!map.containsKey(k1)) {
			map.put(k1, new DoubleHashMap<KEY2, KEY3, V>());
		}
		map.get(k1).put(k2, k3, v);
		// map.get(k1).p
	}

	public DoubleHashMap<KEY2, KEY3, V> get(KEY1 k) {
		return map.get(k);
	}

	public V get(KEY1 k1, KEY2 k2, KEY3 k3) {
		// System.out.println("k1: "+k1);
		// System.out.println("k2: "+k2);
		// System.out.println("k3: "+k3);
		try {
			return map.get(k1).get(k2).get(k3);
		} catch (Exception e) {
			if(k1 instanceof PNArc){
				System.out.println(((PNArc) k1).getFrom().getName());
				System.out.println(((PNArc) k1).getTo().getName());
			}
			System.err.println("k1: " + k1);
			System.err.println("k2: " + k2);
			System.err.println("k3: " + k3);
		}
		return null;
	}

	public void clear() {
		map.clear();
	}

	public DoubleHashMap<KEY2, KEY3, V> remove(KEY1 k) {
		return map.remove(k);
	}

	public V remove(KEY1 k1, KEY2 k2, KEY3 k3) {
		return map.get(k1).get(k2).remove(k3);
	}

	public boolean contains(KEY1 k) {
		return map.containsKey(k);
	}

	public boolean contains(KEY1 k1, KEY2 k2) {
		return map.containsKey(k1) && map.get(k1).contains(k2);
	}

	public boolean contains(KEY1 k1, KEY2 k2, KEY3 k3) {
		return map.containsKey(k1) && map.get(k1).contains(k2, k3);
	}
	
	public int size(){
		return map.size();
	}
	
	public Set<KEY1> getKeys(){
		return map.keySet();
	}

}
