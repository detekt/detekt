package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.ruleSetConfig
import io.gitlab.arturbosch.detekt.formatting.wrappers.*

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

    override fun instance(config: Config) = RuleSet(
        ruleSetId, listOf(
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
            ContextReceiverMapping(config),
            DiscouragedCommentLocation(config),
            EnumWrapping(config),
            FunctionSignature(config),
            IfElseBracing(config),
            IfElseWrapping(config),
            MultilineExpressionWrapping(config),
            NoBlankLineInList(config),
            NoConsecutiveComments(config),
            NoEmptyFirstLineInClassBody(config),
            ParameterListSpacing(config),
            StringTemplateIndent(config),
            TryCatchFinallySpacing(config),
            TypeArgumentListSpacing(config),
            TypeParameterListSpacing(config),
        ).sortedWith(FormattingRuleComparator)
    )

    /**
     * This serves as weak heuristic to order the wrapped rules according to their visitor modifiers.
     * Currently only RunAsLateAsPossible is supported.
     */
    private object FormattingRuleComparator : Comparator<FormattingRule> {
        override fun compare(o1: FormattingRule, o2: FormattingRule): Int {
            if (o1.runAsLateAsPossible == o2.runAsLateAsPossible) {
                return 0
            }
            return if (o1.runAsLateAsPossible) 1 else -1
        }

    }

    companion object {
        @Configuration("if android style guides should be preferred")
        val android by ruleSetConfig(false)
    }
}
