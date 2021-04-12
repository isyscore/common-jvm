import com.google.gson.Gson
import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import okio.ByteString.Companion.encodeUtf8
import org.junit.Test
import java.net.URLEncoder
import java.util.*

class TestDeepL {

    @Test
    fun test() {

        val req = ReqDeepL()
        req.params.jobs[0].raw_en_sentence = "测试一下。"
        val jsonData = Gson().toJson(req)
        println(jsonData)


        http {
            url = "https://static.deepl.com/css/deepl.\$c231b9.css"
            method = HttpMethod.POST
            onSuccess { code, text, headers, cookie ->
                val __cfduid = cookie.firstOrNull()?.value
                println(__cfduid)
                val uuid = UUID.randomUUID().toString()

                val dapSid = """{"sid":"$uuid", "lastUpdate":"${System.currentTimeMillis()}"}"""
                val dapEnc = URLEncoder.encode(dapSid, "UTF-8")
                println(dapEnc)

                http {
                    method = HttpMethod.POST
                    url = "https://www2.deepl.com/jsonrpc"
                    data = Gson().toJson(req)
                    mimeType = "application/json"
                    cookies["__cfduid"] = __cfduid ?: ""
                    cookies["dapSid"] = dapEnc
                    onSuccess { code, text, headers, cookie ->
                        println("code = $code")
                        println("text = $text")
                    }
                    onFail {
                        println("error = $it")
                    }
                }


            }
        }
    }

}