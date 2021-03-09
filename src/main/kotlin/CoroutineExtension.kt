package com.isyscore.kotlin.common

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KtDsl

@KtDsl
fun go(vararg params: Any?, block: suspend (params: Array<*>) -> Unit) {
    GlobalScope.launch {
        block(params)
    }
}