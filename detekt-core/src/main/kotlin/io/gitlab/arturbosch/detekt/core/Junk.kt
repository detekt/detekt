package io.gitlab.arturbosch.detekt.core

import java.nio.file.Files
import java.nio.file.Path

fun Path.exists(): Boolean = Files.exists(this)
fun Path.isFile(): Boolean = Files.isRegularFile(this)
fun Path.isDirectory(): Boolean = Files.isDirectory(this)

val NL: String = System.lineSeparator()
