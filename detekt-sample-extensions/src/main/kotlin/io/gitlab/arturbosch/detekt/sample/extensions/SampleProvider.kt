package io.gitlab.arturbosch.detekt.sample.extensions

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.sample.extensions.rules.TooManyFunctions
import io.gitlab.arturbosch.detekt.sample.extensions.rules.TooManyFunctionsTwo

class SampleProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("sample")

    override fun instance(): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                ::TooManyFunctions,
                ::TooManyFunctionsTwo,
            )
        )
}
