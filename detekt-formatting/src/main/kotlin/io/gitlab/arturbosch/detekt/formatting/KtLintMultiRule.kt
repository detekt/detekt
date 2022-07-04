package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.ruleset.experimental.ParameterListSpacingRule
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
        AnnotationOnSeparateLine(config),
        AnnotationSpacing(config),
        ArgumentListWrapping(config),
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
        SpacingAroundAngleBrackets(config),
        SpacingAroundColon(config),
        SpacingAroundComma(config),
        SpacingAroundCurly(config),
        SpacingAroundDot(config),
        SpacingAroundDoubleColon(config),
        SpacingAroundKeyword(config),
        SpacingAroundOperators(config),
        SpacingAroundParens(config),
        SpacingAroundRangeOperator(config),
        SpacingAroundUnaryOperator(config),
        SpacingBetweenDeclarationsWithAnnotations(config),
        SpacingBetweenDeclarationsWithComments(config),
        StringTemplate(config),
        TrailingComma(config),
        Wrapping(config),

        // Wrappers for ktlint-ruleset-experimental rules. Disabled by default.
        BlockCommentInitialStarAlignment(config),
        CommentWrapping(config),
        DiscouragedCommentLocation(config),
        FunctionReturnTypeSpacing(config),
        FunctionSignature(config),
        FunctionStartOfBodySpacing(config),
        FunctionTypeReferenceSpacing(config),
        FunKeywordSpacing(config),
        KdocWrapping(config),
        ModifierListSpacing(config),
        NullableTypeSpacing(config),
        ParameterListSpacing(config),
        SpacingBetweenFunctionNameAndOpeningParenthesis(config),
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
