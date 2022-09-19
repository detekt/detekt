package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationOnSeparateLine
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ArgumentListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlockCommentInitialStarAlignment
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.DiscouragedCommentLocation
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.FinalNewline
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunKeywordSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionReturnTypeSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionSignature
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionStartOfBodySpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionTypeReferenceSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.KdocWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultiLineIfElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLineBeforeRbrace
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLinesInChainedMethodCalls
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
import io.gitlab.arturbosch.detekt.formatting.wrappers.NullableTypeSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundAngleBrackets
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundColon
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundComma
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundCurly
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundDot
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundDoubleColon
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundKeyword
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundOperators
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundParens
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundRangeOperator
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundUnaryOperator
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenDeclarationsWithAnnotations
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenDeclarationsWithComments
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenFunctionNameAndOpeningParenthesis
import io.gitlab.arturbosch.detekt.formatting.wrappers.StringTemplate
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnCallSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnDeclarationSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeArgumentListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeParameterListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
import org.jetbrains.kotlin.psi.KtFile
import java.util.LinkedList

/**
 * Runs all KtLint rules.
 */
class KtLintMultiRule(config: Config = Config.empty) :
    @Suppress("DEPRECATION")
    io.gitlab.arturbosch.detekt.api.MultiRule() {

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
        NoBlankLinesInChainedMethodCalls(config),
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
        TrailingCommaOnCallSite(config), // in standard ruleset but not enabled by default
        TrailingCommaOnDeclarationSite(config), // in standard ruleset but not enabled by default
        Wrapping(config),

        // Wrappers for ktlint-ruleset-experimental rules. Disabled by default.
        BlockCommentInitialStarAlignment(config),
        CommentWrapping(config),
        DiscouragedCommentLocation(config),
        FunctionReturnTypeSpacing(config),
        FunKeywordSpacing(config),
        FunctionSignature(config),
        FunctionStartOfBodySpacing(config),
        FunctionTypeReferenceSpacing(config),
        KdocWrapping(config),
        ModifierListSpacing(config),
        NullableTypeSpacing(config),
        ParameterListSpacing(config),
        SpacingBetweenFunctionNameAndOpeningParenthesis(config),
        TypeArgumentListSpacing(config),
        TypeParameterListSpacing(config),
        UnnecessaryParenthesesBeforeTrailingLambda(config),
    )

    override fun visit(root: KtFile) {
        getSortedRules().forEach { rule ->
            rule.visit(root)
        }
    }

    internal fun getSortedRules(): List<FormattingRule> {
        val other = mutableListOf<FormattingRule>()
        val runLast = mutableListOf<FormattingRule>()
        for (rule in activeRules.filterIsInstance<FormattingRule>()) {
            when {
                rule.runAsLateAsPossible -> runLast.add(rule)
                else -> other.add(rule)
            }
        }
        return LinkedList<FormattingRule>().apply {
            addAll(other)
            addAll(runLast)
        }
    }
}
