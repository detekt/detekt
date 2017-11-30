package io.gitlab.arturbosch.detekt.generator.collection

data class CodeExample(
		val ruleName: String,
		val compliant: String,
		val nonCompliant: String
)
