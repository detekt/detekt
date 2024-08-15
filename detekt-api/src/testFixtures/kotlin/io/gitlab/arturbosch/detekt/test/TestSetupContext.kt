package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute

class TestSetupContext(
    override val config: Config = Config.empty,
    override val basePath: Path = Path("").absolute(),
    properties: Map<String, Any?> = emptyMap(),
    override val outputChannel: Appendable = StringBuilder(),
    override val errorChannel: Appendable = StringBuilder(),
    override val configUris: Collection<URI> = emptyList(),
) : SetupContext {
    private val _properties: MutableMap<String, Any?> = properties.toMutableMap()
    override val properties: Map<String, Any?> = _properties

    init {
        require(basePath.isAbsolute) { "Base path should be absolute" }
    }

    override fun register(key: String, value: Any) {
        _properties[key] = value
    }
}
