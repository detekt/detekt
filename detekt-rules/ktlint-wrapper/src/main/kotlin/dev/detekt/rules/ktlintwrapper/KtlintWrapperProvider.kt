package dev.detekt.rules.ktlintwrapper

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider
import dev.detekt.rules.ktlintwrapper.wrappers.AnnotationOnSeparateLine
import dev.detekt.rules.ktlintwrapper.wrappers.AnnotationSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.ArgumentListWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.BackingPropertyNaming
import dev.detekt.rules.ktlintwrapper.wrappers.BinaryExpressionWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.BlankLineBeforeDeclaration
import dev.detekt.rules.ktlintwrapper.wrappers.BlankLineBetweenWhenConditions
import dev.detekt.rules.ktlintwrapper.wrappers.BlockCommentInitialStarAlignment
import dev.detekt.rules.ktlintwrapper.wrappers.ChainMethodContinuation
import dev.detekt.rules.ktlintwrapper.wrappers.ChainWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ClassName
import dev.detekt.rules.ktlintwrapper.wrappers.ClassSignature
import dev.detekt.rules.ktlintwrapper.wrappers.CommentSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.CommentWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ConditionWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ContextReceiverListWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ContextReceiverMapping
import dev.detekt.rules.ktlintwrapper.wrappers.EnumEntryNameCase
import dev.detekt.rules.ktlintwrapper.wrappers.EnumWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ExpressionOperandWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.Filename
import dev.detekt.rules.ktlintwrapper.wrappers.FinalNewline
import dev.detekt.rules.ktlintwrapper.wrappers.FunKeywordSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionExpressionBody
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionLiteral
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionName
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionReturnTypeSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionSignature
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionStartOfBodySpacing
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionTypeModifierSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.FunctionTypeReferenceSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.IfElseBracing
import dev.detekt.rules.ktlintwrapper.wrappers.IfElseWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ImportOrdering
import dev.detekt.rules.ktlintwrapper.wrappers.Indentation
import dev.detekt.rules.ktlintwrapper.wrappers.Kdoc
import dev.detekt.rules.ktlintwrapper.wrappers.KdocWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.MaximumLineLength
import dev.detekt.rules.ktlintwrapper.wrappers.MixedConditionOperators
import dev.detekt.rules.ktlintwrapper.wrappers.ModifierListSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.ModifierOrdering
import dev.detekt.rules.ktlintwrapper.wrappers.MultiLineIfElse
import dev.detekt.rules.ktlintwrapper.wrappers.MultilineExpressionWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.MultilineLoop
import dev.detekt.rules.ktlintwrapper.wrappers.NoBlankLineBeforeRbrace
import dev.detekt.rules.ktlintwrapper.wrappers.NoBlankLineInList
import dev.detekt.rules.ktlintwrapper.wrappers.NoBlankLinesInChainedMethodCalls
import dev.detekt.rules.ktlintwrapper.wrappers.NoConsecutiveBlankLines
import dev.detekt.rules.ktlintwrapper.wrappers.NoConsecutiveComments
import dev.detekt.rules.ktlintwrapper.wrappers.NoEmptyClassBody
import dev.detekt.rules.ktlintwrapper.wrappers.NoEmptyFile
import dev.detekt.rules.ktlintwrapper.wrappers.NoEmptyFirstLineInClassBody
import dev.detekt.rules.ktlintwrapper.wrappers.NoEmptyFirstLineInMethodBlock
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakAfterElse
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakBeforeAssignment
import dev.detekt.rules.ktlintwrapper.wrappers.NoMultipleSpaces
import dev.detekt.rules.ktlintwrapper.wrappers.NoSemicolons
import dev.detekt.rules.ktlintwrapper.wrappers.NoSingleLineBlockComment
import dev.detekt.rules.ktlintwrapper.wrappers.NoTrailingSpaces
import dev.detekt.rules.ktlintwrapper.wrappers.NoUnitReturn
import dev.detekt.rules.ktlintwrapper.wrappers.NoUnusedImports
import dev.detekt.rules.ktlintwrapper.wrappers.NoWildcardImports
import dev.detekt.rules.ktlintwrapper.wrappers.NullableTypeSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.PackageName
import dev.detekt.rules.ktlintwrapper.wrappers.ParameterListSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.ParameterListWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.ParameterWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.PropertyName
import dev.detekt.rules.ktlintwrapper.wrappers.PropertyWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundAngleBrackets
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundColon
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundComma
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundCurly
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundDot
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundDoubleColon
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundKeyword
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundOperators
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundParens
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundRangeOperator
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundSquareBrackets
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingAroundUnaryOperator
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingBetweenDeclarationsWithAnnotations
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingBetweenDeclarationsWithComments
import dev.detekt.rules.ktlintwrapper.wrappers.SpacingBetweenFunctionNameAndOpeningParenthesis
import dev.detekt.rules.ktlintwrapper.wrappers.StatementWrapping
import dev.detekt.rules.ktlintwrapper.wrappers.StringTemplate
import dev.detekt.rules.ktlintwrapper.wrappers.StringTemplateIndent
import dev.detekt.rules.ktlintwrapper.wrappers.ThenSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.TrailingCommaOnCallSite
import dev.detekt.rules.ktlintwrapper.wrappers.TrailingCommaOnDeclarationSite
import dev.detekt.rules.ktlintwrapper.wrappers.TryCatchFinallySpacing
import dev.detekt.rules.ktlintwrapper.wrappers.TypeArgumentComment
import dev.detekt.rules.ktlintwrapper.wrappers.TypeArgumentListSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.TypeParameterComment
import dev.detekt.rules.ktlintwrapper.wrappers.TypeParameterListSpacing
import dev.detekt.rules.ktlintwrapper.wrappers.UnnecessaryParenthesesBeforeTrailingLambda
import dev.detekt.rules.ktlintwrapper.wrappers.ValueArgumentComment
import dev.detekt.rules.ktlintwrapper.wrappers.ValueParameterComment
import dev.detekt.rules.ktlintwrapper.wrappers.WhenEntryBracing
import dev.detekt.rules.ktlintwrapper.wrappers.Wrapping
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId as KtlintRuleSetId

