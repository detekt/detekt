package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage

object DeprecatedPrinter : DocumentationPrinter<List<RuleSetPage>> {
    @Suppress("NestedBlockDepth")
    override fun print(item: List<RuleSetPage>): String {
        return item.flatMap { ruleSet ->
            ruleSet.rules.flatMap { rule ->
                buildList {
                    if (rule.isDeprecated()) {
                        add(writeRule(ruleSet, rule))
                    }
                    addAll(
                        rule.configurations
                            .filter { it.isDeprecated() }
                            .map { writeProperty(ruleSet, rule, it) }
                    )
                }
            }
        }
            .plus(migratedRules())
            .sorted()
            .joinToString("\n", postfix = "\n")
    }
}

private fun writeRule(ruleSet: RuleSetPage, rule: Rule): String {
    @Suppress("UnsafeCallOnNullableType")
    return "${ruleSet.ruleSet.name}>${rule.name}=${rule.deprecationMessage!!}"
}

private fun writeProperty(ruleSet: RuleSetPage, rule: Rule, configuration: Configuration): String {
    @Suppress("UnsafeCallOnNullableType")
    return "${ruleSet.ruleSet.name}>${rule.name}>${configuration.name}=${configuration.deprecated!!}"
}

@Suppress("MaxLineLength")
internal fun migratedRules() = listOf(
    "formatting>TrailingComma=Rule is split between `TrailingCommaOnCallSite` and `TrailingCommaOnDeclarationSite` now.",
    "style>ForbiddenPublicDataClass=Rule migrated to `libraries` ruleset plugin",
    "style>LibraryCodeMustSpecifyReturnType=Rule migrated to `libraries` ruleset plugin",
    "style>LibraryEntitiesShouldNotBePublic=Rule migrated to `libraries` ruleset plugin",
    "style>MandatoryBracesIfStatements=Use `BracesOnIfStatements` with `always` configuration instead",
    "complexity>ComplexMethod=Rule is renamed to `CyclomaticComplexMethod` to distinguish between Cyclomatic Complexity and Cognitive Complexity",
)
