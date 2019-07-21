package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingAroundKeywordRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class SpacingAroundKeyword(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingAroundKeywordRule()
    override val issue = issueFor("Reports spaces around keywords")
}
