@file:Suppress("unused", "SpellCheckingInspection", "KDocUnresolvedReference")

package com.isyscore.kotlin.common

import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class KtDsl

/**
 * [全局] 切换至子协程运行，对所有平台有效
 */
@KtDsl
fun go(dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(dispatcher)
    val job = scope.launch { block() }
    return scope to job
}

/**
 * 带有超时的协程
 */
@KtDsl
fun go(timeoutMillis: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(dispatcher)
    val job = scope.launch {
        try {
            withTimeout(timeoutMillis) {
                block()
            }
        } catch (e: TimeoutCancellationException) {

        }
    }
    return scope to job
}

/**
 * [全局] 切行，仅在 Android/S换至主协程运wing/JavaFX 界面操作下有效
 */
@KtDsl
fun gmain(block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(Dispatchers.Main)
    val job = scope.launch { block() }
    return scope to job
}

/**
 * [全局] 切换至 IO 协程运行，在 JVM 下均有效
 */
@KtDsl
fun gio(block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(Dispatchers.IO)
    val job = scope.launch { block() }
    return scope to job
}

/**
 * [Job] 切换至 Job 下属子协程运行，对所有平台有效
 */
@KtDsl
fun cgo(job: Job, dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(dispatcher + job)
    val newjob = scope.launch { block() }
    return scope to newjob
}

/**
 * [Job] 切换至 Job 所属主协程运行，仅在 Android/Swing/JasvaFX 界面操作下有效
 */
@KtDsl
fun cgmain(job: Job, block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job> {
    val scope = CoroutineScope(Dispatchers.Main + job)
    val newjob = scope.launch { block() }
    return scope to newjob
}

/**
 * [Job] 切换至 Job 所属 IO 协程运行，在 JVM 下均有效
 */
@KtDsl
fun cgio(job: Job, block: suspend CoroutineScope.() -> Unit): Pair<CoroutineScope, Job>  {
    val scope = CoroutineScope(Dispatchers.IO + job)
    val newjob = scope.launch { block() }
    return scope to newjob
}

/**
 * 开启协程并返回协程的执行结果
 */
@KtDsl
suspend fun <T> goWith(dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> T): T {
    var result: T? = null
    val scope = CoroutineScope(dispatcher)
    val job = scope.launch {
        result = coroutineScope(block)
    }
    job.join()
    return result!!
}

/**
 * 开启协程并返回协程的执行结果，带有超时
 */
@KtDsl
suspend fun <T> goWith(timeoutMillis: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> T?): T? {
    var result: T? = null
    val scope = CoroutineScope(dispatcher)
    val job = scope.launch {
        try {
            withTimeout(timeoutMillis) {
                result = coroutineScope(block)
            }
        } catch (e: TimeoutCancellationException) {

        }
    }
    job.join()
    return result
}

@KtDsl
fun <T> goSync(dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> T): T {
    val future = CompletableFuture<T>()
    val scope = CoroutineScope(dispatcher)
    scope.launch {
        try {
            future.complete(block())
        } catch (e: Exception) {
            future.completeExceptionally(e)
        }
    }
    return future.get()
}

@KtDsl
fun <T> goSync(timeoutMillis: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend CoroutineScope.() -> T?): T? {
    val future = CompletableFuture<T>()
    val scope = CoroutineScope(dispatcher)
    val job = scope.launch {
        try {
            future.complete(block())
        } catch (e: Throwable) {
            future.completeExceptionally(e)
        }
    }
    return try{
        future.get(timeoutMillis, TimeUnit.MILLISECONDS)
    } catch (e: Throwable) {
        job.cancel()
        null
    }
}