package dev.detekt.test

import dev.detekt.api.Config
import dev.detekt.core.config.YamlConfig
import dev.detekt.utils.openSafeStream
import org.intellij.lang.annotations.Language
import java.io.StringReader
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

fun yamlConfig(name: String): Config =
    resource(name).toURL().openSafeStream().reader().use(YamlConfig::load)

fun yamlConfigFromContent(@Language("yaml") content: String): Config =
    StringReader(content).use(YamlConfig::load)
