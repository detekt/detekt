package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.ImportOrderingRule
import com.pinterest.ktlint.ruleset.standard.rules.ImportOrderingRule.Companion.IJ_KOTLIN_IMPORTS_LAYOUT_PROPERTY
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.configWithAndroidVariants
import dev.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#import-ordering) for documentation.
 *
 * For defining import layout patterns see the [KtLint Source Code](https://github.com/pinterest/ktlint/blob/0.50.0/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/rules/ImportOrderingRule.kt)
 */
@ActiveByDefault(since = "1.19.0")
@AutoCorrectable(since = "1.0.0")
class ImportOrdering(config: Config) : FormattingRule(
    config,
    "Detects imports in non default order"
) {

    override val wrapping = ImportOrderingRule()

    @Configuration("the import ordering layout")
    private val layout: String by configWithAndroidVariants(IDEA_PATTERN, ASCII_PATTERN)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(IJ_KOTLIN_IMPORTS_LAYOUT_PROPERTY to layout)

    companion object {
        const val ASCII_PATTERN = "*"
        const val IDEA_PATTERN = "*,java.**,javax.**,kotlin.**,^"
    }
}
