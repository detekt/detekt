package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
class KtLintMultiRule(config: Config = Config.empty) : MultiRule() {

	override val rules: List<ApplyingRule> = listOf(
			ChainWrapping(config),
			FinalNewline(config),
			ImportOrdering(config),
			Indentation(config),
			MaxLineLength(config),
			ModifierOrder(config),
			NoBlankLineBeforeRbrace(config),
			NoConsecutiveBlankLines(config),
			NoEmptyClassBody(config),
			NoItParamInMultilineLambda(config),
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
			SpacingAroundKeyword(config),
			SpacingAroundOperators(config),
			SpacingAroundRangeOperator(config),
			StringTemplate(config)
	).sortedBy {
		it is com.github.shyiko.ktlint.core.Rule.Modifier.Last
				|| it is com.github.shyiko.ktlint.core.Rule.Modifier.RestrictToRootLast
	}.reversed()

	override fun visit(root: KtFile) {
		activeRules.forEach { it.visit(root) }
		root.node.visitTokens { node ->
			activeRules.forEach { rule ->
				(rule as ApplyingRule).runIfActive { this.apply(node) }
			}
		}
	}

	private fun ASTNode.visitTokens(currentNode: (node: ASTNode) -> Unit) {
		currentNode(this)
		getChildren(null).forEach { it.visitTokens(currentNode) }
	}
}
