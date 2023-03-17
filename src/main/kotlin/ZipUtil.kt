@file:Suppress("unused", "PropertyName", "DuplicatedCode", "MemberVisibilityCanBePrivate")

package com.isyscore.kotlin.common

import org.apache.commons.compress.archivers.zip.Zip64Mode
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.io.IOUtil
import java.io.*
import java.util.zip.ZipFile

object ZipUtil {
    fun readFromZip(zipPath: String, innerPath: String): ByteArray? = readFromZip(File(zipPath), innerPath)
    fun readFromZip(zipFile: File, innerPath: String): ByteArray? {
        val zip = ZipFile(zipFile)
        val entry = zip.getEntry(innerPath) ?: return null
        return zip.getInputStream(entry).use { it.readBytes() }
    }
    fun readStringFromZip(zipPath: String, innerPath: String): String? = readStringFromZip(File(zipPath), innerPath)
    fun readStringFromZip(zipFile: File, innerPath: String): String? = readFromZip(zipFile, innerPath)?.let { String(it) }
}

class ZipUtils {
    var zipPath = ""
    var srcPath = ""
    var destPath = ""
    val filterList = mutableListOf<String>()
    internal var _success: () -> Unit = { }
    internal var _error: (String?) -> Unit = { _ -> }
    internal var _progress: (fileName: String, current: Int, total: Int) -> Unit = { _, _, _ -> }

    fun progress(onProgress: (fileName: String, current: Int, total: Int) -> Unit) {
        _progress = onProgress
    }
    fun success(onSuccess: () -> Unit) {
        _success = onSuccess
    }
    fun error(onError: (error: String?) -> Unit) {
        _error = onError
    }
}

fun zip(init: ZipUtils.() -> Unit) {
    val z = ZipUtils()
    z.init()
    try {
        ZipOperations.zip(z, z.filterList)
        z._success()
    } catch (e: Exception) {
        z._error(e.message)
    }
}

fun unzip(init: ZipUtils.() -> Unit) {
    val z = ZipUtils()
    z.init()
    try {
        ZipOperations.unzip(z)
        z._success()
    } catch (e: Exception) {
        z._error(e.message)
    }
}

private object ZipOperations {

    fun getFiles(dir: String, filterList: List<String>): List<String> {
        val lstFile = mutableListOf<String>()
        File(dir).listFiles()?.filter { !filterList.contains(it.name) }?.forEach {
            if (it.isDirectory) {
                lstFile.add(it.absolutePath)
                lstFile.addAll(getFiles(it.absolutePath, filterList))
            } else {
                lstFile.add(it.absolutePath)
            }
        }
        return lstFile
    }

    fun getFilePathName(dir: String, path: String): String = path.replace(dir + File.separator, "").replace("\\", "/")

    @Throws(Exception::class)
    fun compressFiles(files: Array<String>?, z: ZipUtils) {
        if (files == null || files.isEmpty()) {
            return
        }
        val zipFile = File(z.zipPath)
        val zaos = ZipArchiveOutputStream(zipFile)
        zaos.setUseZip64(Zip64Mode.AsNeeded)
        var current = 0
        for (f in files) {
            val file = File(f)
            val name = getFilePathName(z.srcPath, f)
            val zipEntry = ZipArchiveEntry(file, name)
            zaos.putArchiveEntry(zipEntry)
            if (file.isDirectory) continue
            val bis = BufferedInputStream(FileInputStream(file))
            IOUtil.copy(bis, zaos)
            zaos.closeArchiveEntry()
            bis.close()
            current++
            z._progress(name, current, files.size)
        }
        zaos.close()
    }

    @Throws(Exception::class)
    fun zip(z: ZipUtils, filterList: List<String>) {
        val paths = getFiles(z.srcPath, filterList)
        compressFiles(paths.toTypedArray(), z)
    }

    @Throws(Exception::class)
    fun unzip(z: ZipUtils) {

        var saveFileDir = z.destPath
        if (!saveFileDir.endsWith("\\") && !saveFileDir.endsWith("/")) {
            saveFileDir += File.separator
        }
        val dir = File(saveFileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(z.zipPath)
        if (file.exists()) {
            val zSize = ZipFile(z.zipPath).size()
            val fis = FileInputStream(file)
            val zais = ZipArchiveInputStream(fis)
            var current = 0
            while (true) {
                val archiveEntry = zais.nextEntry ?: break
                val entryFileName = archiveEntry.name
                val entryFilePath = saveFileDir + entryFileName
                val entryFile = File(entryFilePath)
                if (entryFileName.endsWith("/") || entryFileName.endsWith("\\")) {
                    entryFile.mkdirs()
                } else {
                    if (!entryFile.parentFile.exists()) {
                        entryFile.parentFile.mkdirs()
                    }
                    val bos = BufferedOutputStream(FileOutputStream(entryFile))
                    IOUtil.copy(zais, bos)
                    bos.close()
                }
                current++
                z._progress(entryFileName, current, zSize)
            }
            fis.close()
            zais.close()
        }
    }
}
