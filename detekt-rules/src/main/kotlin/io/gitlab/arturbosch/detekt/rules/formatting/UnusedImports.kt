package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.rules.isPartOf
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class UnusedImports(config: Config) : TokenRule("UnusedImports", Severity.Style, config) {

	private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
			"mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign", "divAssign",
			"modAssign", "equals", "compareTo")
	private val ref = mutableSetOf("*")


	override fun procedure(node: ASTNode) {
		if (node.elementType == KtStubElementTypes.FILE) {
			node.visit { node ->
				if (node.elementType == KtStubElementTypes.REFERENCE_EXPRESSION &&
						!node.psi.isPartOf(KtImportDirective::class)) {
					ref.add(node.text.trim('`'))
				}
			}
		} else if (node.elementType == KtStubElementTypes.IMPORT_DIRECTIVE) {
			val importDirective = node.psi as KtImportDirective
			val name = importDirective.importPath?.importedName?.asString()
			if (name != null && !ref.contains(name) && !operatorSet.contains(name)) {
				addFindings(CodeSmell(id, Entity.from(importDirective), "Unused import"))
				withAutoCorrect {
					importDirective.delete()
				}
			}
		}
	}

	private fun ASTNode.visit(cb: (node: ASTNode) -> Unit) {
		cb(this)
		this.getChildren(null).forEach { it.visit(cb) }
	}

}