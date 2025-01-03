package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBetweenWhenConditions
import com.pinterest.ktlint.ruleset.standard.rules.BlankLineBetweenWhenConditions.Companion.LINE_BREAK_AFTER_WHEN_CONDITION_PROPERTY
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/experimental/#blank-lines-between-when-conditions)
 * for documentation.
 */
@AutoCorrectable(since = "2.0.0")
class BlankLineBetweenWhenConditions(config: Config) : FormattingRule(
    config,
    "Consistently add or remove blank lines between when-conditions in a when-statement"
) {

    override val wrapping = BlankLineBetweenWhenConditions()

    @Configuration("require line breaks after multiline entries")
    private val lineBreakAfterWhenEntries: Boolean by config(true)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            LINE_BREAK_AFTER_WHEN_CONDITION_PROPERTY to lineBreakAfterWhenEntries.toString()
        )
}
