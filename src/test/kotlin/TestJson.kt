import com.fasterxml.jackson.annotation.JsonProperty
import com.isyscore.kotlin.common.json.JSONArray
import com.isyscore.kotlin.common.json.JSONObject
import com.isyscore.kotlin.common.toJson
import com.isyscore.kotlin.common.toObj
import org.junit.Test

open class CSample(val name: String = "sample", val field1: String = "f1", val field2: String = "f2", val field3: String? = null)

class TestJson {

    @Test
    fun test() {
        val str = CSample().toJson()
        println(str)

        val json = """{"name":"sample2","field1":"f166","field2":"f266"}"""
        val o = json.toObj<CSample>()
        println(o)
    }

    @Test
    fun test1() {
        val json = """{"user_name":"hxj","user_age": 40}"""
        val u = json.toObj<SampleAssign>()
        println(u)
//        val jo = JSONObject(json)
//        val m: Map<String, Any?> = jo.toMap().toMutableMap()
//        println(m)
    }

    @Test
    fun test2() {
        val json = """[{"name":"aaaa", "age": 5},{"name":"bbbb", "age": 10}]"""
        val ja = JSONArray(json)
        val m: List<Map<String, Any?>> = ja.map { (it as JSONObject).toMap() }
        println(m)
    }


    @Test
    fun test3() {
        val json = """{'user_name':'hxj','age': 40}"""
        val u = json.toObj<SampleAssign>()
        println(u)
    }



}


data class SampleAssign(
    var user_name: String = "",
    @JsonProperty("age")
    var user_age: Int = 0)