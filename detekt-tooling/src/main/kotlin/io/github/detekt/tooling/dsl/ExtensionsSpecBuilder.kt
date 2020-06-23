package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.internal.PluginsHolder
import java.nio.file.Path

@ProcessingModelDsl
class ExtensionsSpecBuilder : Builder<ExtensionsSpec>, ExtensionsSpec {

    override var disableDefaultRuleSets: Boolean = false
    override var plugins: ExtensionsSpec.Plugins? = null

    override fun build(): ExtensionsSpec = ExtensionsModel(disableDefaultRuleSets, plugins)

    fun fromPaths(paths: () -> Collection<Path>) {
        plugins = PluginsHolder(paths(), null)
    }

    fun fromClassloader(classLoader: () -> ClassLoader) {
        plugins = PluginsHolder(null, classLoader())
    }
}

internal data class ExtensionsModel(
    override val disableDefaultRuleSets: Boolean,
    override val plugins: ExtensionsSpec.Plugins?
) : ExtensionsSpec
