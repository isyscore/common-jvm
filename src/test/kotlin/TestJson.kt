import com.isyscore.kotlin.common.toJson
import com.isyscore.kotlin.common.toObj
import org.junit.Test

class TestJson {

    data class CSample(val name: String = "sample", val field1: String = "f1", val field2: String = "f2")

    @Test
    fun test() {
        val str = CSample().toJson()
        println(str)

        val json = """{"name":"sample2","field1":"f166","field2":"f266"}"""
        val o = json.toObj<CSample>()
        println(o)
    }

}