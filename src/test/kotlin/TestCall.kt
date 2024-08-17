import org.junit.Test

class TestCall {

    @Test
    fun test() {

        get {

            call.respond()
        }

    }

    fun get(reqInstance: A.() -> Unit)  {
    }

    data class A(val call: B)

    class B {
        fun respond() {
            println("hello")
        }
    }

    open class Data1(open val a: String, val b: String)

    class Data2(override val a: String, b: String): Data1(a, b)

}