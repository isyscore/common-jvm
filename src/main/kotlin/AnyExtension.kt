@file:Suppress("unused")

package com.isyscore.kotlin.common

import kotlin.collections.map
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

data class ElseBlock<T>(val condition: Boolean, val obj: T)
fun<T> T.ifBlock(condition: Boolean, block: T.() -> T) = ElseBlock(condition, if (condition) block(this) else this)
fun<T> ElseBlock<T>.elseBlock(block: T.() -> T) = if (!condition) block(obj) else obj
fun<T> T.block(block: () -> Unit): T {
    block()
    return this
}

inline fun <reified T: Any> newInstance(vararg params: Any): T =
    T::class.java.getDeclaredConstructor(*params.map { it::class.java }.toTypedArray()).apply { isAccessible = true }.newInstance(*params)

inline fun <reified T: Any> newInstanceConvert(vararg params: Any, converter: (Class<*>) -> Class<*>): T =
    T::class.java.getDeclaredConstructor(*params.map { converter(it::class.java) }.toTypedArray()).apply { isAccessible = true }.newInstance(*params)

suspend fun<T> Any.invokeSuspend(name: String, vararg params: Any): T? {
    val pparam = params.map { it::class.qualifiedName ?: "kotlin.Any" }
    val func = this::class.declaredFunctions.firstOrNull { f ->
        f.name == name && f.isSuspend && f.parameters.drop(1).map { p -> p.type.toString()} == pparam
    }?.apply { isAccessible = true } ?: return null
    return func.callSuspend(this, *params) as? T
}