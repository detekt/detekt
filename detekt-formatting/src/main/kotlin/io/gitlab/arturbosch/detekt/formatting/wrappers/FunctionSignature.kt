package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.experimental.FunctionSignatureRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.21.0")
class FunctionSignature(config: Config) : FormattingRule(config) {

    override val wrapping = FunctionSignatureRule()
    override val issue = issueFor(
        "Rewrites the function signature to a single line when possible or a multiline signature otherwise."
    )
}
