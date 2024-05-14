import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.concurrent.thread

class TestHttpPressure {

    @Test
    fun test() {
        // curl 'http://10.30.30.81:38080/api/permission/auth/status' -H 'token: 4f29d3a5-9556-436d-b0b1-8531e12baf2d'
        runBlocking {
            repeat(1) {
                val jobs = (1..500).map { async {
                    http {
                        url = "http://10.30.30.81:38080/api/permission/auth/status"
                        method = HttpMethod.GET
                        headers["token"] = "88c25554-bee9-4b6f-ad8a-ca3dc9818c8a"
                    } ?: "error"
                } }
                val list = jobs.awaitAll()
                println("done: ${list.size}")
                delay(1000L)
            }
        }
    }

    @Test
    fun testIG() {
        runBlocking {
            for (i in 1..5) {
                thread {
                    println(i)
                }
            }
        }
    }

}