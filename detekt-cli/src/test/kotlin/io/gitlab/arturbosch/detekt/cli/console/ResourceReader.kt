package io.gitlab.arturbosch.detekt.cli.console

import io.github.detekt.test.utils.resourceAsPath
import java.nio.file.Files

internal fun readResource(filename: String): String {
    val path = resourceAsPath(filename)
    return Files.readAllLines(path).joinToString("\n") + "\n"
}
