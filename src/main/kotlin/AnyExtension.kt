@file:Suppress("unused")

package com.isyscore.kotlin.common

data class ElseBlock<T>(val condition: Boolean, val obj: T)
fun<T> T.ifBlock(condition: Boolean, block: T.() -> T) = ElseBlock(condition, if (condition) block(this) else this)
fun<T> ElseBlock<T>.elseBlock(block: T.() -> T) = if (!condition) block(obj) else obj
fun<T> T.block(block: () -> Unit): T {
    block()
    return this
}