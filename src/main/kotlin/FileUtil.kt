@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

fun fileWalk(basePath: String, callback:(File) -> Unit) {
    val f = File(basePath)
    f.listFiles()?.forEach {
        if (it.isDirectory) {
            fileWalk(it.absolutePath, callback)
        } else if (it.isFile) {
            callback(it)
        }
    }
}

fun File.hash(alg: String): String {
    if (!this.isFile) {
        return ""
    }
    val digest = MessageDigest.getInstance(alg)
    val ins = FileInputStream(this)
    val buffer = ByteArray(1024)
    while (true) {
        val len = ins.read(buffer, 0, 1024)
        if (len == -1) break
        digest.update(buffer, 0, len)
    }
    ins.close()
    val bigInt = BigInteger(1, digest.digest())
    return bigInt.toString(16)
}

val File.md5Sha1: String get() = hash("MD5") + hash("SHA1")

