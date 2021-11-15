@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SameParameterValue")

package com.isyscore.kotlin.common.json

import java.io.Closeable
import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.LinkedHashMap

class JSONObject {
    private class Null : Cloneable {
        override fun clone() = this
        override fun equals(other: Any?) = other == null || other === this
        override fun hashCode() = 0
        override fun toString() = "null"
    }

    private val map: LinkedHashMap<String, Any?>

    constructor() {
        map = LinkedHashMap()
    }

    constructor(jo: JSONObject, vararg names: String) : this(names.size) {
        var i = 0
        while (i < names.size) {
            try {
                putOnce(names[i], jo.opt(names[i]))
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    constructor(x: JSONTokener) : this() {
        var c: Char
        var key: String
        if (x.nextClean() != '{') throw x.syntaxError("A JSONObject text must begin with '{'")
        while (true) {
            c = x.nextClean()
            key = when (c) {
                0.toChar() -> throw x.syntaxError("A JSONObject text must end with '}'")
                '}' -> return
                else -> {
                    x.back()
                    x.nextValue().toString()
                }
            }
            c = x.nextClean()
            if (c != ':') throw x.syntaxError("Expected a ':' after a key")
            if (opt(key) != null) throw x.syntaxError("Duplicate key \"$key\"")
            val value = x.nextValue()
            // if (value != null)
            this.put(key, value)
            when (x.nextClean()) {
                ';', ',' -> {
                    if (x.nextClean() == '}') return
                    x.back()
                }
                '}' -> return
                else -> throw x.syntaxError("Expected a ',' or '}'")
            }
        }
    }

    constructor(m: LinkedHashMap<*, *>?) {
        if (m == null) {
            map = LinkedHashMap()
        } else {
            map = LinkedHashMap(m.size)
            for ((key, value) in m) {
                if (key == null) throw NullPointerException("Null key.")
                if (value != null) map[key.toString()] = wrap(value)
            }
        }
    }

    constructor(bean: Any) : this() {
        populateMap(bean)
    }

    constructor(o: Any, vararg names: String) : this(names.size) {
        val c = o.javaClass
        var i = 0
        while (i < names.size) {
            val name = names[i]
            try {
                putOpt(name, c.getField(name)[o])
            } catch (ignore: Exception) {
            }
            i += 1
        }
    }

    constructor(source: String) : this(JSONTokener(source))

    constructor(baseName: String, locale: Locale) : this() {
        val bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().contextClassLoader)
        val keys = bundle.keys
        for (key in keys) {
            if (key != null) {
                val path = key.split("\\.").toTypedArray()
                val last = path.size - 1
                var target = this
                var i = 0
                while (i < last) {
                    val segment = path[i]
                    var nextTarget = target.optJSONObject(segment)
                    if (nextTarget == null) {
                        nextTarget = JSONObject()
                        target.put(segment, nextTarget)
                    }
                    target = nextTarget
                    i += 1
                }
                target.put(path[last], bundle.getString(key))
            }
        }
    }

    constructor(initialCapacity: Int) {
        map = LinkedHashMap(initialCapacity)
    }

    @Throws(JSONException::class)
    fun accumulate(key: String, value: Any?): JSONObject {
        testValidity(value)
        when (val o = opt(key)) {
            null -> this.put(key, if (value is JSONArray) JSONArray().put(value) else value)
            is JSONArray -> o.put(value)
            else -> this.put(key, JSONArray().put(o).put(value))
        }
        return this
    }

    @Throws(JSONException::class)
    fun append(key: String, value: Any?): JSONObject {
        testValidity(value)
        when (val o = opt(key)) {
            null -> this.put(key, JSONArray().put(value))
            is JSONArray -> this.put(key, o.put(value))
            else -> throw wrongValueFormatException(key, "JSONArray", null, null)
        }
        return this
    }

    @Throws(JSONException::class)
    operator fun get(key: String): Any = opt(key) ?: throw JSONException("JSONObject[" + quote(key) + "] not found.")

    @Throws(JSONException::class)
    fun <E : Enum<E>?> getEnum(clazz: Class<E>, key: String): E = optEnum(clazz, key) ?: throw wrongValueFormatException(key, "enum of type " + quote(clazz.simpleName), null)

    @Throws(JSONException::class)
    fun getBoolean(key: String): Boolean {
        val o = this[key]
        if (o == java.lang.Boolean.FALSE || o is String && o.equals("false", ignoreCase = true)) {
            return false
        } else if (o == java.lang.Boolean.TRUE || o is String && o.equals("true", ignoreCase = true)) {
            return true
        }
        throw wrongValueFormatException(key, "Boolean", null)
    }

    @Throws(JSONException::class)
    fun getBigInteger(key: String): BigInteger {
        val o = this[key]
        return objectToBigInteger(o, null) ?: throw wrongValueFormatException(key, "BigInteger", o, null)
    }

    @Throws(JSONException::class)
    fun getBigDecimal(key: String): BigDecimal {
        val o = this[key]
        return objectToBigDecimal(o, null) ?: throw wrongValueFormatException(key, "BigDecimal", o, null)
    }

    @Throws(JSONException::class)
    fun getDouble(key: String): Double {
        val o = this[key]
        return if (o is Number) {
            o.toDouble()
        } else try {
            o.toString().toDouble()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "double", e)
        }
    }

    @Throws(JSONException::class)
    fun getFloat(key: String): Float {
        val o = this[key]
        return if (o is Number) {
            o.toFloat()
        } else try {
            o.toString().toFloat()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "float", e)
        }
    }

