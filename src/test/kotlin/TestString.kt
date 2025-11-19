import com.isyscore.kotlin.common.extension
import com.isyscore.kotlin.common.go
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import org.junit.Test
import java.io.File

class TestString {

    @Test
    fun testSubstring() {


        go {
            val a = async { delay(100); 1 }
            val b = async { delay(200); 2}
            val c = async { delay(300); 3 }
            a.await() + b.await() + c.await()
        }

        // 耗时300ms，返回结果是 6

    }

    @Test
    fun testCollect() {
        val m = mutableMapOf("a" to 1, "b" to 2, "c" to 3)
        val f = File("").readBytes()
    }

    @Test
    fun testExtension() {
        println("a.txt".extension())
    }
}

interface SampleInterface<T: Any> {
    fun <R> map(transform: (T) -> R): R
}

class SampleClass<T: Any>: SampleInterface<T> {

    private lateinit var item: T

    override fun <R> map(transform: (T) -> R): R = transform(item)
}