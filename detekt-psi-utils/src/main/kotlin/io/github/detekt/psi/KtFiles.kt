package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import java.nio.file.Path
import kotlin.io.path.Path

private val KOTLIN_GENERIC_SUFFIXES = listOf(".kt", ".kts")

/**
 * Removes kotlin specific file name suffixes, e.g. .kt.
 * Note, will not remove other possible/known file suffixes like '.java'
 */
fun PsiFile.fileNameWithoutSuffix(multiplatformTargetSuffixes: List<String> = emptyList()): String {
    val fileName = this.name
    val suffixesToRemove = buildPlatformSpecificSuffixes(multiplatformTargetSuffixes) + KOTLIN_GENERIC_SUFFIXES
    for (suffix in suffixesToRemove) {
        if (fileName.endsWith(suffix)) {
            return fileName.removeSuffix(suffix)
        }
    }
    return fileName
}

fun PsiFile.absolutePath(): Path = Path(virtualFile.path)

// #3317 If any rule mutates the PsiElement, searching the original PsiElement may throw an exception.
fun getLineAndColumnInPsiFile(file: PsiFile, range: TextRange): PsiDiagnosticUtils.LineAndColumn? {
    return if (file.textLength == 0) {
        null
    } else {
        runCatching {
            @Suppress("ForbiddenMethodCall")
            DiagnosticUtils.getLineAndColumnInPsiFile(file, range)
        }.getOrNull()
    }
}

private fun buildPlatformSpecificSuffixes(platforms: List<String>): List<String> =
    platforms.map { platform -> ".$platform.kt" }
