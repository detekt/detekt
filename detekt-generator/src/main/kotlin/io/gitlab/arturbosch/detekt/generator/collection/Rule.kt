package io.gitlab.arturbosch.detekt.generator.collection

data class Rule(
    val name: String,
    val description: String,
    val nonCompliantCodeExample: String,
    val compliantCodeExample: String,
    val defaultActivationStatus: DefaultActivationStatus,
    var aliases: List<String>,
    val parent: String,
    val configurations: List<Configuration> = emptyList(),
    val autoCorrect: Boolean = false,
    val requiresFullAnalysis: Boolean = false,
    val deprecationMessage: String? = null,
) {
    fun isDeprecated() = deprecationMessage != null
}
