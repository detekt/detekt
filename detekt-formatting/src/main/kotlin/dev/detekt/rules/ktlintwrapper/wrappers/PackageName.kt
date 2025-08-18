package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.PackageNameRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#package-name) for
 * documentation.
 */
@ActiveByDefault(since = "1.22.0")
class PackageName(config: Config) : FormattingRule(
    config,
    "Checks package name is formatted correctly"
) {

    override val wrapping = PackageNameRule()
}
