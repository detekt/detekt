package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import java.net.URI

@OptIn(UnstableApi::class)
class EmptySetupContext : SetupContext {
    override val configUris: Collection<URI> = emptyList()
    override val config: Config = Config.empty
    override val outputChannel: Appendable = StringBuilder()
    override val errorChannel: Appendable = StringBuilder()
    override val properties: MutableMap<String, Any?> = HashMap()
    override fun register(key: String, value: Any) {
        properties[key] = value
    }
}
