package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.EnumEntryNameCaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.4.0
 */
class EnumEntryNameCase(config: Config) : FormattingRule(config) {

    override val wrapping = EnumEntryNameCaseRule()
    override val issue = issueFor("Reports enum entries with names that don't meet standard conventions.")
}
