package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.resource
import io.github.detekt.utils.openSafeStream
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import java.io.StringReader

fun yamlConfig(name: String) = resource(name).toURL().openSafeStream().reader().use(YamlConfig::load)

fun yamlConfigFromContent(content: String): Config = StringReader(content.trimIndent()).use(YamlConfig::load)
