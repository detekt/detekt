package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.UserDataProperty
import java.nio.file.Path
import kotlin.io.path.Path

const val KOTLIN_SUFFIX = ".kt"
const val KOTLIN_SCRIPT_SUFFIX = ".kts"
private val KOTLIN_GENERIC_SUFFIXES = listOf(KOTLIN_SUFFIX, KOTLIN_SCRIPT_SUFFIX)
private val KOTLIN_KMP_PLATFORM_SUFFIXES = listOf(
    "common",
    "ios",
    "android",
    "js",
    "jvm",
    "native",
    "iosArm64",
    "iosX64",
    "macosX64",
    "mingwX64",
    "linuxX64"
)

private val GENERIC_KOTLIN_FILE_SUFFIXES =
    buildPlatformSpecificSuffixes(KOTLIN_KMP_PLATFORM_SUFFIXES) + KOTLIN_GENERIC_SUFFIXES

/**
 * Removes kotlin specific file name suffixes, e.g. .kt.
 * Note, will not remove other possible/known file suffixes like '.java'
 */
fun PsiFile.fileNameWithoutSuffix(additionalSuffixes: List<String> = emptyList()): String {
    val fileName = this.name
    val suffixesToRemove = buildPlatformSpecificSuffixes(additionalSuffixes) + GENERIC_KOTLIN_FILE_SUFFIXES
    for (suffix in suffixesToRemove) {
        if (fileName.endsWith(suffix)) {
            return fileName.removeSuffix(suffix)
        }
    }
    return fileName
}

var PsiFile.absolutePath: Path? by UserDataProperty(Key("absolutePath"))

/*
absolutePath will be null when the Kotlin compiler plugin is used. The file's path can be obtained from the virtual file
instead.
*/
fun PsiFile.absolutePath(): Path = absolutePath ?: Path(virtualFile.path)

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

private fun buildPlatformSpecificSuffixes(platforms: List<String>): List<String> =
    platforms.map { platform -> ".$platform.kt" }
