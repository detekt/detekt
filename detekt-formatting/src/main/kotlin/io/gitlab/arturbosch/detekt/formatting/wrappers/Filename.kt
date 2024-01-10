package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FilenameRule
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#file-name) for documentation.
 *
 * This rules overlaps with [naming>MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
class Filename(config: Config) : FormattingRule(
    config,
    "Checks if top level class matches the filename"
) {

    override val wrapping = FilenameRule()
}
