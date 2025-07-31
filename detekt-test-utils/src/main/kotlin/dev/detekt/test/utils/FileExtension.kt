package dev.detekt.test.utils

import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

/**
 * Creates an empty file in the default temporary-file directory, using
 * the given [prefix] and [suffix] to generate its name.
 * The resulting file in the returned path is automatically deleted on JVM exit.
 */
fun createTempFileForTest(prefix: String, suffix: String): Path {
    val path = createTempFile(prefix, suffix)
    path.toFile().deleteOnExit()
    return path
}

/**
 * Creates a new directory in the default temporary-file directory, using
 * the given [prefix] to generate its name.
 * The resulting directory in the returned path is automatically deleted on JVM exit.
 */
fun createTempDirectoryForTest(prefix: String): Path {
    val dir = createTempDirectory(prefix)
    dir.toFile().deleteOnExit()
    return dir
}
