package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.cache.enums.EvictionPolicy;

public interface CacheFactory {

    /**
     * Constructs a new Cache<K, V> with the specified maximum capacity and eviction policy
     *
     * @throws IllegalArgumentException if the given capacity is less than or equal to zero
     */
    static <K, V> Cache<K, V> getInstance(long capacity, EvictionPolicy policy) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        if (EvictionPolicy.LEAST_FREQUENTLY_USED == policy) {
            return new LfuCache<>(capacity);
        }
        if (EvictionPolicy.RANDOM_REPLACEMENT == policy) {
            return new RrCache<>(capacity);
        }
        return null;
    }

    /**
     * Constructs a new Cache<K, V> with maximum capacity of 10_000 items and the specified eviction policy
     */
    static <K, V> Cache<K, V> getInstance(EvictionPolicy policy) {
        final int capacity = 10_000;
        return getInstance(capacity, policy);
    }
}
