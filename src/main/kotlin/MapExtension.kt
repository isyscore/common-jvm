@file:Suppress("unused")

package com.isyscore.kotlin.common

fun Map<String, Any>.toJSONString(): String {
    var ret = "{"
    this.keys.forEach {
        ret += "\"$it\":"
        val o = this[it]
        ret +=
                if (o is String) {
                    "\"${o.toJsonEncoded()}\","
                } else {
                    "$o,"
                }
    }
    ret = ret.trimEnd(',')
    ret += "}"
    return ret
}

fun Map<String, Any>.toCookieString() = map { "${it.key}=${it.value}" }.joinToString(";")

inline operator fun <reified K, V> Map<K, V>.minus(map: Map<K, V>) =
        this.filter { !map.contains(it.key) }.toMutableMap()

inline operator fun <reified K, V> Map<K, V>.minus(keys: List<K>) =
        this.filter { !keys.contains(it.key) }.toMutableMap()
