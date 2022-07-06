package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.NoBlankLinesInChainedMethodCallsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io) for documentation.
 */
@ActiveByDefault(since = "1.21.0")
@AutoCorrectable(since = "1.21.0")
class NoBlankLinesInChainedMethodCalls(config: Config) : FormattingRule(config) {

    override val wrapping = NoBlankLinesInChainedMethodCallsRule()
    override val issue = issueFor("Detects blank lines in chained method rules.")
}
