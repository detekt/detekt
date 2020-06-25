package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.IssuesSpec
import io.github.detekt.tooling.api.spec.MaxIssuePolicy

class IssuesSpecBuilder : Builder<IssuesSpec> {

    var activateExperimentalRules: Boolean = false
    var policy: MaxIssuePolicy = MaxIssuePolicy.NoneAllowed()
    var autoCorrect: Boolean = false

    override fun build(): IssuesSpec = IssuesModel(activateExperimentalRules, policy, autoCorrect)
}

internal data class IssuesModel(
    override val activateExperimentalRules: Boolean,
    override val policy: MaxIssuePolicy,
    override val autoCorrect: Boolean,
) : IssuesSpec
