import com.isyscore.kotlin.common.join
import org.junit.Test
import java.lang.NumberFormatException

class TestSQL {

    fun getSQLIdentity(sql: String): List<Triple<String, Int, Int>> {
        var s = sql.replace("\n", " ").replace("\\s+".toRegex(), " ")
        s = s.substring(s.indexOf("(") + 1)
        val flist = s.split(",")
        val list = mutableListOf<Triple<String, Int, Int>>()
        var identityIdx = -1
        var identityField = ""
        var identityFrom = -1
        var identityStep: Int
        for (i in flist.indices) {
            if (identityIdx != -1 && i == identityIdx + 1) {
                identityStep = flist[i].trim(' ', ')').toInt()
                list.add(identityField join identityFrom join identityStep)
                continue
            }
            if (flist[i].contains(" identity(", ignoreCase = true)) {
                identityIdx = i
                identityField = flist[i].trim().split(" ")[0]
                val idstr = flist[i].substring(flist[i].indexOf("identity(") + 9)
                identityFrom = idstr.trim().toInt()
                continue
            }
            if (flist[i].contains(" auto_increment", ignoreCase = true)) {
                list.add(flist[i].trim().split(" ")[0] join 1 join 1)
            }
        }
        return list
    }

    fun mergeSQLIdentity(sql: String, ids: List<Triple<String, Int, Int>>): String {
        var s = sql.replace("\n", " ").replace("\\s+".toRegex(), " ")
        s = s.substring(s.indexOf("(") + 1).trim().trim(')', ';')
        val flist = s.split(",")
        val fieldStr = flist.map { item ->
            println(item)
            val fname = item.trim().split(" ")[0]
            val idItem = ids.firstOrNull { it.first == fname }
            if (idItem != null) {
                val idStr = " identity(${idItem.second},${idItem.third})"
                val itemStr = if (item.contains("number(", ignoreCase = true)) {
                    val nStartIdx = item.indexOf("number(")
                    var numStr = item.substring(nStartIdx + 7)
                    val nEndIdx = item.indexOf(")", startIndex = nStartIdx)
                    numStr = numStr.substring(0, numStr.indexOf(")"))
                    if (numStr.toInt() <= 10) {
                        // int
                        item.replaceRange(nStartIdx..nEndIdx, "int")
                    } else {
                        // bigint
                        item.replaceRange(nStartIdx..nEndIdx, "bigint")
                    }
                } else item
                itemStr + idStr
            } else item
        }.joinToString(",")
        return sql.substring(0, sql.indexOf("(") + 1) + fieldStr + ")"
    }

    @Test
    fun testMerge() {

        /*
        val sql = "create table sample(id int primary key auto_increment, name varchar(20))"
        val list = getSQLIdentity(sql)
        println(list)
         */

        val sql2 = "create table sample2(id int identity(2, 10), name varchar(100))"
        val list2 = getSQLIdentity(sql2)
        println(list2)

        val sqlDest = "create table sample(id number(10) primary key, name varchar2(20) not null);"
        val sqlM = mergeSQLIdentity(sqlDest, list2)
        println(sqlM)
    }

}