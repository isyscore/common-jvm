import com.isyscore.kotlin.common.*
import org.junit.Test
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.logging.LogLevel
import org.ktorm.schema.Column
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.support.mysql.MySqlDialect
import java.math.BigDecimal

class TestKtormFunction {

    var database: Database

    init {
        val (db, err) = databasePoolOf("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/isc_big_magic?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Shanghai", "root", "rootroot", MySqlDialect(), logLevel = LogLevel.DEBUG)
        if (err == null) {
            database = db!!
        } else {
            throw err
        }
    }

    data class VO1(var a: Int = 0, var b: Int = 0)

    @Test
    fun testAscii() {
        val ret = database.from(CustomRouteConfigs).select(
            ascii(CustomRouteConfigs.code),
            ascii("x")
        ).limit(5).map { createEntitySeq<VO1>(it) }
        println(ret)
    }

    @Test
    fun testCharLength() {
        val ret = database.from(CustomRouteConfigs).select(
            charLength(CustomRouteConfigs.code),
            charLength("xyz")
        ).limit(5).map { createEntitySeq<VO1>(it) }
        println(ret)
    }

    data class VO2(var a: String = "")

    @Test
    fun testConcat() {
        val ret = database.from(CustomRouteConfigs).select(
            concat(CustomRouteConfigs.code, ":", CustomRouteConfigs.routeName)
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testLeft() {
        val ret = database.from(CustomRouteConfigs).select(
            left(CustomRouteConfigs.routeName, 5)
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testRepeat() {
        val ret = database.from(CustomRouteConfigs).select(
            repeat(CustomRouteConfigs.code, 5)
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testAbs() {
        val ret = database.from(CustomRouteConfigs).select(
            abs(10)
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testPI() {
        val ret = database.from(CustomRouteConfigs).select(
            pi().times(BigDecimal(5))
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testAddDate() {
        val ret = database.from(CustomRouteConfigs).select(
            addDate(CustomRouteConfigs.createTime, 5)
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testCurDate() {
        val ret = database.from(CustomRouteConfigs).select(

            curDate()
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testCurrentTimestamp() {
        val ret = database.from(CustomRouteConfigs).select(
            currentTimestamp()
        ).limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    @Test
    fun testNow() {
        val ret = database.from(CustomRouteConfigs)
            .select(
                CustomRouteConfigs.code
            )
            .where {
                var c0 = CustomRouteConfigs.code.isNotNull().expr()
                c0 += CustomRouteConfigs.code eq "1"
                // "a" like CustomRouteConfigs.code
                var c1 = CustomRouteConfigs.code eq "1"
                c1 += (CustomRouteConfigs.routeName eq "22")
                c1 += alwaysTrue

                var cond = alwaysTrue
                cond += ("addOqcDefectRecord" like concat("%", CustomRouteConfigs.code, "%"))
                cond
            }.limit(5).map { createEntitySeq<VO2>(it) }
        println(ret)
    }

    data class VO3(val a: String = "", val b: String = "")

    @Test
    fun testFromQuery1() {
        val ret = database.from(database.from(CustomRouteConfigs).tempSelect())
            .select(
                column<String>("code"),
                column<String>("route_name")
            ).limit(5).map { createEntitySeq<VO3>(it) }
        println(ret)
    }

    @Test
    fun testFromQuery2() {
        val ret = database.from(database.from(CustomRouteConfigs).tempSelect(CustomRouteConfigs.code, CustomRouteConfigs.routeName))
            .select(
                column<String>("code", CustomRouteConfigs),
                column<String>("route_name", CustomRouteConfigs)
            ).limit(5).map { createEntitySeq<VO3>(it) }
        println(ret)
    }

    data class VO4(val a: String = "", val b: String? = null, val c: String = "")

    @Test
    fun testArg() {
        val ret = database.from(CustomRouteConfigs).select(
            CustomRouteConfigs.code,
            null.asArg<String>().aliased("sample"),
            CustomRouteConfigs.routeName
        ).limit(5).map { createEntitySeq<VO4>(it) }
        println(ret)
    }
}


interface CustomRouteConfig : Entity<CustomRouteConfig> {
    companion object : Entity.Factory<CustomRouteConfig>()

    var code: String
    var routeName: String
    var routePath: String
    var reqMethod: String
    var reqDataType: String

    var scriptCode: String
    var extImport: LinkedHashSet<String>
    var extDefine: String
    var extFunction: String
    var sampleResponse: String
    var sampleResponseType: String
    var enabled: Boolean
    var createTime: String
    var updateTime: String
    var remark: String
}


object CustomRouteConfigs : Table<CustomRouteConfig>("custom_route_config") {

    var code = varchar("code").primaryKey().bindTo { it.code }
    var routeName = varchar("route_name").bindTo { it.routeName }
    var routePath = varchar("route_path").bindTo { it.routePath }
    var reqMethod = varchar("req_method").bindTo { it.reqMethod }
    var reqDataType = varchar("req_datatype").bindTo { it.reqDataType }
    var scriptCode = varchar("script_code").bindTo { it.scriptCode }
    var extImport = varchar("ext_import").transform({
        if (it.isBlank()) linkedSetOf() else it.toObj<LinkedHashSet<String>>()
    }, { it.toJson() }).bindTo { it.extImport }
    var extDefine = varchar("ext_define").bindTo { it.extDefine }
    var extFunction = varchar("ext_function").bindTo { it.extFunction }
    var sampleResponse = varchar("sample_response").bindTo { it.sampleResponse }
    var sampleResponseType = varchar("sample_response_type").bindTo { it.sampleResponseType }
    var enabled = int("enabled").transform({ it != 0 }, { if (it) 1 else 0 }).bindTo { it.enabled }
    var createTime = varchar("create_time").bindTo { it.createTime }
    var updateTime = varchar("update_time").bindTo { it.updateTime }
    var remark = varchar("remark").bindTo { it.remark }
}

val Database.customRouteConfigs get() = this.sequenceOf(CustomRouteConfigs)
