package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationOnSeparateLine
import io.gitlab.arturbosch.detekt.formatting.wrappers.AnnotationSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ArgumentListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.BackingPropertyNaming
import io.gitlab.arturbosch.detekt.formatting.wrappers.BinaryExpressionWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlankLineBeforeDeclaration
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlankLineBetweenWhenConditions
import io.gitlab.arturbosch.detekt.formatting.wrappers.BlockCommentInitialStarAlignment
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainMethodContinuation
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ClassName
import io.gitlab.arturbosch.detekt.formatting.wrappers.ClassSignature
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.CommentWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ConditionWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ContextReceiverListWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ContextReceiverMapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumEntryNameCase
import io.gitlab.arturbosch.detekt.formatting.wrappers.EnumWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ExpressionOperandWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.Filename
import io.gitlab.arturbosch.detekt.formatting.wrappers.FinalNewline
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunKeywordSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionExpressionBody
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionLiteral
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionName
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionReturnTypeSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionSignature
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionStartOfBodySpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionTypeModifierSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.FunctionTypeReferenceSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.IfElseBracing
import io.gitlab.arturbosch.detekt.formatting.wrappers.IfElseWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.ImportOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.Indentation
import io.gitlab.arturbosch.detekt.formatting.wrappers.Kdoc
import io.gitlab.arturbosch.detekt.formatting.wrappers.KdocWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.formatting.wrappers.MixedConditionOperators
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.ModifierOrdering
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultiLineIfElse
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultilineExpressionWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.MultilineLoop
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
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundSquareBrackets
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingAroundUnaryOperator
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenDeclarationsWithAnnotations
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenDeclarationsWithComments
import io.gitlab.arturbosch.detekt.formatting.wrappers.SpacingBetweenFunctionNameAndOpeningParenthesis
import io.gitlab.arturbosch.detekt.formatting.wrappers.StatementWrapping
import io.gitlab.arturbosch.detekt.formatting.wrappers.StringTemplate
import io.gitlab.arturbosch.detekt.formatting.wrappers.StringTemplateIndent
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnCallSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TrailingCommaOnDeclarationSite
import io.gitlab.arturbosch.detekt.formatting.wrappers.TryCatchFinallySpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeArgumentComment
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeArgumentListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeParameterComment
import io.gitlab.arturbosch.detekt.formatting.wrappers.TypeParameterListSpacing
import io.gitlab.arturbosch.detekt.formatting.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import io.gitlab.arturbosch.detekt.formatting.wrappers.ValueArgumentComment
import io.gitlab.arturbosch.detekt.formatting.wrappers.ValueParameterComment
import io.gitlab.arturbosch.detekt.formatting.wrappers.WhenEntryBracing
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

    override val ruleSetId = RuleSet.Id("formatting")

    @Suppress("LongMethod")
    override fun instance() = RuleSet(
        ruleSetId,
        listOf(
            // Wrappers for standard rules. Enabled by default.
            ::AnnotationOnSeparateLine,
            ::AnnotationSpacing,
            ::ArgumentListWrapping,
            ::BackingPropertyNaming,
            ::BinaryExpressionWrapping,
            ::BlankLineBeforeDeclaration,
            ::BlockCommentInitialStarAlignment,
            ::ChainMethodContinuation,
            ::ChainWrapping,
            ::ClassName,
            ::ClassSignature,
            ::CommentSpacing,
            ::CommentWrapping,
            ::ConditionWrapping,
            ::ContextReceiverMapping,
            ::ContextReceiverListWrapping,
            ::EnumEntryNameCase,
            ::EnumWrapping,
            ::Filename,
            ::FinalNewline,
            ::FunctionName,
            ::FunKeywordSpacing,
            ::FunctionLiteral,
            ::FunctionExpressionBody,
            ::FunctionReturnTypeSpacing,
            ::FunctionSignature,
            ::FunctionStartOfBodySpacing,
            ::FunctionTypeModifierSpacing,
            ::FunctionTypeReferenceSpacing,
            ::IfElseBracing,
            ::IfElseWrapping,
            ::ImportOrdering,
            ::Indentation,
            ::KdocWrapping,
            ::MaximumLineLength,
            ::ModifierListSpacing,
            ::ModifierOrdering,
            ::MultiLineIfElse,
            ::MultilineExpressionWrapping,
            ::MultilineLoop,
            ::NoBlankLineBeforeRbrace,
            ::NoBlankLineInList,
            ::NoBlankLinesInChainedMethodCalls,
            ::NoConsecutiveBlankLines,
            ::NoConsecutiveComments,
            ::NoEmptyClassBody,
            ::NoEmptyFile,
            ::NoEmptyFirstLineInClassBody,
            ::NoEmptyFirstLineInMethodBlock,
            ::NoLineBreakAfterElse,
            ::NoLineBreakBeforeAssignment,
            ::NoMultipleSpaces,
            ::NoSemicolons,
            ::NoSingleLineBlockComment,
            ::NoTrailingSpaces,
            ::NoUnitReturn,
            ::NoUnusedImports,
            ::NoWildcardImports,
            ::NullableTypeSpacing,
            ::PackageName,
            ::ParameterListSpacing,
            ::ParameterListWrapping,
            ::ParameterWrapping,
            ::PropertyName,
            ::PropertyWrapping,
            ::SpacingAroundAngleBrackets,
            ::SpacingAroundColon,
            ::SpacingAroundComma,
            ::SpacingAroundCurly,
            ::SpacingAroundDot,
            ::SpacingAroundDoubleColon,
            ::SpacingAroundKeyword,
            ::SpacingAroundOperators,
            ::SpacingAroundParens,
            ::SpacingAroundRangeOperator,
            ::SpacingAroundUnaryOperator,
            ::SpacingBetweenDeclarationsWithAnnotations,
            ::SpacingBetweenDeclarationsWithComments,
            ::SpacingBetweenFunctionNameAndOpeningParenthesis,
            ::StatementWrapping,
            ::StringTemplate,
            ::StringTemplateIndent,
            ::TrailingCommaOnCallSite, // standard rule but not enabled by default
            ::TrailingCommaOnDeclarationSite, // standard rule but not enabled by default
            ::TryCatchFinallySpacing,
            ::TypeArgumentComment,
            ::TypeArgumentListSpacing,
            ::TypeParameterComment,
            ::TypeParameterListSpacing,
            ::UnnecessaryParenthesesBeforeTrailingLambda,
            ::ValueArgumentComment,
            ::ValueParameterComment,
            ::Wrapping,
            // Wrappers for experimental rules. Disabled by default.
            ::BlankLineBetweenWhenConditions,
            ::ExpressionOperandWrapping,
            ::Kdoc,
            ::MixedConditionOperators,
            ::SpacingAroundSquareBrackets,
            ::WhenEntryBracing,
        ).sorted()
    )

    companion object {
        @Configuration("ktlint code style for formatting rules (ktlint_official, intellij_idea or android_studio)")
        val code_style by ruleSetConfig("intellij_idea")

        @Configuration("if rules should auto correct style violation")
        val autoCorrect by ruleSetConfig(true)
    }
}

