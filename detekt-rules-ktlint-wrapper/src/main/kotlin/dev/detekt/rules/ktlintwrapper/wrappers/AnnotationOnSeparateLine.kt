package dev.detekt.rules.ktlintwrapper.wrappers

import io.github.ktlint.core.rule.engine.core.api.editorconfig.EditorConfigProperty
import io.github.ktlint.core.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import io.github.ktlint.core.ruleset.standard.rules.AnnotationRule
import io.github.ktlint.core.ruleset.standard.rules.AnnotationRule.Companion.ANNOTATIONS_WITH_PARAMETERS_NOT_TO_BE_WRAPPED_PROPERTY
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.AutoCorrectable
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.config
import dev.detekt.rules.ktlintwrapper.KtlintRule

/**
 * See [ktlint docs](https://ktlint.github.io/ktlint/<ktlintVersion/>/rules/standard/#annotation-formatting) for documentation.
 */
@ActiveByDefault(since = "1.22.0")
internal class AnnotationOnSeparateLine(config: Config) :
    KtlintRule(config, "Multiple annotations should be placed on separate lines."),
    AutoCorrectable {

    override val wrapping = AnnotationRule()

    @Configuration("indentation size")
    private val indentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<EditorConfigProperty<*>, String> =
        mapOf(
            INDENT_SIZE_PROPERTY to indentSize.toString(),
            ANNOTATIONS_WITH_PARAMETERS_NOT_TO_BE_WRAPPED_PROPERTY to "",
        )
}
