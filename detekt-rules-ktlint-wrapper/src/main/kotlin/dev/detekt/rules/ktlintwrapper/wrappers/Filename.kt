package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.FilenameRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#file-name) for documentation.
 *
 * This rules overlaps with [naming>MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
 * from the standard rules, make sure to enable just one.
 */
@ActiveByDefault(since = "1.0.0")
internal class Filename(config: Config) : KtlintRule(config, "Checks if top level class matches the filename") {

    override val wrapping = FilenameRule()
}
