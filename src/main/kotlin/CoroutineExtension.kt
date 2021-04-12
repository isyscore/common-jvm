package com.isyscore.kotlin.common

import kotlinx.coroutines.*

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KtDsl

@KtDsl
fun go(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch { block() }

@KtDsl
fun<T> async(block: suspend CoroutineScope.() -> T): Deferred<T> = GlobalScope.async { block() }
