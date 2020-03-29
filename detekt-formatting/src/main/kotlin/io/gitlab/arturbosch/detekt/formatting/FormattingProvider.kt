package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.
 *
 * Note: Issues reported by this rule set can only be suppressed on file level (@file:Suppress("detekt.rule").
 * Note: The formatting rule set is not included in the detekt-cli or gradle plugin.
 *
 * To enable this rule set, add <i>detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"</i>
 * to your gradle dependencies or reference the `detekt-formatting`-jar with the `--plugins` option
 * in the command line interface.
 *
 * @configuration android - if android style guides should be preferred (default: `false`)
 * @configuration autoCorrect - if rules should auto correct style violation (default: `true`)
 * @active since v1.0.0
 */
class FormattingProvider : RuleSetProvider {

    override val ruleSetId: String = "formatting"

    override fun instance(config: Config) =
            RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))
}
