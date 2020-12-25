package com.isyscore.kotlin.common.json

import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

class JSONPointer {

    class Builder {
        private val refTokens: MutableList<String> = ArrayList()

        fun build() = JSONPointer(refTokens)

        fun append(token: String): Builder {
            refTokens.add(token)
            return this
        }

        fun append(arrayIndex: Int): Builder {
            refTokens.add("$arrayIndex")
            return this
        }
    }

    private val refTokens: MutableList<String>

    constructor(pointer: String) {

        if (pointer.isEmpty() || pointer == "#") {
            refTokens = mutableListOf()
            return
        }
        val refs = when {
            pointer.startsWith("#/") -> URLDecoder.decode(pointer.substring(2), ENCODING)
            pointer.startsWith("/") -> pointer.substring(1)
            else -> throw IllegalArgumentException("a JSON pointer should start with '/' or '#/'")
        }
        refTokens = mutableListOf()
        var slashIdx = -1
        var prevSlashIdx: Int
        do {
            prevSlashIdx = slashIdx + 1
            slashIdx = refs.indexOf('/', prevSlashIdx)
            refTokens.add(when {
                prevSlashIdx == slashIdx || prevSlashIdx == refs.length -> ""
                slashIdx >= 0 -> unescape(refs.substring(prevSlashIdx, slashIdx))
                else -> unescape(refs.substring(prevSlashIdx))
            })
        } while (slashIdx >= 0)
    }

    constructor(refTokens: List<String>) {
        this.refTokens = mutableListOf(*refTokens.toTypedArray())
    }

    @Throws(JSONPointerException::class)
    fun queryFrom(document: Any?): Any? {
        if (refTokens.isEmpty()) return document
        var current = document
        for (token in refTokens) {
            current = if (current is JSONObject) {
                current.opt(unescape(token))
            } else {
                (current as? JSONArray)?.let { readByIndexToken(it, token) } ?: throw JSONPointerException("value [$current] is not an array or object therefore its key $token cannot be resolved")
            }
        }
        return current
    }

    override fun toString(): String = refTokens.joinToString("") { "/${escape(it)}" }

    @Throws(RuntimeException::class)
    fun toURIFragment(): String = refTokens.joinToString("", prefix = "#") { "/${URLEncoder.encode(it, ENCODING)}" }

    companion object {
        private const val ENCODING = "utf-8"

        fun builder() = Builder()

        private fun escape(token: String) = token.replace("~", "~0").replace("/", "~1").replace("\\", "\\\\").replace("\"", "\\\"")
        private fun unescape(token: String) = token.replace("~1", "/").replace("~0", "~").replace("\\\"", "\"").replace("\\\\", "\\")

        @Throws(JSONPointerException::class)
        private fun readByIndexToken(current: Any, indexToken: String): Any = try {
            val index = indexToken.toInt()
            val currentArr = current as JSONArray
            if (index >= currentArr.length()) throw JSONPointerException("index $indexToken is out of bounds - the array has ${currentArr.length()} elements")
            try { currentArr[index] } catch (e: JSONException) { throw JSONPointerException("Error reading value at index position $index", e) }
        } catch (e: NumberFormatException) {
            throw JSONPointerException(String.format("%s is not an array index", indexToken), e)
        }

    }
}