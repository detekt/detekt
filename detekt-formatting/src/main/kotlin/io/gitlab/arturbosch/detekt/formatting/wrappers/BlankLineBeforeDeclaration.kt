package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBeforeDeclarationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-before-declarations) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "2.0.0")
class BlankLineBeforeDeclaration(config: Config) : FormattingRule(config) {

    override val wrapping = BlankLineBeforeDeclarationRule()
    override val issue =
        issueFor(
            "A blank line is required before any class or function declaration, and before any list of top level or " +
                "class properties."
        )
}
