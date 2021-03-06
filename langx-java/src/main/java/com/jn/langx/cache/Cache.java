package com.jn.langx.cache;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.function.Supplier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface Cache<K, V> {
    /**
     * set a key-value to cache, with out expire time never
     * @param key the key
     * @param value the value
     */
    void set(@NonNull K key, @Nullable V value);

    /**
     * set a key-value to cache, with the specified expire time
     * @param key the key
     * @param value the value
     * @param expireTime the expire time
     */
    void set(@NonNull K key, @Nullable V value, long expireTime);

    /**
     * set a key-value to cache, with the specified expire time
     * @param key the key
     * @param value the value
     * @param ttl the time-to-live time, the live time
     */
    void set(@NonNull K key, @Nullable V value, long ttl, TimeUnit timeUnit);

    /**
     * get by key
     * @param key the key
     * @return the value, null if key is not exist
     */
    V get(@NonNull K key);

    /**
     * multiple get
     * @param keys get the specified keys
     * @return the values
     */
    Map<K, V> getAll(@NonNull Iterable<K> keys);

    /**
     * multiple get
     * @param keys get the specified keys
     * @return the values
     */
    Map<K, V> getAllIfPresent(@NonNull Iterable<K> keys);

    V getIfPresent(@NonNull K key);

    V get(@NonNull K key, @Nullable Supplier<K, V> loader);

    V remove(@NonNull K key);

    List<V> remove(Collection<K> keys);

    void refresh(@NonNull K key);

    void clean();

    int size();

    Map<K, V> toMap();
}
