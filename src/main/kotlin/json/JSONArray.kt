@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SameParameterValue")

package com.isyscore.kotlin.common.json

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Array
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.collections.LinkedHashMap

class JSONArray : Iterable<Any?> {

    private val myArrayList: ArrayList<Any?>

    constructor() {
        myArrayList = ArrayList()
    }

    constructor(x: JSONTokener) : this() {
        if (x.nextClean() != '[') throw x.syntaxError("A JSONArray text must start with '['")
        var nextChar = x.nextClean()
        if (nextChar.code == 0) throw x.syntaxError("Expected a ',' or ']'")
        if (nextChar != ']') {
            x.back()
            while (true) {
                if (x.nextClean() == ',') {
                    x.back()
                    myArrayList.add(JSONObject.NULL)
                } else {
                    x.back()
                    myArrayList.add(x.nextValue())
                }
                when (x.nextClean()) {
                    0.toChar() -> throw x.syntaxError("Expected a ',' or ']'")
                    ',' -> {
                        nextChar = x.nextClean()
                        if (nextChar.code == 0) throw x.syntaxError("Expected a ',' or ']'")
                        if (nextChar == ']') return
                        x.back()
                    }
                    ']' -> return
                    else -> throw x.syntaxError("Expected a ',' or ']'")
                }
            }
        }
    }

    constructor(source: String) : this(JSONTokener(source))

    constructor(collection: Collection<*>?) {
        if (collection == null) {
            myArrayList = ArrayList()
        } else {
            myArrayList = ArrayList(collection.size)
            this.addAll(collection, true)
        }
    }

    constructor(iter: Iterable<*>?) : this() {
        if (iter == null) return
        this.addAll(iter, true)
    }

    constructor(array: JSONArray?) {
        myArrayList = if (array == null) {
            ArrayList()
        } else {
            ArrayList(array.myArrayList)
        }
    }

    constructor(array: Any) : this() {
        if (!array.javaClass.isArray) throw JSONException("JSONArray initial value should be a string or collection or array.")
        this.addAll(array, true)
    }

    constructor(initialCapacity: Int) {
        if (initialCapacity < 0) throw JSONException("JSONArray initial capacity cannot be negative.")
        myArrayList = ArrayList(initialCapacity)
    }

    override fun iterator(): Iterator<Any?> = myArrayList.iterator()

    @Throws(JSONException::class)
    operator fun get(index: Int): Any = opt(index) ?: throw JSONException("JSONArray[$index] not found.")

    @Throws(JSONException::class)
    fun getBoolean(index: Int): Boolean {
        val o = this[index]
        if (o == java.lang.Boolean.FALSE || o is String && o.equals("false", ignoreCase = true)) {
            return false
        } else if (o == java.lang.Boolean.TRUE || o is String && o.equals("true", ignoreCase = true)) {
            return true
        }
        throw wrongValueFormatException(index, "boolean", null)
    }

    @Throws(JSONException::class)
    fun getDouble(index: Int): Double {
        val o = this[index]
        return if (o is Number) {
            o.toDouble()
        } else try {
            o.toString().toDouble()
        } catch (e: Exception) {
            throw wrongValueFormatException(index, "double", e)
        }
    }

    @Throws(JSONException::class)
    fun getFloat(index: Int): Float {
        val o = this[index]
        return if (o is Number) {
            o.toFloat()
        } else try {
            o.toString().toFloat()
        } catch (e: Exception) {
            throw wrongValueFormatException(index, "float", e)
        }
    }

    @Throws(JSONException::class)
    fun getNumber(index: Int): Number {
        val o = this[index]
        return try {
            if (o is Number) o else JSONObject.stringToNumber("$o")
        } catch (e: Exception) {
            throw wrongValueFormatException(index, "number", e)
        }
    }

    @Throws(JSONException::class)
    fun <E : Enum<E>?> getEnum(clazz: Class<E>, index: Int): E = optEnum(clazz, index) ?: throw wrongValueFormatException(index, "enum of type " + JSONObject.quote(clazz.simpleName), null)

    @Throws(JSONException::class)
    fun getBigDecimal(index: Int): BigDecimal {
        val o = this[index]
        return JSONObject.objectToBigDecimal(o, null) ?: throw wrongValueFormatException(index, "BigDecimal", o, null)
    }

