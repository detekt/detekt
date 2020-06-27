package io.github.detekt.tooling.internal

import io.github.detekt.tooling.api.spec.ExtensionsSpec
import java.nio.file.Files
import java.nio.file.Path

internal data class PluginsHolder(
    override val paths: Collection<Path>?,
    override val loader: ClassLoader?
) : ExtensionsSpec.Plugins {

    init {
        require(paths == null || loader == null) { "Either paths or loader must be specified, not both." }
        paths?.forEach { require(Files.exists(it)) { "Plugin jar '$it' does not exist." } }
    }
}
