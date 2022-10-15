package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault

/**
 * Rules in this rule set report issues related to libraries API exposure.
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
