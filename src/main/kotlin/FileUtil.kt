@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path

/**
 * 这个类方便通过文件 + 一堆子目录进行文件全路径拼接
 */
class PathFile(f: File, vararg subs: String): File(Path(f.absolutePath, *subs).toFile().absolutePath) {
    init {
        if (!parentFile.exists()) parentFile.mkdirs()
    }
}

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

