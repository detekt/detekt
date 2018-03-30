package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Artur Bosch
 */

fun <T> Stream<T>.toList(): List<T> = collect(Collectors.toList<T>())

fun Path.exists(): Boolean = Files.exists(this)
fun Path.isFile(): Boolean = Files.isRegularFile(this)
fun Path.isDirectory(): Boolean = Files.isDirectory(this)

fun KtFile.relativePath(): String? = getUserData(KtCompiler.RELATIVE_PATH)
fun KtFile.absolutePath(): String? = getUserData(KtCompiler.ABSOLUTE_PATH)

fun MutableMap<String, List<Finding>>.mergeSmells(other: Map<String, List<Finding>>) {
	for ((key, findings) in other.entries) {
		merge(key, findings) { f1, f2 -> f1.plus(f2) }
	}
}
