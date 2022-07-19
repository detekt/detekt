package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.core.api.DefaultEditorConfigProperties
import com.pinterest.ktlint.core.api.UsesEditorConfigProperties
import com.pinterest.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.
 */
@ActiveByDefault(since = "1.19.0")
@AutoCorrectable(since = "1.0.0")
class Indentation(config: Config) : FormattingRule(config) {

    override val wrapping = IndentationRule()
    override val issue = issueFor("Reports mis-indented code")

    @Configuration("indentation size")
    private val indentSize by config(4)

    @Configuration("continuation indentation size")
    @Deprecated("`continuationIndentSize` is ignored by KtLint and will have no effect")
    @Suppress("UnusedPrivateMember")
    private val continuationIndentSize by config(4)

    override fun overrideEditorConfigProperties(): Map<UsesEditorConfigProperties.EditorConfigProperty<*>, String> =
        mapOf(
            DefaultEditorConfigProperties.indentSizeProperty to indentSize.toString(),
        )

    /**
     * [IndentationRule] has visitor modifier RunOnRootNodeOnly, so [node] is always the root file.
     * Override the parent implementation to highlight the entire file.
     */
    override fun getTextLocationForViolation(node: ASTNode, offset: Int): TextLocation {
        val relativeEnd = node.text
            .drop(offset)
            .indexOfFirst { !it.isWhitespace() }
        return TextLocation(offset, offset + relativeEnd)
    }
}
