package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPartOf
import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtReferenceExpression

/**
 * This rule reports unused imports. Unused imports are dead code and should be removed.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */
class UnusedImports(config: Config) : Rule(config) {

	override val issue = Issue(
			javaClass.simpleName,
			Severity.Style,
			"Unused Imports are dead code and should be removed.",
			Debt.FIVE_MINS)

	companion object {
		private val operatorSet = setOf("unaryPlus", "unaryMinus", "not", "inc", "dec", "plus", "minus", "times", "div",
				"mod", "rangeTo", "contains", "get", "set", "invoke", "plusAssign", "minusAssign", "timesAssign", "divAssign",
				"modAssign", "equals", "compareTo", "iterator", "getValue", "setValue")

		private val kotlinDocReferencesRegExp = Regex("\\[([^]]+)](?!\\[)")
		private val kotlinDocSeeReferenceRegExp = Regex("^@see (.+)")
		private val whiteSpaceRegex = Regex("\\s+")
	}

	override fun visit(root: KtFile) {
		with(UnusedImportsVisitor()) {
			root.accept(this)
			unusedImports().forEach {
				report(CodeSmell(issue, Entity.from(it), "The import '${it.importedFqName}' is unused."))
			}
		}
		super.visit(root)
	}

	private class UnusedImportsVisitor : DetektVisitor() {
		private var currentPackage: FqName? = null
		private var imports: List<KtImportDirective>? = null
		private val namedReferences = mutableSetOf<String>()

		fun unusedImports(): List<KtImportDirective> {
			return imports
					?.filter { it.importedFqName?.parent() == currentPackage || it.identifier() !in namedReferences }
					.orEmpty()
		}

		override fun visitPackageDirective(directive: KtPackageDirective) {
			currentPackage = directive.fqName
			super.visitPackageDirective(directive)
		}

		override fun visitImportList(importList: KtImportList) {
			imports = importList.imports.filter { it.isValidImport }
					.filter { it.identifier()?.contains("*")?.not() == true }
					.filter { it.identifier() != null }
					.filter { !operatorSet.contains(it.identifier()) }
			super.visitImportList(importList)
		}

		override fun visitReferenceExpression(expression: KtReferenceExpression) {
			expression
					.takeIf { !it.isPartOf(KtImportDirective::class) && !it.isPartOf(KtPackageDirective::class) }
					?.takeIf { it.children.isEmpty() }
					?.run { namedReferences.add(text.trim('`')) }
			super.visitReferenceExpression(expression)
		}

		override fun visitDeclaration(dcl: KtDeclaration) {
			val kdoc = dcl.docComment?.getDefaultSection()

			kdoc?.children
					?.filter { it is KDocTag }
					?.map { it.text }
					?.forEach { handleKDoc(it) }

			kdoc?.getContent()?.let {
				handleKDoc(it)
			}
			super.visitDeclaration(dcl)
		}

		private fun handleKDoc(content: String) {
			kotlinDocReferencesRegExp.findAll(content, 0)
					.map { it.groupValues[1] }
					.forEach { namedReferences.add(it.split(".")[0]) }
			kotlinDocSeeReferenceRegExp.find(content)?.let {
				val str = it.groupValues[1].split(whiteSpaceRegex)[0]
				namedReferences.add(str.split(".")[0])
			}
		}
	}
}

private fun KtImportDirective.identifier() = this.importPath?.importedName?.identifier
