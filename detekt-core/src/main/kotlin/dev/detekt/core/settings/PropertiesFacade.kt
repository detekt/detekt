package dev.detekt.core.settings

import dev.detekt.api.PropertiesAware
import java.util.concurrent.ConcurrentHashMap

internal class PropertiesFacade : PropertiesAware {

    private val _properties: MutableMap<String, Any?> = ConcurrentHashMap()
    override val properties: Map<String, Any?> = _properties

    override fun register(key: String, value: Any) {
        _properties[key] = value
    }
}
