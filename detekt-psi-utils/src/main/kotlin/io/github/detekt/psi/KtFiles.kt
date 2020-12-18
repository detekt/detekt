package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

const val KOTLIN_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"

val PsiFile.fileName: String
    get() = name.substringAfterLast(File.separatorChar)

fun PsiFile.fileNameWithoutSuffix(): String {
    val fileName = this.fileName
    if (fileName.endsWith(KOTLIN_SCRIPT_SUFFIX)) {
        return fileName.removeSuffix(KOTLIN_SCRIPT_SUFFIX)
    }
    return fileName.removeSuffix(KOTLIN_SUFFIX)
}

fun PsiFile.absolutePath(): Path = Paths.get(name)

/**
 * A file path that represents both relative path or absolute path.
 * If representing an absolute path, [basePath] is null and [path] represents the full path.
 * If representing a relative path, [basePath] is the base path while [path] represents the path relative to [basePath].
 */
data class FilePath constructor(val path: String, val basePath: String? = null) {
    companion object {
        fun fromAbsolute(path: String) = FilePath(path)
        fun fromRelative(basePath: String, path: String) = FilePath(path, basePath)
    }
}

fun PsiFile.relativePath(): Path? = getUserData(RELATIVE_PATH)?.let { Paths.get(it) }

fun PsiFile.basePath(): Path? = getUserData(BASE_PATH)?.let { Paths.get(it) }

fun PsiFile.toFilePath(): FilePath {
    val relativePath = relativePath()
    val basePath = basePath()
    return when {
        relativePath != null && basePath != null ->
            FilePath.fromRelative(basePath = basePath.toString(), path = relativePath.toString())
        relativePath == null && basePath == null ->
            FilePath.fromAbsolute(path = absolutePath().toString())
        else -> error("Cannot build a FilePath from relative path = $relativePath and base path = $basePath")
    }
}
