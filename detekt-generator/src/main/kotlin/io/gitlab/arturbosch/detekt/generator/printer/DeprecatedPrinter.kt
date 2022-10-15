package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.collection.Rule
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage

object DeprecatedPrinter : DocumentationPrinter<List<RuleSetPage>> {
    @Suppress("NestedBlockDepth")
    override fun print(item: List<RuleSetPage>): String {
        val builder = StringBuilder()
        item.forEach { ruleSet ->
            ruleSet.rules.forEach { rule ->
                if (rule.isDeprecated()) {
                    builder.appendLine(writeRule(ruleSet, rule))
                }
                rule.configuration.forEach { configuration ->
                    if (configuration.isDeprecated()) {
                        builder.appendLine(writeProperty(ruleSet, rule, configuration))
                    }
                }
            }
        }
        builder.appendLine(writeMigratedRules())
        return builder.toString()
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

internal fun writeMigratedRules(): String {
    return """
        style>ForbiddenPublicDataClass=Rule migrated to `libraries` ruleset plugin
        style>LibraryCodeMustSpecifyReturnType=Rule migrated to `libraries` ruleset plugin
        style>LibraryEntitiesShouldNotBePublic=Rule migrated to `libraries` ruleset plugin
    """.trimIndent()
}
