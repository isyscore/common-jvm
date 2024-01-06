package com.isyscore.kotlin.common.json

class JSONPointerException : JSONException {

    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

}