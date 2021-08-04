package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.ImportOrderingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * For defining import layout patterns see:
 * https://github.com/pinterest/ktlint/blob/a6ca5b2edf95cc70a138a9470cfb6c4fd5d9d3ce/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt
 */
@OptIn(FeatureInAlphaState::class)
@AutoCorrectable(since = "1.0.0")
class ImportOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ImportOrderingRule()
    override val issue = issueFor("Detects imports in non default order")

    @Configuration("the import ordering layout")
    private val layout: String by configWithAndroidVariants(IDEA_PATTERN, ASCII_PATTERN)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(ImportOrderingRule.ideaImportsLayoutProperty to layout)

    companion object {
        const val ASCII_PATTERN = "*"
        const val IDEA_PATTERN = "*,java.**,javax.**,kotlin.**,^"
    }
}
