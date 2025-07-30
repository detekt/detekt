package dev.detekt.generator.collection.exception

class InvalidCodeExampleDocumentationException(ruleName: String) :
    RuntimeException("Invalid rule documentation for noncompliant and compliant code examples in $ruleName.")
