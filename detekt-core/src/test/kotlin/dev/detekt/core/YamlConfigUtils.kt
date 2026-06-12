package dev.detekt.core

import dev.detekt.core.config.YamlConfig
import dev.detekt.test.utils.resource
import dev.detekt.utils.openSafeStream
import org.intellij.lang.annotations.Language
import java.io.StringReader

fun yamlConfig(name: String): YamlConfig = resource(name).toURL().openSafeStream().reader().use(YamlConfig::load)

fun yamlConfigFromContent(@Language("yaml") content: String): YamlConfig = StringReader(content).use(YamlConfig::load)
