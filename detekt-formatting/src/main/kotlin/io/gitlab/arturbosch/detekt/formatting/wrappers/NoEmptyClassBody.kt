package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoEmptyClassBodyRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-empty-class-body">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class NoEmptyClassBody(config: Config) : FormattingRule(config) {

    override val wrapping = NoEmptyClassBodyRule()
    override val issue = issueFor("Reports empty class bodies")
}
