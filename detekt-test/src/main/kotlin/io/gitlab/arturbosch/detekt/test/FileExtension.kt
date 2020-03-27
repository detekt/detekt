package io.gitlab.arturbosch.detekt.test

import java.nio.file.Files
import java.nio.file.Path

/**
 * Creates an empty file in the default temporary-file directory, using
 * the given [prefix] and [suffix] to generate its name.
 * The resulting file in the returned path is automatically deleted on JVM exit.
 */
fun createTempFileForTest(prefix: String, suffix: String): Path {
    val path = Files.createTempFile(prefix, suffix)
    path.toFile().deleteOnExit()
    return path
}
