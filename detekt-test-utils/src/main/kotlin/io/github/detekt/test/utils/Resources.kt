package io.github.detekt.test.utils

import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal object Resources

fun resourceUrl(name: String): URL {
    val explicitName = if (name.startsWith("/")) name else "/$name"
    val resource = Resources::class.java.getResource(explicitName)
    requireNotNull(resource) { "Make sure the resource '$name' exists!" }
    return resource
}

fun resource(name: String): URI = resourceUrl(name).toURI()

fun resourceAsPath(name: String): Path = Paths.get(resource(name))

fun readResourceContent(name: String): String {
    val path = resourceAsPath(name)
    return Files.readAllLines(path).joinToString("\n") + "\n"
}
