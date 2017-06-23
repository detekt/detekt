package io.gitlab.arturbosch.detekt.test

import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

fun resource(name: String): URI = KtTestCompiler::class.java.getResource(
		if (name.startsWith("/")) name else "/$name").toURI()

fun resourcePath(name: String): String = resource(name).path

fun resourceAsString(name: String): String = String(Files.readAllBytes(Paths.get(resourcePath(name))))