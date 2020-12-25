package com.isyscore.kotlin.common.json

import java.io.*

open class JSONTokener(reader: Reader) {

    private var character = 1L
    private var eof = false
    private var index = 0L
    private var line = 1L
    private var previous = 0.toChar()
    private val reader: Reader = if (reader.markSupported()) reader else BufferedReader(reader)
    private var usePrevious = false
    private var characterPreviousLine = 0L

    constructor(inputStream: InputStream) : this(InputStreamReader(inputStream))
    constructor(s: String) : this(StringReader(s))

    @Throws(JSONException::class)
    fun back() {
        if (usePrevious || index <= 0) throw JSONException("Stepping back two steps is not supported")
        decrementIndexes()
        usePrevious = true
        eof = false
    }

    private fun decrementIndexes() {
        index--
        if (previous == '\r' || previous == '\n') {
            line--
            character = characterPreviousLine
        } else if (character > 0) {
            character--
        }
    }

    fun end(): Boolean = eof && !usePrevious

    @Throws(JSONException::class)
    fun more(): Boolean {
        if (usePrevious) return true
        try {
            reader.mark(1)
        } catch (e: IOException) {
            throw JSONException("Unable to preserve stream position", e)
        }
        try {
            if (reader.read() <= 0) {
                eof = true
                return false
            }
            reader.reset()
        } catch (e: IOException) {
            throw JSONException("Unable to read the next character from the stream", e)
        }
        return true
    }

    @Throws(JSONException::class)
    operator fun next(): Char {
        val c = if (usePrevious) {
            usePrevious = false
            previous.toInt()
        } else {
            try {
                reader.read()
            } catch (exception: IOException) {
                throw JSONException(exception)
            }
        }
        if (c <= 0) {
            eof = true
            return 0.toChar()
        }
        incrementIndexes(c)
        previous = c.toChar()
        return previous
    }

    private fun incrementIndexes(c: Int) {
        if (c > 0) {
            index++
            when (c) {
                '\r'.toInt() -> {
                    line++
                    characterPreviousLine = character
                    character = 0
                }
                '\n'.toInt() -> {
                    if (previous != '\r') {
                        line++
                        characterPreviousLine = character
                    }
                    character = 0
                }
                else -> character++
            }
        }
    }

    @Throws(JSONException::class)
    fun next(c: Char): Char {
        val n = this.next()
        if (n != c) {
            if (n.toInt() > 0) throw this.syntaxError("Expected '$c' and instead saw '$n'")
            throw this.syntaxError("Expected '$c' and instead saw ''")
        }
        return n
    }

    @Throws(JSONException::class)
    fun next(n: Int): String {
        if (n == 0) return ""
        val chars = CharArray(n)
        var pos = 0
        while (pos < n) {
            chars[pos] = this.next()
            if (end()) throw this.syntaxError("Substring bounds error")
            pos += 1
        }
        return String(chars)
    }

    @Throws(JSONException::class)
    fun nextClean(): Char {
        while (true) {
            val c = this.next()
            if (c.toInt() == 0 || c > ' ') return c
        }
    }

    @Throws(JSONException::class)
    fun nextString(quote: Char): String {
        var c: Char
        val sb = StringBuilder()
        while (true) {
            c = this.next()
            when (c) {
                0.toChar(), '\n', '\r' -> throw this.syntaxError("Unterminated string")
                '\\' -> {
                    c = this.next()
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        'f' -> sb.append('\u000C')
                        'r' -> sb.append('\r')
                        'u' -> try {
                            sb.append(this.next(4).toInt(16).toChar())
                        } catch (e: NumberFormatException) {
                            throw this.syntaxError("Illegal escape.", e)
                        }
                        '"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw this.syntaxError("Illegal escape.")
                    }
                }
                else -> {
                    if (c == quote) return sb.toString()
                    sb.append(c)
                }
            }
        }
    }

    @Throws(JSONException::class)
    fun nextTo(delimiter: Char): String {
        val sb = StringBuilder()
        while (true) {
            val c = this.next()
            if (c == delimiter || c.toInt() == 0 || c == '\n' || c == '\r') {
                if (c.toInt() != 0) back()
                return sb.toString().trim { it <= ' ' }
            }
            sb.append(c)
        }
    }

    @Throws(JSONException::class)
    fun nextTo(delimiters: String): String {
        var c: Char
        val sb = StringBuilder()
        while (true) {
            c = this.next()
            if (delimiters.indexOf(c) >= 0 || c.toInt() == 0 || c == '\n' || c == '\r') {
                if (c.toInt() != 0) back()
                return sb.toString().trim { it <= ' ' }
            }
            sb.append(c)
        }
    }

    @Throws(JSONException::class)
    fun nextValue(): Any? {
        var c = nextClean()
        val string: String
        when (c) {
            '"', '\'' -> return nextString(c)
            '{' -> {
                back()
                return JSONObject(this)
            }
            '[' -> {
                back()
                return JSONArray(this)
            }
        }
        val sb = StringBuilder()
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c)
            c = this.next()
        }
        if (!eof) back()
        string = sb.toString().trim { it <= ' ' }
        if (string == "") throw this.syntaxError("Missing value")
        return JSONObject.stringToValue(string)
    }

    @Throws(JSONException::class)
    fun skipTo(to: Char): Char {
        var c: Char
        try {
            val startIndex = index
            val startCharacter = character
            val startLine = line
            reader.mark(1000000)
            do {
                c = this.next()
                if (c.toInt() == 0) {
                    reader.reset()
                    index = startIndex
                    character = startCharacter
                    line = startLine
                    return 0.toChar()
                }
            } while (c != to)
            reader.mark(1)
        } catch (exception: IOException) {
            throw JSONException(exception)
        }
        back()
        return c
    }

    fun syntaxError(message: String) = JSONException(message + this.toString())
    fun syntaxError(message: String, causedBy: Throwable?) = JSONException(message + this.toString(), causedBy)

    override fun toString() = " at $index [character $character line $line]"

    companion object {
        fun dehexchar(c: Char): Int = when (c) {
            in '0'..'9' -> c - '0'
            in 'A'..'F' -> c.toInt() - ('A'.toInt() - 10)
            in 'a'..'f' -> c.toInt() - ('a'.toInt() - 10)
            else -> -1
        }
    }


}