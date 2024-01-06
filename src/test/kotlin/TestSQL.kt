import org.junit.Test
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.entity.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

interface Ids : Entity<Ids> {
    companion object : Entity.Factory<Ids>()
    val id: Int
    var name: String
}

object Idss : Table<Ids>("ids") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
}

val Database.idss get() = this.sequenceOf(Idss)

class TestSQL {

    @Test
    fun testMerge() {
        val database = Database.connect("jdbc:mysql://localhost:3306/sampledb", user = "root", password = "root")
        for (i in database.idss) {
            println(i.name)
        }
        println(database.idss.count())
        database.idss.add(Ids {
            name = "test233"
        })
    }

    @Test
    fun testUpdate() {
        val database = Database.connect("jdbc:mysql://localhost:3306/sampledb", user = "root", password = "root")
        val i = database.idss.find { it.id eq 2 }
        if ( i != null) {
            i.name = "changed"
            i.flushChanges()
        }
    }


}