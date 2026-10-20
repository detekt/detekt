@file:Suppress("DEPRECATION")

package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.NoUnusedImportsRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-unused-imports) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class NoUnusedImports(config: Config) :
    KtlintRule(config, "Detects unused imports"),
    AutoCorrectable {

    override val wrapping = NoUnusedImportsRule()
}
