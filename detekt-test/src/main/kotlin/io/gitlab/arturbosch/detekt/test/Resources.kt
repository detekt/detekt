package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.YamlConfig
import java.net.URI

internal object Resources

fun resource(name: String): URI {
	val explicitName = if (name.startsWith("/")) name else "/$name"
	val resource = Resources::class.java.getResource(explicitName)
	requireNotNull(resource) { "Make sure the resource '$name' exists!" }
	return resource.toURI()
}

fun yamlConfig(resource: String) = YamlConfig.loadResource(Resources::class.java.getResource("/$resource"))
