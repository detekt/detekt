package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.FilenameRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @active since v1.0.0
 */
class Filename(config: Config) : FormattingRule(config) {

    override val wrapping = FilenameRule()
    override val issue = issueFor("Checks if top level class matches the filename")
}
