package dev.detekt.rules.ktlintwrapper.wrappers

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.KtlintRule
import io.github.ktlint.core.ruleset.standard.rules.ClassNamingRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#class-naming) for
 * documentation.
 */
internal class ClassName(config: Config) :
    KtlintRule(config, "Class or object name should start with an uppercase letter and use camel case.") {
    override val wrapping = ClassNamingRule()
}
