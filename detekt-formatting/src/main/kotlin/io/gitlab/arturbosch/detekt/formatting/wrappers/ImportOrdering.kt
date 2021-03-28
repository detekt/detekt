package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.FeatureInAlphaState
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.ImportOrderingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * For defining custom import layout patterns see: https://github.com/pinterest/ktlint/blob/cdf871b6f015359f9a6f02e15ef1b85a6c442437/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt
 *
 * @configuration layout - the import ordering layout; use 'ascii', 'idea' or define a custom one.
 * See the documentation on the Ktlint ImportOrderingRule for customized details.
 * https://github.com/pinterest/ktlint/blob/master/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt
 * (default: `'idea'`)
 *
 * @autoCorrect since v1.0.0
 */
@OptIn(FeatureInAlphaState::class)
class ImportOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ImportOrderingRule()
    override val issue = issueFor("Detects imports in non default order")

    private val layout: String = valueOrNull(LAYOUT_PATTERN) ?: chooseDefaultLayout()

    private fun chooseDefaultLayout() = if (isAndroid) ASCII else IDEA

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(ImportOrderingRule.ideaImportsLayoutProperty to layout)

    companion object {
        const val LAYOUT_PATTERN = "layout"
        const val ASCII = "ascii"
        const val IDEA = "idea"
    }
}
