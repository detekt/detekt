package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ImportOrderingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @author Artur Bosch
 */
class ImportOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ImportOrderingRule()
    override val issue = issueFor("Detects imports in non default order")
}
