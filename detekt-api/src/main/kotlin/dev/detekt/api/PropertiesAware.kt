package dev.detekt.api

/**
 * Properties holder. Allows to store and retrieve any data.
 */
interface PropertiesAware {

    /**
     * Raw properties.
     */
    val properties: Map<String, Any?>

    /**
     * Binds a given value with given key and stores it for later use.
     */
    fun register(key: String, value: Any)
}

/**
 * Allows to retrieve stored properties in a type safe way.
 */
inline fun <reified T : Any> PropertiesAware.getOrNull(key: String): T? {
    val value = properties[key]
    if (value != null) {
        return value as? T ?: error("No value of type ''${T::class} for key '$key'.")
    }
    return null
}
