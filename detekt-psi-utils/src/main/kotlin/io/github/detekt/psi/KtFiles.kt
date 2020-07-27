package io.github.detekt.psi

import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

const val KOTLIN_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"

val KtFile.fileName: String
    get() = name.substringAfterLast(File.separatorChar)

fun KtFile.fileNameWithoutSuffix(): String {
    val fileName = this.fileName
    if (fileName.endsWith(KOTLIN_SCRIPT_SUFFIX)) {
        return fileName.removeSuffix(KOTLIN_SCRIPT_SUFFIX)
    }
    return fileName.removeSuffix(KOTLIN_SUFFIX)
}

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
