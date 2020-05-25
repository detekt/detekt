package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.resource
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig

fun yamlConfig(name: String) = YamlConfig.loadResource(resource(name).toURL())
