package dev.detekt.rules.libraries

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

/**
 * Rules in this rule set report issues related to libraries API exposure.
 *
 * **Note: The `libraries` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "dev.detekt:detekt-rules-libraries:$version"`
 * to your Gradle `dependencies` or reference the `detekt-rules-libraries`-jar with the `--plugins` option
 * in the command line interface.
 */
@ActiveByDefault("1.16.0")
class RuleLibrariesProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("libraries")

    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            ::ForbiddenPublicDataClass,
            ::LibraryEntitiesShouldNotBePublic,
            ::LibraryCodeMustSpecifyReturnType,
        )
    )
}
