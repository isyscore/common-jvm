package com.isyscore.kotlin.common

import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.FunctionExpression
import org.ktorm.expression.ScalarExpression
import org.ktorm.schema.*
import java.math.BigDecimal
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime


/**
 * ASCII(s)
 *
 * 返回字符串 s 的第一个字符的 ASCII 码。
 */
fun ascii(s: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("ascii", listOf(s.asExpression()), IntSqlType)
fun ascii(s: String): FunctionExpression<Int> = FunctionExpression("ascii", listOf(ArgumentExpression(s, SqlType.of<String>()!!)), IntSqlType)

/**
 * CHAR_LENGTH(s)
 *
 * 返回字符串 s 的字符数
 */
fun charLength(s: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("char_length", listOf(s.asExpression()), IntSqlType)
fun charLength(s: String): FunctionExpression<Int> = FunctionExpression("char_length", listOf(ArgumentExpression(s, SqlType.of<String>()!!)), IntSqlType)

/**
 * CONCAT(s1,s2…sn)
 *
 * 字符串 s1,s2 等多个字符串合并为一个字符串
 *
 * @param args 数据类型必须是 ColumnDeclaring<*>, ScalarExpression<*>, 可以被转换为 String 的其他类型
 */
fun concat(vararg args: Any): FunctionExpression<String> = FunctionExpression("concat", args.map(::strMap), SqlType.of<String>()!!)
fun concat(vararg columns: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("concat", columns.map { it.asExpression() }, SqlType.of<String>()!!)

/**
 * FIELD(s,s1,s2…)
 *
 * 返回第一个字符串 s 在字符串列表(s1,s2…)中的位置
 *
 * @param args 数据类型必须是 ColumnDeclaring<*>, ScalarExpression<*>, 可以被转换为 String 的其他类型
 */
fun field(vararg args: Any): FunctionExpression<Int> = FunctionExpression("field", args.map(::strMap), IntSqlType)
fun field(vararg columns: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("field", columns.map { it.asExpression() }, IntSqlType)

/**
 * FIND_IN_SET(s1,s2)
 *
 * 返回在字符串s2中与s1匹配的字符串的位置
 */
fun findInSet(s1: ColumnDeclaring<String>, s2: String): FunctionExpression<Int> = FunctionExpression("find_in_set", listOf(s1.asExpression(), ArgumentExpression(s2, SqlType.of<String>()!!)), IntSqlType)
fun findInSet(s1: String, s2: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("find_in_set", listOf(ArgumentExpression(s1, SqlType.of<String>()!!), s2.asExpression()), IntSqlType)
fun findInSet(s1: ColumnDeclaring<String>, s2: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("find_in_set", listOf(s1.asExpression(), s2.asExpression()), IntSqlType)

/**
 * INSERT(s1,x,len,s2)
 *
 * 字符串 s2 替换 s1 的 x 位置开始长度为 len 的字符串
 */
fun insert(s1: ColumnDeclaring<String>, x: Int, len: Int, s2: String): FunctionExpression<String> =
    FunctionExpression("insert", listOf(s1.asExpression(), ArgumentExpression(x, SqlType.of<Int>()!!), ArgumentExpression(len, SqlType.of<Int>()!!), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun insert(s1: ColumnDeclaring<String>, x: Int, len: Int, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("insert", listOf(s1.asExpression(), ArgumentExpression(x, SqlType.of<Int>()!!), ArgumentExpression(len, SqlType.of<Int>()!!), s2.asExpression()), SqlType.of<String>()!!)

/**
 * LOCATE(s1,s)
 *
 * 从字符串 s 中获取 s1 的开始位置
 */
fun locate(s1: ColumnDeclaring<String>, s: String): FunctionExpression<Int> = FunctionExpression("locate", listOf(s1.asExpression(), ArgumentExpression(s, SqlType.of<String>()!!)), IntSqlType)
fun locate(s1: ColumnDeclaring<String>, s: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("locate", listOf(s1.asExpression(), s.asExpression()), IntSqlType)
fun locate(s1: String, s: ColumnDeclaring<String>): FunctionExpression<Int> = FunctionExpression("locate", listOf(ArgumentExpression(s1, SqlType.of<String>()!!), s.asExpression()), IntSqlType)

/**
 * LEFT(s,n)
 *
 * 返回字符串 s 的前 n 个字符
 */
fun left(s: ColumnDeclaring<String>, n: Int): FunctionExpression<String> = FunctionExpression("left", listOf(s.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<String>()!!)

/**
 * LPAD(s1,len,s2)
 *
 * 在字符串 s1 的开始处填充字符串 s2，使字符串长度达到 len
 */
fun lpad(s1: ColumnDeclaring<String>, len: Int, s2: String): FunctionExpression<String> =
    FunctionExpression("lpad", listOf(s1.asExpression(), ArgumentExpression(len, IntSqlType), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun lpad(s1: ColumnDeclaring<String>, len: Int, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("lpad", listOf(s1.asExpression(), ArgumentExpression(len, IntSqlType), s2.asExpression()), SqlType.of<String>()!!)

/**
 * LTRIM(s)
 *
 * 去掉字符串 s 开始处的空格
 */
fun ltrim(s: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("ltrim", listOf(s.asExpression()), SqlType.of<String>()!!)

/**
 * REPEAT(s,n)
 *
 * 将字符串 s 重复 n 次
 */
fun repeat(s: ColumnDeclaring<*>, n: Int): FunctionExpression<String> = FunctionExpression("repeat", listOf(s.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<String>()!!)

/**
 * REPLACE(s,s1,s2)
 *
 * 将字符串 s2 替代字符串 s 中的字符串 s1
 */
fun replace(s: ColumnDeclaring<String>, s1: String, s2: String): FunctionExpression<String> =
    FunctionExpression("replace", listOf(s.asExpression(), ArgumentExpression(s1, SqlType.of<String>()!!), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun replace(s: ColumnDeclaring<String>, s1: String, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("replace", listOf(s.asExpression(), ArgumentExpression(s1, SqlType.of<String>()!!), s2.asExpression()), SqlType.of<String>()!!)

fun replace(s: ColumnDeclaring<String>, s1: ColumnDeclaring<String>, s2: String): FunctionExpression<String> =
    FunctionExpression("replace", listOf(s.asExpression(), s1.asExpression(), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun replace(s: ColumnDeclaring<String>, s1: ColumnDeclaring<String>, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("replace", listOf(s.asExpression(), s1.asExpression(), s2.asExpression()), SqlType.of<String>()!!)

fun replace(s: String, s1: ColumnDeclaring<String>, s2: String): FunctionExpression<String> =
    FunctionExpression("replace", listOf(ArgumentExpression(s, SqlType.of<String>()!!), s1.asExpression(), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun replace(s: String, s1: ColumnDeclaring<String>, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("replace", listOf(ArgumentExpression(s, SqlType.of<String>()!!), s1.asExpression(), s2.asExpression()), SqlType.of<String>()!!)

fun replace(s: String, s1: String, s2: String): FunctionExpression<String> =
    FunctionExpression("replace", listOf(ArgumentExpression(s, SqlType.of<String>()!!), ArgumentExpression(s1, SqlType.of<String>()!!), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun replace(s: String, s1: String, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("replace", listOf(ArgumentExpression(s, SqlType.of<String>()!!), ArgumentExpression(s1, SqlType.of<String>()!!), s2.asExpression()), SqlType.of<String>()!!)

/**
 * REVERSE(s)
 *
 * 将字符串s的顺序反过来
 */
fun reverse(s: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("reverse", listOf(s.asExpression()), SqlType.of<String>()!!)

/**
 * RIGHT(s,n)
 *
 * 返回字符串 s 的后 n 个字符
 */
fun right(s: ColumnDeclaring<String>, n: Int): FunctionExpression<String> = FunctionExpression("right", listOf(s.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<String>()!!)

/**
 * RPAD(s1,len,s2)
 *
 * 在字符串 s1 的结尾处添加字符串 s2，使字符串的长度达到 len
 */
fun rpad(s1: ColumnDeclaring<String>, len: Int, s2: String): FunctionExpression<String> =
    FunctionExpression("rpad", listOf(s1.asExpression(), ArgumentExpression(len, IntSqlType), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)

fun rpad(s1: ColumnDeclaring<String>, len: Int, s2: ColumnDeclaring<String>): FunctionExpression<String> =
    FunctionExpression("rpad", listOf(s1.asExpression(), ArgumentExpression(len, IntSqlType), s2.asExpression()), SqlType.of<String>()!!)


/**
 * RTRIM(s)
 *
 * 去掉字符串 s 结尾处的空格
 */
fun rtrim(s: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("rtrim", listOf(s.asExpression()), SqlType.of<String>()!!)

/**
 * SPACE(n)
 *
 * 返回 n 个空格
 */
fun space(n: Int): FunctionExpression<String> = FunctionExpression("space", listOf(ArgumentExpression(n, IntSqlType)), SqlType.of<String>()!!)

/**
 * STRCMP(s1,s2)
 *
 * 比较字符串 s1 和 s2，如果 s1 与 s2 相等返回 0 ，如果 s1>s2 返回 1，如果 s1<s2 返回 -1
 */
fun strcmp(s1: ColumnDeclaring<String>, s2: String): FunctionExpression<String> = FunctionExpression("strcmp", listOf(s1.asExpression(), ArgumentExpression(s2, SqlType.of<String>()!!)), SqlType.of<String>()!!)
fun strcmp(s1: ColumnDeclaring<String>, s2: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("strcmp", listOf(s1.asExpression(), s2.asExpression()), SqlType.of<String>()!!)
fun strcmp(s1: String, s2: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("strcmp", listOf(ArgumentExpression(s1, SqlType.of<String>()!!), s2.asExpression()), SqlType.of<String>()!!)

/**
 * SUBSTRING(s, start, length)
 *
 * 从字符串 s 的 start 位置截取长度为 length 的子字符串
 */
fun substring(s: ColumnDeclaring<String>, start: Int, length: Int): FunctionExpression<String> = FunctionExpression("substring", listOf(s.asExpression(), ArgumentExpression(start, IntSqlType), ArgumentExpression(length, IntSqlType)), SqlType.of<String>()!!)

/**
 * SUBSTRING_INDEX(s, delimiter, number)
 *
 * 返回从字符串 s 的第 number 个出现的分隔符 delimiter 之后的子串。 如果 number 是正数，返回第 number 个字符左边的字符串。 如果 number 是负数，返回第(number 的绝对值(从右边数))个字符右边的字符串。
 */
fun substringIndex(s: ColumnDeclaring<String>, delimiter: String, number: Int): FunctionExpression<String> = FunctionExpression("substring_index", listOf(s.asExpression(), ArgumentExpression(delimiter, SqlType.of<String>()!!), ArgumentExpression(number, IntSqlType)), SqlType.of<String>()!!)

/**
 * TRIM(s)
 *
 * 去掉字符串 s 开始和结尾处的空格
 */
fun trim(s: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("trim", listOf(s.asExpression()), SqlType.of<String>()!!)

/**
 * ABS(x)
 *
 * 返回 x 的绝对值
 */
inline fun <reified T : Number> abs(x: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("abs", listOf(x.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> abs(x: T): FunctionExpression<T> = FunctionExpression("abs", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<T>()!!)

/**
 * ACOS(x)
 *
 * 求 x 的反余弦值（单位为弧度），x 为一个数值
 */
fun acos(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("acos", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> acos(x: T): FunctionExpression<BigDecimal> = FunctionExpression("acos", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * ASIN(x)
 *
 * 求反正弦值（单位为弧度），x 为一个数值
 */
fun asin(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("asin", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> asin(x: T): FunctionExpression<BigDecimal> = FunctionExpression("asin", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * ATAN(x)
 *
 * 求反正切值（单位为弧度），x 为一个数值
 */
fun atan(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("atan", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> atan(x: T): FunctionExpression<BigDecimal> = FunctionExpression("atan", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * ATAN2(n, m)
 *
 * 求反正切值（单位为弧度）
 */
inline fun <reified T : Number> atan2(n: ColumnDeclaring<*>, m: T): FunctionExpression<BigDecimal> = FunctionExpression("atan2", listOf(n.asExpression(), ArgumentExpression(m, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)
fun atan2(n: ColumnDeclaring<*>, m: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("atan2", listOf(n.asExpression(), m.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> atan2(n: T, m: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("atan2", listOf(ArgumentExpression(n, SqlType.of<T>()!!), m.asExpression()), SqlType.of<BigDecimal>()!!)

/**
 * CEIL(x)
 *
 * 返回大于或等于 x 的最小整数
 */
inline fun <reified T : Number> ceil(x: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("ceil", listOf(x.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> ceil(x: T): FunctionExpression<T> = FunctionExpression("ceil", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<T>()!!)

/**
 * CEILING(x)
 *
 * 返回大于或等于 x 的最小整数
 */
inline fun <reified T : Number> ceiling(x: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("ceiling", listOf(x.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> ceiling(x: T): FunctionExpression<T> = FunctionExpression("ceiling", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<T>()!!)

/**
 * COS(x)
 *
 * 求余弦值(参数是弧度)
 */
fun cos(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("cos", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> cos(x: T): FunctionExpression<BigDecimal> = FunctionExpression("cos", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * COT(x)
 *
 * 求余切值(参数是弧度)
 */
fun cot(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("cot", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> cot(x: T): FunctionExpression<BigDecimal> = FunctionExpression("cot", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * DEGREES(x)
 *
 * 将弧度转换为角度
 */
fun degree(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("degree", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> degree(x: T): FunctionExpression<BigDecimal> = FunctionExpression("degree", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * EXP(x)
 *
 * 返回 e 的 x 次方
 */
fun exp(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("exp", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> exp(x: T): FunctionExpression<BigDecimal> = FunctionExpression("exp", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * FLOOR(x)
 *
 * 返回小于或等于 x 的最大整数
 */
inline fun <reified T : Number> floor(x: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("floor", listOf(x.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> floor(x: T): FunctionExpression<T> = FunctionExpression("floor", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<T>()!!)

/**
 * LN
 *
 * 返回数字的自然对数，以 e 为底。
 */
fun ln(): FunctionExpression<BigDecimal> = FunctionExpression("ln", listOf(), SqlType.of<BigDecimal>()!!)

/**
 * LOG(x) 或 LOG(base, x)
 *
 * 返回自然对数(以 e 为底的对数)，如果带有 base 参数，则 base 为指定带底数。
 */
fun log(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("log", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> log(x: T): FunctionExpression<BigDecimal> = FunctionExpression("log", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * LOG(x) 或 LOG(base, x)
 *
 * 返回自然对数(以 e 为底的对数)，如果带有 base 参数，则 base 为指定带底数。
 */
inline fun <reified T : Number> log(base: ColumnDeclaring<*>, x: T): FunctionExpression<BigDecimal> = FunctionExpression("log", listOf(base.asExpression(), ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)
fun log(base: ColumnDeclaring<*>, x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("log", listOf(base.asExpression(), x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> log(base: T, x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("log", listOf(ArgumentExpression(base, SqlType.of<T>()!!), x.asExpression()), SqlType.of<BigDecimal>()!!)

/**
 * LOG10(x)
 *
 * 返回以 10 为底的对数
 */
fun log10(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("log10", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> log10(x: T): FunctionExpression<BigDecimal> = FunctionExpression("log10", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * LOG2(x)
 *
 * 返回以 2 为底的对数
 */
fun log2(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("log2", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> log2(x: T): FunctionExpression<BigDecimal> = FunctionExpression("log2", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * MOD(x,y)
 *
 * 返回 x 除以 y 以后的余数
 */
fun mod(x: ColumnDeclaring<Int>, y: Int): FunctionExpression<Int> = FunctionExpression("mod", listOf(x.asExpression(), ArgumentExpression(y, IntSqlType)), IntSqlType)
fun mod(x: ColumnDeclaring<Int>, y: ColumnDeclaring<Int>): FunctionExpression<Int> = FunctionExpression("mod", listOf(x.asExpression(), y.asExpression()), IntSqlType)
fun mod(x: Int, y: ColumnDeclaring<Int>): FunctionExpression<Int> = FunctionExpression("mod", listOf(ArgumentExpression(x, IntSqlType), y.asExpression()), IntSqlType)

/**
 * PI()
 *
 * 返回圆周率(3.141593）
 */
fun pi(): FunctionExpression<BigDecimal> = FunctionExpression("pi", listOf(), SqlType.of<BigDecimal>()!!)

/**
 * POW(x,y)
 *
 * 返回 x 的 y 次方
 */
inline fun <reified T : Number> pow(x: ColumnDeclaring<T>, y: T): FunctionExpression<T> = FunctionExpression("pow", listOf(x.asExpression(), ArgumentExpression(y, SqlType.of<T>()!!)), SqlType.of<T>()!!)
inline fun <reified T : Number> pow(x: ColumnDeclaring<T>, y: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("pow", listOf(x.asExpression(), y.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> pow(x: T, y: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("pow", listOf(ArgumentExpression(x, SqlType.of<T>()!!), y.asExpression()), SqlType.of<T>()!!)

/**
 * POWER(x,y)
 *
 * 返回 x 的 y 次方
 */
inline fun <reified T : Number> power(x: ColumnDeclaring<T>, y: T): FunctionExpression<T> = FunctionExpression("power", listOf(x.asExpression(), ArgumentExpression(y, SqlType.of<T>()!!)), SqlType.of<T>()!!)
inline fun <reified T : Number> power(x: ColumnDeclaring<T>, y: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("power", listOf(x.asExpression(), y.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> power(x: T, y: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("power", listOf(ArgumentExpression(x, SqlType.of<T>()!!), y.asExpression()), SqlType.of<T>()!!)

/**
 * RADIANS(x)
 *
 * 将角度转换为弧度
 */
fun radians(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("radians", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> radians(x: T): FunctionExpression<BigDecimal> = FunctionExpression("radians", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * ROUND(x [,y])
 *
 * 返回离 x 最近的整数，可选参数 y 表示要四舍五入的小数位数，如果省略，则返回整数。
 */
inline fun <reified T : Number> round(x: ColumnDeclaring<T>): FunctionExpression<T> = FunctionExpression("round", listOf(x.asExpression()), SqlType.of<T>()!!)
inline fun <reified T : Number> round(x: T): FunctionExpression<T> = FunctionExpression("round", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<T>()!!)

/**
 * ROUND(x [,y])
 *
 * 返回离 x 最近的整数，可选参数 y 表示要四舍五入的小数位数，如果省略，则返回整数。
 */
inline fun <reified T : Number> round(x: ColumnDeclaring<T>, y: Int): FunctionExpression<T> = FunctionExpression("round", listOf(x.asExpression(), ArgumentExpression(y, IntSqlType)), SqlType.of<T>()!!)
inline fun <reified T : Number> round(x: T, y: Int): FunctionExpression<T> = FunctionExpression("round", listOf(ArgumentExpression(x, SqlType.of<T>()!!), ArgumentExpression(y, IntSqlType)), SqlType.of<T>()!!)

/**
 * SIGN(x)	返回 x 的符号，x 是负数、0、正数分别返回 -1、0 和 1
 */
fun sign(x: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("sign", listOf(x.asExpression()), IntSqlType)
inline fun <reified T : Number> sign(x: T): FunctionExpression<Int> = FunctionExpression("sign", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), IntSqlType)

/**
 * SIN(x)
 *
 * 求正弦值(参数是弧度)
 */
fun sin(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("sin", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> sin(x: T): FunctionExpression<BigDecimal> = FunctionExpression("sin", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * SQRT(x)
 *
 * 返回x的平方根
 */
fun sqrt(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("sqrt", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> sqrt(x: T): FunctionExpression<BigDecimal> = FunctionExpression("sqrt", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * TAN(x)
 *
 * 求正切值(参数是弧度)
 */
inline fun <reified T : Number> tan(x: ColumnDeclaring<*>): FunctionExpression<BigDecimal> = FunctionExpression("tan", listOf(x.asExpression()), SqlType.of<BigDecimal>()!!)
inline fun <reified T : Number> tan(x: T): FunctionExpression<BigDecimal> = FunctionExpression("tan", listOf(ArgumentExpression(x, SqlType.of<T>()!!)), SqlType.of<BigDecimal>()!!)

/**
 * TRUNCATE(x,y)
 *
 * 返回数值 x 保留到小数点后 y 位的值（与 ROUND 最大的区别是不会进行四舍五入）
 */
inline fun <reified T : Number> truncate(x: ColumnDeclaring<T>, y: Int): FunctionExpression<T> = FunctionExpression("truncate", listOf(x.asExpression(), ArgumentExpression(y, IntSqlType)), SqlType.of<T>()!!)
inline fun <reified T : Number> truncate(x: T, y: Int): FunctionExpression<T> = FunctionExpression("truncate", listOf(ArgumentExpression(x, SqlType.of<T>()!!), ArgumentExpression(y, IntSqlType)), SqlType.of<T>()!!)

/**
 * ADDDATE(d,n)
 *
 * 计算起始日期 d 加上 n 天的日期
 */
fun addDate(d: ColumnDeclaring<*>, n: Int): FunctionExpression<Date> = FunctionExpression("adddate", listOf(d.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<Date>()!!)

/**
 * ADDTIME(t,n)	n
 *
 * 是一个时间表达式，时间 t 加上时间表达式 n
 */
fun addTime(t: ColumnDeclaring<*>, n: Int): FunctionExpression<Time> = FunctionExpression("addtime", listOf(t.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<Time>()!!)

/**
 * CURDATE()
 *
 * 返回当前日期
 */
fun curDate(): FunctionExpression<Date> = FunctionExpression("curdate", listOf(), SqlType.of<Date>()!!)

/**
 * CURRENT_DATE()
 *
 * 返回当前日期
 */
fun currentDate(): FunctionExpression<Date> = FunctionExpression("current_date", listOf(), SqlType.of<Date>()!!)

/**
 * CURRENT_TIME
 *
 * 返回当前时间
 */
fun currentTime(): FunctionExpression<Time> = FunctionExpression("current_time", listOf(), SqlType.of<Time>()!!)

/**
 * CURRENT_TIMESTAMP()
 *
 * 返回当前日期和时间
 */
fun currentTimestamp(): FunctionExpression<Timestamp> = FunctionExpression("current_timestamp", listOf(), SqlType.of<Timestamp>()!!)

/**
 * CURTIME()
 *
 * 返回当前时间
 */
fun curTime(): FunctionExpression<Time> = FunctionExpression("curTime", listOf(), SqlType.of<Time>()!!)

/**
 * DATE(d)
 *
 * 从日期或日期时间表达式中提取日期值
 */
fun date(d: ColumnDeclaring<*>): FunctionExpression<Date> = FunctionExpression("data", listOf(d.asExpression()), SqlType.of<Date>()!!)

/**
 * DATE_FORMAT(d,f)
 *
 * 按表达式 f的要求显示日期 d
 */
fun dateFormat(d: ColumnDeclaring<*>, f: String): FunctionExpression<String> = FunctionExpression("date_format", listOf(d.asExpression(), ArgumentExpression(f, SqlType.of<String>()!!)), SqlType.of<String>()!!)
fun dateFormat(d: ColumnDeclaring<*>, f: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("date_format", listOf(d.asExpression(), f.asExpression()), SqlType.of<String>()!!)

/**
 * DAY(d)
 *
 * 返回日期值 d 的日期部分
 */
fun day(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("day", listOf(d.asExpression()), IntSqlType)

/**
 * DAYNAME(d)
 *
 * 返回日期 d 是星期几，如 Monday,Tuesday
 */
fun dayName(d: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("day", listOf(d.asExpression()), SqlType.of<String>()!!)

/**
 * DAYOFMONTH(d)
 *
 * 计算日期 d 是本月的第几天
 */
fun dayOfMonth(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("dayofmonth", listOf(d.asExpression()), IntSqlType)

/**
 * DAYOFWEEK(d)
 *
 * 日期 d 今天是星期几，1 星期日，2 星期一，以此类推
 */
fun dayOfWeek(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("dayofweek", listOf(d.asExpression()), IntSqlType)

/**
 * DAYOFYEAR(d)
 *
 * 计算日期 d 是本年的第几天
 */
fun dayOfYear(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("dayofyear", listOf(d.asExpression()), IntSqlType)

/**
 * FROM_DAYS(n)
 *
 * 计算从 0000 年 1 月 1 日开始 n 天后的日期
 */
fun fromDays(n: ColumnDeclaring<Int>): FunctionExpression<Date> = FunctionExpression("from_days", listOf(n.asExpression()), SqlType.of<Date>()!!)

/**
 * HOUR(t)
 *
 * 返回 t 中的小时值
 */
fun hour(t: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("hour", listOf(t.asExpression()), IntSqlType)

/**
 * LAST_DAY(d)
 *
 * 返回给给定日期的那一月份的最后一天
 */
fun lastDay(d: ColumnDeclaring<*>): FunctionExpression<Date> = FunctionExpression("last_day", listOf(d.asExpression()), SqlType.of<Date>()!!)

/**
 * LOCALTIME()
 *
 * 返回当前日期和时间
 */
fun localTime(): FunctionExpression<LocalDateTime> = FunctionExpression("localtime", listOf(), SqlType.of<LocalDateTime>()!!)

/**
 * LOCALTIMESTAMP()
 *
 * 返回当前日期和时间
 */
fun localTimestamp(): FunctionExpression<LocalDateTime> = FunctionExpression("localtimestamp", listOf(), SqlType.of<LocalDateTime>()!!)

/**
 * MAKEDATE(year, day-of-year)
 *
 * 基于给定参数年份 year 和所在年中的天数序号 day-of-year 返回一个日期
 */
fun makeDate(year: ColumnDeclaring<Int>, dayOfYear: Int): FunctionExpression<Date> = FunctionExpression("makedate", listOf(year.asExpression(), ArgumentExpression(dayOfYear, IntSqlType)), SqlType.of<Date>()!!)
fun makeDate(year: ColumnDeclaring<Int>, dayOfYear: ColumnDeclaring<Int>): FunctionExpression<Date> = FunctionExpression("makedate", listOf(year.asExpression(), dayOfYear.asExpression()), SqlType.of<Date>()!!)
fun makeDate(year: Int, dayOfYear: ColumnDeclaring<Int>): FunctionExpression<Date> = FunctionExpression("makedate", listOf(ArgumentExpression(year, IntSqlType), dayOfYear.asExpression()), SqlType.of<Date>()!!)

/**
 * MAKETIME(hour, minute, second)
 *
 * 组合时间，参数分别为小时、分钟、秒
 */
fun makeTime(hour: ColumnDeclaring<Int>, minute: ColumnDeclaring<Int>, second: ColumnDeclaring<Int>): FunctionExpression<Time> = FunctionExpression("maketime", listOf(hour.asExpression(), minute.asExpression(), second.asExpression()), SqlType.of<Time>()!!)
fun makeTime(hour: ColumnDeclaring<Int>, minute: ColumnDeclaring<Int>, second: Int): FunctionExpression<Time> = FunctionExpression("maketime", listOf(hour.asExpression(), minute.asExpression(), ArgumentExpression(second, IntSqlType)), SqlType.of<Time>()!!)
fun makeTime(hour: ColumnDeclaring<Int>, minute: Int, second: ColumnDeclaring<Int>): FunctionExpression<Time> = FunctionExpression("maketime", listOf(hour.asExpression(), ArgumentExpression(minute, IntSqlType), second.asExpression()), SqlType.of<Time>()!!)
fun makeTime(hour: ColumnDeclaring<Int>, minute: Int, second: Int): FunctionExpression<Time> = FunctionExpression("maketime", listOf(hour.asExpression(), ArgumentExpression(minute, IntSqlType), ArgumentExpression(second, IntSqlType)), SqlType.of<Time>()!!)
fun makeTime(hour: Int, minute: ColumnDeclaring<Int>, second: ColumnDeclaring<Int>): FunctionExpression<Time> = FunctionExpression("maketime", listOf(ArgumentExpression(hour, IntSqlType), minute.asExpression(), second.asExpression()), SqlType.of<Time>()!!)
fun makeTime(hour: Int, minute: ColumnDeclaring<Int>, second: Int): FunctionExpression<Time> = FunctionExpression("maketime", listOf(ArgumentExpression(hour, IntSqlType), minute.asExpression(), ArgumentExpression(second, IntSqlType)), SqlType.of<Time>()!!)
fun makeTime(hour: Int, minute: Int, second: ColumnDeclaring<Int>): FunctionExpression<Time> = FunctionExpression("maketime", listOf(ArgumentExpression(hour, IntSqlType), ArgumentExpression(minute, IntSqlType), second.asExpression()), SqlType.of<Time>()!!)
fun makeTime(hour: Int, minute: Int, second: Int): FunctionExpression<Time> = FunctionExpression("maketime", listOf(ArgumentExpression(hour, IntSqlType), ArgumentExpression(minute, IntSqlType), ArgumentExpression(second, IntSqlType)), SqlType.of<Time>()!!)

/**
 * MICROSECOND(date)
 *
 * 返回日期参数所对应的微秒数
 */
fun microsecond(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("microsecond", listOf(d.asExpression()), IntSqlType)

/**
 * MINUTE(t)
 * 返回 t 中的分钟值
 */
fun minute(t: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("minute", listOf(t.asExpression()), IntSqlType)

/**
 * MONTHNAME(d)
 *
 * 返回日期当中的月份名称，如 November
 */
fun monthName(d: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("monthname", listOf(d.asExpression()), SqlType.of<String>()!!)

/**
 * MONTH(d)
 *
 * 返回日期d中的月份值，1 到 12
 */
fun month(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("month", listOf(d.asExpression()), IntSqlType)

/**
 * NOW()
 *
 * 返回当前日期和时间
 */
fun now(): FunctionExpression<Timestamp> = FunctionExpression("now", listOf(), SqlType.of<Timestamp>()!!)

/**
 * QUARTER(d)
 *
 * 返回日期d是第几季度，返回 1 到 4
 */
fun quarter(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("quarter", listOf(d.asExpression()), IntSqlType)

/**
 * SECOND(t)
 *
 * 返回 t 中的秒钟值
 */
fun second(t: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("second", listOf(t.asExpression()), IntSqlType)

/**
 * STR_TO_DATE(string, format_mask)
 *
 * 将字符串转变为日期
 */
fun strToDate(str: ColumnDeclaring<String>, format: String): FunctionExpression<Date> = FunctionExpression("str_to_date", listOf(str.asExpression(), ArgumentExpression(format, SqlType.of<String>()!!)), SqlType.of<Date>()!!)
fun strToDate(str: ColumnDeclaring<String>, format: ColumnDeclaring<String>): FunctionExpression<Date> = FunctionExpression("str_to_date", listOf(str.asExpression(), format.asExpression()), SqlType.of<Date>()!!)
fun strToDate(str: String, format: ColumnDeclaring<String>): FunctionExpression<Date> = FunctionExpression("str_to_date", listOf(ArgumentExpression(str, SqlType.of<String>()!!), format.asExpression()), SqlType.of<Date>()!!)

/**
 * SUBDATE(d,n)
 *
 * 日期 d 减去 n 天后的日期
 */
fun subDate(d: ColumnDeclaring<*>, n: Int): FunctionExpression<Date> = FunctionExpression("subdate", listOf(d.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<Date>()!!)

/**
 * SUBTIME(t,n)
 *
 * 时间 t 减去 n 秒的时间
 */
fun subTime(t: ColumnDeclaring<*>, n: Int): FunctionExpression<Time> = FunctionExpression("subtime", listOf(t.asExpression(), ArgumentExpression(n, IntSqlType)), SqlType.of<Time>()!!)

/**
 * SYSDATE()
 *
 * 返回当前日期和时间
 */
fun sysdate(): FunctionExpression<Timestamp> = FunctionExpression("sysdate", listOf(), SqlType.of<Timestamp>()!!)

/**
 * TIME(expression)
 *
 * 提取传入表达式的时间部分
 */
fun time(d: ColumnDeclaring<*>): FunctionExpression<Time> = FunctionExpression("time", listOf(d.asExpression()), SqlType.of<Time>()!!)

/**
 * TIME_FORMAT(t,f)
 *
 * 按表达式 f 的要求显示时间 t
 */
fun timeFormat(t: ColumnDeclaring<*>, f: String): FunctionExpression<String> = FunctionExpression("time_format", listOf(t.asExpression(), ArgumentExpression(f, SqlType.of<String>()!!)), SqlType.of<String>()!!)
fun timeFormat(t: ColumnDeclaring<*>, f: ColumnDeclaring<String>): FunctionExpression<String> = FunctionExpression("time_format", listOf(t.asExpression(), f.asExpression()), SqlType.of<String>()!!)

/**
 * TIMESTAMP(expression)
 *
 * 返回日期或日期时间表达式
 */
fun timestamp(d: ColumnDeclaring<*>): FunctionExpression<Timestamp> = FunctionExpression("timestamp", listOf(d.asExpression()), SqlType.of<Timestamp>()!!)

/**
 * TO_DAYS(d)
 *
 * 计算日期 d 距离 0000 年 1 月 1 日的天数
 */
fun toDays(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("to_days", listOf(d.asExpression()), IntSqlType)

/**
 * WEEK(d)
 *
 * 计算日期 d 是本年的第几个星期，范围是 0 到 53
 */
fun week(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("week", listOf(d.asExpression()), IntSqlType)

/**
 * WEEKDAY(d)
 *
 * 日期 d 是星期几，0 表示星期一，1 表示星期二
 */
fun weekday(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("weekday", listOf(d.asExpression()), IntSqlType)

/**
 * WEEKOFYEAR(d)
 *
 * 计算日期 d 是本年的第几个星期，范围是 0 到 53
 */
fun weekOfYear(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("weekofyear", listOf(d.asExpression()), IntSqlType)

/**
 * YEAR(d)
 *
 * 返回年份
 */
fun year(d: ColumnDeclaring<*>): FunctionExpression<Int> = FunctionExpression("year", listOf(d.asExpression()), IntSqlType)

/**
 * YEARWEEK(date, mode)
 *
 * 返回年份及第几周（0到53），mode 中 0 表示周天，1表示周一，以此类推
 */
fun yearWeek(d: ColumnDeclaring<*>, mode: Int): FunctionExpression<Int> = FunctionExpression("yearweek", listOf(d.asExpression(), ArgumentExpression(mode, IntSqlType)), IntSqlType)

/**
 * password(str）
 *
 * 将str字符串以数据库密码的形式加密
 */
fun password(c: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("password", listOf(c.asExpression()), SqlType.of<String>()!!)

/**
 * md5(str)
 *
 * 对str字符串以MD5不可逆算法模式加密
 */
fun md5(c: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("md5", listOf(c.asExpression()), SqlType.of<String>()!!)

/**
 * sha(str)
 *
 * 计算str字符串的散列算法校验值。
 */
fun sha(c: ColumnDeclaring<*>): FunctionExpression<String> = FunctionExpression("sha", listOf(c.asExpression()), SqlType.of<String>()!!)

fun strMap(arg: Any): ScalarExpression<*> = when (arg) {
    is Column<*> -> arg.asExpression()
    is ScalarExpression<*> -> arg
    else -> ArgumentExpression("$arg", SqlType.of<String>()!!)
}

