package io.gitlab.arturbosch.detekt.generator.collection.exception

class InvalidIssueDeclaration(attributeName: String) :
    RuntimeException("Invalid issue declaration attribute $attributeName.")
