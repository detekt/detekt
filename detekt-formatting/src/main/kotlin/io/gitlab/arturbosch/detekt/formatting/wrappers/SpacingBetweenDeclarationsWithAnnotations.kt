package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.SpacingBetweenDeclarationsWithAnnotationsRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint-website](https://ktlint.github.io#rule-spacing) for documentation.
 */
@AutoCorrectable(since = "1.10.0")
@ActiveByDefault(since = "1.21.0")
class SpacingBetweenDeclarationsWithAnnotations(config: Config) : FormattingRule(config) {

    override val wrapping = SpacingBetweenDeclarationsWithAnnotationsRule()
    override val issue = issueFor(
        // message reported by the KtLint rule
        "Declarations and declarations with annotations should have an empty space between."
    )
}
