
import com.isyscore.kotlin.common.go
import kotlinx.coroutines.*
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
            val r1 = r1a.await()
            val r2 = r2a.await()
            val r3 = r3a.await()
            val t2 = System.currentTimeMillis()
            println(r1)
            println(r2)
            println(r3)
            println(t2 - t1)
        }
    }

    suspend fun<T> Any.gInvokeSuspend(name: String, vararg params: Any): T? {
        val pparam = params.map { it::class.qualifiedName ?: "kotlin.Any" }
        val func = this::class.declaredFunctions.firstOrNull { f ->
            f.name == name && f.isSuspend && f.parameters.drop(1).map { p -> p.type.toString()} == pparam
        }?.apply { isAccessible = true } ?: return null
        return func.callSuspend(this, *params) as? T
    }

    suspend fun Any.iInvokeSuspend(name: String, vararg params: Any): Any? {
        val pparam = params.map { it::class.qualifiedName ?: "kotlin.Any" }
        val func = this::class.declaredFunctions.firstOrNull { f ->
            f.name == name && f.isSuspend && f.parameters.drop(1).map { p -> p.type.toString()} == pparam
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

}