package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault

/**
 * Rules in this rule set report issues related to libraries API exposure.
 * 
 * Note: The `libraries` rule set is not included in the detekt-cli or Gradle plugin.
 * 
 * To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-rules-libraries:$version"`
 * to your Gradle `dependencies` or reference the `detekt-rules-libraries`-jar with the `--plugins` option
 * in the command line interface.
 */
@ActiveByDefault("1.16.0")
class RuleLibrariesProvider : RuleSetProvider {

    override val ruleSetId: String = "libraries"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            ForbiddenPublicDataClass(config),
            LibraryEntitiesShouldNotBePublic(config),
            LibraryCodeMustSpecifyReturnType(config),
        )
    )
}
