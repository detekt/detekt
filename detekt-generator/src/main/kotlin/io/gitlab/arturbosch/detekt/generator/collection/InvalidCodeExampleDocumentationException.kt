package io.gitlab.arturbosch.detekt.generator.collection

class InvalidCodeExampleDocumentationException(ruleName: String)
	: RuntimeException("Invalid rule documentation for noncompliant and compliant code examples in $ruleName.")
