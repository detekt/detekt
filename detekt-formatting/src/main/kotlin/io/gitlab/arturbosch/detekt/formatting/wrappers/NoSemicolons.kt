package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoSemicolonsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io/#rule-semi">ktlint-website</a> for documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoSemicolons(config: Config) : FormattingRule(config) {

    override val wrapping = NoSemicolonsRule()
    override val issue = issueFor("Detects semicolons")
}
