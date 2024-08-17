import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import com.isyscore.kotlin.common.httpGet
import com.isyscore.kotlin.common.toObj
import org.junit.Test
import java.time.format.DateTimeFormatter

class TestNewHttp {

    @Test
    fun test() {
        val resp = httpGet("http://10.30.30.78:38080/api/core/license/info", header = mutableMapOf("token" to "4dd6f8ba-6863-45cc-bbc3-0196c110e4c6"))
        println(resp)

        val obj = http {
            method = HttpMethod.POST


        }
    }

}