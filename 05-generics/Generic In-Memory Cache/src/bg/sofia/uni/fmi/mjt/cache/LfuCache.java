package bg.sofia.uni.fmi.mjt.cache;

import java.util.HashMap;
import java.util.Map;

public class LfuCache<K, V> implements Cache<K, V> {

    private long successfulHits;
    private long allHits;
    private long capacity;

    private Map<K, V> cache;
    private Map<K, Long> cacheCount;

    LfuCache(long capacity) {
        cache = new HashMap<>();
        cacheCount = new HashMap<>();

        this.capacity = capacity;
        this.successfulHits = 0;
        this.allHits = 0;
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            successfulHits++;
            increaseCount(key);
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
        increaseCount(key);
        cache.put(key, value);
    }

    @Override
    public boolean remove(K key) {
        return cache.remove(key) != null
                && cacheCount.remove(key) != null;
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
        Long count = cacheCount.get(key);
        return (count != null) ? count : 0;
    }

    private void evict() {
        K lfuKey = null;
        Long lfuCount = Long.MAX_VALUE;

        for (K key : cache.keySet()) {
            Long keyCount = cacheCount.get(key);
            if (keyCount < lfuCount) {
                lfuKey = key;
                lfuCount = keyCount;
            }
        }
        remove(lfuKey);
    }

    private void increaseCount(K key) {
        Long count = cacheCount.get(key);
        if (count == null) {
            count = 0L;
        }
        cacheCount.put(key, count + 1);
    }
}
