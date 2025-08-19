package dev.detekt.rules.ktlintwrapper.wrappers

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfigProperty
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INSERT_FINAL_NEWLINE_PROPERTY
import com.pinterest.ktlint.ruleset.standard.rules.FinalNewlineRule
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.api.internal.AutoCorrectable
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://pinterest.github.io/ktlint/<ktlintVersion/>/rules/standard/#final-newline) for documentation.
 *
 * This rules overlaps with [style>NewLineAtEndOfFile](https://detekt.dev/style.html#newlineatendoffile)
 * from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.
 */
@ActiveByDefault(since = "1.0.0")
@AutoCorrectable(since = "1.0.0")
class FinalNewline(config: Config) : KtlintRule(
    config,
    "Detects missing final newlines"
) {

    override val wrapping = FinalNewlineRule()

    @Configuration("report absence or presence of a newline")
    private val insertFinalNewLine by config(true)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(INSERT_FINAL_NEWLINE_PROPERTY to insertFinalNewLine.toString())
}
