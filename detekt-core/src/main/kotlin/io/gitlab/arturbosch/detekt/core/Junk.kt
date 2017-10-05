package io.gitlab.arturbosch.detekt.core

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
