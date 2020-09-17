package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.ArgumentListWrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * @autoCorrect since v1.0.0
 */
class ArgumentListWrapping(config: Config) : FormattingRule(config) {

    override val wrapping = ArgumentListWrappingRule()
    override val issue = issueFor("Reports incorrect argument list wrapping")
}
