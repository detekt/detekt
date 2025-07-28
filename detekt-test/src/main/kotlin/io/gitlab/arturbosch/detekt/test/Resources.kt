package io.gitlab.arturbosch.detekt.test

import dev.detekt.utils.openSafeStream
import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import org.intellij.lang.annotations.Language
import java.io.StringReader

fun yamlConfig(name: String): Config =
    resource(name).toURL().openSafeStream().reader().use(YamlConfig::load)

fun yamlConfigFromContent(@Language("yaml") content: String): Config =
    StringReader(content).use(YamlConfig::load)
