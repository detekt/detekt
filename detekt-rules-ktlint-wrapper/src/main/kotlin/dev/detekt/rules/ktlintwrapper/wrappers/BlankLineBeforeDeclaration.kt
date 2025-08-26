package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBeforeDeclarationRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-before-declarations) for
 * documentation.
 */
@ActiveByDefault(since = "2.0.0")
@AutoCorrectable(since = "2.0.0")
class BlankLineBeforeDeclaration(config: Config) : KtlintRule(
    config,
    "A blank line is required before any class or function declaration, and before any list of top level or " +
        "class properties."
) {

    override val wrapping = BlankLineBeforeDeclarationRule()
}
