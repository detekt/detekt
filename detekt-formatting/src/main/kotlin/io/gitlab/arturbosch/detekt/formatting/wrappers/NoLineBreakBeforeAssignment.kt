package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoLineBreakBeforeAssignmentRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 * @autoCorrect since v1.0.0
 */
class NoLineBreakBeforeAssignment(config: Config) : FormattingRule(config) {

    override val wrapping = NoLineBreakBeforeAssignmentRule()
    override val issue = issueFor("Reports line breaks before assignment")
}
