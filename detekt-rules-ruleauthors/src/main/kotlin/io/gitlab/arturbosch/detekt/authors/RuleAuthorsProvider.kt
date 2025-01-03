package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.net.URI

/**
 * The rule authors ruleset provides rules that ensures good practices when writing detekt rules.
 *
 * **Note: The `ruleauthors` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:$version"`
 * to your Gradle `dependencies` or reference the `detekt-rules-ruleauthors`-jar with the `--plugins` option
 * in the command line interface.
 */
@ActiveByDefault("1.22.0")
class RuleAuthorsProvider : RuleSetProvider {

    override val ruleSetId = RuleSet.Id(RULE_SET_ID)

    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            ::ViolatesTypeResolutionRequirements,
            ::UseEntityAtName,
        )
    )
}

internal fun generateRuleUrl(ruleName: String) =
    URI("https://detekt.dev/docs/rules/$RULE_SET_ID#${ruleName.lowercase()}")

private const val RULE_SET_ID = "ruleauthors"
