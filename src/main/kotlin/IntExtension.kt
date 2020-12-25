package com.isyscore.kotlin.common

fun Int.toGridData(columns: Int = 1) = mutableListOf<List<Int>>().apply {
    var count = 0
    var sub = mutableListOf<Int>()
    for (item in 0 until this@toGridData) {
        if (count == columns) {
            add(sub.toList())
            sub = mutableListOf()
            sub.add(item)
            count = 1
            continue
        }
        sub.add(item)
        count++
    }
    add(sub.toList())
}.toList()
