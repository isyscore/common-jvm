import com.isyscore.kotlin.common.createEntity
import com.isyscore.kotlin.common.databasePoolOf
import com.isyscore.kotlin.common.useMap
import org.junit.Test
import org.ktorm.support.mysql.MySqlDialect
import org.ktorm.support.oracle.OracleDialect
import java.sql.DriverManager

class TestDatabasePool {

    data class VO(var code: String = "", var routeName: String = "", var routePath: String = "")
    data class VO2(var id: Long = 0L, var kanji: String = "", var kk: String = "", var donetime: Long = 0L)

    @Test
    fun test() {
        val (db, err) = databasePoolOf("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/isc_big_magic?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai", "root", "rootroot", MySqlDialect())
        if (db == null) return
        val list = db.useConnection { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("select code, route_name, route_path from custom_route_config limit 5").useMap { createEntity<VO>(it) }
            }
        }
        println(list)
    }

    @Test
    fun testKingbase() {
        Class.forName("com.kingbase8.Driver")
        val conn = DriverManager.getConnection("jdbc:kingbase8://localhost:54321/root", "root", "rootroot")
        conn.schema = "yugiohapi2"
        println(conn)
    }

}