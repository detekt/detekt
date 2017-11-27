@file:JvmName("Main")

package io.gitlab.arturbosch.detekt.generator

import io.gitlab.arturbosch.detekt.core.exists
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Marvin Ramin
 */
fun main(args: Array<String>) {
	val path = getPath()
	val executable = Runner(path)
	executable.execute()
}

private const val RULES_SOURCES_PATH = "./detekt-rules/src/main/kotlin"
fun getPath(): Path {
	val path = Paths.get(RULES_SOURCES_PATH)
	require(path.exists()) { "Path to detekt-rules module does not exist." }
	return path
}
