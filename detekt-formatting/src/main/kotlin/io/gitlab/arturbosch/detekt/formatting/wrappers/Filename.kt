package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.FilenameRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#file-name) for documentation.
 *
 * This rules overlaps with [naming>MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
class Filename(config: Config) : FormattingRule(config) {

    override val wrapping = FilenameRule()
    override val issue = issueFor("Checks if top level class matches the filename")

    override fun canBeCorrectedByKtLint(message: String): Boolean = false
}
