package io.gitlab.arturbosch.detekt.generator.collection

data class Rule(
    val name: String,
    val description: String,
    val nonCompliantCodeExample: String,
    val compliantCodeExample: String,
    val defaultActivationStatus: DefaultActivationStatus,
    var severity: String,
    var debt: String,
    var aliases: String?,
    val parent: String,
    val configurations: List<Configuration> = emptyList(),
    val autoCorrect: Boolean = false,
    val requiresTypeResolution: Boolean = false,
    val deprecationMessage: String? = null
) {
    fun isDeprecated() = deprecationMessage != null
}
