package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.MultiLineIfElseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.
 */
@AutoCorrectable(since = "1.0.0")
class MultiLineIfElse(config: Config) : FormattingRule(config) {

    override val wrapping = MultiLineIfElseRule()
    override val issue = issueFor("Detects multiline if-else statements without braces")
}
