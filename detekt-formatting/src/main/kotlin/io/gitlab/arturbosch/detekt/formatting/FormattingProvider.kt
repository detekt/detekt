package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.ruleSetConfig
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationOnSeparateLine
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ArgumentListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.BinaryExpressionWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlankLineBeforeDeclaration
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlockCommentInitialStarAlignment
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ClassName
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ContextReceiverMapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.DiscouragedCommentLocation
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.FinalNewline
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunKeywordSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionName
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionReturnTypeSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionSignature
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionStartOfBodySpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionTypeReferenceSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.IfElseBracing
import io.gitlab.arturbosch.detekt.formatting.wrappers.IfElseWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.KdocWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultiLineIfElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultilineExpressionWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLineBeforeRbrace
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLineInList
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoBlankLinesInChainedMethodCalls
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoConsecutiveBlankLines
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoConsecutiveComments
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyClassBody
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyFile
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyFirstLineInClassBody
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoEmptyFirstLineInMethodBlock
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakAfterElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoLineBreakBeforeAssignment
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoMultipleSpaces
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoSemicolons
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoSingleLineBlockComment
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoTrailingSpaces
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoUnitReturn
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoUnusedImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.NoWildcardImports
import io.gitlab.arturbosch.detekt.formatting.wrappers.NullableTypeSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.PackageName
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.PropertyName
import io.gitlab.arturbosch.detekt.formatting.wrappers.PropertyWrapping
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
import io.gitlab.arturbosch.detekt.formatting.wrappers.StringTemplateIndent
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnCallSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnDeclarationSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TryCatchFinallySpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeArgumentListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeParameterListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping

/**
 * This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.
 *
 * **Note: The `formatting` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"`
 * to your gradle `dependencies` or reference the `detekt-formatting`-jar with the `--plugins` option
 * in the command line interface.
 *
 * Note: Issues reported by this rule set can only be suppressed on file level (`@file:Suppress("detekt.rule")`).
 */
@ActiveByDefault(since = "1.0.0")
class FormattingProvider : RuleSetProvider {

    override val ruleSetId: String = "formatting"

    @Suppress("LongMethod")
    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            // Wrappers for ktlint-ruleset-standard rules. Enabled by default.
            AnnotationOnSeparateLine(config),
            AnnotationSpacing(config),
            ArgumentListWrapping(config),
            BlockCommentInitialStarAlignment(config),
            ChainWrapping(config),
            ClassName(config),
            CommentSpacing(config),
            CommentWrapping(config),
            EnumEntryNameCase(config),
            Filename(config),
            FinalNewline(config),
            FunctionName(config),
            FunKeywordSpacing(config),
            FunctionReturnTypeSpacing(config),
            FunctionStartOfBodySpacing(config),
            FunctionTypeReferenceSpacing(config),
            ImportOrdering(config),
            Indentation(config),
            KdocWrapping(config),
            MaximumLineLength(config),
            ModifierListSpacing(config),
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
            NoSingleLineBlockComment(config),
            NoTrailingSpaces(config),
            NoUnitReturn(config),
            NoUnusedImports(config),
            NoWildcardImports(config),
            NullableTypeSpacing(config),
            PackageName(config),
            ParameterListWrapping(config),
            ParameterWrapping(config),
            PropertyName(config),
            PropertyWrapping(config),
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
            SpacingBetweenFunctionNameAndOpeningParenthesis(config),
            StringTemplate(config),
            TrailingCommaOnCallSite(config), // in standard ruleset but not enabled by default
            TrailingCommaOnDeclarationSite(config), // in standard ruleset but not enabled by default
            UnnecessaryParenthesesBeforeTrailingLambda(config),
            Wrapping(config),

            // Wrappers for ktlint-ruleset-experimental rules. Disabled by default.
            BinaryExpressionWrapping(config),
            BlankLineBeforeDeclaration(config),
            ContextReceiverMapping(config),
            DiscouragedCommentLocation(config),
            EnumWrapping(config),
            FunctionSignature(config),
            IfElseBracing(config),
            IfElseWrapping(config),
            MultilineExpressionWrapping(config),
            NoBlankLineInList(config),
            NoConsecutiveComments(config),
            NoEmptyFile(config),
            NoEmptyFirstLineInClassBody(config),
            ParameterListSpacing(config),
            StringTemplateIndent(config),
            TryCatchFinallySpacing(config),
            TypeArgumentListSpacing(config),
            TypeParameterListSpacing(config),
        ).sortedWith(FormattingRuleComparator)
    )

    companion object {
        @Configuration("if android style guides should be preferred")
        val android by ruleSetConfig(false)

        @Configuration("if rules should auto correct style violation")
        val autoCorrect by ruleSetConfig(true)
    }
}
