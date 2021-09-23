@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.isyscore.kotlin.common

import java.io.File
import java.net.URL
import java.net.URLClassLoader

object ClassUtil {

    fun loadJar(jarPath: String) = loadJar(File(jarPath))

    fun loadJar(jarFile: File) {
        val method = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply { isAccessible = true }
        val classloader = ClassLoader.getSystemClassLoader() as URLClassLoader
        method.invoke(classloader, jarFile.toURI().toURL())
    }

}