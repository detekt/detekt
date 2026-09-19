package dev.detekt.rules.ktlintwrapper.wrappers

import io.github.ktlint.core.rule.engine.core.api.editorconfig.EditorConfigProperty
import io.github.ktlint.core.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import io.github.ktlint.core.ruleset.standard.rules.NoSingleLineBlockCommentRule
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#no-single-line-block-comment) for documentation.
 */
internal class NoSingleLineBlockComment(config: Config) :
    KtlintRule(config, "Reports single block line comments"),
    AutoCorrectable {

    override val wrapping = NoSingleLineBlockCommentRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
        )
}
