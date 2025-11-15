package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoEmptyFileRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-empty-file) for documentation.
 */
@ActiveByDefault(since = "2.0.0")
class NoEmptyFile(config: Config) : KtlintRule(config, "Kotlin files must contain at least one declaration") {

    override val wrapping = NoEmptyFileRule()
}
