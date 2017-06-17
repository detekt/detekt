package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.*

/**
 * @author Artur Bosch
 */
class UnusedImports(config: Config) : Rule("UnusedImports", config) {

	private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
			"mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign", "divAssign",
			"modAssign", "equals", "compareTo", "iterator", "getValue", "setValue")

	private var imports = mutableListOf<Pair<String, KtImportDirective>>()
	private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")

	override fun preVisit(context: Context, root: KtFile) {
		imports.clear()
	}

	override fun postVisit(context: Context, root: KtFile) {
		imports.forEach {
			context.report(CodeSmell(ISSUE, Entity.from(it.second), "Unused import"))
			withAutoCorrect {
				it.second.delete()
			}
		}
	}

	override fun visitImportList(context: Context, importList: KtImportList) {
		imports = importList.imports.filter { it.isValidImport }
				.filter { it.identifier()?.contains("*")?.not() ?: false }
				.filter { it.identifier() != null }
				.filter { !operatorSet.contains(it.identifier()) }
				.map { it.identifier()!! to it }
				.toMutableList()
		super.visitImportList(context, importList)
	}

	override fun visitReferenceExpression(context: Context, expression: KtReferenceExpression) {
		if (expression.isPartOf(KtImportDirective::class)) return

		val reference = expression.text.trim('`')
		imports.find { it.first == reference }?.let {
			imports.remove(it)
		}

		super.visitReferenceExpression(context, expression)
	}

	override fun visitDeclaration(context: Context, dcl: KtDeclaration) {
		dcl.docComment?.getDefaultSection()?.getContent()?.let {
			kotlinDocReferencesRegExp.findAll(it, 0)
					.map { it.groupValues[1] }
					.forEach { imports.removeIf { pair -> pair.second.identifier() == it } }
		}
		super.visitDeclaration(context, dcl)
	}

	private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier

	companion object {
		val ISSUE = Issue("UnusedImports", Issue.Severity.Style)
	}
}