/**
 * Return a list of [FormattingRule] that respects
 * [Rule.VisitorModifier.RunAsLateAsPossible] and [Rule.VisitorModifier.RunAfterRule].
 * Algorithm is based on [com.pinterest.ktlint.rule.engine.internal.RuleProviderSorter].
 */
internal fun List<(Config) -> FormattingRule>.sorted(): List<(Config) -> FormattingRule> {
    val sortedRules = mutableListOf<(Config) -> FormattingRule>()
    val sortedRuleIds = mutableSetOf<RuleId>()
    val unprocessedRules = this
        .map { it to it(Config.empty) }
        .sortedWith(defaultRuleOrderComparator())
        .toMutableList()

    // Initially the list only contains the rules without any VisitorModifiers
    unprocessedRules
        .filter { (_, rule) -> !rule.runAsLateAsPossible && rule.hasNoRunAfterRules() }
        .forEach { (provider, rule) ->
            sortedRules.add(provider)
            sortedRuleIds.add(rule.wrappingRuleId)
        }
    unprocessedRules.removeAll { (provider, _) -> provider in sortedRules }

    // Then we add the rules that have a RunAsLateAsPossible modifier
    // and we obey the RunAfterRule modifiers as well.
    while (unprocessedRules.isNotEmpty()) {
        val (provider, rule) =
            checkNotNull(
                unprocessedRules
                    .firstOrNull { (_, rule) ->
                        rule
                            .runAfterRules()
                            .all { it.ruleId in sortedRuleIds }
                    }
            ) {
                "Can not complete sorting of rule providers as next item can not be determined."
            }
        sortedRuleIds.add(rule.wrappingRuleId)
        sortedRules.add(provider)
        unprocessedRules.removeAll { (provider, _) -> provider in sortedRules }
    }

    return sortedRules
}

private fun defaultRuleOrderComparator() =
// The sort order below should guarantee a stable order of the rule between multiple invocations of KtLint given
    // the same set of input parameters. There should be no dependency on data ordering outside this class.
    compareBy<Pair<(Config) -> FormattingRule, FormattingRule>> { (_, rule) ->
        if (rule.runAsLateAsPossible) 1 else 0
    }.thenBy { (_, rule) ->
        if (rule.wrappingRuleId.ruleSetId == RuleSetId.STANDARD) 0 else 1
    }.thenBy { (_, rule) -> rule.wrappingRuleId.value }

internal val FormattingRule.wrappingRuleId
    get() = wrapping.ruleId

internal val FormattingRule.visitorModifiers
    get() = wrapping.visitorModifiers

internal val FormattingRule.runAsLateAsPossible
    get() = Rule.VisitorModifier.RunAsLateAsPossible in visitorModifiers

private fun FormattingRule.runAfterRules() =
    visitorModifiers.filterIsInstance<Rule.VisitorModifier.RunAfterRule>()

private fun FormattingRule.hasNoRunAfterRules() =
    visitorModifiers.filterIsInstance<Rule.VisitorModifier.RunAfterRule>().isEmpty()
