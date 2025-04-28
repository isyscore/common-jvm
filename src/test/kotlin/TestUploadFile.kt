import com.isyscore.kotlin.common.HttpMethod
import com.isyscore.kotlin.common.http
import com.isyscore.kotlin.common.toObj
import org.junit.Test

data class ResultData<T>(var code: Int = 0, var message: String = "", var data: T? = null)


class TestUploadFile {

    @Test
    fun test() {
        val result = http {
            url = "http://111.0.121.148:8883/background"
            method = HttpMethod.PUT
            getParam = "userId=7ce9055b5a2645d2885c670b4e59eece&robotId=LuoLuo"
            fileParam["file"] = "/Users/rarnu/Downloads/miku.jpg"

            onFail {
                println(it)
            }
        }?.toObj<ResultData<String>>()

        println(result)
    }

}