/**
 * This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.
 *
 * **Note: The `ktlint` rule set is not included in the detekt-cli or Gradle plugin.**
 *
 * To enable this rule set, add `detektPlugins "dev.detekt:detekt-rules-ktlint-wrapper:$version"`
 * to your gradle `dependencies` or reference the `detekt-rules-ktlint-wrapper`-jar with the `--plugins` option
 * in the command line interface.
 *
 * Note: Issues reported by this rule set can only be suppressed on file level (`@file:Suppress("detekt.rule")`).
 */
@ActiveByDefault(since = "1.0.0")
class KtlintWrapperProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("ktlint")

    @Suppress("LongMethod")
    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                // Wrappers for standard rules. Enabled by default.
                ::AnnotationOnSeparateLine,
                ::AnnotationSpacing,
                ::ArgumentListWrapping,
                ::BackingPropertyNaming,
                ::BinaryExpressionWrapping,
                ::BlankLineBetweenWhenConditions,
                ::BlockCommentInitialStarAlignment,
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
                ::ImportOrdering,
                ::Indentation,
                ::Kdoc,
                ::KdocWrapping,
                ::MaximumLineLength,
                ::MixedConditionOperators,
                ::ModifierListSpacing,
                ::ModifierOrdering,
                ::MultiLineIfElse,
                ::MultilineLoop,
                ::NoBlankLineBeforeRbrace,
                ::NoBlankLinesInChainedMethodCalls,
                ::NoConsecutiveBlankLines,
                ::NoEmptyClassBody,
                ::NoEmptyFile,
                ::NoEmptyFirstLineInMethodBlock,
                ::NoLineBreakAfterElse,
                ::NoLineBreakBeforeAssignment,
                ::NoMultipleSpaces,
                ::NoSemicolons,
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
                ::SpacingAroundSquareBrackets,
                ::SpacingAroundUnaryOperator,
                ::SpacingBetweenDeclarationsWithAnnotations,
                ::SpacingBetweenDeclarationsWithComments,
                ::SpacingBetweenFunctionNameAndOpeningParenthesis,
                ::StatementWrapping,
                ::StringTemplate,
                ::ThenSpacing,
                ::TrailingCommaOnCallSite,
                ::TrailingCommaOnDeclarationSite,
                ::TypeArgumentComment,
                ::TypeArgumentListSpacing,
                ::TypeParameterComment,
                ::TypeParameterListSpacing,
                ::UnnecessaryParenthesesBeforeTrailingLambda,
                ::ValueArgumentComment,
                ::ValueParameterComment,
                ::Wrapping,
                // Wrappers for rules that are only enabled when using ktlint_official code style. Disabled by default.
                // Check ktlint rules that implement com.pinterest.ktlint.rule.engine.core.api.Rule.OfficialCodeStyle
                ::BlankLineBeforeDeclaration,
                ::ChainMethodContinuation,
                ::IfElseBracing,
                ::IfElseWrapping,
                ::MultilineExpressionWrapping,
                ::NoBlankLineInList,
                ::NoConsecutiveComments,
                ::NoEmptyFirstLineInClassBody,
                ::NoSingleLineBlockComment,
                ::StringTemplateIndent,
                ::TryCatchFinallySpacing,
                ::WhenEntryBracing,
                // Wrappers for experimental rules. Disabled by default.
                // Check ktlint rules that implement com.pinterest.ktlint.rule.engine.core.api.Rule.Experimental
                ::ExpressionOperandWrapping,
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
 * Return a list of [KtlintRule] that respects
 * [Rule.VisitorModifier.RunAsLateAsPossible] and [Rule.VisitorModifier.RunAfterRule].
 * Algorithm is based on [com.pinterest.ktlint.rule.engine.internal.RuleProviderSorter].
 */
internal fun List<(Config) -> KtlintRule>.sorted(): List<(Config) -> KtlintRule> {
    val sortedRules = mutableListOf<(Config) -> KtlintRule>()
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
    compareBy<Pair<(Config) -> KtlintRule, KtlintRule>> { (_, rule) ->
        if (rule.runAsLateAsPossible) 1 else 0
    }.thenBy { (_, rule) ->
        if (rule.wrappingRuleId.ruleSetId == KtlintRuleSetId.STANDARD) 0 else 1
    }.thenBy { (_, rule) -> rule.wrappingRuleId.value }

internal val KtlintRule.wrappingRuleId
    get() = wrapping.ruleId

internal val KtlintRule.visitorModifiers
    get() = wrapping.visitorModifiers

internal val KtlintRule.runAsLateAsPossible
    get() = Rule.VisitorModifier.RunAsLateAsPossible in visitorModifiers

private fun KtlintRule.runAfterRules() = visitorModifiers.filterIsInstance<Rule.VisitorModifier.RunAfterRule>()

private fun KtlintRule.hasNoRunAfterRules() =
    visitorModifiers.filterIsInstance<Rule.VisitorModifier.RunAfterRule>().isEmpty()
