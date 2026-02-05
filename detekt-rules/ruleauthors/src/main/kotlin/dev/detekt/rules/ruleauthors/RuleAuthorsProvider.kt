package dev.detekt.rules.ruleauthors

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

/**
 * The rule authors ruleset provides rules that ensures good practices when writing detekt rules.
 *
 * **Note: The `ruleauthors` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "dev.detekt:detekt-rules-ruleauthors:$version"`
 * to your Gradle `dependencies` or reference the `detekt-rules-ruleauthors`-jar with the `--plugins` option
 * in the command line interface.
 */
@ActiveByDefault("1.22.0")
class RuleAuthorsProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("ruleauthors")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::UseEntityAtName,
            )
        )
}
