package util;

import java.util.HashMap;

public class DoubleHashMap<KEY1,KEY2,V>{

	private HashMap<KEY1,HashMap<KEY2,V>> map;
	
	
	public DoubleHashMap(){
		map = new HashMap<KEY1,HashMap<KEY2,V>>();
		
	}
	
	public void put(KEY1 k1, KEY2 k2, V v){
		if(!map.containsKey(k1)){
			map.put(k1, new HashMap<KEY2, V>());
		}
		if(!map.get(k1).containsKey(k2)){
			map.get(k1).put(k2, v);
		}
		//map.get(k1).p
	}
	
	public HashMap<KEY2, V> get(KEY1 k){
		return map.get(k);
	}
	
	public V get(KEY1 k1, KEY2 k2){
		return map.get(k1).get(k2);
	}
	
	public void clear(){
		map.clear();
	}
	
	public HashMap<KEY2, V> remove(KEY1 k){
		return map.remove(k);
	}
	
	public V remove(KEY1 k1, KEY2 k2){
		return map.get(k1).remove(k2);
	}
	
	public boolean contains(KEY1 k){
		return map.containsKey(k);
	}
	
	public boolean contains(KEY1 k1, KEY2 k2){
		return map.containsKey(k1) && map.get(k1).containsKey(k2);
	}
	
	public int size(){
		return map.size();
	}
	
}
