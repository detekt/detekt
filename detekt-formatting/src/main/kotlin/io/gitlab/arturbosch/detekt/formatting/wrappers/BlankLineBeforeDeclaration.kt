package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBeforeDeclarationRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-before-declarations) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "2.0.0")
class BlankLineBeforeDeclaration(config: Config) : FormattingRule(
    config,
    "A blank line is required before any class or function declaration, and before any list of top level or " +
        "class properties."
) {

    override val wrapping = BlankLineBeforeDeclarationRule()
}
