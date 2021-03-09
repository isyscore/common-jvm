
import com.isyscore.kotlin.common.go
import kotlinx.coroutines.delay
import org.junit.Test

class TestCoroutines {

    @Test
    fun test() {
        println("Start")
        go("a", "b") {
            println("item1 = $it, size = ${it.size}")
            delay(1000)
            println("Hello, ${it[0]}, ${it[1]}")
        }
        go {
            println("item2 = $it, size = ${it.size}")
        }

        Thread.sleep(2000) // 等待 2 秒钟
        println("Stop")
        go {}
    }

}