package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import java.io.StringReader

fun yamlConfig(name: String) = YamlConfig.load(resource(name).toURL().openStream().reader())

fun yamlConfigFromContent(content: String): Config =
    YamlConfig.load(StringReader(content.trimIndent()))
