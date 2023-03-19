
import com.isyscore.kotlin.common.gio
import com.isyscore.kotlin.common.go
import kotlinx.coroutines.*
import org.junit.Test

class TestCoroutines {

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