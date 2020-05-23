package io.github.detekt.test.utils

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

/**
 * Creates a new directory in the default temporary-file directory, using
 * the given [prefix] to generate its name.
 * The resulting directory in the returned path is automatically deleted on JVM exit.
 */
fun createTempDirectoryForTest(prefix: String): Path {
    val dir = Files.createTempDirectory(prefix)
    dir.toFile().deleteOnExit()
    return dir
}
