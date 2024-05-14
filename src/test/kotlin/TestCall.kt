import org.junit.Test

class TestCall {

    @Test
    fun test() {

        get {

            call.respond()
        }

    }

    fun get(reqInstance: A.() -> Unit)  {
        //
    }

    data class A(val call: B)

    class B {
        fun respond() {
            println("hello")
        }
    }
}