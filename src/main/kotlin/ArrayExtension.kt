package com.isyscore.kotlin.common

inline operator fun <reified T> Array<T>.minus(arr: Array<T>) = this.filter { !arr.contains(it) }.toTypedArray()