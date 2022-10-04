package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.PackageNameRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#no-underscores-in-package-names) for
 * documentation.
 */
@AutoCorrectable(since = "1.0.0")
@ActiveByDefault(since = "1.22.0")
class PackageName(config: Config) : FormattingRule(config) {

    override val wrapping = PackageNameRule()
    override val issue = issueFor("Checks package name is formatted correctly")

    override fun canBeCorrectedByKtLint(message: String): Boolean = false
}
