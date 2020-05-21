package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import java.net.URI

fun resource(name: String): URI = io.github.detekt.test.utils.resource(name)

fun yamlConfig(name: String) = YamlConfig.loadResource(resource(name).toURL())
