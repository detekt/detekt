package io.github.detekt.psi

import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Path
import java.nio.file.Paths

fun KtFile.absolutePath(): Path {
    val value = getUserData(ABSOLUTE_PATH)
    checkNotNull(value) { "KtFile '$name' expected to have an absolute path." }
    return Paths.get(value)
}

fun KtFile.relativePath(): Path {
    val value = getUserData(RELATIVE_PATH)
    checkNotNull(value) { "KtFile '$name' expected to have an relative path." }
    return Paths.get(value)
}
