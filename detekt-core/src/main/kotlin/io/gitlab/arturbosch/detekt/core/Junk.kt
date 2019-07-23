package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.HashMap

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

fun <K, V> List<Pair<K, List<V>>>.toMergedMap(): Map<K, List<V>> {
    val map = HashMap<K, MutableList<V>>()
    for ((key, values) in this) {
        map.merge(key, values.toMutableList()) { l1, l2 ->
            l1.apply { addAll(l2) }
        }
    }
    return map
}
