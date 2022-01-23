package io.gitlab.arturbosch.detekt.generator.collection

data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: DefaultValue,
    val defaultAndroidValue: DefaultValue?,
    val deprecated: String?
) {
    fun isDeprecated() = deprecated != null

    fun isDefaultValueNonEmptyList() = defaultValue.isNonEmptyList()

    fun getDefaultValueAsList(): List<String> = defaultValue.getAsList()
}
