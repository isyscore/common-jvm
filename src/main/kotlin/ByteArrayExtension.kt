@file:Suppress("unused")

package com.isyscore.kotlin.common

import java.io.File

fun ByteArray.save(dest: File) = dest.writeBytes(this)