import com.isyscore.kotlin.common.json.JSONObject
import com.isyscore.kotlin.common.toJson
import com.isyscore.kotlin.common.toObj
import org.junit.Test

open class CSample(val name: String = "sample", val field1: String = "f1", val field2: String = "f2")

// typealias Email=String
// typealias MyEmail=Email

class MySample: CSample("", "", "")



class TestJson {

    @Test
    fun test() {
        String
        val str = CSample().toJson()
        println(str)

        val json = """{"name":"sample2","field1":"f166","field2":"f266"}"""
        val o = json.toObj<CSample>()
        println(o)

        val jobj = JSONObject(json)
        val n = jobj.getString("name")
        println(n)


    }

}