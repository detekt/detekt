package dev.detekt.tooling.internal

import dev.detekt.tooling.api.spec.ExtensionsSpec
import java.nio.file.Path
import kotlin.io.path.exists

internal data class PluginsHolder(override val paths: Collection<Path>?, override val loader: ClassLoader?) :
    ExtensionsSpec.Plugins {

    init {
        require(paths == null || loader == null) { "Either paths or loader must be specified, not both." }
        paths?.forEach { require(it.exists()) { "Plugin jar '$it' does not exist." } }
    }
}
