package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.TypeParameterListSpacingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/experimental/#type-parameter-list-spacing) for
 * documentation.
 */
@AutoCorrectable(since = "1.22.0")
class TypeParameterListSpacing(config: Config) : FormattingRule(config) {

    override val wrapping = TypeParameterListSpacingRule()
    override val issue = issueFor("Check spacing after a type parameter list in function and class declarations.")
}
