package com.isyscore.kotlin.common

inline operator fun <reified T> Set<T>.minus(set: Set<T>) = this.filter { !set.contains(it) }.toMutableSet()