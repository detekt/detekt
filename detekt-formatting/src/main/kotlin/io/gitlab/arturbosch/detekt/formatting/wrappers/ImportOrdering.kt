package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.ImportOrderingRule
import com.pinterest.ktlint.ruleset.standard.rules.ImportOrderingRule.Companion.IJ_KOTLIN_IMPORTS_LAYOUT_PROPERTY
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.configWithAndroidVariants
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#import-ordering) for documentation.
 *
 * For defining import layout patterns see the [KtLint Source Code](https://github.com/pinterest/ktlint/blob/0.50.0/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/rules/ImportOrderingRule.kt)
 */
@ActiveByDefault(since = "1.19.0")
@AutoCorrectable(since = "1.0.0")
class ImportOrdering(config: Config) : FormattingRule(config) {

    override val wrapping = ImportOrderingRule()
    override val issue = issueFor("Detects imports in non default order")

    @Configuration("the import ordering layout")
    private val layout: String by configWithAndroidVariants(IDEA_PATTERN, ASCII_PATTERN)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(IJ_KOTLIN_IMPORTS_LAYOUT_PROPERTY to layout)

    companion object {
        const val ASCII_PATTERN = "*"
        const val IDEA_PATTERN = "*,java.**,javax.**,kotlin.**,^"
    }
}
