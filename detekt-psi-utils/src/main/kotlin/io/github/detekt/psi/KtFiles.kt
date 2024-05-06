package io.github.detekt.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtFile
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

// KtFile.virtualFilePath is cached so should be a tiny bit more performant when called repeatedly for the same file.
fun KtFile.absolutePath(): Path = Path(virtualFilePath)

private fun buildPlatformSpecificSuffixes(platforms: List<String>): List<String> =
    platforms.map { platform -> ".$platform.kt" }
