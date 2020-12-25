package com.isyscore.kotlin.common.json

class JSONPointerException : JSONException {

    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    companion object {
        private const val serialVersionUID = 8872944667561856751L
    }
}