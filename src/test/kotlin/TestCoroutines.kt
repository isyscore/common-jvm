import com.isyscore.kotlin.common.go
import com.isyscore.kotlin.common.goSync
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

class TestCoroutines {

    @Test
    fun test0() {
        runBlocking {
            val t1 = System.currentTimeMillis()
            val r1 = TestObj.gInvokeSuspend<Int>("over")
            val r2 = TestObj.gInvokeSuspend<Int>("over", 2)
            val r3 = TestObj.gInvokeSuspend<Int>("over", 2, 4)
            val t2 = System.currentTimeMillis()
            println(r1)
            println(r2)
            println(r3)
            println(t2 - t1)
        }
    }

    @Test
    fun test1() {
        runBlocking {
            val t1 = System.currentTimeMillis()
            val jobs = (1..6).map { async { TestObj.gInvokeSuspend<Int>("api$it") } }
            jobs.sumOf { it.await()!! }

            val r1a = async { TestObj.gInvokeSuspend<Int>("over") }
            val r2a = async { TestObj.gInvokeSuspend<Int>("over", 2) }
            val r3a = async { TestObj.gInvokeSuspend<Int>("over", 2, 4) }
            listOf<Deferred<Int?>>().awaitAll()
            val r1 = r1a.await()
            val r2 = r2a.await()
            val r3 = r3a.await()

            "".format()

            val t2 = System.currentTimeMillis()
            println(r1)
            println(r2)
            println(r3)
            println(t2 - t1)
        }
    }

    suspend fun <T> Any.gInvokeSuspend(name: String, vararg params: Any): T? {
        val pparam = params.map { it::class.qualifiedName ?: "kotlin.Any" }
        val func = this::class.declaredFunctions.firstOrNull { f ->
            f.name == name && f.isSuspend && f.parameters.drop(1).map { p -> p.type.toString() } == pparam
        }?.apply { isAccessible = true } ?: return null
        return func.callSuspend(this, *params) as? T
    }

    suspend fun Any.iInvokeSuspend(name: String, vararg params: Any): Any? {
        val pparam = params.map { it::class.qualifiedName ?: "kotlin.Any" }
        val func = this::class.declaredFunctions.firstOrNull { f ->
            f.name == name && f.isSuspend && f.parameters.drop(1).map { p -> p.type.toString() } == pparam
        }?.apply { isAccessible = true } ?: return null
        return func.callSuspend(this, *params)
    }

    @Test
    fun test() {

        val startTime = System.currentTimeMillis()
        go {
            val ret1 = async { getResult1() }
            val ret2 = async { getResult2() }
            val ret3 = async { getResult3() }
            ret1.cancel()
            val ret = ret1.await() + ret2.await() + ret3.await()
            val endTime = System.currentTimeMillis()
            println("ret = $ret, time = ${endTime - startTime}")
            done = true
        }

        while (!done) {
            // wait
        }
    }

    private suspend fun getResult1(): Int {
        delay(3000)
        return 1
    }

    private suspend fun getResult2(): Int {
        delay(4000)
        return 2
    }

    private suspend fun getResult3(): Int {
        delay(5000)
        return 3
    }

    @Volatile
    var done = false

    @Test
    fun test2() {

        go {
            try {
                withTimeout(5000) {
                    println("start awaiting with 5 secs.")
                    async {
                        while (!done) {
                            println("job waiting")
                            delay(500)
                        }
                    }.await()
                }
            } catch (ex: TimeoutCancellationException) {
                println("timeout")
            }
            done = true
        }

        while (!done) {
            // wait
        }
    }

    @Test
    fun test3() {
        runBlocking {
            val seq = sequence {
                log("yield 1,2,3")
                yieldAll(listOf(1, 2, 3))
                log("yield 4,5,6")
                yieldAll(listOf(4, 5, 6))
                log("yield 7,8,9")
                yieldAll(listOf(7, 8, 9))
            }
            val ss = seq.take(5)
            ss.forEach { println(it) }
        }
    }

    @Test
    fun test4() {
        val apis = listOf(::api1, ::api2, ::api3)
        runBlocking {
            val list = apis.map { api -> async { api() } }.awaitAll()
            list.sum()
        }
    }

    @Test
    fun test5() {
        val apis = listOf(::api1, ::api2, ::api3)
        val start = System.currentTimeMillis()
        val sum = goSync {
            val list = apis.map { api ->
                async {
                    delay(2000)
                    api()
                }
            }.awaitAll()
            list.sum()
        }
        val end = System.currentTimeMillis()
        println(end - start)
        println(sum)
    }

    @Test
    fun test6() {
        val apis = listOf(::api1, ::api2, ::api3)
        val start = System.currentTimeMillis()
        val sum = goSync(timeoutMillis = 1000) {
            val list = apis.map { api ->
                async {
                    api()
                }
            }.awaitAll()
            list.sum()
        }
        val end = System.currentTimeMillis()
        println(end - start)
        println(sum)

    }


}

fun api1(): Int = 1
fun api2(): Int {
    // throw RuntimeException("666")
    return 2
}
fun api3(): Int = 3

fun log(msg: String): Unit = println(msg)
fun log(msg: Int): Unit = println(msg)
