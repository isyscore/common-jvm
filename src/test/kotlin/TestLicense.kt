import com.isyscore.kotlin.common.http
import com.isyscore.kotlin.common.httpGet
import org.junit.Test
import kotlin.concurrent.thread

class TestLicense {

    @Volatile var count = 0

    @Test
    fun test() {

        count = 0

        repeat(100) {

                val ret = httpGet("http://10.30.30.8:29013/api/license/valid")
                println("req[$it] = $ret")
                count++

        }
//
//        while (count < 100) {
//            Thread.sleep(1000)
//        }
    }

}