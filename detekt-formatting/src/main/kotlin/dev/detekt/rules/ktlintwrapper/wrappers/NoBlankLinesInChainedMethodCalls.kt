package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoBlankLinesInChainedMethodCallsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-blank-lines-in-chained-method-calls) for
 * documentation.
 */
@ActiveByDefault(since = "1.22.0")
@AutoCorrectable(since = "1.22.0")
class NoBlankLinesInChainedMethodCalls(config: Config) : FormattingRule(
    config,
    "Detects blank lines in chained method rules."
) {

    override val wrapping = NoBlankLinesInChainedMethodCallsRule()
}