    @Throws(JSONException::class)
    fun getNumber(key: String): Number {
        val o = this[key]
        return try {
            if (o is Number) o else stringToNumber("$o")
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "number", e)
        }
    }

    @Throws(JSONException::class)
    fun getInt(key: String): Int {
        val o = this[key]
        return if (o is Number) {
            o.toInt()
        } else try {
            o.toString().toInt()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "int", e)
        }
    }

    @Throws(JSONException::class)
    fun getJSONArray(key: String): JSONArray {
        val o = this[key]
        return if (o is JSONArray) o else throw wrongValueFormatException(key, "JSONArray", null)
    }

    @Throws(JSONException::class)
    fun getJSONObject(key: String): JSONObject {
        val o = this[key]
        return if (o is JSONObject) o else throw wrongValueFormatException(key, "JSONObject", null)
    }

    @Throws(JSONException::class)
    fun getLong(key: String): Long {
        val o = this[key]
        return if (o is Number) {
            o.toLong()
        } else try {
            o.toString().toLong()
        } catch (e: Exception) {
            throw wrongValueFormatException(key, "long", e)
        }
    }

    @Throws(JSONException::class)
    fun getString(key: String): String {
        val o = this[key]
        return if (o is String) o else throw wrongValueFormatException(key, "string", null)
    }

    fun has(key: String?) = map.containsKey(key)

    @Throws(JSONException::class)
    fun increment(key: String): JSONObject {
        when (val value = opt(key)) {
            null -> this.put(key, 1)
            is Int -> this.put(key, value.toInt() + 1)
            is Long -> this.put(key, value.toLong() + 1L)
            is BigInteger -> this.put(key, value.add(BigInteger.ONE))
            is Float -> this.put(key, value.toFloat() + 1.0f)
            is Double -> this.put(key, value.toDouble() + 1.0)
            is BigDecimal -> this.put(key, value.add(BigDecimal.ONE))
            else -> throw JSONException("Unable to increment [" + quote(key) + "].")
        }
        return this
    }

    fun isNull(key: String) = opt(key) == NULL

    fun keys(): Iterator<String> = keySet().iterator()

    fun keySet(): Set<String> = map.keys

    fun entrySet(): Set<Map.Entry<String, Any?>> = map.entries

    fun length(): Int = map.size

    val isEmpty: Boolean get() = map.isEmpty()

    fun names(): JSONArray? = if (map.isEmpty()) null else JSONArray(map.keys as? Collection<*>)

    fun opt(key: String): Any? = map[key]

    fun <E : Enum<E>?> optEnum(clazz: Class<E>, key: String): E? = this.optEnum(clazz, key, null)

    fun <E : Enum<E>?> optEnum(clazz: Class<E>, key: String, defaultValue: E?): E? =
        try {
            @Suppress("UNCHECKED_CAST")
            when (val v = opt(key)) {
                NULL -> defaultValue
                clazz.isAssignableFrom(v!!.javaClass) -> v as? E
                else -> java.lang.Enum.valueOf(clazz, "$v")
            }
        } catch (e: Exception) {
            defaultValue
        }

    @JvmOverloads
    fun optBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val v = opt(key)
        if (v == NULL) return defaultValue
        return if (v is Boolean) {
            v
        } else try {
            getBoolean(key)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun optBigDecimal(key: String, defaultValue: BigDecimal?): BigDecimal? = objectToBigDecimal(opt(key), defaultValue)

    fun optBigInteger(key: String, defaultValue: BigInteger?): BigInteger? = objectToBigInteger(opt(key), defaultValue)

    @JvmOverloads
    fun optDouble(key: String, defaultValue: Double = Double.NaN): Double {
        val v = optNumber(key) ?: return defaultValue
        return v.toDouble()
    }

    @JvmOverloads
    fun optFloat(key: String, defaultValue: Float = Float.NaN): Float {
        val v = optNumber(key) ?: return defaultValue
        return v.toFloat()
    }

    @JvmOverloads
    fun optInt(key: String, defaultValue: Int = 0): Int {
        val v = optNumber(key, null) ?: return defaultValue
        return v.toInt()
    }

    fun optJSONArray(key: String): JSONArray? {
        val o = opt(key)
        return if (o is JSONArray) o else null
    }

    fun optJSONObject(key: String): JSONObject? {
        val o = opt(key)
        return if (o is JSONObject) o else null
    }

    @JvmOverloads
    fun optLong(key: String, defaultValue: Long = 0): Long {
        val v = optNumber(key, null) ?: return defaultValue
        return v.toLong()
    }

    @JvmOverloads
    fun optNumber(key: String, defaultValue: Number? = null): Number? {
        val v = opt(key)
        if (v == NULL) return defaultValue
        return if (v is Number) {
            v
        } else try {
            stringToNumber(v.toString())
        } catch (e: Exception) {
            defaultValue
        }
    }

    @JvmOverloads
    fun optString(key: String, defaultValue: String = ""): String {
        val o = opt(key)
        return if (NULL == o) defaultValue else o.toString()
    }

    private fun populateMap(bean: Any) {
        val klass: Class<*> = bean.javaClass
        val includeSuperClass = klass.classLoader != null
        val methods = if (includeSuperClass) klass.methods else klass.declaredMethods
        for (method in methods) {
            val modifiers = method.modifiers
            if (Modifier.isPublic(modifiers)
                && !Modifier.isStatic(modifiers)
                && method.parameterTypes.isEmpty() && !method.isBridge
                && method.returnType != Void.TYPE && isValidMethodName(method.name)
            ) {
                val key = getKeyNameFromMethod(method)
                if (key != null && key.isNotEmpty()) {
                    try {
                        val result = method.invoke(bean)
                        if (result != null) {
                            map[key] = wrap(result)
                            if (result is Closeable) {
                                try {
                                    result.close()
                                } catch (ignore: IOException) {
                                }
                            }
                        }
                    } catch (ignore: IllegalAccessException) {
                    } catch (ignore: IllegalArgumentException) {
                    } catch (ignore: InvocationTargetException) {
                    }
                }
            }
        }
    }

    @Throws(JSONException::class)
    fun put(key: String, value: Boolean): JSONObject = this.put(key, if (value) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE)

    @Throws(JSONException::class)
    fun put(key: String, value: Collection<*>?): JSONObject = this.put(key, JSONArray(value))

    @Throws(JSONException::class)
    fun put(key: String, value: Double): JSONObject = this.put(key, java.lang.Double.valueOf(value))

    @Throws(JSONException::class)
    fun put(key: String, value: Float): JSONObject = this.put(key, java.lang.Float.valueOf(value))

    @Throws(JSONException::class)
    fun put(key: String, value: Int): JSONObject = this.put(key, Integer.valueOf(value))

    @Throws(JSONException::class)
    fun put(key: String, value: Long): JSONObject = this.put(key, java.lang.Long.valueOf(value))

    @Throws(JSONException::class)
    fun put(key: String, value: LinkedHashMap<*, *>?): JSONObject = this.put(key, JSONObject(value))

    @Throws(JSONException::class)
    fun put(key: String, value: Any?): JSONObject {
        if (value != null) {
            testValidity(value)
            map[key] = value
        } else {
            this.remove(key)
        }
        return this
    }

    @Throws(JSONException::class)
    fun putOnce(key: String?, value: Any?): JSONObject {
        if (key != null && value != null) {
            if (opt(key) != null) throw JSONException("Duplicate key \"$key\"")
            return this.put(key, value)
        }
        return this
    }

    @Throws(JSONException::class)
    fun putOpt(key: String?, value: Any?): JSONObject = if (key != null && value != null) this.put(key, value) else this

    fun query(jsonPointer: String): Any? = query(JSONPointer(jsonPointer))

    fun query(jsonPointer: JSONPointer): Any? = jsonPointer.queryFrom(this)

    fun optQuery(jsonPointer: String): Any? = optQuery(JSONPointer(jsonPointer))

    fun optQuery(jsonPointer: JSONPointer): Any? =
        try {
            jsonPointer.queryFrom(this)
        } catch (e: JSONPointerException) {
            null
        }

    fun remove(key: String?): Any? = map.remove(key)

    fun similar(other: Any?): Boolean {
        return try {
            if (other !is JSONObject) return false
            if (keySet() != other.keySet()) return false
            for ((name, valueThis) in entrySet()) {
                val valueOther = other[name]
                if (valueThis === valueOther) continue
                if (valueThis == null) return false
                if (valueThis is JSONObject) {
                    if (!valueThis.similar(valueOther)) return false
                } else if (valueThis is JSONArray) {
                    if (!valueThis.similar(valueOther)) return false
                } else if (valueThis != valueOther) {
                    return false
                }
            }
            true
        } catch (exception: Throwable) {
            false
        }
    }

    @Throws(JSONException::class)
    fun toJSONArray(names: JSONArray?): JSONArray? {
        if (names == null || names.isEmpty) return null
        val ja = JSONArray()
        var i = 0
        while (i < names.length()) {
            ja.put(opt(names.getString(i)))
            i += 1
        }
        return ja
    }

    override fun toString(): String = try { this.toString(0) } catch (e: Exception) { "null" }

    @Throws(JSONException::class)
    fun toString(indentFactor: Int): String {
        val w = StringWriter()
        return synchronized(w.buffer) { write(w, indentFactor, 0).toString() }
    }

    @JvmOverloads
    @Throws(JSONException::class)
    fun write(writer: Writer, indentFactor: Int = 0, indent: Int = 0): Writer {
        return try {
            var needsComma = false
            val length = length()
            writer.write('{'.code)
            if (length == 1) {
                val entry = entrySet().iterator().next()
                val key = entry.key
                writer.write(quote(key))
                writer.write(':'.code)
                if (indentFactor > 0) writer.write(' '.code)
                try {
                    writeValue(writer, entry.value, indentFactor, indent)
                } catch (e: Exception) {
                    throw JSONException("Unable to write JSONObject value for key: $key", e)
                }
            } else if (length != 0) {
                val newIndent = indent + indentFactor
                for ((key, value) in entrySet()) {
                    if (needsComma) writer.write(','.code)
                    if (indentFactor > 0) writer.write('\n'.code)
                    indent(writer, newIndent)
                    writer.write(quote(key))
                    writer.write(':'.code)
                    if (indentFactor > 0) writer.write(' '.code)
                    try {
                        writeValue(writer, value, indentFactor, newIndent)
                    } catch (e: Exception) {
                        throw JSONException("Unable to write JSONObject value for key: $key", e)
                    }
                    needsComma = true
                }
                if (indentFactor > 0) writer.write('\n'.code)
                indent(writer, indent)
            }
            writer.write('}'.code)
            writer
        } catch (exception: IOException) {
            throw JSONException(exception)
        }
    }

    fun toMap(): LinkedHashMap<String, Any?> {
        val results = linkedMapOf<String, Any?>()
        for ((key, value1) in entrySet()) {
            val value: Any? = when (value1) {
                null, NULL -> null
                is JSONObject -> value1.toMap()
                is JSONArray -> value1.toList()
                else -> value1
            }
            results[key] = value
        }
        return results
    }

    companion object {

        val numberPattern: Pattern = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")
        val NULL: Any = Null()

        fun doubleToString(d: Double): String {
            if (d.isInfinite() || d.isNaN()) return "null"
            var string = "$d"
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) string = string.substring(0, string.length - 1)
            }
            return string
        }

        fun getNames(jo: JSONObject): Array<String>? = if (jo.isEmpty) null else jo.keySet().toTypedArray()

        fun getNames(o: Any?): Array<String?>? {
            if (o == null) return null
            val klass: Class<*> = o.javaClass
            val fields = klass.fields
            val length = fields.size
            if (length == 0) return null
            val names = arrayOfNulls<String>(length)
            var i = 0
            while (i < length) {
                names[i] = fields[i].name
                i += 1
            }
            return names
        }

        @Throws(JSONException::class)
        fun numberToString(number: Number): String {
            testValidity(number)
            var string = number.toString()
            if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
                while (string.endsWith("0")) {
                    string = string.substring(0, string.length - 1)
                }
                if (string.endsWith(".")) string = string.substring(0, string.length - 1)
            }
            return string
        }

        fun objectToBigDecimal(v: Any?, defaultValue: BigDecimal?): BigDecimal? = when (v) {
            NULL -> defaultValue
            is BigDecimal -> v
            is BigInteger -> BigDecimal(v)
            is Double, is Float -> {
                val d = (v as Number).toDouble()
                if (d.isNaN()) defaultValue else BigDecimal(d)
            }
            is Long, is Int, is Short, is Byte -> BigDecimal((v as Number).toLong())
            else -> try { BigDecimal("$v") } catch (e: Exception) { defaultValue }
        }

        fun objectToBigInteger(v: Any?, defaultValue: BigInteger?): BigInteger? = when (v) {
            NULL -> defaultValue
            is BigInteger -> v
            is BigDecimal -> v.toBigInteger()
            is Double, is Float -> {
                val d = (v as Number).toDouble()
                if (d.isNaN()) defaultValue else BigDecimal(d).toBigInteger()
            }
            is Long, is Int, is Short, is Byte -> BigInteger.valueOf((v as Number).toLong())
            else -> try {
                val valStr = "$v"
                if (isDecimalNotation(valStr)) BigDecimal(valStr).toBigInteger() else BigInteger(valStr)
            } catch (e: Exception) {
                defaultValue
            }
        }

        private fun isValidMethodName(name: String): Boolean = "getClass" != name && "getDeclaringClass" != name

        private fun getKeyNameFromMethod(method: Method): String? {
            val ignoreDepth = getAnnotationDepth(method, JSONPropertyIgnore::class.java)
            if (ignoreDepth > 0) {
                val forcedNameDepth = getAnnotationDepth(method, JSONPropertyName::class.java)
                if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) return null
            }
            val annotation = getAnnotation(method, JSONPropertyName::class.java)
            if (annotation?.value != null && annotation.value.isNotEmpty()) return annotation.value
            val name = method.name
            var key = when {
                name.startsWith("get") && name.length > 3 -> name.substring(3)
                name.startsWith("is") && name.length > 2 -> name.substring(2)
                else -> return null
            }
            if (Character.isLowerCase(key[0])) return null
            if (key.length == 1) {
                key = key.lowercase(Locale.ROOT)
            } else if (!Character.isUpperCase(key[1])) {
                key = key.substring(0, 1).lowercase(Locale.ROOT) + key.substring(1)
            }
            return key
        }

        private fun <A : Annotation?> getAnnotation(m: Method?, annotationClass: Class<A>?): A? {
            if (m == null || annotationClass == null) return null
            if (m.isAnnotationPresent(annotationClass)) return m.getAnnotation(annotationClass)
            val c = m.declaringClass
            if (c.superclass == null) return null
            for (i in c.interfaces) {
                return try {
                    val im = i.getMethod(m.name, *m.parameterTypes)
                    getAnnotation(im, annotationClass)
                } catch (ex: SecurityException) {
                    continue
                } catch (ex: NoSuchMethodException) {
                    continue
                }
            }
            return try {
                getAnnotation(c.superclass.getMethod(m.name, *m.parameterTypes), annotationClass)
            } catch (ex: SecurityException) {
                null
            } catch (ex: NoSuchMethodException) {
                null
            }
        }

        private fun getAnnotationDepth(m: Method?, annotationClass: Class<out Annotation>?): Int {
            if (m == null || annotationClass == null) return -1
            if (m.isAnnotationPresent(annotationClass)) return 1
            val c = m.declaringClass
            if (c.superclass == null) return -1
            for (i in c.interfaces) {
                try {
                    val im = i.getMethod(m.name, *m.parameterTypes)
                    val d = getAnnotationDepth(im, annotationClass)
                    if (d > 0) return d + 1
                } catch (ex: SecurityException) {
                    continue
                } catch (ex: NoSuchMethodException) {
                    continue
                }
            }
            return try {
                val d = getAnnotationDepth(c.superclass.getMethod(m.name, *m.parameterTypes), annotationClass)
                if (d > 0) d + 1 else -1
            } catch (ex: SecurityException) {
                -1
            } catch (ex: NoSuchMethodException) {
                -1
            }
        }

        fun quote(string: String?): String {
            val sw = StringWriter()
            return synchronized(sw.buffer) {
                try {
                    quote(string, sw).toString()
                } catch (ignored: IOException) {
                    ""
                }
            }
        }

        @Throws(IOException::class)
        fun quote(string: String?, w: Writer): Writer {
            if (string == null || string.isEmpty()) {
                w.write("\"\"")
                return w
            }
            var b: Char
            var c = 0.toChar()
            var hhhh: String
            var i = 0
            val len = string.length
            w.write('"'.code)
            while (i < len) {
                b = c
                c = string[i]
                when (c) {
                    '\\', '"' -> {
                        w.write('\\'.code)
                        w.write(c.code)
                    }
                    '/' -> {
                        if (b == '<') w.write('\\'.code)
                        w.write(c.code)
                    }
                    '\b' -> w.write("\\b")
                    '\t' -> w.write("\\t")
                    '\n' -> w.write("\\n")
                    '\u000C' -> w.write("\\f")
                    '\r' -> w.write("\\r")
                    else -> if (c < ' ' || c in '\u0080' until '\u00a0' || c in '\u2000' until '\u2100') {
                        w.write("\\u")
                        hhhh = Integer.toHexString(c.code)
                        w.write("0000", 0, 4 - hhhh.length)
                        w.write(hhhh)
                    } else {
                        w.write(c.code)
                    }
                }
                i += 1
            }
            w.write('"'.code)
            return w
        }

        fun isDecimalNotation(v: String): Boolean = v.indexOf('.') > -1 || v.indexOf('e') > -1 || v.indexOf('E') > -1 || "-0" == v

        @Throws(NumberFormatException::class)
        fun stringToNumber(v: String): Number {
            val initial = v[0]
            if (initial in '0'..'9' || initial == '-') {
                if (isDecimalNotation(v)) {
                    return try {
                        val bd = BigDecimal(v)
                        if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) -0.0 else bd
                    } catch (retryAsDouble: NumberFormatException) {
                        try {
                            v.toDouble().apply { if (isNaN() || isInfinite()) throw NumberFormatException("val [$this] is not a valid number.") }
                        } catch (ignore: NumberFormatException) {
                            throw NumberFormatException("val [$v] is not a valid number.")
                        }
                    }
                }
                if (initial == '0' && v.length > 1) {
                    val at1 = v[1]
                    if (at1 in '0'..'9') throw NumberFormatException("val [$v] is not a valid number.")
                } else if (initial == '-' && v.length > 2) {
                    val at1 = v[1]
                    val at2 = v[2]
                    if (at1 == '0' && at2 >= '0' && at2 <= '9') throw NumberFormatException("val [$v] is not a valid number.")
                }
                val bi = BigInteger(v)
                if (bi.bitLength() <= 31) return Integer.valueOf(bi.toInt())
                return if (bi.bitLength() <= 63) bi.toLong() else bi
            }
            throw NumberFormatException("val [$v] is not a valid number.")
        }

        fun stringToValue(string: String): Any {
            if ("" == string) return string
            if ("true".equals(string, ignoreCase = true)) return java.lang.Boolean.TRUE
            if ("false".equals(string, ignoreCase = true)) return java.lang.Boolean.FALSE
            if ("null".equals(string, ignoreCase = true)) return NULL
            val initial = string[0]
            if (initial in '0'..'9' || initial == '-') {
                try {
                    return stringToNumber(string)
                } catch (ignore: Exception) {
                }
            }
            return string
        }

        @Throws(JSONException::class)
        fun testValidity(o: Any?) {
            if (o != null) {
                if (o is Double) {
                    if (o.isInfinite() || o.isNaN()) throw JSONException("JSON does not allow non-finite numbers.")
                } else if (o is Float) {
                    if (o.isInfinite() || o.isNaN()) throw JSONException("JSON does not allow non-finite numbers.")
                }
            }
        }

        @Throws(JSONException::class)
        fun valueToString(value: Any?): String = JSONWriter.valueToString(value)

        fun wrap(o: Any?): Any? = try {
            when (o) {
                null -> NULL
                is JSONObject, is JSONArray, NULL, is JSONString,
                is Byte, is Char, is Short, is Int, is Long, is Boolean, is Float, is Double,
                is String, is BigInteger, is BigDecimal, is Enum<*> -> o
                is Collection<*> -> JSONArray(o as? Collection<*>)
                o.javaClass.isArray -> JSONArray(o)
                is LinkedHashMap<*, *> -> JSONObject(o as? LinkedHashMap<*, *>)
                else -> {
                    val objectPackage = o.javaClass.getPackage()
                    val objectPackageName = if (objectPackage != null) objectPackage.name else ""
                    if (objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || o.javaClass.classLoader == null) "$o" else JSONObject(o)
                }
            }
        } catch (exception: Exception) {
            null
        }


        @Throws(JSONException::class, IOException::class)
        fun writeValue(writer: Writer, value: Any?, indentFactor: Int, indent: Int): Writer {
            when (value) {
                null -> writer.write("null")
                is JSONString -> {
                    val o: Any? = try {
                        value.toJSONString()
                    } catch (e: Exception) {
                        throw JSONException(e)
                    }
                    writer.write(o?.toString() ?: quote(value.toString()))
                }
                is Number -> {
                    val numberAsString = numberToString(value)
                    if (numberPattern.matcher(numberAsString).matches()) {
                        writer.write(numberAsString)
                    } else {
                        quote(numberAsString, writer)
                    }
                }
                is Boolean -> writer.write(value.toString())
                is Enum<*> -> writer.write(quote(value.name))
                is JSONObject -> value.write(writer, indentFactor, indent)
                is JSONArray -> value.write(writer, indentFactor, indent)
                is LinkedHashMap<*, *> -> JSONObject(value as? LinkedHashMap<*, *>).write(writer, indentFactor, indent)
                is Collection<*> -> JSONArray(value as? Collection<*>).write(writer, indentFactor, indent)
                value.javaClass.isArray -> JSONArray(value).write(writer, indentFactor, indent)
                else -> quote("$value", writer)
            }
            return writer
        }

        @Throws(IOException::class)
        fun indent(writer: Writer, indent: Int) {
            var i = 0
            while (i < indent) {
                writer.write(' '.code)
                i++
            }
        }

        private fun wrongValueFormatException(key: String, valueType: String, cause: Throwable?) = JSONException("JSONObject[${quote(key)}] is not a $valueType.", cause)
        private fun wrongValueFormatException(key: String, valueType: String, value: Any?, cause: Throwable?) = JSONException("JSONObject[${quote(key)}] is not a $valueType ($value).", cause)

    }
}