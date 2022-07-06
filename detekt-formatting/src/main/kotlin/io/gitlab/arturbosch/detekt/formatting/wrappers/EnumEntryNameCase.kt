package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.EnumEntryNameCaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io) for documentation.
 */
@ActiveByDefault(since = "1.21.0")
@AutoCorrectable(since = "1.4.0")
class EnumEntryNameCase(config: Config) : FormattingRule(config) {

    override val wrapping = EnumEntryNameCaseRule()
    override val issue = issueFor("Reports enum entries with names that don't meet standard conventions.")
}
