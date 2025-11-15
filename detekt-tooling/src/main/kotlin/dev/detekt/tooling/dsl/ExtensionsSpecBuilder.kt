package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.spec.ExtensionId
import dev.detekt.tooling.api.spec.ExtensionsSpec
import dev.detekt.tooling.internal.PluginsHolder
import java.nio.file.Path

@ProcessingModelDsl
class ExtensionsSpecBuilder : Builder<ExtensionsSpec> {

    var plugins: ExtensionsSpec.Plugins? = null

    private val disabledExtensions: MutableSet<ExtensionId> = mutableSetOf()

    override fun build(): ExtensionsSpec =
        ExtensionsModel(
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

private data class ExtensionsModel(
    override val plugins: ExtensionsSpec.Plugins?,
    override val disabledExtensions: Set<ExtensionId>,
) : ExtensionsSpec
