@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.sql.Array
import java.sql.Date
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp

fun ResultSet.toJsonArray(): String {
    var str = "["
    if (first()) {
        do {
            var item = "{"
            for (i in 0 until metaData.columnCount) {
                item += "\"${metaData.getColumnName(i)}\": \"${getString(i)}\","
            }
            item += "},"
            str += item
        } while (next())
    }
    str = str.trimEnd(',')
    str += "]"
    return str
}

fun ResultSet.forEach(operator:(ResultSet) -> Unit) {
    if (first()) {
        do {
            operator(this)
        } while (next())
    }
}

fun ResultSet.firstRecord(operator: (ResultSet) -> Unit) {
    if (first()) {
        operator(this)
    }
}

fun ResultSet.string(columnName: String): String = getString(columnName)
fun ResultSet.short(columnName: String): Short = getShort(columnName)
fun ResultSet.int(columnName: String): Int = getInt(columnName)
fun ResultSet.float(columnName: String): Float = getFloat(columnName)
fun ResultSet.double(columnName: String): Double = getDouble(columnName)
fun ResultSet.long(columnName: String): Long = getLong(columnName)
fun ResultSet.timestamp(columnName: String): Timestamp = getTimestamp(columnName)
fun ResultSet.date(columnName: String): Date = getDate(columnName)
fun ResultSet.time(columnName: String): Time = getTime(columnName)
fun ResultSet.obj(columnName: String): Any = getObject(columnName)
fun ResultSet.array(columnName: String): Array = getArray(columnName)
fun ResultSet.byte(columnName: String): Byte = getByte(columnName)
fun ResultSet.bytes(columnName: String): ByteArray = getBytes(columnName)