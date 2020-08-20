package io.gitlab.arturbosch.detekt.generator.collection

data class Rule(
    val name: String,
    val description: String,
    val nonCompliantCodeExample: String,
    val compliantCodeExample: String,
    val active: Boolean,
    var severity: String,
    var debt: String,
    var aliases: String?,
    val parent: String,
    val configuration: List<Configuration> = listOf(),
    val autoCorrect: Boolean = false,
    var inMultiRule: String? = null,
    val requiresTypeResolution: Boolean = false
)
