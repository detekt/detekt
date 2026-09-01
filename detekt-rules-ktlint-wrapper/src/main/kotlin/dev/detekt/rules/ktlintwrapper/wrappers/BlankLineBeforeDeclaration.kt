package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBeforeDeclarationRule
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#blank-line-before-declarations) for
 * documentation.
 */
internal class BlankLineBeforeDeclaration(config: Config) :
    KtlintRule(
        config,
        "A blank line is required before any class or function declaration, and before any list of top level or " +
            "class properties."
    ),
    AutoCorrectable {

    override val wrapping = BlankLineBeforeDeclarationRule()
}
