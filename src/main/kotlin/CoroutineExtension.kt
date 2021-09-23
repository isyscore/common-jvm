@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference")

package com.isyscore.kotlin.common

import kotlinx.coroutines.*

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KtDsl

/**
 * [全局] 切换至子协程运行，对所有平台有效
 */
@DelicateCoroutinesApi
@KtDsl
fun go(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(Dispatchers.Default) { block() }

/**
 * [全局] 切换至主协程运行，仅在 Android/Swing/JavaFX 界面操作下有效
 */
@DelicateCoroutinesApi
@KtDsl
fun gmain(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(Dispatchers.Main) { block() }

/**
 * [全局] 切换至 IO 协程运行，在 JVM 下均有效
 */
@DelicateCoroutinesApi
@KtDsl
fun gio(block: suspend CoroutineScope.() -> Unit): Job = GlobalScope.launch(Dispatchers.IO) { block() }

/**
 * [Job] 切换至 Job 下属子协程运行，对所有平台有效
 */
@KtDsl
fun cgo(job: Job, block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope (Dispatchers.Default + job).launch { block() }

/**
 * [Job] 切换至 Job 所属主协程运行，仅在 Android/Swing/JasvaFX 界面操作下有效
 */
@KtDsl
fun cgmain(job: Job, block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope (Dispatchers.Main + job).launch { block() }

/**
 * [Job] 切换至 Job 所属 IO 协程运行，在 JVM 下均有效
 */
@KtDsl
fun cgio(job: Job, block: suspend CoroutineScope.() -> Unit): Job = CoroutineScope (Dispatchers.IO + job).launch { block() }