package dev.detekt.test.utils

import java.net.URI
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.toPath

internal object Resources

fun resourceUrl(name: String): URL {
    val explicitName = if (name.startsWith("/")) name else "/$name"
    return requireNotNull(Resources::class.java.getResource(explicitName)) { "Make sure the resource '$name' exists!" }
}

fun resource(name: String): URI = resourceUrl(name).toURI()

fun resourceAsPath(name: String): Path = resource(name).toPath()

fun readResourceContent(name: String): String = resourceUrl(name).readText()
