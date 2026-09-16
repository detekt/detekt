package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoMultipleSpacesRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-multi-spaces) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoMultipleSpaces(config: Config) :
    KtlintRule(config, "Reports multiple space usages"),
    AutoCorrectable {

    override val wrapping = NoMultipleSpacesRule()
}
