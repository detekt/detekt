package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.isPartOf
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtReferenceExpression

/**
 * @author Artur Bosch
 */
class UnusedImports(config: Config) : Rule(config) {

	override val issue = Issue(
			javaClass.simpleName,
			Severity.Style,
			"Unused Imports are dead code and should be removed.",
			Debt.FIVE_MINS)

	private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
			"mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign", "divAssign",
			"modAssign", "equals", "compareTo", "iterator", "getValue", "setValue")

	private var imports = mutableListOf<Pair<String, KtImportDirective>>()
	private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")

	override fun visitFile(file: PsiFile?) {
		imports.clear()
		super.visitFile(file)
		imports.forEach { report(CodeSmell(issue, Entity.from(it.second))) }
	}

	override fun visitImportList(importList: KtImportList) {
		imports = importList.imports.filter { it.isValidImport }
				.filter { it.identifier()?.contains("*")?.not() == true }
				.filter { it.identifier() != null }
				.filter { !operatorSet.contains(it.identifier()) }
				.map { it.identifier()!! to it }
				.toMutableList()
		super.visitImportList(importList)
	}

	override fun visitReferenceExpression(expression: KtReferenceExpression) {
		if (expression.isPartOf(KtImportDirective::class)) return

		val reference = expression.text.trim('`')
		imports.find { it.first == reference }?.let {
			imports.remove(it)
		}

		super.visitReferenceExpression(expression)
	}

	override fun visitDeclaration(dcl: KtDeclaration) {
		dcl.docComment?.getDefaultSection()?.getContent()?.let {
			kotlinDocReferencesRegExp.findAll(it, 0)
					.map { it.groupValues[1] }
					.forEach { checkImports(it) }
		}
		super.visitDeclaration(dcl)
	}

	private fun checkImports(it: String) {
		imports.removeIf { pair ->
			val identifier = pair.second.identifier()
			identifier != null && it.startsWith(identifier)
		}
	}

	private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier

}
