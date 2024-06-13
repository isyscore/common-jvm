import com.isyscore.kotlin.common.gio
import com.isyscore.kotlin.common.gmain
import com.isyscore.kotlin.common.go
import com.isyscore.kotlin.common.unzip
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import org.junit.Test

class TestUnzip {

    @Test
    fun test() {
        unzip {
            zipPath = "/Users/rarnu/Code/github/ygodiy/tools-vue-rarnu/dist/yugioh.zip"
            destPath = "/Users/rarnu/Code/github/common-jvm/output"
        }

    }

}