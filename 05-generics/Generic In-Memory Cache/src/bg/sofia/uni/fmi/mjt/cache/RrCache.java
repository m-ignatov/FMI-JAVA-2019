package bg.sofia.uni.fmi.mjt.cache;

import java.util.*;

public class RrCache<K, V> implements Cache<K, V> {

    private long successfulHits;
    private long allHits;
    private long capacity;

    private Map<K, V> cache;

    RrCache(long capacity) {
        cache = new HashMap<>();

        this.capacity = capacity;
        this.successfulHits = 0;
        this.allHits = 0;
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            successfulHits++;
        }
        allHits++;
        return value;
    }

    @Override
    public void set(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        if (size() == capacity) {
            evict();
        }
        cache.put(key, value);
    }

    @Override
    public boolean remove(K key) {
        return cache.remove(key) != null;
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public void clear() {
        successfulHits = 0;
        allHits = 0;
        cache.clear();
    }

    @Override
    public double getHitRate() {
        return (allHits != 0) ? (double) (successfulHits) / (allHits) : 0;
    }

    @Override
    public long getUsesCount(K key) {
        throw new UnsupportedOperationException();
    }

    private void evict() {
        List<K> keys = new ArrayList<>(cache.keySet());
        Collections.shuffle(keys);
        cache.remove(keys.get(0));
    }
}
