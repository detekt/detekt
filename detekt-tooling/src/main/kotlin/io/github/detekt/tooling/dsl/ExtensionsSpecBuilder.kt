package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ExtensionId
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.internal.PluginsHolder
import java.nio.file.Path

@ProcessingModelDsl
class ExtensionsSpecBuilder : Builder<ExtensionsSpec>, ExtensionsSpec {

    override var disableDefaultRuleSets: Boolean = false
    override var plugins: ExtensionsSpec.Plugins? = null
    override var disabledExtensions: MutableSet<ExtensionId> = mutableSetOf()

    override fun build(): ExtensionsSpec = ExtensionsModel(
        disableDefaultRuleSets,
        plugins,
        disabledExtensions
    )

    fun disableExtension(id: ExtensionId) {
        disabledExtensions.add(id)
    }

    fun fromPaths(paths: () -> Collection<Path>) {
        require(plugins == null) { "Plugin source already specified." }
        plugins = PluginsHolder(paths(), null)
    }

    fun fromClassloader(classLoader: () -> ClassLoader) {
        require(plugins == null) { "Plugin source already specified." }
        plugins = PluginsHolder(null, classLoader())
    }
}

internal data class ExtensionsModel(
    override val disableDefaultRuleSets: Boolean,
    override val plugins: ExtensionsSpec.Plugins?,
    override val disabledExtensions: Set<ExtensionId>
) : ExtensionsSpec
