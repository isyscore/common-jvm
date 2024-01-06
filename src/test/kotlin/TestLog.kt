import com.isyscore.kotlin.common.gmain
import com.isyscore.kotlin.common.go
import org.junit.Test
import java.io.File
import kotlin.concurrent.timer

class TestLog {

    class LogCenter {
        private val logs = mutableListOf<String>()
        private val logFile = File("mylog.txt")

        fun log(message: String) {
            logs.add(message)
        }

        init {
            timer(period = 60000L) {
                gmain {
                    // 主协程写文件
                    logFile.appendText(logs.toString())
                    // 写完清空
                    logs.clear()
                }
            }
        }
    }

    @Test
    fun test() {

        val lc = LogCenter()

        go {
            // 子协程发送日志内容
            lc.log("sample")
        }
    }

}