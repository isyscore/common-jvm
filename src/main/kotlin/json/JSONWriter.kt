package com.isyscore.kotlin.common.json

import java.io.IOException

open class JSONWriter(w: Appendable) {
    private var comma = false
    protected var mode = 'i'
    private val stack = arrayOfNulls<JSONObject>(MAX_DEPTH)
    private var top = 0
    protected var writer: Appendable = w

    @Throws(JSONException::class)
    private fun append(string: String): JSONWriter {
        if (mode == 'o' || mode == 'a') {
            try {
                if (comma && mode == 'a') writer.append(',')
                writer.append(string)
            } catch (e: IOException) {
                throw JSONException(e)
            }
            if (mode == 'o') mode = 'k'
            comma = true
            return this
        }
        throw JSONException("Value out of sequence.")
    }

    @Throws(JSONException::class)
    fun array(): JSONWriter {
        if (mode == 'i' || mode == 'o' || mode == 'a') {
            push(null)
            this.append("[")
            comma = false
            return this
        }
        throw JSONException("Misplaced array.")
    }

    @Throws(JSONException::class)
    private fun end(m: Char, c: Char): JSONWriter {
        if (mode != m) throw JSONException(if (m == 'a') "Misplaced endArray." else "Misplaced endObject.")
        pop(m)
        try {
            writer.append(c)
        } catch (e: IOException) {
            throw JSONException(e)
        }
        comma = true
        return this
    }

    @Throws(JSONException::class)
    fun endArray(): JSONWriter = end('a', ']')

    @Throws(JSONException::class)
    fun endObject(): JSONWriter = end('k', '}')

    @Throws(JSONException::class)
    fun key(string: String): JSONWriter {
        if (mode == 'k') {
            return try {
                val topObject = stack[top - 1]!!
                if (topObject.has(string)) throw JSONException("Duplicate key \"$string\"")
                topObject.put(string, true)
                if (comma) writer.append(',')
                writer.append(JSONObject.quote(string)).append(':')
                comma = false
                mode = 'o'
                this
            } catch (e: IOException) {
                throw JSONException(e)
            }
        }
        throw JSONException("Misplaced key.")
    }

    @Throws(JSONException::class)
    fun `object`(): JSONWriter {
        if (mode == 'i') mode = 'o'
        if (mode == 'o' || mode == 'a') {
            this.append("{")
            push(JSONObject())
            comma = false
            return this
        }
        throw JSONException("Misplaced object.")
    }

    @Throws(JSONException::class)
    private fun pop(c: Char) {
        if (top <= 0) throw JSONException("Nesting error.")
        val m = if (stack[top - 1] == null) 'a' else 'k'
        if (m != c) throw JSONException("Nesting error.")
        top -= 1
        mode = if (top == 0) 'd' else if (stack[top - 1] == null) 'a' else 'k'
    }

    @Throws(JSONException::class)
    private fun push(jo: JSONObject?) {
        if (top >= MAX_DEPTH) throw JSONException("Nesting too deep.")
        stack[top] = jo
        mode = if (jo == null) 'a' else 'k'
        top += 1
    }

    @Throws(JSONException::class)
    fun value(b: Boolean): JSONWriter = this.append(if (b) "true" else "false")

    @Throws(JSONException::class)
    fun value(d: Double): JSONWriter = this.append("$d")

    @Throws(JSONException::class)
    fun value(l: Long): JSONWriter = this.append("$l")

    @Throws(JSONException::class)
    fun value(o: Any?): JSONWriter = this.append(valueToString(o))

    companion object {
        private const val MAX_DEPTH = 200

        @Throws(JSONException::class)
        fun valueToString(value: Any?): String = when (value) {
            null -> "null"
            is JSONString -> try { value.toJSONString() } catch (e: Exception) { throw JSONException(e) } ?: throw JSONException("Bad value from toJSONString")
            is Number -> {
                val numberAsString = JSONObject.numberToString(value)
                if (JSONObject.numberPattern.matcher(numberAsString).matches()) numberAsString else JSONObject.quote(numberAsString)
            }
            is Boolean, is JSONObject, is JSONArray -> "$value"
            is LinkedHashMap<*, *> -> JSONObject(value as? LinkedHashMap<*, *>).toString()
            is Collection<*> -> JSONArray(value as? Collection<*>).toString()
            value.javaClass.isArray -> JSONArray(value).toString()
            is Enum<*> -> JSONObject.quote(value.name)
            else -> JSONObject.quote("$value")
        }
    }

}