@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST", "unused")

package com.isyscore.kotlin.common

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit

private val cacheMap = mutableMapOf<Long, Cache<String, Any>>()

object Cache {

    fun<T> get(key: String, expiresMillis: Long = 5 * 60 * 1000): T? {
        var c = cacheMap[expiresMillis]
        if (c == null) {
            c = CacheBuilder<String, Any>.newBuilder().expireAfterWrite(expiresMillis, TimeUnit.MILLISECONDS).build()
            cacheMap[expiresMillis] = c
        }
        return c.getIfPresent(key) as? T
    }

    fun<T> get(key: String, expiresMillis: Long = 5 * 60 * 1000, loader: () -> T): T {
        var c = cacheMap[expiresMillis]
        if (c == null) {
            c = CacheBuilder<String, Any>.newBuilder().expireAfterWrite(expiresMillis, TimeUnit.MILLISECONDS).build()
            cacheMap[expiresMillis] = c
        }
        var item = c.getIfPresent(key) as? T
        if (item != null) {
            return item
        }
        item = loader()
        c.put(key, item)
        return item
    }

    fun clean(key: String) {
        cacheMap.forEach { (_, c) ->
            c.invalidate(key)
        }
    }

    fun cleanAll() {
        cacheMap.forEach { (_, c) ->
            c.invalidateAll()
        }
    }

    fun destroy(expiresMillis: Long) {
        cacheMap.remove(expiresMillis)
    }

    fun destroyAll() {
        cacheMap.clear()
    }
}

