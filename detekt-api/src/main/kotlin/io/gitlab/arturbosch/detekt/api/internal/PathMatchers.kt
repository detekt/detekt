package io.gitlab.arturbosch.detekt.api.internal

import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.PathMatcher

/**
 * Converts given [pattern] into a [PathMatcher] specified by [FileSystem.getPathMatcher].
 * We only support the "glob:" syntax to stay os independently.
 * Internally a globbing pattern is transformed to a regex respecting the Windows file system.
 */
fun pathMatcher(pattern: String): PathMatcher {

    val result = when (pattern.substringBefore(":")) {
        "glob" -> pattern
        "regex" -> throw IllegalArgumentException(USE_GLOB_MSG)
        else -> "glob:$pattern"
    }

    return FileSystems.getDefault().getPathMatcher(result)
}

private const val USE_GLOB_MSG =
    "Only globbing patterns are supported as they are treated os-independently by the PathMatcher api."

fun KtFile.absolutePath(): String = getUserData(ABSOLUTE_PATH)
    ?: throw IllegalStateException("KtFile '$name' expected to have an absolute path.")

fun KtFile.relativePath(): String = getUserData(RELATIVE_PATH)
    ?: throw IllegalStateException("KtFile '$name' expected to have an relative path.")

val RELATIVE_PATH: Key<String> = Key("relativePath")
val ABSOLUTE_PATH: Key<String> = Key("absolutePath")
