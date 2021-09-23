@file:Suppress("DuplicatedCode", "unused")

package com.isyscore.kotlin.common

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.io.IOUtil
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

object Resource {

    fun read(file: String): String? = javaClass.getResourceAsStream("/$file").use { if (it == null) null else String(it.readBytes()) }
    fun readBytes(file: String): ByteArray? = javaClass.getResourceAsStream("/$file").use { it?.readBytes() }

    fun extract(file: String, dest: String) {
        var saveFileDir = dest
        if (!saveFileDir.endsWith(File.separator)) {
            saveFileDir += File.separator
        }
        val dir = File(saveFileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val ins = javaClass.getResourceAsStream("/$file")
        val zais = ZipArchiveInputStream(ins)

        while (true) {
            val archiveEntry = zais.nextEntry ?: break
            val entryFileName = archiveEntry.name
            val entryFilePath = saveFileDir + entryFileName
            val entryFile = File(entryFilePath)
            if (entryFileName.endsWith(File.separator)) {
                entryFile.mkdirs()
            } else {
                val bos = BufferedOutputStream(FileOutputStream(entryFile))
                IOUtil.copy(zais, bos)
                bos.close()
            }
        }
        zais.close()
        ins?.close()
    }
}

