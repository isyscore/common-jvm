import org.junit.Test
import java.util.Vector

class TestLicense {

    fun gcd(a: Int, b: Int): Int =
        if (b == 0) a else gcd(b, a % b)

    fun rem(n: Int, d: Int): Int =
        if (n < d) n else rem(n - d, d)


    @Test
    fun test() {
        println(gcd(10, 25))
        println(rem(12, 4))

        val vec = Vector<Int>(10)
        val v0 = vec[0]
        vec[1] = 1
    }
}