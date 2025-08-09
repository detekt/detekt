package dev.detekt.gradle.plugin.testkit

import org.gradle.api.Task
import org.intellij.lang.annotations.Language

fun Task.dependenciesAsPaths() = this.taskDependencies.getDependencies(this).map { it.path }

/**
 * This assumes the usual structure of a Kotlin test file:
 * ```
 * class Test {
 *     fun `test name`() {
 *         val code = """
 *             // code
 *             ${otherCode.reIndent()}
 *             // more code
 *             some.block {
 *                 ${innerCode.reIndent(1)}
 *             }
 *         """.trimIndent()
 *     }
 * }
 * ```
 * In case the call site doesn't look like the above,
 * set the [baseIndent] parameter to the number of indentation levels until the inside of the `"""`.
 */
fun String.reIndent(level: Int = 0, baseIndent: Int = 3): String =
    this.replaceIndent("    ".repeat(baseIndent + level)).trim()

fun joinGradleBlocks(@Language("gradle.kts") vararg blocks: String): String =
    blocks.joinToString(separator = "\n\n")
