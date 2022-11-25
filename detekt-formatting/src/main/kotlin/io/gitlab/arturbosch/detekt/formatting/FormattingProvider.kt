package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.ruleSetConfig

/**
 * This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.
 *
 * **Note: The `formatting` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"`
 * to your gradle `dependencies` or reference the `detekt-formatting`-jar with the `--plugins` option
 * in the command line interface.
 *
 * Note: Issues reported by this rule set can only be suppressed on file level (`@file:Suppress("detekt.rule")`).
 */
@ActiveByDefault(since = "1.0.0")
class FormattingProvider : RuleSetProvider {

    override val ruleSetId: String = "formatting"

    override fun instance(config: Config) = RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))

    companion object {
        @Configuration("if android style guides should be preferred")
        val android by ruleSetConfig(false)

        @Configuration("if rules should auto correct style violation")
        val autoCorrect by ruleSetConfig(true)
    }
}
