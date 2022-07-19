package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingAroundUnaryOperatorRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.
 */
@AutoCorrectable(since = "1.16.0")
@ActiveByDefault(since = "1.22.0")
class SpacingAroundUnaryOperator(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundUnaryOperatorRule()
    override val issue = issueFor("Reports spaces around unary operator")
}
