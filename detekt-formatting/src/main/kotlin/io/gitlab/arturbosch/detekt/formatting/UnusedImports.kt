package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.isPartOf
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtReferenceExpression

/**
 * @author Artur Bosch
 */
class UnusedImports(config: Config) : Rule("UnusedImports", Severity.Style, config) {

	private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
			"mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign", "divAssign",
			"modAssign", "equals", "compareTo")

	private var imports = mutableListOf<Pair<String, KtImportDirective>>()

	override fun visitFile(file: PsiFile?) {
		imports.clear()
		super.visitFile(file)
		imports.forEach {
			addFindings(CodeSmell(id, Entity.from(it.second), "Unused import"))
			withAutoCorrect {
				it.second.delete()
			}
		}
	}

	override fun visitImportList(importList: KtImportList) {
		imports = importList.imports.filter { it.isValidImport }
				.filter { it.importPath?.importedName?.identifier?.contains("*")?.not() ?: false }
				.filter { it.importPath?.importedName?.identifier != null }
				.filter { !operatorSet.contains(it.importPath?.importedName?.identifier) }
				.map { it.importPath?.importedName?.identifier!! to it }
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

}