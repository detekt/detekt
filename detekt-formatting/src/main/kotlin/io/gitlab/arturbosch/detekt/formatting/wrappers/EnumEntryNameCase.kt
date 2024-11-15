package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.EnumEntryNameCaseRule
import com.pinterest.ktlint.ruleset.standard.rules.EnumEntryNameCaseRule.Companion.ENUM_ENTRY_NAME_CASING_PROPERTY
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#enum-entry) for documentation.
 */
@AutoCorrectable(since = "1.4.0")
@ActiveByDefault(since = "1.22.0")
@Alias("EnumEntryName")
class EnumEntryNameCase(config: Config) : FormattingRule(
    config,
    "Reports enum entries with names that don't meet standard conventions."
) {

    override val wrapping = EnumEntryNameCaseRule()

    @Configuration("enum entry naming casing")
    private val enumEntryNameCasing by config("upper_or_camel_cases")

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            ENUM_ENTRY_NAME_CASING_PROPERTY to enumEntryNameCasing,
        )
}
