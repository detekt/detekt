package io.gitlab.arturbosch.detekt.generator.collection

data class Configuration(
    val name: String,
    val description: String,
    val defaultValue: String,
    val deprecated: String?
)
