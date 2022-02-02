package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.IndentationRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.formatting.INDENT_SIZE_KEY
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.
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
    private val continuationIndentSize by config(4)

    override fun overrideEditorConfig() = mapOf(
        INDENT_SIZE_KEY to indentSize,
    )

    /**
     * [wrapping] is working with file's [node] and we don't want to highlight the whole file
     */
    override fun getTextLocationForViolation(node: ASTNode, offset: Int): TextLocation {
        val relativeEnd = node.text
            .drop(offset)
            .indexOfFirst { !it.isWhitespace() }
        return TextLocation(offset, offset + relativeEnd)
    }
}
