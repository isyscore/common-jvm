@file:Suppress("DuplicatedCode", "PropertyName", "SpellCheckingInspection", "unused")

package com.isyscore.kotlin.common

import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.io.File
import java.net.Proxy
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

val globalHttpSSEHeaders = mutableMapOf(
    "Content-Type" to "text/event-stream"
)

class HttpSSEUtils {

    var url = ""
    var method = HttpMethod.GET
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

    internal var _streamEvent: (text: String) -> String = { it }
    internal var _streamComplete: (list: List<String>) -> String = { list ->
        list.filter { it != "" }.joinToString("")
    }
    internal var _success: (String) -> Unit = { _ -> }

    internal var _fail: (Throwable?) -> Unit = {}

    fun onStreamEvent(evt: (text: String) -> String) {
        _streamEvent = evt
    }

    fun onStreamComplete(evt: (list: List<String>) -> String) {
        _streamComplete = evt
    }

    fun onSuccess(onSuccess: (ctext: String) -> Unit) {
        _success = onSuccess
    }

    fun onFail(onFail: (error: Throwable?) -> Unit) {
        _fail = onFail
    }
}

fun httpSSE(init: HttpSSEUtils.() -> Unit) {
    val h = HttpSSEUtils().apply { init() }
    val op = HttpSSEOperations()
    val req = op.buildRequest(h)
    op.executeForResult(req, h)
}

private class HttpSSEOperations {

    @Volatile
    private var inProgress = false

    private fun Request.Builder.headers(util: HttpSSEUtils, map: MutableMap<String, String>): Request.Builder {
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
        return this.headers((globalHttpSSEHeaders + map + authMap + cookieMap).toHeaders())
    }

    fun buildRequest(util: HttpSSEUtils) = when (util.method) {
        HttpMethod.GET -> Request.Builder().url(util.url + if (util.getParam != "") "?${util.getParam}" else "").headers(util, util.headers).get().build()

        HttpMethod.POST -> Request.Builder().url(util.url + if (util.getParam != "") "?${util.getParam}" else "").headers(util, util.headers).post(
            if (util.data != "") {
                buildDataBody("application/json", util.data)
            } else {
                if (util.fileParam.isEmpty()) {
                    buildBody(util.postParam)
                } else {
                    buildPostFileParts(util.postParam, util.fileParam)
                }
            }
        ).build()

        HttpMethod.PUT -> Request.Builder().url(util.url + if (util.getParam != "") "?${util.getParam}" else "").headers(util, util.headers).put(
            if (util.data != "") {
                buildDataBody("application/json", util.data)
            } else {
                if (util.fileParam.isEmpty()) {
                    buildBody(util.postParam)
                } else {
                    buildPostFileParts(util.postParam, util.fileParam)
                }
            }
        ).build()

        HttpMethod.DELETE -> Request.Builder().url(util.url + if (util.getParam != "") "?${util.getParam}" else "").headers(util, util.headers).delete(buildBody(util.postParam)).build()

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

    fun executeForResult(req: Request, util: HttpSSEUtils) {
        val builder =
            OkHttpClient.Builder().connectTimeout(util.connectTimeout, TimeUnit.MILLISECONDS).callTimeout(util.callTimeout, TimeUnit.MILLISECONDS).readTimeout(util.readTimeout, TimeUnit.MILLISECONDS).writeTimeout(util.writeTimeout, TimeUnit.MILLISECONDS).followRedirects(util.followRedirects)
                .followSslRedirects(util.followRedirects).proxy(util.proxy)

        if (util.isHttps) {
            val trust = object : X509TrustManager {
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {}
                override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
            }
            builder.sslSocketFactory(SSLContext.getInstance("SSL").apply { init(null, arrayOf(trust), SecureRandom()) }.socketFactory, trust).hostnameVerifier { _, _ -> true }
        }
        val http = builder.build()

        val retList = mutableListOf<String>()
        inProgress = true

        EventSources.createFactory(http).newEventSource(req, object : EventSourceListener() {

            override fun onClosed(eventSource: EventSource) {
                inProgress = false
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                retList.add(util._streamEvent(data))
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                util._fail(t)
            }
        })
        while (inProgress) {
        }
        val finalStr = util._streamComplete(retList)
        util._success(finalStr)
    }
}