@file:Suppress("unused")

package com.isyscore.kotlin.common

import org.burningwave.core.assembler.StaticComponentContainer.Modules

object Exporter {

    fun export(vararg packageNames: String) {
        Modules.exportPackageToAllUnnamed("java.base", *packageNames)
    }

}