package dev.detekt.generator.collection

data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: DefaultValue,
    val defaultAndroidValue: DefaultValue?,
    val deprecated: String?,
) {
    fun isDeprecated() = deprecated != null
}
