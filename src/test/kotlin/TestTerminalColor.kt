import com.fasterxml.jackson.annotation.JsonFormat
import com.isyscore.kotlin.common.*
import org.junit.Test

data class Person(
    @JsonFormat(pattern = "yyyyMMdd_HHmmss")
    val name: String = "", val age: Int = 0)

class TestTerminalColor {

    @Test
    fun test0() {
        val str1 = """{name: "张三",age: 10}"""
        val str2 = """{'name': '张三','age': 10}"""
        val str3 = """{name: "张三",'age': 10}"""
        val p1 = str1.toObj<Person>()
        val p2 = str2.toObj<Person>()
        val p3 = str3.toObj<Person>()
        println(p1)
        println(p2)
        println(p3)
    }


    @Test
    fun test() {
        println("this is black string".black())
        println("this is red string".red())
        println("this is green string".green())
        println("this is yellow string".yellow())
        println("this is blue string".blue())
        println("this is magenta string".magenta())
        println("this is cyan string".cyan())
        println("this is white string".white())

        println("this is red string with green background".red().bgGreen())

    }

    @Test
    fun test1() {
        val c = generate<C1>()
        println(c)

        val (f1,f2) = C2()


    }

    data class C1 (val f1: String = "", val f2: Int = 0)

    inline fun <reified T> generate() = T::class.constructors.firstOrNull {
        it.parameters.isEmpty()
    }?.call() ?: throw RuntimeException("")


    class C2 {
        var f1: String = ""
        var f2: Int = 0
        operator fun component1() = f1
        operator fun component2() = f2
    }
}
