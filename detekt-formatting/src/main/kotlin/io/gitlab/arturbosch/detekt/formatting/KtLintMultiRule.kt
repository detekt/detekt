package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.formatting.wrappers.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyElement
import org.jetbrains.kotlin.com.intellij.psi.impl.source.JavaDummyHolder
import org.jetbrains.kotlin.psi.KtFile
import java.util.*

/**
 * Runs all KtLint rules.
 */
class KtLintMultiRule(config: Config = Config.empty) : MultiRule() {

    override val rules: List<Rule> = listOf(
        // Wrappers for ktlint-ruleset-standard rules. Enabled by default.
        ChainWrapping(config),
        CommentSpacing(config),
        Filename(config),
        FinalNewline(config),
        ImportOrdering(config),
        Indentation(config),
        MaximumLineLength(config),
        ModifierOrdering(config),
        NoBlankLineBeforeRbrace(config),
        NoConsecutiveBlankLines(config),
        NoEmptyClassBody(config),
        NoLineBreakAfterElse(config),
        NoLineBreakBeforeAssignment(config),
        NoMultipleSpaces(config),
        NoSemicolons(config),
        NoTrailingSpaces(config),
        NoUnitReturn(config),
        NoUnusedImports(config),
        NoWildcardImports(config),
        ParameterListWrapping(config),
        SpacingAroundColon(config),
        SpacingAroundComma(config),
        SpacingAroundCurly(config),
        SpacingAroundDot(config),
        SpacingAroundKeyword(config),
        SpacingAroundOperators(config),
        SpacingAroundParens(config),
        SpacingAroundRangeOperator(config),
        StringTemplate(config),
        Wrapping(config),

        // Wrappers for ktlint-ruleset-experimental rules. Disabled by default.
        AnnotationOnSeparateLine(config),
        AnnotationSpacing(config),
        ArgumentListWrapping(config),
        BlockCommentInitialStarAlignment(config),
        CommentWrapping(config),
        DiscouragedCommentLocation(config),
        EnumEntryNameCase(config),
        FunctionTypeReferenceSpacing(config),
        FunKeywordSpacing(config),
        KdocWrapping(config),
        ModifierListSpacing(config),
        MultiLineIfElse(config),
        NoEmptyFirstLineInMethodBlock(config),
        PackageName(config),
        SpacingAroundAngleBrackets(config),
        SpacingAroundDoubleColon(config),
        SpacingAroundUnaryOperator(config),
        SpacingBetweenDeclarationsWithAnnotations(config),
        SpacingBetweenDeclarationsWithComments(config),
        TrailingComma(config),
        TypeArgumentListSpacing(config),
        UnnecessaryParenthesesBeforeTrailingLambda(config),
    )

    override fun visit(root: KtFile) {
        val sortedRules = getSortedRules()
        sortedRules.forEach { it.visit(root) }
        root.node.visitTokens { node ->
            sortedRules.forEach { it.apply(node) }
        }
    }

    internal fun getSortedRules(): List<FormattingRule> {
        val runFirstOnRoot = mutableListOf<FormattingRule>()
        val other = mutableListOf<FormattingRule>()
        val runLastOnRoot = mutableListOf<FormattingRule>()
        val runLast = mutableListOf<FormattingRule>()
        for (rule in activeRules.filterIsInstance<FormattingRule>()) {
            when {
                rule.runOnRootNodeOnly && rule.runAsLateAsPossible -> runLastOnRoot.add(rule)
                rule.runOnRootNodeOnly -> runFirstOnRoot.add(rule)
                rule.runAsLateAsPossible -> runLast.add(rule)
                else -> other.add(rule)
            }
        }
        return LinkedList<FormattingRule>().apply {
            addAll(runFirstOnRoot)
            addAll(other)
            addAll(runLastOnRoot)
            addAll(runLast)
        }
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
