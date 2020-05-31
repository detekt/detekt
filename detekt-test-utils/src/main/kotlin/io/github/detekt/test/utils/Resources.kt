package io.github.detekt.test.utils

import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal object Resources

fun resource(name: String): URI {
    val explicitName = if (name.startsWith("/")) name else "/$name"
    val resource = Resources::class.java.getResource(explicitName)
    requireNotNull(resource) { "Make sure the resource '$name' exists!" }
    return resource.toURI()
}

fun resourceAsPath(name: String): Path = Paths.get(resource(name))

fun readResourceContent(name: String): String {
    val path = resourceAsPath(name)
    return Files.readAllLines(path).joinToString("\n") + "\n"
}
