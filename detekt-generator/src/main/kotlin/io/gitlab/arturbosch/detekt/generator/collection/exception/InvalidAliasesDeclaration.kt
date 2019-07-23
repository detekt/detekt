package io.gitlab.arturbosch.detekt.generator.collection.exception

class InvalidAliasesDeclaration : RuntimeException(
        "Invalid aliases declaration. Example: override val defaultRuleIdAliases = setOf(\"Name1\", \"Name2\")"
)
