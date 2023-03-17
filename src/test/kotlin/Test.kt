import com.isyscore.kotlin.common.*
import com.isyscore.kotlin.common.json.JSONObject
import org.junit.Test
import java.lang.reflect.Method
import java.net.InetSocketAddress
import java.net.Proxy
import java.security.MessageDigest
import kotlin.math.ceil


class Sample {
    lateinit var list: MutableList<String>

    infix fun and(a: String): Sample {
        if (!this::list.isInitialized) {
            this.list = mutableListOf()
        }
        list.add(a)
        return this
    }
}


const val json = """
{
    "str":"a", 
    "int":1, 
    "obj":{"str":"b"}, 
    "array":[
        {"str":"x"},
        {"str":"y"},
        {"str":"z"}
    ]
}
"""

data class JsonClass(val str: String, val int: Int, val obj: String, val array: List<String>) {
    companion object
}

fun String.md5(): String {
    val ba = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return ba.map { (it.toInt() and 0xFF).toString().padStart(2, '0') }.reduce { acc, s -> acc + s }
}

fun JsonClass.Companion.fromString(str: String): JsonClass =
    JSONObject(str).run {
        JsonClass(
            getString("str"),
            getInt("int"),
            getJSONObject("obj").getString("str"),
            getJSONArray("array").map { a ->
                a as JSONObject
                a.getString("str")
            }
        )
    }

inline fun <reified T> newInstance(): T {

    val clz = T::class.java
    val mCreate = clz.getDeclaredConstructor()
    mCreate.isAccessible = true
    return mCreate.newInstance()
}

data class MyJsonClass(
    val str1: String,
    val str2: String
)

class TestCase {

    @Test
    fun testSample() {
        val sample = Sample()
        sample and "aaa"
        println(sample.list)
    }


    @Test
    fun check() {

        runCommand {
            commands.add("")
            commands.addAll(listOf())
        }.run { output join error }
        val (a, b, c, d) = 1 join 2 join 3 join 4 join 5 join 6
        println(a)
        println(b)
        println(c)
        println(d)

    }

    @Test
    fun httpTest() {
        val ret =
            httpGet("https://devapi.heweather.net/v7/weather/3d?location=101210101&key=338e3ef0ebf54d8580c0b1043ec5bcef")
        println(ret)
    }

    @Test
    fun test() {
        val jarr = JSONObject(JSON).getJSONObject("data").getJSONArray("result")
        val list = jarr.flatMap { item ->
            item as JSONObject
            val ip = item.getJSONObject("stream").getString("pod_host_ip")
            item.getJSONArray("values").map {
                // "$it".run { this.trimEnd(']') + ""","pod_host_ip":"$ip"]""" }
                "$it".run { """["$ip",""" + this.trimStart('[') }
            }
        }
        list.forEach { println(it) }
    }

    @Test
    fun timeTest() {
        val d = -0.1
        println(ceil(d).toInt())

    }

    fun <T, R> sample(item: T, block: T.() -> R): R = block(item)

    @Test
    fun call() {
        val n = 1.takeUnless { it % 2 == 0 }
        println(n)
    }

