package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.EnumEntryNameCaseRule
import com.pinterest.ktlint.ruleset.standard.rules.EnumEntryNameCaseRule.Companion.ENUM_ENTRY_NAME_CASING_PROPERTY
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#enum-entry) for documentation.
 */
@AutoCorrectable(since = "1.4.0")
@ActiveByDefault(since = "1.22.0")
@Alias("EnumEntryName")
class EnumEntryNameCase(config: Config) :
    KtlintRule(config, "Reports enum entries with names that don't meet standard conventions.") {

    override val wrapping = EnumEntryNameCaseRule()

    @Configuration("enum entry naming casing")
    private val enumEntryNameCasing by config("upper_or_camel_cases")

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            ENUM_ENTRY_NAME_CASING_PROPERTY to enumEntryNameCasing,
        )
}
