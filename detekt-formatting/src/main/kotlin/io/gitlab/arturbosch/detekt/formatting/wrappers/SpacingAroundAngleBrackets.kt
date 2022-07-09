package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.SpacingAroundAngleBracketsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.
 */
@AutoCorrectable(since = "1.16.0")
class SpacingAroundAngleBrackets(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundAngleBracketsRule()
    override val issue = issueFor("Reports spaces around angle brackets")
}
