package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import java.net.URI

/**
 * Rules in this rule set report issues related to libraries API exposure.
 *
 * **Note: The `libraries` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-rules-libraries:$version"`
 * to your Gradle `dependencies` or reference the `detekt-rules-libraries`-jar with the `--plugins` option
 * in the command line interface.
 */
@ActiveByDefault("1.16.0")
class RuleLibrariesProvider : RuleSetProvider {

    override val ruleSetId = RuleSet.Id(RULE_SET_ID)

    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            ::ForbiddenPublicDataClass,
            ::LibraryEntitiesShouldNotBePublic,
            ::LibraryCodeMustSpecifyReturnType,
        )
    )
}

internal fun generateRuleUrl(ruleName: String) =
    URI("https://detekt.dev/docs/rules/$RULE_SET_ID#${ruleName.lowercase()}")

private const val RULE_SET_ID = "libraries"
