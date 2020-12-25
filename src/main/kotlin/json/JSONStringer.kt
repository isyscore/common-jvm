package com.isyscore.kotlin.common.json

import java.io.StringWriter

class JSONStringer : JSONWriter(StringWriter()) {
    override fun toString(): String {
        return if (mode == 'd') writer.toString() else "null"
    }
}