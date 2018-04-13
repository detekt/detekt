package io.gitlab.arturbosch.detekt.formatting

import com.github.shyiko.ktlint.ruleset.standard.ChainWrappingRule
import com.github.shyiko.ktlint.ruleset.standard.FinalNewlineRule
import com.github.shyiko.ktlint.ruleset.standard.ImportOrderingRule
import com.github.shyiko.ktlint.ruleset.standard.IndentationRule
import com.github.shyiko.ktlint.ruleset.standard.MaxLineLengthRule
import com.github.shyiko.ktlint.ruleset.standard.ModifierOrderRule
import com.github.shyiko.ktlint.ruleset.standard.NoBlankLineBeforeRbraceRule
import com.github.shyiko.ktlint.ruleset.standard.NoConsecutiveBlankLinesRule
import com.github.shyiko.ktlint.ruleset.standard.NoEmptyClassBodyRule
import com.github.shyiko.ktlint.ruleset.standard.NoItParamInMultilineLambdaRule
import com.github.shyiko.ktlint.ruleset.standard.NoLineBreakAfterElseRule
import com.github.shyiko.ktlint.ruleset.standard.NoLineBreakBeforeAssignmentRule
import com.github.shyiko.ktlint.ruleset.standard.NoMultipleSpacesRule
import com.github.shyiko.ktlint.ruleset.standard.NoSemicolonsRule
import com.github.shyiko.ktlint.ruleset.standard.NoTrailingSpacesRule
import com.github.shyiko.ktlint.ruleset.standard.NoUnitReturnRule
import com.github.shyiko.ktlint.ruleset.standard.NoUnusedImportsRule
import com.github.shyiko.ktlint.ruleset.standard.NoWildcardImportsRule
import com.github.shyiko.ktlint.ruleset.standard.ParameterListWrappingRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundColonRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundCommaRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundCurlyRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundKeywordRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundOperatorsRule
import com.github.shyiko.ktlint.ruleset.standard.SpacingAroundRangeOperatorRule
import com.github.shyiko.ktlint.ruleset.standard.StringTemplateRule
import io.gitlab.arturbosch.detekt.api.Config

/**
 * This file is a collection of KtLint-Rule wrappers.
 * A wrapper can be controlled through the detekt config api but mirrors
 * the functionality of a specific KtLint rule.
 *
 * @author Artur Bosch
 */

class ChainWrapping(config: Config) : ApplyingRule(config) {

	override val wrapping = ChainWrappingRule()
	override val issue = issueFor("Checks if condition chaining is wrapped right")
}

class FinalNewline(config: Config) : ApplyingRule(config) {

	override val wrapping = FinalNewlineRule()
	override val issue = issueFor("Detects missing final newlines")
}

class ImportOrdering(config: Config) : ApplyingRule(config) {

	override val wrapping = ImportOrderingRule()
	override val issue = issueFor("Detects imports in non default order")
}

class Indentation(config: Config) : ApplyingRule(config) {

	override val wrapping = IndentationRule()
	override val issue = issueFor("Reports mis-indented code")
}

class MaxLineLength(config: Config) : ApplyingRule(config) {

	override val wrapping = MaxLineLengthRule()
	override val issue = issueFor("Reports lines with exceeded length")
}

class ModifierOrder(config: Config) : ApplyingRule(config) {

	override val wrapping = ModifierOrderRule()
	override val issue = issueFor("Detects modifiers in non default order")
}

class NoBlankLineBeforeRbrace(config: Config) : ApplyingRule(config) {

	override val wrapping = NoBlankLineBeforeRbraceRule()
	override val issue = issueFor("Detects blank lines before rbraces")
}

class NoConsecutiveBlankLines(config: Config) : ApplyingRule(config) {

	override val wrapping = NoConsecutiveBlankLinesRule()
	override val issue = issueFor("Reports consecutive blank lines")
}

class NoEmptyClassBody(config: Config) : ApplyingRule(config) {

	override val wrapping = NoEmptyClassBodyRule()
	override val issue = issueFor("Reports empty class bodies")
}

class NoItParamInMultilineLambda(config: Config) : ApplyingRule(config) {

	override val wrapping = NoItParamInMultilineLambdaRule()
	override val issue = issueFor("Reports 'it' variable usages in multiline lambdas")
}

class NoLineBreakAfterElse(config: Config) : ApplyingRule(config) {

	override val wrapping = NoLineBreakAfterElseRule()
	override val issue = issueFor("Reports line breaks after else")
}

class NoLineBreakBeforeAssignment(config: Config) : ApplyingRule(config) {

	override val wrapping = NoLineBreakBeforeAssignmentRule()
	override val issue = issueFor("Reports line breaks after else")
}

class NoMultipleSpaces(config: Config) : ApplyingRule(config) {

	override val wrapping = NoMultipleSpacesRule()
	override val issue = issueFor("Reports multiple space usages")
}

class NoSemicolons(config: Config) : ApplyingRule(config) {

	override val wrapping = NoSemicolonsRule()
	override val issue = issueFor("Detects semicolons")
}

class NoTrailingSpaces(config: Config) : ApplyingRule(config) {

	override val wrapping = NoTrailingSpacesRule()
	override val issue = issueFor("Detects trailing spaces")
}

class NoUnitReturn(config: Config) : ApplyingRule(config) {

	override val wrapping = NoUnitReturnRule()
	override val issue = issueFor("Detects optional 'Unit' return types")
}

class NoUnusedImports(config: Config) : ApplyingRule(config) {

	override val wrapping = NoUnusedImportsRule()
	override val issue = issueFor("Detects unused imports")
}

class NoWildcardImports(config: Config) : ApplyingRule(config) {

	override val wrapping = NoWildcardImportsRule()
	override val issue = issueFor("Detects wildcast import usages")
}

class ParameterListWrapping(config: Config) : ApplyingRule(config) {

	override val wrapping = ParameterListWrappingRule()
	override val issue = issueFor("Detects mis-aligned parater lists")
}

class SpacingAroundColon(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundColonRule()
	override val issue = issueFor("Reports spaces around colons")
}

class SpacingAroundComma(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundCommaRule()
	override val issue = issueFor("Reports spaces around commas")
}

class SpacingAroundCurly(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundCurlyRule()
	override val issue = issueFor("Reports spaces around curly braces")
}

class SpacingAroundKeyword(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundKeywordRule()
	override val issue = issueFor("Reports spaces around keywords")
}

class SpacingAroundOperators(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundOperatorsRule()
	override val issue = issueFor("Reports spaces around operators")
}

class SpacingAroundRangeOperator(config: Config) : ApplyingRule(config) {

	override val wrapping = SpacingAroundRangeOperatorRule()
	override val issue = issueFor("Reports spaces around range operator")
}

class StringTemplate(config: Config) : ApplyingRule(config) {

	override val wrapping = StringTemplateRule()
	override val issue = issueFor("Detects simplifications in template strings")
}


