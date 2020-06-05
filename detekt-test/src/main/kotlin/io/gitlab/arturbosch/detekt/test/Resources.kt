package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import java.io.StringReader

fun yamlConfig(name: String) = YamlConfig.loadResource(resource(name).toURL())

fun yamlConfigFromContent(content: String): Config =
    YamlConfig.load(StringReader(content.trimIndent()))
