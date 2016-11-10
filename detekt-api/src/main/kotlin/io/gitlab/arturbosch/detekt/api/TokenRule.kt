package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtFile

/**
 * A token rule overrides the visiting process and prohibits the use of standard visit functions.
 * As every ASTNode is considered as single object, this abstract rule can be used to define
 * rules which operate on spaces, tabs, semicolons etc.
 *
 * Token rules must define a procedure function which specifies what to do with each ASTNode.
 *
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

	/**
	 * Every ASTNode is considered in isolation. Use 'is' operator to search for wished elements.
	 */
	abstract fun procedure(node: ASTNode)

}
