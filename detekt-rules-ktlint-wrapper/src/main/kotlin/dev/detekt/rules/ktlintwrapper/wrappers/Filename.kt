package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.FilenameRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#file-name) for documentation.
 *
 * This rules overlaps with [naming>MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
class Filename(config: Config) : KtlintRule(config, "Checks if top level class matches the filename") {

    override val wrapping = FilenameRule()
}