    @Test
    fun socksTest() {
        System.getProperties()["socksProxySet"] = "true"
        System.getProperties()["socksProxyHost"] = "localhost"
        System.getProperties()["socksProxyPort"] = "8888"
        http {
            url = "http://www.baidu.com"
            method = HttpMethod.GET
            proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("localhost", 8888))
            onSuccess { _, text, _, _ ->
                println(text)
            }
            onFail {
                println("fail: $it")
            }
        }
    }


    var parameterInfo: MutableMap<String, Any?>? = null

    fun enter(className: String?, method: Method, args: Array<Any?>) {
        if (method.name == "setRequestProperty" || method.name == "addRequestProperty") {
            (parameterInfo ?: mutableMapOf()).putAll(collectParameters(method, args))
        }
    }

    fun collectParameters(method: Method, args: Array<Any?>): MutableMap<String, Any?> =
        method.parameters.mapIndexed { i, p -> p.name to args[i] }.toMap().toMutableMap()

    @Test
    fun testIstio() {
        http {
            url = "http://10.211.55.16:10000/session"
            method = HttpMethod.GET
            headers["Accept"] = "*/*"
            headers["Content-Type"] = "text/plain;charset=UTF-8"
            headers["Accept-Encoding"] = "plain"
            onSuccess { code, text, headers, cookie ->
                println("resp: $code")
                println("text: $text")
            }
            onFail {
                println("ERROR")
                println(it)
            }
        }
    }

    @Test
    fun testReqLic() {
        http {
            url = "http://0.0.0.0:9900/clientInfo"
            method = HttpMethod.POST
            mimeType = "application/json"
            data = """{"code":"b66be662-2e0c-487b-ba8a-609e6b94815e", "email":"hexj@isyscore.com"}"""
            onSuccess { _, text, _, _ ->
                println("ret => $text")
            }
            onFail {
                println("fail => $it")
            }
        }
    }


    @Test
    fun testKt() {
        // val list = listOf(listOf("s")).flatten()
        val list = (0..99).toList().groupBy { }
        val str = list.map { "$it" }.reduce { acc, s -> "$acc:$s" }
        println(str)
    }

    @Test
    fun testPrint() {
        var str = "hello"
        str
    }

    fun sum(a: Double, b: Double, term: (Double) -> Double, next: (Double) -> Double): Double =
        if (a > b) 0.0 else term(a) + sum(next(a), b, term, next)

    fun integral(a: Double, b: Double, term:(Double)->Double, dx: Double): Double =
        dx * sum(a + (dx / 2.0), b, term) { x -> x + dx }


    @Test
    fun Test0() {
        val cube = { x: Double -> x * x * x }
        val p = integral(0.0, 1.0, cube, 0.01)
        println(p)
        val p1 = integral(0.0, 1.0, cube, 0.001)
        println(p1)
    }


    @Test
    fun testBool() {

    }
}


