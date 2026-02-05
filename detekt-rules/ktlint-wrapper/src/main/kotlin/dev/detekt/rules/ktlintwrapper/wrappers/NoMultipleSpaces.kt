package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoMultipleSpacesRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-multi-spaces) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class NoMultipleSpaces(config: Config) : KtlintRule(config, "Reports multiple space usages") {

    override val wrapping = NoMultipleSpacesRule()
}