    @Throws(JSONException::class)
    fun getBigInteger(index: Int): BigInteger {
        val o = this[index]
        return JSONObject.objectToBigInteger(o, null) ?: throw wrongValueFormatException(index, "BigInteger", o, null)
    }

    @Throws(JSONException::class)
    fun getInt(index: Int): Int {
        val o = this[index]
        return if (o is Number) {
            o.toInt()
        } else try {
            o.toString().toInt()
        } catch (e: Exception) {
            throw wrongValueFormatException(index, "int", e)
        }
    }

    @Throws(JSONException::class)
    fun getJSONArray(index: Int): JSONArray {
        val o = this[index]
        return if (o is JSONArray) o else throw wrongValueFormatException(index, "JSONArray", null)
    }

    @Throws(JSONException::class)
    fun getJSONObject(index: Int): JSONObject {
        val o = this[index]
        return if (o is JSONObject) o else throw wrongValueFormatException(index, "JSONObject", null)
    }

    @Throws(JSONException::class)
    fun getLong(index: Int): Long {
        val o = this[index]
        return if (o is Number) {
            o.toLong()
        } else try {
            o.toString().toLong()
        } catch (e: Exception) {
            throw wrongValueFormatException(index, "long", e)
        }
    }

    @Throws(JSONException::class)
    fun getString(index: Int): String {
        val o = this[index]
        return if (o is String) o else throw wrongValueFormatException(index, "String", null)
    }

    fun isNull(index: Int): Boolean = JSONObject.NULL == opt(index)

    @Throws(JSONException::class)
    fun join(separator: String): String {
        val len = length()
        if (len == 0) return ""
        val sb = StringBuilder(JSONObject.valueToString(myArrayList[0]))
        for (i in 1 until len) {
            sb.append(separator).append(JSONObject.valueToString(myArrayList[i]))
        }
        return sb.toString()
    }

    fun length(): Int = myArrayList.size

    fun opt(index: Int): Any? = if (index < 0 || index >= length()) null else myArrayList[index]

    fun optBoolean(index: Int, defaultValue: Boolean = false): Boolean = try {
        getBoolean(index)
    } catch (e: Exception) {
        defaultValue
    }

    fun optDouble(index: Int, defaultValue: Double = Double.NaN) = (optNumber(index, null) ?: defaultValue).toDouble()

    fun optFloat(index: Int, defaultValue: Float = Float.NaN) = (optNumber(index, null) ?: defaultValue).toFloat()

    fun optInt(index: Int, defaultValue: Int = 0) = (optNumber(index, null) ?: defaultValue).toInt()

    fun <E : Enum<E>?> optEnum(clazz: Class<E>, index: Int): E? = this.optEnum(clazz, index, null)

    @Suppress("UNCHECKED_CAST")
    fun <E : Enum<E>?> optEnum(clazz: Class<E>, index: Int, defaultValue: E?): E? = try {
        val v = opt(index)
        when {
            v == JSONObject.NULL -> defaultValue
            clazz.isAssignableFrom(v!!.javaClass) -> v as E?
            else -> java.lang.Enum.valueOf(clazz, v.toString())
        }
    } catch (e: Exception) {
        defaultValue
    }

    fun optBigInteger(index: Int, defaultValue: BigInteger?): BigInteger? = JSONObject.objectToBigInteger(opt(index), defaultValue)

    fun optBigDecimal(index: Int, defaultValue: BigDecimal?): BigDecimal? = JSONObject.objectToBigDecimal(opt(index), defaultValue)

    fun optJSONArray(index: Int): JSONArray? {
        val o = opt(index)
        return if (o is JSONArray) o else null
    }

    fun optJSONObject(index: Int): JSONObject? {
        val o = opt(index)
        return if (o is JSONObject) o else null
    }

    fun optLong(index: Int, defaultValue: Long = 0) = (optNumber(index, null) ?: defaultValue).toLong()

    fun optNumber(index: Int, defaultValue: Number? = null): Number? = when (val v = opt(index)) {
        JSONObject.NULL -> defaultValue
        is Number -> v
        is String -> try { JSONObject.stringToNumber(v) } catch (e: Exception) { defaultValue }
        else -> defaultValue
    }