const val JSON = """
{"status":"success","data":{"resultType":"streams","result":[{"stream":{"stream":"stdout","app":"isc-sso-service","container_name":"isc-sso-service","filename":"/var/log/pods/default_isc-sso-service-5d8974f8f7-gsq7l_732aad5f-5faf-41d2-8961-05b94331e90a/isc-sso-service/0.log","namespace":"default","namespace_service":"default/isc-sso-service","pod_host_ip":"10.30.30.25","pod_name":"isc-sso-service-5d8974f8f7-gsq7l"},"values":[["1601440461869556478","2020-09-30 12:34:21.869 isc-sso-service-5d8974f8f7-gsq7l [isc-sso-service] [] INFO com.netflix.discovery.shared.resolver.aws.ConfigClusterResolver getClusterEndpoints [AsyncResolver-bootstrap-executor-0@6] : Resolving eureka endpoints via configuration\n"]]},{"stream":{"pod_host_ip":"10.30.30.35","pod_name":"isc-sso-service-55945b4584-fznl4","stream":"stdout","app":"isc-sso-service","container_name":"isc-sso-service","filename":"/var/log/pods/default_isc-sso-service-55945b4584-fznl4_eaeddca3-0805-408c-9347-e178a54c8820/isc-sso-service/0.log","namespace":"default","namespace_service":"default/isc-sso-service"},"values":[["1601440536414676305","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440536414663024","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440535715401069","2020-09-30 12:35:35.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440535436140277","2020-09-30 12:35:35.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440526413294030","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440526413279906","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440525715711193","2020-09-30 12:35:25.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440525436266500","2020-09-30 12:35:25.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440516412110787","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440516412096751","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440515715724983","2020-09-30 12:35:15.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440515436506536","2020-09-30 12:35:15.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440506410580870","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440506410543538","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440505715547311","2020-09-30 12:35:05.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440505436518134","2020-09-30 12:35:05.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440500000988401","path=./logs/middleware/monitor/10.244.6.195/monitor-client.log\n"],["1601440496409437349","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440496409428377","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440495715374032","2020-09-30 12:34:55.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440495436346878","2020-09-30 12:34:55.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440486407930352","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440486407915138","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440485715484827","2020-09-30 12:34:45.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440485436341135","2020-09-30 12:34:45.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440476406767365","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440476406753642","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440475715680633","2020-09-30 12:34:35.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440475436161112","2020-09-30 12:34:35.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440466405350186","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440466405336984","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440465715420246","2020-09-30 12:34:25.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440465436288670","2020-09-30 12:34:25.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440456404169846","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440456404129298","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440455715528364","2020-09-30 12:34:15.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440455436443067","2020-09-30 12:34:15.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440446402755659","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440446402748600","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440445717170865","2020-09-30 12:34:05.716 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440445436322445","2020-09-30 12:34:05.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440440001054487","path=./logs/middleware/monitor/10.244.6.195/monitor-client.log\n"],["1601440436401696714","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440436401683305","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440435715358053","2020-09-30 12:33:55.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440435436120932","2020-09-30 12:33:55.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440426400454240","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440426400442177","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440425715627512","2020-09-30 12:33:45.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440425436286783","2020-09-30 12:33:45.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440416399004232","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440416398991459","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440415715269693","2020-09-30 12:33:35.714 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440415436361738","2020-09-30 12:33:35.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440406397454517","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440406397440726","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440405715784125","2020-09-30 12:33:25.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440405436392283","2020-09-30 12:33:25.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440396396366032","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440396396337789","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440395715372265","2020-09-30 12:33:15.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440395436471604","2020-09-30 12:33:15.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440386394701404","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440386394668118","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440385715629779","2020-09-30 12:33:05.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440385436343375","2020-09-30 12:33:05.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440380001023809","path=./logs/middleware/monitor/10.244.6.195/monitor-client.log\n"],["1601440376393374437","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440376393361214","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440375715394510","2020-09-30 12:32:55.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440375436265904","2020-09-30 12:32:55.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440366392194759","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440366392162442","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440365715735638","2020-09-30 12:32:45.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440365436575502","2020-09-30 12:32:45.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440356391027508","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440356391014491","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440355715514921","2020-09-30 12:32:35.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440355436266993","2020-09-30 12:32:35.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440346389683158","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440346389646856","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440345717006101","2020-09-30 12:32:25.716 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440345436430137","2020-09-30 12:32:25.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"],["1601440336388115112","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440336388101135","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440335717112327","2020-09-30 12:32:15.716 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-15@6] : ------------tracerfilter start------------\n"],["1601440335436496836","2020-09-30 12:32:15.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-20@6] : ------------tracerfilter start------------\n"],["1601440326386842814","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440326386829807","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440325717197104","2020-09-30 12:32:05.716 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-17@6] : ------------tracerfilter start------------\n"],["1601440325436182920","2020-09-30 12:32:05.435 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-18@6] : ------------tracerfilter start------------\n"],["1601440320000986041","path=./logs/middleware/monitor/10.244.6.195/monitor-client.log\n"],["1601440316385647582","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440316385611111","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440315715769776","2020-09-30 12:31:55.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-16@6] : ------------tracerfilter start------------\n"],["1601440315436534507","2020-09-30 12:31:55.436 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-19@6] : ------------tracerfilter start------------\n"],["1601440306384168330","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440306384148483","path=./logs/middleware/trace/10.244.6.195/trace.log\n"],["1601440305715826063","2020-09-30 12:31:45.715 isc-sso-service-55945b4584-fznl4 [isc-sso-service] [] INFO com.isyscore.boot.web.filter.TracerFilter doFilterInternal [qtp586358252-13@6] : ------------tracerfilter start------------\n"]]}],"stats":{"summary":{"bytesProcessedPerSecond":43125540,"linesProcessedPerSecond":337251,"totalBytesProcessed":46546,"totalLinesProcessed":364,"execTime":0.001079314},"store":{"totalChunksRef":0,"totalChunksDownloaded":0,"chunksDownloadTime":0,"headChunkBytes":0,"headChunkLines":0,"decompressedBytes":0,"decompressedLines":0,"compressedBytes":0,"totalDuplicates":0},"ingester":{"totalReached":1,"totalChunksMatched":2,"totalBatches":0,"totalLinesSent":0,"headChunkBytes":46546,"headChunkLines":364,"decompressedBytes":0,"decompressedLines":0,"compressedBytes":0,"totalDuplicates":0}}}}
"""