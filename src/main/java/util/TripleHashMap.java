package util;

import java.util.HashMap;
import java.util.Map;

public class TripleHashMap<KEY1, KEY2, KEY3, V> {
	private final Map<KEY1, DoubleHashMap<KEY2, KEY3, V>> map = new HashMap<>();

	/**
	 * Associates the specified value with the specified keys in this map. If the map previously contained a mapping for
	 * the keys, the old value is replaced by the specified value.
	 *
	 * @param key1  first key with which the specified value is to be associated
	 * @param key2  second key with which the specified value is to be associated
	 * @param key3  third key with which the specified value is to be associated
	 * @param value value to be associated with the specified keys
	 * @return the previous value associated with {@code key1}, {@code key2}, and {@code key3}, or {@code null} if there
	 * was no mapping for {@code key1}, {@code key2}, and {@code key3}. (A {@code null} return can also indicate that
	 * the map previously associated {@code null} with {@code key1}, {@code key2}, and {@code key3}.)
	 */
	public V put(KEY1 key1, KEY2 key2, KEY3 key3, V value) {
		final DoubleHashMap<KEY2, KEY3, V> k1Map = map.computeIfAbsent(key1, k -> new DoubleHashMap<>());
		return k1Map.put(key2, key3, value);
	}

	public DoubleHashMap<KEY2, KEY3, V> get(KEY1 key1) {
		return map.get(key1);
	}

	public V get(KEY1 key1, KEY2 key2, KEY3 key3) {
		final DoubleHashMap<KEY2, KEY3, V> k1Map = map.get(key1);
		return k1Map != null ? k1Map.get(key2, key3) : null;
	}

	public void clear() {
		map.clear();
	}

	/**
	 * Removes the mapping for a key from this map if it is present (optional operation). More formally, if this map
	 * contains a mapping from first key {@code key1} to {@code key2}, {@code key3}, and value {@code v} such that
	 * {@code Objects.equals(key, k)}, that mapping is removed. (The map can contain at most one such mapping.)
	 *
	 * @return the map to which this map previously associated the key, or {@code null} if the map contained no mapping
	 * for the key.
	 */
	public DoubleHashMap<KEY2, KEY3, V> remove(KEY1 key1) {
		return map.remove(key1);
	}

	/**
	 * Removes the mapping for a first and second key from this map if it is present (optional operation). More
	 * formally, if this map contains a mapping from first key {@code key1} and second key {@code key2} to third key
	 * {@code key3} and value {@code v}, that mapping is removed. (The map can contain at most one such mapping.)
	 *
	 * @return the value to which this map previously associated the keys, or {@code null} if the map contained no
	 * mapping for the keys.
	 */
	public Map<KEY3, V> remove(KEY1 key1, KEY2 key2) {
		return map.get(key1).remove(key2);
	}

	/**
	 * Removes the mapping for a first and second key from this map if it is present (optional operation). More
	 * formally, if this map contains a mapping from first key {@code key1}, second key {@code key2}, and third key
	 * {@code key3} to value {@code v}, that mapping is removed. (The map can contain at most one such mapping.)
	 *
	 * @return the value to which this map previously associated the keys, or {@code null} if the map contained no
	 * mapping for the keys.
	 */
	public V remove(KEY1 key1, KEY2 key2, KEY3 key3) {
		return map.get(key1).get(key2).remove(key3);
	}

	public boolean containsKey(KEY1 key1) {
		return map.containsKey(key1);
	}

	public boolean containsKey(KEY1 key1, KEY2 key2) {
		final DoubleHashMap<KEY2, KEY3, V> k1Map = map.get(key1);
		return k1Map != null && k1Map.containsKey(key2);
	}

	public boolean containsKey(KEY1 key1, KEY2 key2, KEY3 key3) {
		final DoubleHashMap<KEY2, KEY3, V> k1Map = map.get(key1);
		return k1Map != null && k1Map.containsKey(key2, key3);
	}

	public int size() {
		return map.size();
	}
}
