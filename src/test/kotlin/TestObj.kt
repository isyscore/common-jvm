import kotlinx.coroutines.delay

object TestObj {

    suspend fun over(): Int {
        delay(500)
        return 0
    }
    suspend fun over(a: Int): Int {
        delay(500)
        return a
    }
    suspend fun over(a: Int, b: Int): Int {
        delay(500)
        return a * b
    }

}