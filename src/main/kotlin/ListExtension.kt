@file:Suppress("unused")

package com.isyscore.kotlin.common

inline operator fun <reified T> List<T>.minus(list: List<T>) = this.filter { !list.contains(it) }.toMutableList()

fun<T> List<T>.toGridData(column: Int = 1) = mutableListOf<List<T>>().apply {
    var count = 0
    var sub = mutableListOf<T>()
    for (item in this@toGridData) {
        if (count == column) {
            add(sub.toList())
            sub = mutableListOf()
            sub.add(item)
            count = 1
            continue
        }
        sub.add(item)
        count++
    }
    if (sub.isNotEmpty()) { add(sub.toList()) }
}.toList()

fun <T> List<List<T>>.toListData() = mutableListOf<T>().apply {
    this@toListData.forEach { l -> l.forEach { i -> add(i) } }
}.toList()
