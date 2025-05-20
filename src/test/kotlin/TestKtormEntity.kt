import com.fasterxml.jackson.annotation.JsonProperty
import com.isyscore.kotlin.common.toJson
import org.junit.Test
import org.ktorm.entity.Entity

class TestKtormEntity {

    @Test
    fun test() {
        val s = Sample1 {
            id = 1L
        }
        println(s.toJson())
    }

}

interface Sample1 : Entity<Sample1> {
    companion object: Entity.Factory<Sample1>()

    @get:JsonProperty("mName")
    var id: Long
    var name: String
    var age: Int?
}
