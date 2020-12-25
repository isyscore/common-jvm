package com.isyscore.kotlin.common

inline operator fun <reified T> MutableCollection<T>.minusAssign(list: List<T>) {
    this.removeIf { it in list }
}