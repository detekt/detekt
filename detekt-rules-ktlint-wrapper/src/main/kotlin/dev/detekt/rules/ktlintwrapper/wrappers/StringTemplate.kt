package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.StringTemplateRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#string-template) for documentation.
 */
@ActiveByDefault(since = "1.0.0")
internal class StringTemplate(config: Config) :
    KtlintRule(config, "Detects simplifications in template strings"),
    AutoCorrectable {

    override val wrapping = StringTemplateRule()
}
