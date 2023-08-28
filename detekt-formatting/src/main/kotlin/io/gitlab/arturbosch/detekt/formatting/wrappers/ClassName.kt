package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.ClassNamingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#classobject-naming) for
 * documentation.
 */
class ClassName(config: Config) : FormattingRule(config) {
    override val wrapping = ClassNamingRule()
    override val issue =
        issueFor("Class or object name should start with an uppercase letter and use camel case.")
}
