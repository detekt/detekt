package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationOnSeparateLine
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.FinalNewline
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultiLineIfElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLineBeforeRbrace
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoConsecutiveBlankLines
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyClassBody
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyFirstLineInMethodBlock
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakAfterElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoMultipleSpaces
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoSemicolons
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoTrailingSpaces
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoUnitReturn
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoUnusedImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundColon
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundComma
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundCurly
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundDot
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundKeyword
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundOperators
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundParens
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundRangeOperator
import io.gitlab.arturbosch.detekt.formatting.wrappers.StringTemplate
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyHolder
import org.jetbrains.kotlin.psi.KtFile

/**
 * Runs all KtLint rules.
 */
class KtLintMultiRule(config: Config = Config.empty) : MultiRule() {

    override val rules: List<Rule> = listOf(
        AnnotationOnSeparateLine(config),
        ChainWrapping(config),
        CommentSpacing(config),
        EnumEntryNameCase(config),
        Filename(config),
        FinalNewline(config),
        ImportOrdering(config),
        Indentation(config),
        MaximumLineLength(config),
        ModifierOrdering(config),
        MultiLineIfElse(config),
        NoBlankLineBeforeRbrace(config),
        NoConsecutiveBlankLines(config),
        NoEmptyClassBody(config),
        NoEmptyFirstLineInMethodBlock(config),
        NoLineBreakAfterElse(config),
        NoLineBreakBeforeAssignment(config),
        NoMultipleSpaces(config),
        NoSemicolons(config),
        NoTrailingSpaces(config),
        NoUnitReturn(config),
        NoUnusedImports(config),
        NoWildcardImports(config),
        PackageName(config),
        ParameterListWrapping(config),
        SpacingAroundColon(config),
        SpacingAroundComma(config),
        SpacingAroundCurly(config),
        SpacingAroundDot(config),
        SpacingAroundKeyword(config),
        SpacingAroundOperators(config),
        SpacingAroundParens(config),
        SpacingAroundRangeOperator(config),
        StringTemplate(config)
    )

    override fun visit(root: KtFile) {
        val sortedRules = activeRules.sortedBy { it.lastModifier() }
        sortedRules.forEach { it.visit(root) }
        root.node.visitTokens { node ->
            sortedRules.forEach { rule ->
                (rule as? FormattingRule)?.runIfActive { this.apply(node) }
            }
        }
    }

    private fun Rule.lastModifier(): Boolean {
        val rule = (this as? FormattingRule)?.wrapping ?: return false
        return rule is com.pinterest.ktlint.core.Rule.Modifier.Last ||
            rule is com.pinterest.ktlint.core.Rule.Modifier.RestrictToRootLast
    }

    private fun ASTNode.visitTokens(currentNode: (ASTNode) -> Unit) {
        if (this.isNoFakeElement()) {
            currentNode(this)
        }
        getChildren(null).forEach { it.visitTokens(currentNode) }
    }

    private fun ASTNode.isNoFakeElement(): Boolean {
        val parent = this.psi?.parent
        return parent !is JavaDummyHolder && parent !is JavaDummyElement
    }
}