    fun optString(index: Int, defaultValue: String = ""): String {
        val o = opt(index)
        return if (o == JSONObject.NULL) defaultValue else "$o"
    }

    fun put(value: Boolean): JSONArray = this.put(if (value) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE)

    fun put(value: Collection<*>?): JSONArray = this.put(JSONArray(value))

    @Throws(JSONException::class)
    fun put(value: Double): JSONArray = this.put(java.lang.Double.valueOf(value))

    @Throws(JSONException::class)
    fun put(value: Float): JSONArray = this.put(java.lang.Float.valueOf(value))

    fun put(value: Int): JSONArray = this.put(Integer.valueOf(value))

    fun put(value: Long): JSONArray = this.put(java.lang.Long.valueOf(value))

    fun put(value: LinkedHashMap<*, *>?): JSONArray = this.put(JSONObject(value))

    fun put(value: Any?): JSONArray {
        JSONObject.testValidity(value)
        myArrayList.add(value)
        return this
    }

    @Throws(JSONException::class)
    fun put(index: Int, value: Boolean): JSONArray = this.put(index, if (value) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE)

    @Throws(JSONException::class)
    fun put(index: Int, value: Collection<*>?): JSONArray = this.put(index, JSONArray(value))

    @Throws(JSONException::class)
    fun put(index: Int, value: Double): JSONArray = this.put(index, java.lang.Double.valueOf(value))

    @Throws(JSONException::class)
    fun put(index: Int, value: Float): JSONArray = this.put(index, java.lang.Float.valueOf(value))

    @Throws(JSONException::class)
    fun put(index: Int, value: Int): JSONArray = this.put(index, Integer.valueOf(value))

    @Throws(JSONException::class)
    fun put(index: Int, value: Long): JSONArray = this.put(index, java.lang.Long.valueOf(value))

    @Throws(JSONException::class)
    fun put(index: Int, value: LinkedHashMap<*, *>?): JSONArray {
        this.put(index, JSONObject(value))
        return this
    }

    @Throws(JSONException::class)
    fun put(index: Int, value: Any): JSONArray {
        if (index < 0) throw JSONException("JSONArray[$index] not found.")
        if (index < length()) {
            JSONObject.testValidity(value)
            myArrayList[index] = value
            return this
        }
        if (index == length()) return this.put(value)
        myArrayList.ensureCapacity(index + 1)
        while (index != length()) {
            myArrayList.add(JSONObject.NULL)
        }
        return this.put(value)
    }

    fun putAll(collection: Collection<*>): JSONArray {
        this.addAll(collection, false)
        return this
    }

    fun putAll(iter: Iterable<*>): JSONArray {
        this.addAll(iter, false)
        return this
    }

    fun putAll(array: JSONArray): JSONArray {
        myArrayList.addAll(array.myArrayList)
        return this
    }

    @Throws(JSONException::class)
    fun putAll(array: Any): JSONArray {
        this.addAll(array, false)
        return this
    }

    fun query(jsonPointer: String): Any? = query(JSONPointer(jsonPointer))

    fun query(jsonPointer: JSONPointer): Any? = jsonPointer.queryFrom(this)

    fun optQuery(jsonPointer: String): Any? = optQuery(JSONPointer(jsonPointer))

    fun optQuery(jsonPointer: JSONPointer): Any? = try { jsonPointer.queryFrom(this) } catch (e: JSONPointerException) { null }

    fun remove(index: Int): Any? = if (index >= 0 && index < length()) myArrayList.removeAt(index) else null

    fun similar(other: Any?): Boolean {
        if (other !is JSONArray) return false
        val len = length()
        if (len != other.length()) return false
        var i = 0
        while (i < len) {
            val valueThis = myArrayList[i]
            val valueOther = other.myArrayList[i]
            if (valueThis === valueOther) {
                i += 1
                continue
            }
            if (valueThis == null) return false
            if (valueThis is JSONObject) {
                if (!valueThis.similar(valueOther)) return false
            } else if (valueThis is JSONArray) {
                if (!valueThis.similar(valueOther)) return false
            } else if (valueThis != valueOther) {
                return false
            }
            i += 1
        }
        return true
    }

