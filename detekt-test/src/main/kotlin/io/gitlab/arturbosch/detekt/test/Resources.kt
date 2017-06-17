package io.gitlab.arturbosch.detekt.test

import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

fun resource(name: String): URL = KtTestCompiler::class.java.getResource("/$name")

fun resourcePath(name: String): String = resource(name).path

fun resourceAsString(name: String): String = String(Files.readAllBytes(Paths.get(resourcePath(name))))