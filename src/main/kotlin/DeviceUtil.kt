@file:Suppress("unused")

package com.isyscore.kotlin.common

val isMac: Boolean get() = System.getProperty("os.name").contains("Mac")
val isWindows: Boolean get() = System.getProperty("os.name").contains("Windows")
val isUnix: Boolean get() = !isMac && !isWindows