    @Throws(JSONException::class)
    fun toJSONObject(names: JSONArray?): JSONObject? {
        if (names == null || names.isEmpty || isEmpty) return null
        val jo = JSONObject(names.length())
        var i = 0
        while (i < names.length()) {
            jo.put(names.getString(i), opt(i))
            i++
        }
        return jo
    }

    override fun toString() = try { this.toString(0) } catch (e: Exception) { "null" }

    @Throws(JSONException::class)
    fun toString(indentFactor: Int): String {
        val sw = StringWriter()
        return synchronized(sw.buffer) { write(sw, indentFactor, 0).toString() }
    }

    @JvmOverloads
    @Throws(JSONException::class)
    fun write(writer: Writer, indentFactor: Int = 0, indent: Int = 0): Writer {
        return try {
            var needsComma = false
            val length = length()
            writer.write('['.code)
            if (length == 1) {
                try {
                    JSONObject.writeValue(writer, myArrayList[0], indentFactor, indent)
                } catch (e: Exception) {
                    throw JSONException("Unable to write JSONArray value at index: 0", e)
                }
            } else if (length != 0) {
                val newIndent = indent + indentFactor
                var i = 0
                while (i < length) {
                    if (needsComma) writer.write(','.code)
                    if (indentFactor > 0) writer.write('\n'.code)
                    JSONObject.indent(writer, newIndent)
                    try {
                        JSONObject.writeValue(writer, myArrayList[i], indentFactor, newIndent)
                    } catch (e: Exception) {
                        throw JSONException("Unable to write JSONArray value at index: $i", e)
                    }
                    needsComma = true
                    i += 1
                }
                if (indentFactor > 0) writer.write('\n'.code)
                JSONObject.indent(writer, indent)
            }
            writer.write(']'.code)
            writer
        } catch (e: IOException) {
            throw JSONException(e)
        }
    }

    fun toList(): List<Any?> {
        val results = ArrayList<Any?>(myArrayList.size)
        for (element in myArrayList) {
            results.add(when(element) {
                null, JSONObject.NULL -> null
                is JSONArray -> element.toList()
                is JSONObject -> element.toMap()
                else -> element
            })
        }
        return results
    }

    val isEmpty: Boolean get() = myArrayList.isEmpty()

    private fun addAll(collection: Collection<*>, wrap: Boolean) {
        myArrayList.ensureCapacity(myArrayList.size + collection.size)
        if (wrap) {
            for (o in collection) {
                this.put(JSONObject.wrap(o))
            }
        } else {
            for (o in collection) {
                this.put(o)
            }
        }
    }

    private fun addAll(iter: Iterable<*>, wrap: Boolean) {
        if (wrap) {
            for (o in iter) {
                this.put(JSONObject.wrap(o))
            }
        } else {
            for (o in iter) {
                this.put(o)
            }
        }
    }

    @Throws(JSONException::class)
    private fun addAll(array: Any, wrap: Boolean) {
        when(array) {
            array.javaClass.isArray -> {
                val length = Array.getLength(array)
                myArrayList.ensureCapacity(myArrayList.size + length)
                if (wrap) {
                    var i = 0
                    while (i < length) {
                        this.put(JSONObject.wrap(Array.get(array, i)))
                        i += 1
                    }
                } else {
                    var i = 0
                    while (i < length) {
                        this.put(Array.get(array, i))
                        i += 1
                    }
                }
            }
            is JSONArray -> myArrayList.addAll(array.myArrayList)
            is Collection<*> -> this.addAll(array, wrap)
            is Iterable<*> -> this.addAll(array, wrap)
            else -> throw JSONException("JSONArray initial value should be a string or collection or array.")
        }
    }

    companion object {
        private fun wrongValueFormatException(idx: Int, valueType: String, cause: Throwable?) = JSONException("JSONArray[$idx] is not a $valueType.", cause)
        private fun wrongValueFormatException(idx: Int, valueType: String, value: Any, cause: Throwable?) = JSONException("JSONArray[$idx] is not a $valueType ($value).", cause)
    }
}