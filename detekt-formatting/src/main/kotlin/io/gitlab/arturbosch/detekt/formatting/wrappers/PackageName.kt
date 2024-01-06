package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.rules.PackageNameRule
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#package-name) for
 * documentation.
 */
@ActiveByDefault(since = "1.22.0")
class PackageName(config: Config) : FormattingRule(config) {

    override val wrapping = PackageNameRule()
    override val issue = issueFor("Checks package name is formatted correctly")
}
