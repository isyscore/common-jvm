@file:Suppress("DuplicatedCode", "PropertyName", "SpellCheckingInspection", "unused")

package com.isyscore.kotlin.common

import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.Proxy
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

enum class HttpMethod { GET, POST, PUT, DELETE }

val globalHttpHeaders = mutableMapOf<String, String>()

class HttpUtils {
    var url = ""
    var method = HttpMethod.GET
    var mimeType = "text/json"
    var data = ""
    var getParam = ""
    var postParam = mutableMapOf<String, String>()
    var fileParam = mutableMapOf<String, String>()
    var headers = mutableMapOf<String, String>()
    var cookies = mutableMapOf<String, String>()
    var followRedirects = true
    var connectTimeout = 10000L // 10sec
    var callTimeout = 10000L // 10sec
    var readTimeout = 10000L // 10sec
    var writeTimeout = 10000L // 10sec
    var proxy: Proxy? = null
    var authenticatorUser: String? = null
    var authenticatorPassword: String? = null
    var isHttps = true
    var isReturnBytes = false

    internal var _success: (Int, String?, headers: Map<String, String>, cookies: Set<Cookie>) -> Unit = { _, _, _, _ -> }
    internal var _successBytes: (Int, ByteArray?, headers: Map<String, String>, cookies: Set<Cookie>) -> Unit = { _, _, _, _ -> }
    internal var _fail: (Throwable?) -> Unit = {}

    fun onSuccessBytes(onSuccessBytes: (code: Int, bytes: ByteArray?, headers: Map<String, String>, cookie: Set<Cookie>) -> Unit) {
        _successBytes = onSuccessBytes
    }

    fun onSuccess(onSuccess: (code: Int, text: String?, headers: Map<String, String>, cookie: Set<Cookie>) -> Unit) {
        _success = onSuccess
    }

    fun onFail(onFail: (error: Throwable?) -> Unit) {
        _fail = onFail
    }
}

fun http(init: HttpUtils.() -> Unit): String? {
    val h = HttpUtils().apply { init() }
    val req = HttpOperations.buildRequest(h)
    return HttpOperations.executeForResult(req, h)
}

data class HttpResponse(
    var code: Int = -1,
    var header: Map<String, String> = mapOf(),
    var body: String = "",
    var cookie: Set<Cookie>? = setOf(),
    var error: Throwable? = null
)

