package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
abstract class TokenRule(id: String,
						 severity: Severity = Rule.Severity.Minor,
						 config: Config = Config.empty) : Rule(id, severity, config) {

	override fun visit(root: KtFile) {
		ifRuleActive {
			clearFindings()
			preVisit(root)
			root.node.visitTokens { procedure(it) }
			postVisit(root)
		}
	}

	abstract fun procedure(node: ASTNode)
}
