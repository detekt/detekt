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
 * Represents both absolute path and relative path if available.
 */
data class FilePath constructor(
    val absolutePath: Path,
    val basePath: Path? = null,
    val relativePath: Path? = null
) {

    init {
        require(basePath == null ||
            relativePath == null ||
            absolutePath == basePath.resolve(relativePath).normalize()
        ) {
            "Absolute path = $absolutePath much match base path = $basePath and relative path = $relativePath"
        }
    }

    companion object {
        fun fromAbsolute(path: Path) = FilePath(absolutePath = path.normalize())
        fun fromRelative(basePath: Path, relativePath: Path) = FilePath(
            absolutePath = basePath.resolve(relativePath).normalize(),
            basePath = basePath.normalize(),
            relativePath = relativePath
        )
    }
}

fun PsiFile.toFilePath(): FilePath {
    val relativePath = getUserData(RELATIVE_PATH)
    val basePath = getUserData(BASE_PATH)
    return when {
        basePath != null && relativePath != null -> FilePath(
            absolutePath = absolutePath(),
            basePath = Paths.get(basePath),
            relativePath = Paths.get(relativePath)
        )
        basePath == null && relativePath == null -> FilePath(absolutePath = absolutePath())
        else -> error("Cannot build a FilePath from base path = $basePath and relative path = $relativePath")
    }
}
