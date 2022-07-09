package io.gitlab.arturbosch.detekt.formatting.wrappers

import com.pinterest.ktlint.ruleset.standard.WrappingRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.
 */
@ActiveByDefault(since = "1.20.0")
@AutoCorrectable(since = "1.20.0")
class Wrapping(config: Config) : FormattingRule(config) {

    override val wrapping = WrappingRule()
    override val issue = issueFor("Reports missing newlines (e.g. between parentheses of a multi-line function call")

    /**
     * [Wrapping] has visitor modifier RunOnRootNodeOnly, so [node] is always the root file.
     * Override the parent implementation to highlight the entire file.
     */
    override fun getTextLocationForViolation(node: ASTNode, offset: Int): TextLocation {
        // Use offset + 1 since Wrapping always reports the location of missing new line.
        return TextLocation(offset, offset + 1)
    }
}
