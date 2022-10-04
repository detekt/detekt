package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.EnumEntryNameCaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/rules/standard/#enum-entry) for documentation.
 */
@AutoCorrectable(since = "1.4.0")
@ActiveByDefault(since = "1.22.0")
class EnumEntryNameCase(config: Config) : FormattingRule(config) {

    override val wrapping = EnumEntryNameCaseRule()
    override val issue = issueFor("Reports enum entries with names that don't meet standard conventions.")

    override fun canBeCorrectedByKtLint(message: String): Boolean = false
}