fun httpGet(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.GET
        getParam = paramMap?.map { "${it.key}=${it.value}" }?.joinToString("&") ?: ""

        if (header != null) headers.putAll(header)

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpGetForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): T? {
    val resp = httpGet(u, header, paramMap, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

fun httpPost(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, body: Any? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.POST
        if (paramMap != null) postParam.putAll(paramMap)
        if (header != null) headers.putAll(header)
        mimeType = "application/json"
        data = body?.toJson() ?: "null"

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpPostForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, body: Any? = null, timeout: Long = 10000L): T? {
    val resp = httpPost(u, header, paramMap, body, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

fun httpPostForm(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.POST
        if (paramMap != null) postParam.putAll(paramMap)
        if (header != null) headers.putAll(header)

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpPostFormForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): T? {
    val resp = httpPostForm(u, header, paramMap, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

fun httpPut(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, body: Any? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.PUT
        if (paramMap != null) postParam.putAll(paramMap)
        if (header != null) headers.putAll(header)
        mimeType = "application/json"
        data = body?.toJson() ?: "null"

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpPutForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, body: Any? = null, timeout: Long = 10000L): T? {
    val resp = httpPut(u, header, paramMap, body, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

fun httpPutForm(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.PUT
        if (paramMap != null) postParam.putAll(paramMap)
        if (header != null) headers.putAll(header)

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpPutFormForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): T? {
    val resp = httpPutForm(u, header, paramMap, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

fun httpDelete(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): HttpResponse {
    val resp = HttpResponse()
    http {
        url = u
        method = HttpMethod.DELETE
        getParam = paramMap?.map { "${it.key}=${it.value}" }?.joinToString("&") ?: ""
        if (header != null) headers.putAll(header)

        connectTimeout = timeout
        readTimeout = timeout
        writeTimeout = timeout
        callTimeout = timeout

        onSuccess { code, text, headers, cookie ->
            resp.code = code
            resp.body = text ?: ""
            resp.header = headers
            resp.cookie = cookie
        }
        onFail {
            resp.error = it
        }
    }
    return resp
}

inline fun <reified T> httpDeleteForObj(u: String, header: MutableMap<String, String>? = null, paramMap: MutableMap<String, String>? = null, timeout: Long = 10000L): T? {
    val resp = httpDelete(u, header, paramMap, timeout)
    return if (resp.code == 200) try {
        resp.body.toObj<T>()
    } catch (e: Exception) {
        null
    } else null
}

private object HttpOperations {

    private fun Request.Builder.headers(util: HttpUtils, map: MutableMap<String, String>): Request.Builder {
        val authMap = mutableMapOf<String, String>().apply {
            if (util.authenticatorUser != null && util.authenticatorPassword != null) {
                this["Authorization"] = Credentials.basic(util.authenticatorUser!!, util.authenticatorPassword!!)
            }
        }
        val cookieMap = if (util.cookies.isNotEmpty()) {
            mutableMapOf<String, String>().apply {
                this["Cookie"] = util.cookies.map { "${it.key}=${it.value}" }.joinToString("; ")
            }
        } else {
            mutableMapOf()
        }
        return this.headers((globalHttpHeaders + map + authMap + cookieMap).toHeaders())
    }

    fun buildRequest(util: HttpUtils) = when (util.method) {
        HttpMethod.GET -> Request.Builder()
            .url(util.url + if (util.getParam != "") "?${util.getParam}" else "")
            .headers(util, util.headers)
            .get().build()

        HttpMethod.POST -> Request.Builder()
            .url(util.url + if (util.getParam != "") "?${util.getParam}" else "")
            .headers(util, util.headers)
            .post(
                if (util.data != "") {
                    buildDataBody(util.mimeType, util.data)
                } else {
                    if (util.fileParam.isEmpty()) {
                        buildBody(util.postParam)
                    } else {
                        buildPostFileParts(util.postParam, util.fileParam)
                    }
                }
            ).build()

        HttpMethod.PUT -> Request.Builder()
            .url(util.url + if (util.getParam != "") "?${util.getParam}" else "")
            .headers(util, util.headers)
            .put(
                if (util.data != "") {
                    buildDataBody(util.mimeType, util.data)
                } else {
                    if (util.fileParam.isEmpty()) {
                        buildBody(util.postParam)
                    } else {
                        buildPostFileParts(util.postParam, util.fileParam)
                    }
                }
            )
            .build()

        HttpMethod.DELETE -> Request.Builder()
            .url(util.url + if (util.getParam != "") "?${util.getParam}" else "")
            .headers(util, util.headers)
            .delete(buildBody(util.postParam))
            .build()

    }

    fun buildPostFileParts(params: Map<String, String>, files: Map<String, String>) = MultipartBody.Builder().apply {
        setType(MultipartBody.FORM)
        params.forEach { (t, u) -> addFormDataPart(t, u) }
        files.forEach { (t, u) -> addFormDataPart(t, u.substringAfterLast("/"), File(u).asRequestBody("application/octet-stream".toMediaTypeOrNull())) }
    }.build()

    fun buildDataBody(type: String, data: String) = data.toRequestBody(type.toMediaTypeOrNull())

    fun buildBody(params: Map<String, String>) = FormBody.Builder().apply {
        params.forEach { (t, u) -> add(t, u) }
    }.build()

    fun executeForResult(req: Request, util: HttpUtils): String? {
        val builder = OkHttpClient.Builder()
            .connectTimeout(util.connectTimeout, TimeUnit.MILLISECONDS)
            .callTimeout(util.callTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(util.readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(util.writeTimeout, TimeUnit.MILLISECONDS)
            .followRedirects(util.followRedirects)
            .followSslRedirects(util.followRedirects)
            .proxy(util.proxy)

        if (util.isHttps) {
            val trust = object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            }
            builder.sslSocketFactory(SSLContext.getInstance("SSL").apply { init(null, arrayOf(trust), SecureRandom()) }.socketFactory, trust).hostnameVerifier { _, _ -> true }
        }
        val http = builder.build()
        val call = http.newCall(req)
        var ret: String? = null
        try {
            val resp = call.execute()
            val headers = (resp.headers + (resp.networkResponse?.headers ?: Headers.headersOf()) + (resp.priorResponse?.headers ?: Headers.headersOf())).toSet().toMap()
            val cookies = (resp.headers.toCookies(req) + (resp.networkResponse?.headers?.toCookies(req) ?: listOf()) + (resp.priorResponse?.headers?.toCookies(req) ?: listOf())).toSet()
            if (resp.isSuccessful || resp.isRedirect) {
                if (util.isReturnBytes) {
                    val retBytes = resp.body?.bytes()
                    if (retBytes != null) ret = String(retBytes)
                    util._successBytes(resp.code, retBytes, headers, cookies)
                } else {
                    ret = resp.body?.string()
                    util._success(resp.code, ret, headers, cookies)
                }
            } else {
                util._success(resp.code, null, headers, cookies)
            }
            try {
                resp.body?.close()
            } catch (_: Exception) {

            }
        } catch (e: Throwable) {
            util._fail(e)
        }
        return ret
    }
}

fun Headers.toCookies(req: Request) = Cookie.parseAll(req.url, this)