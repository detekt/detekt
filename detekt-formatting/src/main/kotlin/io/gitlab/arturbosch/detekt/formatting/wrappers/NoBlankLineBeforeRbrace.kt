package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoBlankLineBeforeRbraceRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoBlankLineBeforeRbrace(config: Config) : FormattingRule(config) {

    override val wrapping = NoBlankLineBeforeRbraceRule()
    override val issue = issueFor("Detects blank lines before rbraces")
}
