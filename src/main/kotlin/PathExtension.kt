@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.nio.file.Path

fun Path.normalizeAndRelativize(): Path =
    root?.relativize(this)?.normalize()?.dropLeadingTopDirs() ?: normalize().dropLeadingTopDirs()

private fun Path.dropLeadingTopDirs(): Path {
    val startIndex = indexOfFirst { it.toString() != ".." }
    if (startIndex == 0) return this
    return subpath(startIndex, nameCount)
}