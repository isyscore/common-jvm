package com.isyscore.kotlin.common.json

open class JSONException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable) : super(cause.message, cause)

    companion object {
        private const val serialVersionUID: Long = 0
    }
}