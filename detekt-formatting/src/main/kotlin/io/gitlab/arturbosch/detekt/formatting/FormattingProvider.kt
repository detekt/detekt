package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

/**
 * This rule set provides rules that address formatting issues.
 *
 * Note: The formatting rule set is not included in the detekt-cli or gradle plugin.
 *
 * To enable this rule set, add <i>detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"</i>
 * to your gradle dependencies or reference the `detekt-formatting`-jar with the `--plugins` option
 * in the command line interface.
 *
 * @configuration android - if android style guides should be preferred (default: false)
 * @configuration autoCorrect - if rules should auto correct style violation (default: true)
 * @active since v1.0.0
 * @author Artur Bosch
 */
class FormattingProvider : RuleSetProvider {

    override val ruleSetId: String = "formatting"

    override fun instance(config: Config) =
            RuleSet(ruleSetId, listOf(KtLintMultiRule(config)))
}
