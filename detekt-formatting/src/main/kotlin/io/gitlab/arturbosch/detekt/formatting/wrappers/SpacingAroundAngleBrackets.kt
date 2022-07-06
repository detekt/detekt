package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingAroundAngleBracketsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@ActiveByDefault(since = "1.21.0")
@AutoCorrectable(since = "1.16.0")
class SpacingAroundAngleBrackets(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundAngleBracketsRule()
    override val issue = issueFor("Reports spaces around angle brackets")
}
