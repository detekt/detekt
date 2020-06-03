package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.EditorConfig
import com.pinterest.ktlint.ruleset.standard.ImportOrderingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.KOTLIN_IMPORTS_LAYOUT_KEY
import io.gitlab.arturbosch.detekt.formatting.copy

/**
 * See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.
 *
 * For defining custom import layout patterns see: https://github.com/pinterest/ktlint/blob/cdf871b6f015359f9a6f02e15ef1b85a6c442437/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt
 *
 * @configuration layout - the import ordering layout; use 'ascii', 'idea' or define a custom one (default: `'idea'`)
 *
 * @autoCorrect since v1.0.0
 */
class ImportOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ImportOrderingRule()
    override val issue = issueFor("Detects imports in non default order")

    private val layout: String = valueOrNull(LAYOUT_PATTERN) ?: chooseDefaultLayout()

    private fun chooseDefaultLayout() = if (isAndroid) ASCII else IDEA

    override fun editorConfigUpdater(): ((oldEditorConfig: EditorConfig?) -> EditorConfig)? =
        { it.copy(KOTLIN_IMPORTS_LAYOUT_KEY to layout) }

    companion object {
        const val LAYOUT_PATTERN = "layout"
        const val ASCII = "ascii"
        const val IDEA = "idea"
    }
}
