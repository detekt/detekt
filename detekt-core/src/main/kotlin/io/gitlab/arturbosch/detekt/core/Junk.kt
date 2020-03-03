package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.psi.KtFile
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

fun Path.exists(): Boolean = Files.exists(this)
fun Path.isFile(): Boolean = Files.isRegularFile(this)
fun Path.isDirectory(): Boolean = Files.isDirectory(this)

val LINE_SEPARATOR: Key<String> = Key("lineSeparator")

fun MutableMap<String, List<Finding>>.mergeSmells(other: Map<String, List<Finding>>) {
    for ((key, findings) in other.entries) {
        merge(key, findings) { f1, f2 -> f1.plus(f2) }
    }
}

fun Throwable.printStacktraceRecursively(logger: PrintStream) {
    stackTrace.forEach { logger.println(it) }
    cause?.printStacktraceRecursively(logger)
}

typealias FindingsResult = List<Map<RuleSetId, List<Finding>>>

fun createErrorMessage(file: KtFile, error: Throwable): String =
    "Analyzing '${file.absolutePath()}' led to an exception.\n" +
        "The original exception message was: ${error.localizedMessage}\n" +
        "Running detekt '${whichDetekt() ?: "unknown"}' on Java '${whichJava()}' on OS '${whichOS()}'.\n" +
        "If the exception message does not help, please feel free to create an issue on our GitHub page."
