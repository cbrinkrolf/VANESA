package util;

import java.util.HashMap;
import java.util.Map;

public class DoubleHashMap<KEY1, KEY2, V> {
	private final Map<KEY1, Map<KEY2, V>> map = new HashMap<>();

	/**
	 * Associates the specified value with the specified keys in this map. If the map previously contained a mapping for
	 * the keys, the old value is replaced by the specified value.
	 *
	 * @param key1  first key with which the specified value is to be associated
	 * @param key2  second key with which the specified value is to be associated
	 * @param value value to be associated with the specified keys
	 * @return the previous value associated with {@code key1} and {@code key2}, or {@code null} if there was no mapping
	 * for {@code key1} and {@code key2}. (A {@code null} return can also indicate that the map previously associated
	 * {@code null} with {@code key1} and {@code key2}.)
	 */
	public V put(final KEY1 key1, final KEY2 key2, final V value) {
		final Map<KEY2, V> k1Map = map.computeIfAbsent(key1, k -> new HashMap<>());
		final V returnValue = k1Map.get(key2);
		k1Map.put(key2, value);
		return returnValue;
	}

	/**
	 * Removes the mapping for a key from this map if it is present (optional operation). More formally, if this map
	 * contains a mapping from first key {@code key1} to {@code key2} and value {@code v} such that
	 * {@code Objects.equals(key, k)}, that mapping is removed. (The map can contain at most one such mapping.)
	 *
	 * @return the map to which this map previously associated the key, or {@code null} if the map contained no mapping
	 * for the key.
	 */
	public Map<KEY2, V> get(final KEY1 key1) {
		return map.get(key1);
	}

	/**
	 * Removes the mapping for a first and second key from this map if it is present (optional operation). More
	 * formally, if this map contains a mapping from first key {@code key1} and second key {@code key2} to value
	 * {@code v}, that mapping is removed. (The map can contain at most one such mapping.)
	 *
	 * @return the value to which this map previously associated the keys, or {@code null} if the map contained no
	 * mapping for the keys.
	 */
	public V get(final KEY1 key1, final KEY2 key2) {
		final Map<KEY2, V> k1Map = map.get(key1);
		return k1Map != null ? k1Map.get(key2) : null;
	}

	public void clear() {
		map.clear();
	}

	public Map<KEY2, V> remove(final KEY1 key1) {
		return map.remove(key1);
	}

	public V remove(final KEY1 key1, final KEY2 key2) {
		final Map<KEY2, V> k1Map = map.get(key1);
		return k1Map != null ? k1Map.remove(key2) : null;
	}

	public boolean containsKey(final KEY1 key1) {
		return map.containsKey(key1);
	}

	public boolean containsKey(final KEY1 key1, final KEY2 key2) {
		final Map<KEY2, V> k1Map = map.get(key1);
		return k1Map != null && k1Map.containsKey(key2);
	}

	public int size() {
		return map.size();
	}
}
