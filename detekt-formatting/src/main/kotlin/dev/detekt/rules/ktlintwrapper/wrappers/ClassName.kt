package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ClassNamingRule
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#class-naming) for
 * documentation.
 */
class ClassName(config: Config) : FormattingRule(
    config,
    "Class or object name should start with an uppercase letter and use camel case."
) {
    override val wrapping = ClassNamingRule()
}
