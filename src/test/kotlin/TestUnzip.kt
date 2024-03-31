import com.isyscore.kotlin.common.unzip
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