package io.gitlab.arturbosch.detekt.generator.collection

class InvalidIssueDeclaration(attributeName: String)
	: RuntimeException("Invalid issue declaration attribute $attributeName.")
