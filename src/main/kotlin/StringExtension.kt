@file:Suppress("unused", "SpellCheckingInspection")

package com.isyscore.kotlin.common

import com.isyscore.kotlin.common.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 命名方式转换，将下划线转换为驼峰
 */
fun String.conv(upperFirst: Boolean = false): String {
    val tmp = split("_").joinToString(separator = "") { it.toTitleUpperCase() }
    return if (upperFirst) tmp else tmp[0].lowercase() + tmp.drop(1)
}

/**
 * 命名方式转换，将驼峰转为下划线
 */
fun String.unconv(): String = replace("([A-Z])".toRegex(), "_\$1").lowercase()

/**
 * 命名方式转换，将下划线转换为驼峰，优先大写转小写
 */
fun String.uconv(upperFirst: Boolean = false): String = this.lowercase().conv(upperFirst)

/**
 * 命名方式转换，将驼峰转为下划线，优先小写转大写
 */
fun String.uunconv(): String = this.unconv().uppercase()


fun String.decodeURLPart(start: Int = 0, end: Int = length, charset: Charset = Charsets.UTF_8): String = decodeScan(start, end, false, charset)

fun String.toJsonEncoded() = this.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")
fun String.toTitleUpperCase() = substring(0, 1).uppercase(Locale.getDefault()) + substring(1)

fun String.appendPathPart(part: String) = when ((if (isNotEmpty() && this[length - 1] == '/') 1 else 0) + (if (part.isNotEmpty() && part[0] == '/') 1 else 0)) {
    2 -> this + part.removePrefix("/")
    1 -> this + part
    else -> StringBuilder(length + part.length + 1).apply { append(this@appendPathPart); append('/'); append(part) }.toString()
}

fun String.extension(): String {
    val indexOfName = lastIndexOf('/').takeIf { it != -1 } ?: lastIndexOf('\\').takeIf { it != -1 } ?: 0
    val indexOfDot = indexOf('.', indexOfName)
    return if (indexOfDot >= 0) substring(indexOfDot) else ""
}

fun String.replaceTag(tag: String, block: () -> String) = replace(tag, block())

fun String.skipEmptyLine() = lines().filterNot { it.trim() == "" }.joinToString("\n")

fun String.toMap() = split("&").associate { s -> s.indexOf("=").let { i -> Pair(s.substring(0, i), s.substring(i + 1)) } }

fun String.jsonToMap() = linkedMapOf<String, Any>().apply {
    try {
        val j = JSONObject(this as? LinkedHashMap<*, *>)
        j.keys().forEach { k ->
            this[k] = j[k]
        }
    } catch (_: Exception) {
    }
}.toMap()

fun String.toCookieMap() = split(";").map { it.trim() }.associate {
    val kv = it.split("=")
    Pair<String, Any>(kv[0].trim(), kv[1].trim())
}

fun String.toPair(): Pair<String, String> {
    val p = this.split("=").map { it.trim() }
    return Pair(p[0], p[1])
}

fun String.save(dest: File) = dest.writeText(this)

fun String.hash(alg: String): String = try {
    val instance = MessageDigest.getInstance(alg)
    val digest = instance.digest(this.toByteArray())
    val sb = StringBuffer()
    for (b in digest) {
        val i = b.toInt() and 0xff
        var hexString = Integer.toHexString(i)
        if (hexString.length < 2) {
            hexString = "0$hexString"
        }
        sb.append(hexString)
    }
    sb.toString()
} catch (e: Exception) {
    ""
}

val String.md5sha1: String get() = hash("MD5") + hash("SHA1")

fun String.asFileWriteText(text: String) = File(this).apply {
    parentFile.apply { if (!exists()) mkdirs() }
    writeText(text)
}

fun String.asFileReadText() = File(this).run { if (exists()) readText() else null }
fun String.asFileMkdirs() = File(this).apply { if (!exists()) mkdirs() }
fun String.asFile() = File(this)

private fun String.decodeScan(start: Int, end: Int, plusIsSpace: Boolean, charset: Charset): String {
    for (index in start until end) {
        val ch = this[index]
        if (ch == '%' || (plusIsSpace && ch == '+')) {
            return decodeImpl(start, end, index, plusIsSpace, charset)
        }
    }
    return if (start == 0 && end == length) toString() else substring(start, end)
}

private fun CharSequence.decodeImpl(start: Int, end: Int, prefixEnd: Int, plusIsSpace: Boolean, charset: Charset): String {
    val length = end - start
    val sbSize = if (length > 255) length / 3 else length
    val sb = StringBuilder(sbSize)
    if (prefixEnd > start) sb.append(this, start, prefixEnd)
    var index = prefixEnd
    var bytes: ByteArray? = null
    while (index < end) {
        val c = this[index]
        when {
            plusIsSpace && c == '+' -> {
                sb.append(' ')
                index++
            }
            c == '%' -> {
                if (bytes == null) bytes = ByteArray((end - index) / 3)
                var count = 0
                while (index < end && this[index] == '%') {
                    if (index + 2 >= end) throw Exception("Incomplete trailing HEX escape: ${substring(index)}, in $this at $index")
                    val digit1 = charToHexDigit(this[index + 1])
                    val digit2 = charToHexDigit(this[index + 2])
                    if (digit1 == -1 || digit2 == -1) throw Exception("Wrong HEX escape: %${this[index + 1]}${this[index + 2]}, in $this, at $index")
                    bytes[count++] = (digit1 * 16 + digit2).toByte()
                    index += 3
                }
                sb.append(String(bytes, offset = 0, length = count, charset = charset))
            }
            else -> {
                sb.append(c)
                index++
            }
        }
    }
    return sb.toString()
}

private fun charToHexDigit(c2: Char) = when (c2) {
    in '0'..'9' -> c2 - '0'
    in 'A'..'F' -> c2 - 'A' + 10
    in 'a'..'f' -> c2 - 'a' + 10
    else -> -1
}
