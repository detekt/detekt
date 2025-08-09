package dev.detekt.gradle.plugin.internal

import java.io.File

internal fun File.existingVariantOrBaseFile(variant: String): File? {
    val variantFile = this.addVariantName(variant)
    // if there is a file with the variant name, it has precedence
    return when {
        variantFile.exists() -> variantFile
        this.exists() -> this
        else -> null
    }
}

internal fun File.addVariantName(variant: String, separator: String = "-"): File =
    File(parent, name.substringBeforeLast(".") + "$separator$variant." + name.substringAfterLast("."))
