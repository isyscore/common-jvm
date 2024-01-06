
import org.junit.Test
import kotlin.concurrent.thread
import kotlin.math.abs

class Test2 {

    class Account {
        var balance: Int = 100
        fun withdraw(amount: Int): Int {
            if (amount > balance) {
                return -1
            } else {
                balance -= amount
                return balance
            }
        }
        fun deposit(amount: Int): Int {
            balance += amount
            return balance
        }
        fun dispatch(f: String): (Int) -> Int {
            if (f == "withdraw") {
                return this::withdraw
            } else if (f == "deposit") {
                return this::deposit
            } else {
                throw Exception("Unknown method")
            }
        }
    }

    @Test
    fun test() {
        val a = Account()
        println(a.dispatch("withdraw")(25))
    }

    fun sqrt(x: Double): Double {
        val goodEnough = { guess: Double ->
            abs(guess * guess - x) < 0.001
        }
        val improve = { guess: Double ->
            ((x / guess) + guess) / 2
        }
        fun sqrtIter(guess: Double): Double =
            if (goodEnough(guess)) guess else sqrtIter(improve(guess))
        return sqrtIter(1.0)
    }

    @Test
    fun testSqrt() {
        val a = sqrt(2.0)
        println(a)
    }

    /*
    (define (sgrt x)
( d e fi n e (good-enough? guess )
(< (abs (- (square guess) ×)) 0.001))
(define (improve guess) (average quess (/ X guess)))
(define (sqrt-iter guess) (if (good-enough? guess)
guess
(sqrt-iter (improve guess) )))
(sart-iter 1.0))
     */

    operator fun String.times(x: Int): String = this.repeat(x)
    operator fun Int.times(x: String): String = x.repeat(this)

    @Test
    fun testTimes() {
        var x = "abcde"
        val y = x * 2
        println(y)

        x *= 3
        println(x)

        var a = 5
        val b = a * "xyz"
        println(b)

    }

    val lock = Any()
    var balance = 100

    @Test
    fun test3() {

        thread {
            repeat(10) {
                thread {
                    synchronized(lock) {
                        balance -= 1
                        println("thread-1: $balance")
                    }
                }
            }
        }
        thread {
            repeat(10) {
                thread {
                    synchronized(lock) {
                        balance -= 1
                        println("thread-2: $balance")
                    }
                }
            }
        }
        Thread.sleep(2000L)
    }



}