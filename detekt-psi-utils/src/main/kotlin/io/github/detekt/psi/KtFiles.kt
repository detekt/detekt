package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.invariantSeparatorsPathString

const val KOTLIN_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"
private const val KOTLIN_KMP_COMMON_SUFFIX = ".common.kt"

private val KOTLIN_FILE_SUFFIXES = arrayOf(
    KOTLIN_KMP_COMMON_SUFFIX,
    KOTLIN_SUFFIX,
    KOTLIN_SCRIPT_SUFFIX
)

val PsiFile.fileName: String
    get() = name.substringAfterLast(File.separatorChar)

/**
 * Removes kotlin specific file name suffixes, e.g. .kt.
 * Note, will not remove other possible/known file suffixes like '.java'
 */
fun PsiFile.fileNameWithoutSuffix(): String {
    val fileName = this.fileName
    for (suffix in KOTLIN_FILE_SUFFIXES) {
        if (fileName.endsWith(suffix)) {
            return fileName.removeSuffix(suffix)
        }
    }
    return fileName
}

fun PsiFile.absolutePath(): Path = Path(name)

fun PsiFile.relativePath(): Path? = getUserData(RELATIVE_PATH)?.let { Path(it) }

fun PsiFile.basePath(): Path? = getUserData(BASE_PATH)?.let { Path(it) }

/**
 * Represents both absolute path and relative path if available.
 */
data class FilePath constructor(
    val absolutePath: Path,
    val basePath: Path? = null,
    val relativePath: Path? = null
) {

    init {
        require(
            basePath == null ||
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
    val relativePath = relativePath()
    val basePath = basePath()
    return when {
        basePath != null && relativePath != null -> FilePath(
            absolutePath = absolutePath(),
            basePath = basePath,
            relativePath = relativePath
        )

        basePath == null && relativePath == null -> FilePath(absolutePath = absolutePath())
        else -> error("Cannot build a FilePath from base path = $basePath and relative path = $relativePath")
    }
}

// #3317 If any rule mutates the PsiElement, searching the original PsiElement may throw an exception.
fun getLineAndColumnInPsiFile(file: PsiFile, range: TextRange): PsiDiagnosticUtils.LineAndColumn? {
    return runCatching {
        @Suppress("ForbiddenMethodCall")
        DiagnosticUtils.getLineAndColumnInPsiFile(file, range)
    }.getOrNull()
}

/**
 * Returns a system-independent string with UNIX system file separator.
 */
@Deprecated(
    "Use stdlib method",
    ReplaceWith("invariantSeparatorsPathString", "kotlin.io.path.invariantSeparatorsPathString")
)
fun Path.toUnifiedString(): String = invariantSeparatorsPathString
